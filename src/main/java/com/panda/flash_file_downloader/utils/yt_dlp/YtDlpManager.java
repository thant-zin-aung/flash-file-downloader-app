package com.panda.flash_file_downloader.utils.yt_dlp;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

public class YtDlpManager {
    private static final String BINARY_NAME = "yt-dlp.exe";
    private static final String RESOURCE_PATH = "/com/panda/flash_file_downloader/bin/" + BINARY_NAME;
    private static final String LOCAL_DIR = System.getProperty("user.home") + "/.flash_file_downloader/bin";
    private static final File TARGET_FILE = new File(LOCAL_DIR, BINARY_NAME);

    private static File getOrExtractYtDlp() throws IOException {
        if (TARGET_FILE.exists()) {
            return TARGET_FILE; // Reusing existing yt-dlp
        }
        TARGET_FILE.getParentFile().mkdirs();
        try (InputStream in = YtDlpManager.class.getResourceAsStream(RESOURCE_PATH)) {
            if (in == null) throw new FileNotFoundException("yt-dlp.exe not found in resources!");
            Files.copy(in, TARGET_FILE.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        TARGET_FILE.setExecutable(true);
        return TARGET_FILE;
    }

    public static String getYtDlpPath() throws IOException {
        return getOrExtractYtDlp().getAbsolutePath();
    }

    public static void updateYtDlp() throws IOException {
        File ytDlp = getOrExtractYtDlp();
        ProcessBuilder pb = new ProcessBuilder(ytDlp.getAbsolutePath(), "-U");
        pb.inheritIO();
        pb.start();
    }

    public static String getDirectVideoUrl(String videoUrl) throws IOException {
        List<String> command = Arrays.asList(
                getYtDlpPath(),
                "-f", "best",
                "-g",
                videoUrl
        );
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        String directUrl = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    directUrl = line; // save last non-empty line
                }
            }
        }
        return directUrl;
    }
}
