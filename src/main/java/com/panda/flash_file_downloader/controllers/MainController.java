package com.panda.flash_file_downloader.controllers;
import com.panda.flash_file_downloader.utils.StageSwitcher;
import com.panda.flash_file_downloader.utils.UIUtility;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;

public class MainController {
    @FXML
    private TextField fileUrlBox,savePathBox;

    @FXML
    public void clickOnChooseFolderBtn() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Output Folder");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File selectedDirectory = directoryChooser.showDialog(savePathBox.getScene().getWindow());
        if (selectedDirectory != null) {
            savePathBox.setText(selectedDirectory.getAbsolutePath());
            System.out.println("Selected folder: " + selectedDirectory.getAbsolutePath());
        } else {
            System.out.println("No folder selected.");
        }
    }

    @FXML
    public void clickOnDownloadBtn() throws IOException {
        String fileUrl = fileUrlBox.getText();
        String savePath = savePathBox.getText();
        if(!fileUrl.isEmpty() && !savePath.isEmpty()) {
            System.out.println("Start downloading...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/panda/flash_file_downloader/views/download-view.fxml"));
            Parent root = loader.load();

            DownloadController controller = loader.getController();
            controller.setFileUrl(fileUrl);
            controller.setSavePath(savePath);

            Stage mainStage = (Stage) fileUrlBox.getScene().getWindow();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            mainStage.hide();
            stage.show();
            UIUtility.makeStageDraggable(stage, root);
            StageSwitcher.addNewStage(StageSwitcher.Stages.DOWNLOAD_STAGE, stage);
            StageSwitcher.switchStage(StageSwitcher.Stages.DOWNLOAD_STAGE);
            controller.startDownload();
        }
    }


}