package com.panda.flash_file_downloader.utils.network;

import com.panda.flash_file_downloader.controllers.DownloadController;
import com.panda.flash_file_downloader.utils.StageSwitcher;
import com.panda.flash_file_downloader.utils.UIUtility;
import com.panda.flash_file_downloader.utils.yt_dlp.Youtube.YoutubeUtility;
import com.panda.flash_file_downloader.utils.yt_dlp.YtDlpManager;
import com.sun.net.httpserver.HttpExchange;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class DownloadRequestHandler {

    public static String processRequest(HttpExchange exchange) {
        if (!"GET".equals(exchange.getRequestMethod())) {
            return "Invalid request method";
        }

        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            return "Missing query parameters";
        }

        Map<String, String> params = ObjectUtil.queryToMap(query);
        String videoUrl = params.get("url");
        String formatId = params.get("formatId");

        if (videoUrl == null || videoUrl.isEmpty()) {
            return "Missing URL parameter";
        }

        if (!videoUrl.startsWith("https://www.youtube.com")) {
            return "Invalid YouTube URL";
        }

        System.out.println("Download request received for: " + videoUrl + " with formatId: " + formatId);

        new Thread(() -> {
            Platform.runLater(() -> {
                try {
                    DownloadController downloadController = UIUtility.getDownloadController();
                    StageSwitcher.switchStage(StageSwitcher.Stages.DOWNLOAD_STAGE);
                    Stage downloadStage = StageSwitcher.getCurrentStage();
                    downloadStage.show();
                    downloadController.setFileUrl(videoUrl);
                    downloadController.setFormatId(formatId);  // use dynamic formatId here
                    downloadController.setSavePath("D:\\project_test_files");
                    downloadController.startDownload(true);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }).start();

        return "Started download for " + videoUrl + " with formatId " + formatId;
    }

}
