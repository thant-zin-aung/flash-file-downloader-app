package com.panda.flash_file_downloader.controllers;
import com.panda.flash_file_downloader.utils.network.RequestMaker;
import com.panda.flash_file_downloader.utils.youtube.Format;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class MainController {
    @FXML
    private TextField fileUrlBox;
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
    public void clickOnDownloadBtn() throws Exception {
        String fileUrl = fileUrlBox.getText();
        if(!fileUrl.isEmpty()) {
            System.out.println("Start downloading...");
            if(youtubeCheckbox.isSelected()) {
                String selectedId = getSelectedResolutionId();
                if( selectedId != null) {
                    RequestMaker.makeYoutubeDownloadRequest(fileUrl, selectedId);
                }
            } else {
                RequestMaker.makeGeneralDownloadRequest(fileUrl);
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
            System.out.println(fileUrlBox.getText());
            List<Format> formats = new java.util.ArrayList<>(RequestMaker.makeYoutubeFormatRequest(fileUrlBox.getText()));
            Collections.reverse(formats);
            resolutionBox.getItems().addAll(formats.stream()
                    .filter(format -> !format.getFilesize().equalsIgnoreCase("unknown"))
                    .map(format -> format.getResolution()+" - "+format.getFilesize()+" - "+format.getType()+" - "+format.getFormatId()).toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getSelectedResolutionId() {
        if(resolutionBox.getItems().size() > 0 ) return ""+resolutionBox.getSelectionModel().getSelectedItem().split("-")[3].trim();
        return null;
    }


}