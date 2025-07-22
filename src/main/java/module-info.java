module com.panda.flash_file_downloader {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.panda.flash_file_downloader to javafx.fxml;
    exports com.panda.flash_file_downloader;
    exports com.panda.flash_file_downloader.controllers;
    opens com.panda.flash_file_downloader.controllers to javafx.fxml;
}