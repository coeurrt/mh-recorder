package com.coeurrt.mhrecorder.detection;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class QuestEndDetection {
    private final static double X_START_PERCENT = 0.12;
    private final static double X_END_PERCENT = 0.88;
    private final static double Y_START_PERCENT = 0.23;
    private final static double Y_END_PERCENT = 0.74;
    private static final int CONSECUTIVE_DETECTION_THRESHOLD = 4;
    private static final ColorRange SUCCESS_RANGE =
            new ColorRange(0.13, 0.17, 0.5,1, 0.9,1,0.03,6);
    private static final ColorRange FAILURE_RANGE =
            new ColorRange(0.97, 0.98, 0.7,1, 0.85,1,0.003,4);
    private static final ColorRange ABANDON_RANGE =
            new ColorRange(0.66, 0.67, 0.4, 1,0.88,1,0.002,4);
    private int consecutiveDetections = 0;

    public boolean detect(BufferedImage image) {

        BufferedImage roiImage = createRoiImage(image);

        double successRatio = ColorRatioCalculator.calculateRatio(roiImage,SUCCESS_RANGE);
        double failureRatio = ColorRatioCalculator.calculateRatio(roiImage,FAILURE_RANGE);
        double abandonRatio = ColorRatioCalculator.calculateRatio(roiImage,ABANDON_RANGE);

        log("successRatio",successRatio);
        log("failureRatio",failureRatio);
        log("abandonRatio",abandonRatio);

        boolean endColorDetected =
                successRatio > SUCCESS_RANGE.detectionThreshold()
                        || failureRatio > FAILURE_RANGE.detectionThreshold()
                        || abandonRatio > ABANDON_RANGE.detectionThreshold();

        if (endColorDetected) {
            consecutiveDetections++;
        } else {
            consecutiveDetections = 0;
        }
        return consecutiveDetections == CONSECUTIVE_DETECTION_THRESHOLD;

    }

    //TODO POC DELETE AFTER
    private void log(String label,double ratio) {
        Path logPath = Path.of("C:\\Users\\Mimi\\Documents\\dev\\quest-detection.log");

        String logLine = LocalDateTime.now() + " | " + label + " | ratio=" + ratio + " | above threshold=" + (ratio > 0) + System.lineSeparator();
        try {
            Files.writeString(logPath, logLine, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedImage createRoiImage(BufferedImage image) {
        int xStart = (int) (image.getWidth() * X_START_PERCENT);
        int xEnd = (int) (image.getWidth() * X_END_PERCENT);
        int yStart = (int) (image.getHeight() * Y_START_PERCENT);
        int yEnd = (int) (image.getHeight() * Y_END_PERCENT);
        return image.getSubimage(xStart, yStart, xEnd - xStart, yEnd - yStart);
    }
}
