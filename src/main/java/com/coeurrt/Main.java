package com.coeurrt;

import com.coeurrt.mhrecorder.detection.QuestStartDetection;
import com.coeurrt.mhrecorder.detection.WindowsGameLocator;

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

        while (true) {
            if(q.detect())
                System.out.println("QUEST STARTED");
            Thread.sleep(500);
        }

    }
}