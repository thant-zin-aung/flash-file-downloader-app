package com.panda.flash_file_downloader;

import com.panda.flash_file_downloader.utils.StageSwitcher;
import com.panda.flash_file_downloader.utils.yt_dlp.Youtube.YoutubeUtility;
import com.panda.flash_file_downloader.utils.yt_dlp.YtDlpFormatFetcherJson;
import com.panda.flash_file_downloader.utils.yt_dlp.YtDlpManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("views/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Flash File Downloader");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        StageSwitcher.addNewStage(StageSwitcher.Stages.MAIN_STAGE, stage);
    }

    public static void main(String[] args) {
        launch();
//        testYtDlp();
    }

    private static void testYtDlp() {
//        String url = "https://www.youtube.com/watch?v=bCOIgI1Vr50&list=RDMM&index=8";
        String url = " https://www.youtube.com/watch?v=4fndeDfaWCg";
        try {
//            System.out.println("URL: "+YtDlpManager.getDirectVideoUrl(url));
//            System.out.println("📦 Best Formats (Video + Audio):");
//            YtDlpFormatFetcherJson.getFormats(url).values().forEach(System.out::println);
//            YoutubeUtility.downloadVideo(url, "137");
            YoutubeUtility.downloadAndMerge("137", url, "D:\\project_test_files");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}