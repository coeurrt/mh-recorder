package com.coeurrt;

import com.coeurrt.mhrecorder.AppConfig;
import com.coeurrt.mhrecorder.ConfigLoader;
import com.coeurrt.mhrecorder.detection.QuestDetectionManager;
import com.coeurrt.mhrecorder.detection.QuestEndDetection;
import com.coeurrt.mhrecorder.detection.QuestStartDetection;
import com.coeurrt.mhrecorder.obs.ObsConnectionManager;
import com.coeurrt.mhrecorder.ui.MhRecorderApplication;
import javafx.application.Application;

import java.net.URI;
import java.nio.file.Path;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws Exception {
        Map<String, String> config =
                ConfigLoader.load(Path.of("config.txt"));

        AppConfig.OBS_PASSWORD = config.get("OBS_PASSWORD");
        AppConfig.LOG_PATH = config.get("LOG_PATH");

        URI obsUri = new URI("ws://localhost:4455");

        ObsConnectionManager obsConnectionManager = new ObsConnectionManager(obsUri);

        MhRecorderApplication.setObsConnectionManager(obsConnectionManager);

        obsConnectionManager.connect();

        QuestDetectionManager questDetectionManager = new QuestDetectionManager(obsConnectionManager);

        questDetectionManager.startDetection();

        Application.launch(MhRecorderApplication.class, args);
    }
}