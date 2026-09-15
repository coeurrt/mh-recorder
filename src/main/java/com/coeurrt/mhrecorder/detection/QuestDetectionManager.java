package com.coeurrt.mhrecorder.detection;

import com.coeurrt.mhrecorder.obs.ObsConnectionManager;

public class QuestDetectionManager {

    private final ObsConnectionManager obsConnectionManager;
    private final QuestStartDetection questStartDetection;
    private final QuestEndDetection questEndDetection;
    private QuestState currentState;

    public QuestDetectionManager(ObsConnectionManager obsConnectionManager) {
        this.obsConnectionManager = obsConnectionManager;
        questStartDetection = new QuestStartDetection();
        questEndDetection = new QuestEndDetection();
        currentState = QuestState.IDLE;
    }

    public void startDetection() {
        obsConnectionManager.setScreenshotCallback(image -> {
            if (currentState == QuestState.IDLE) {
                if (questStartDetection.detect(image)) {
                    currentState = QuestState.IN_QUEST;
                    System.out.println("QUEST STARTED");
                }
            } else {
                if (questEndDetection.detect(image)) {
                    currentState = QuestState.IDLE;
                    System.out.println("QUEST ENDED");
                }
            }
        });
    }
}
