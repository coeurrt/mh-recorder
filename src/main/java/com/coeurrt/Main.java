package com.coeurrt;

import com.coeurrt.mhrecorder.detection.QuestStartDetection;
import com.coeurrt.mhrecorder.obs.ObsWebSocketClient;
import com.coeurrt.mhrecorder.ui.MhRecorderApplication;
import javafx.application.Application;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws Exception {
        URI obsUri = new URI("ws://localhost:4455");

        ObsWebSocketClient client = new ObsWebSocketClient(obsUri);

        MhRecorderApplication.setObsClient(client);

        QuestStartDetection questStartDetection = new QuestStartDetection();

        client.setScreenshotCallback(image -> {
            if (questStartDetection.detect(image)) {
                System.out.println("QUEST STARTED");
            }
        });

        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(
                client::getScreenshot,
                2000,
                500,
                TimeUnit.MILLISECONDS
        );

        Application.launch(MhRecorderApplication.class, args);
    }
}