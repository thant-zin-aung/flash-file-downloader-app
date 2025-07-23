package com.panda.flash_file_downloader.utils;

import javafx.scene.Node;
import javafx.stage.Stage;

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
}
