package com.coeurrt.mhrecorder.detection;

import com.coeurrt.mhrecorder.AppConfig;
import com.coeurrt.mhrecorder.obs.ObsConnectionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class QuestDetectionManager {

    private final ObsConnectionManager obsConnectionManager;
    private final QuestStartDetection questStartDetection;
    private final QuestEndDetection questEndDetection;
    private final QuestStartDetection debugQuestStartDetection;
    private final QuestEndDetection debugQuestEndDetection;
    private QuestState currentState;

    public QuestDetectionManager(ObsConnectionManager obsConnectionManager) {
        this.obsConnectionManager = obsConnectionManager;
        questStartDetection = new QuestStartDetection();
        questEndDetection = new QuestEndDetection();
        debugQuestStartDetection = new QuestStartDetection();
        debugQuestEndDetection = new QuestEndDetection();
        currentState = QuestState.IDLE;
    }

    public void startDetection() {
        obsConnectionManager.setScreenshotCallback(image -> {
            if (currentState == QuestState.IDLE) {
                if (questStartDetection.detect(image)) {
                    currentState = QuestState.IN_QUEST;
                    log("QUEST STARTED");
                }
                if (debugQuestEndDetection.detect(image)) {
                    log("<!>FALSE QUEST ENDED DETECTED</!> | " + questEndDetection.detect(image));
                }
            } else {
                if (questEndDetection.detect(image)) {
                    currentState = QuestState.IDLE;
                    log("QUEST ENDED | " + questEndDetection.endReason());
                }
                if (debugQuestStartDetection.detect(image)) {
                    log("<!>FALSE QUEST STARTED DETECTED</!>");
                }
            }
        });
    }

    public void testLog() {
        log("TEST LOG");
    }

    //TODO POC DELETE AFTER
    private void log(String state) {
        Path logPath = Path.of(AppConfig.LOG_PATH)
                .resolve("detection-log.txt");

        String logLine =
                LocalDateTime.now()
                        + " | "
                        + state
                        + System.lineSeparator();

        try {
            Files.createDirectories(logPath.getParent());

            Files.writeString(
                    logPath,
                    logLine,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void hello(){
        log("hello");
    }

    public void end(){
        log("terminated");
    }
}
