package com.panda.flash_file_downloader.controllers;

import com.panda.flash_file_downloader.utils.MultiThreadedDownloader;
import com.panda.flash_file_downloader.utils.StageSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class DownloadController {

    @FXML
    private Label filenameLabel, percentLabel, etaLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button pauseButton, cancelButton;

    private String fileUrl;
    private String savePath;

    private final MultiThreadedDownloader multiThreadedDownloader;
    private Thread fileDownloadThread;

    public DownloadController() {
        multiThreadedDownloader = new MultiThreadedDownloader();
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public void startDownload() {
        fileDownloadThread = new Thread(() -> {
            try {
                multiThreadedDownloader.downloadFile(fileUrl, savePath);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        fileDownloadThread.start();
    }

    @FXML
    public void clickOnCancelBtn() {
        StageSwitcher.getCurrentStage().close();
        StageSwitcher.switchStage(StageSwitcher.Stages.MAIN_STAGE);
        StageSwitcher.getCurrentStage().show();
        multiThreadedDownloader.stopDownload();
    }
}
