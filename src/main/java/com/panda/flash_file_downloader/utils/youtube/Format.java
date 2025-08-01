package com.panda.flash_file_downloader.utils.youtube;

public class Format {
    String formatId;
    String extension;
    String resolution;
    String filesize;
    String type;

    public Format() {} // required for jackson

    public void setFormatId(String formatId) {
        this.formatId = formatId;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormatId() {
        return formatId;
    }

    public String getResolution() {
        return resolution;
    }

    public String getFilesize() {
        return filesize;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Format{" +
                "formatId='" + formatId + '\'' +
                ", extension='" + extension + '\'' +
                ", resolution='" + resolution + '\'' +
                ", filesize='" + filesize + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}