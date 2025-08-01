module com.panda.flash_file_downloader {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires jdk.httpserver;
    requires com.google.gson;
    requires org.json;


    opens com.panda.flash_file_downloader to javafx.fxml;
    exports com.panda.flash_file_downloader;
    exports com.panda.flash_file_downloader.controllers;
    opens com.panda.flash_file_downloader.controllers to javafx.fxml;
    opens com.panda.flash_file_downloader.utils.youtube to com.fasterxml.jackson.databind;
}