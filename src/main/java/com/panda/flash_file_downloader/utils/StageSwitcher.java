package com.panda.flash_file_downloader.utils;

import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class StageSwitcher {
    public static enum Stages{ MAIN_STAGE, DOWNLOAD_STAGE };
    private static Stage currentStage;

    private static final Map<Stages, Stage> stageMap = new HashMap<>();

    public static void switchStage(Stages switchStage) {
        currentStage = stageMap.get(switchStage);
    }

    public static Stage getCurrentStage() {
        return currentStage;
    }

    public static void addNewStage(Stages stageName, Stage addStage) {
        stageMap.put(stageName, addStage);
    }
}
