package com.panda.flash_file_downloader.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiThreadedDownloader {

    private static final int THREAD_COUNT = 4; // Adjust as needed
    private static final int BUFFER_SIZE = 8192;
    private static boolean isDownloadStop = false;
    public static ObservableValue<String> fileNameObserver = new ObservableValue<>();
    public static ObservableValue<String> fileSizeObserver = new ObservableValue<>();
    public static ObservableValue<Double> progressPercentObserver = new ObservableValue<>();
    public static ObservableValue<String> connectionSpeedObserver = new ObservableValue<>();
    public static ObservableValue<String> etaObserver = new ObservableValue<>();

    public void stopDownload() {
        isDownloadStop = true;
    }
    public void downloadFile(String fileURL, String outputDir) throws Exception {
        isDownloadStop = false;
        URL url = new URL(fileURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("HEAD");

        long contentLength = conn.getContentLengthLong();
        if (contentLength <= 0) {
            throw new IOException("Could not get content length from server.");
        }
        String fileName = detectFileName(conn, fileURL);
        conn.disconnect();
        String fileSize = (contentLength / 1024 / 1024) + " MB";
        System.out.println("Downloading: " + fileName);
        System.out.println("File size: " + fileSize);

        fileNameObserver.setValue(fileName);
        fileSizeObserver.setValue(fileSize);

        File outputFile = new File(outputDir, fileName);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        long partSize = contentLength / THREAD_COUNT;
        long[] downloadedPerThread = new long[THREAD_COUNT];
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadIndex = i;
            long startByte = partSize * i;
            long endByte = (i == THREAD_COUNT - 1) ? contentLength - 1 : startByte + partSize - 1;

            File partFile = new File(outputDir, fileName + ".part" + threadIndex);
            long existingSize = partFile.exists() ? partFile.length() : 0;

            long totalPartSize = endByte - startByte + 1;
            downloadedPerThread[i] = Math.min(existingSize, totalPartSize);

            long finalStart = startByte + downloadedPerThread[i];

            if (downloadedPerThread[i] >= totalPartSize) {
                // Part fully downloaded, skip and count down latch
                latch.countDown();
                System.out.println("Part " + threadIndex + " already downloaded.");
                continue;
            }

            executor.submit(() -> {
                try {
                    downloadPart(fileURL, partFile, finalStart, endByte, downloadedPerThread, threadIndex);
                } catch (IOException e) {
                    System.err.println("Thread " + threadIndex + " error: " + e.getMessage());
                }
                latch.countDown();
            });
        }

        // Progress Monitor Thread
        new Thread(() -> {
            long previousDownloaded = 0;
            long previousTime = System.currentTimeMillis();

            while (latch.getCount() > 0) {
                if(isDownloadStop) return;
                long totalDownloaded = 0;
                for (long l : downloadedPerThread) totalDownloaded += l;

                long now = System.currentTimeMillis();
                long timeDiff = now - previousTime;
                long bytesDiff = totalDownloaded - previousDownloaded;

                if (timeDiff >= 1000) {
                    double speedBps = bytesDiff / (timeDiff / 1000.0);
                    double speedMBps = speedBps / (1024.0 * 1024.0);
                    double percent = (totalDownloaded * 100.0) / contentLength;

                    long remainingBytes = contentLength - totalDownloaded;
                    long etaSeconds = (long) (speedBps > 0 ? remainingBytes / speedBps : -1);
                    String etaStr = etaSeconds >= 0 ? formatETA(etaSeconds) : "Calculating...";

                    progressPercentObserver.setValue(percent);
                    connectionSpeedObserver.setValue("%.2f MB/s".formatted(speedMBps));
                    etaObserver.setValue(etaStr);

                    System.out.printf("\rDownloaded: %.2f%% (%.2f MB/s) | ETA: %s", percent, speedMBps, etaStr);

                    previousDownloaded = totalDownloaded;
                    previousTime = now;
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {}
            }
        }).start();

        latch.await();
        executor.shutdown();

        if(!isDownloadStop) {
            System.out.println("\nMerging parts...");
            mergeParts(outputDir, fileName, outputFile);
            System.out.println("Download complete: " + outputFile.getAbsolutePath());
        }
    }

    private void downloadPart(String fileURL, File partFile, long start, long end, long[] downloaded, int index) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(fileURL).openConnection();
        conn.setRequestProperty("Range", "bytes=" + start + "-" + end);
        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_PARTIAL && responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Server does not support partial content. Response code: " + responseCode);
        }

        try (InputStream in = conn.getInputStream();
             RandomAccessFile raf = new RandomAccessFile(partFile, "rw")) {
            raf.seek(partFile.length()); // resume where left off

            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = in.read(buffer)) != -1) {
                if(isDownloadStop) return;
                raf.write(buffer, 0, read);
                downloaded[index] += read;
            }
        } finally {
            conn.disconnect();
        }
    }

    private void mergeParts(String outputDir, String fileName, File outputFile) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            for (int i = 0; i < THREAD_COUNT; i++) {
                File part = new File(outputDir, fileName + ".part" + i);
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(part))) {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int read;
                    while ((read = bis.read(buffer)) != -1) {
                        bos.write(buffer, 0, read);
                    }
                }
                if (!part.delete()) {
                    System.err.println("Failed to delete part file: " + part.getAbsolutePath());
                }
            }
        }
    }

    private String detectFileName(HttpURLConnection conn, String fileURL) {
        String disposition = conn.getHeaderField("Content-Disposition");
        if (disposition != null) {
            Matcher matcher = Pattern.compile("filename=\"?([^\";]+)\"?").matcher(disposition);
            if (matcher.find()) return matcher.group(1);
        }
        return fileURL.substring(fileURL.lastIndexOf('/') + 1);
    }

    private static String formatETA(long seconds) {
        long mins = seconds / 60;
        long secs = seconds % 60;
        return mins > 0 ? String.format("%dm %ds", mins, secs) : String.format("%ds", secs);
    }
}
