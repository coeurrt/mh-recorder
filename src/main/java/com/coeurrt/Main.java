package com.coeurrt;

import com.coeurrt.mhrecorder.detection.QuestStartDetection;
import com.coeurrt.mhrecorder.obs.ObsWebSocketClient;
import com.coeurrt.mhrecorder.ui.MhRecorderApplication;
import javafx.application.Application;

import java.net.URI;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws Exception {
//        URI obsUri = new URI("ws://localhost:4455");
//
//        ObsWebSocketClient client = new ObsWebSocketClient(obsUri);
//
//        MhRecorderApplication.setObsClient(client);
//
//        Application.launch(MhRecorderApplication.class, args);

        QuestStartDetection q = new QuestStartDetection();
        for (int i = 1; i <= 5; i++) {
            System.out.println(i);
            System.out.println(q.detect(Path.of("C:\\Users\\Mimi\\Documents\\dev\\screen"+i+".png")));
        }

    }
}