package com.panda.flash_file_downloader.controllers;
import com.panda.MultiThreadedDownloader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {
    @FXML
    private Label welcomeText;

    private MultiThreadedDownloader multiThreadedDownloader;

    public MainController() {
        multiThreadedDownloader = new MultiThreadedDownloader();
    }


}