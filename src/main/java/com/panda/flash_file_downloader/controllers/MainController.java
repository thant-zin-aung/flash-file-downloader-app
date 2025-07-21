package com.panda.flash_file_downloader.controllers;
import com.panda.MultiThreadedDownloader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class MainController {
    @FXML
    private TextField fileUrlBox,savePathBox;

    private MultiThreadedDownloader multiThreadedDownloader;

    public MainController() {
        multiThreadedDownloader = new MultiThreadedDownloader();
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
    public void clickOnDownloadBtn() {
        String fileUrl = fileUrlBox.getText();
        String savePath = savePathBox.getText();
        if(!fileUrl.isEmpty() && !savePath.isEmpty()) {
            System.out.println("Start downloading...");
        }
    }


}