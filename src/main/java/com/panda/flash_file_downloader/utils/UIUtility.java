package com.panda.flash_file_downloader.utils;

import com.panda.flash_file_downloader.MainApplication;
import com.panda.flash_file_downloader.controllers.DownloadController;
import com.panda.flash_file_downloader.controllers.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class UIUtility {
    public static void makeStageDraggable(Stage stage, Node dragNode) {
        final double[] xOffset = new double[1];
        final double[] yOffset = new double[1];

        dragNode.setOnMousePressed(event -> {
            xOffset[0] = event.getSceneX();
            yOffset[0] = event.getSceneY();
        });

        dragNode.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset[0]);
            stage.setY(event.getScreenY() - yOffset[0]);
        });
    }

    public static DownloadController getDownloadController() throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/com/panda/flash_file_downloader/views/download-view.fxml"));
        Parent root = loader.load();
        DownloadController controller = loader.getController();
        Stage downloadStage = new Stage();
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        downloadStage.setAlwaysOnTop(true);
        downloadStage.setScene(scene);
        downloadStage.initStyle(StageStyle.TRANSPARENT);
        UIUtility.makeStageDraggable(downloadStage, root);
        StageSwitcher.addNewStage(StageSwitcher.Stages.DOWNLOAD_STAGE, downloadStage);
        return controller;
    }
}
