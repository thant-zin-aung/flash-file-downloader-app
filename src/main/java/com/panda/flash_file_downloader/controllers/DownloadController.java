package com.panda.flash_file_downloader.controllers;

import com.panda.flash_file_downloader.utils.MultiThreadedDownloader;
import com.panda.flash_file_downloader.utils.StageSwitcher;
import com.panda.flash_file_downloader.utils.yt_dlp.Youtube.YoutubeUtility;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class DownloadController {

    @FXML
    private Label filenameLabel, percentLabel, connectionSpeedLabel, etaLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button pauseButton, cancelButton;

    private String fileUrl;
    private String savePath;
    private String formatId;

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

    public String getFormatId() {
        return formatId;
    }

    public void setFormatId(String formatId) {
        this.formatId = formatId;
    }

    public void startDownload(boolean isYoutube) {
        fileDownloadThread = new Thread(() -> {
            try {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        if(isYoutube) {
                            YoutubeUtility.progressPercentObserver.setListener((oldValue, newValue) -> Platform.runLater(()-> {
                                percentLabel.setText((int)Math.round(newValue)+"%");
                                updateProgress(newValue, 100);
                            }));
                        } else {
                            MultiThreadedDownloader.progressPercentObserver.setListener((oldValue, newValue) -> Platform.runLater(()-> {
                                percentLabel.setText((int)Math.round(newValue)+"%");
                                updateProgress(newValue, 100);
                            }));
                        }
                        return null;
                    }
                };
                progressBar.progressProperty().bind(task.progressProperty());
                new Thread(task).start();
                if(isYoutube) {
                    YoutubeUtility.fileNameObserver.setListener((oldValue, newValue) -> Platform.runLater(()->filenameLabel.setText(newValue)));
//                MultiThreadedDownloader.fileSizeObserver.setListener((oldValue, newValue) -> filenameLabel.setText(newValue));
                    YoutubeUtility.connectionSpeedObserver.setListener(((oldValue, newValue) -> Platform.runLater(()->connectionSpeedLabel.setText("("+newValue+")"))));
                    YoutubeUtility.etaObserver.setListener((oldValue, newValue) -> Platform.runLater(()->etaLabel.setText("| ETA: "+newValue)));
                    YoutubeUtility.downloadAndMerge(formatId, fileUrl, savePath);
                } else {
                    MultiThreadedDownloader.fileNameObserver.setListener((oldValue, newValue) -> Platform.runLater(()->filenameLabel.setText(newValue)));
//                MultiThreadedDownloader.fileSizeObserver.setListener((oldValue, newValue) -> filenameLabel.setText(newValue));
                    MultiThreadedDownloader.connectionSpeedObserver.setListener(((oldValue, newValue) -> Platform.runLater(()->connectionSpeedLabel.setText("("+newValue+")"))));
                    MultiThreadedDownloader.etaObserver.setListener((oldValue, newValue) -> Platform.runLater(()->etaLabel.setText("| ETA: "+newValue)));
                    multiThreadedDownloader.downloadFile(fileUrl, savePath);
                }

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
