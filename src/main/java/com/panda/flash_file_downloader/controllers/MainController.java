package com.panda.flash_file_downloader.controllers;
import com.panda.flash_file_downloader.utils.StageSwitcher;
import com.panda.flash_file_downloader.utils.UIUtility;
import com.panda.flash_file_downloader.utils.yt_dlp.YtDlpFormatFetcherJson;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainController {
    @FXML
    private TextField fileUrlBox,savePathBox;
    @FXML
    private CheckBox youtubeCheckbox;
    @FXML
    private ComboBox<String> resolutionBox;

    public void initialize() {
        fileUrlBox.textProperty().addListener((observable, oldValue, newValue) -> {
            if(youtubeCheckbox.isSelected() && fileUrlBox.getText().toLowerCase().contains("https://www.youtube.com")) {
                convertLinkToYoutubeFormat();
                syncResolutions();
            }
        });
    }

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
        String savePath = savePathBox.getText().isEmpty() ? Paths.get(System.getProperty("user.home"), "Downloads").toString() : savePathBox.getText();
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
            if(youtubeCheckbox.isSelected()) {
                String selectedId = getSelectedResolutionId();
                if( selectedId != null) {
                    controller.setFormatId(selectedId);
                    controller.startDownload(true);
                }
            } else {
                controller.startDownload(false);
            }
        }
    }

    @FXML
    public void onChangeUrlBox() {
        if(youtubeCheckbox.isSelected()) convertLinkToYoutubeFormat();
    }
    @FXML
    public void onChangeYoutubeCheckbox() {
        if(youtubeCheckbox.isSelected() && fileUrlBox.getText().toLowerCase().contains("https://www.youtube.com")) {
            convertLinkToYoutubeFormat();
            syncResolutions();
        }
        resolutionBox.setVisible(youtubeCheckbox.isSelected());
    }

    private void convertLinkToYoutubeFormat() {
        fileUrlBox.setText(fileUrlBox.getText().split("&")[0]);
    }

    private void syncResolutions() {
        resolutionBox.setVisible(true);
        resolutionBox.getItems().clear();
        try {
            List<YtDlpFormatFetcherJson.Format> formats = new java.util.ArrayList<>(YtDlpFormatFetcherJson.getFormats(fileUrlBox.getText()).values().stream().toList());
            Collections.reverse(formats);
            resolutionBox.getItems().addAll(formats.stream()
                    .filter(format -> !format.getFilesize().equalsIgnoreCase("unknown"))
                    .map(format -> format.getResolution()+" - "+format.getFilesize()+" - "+format.getType()+" - "+format.getFormatId()).toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getSelectedResolutionId() {
        if(resolutionBox.getItems().size() > 0 ) return ""+resolutionBox.getSelectionModel().getSelectedItem().split("-")[3].trim();
        return null;
    }


}