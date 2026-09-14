package com.coeurrt;

import com.coeurrt.mhrecorder.detection.QuestStartDetection;
import com.coeurrt.mhrecorder.obs.ObsConnectionManager;
import com.coeurrt.mhrecorder.ui.MhRecorderApplication;
import javafx.application.Application;

import java.net.URI;

public class Main {

    public static void main(String[] args) throws Exception {
        URI obsUri = new URI("ws://localhost:4455");

        ObsConnectionManager obsConnectionManager = new ObsConnectionManager(obsUri);

        MhRecorderApplication.setObsConnectionManager(obsConnectionManager);

        obsConnectionManager.connect();

        QuestStartDetection questStartDetection = new QuestStartDetection();

        obsConnectionManager.setScreenshotCallback(image -> {
            if (questStartDetection.detect(image)) {
                System.out.println("QUEST STARTED");
            }
        });

        Application.launch(MhRecorderApplication.class, args);
    }
}