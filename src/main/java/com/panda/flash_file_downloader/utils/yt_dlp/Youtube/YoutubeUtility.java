package com.panda.flash_file_downloader.utils.yt_dlp.Youtube;

import com.panda.flash_file_downloader.utils.ObservableValue;
import com.panda.flash_file_downloader.utils.yt_dlp.YtDlpManager;

import java.io.*;
import java.util.regex.*;

public class YoutubeUtility {

    public static final ObservableValue<String> fileNameObserver = new ObservableValue<>();
    public static final ObservableValue<String> fileSizeObserver = new ObservableValue<>();
    public static final ObservableValue<Double> progressPercentObserver = new ObservableValue<>();
    public static final ObservableValue<String> connectionSpeedObserver = new ObservableValue<>();
    public static final ObservableValue<String> etaObserver = new ObservableValue<>();

    public record DownloadResult(String filename, int exitCode) {}

    public static DownloadResult downloadAndMerge(String videoFormatId, String youtubeUrl, String outputDir) throws Exception {
        String outputTemplate = outputDir + "/%(title)s.%(ext)s";

        ProcessBuilder pb = new ProcessBuilder(
                YtDlpManager.getYtDlpPath(),
                "-f", videoFormatId + "+bestaudio",
                "--merge-output-format", "mp4",
                "--output", outputTemplate,
                youtubeUrl
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        String finalMergedFilename = null;
        Pattern progressPattern = Pattern.compile("\\[download\\]\\s+(\\d{1,3}(?:\\.\\d+)?)% of (.+?) at (.+?) ETA (.+)");
        Pattern mergerPattern = Pattern.compile("\\[Merger\\] Merging formats into \"(.+?)\"");
        Pattern destinationPattern = Pattern.compile("\\[download\\] Destination: (.+)");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);

                // Parse download destination
                Matcher destinationMatcher = destinationPattern.matcher(line);
                if (destinationMatcher.find()) {
                    String rawPath = destinationMatcher.group(1).trim();
                    String baseName = new File(rawPath).getName();
                    baseName = baseName.replaceAll("\\.f\\d+\\.(mp4|webm|m4a)$", ".mp4");
                    String cleanName = sanitizeFileName(baseName);
                    fileNameObserver.setValue(cleanName);
                }

                // Parse merging result
                Matcher mergerMatcher = mergerPattern.matcher(line);
                if (mergerMatcher.find()) {
                    String mergedPath = mergerMatcher.group(1).trim();
                    finalMergedFilename = sanitizeFileName(new File(mergedPath).getName());
                    fileNameObserver.setValue(finalMergedFilename);
                }

                handleProgressLine(line, progressPattern);
            }
        }

        int exitCode = process.waitFor();
        System.out.println("Download finished with exit code: " + exitCode);

        return new DownloadResult(finalMergedFilename, exitCode);
    }

    private static void handleProgressLine(String line, Pattern pattern) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            try {
                double percent = Double.parseDouble(matcher.group(1));
                progressPercentObserver.setValue(percent);
                fileSizeObserver.setValue(matcher.group(2));
                connectionSpeedObserver.setValue(matcher.group(3));
                etaObserver.setValue(matcher.group(4));
            } catch (NumberFormatException ignored) {}
        }
    }

    private static String sanitizeFileName(String name) {
        name = name.replaceAll("[\\\\/:*?\"<>|]", "_");
        name = name.replaceAll("\\s{2,}", " ").trim();
        name = name.replaceAll("^[\\s\\-_.]+", "");
        return name;
    }
}
