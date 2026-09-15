package com.coeurrt.mhrecorder.detection;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class QuestStartDetection {

    private final static double X_START_PERCENT = 0.42;
    private final static double X_END_PERCENT = 0.58;
    private final static double Y_START_PERCENT = 0.1;
    private final static double Y_END_PERCENT = 0.32;
    private static final ColorRange START_RANGE =
            new ColorRange(0.47, 0.53, 0.70, 1, 0.70, 1, 0.1, 5);
    private static final ColorRange START_HUD_RANGE =
            new ColorRange(0.49, 0.51, 0.70, 1, 0.05, 0.40, 0.001, 5);
    private static final ColorRange TRAVEL_RANGE =
            new ColorRange(0, 1, 0, 1, 0, 0.05, 0.95, 5);
    private int consecutiveDetections = 0;

    public boolean detect(BufferedImage image) {

        BufferedImage roiImage = createRoiImage(image);

        double startRatio = ColorRatioCalculator.calculateRatio(roiImage, START_RANGE);
        double startHudRatio = ColorRatioCalculator.calculateRatio(roiImage, START_HUD_RANGE);
        double travelRatio = ColorRatioCalculator.calculateRatio(roiImage, TRAVEL_RANGE);

        if ((consecutiveDetections > 0 && travelRatio > TRAVEL_RANGE.detectionThreshold()) ||
                startRatio > START_RANGE.detectionThreshold() ||
                startHudRatio > START_HUD_RANGE.detectionThreshold()) {
            consecutiveDetections++;
        } else {
            consecutiveDetections = 0;
        }
        return consecutiveDetections == START_RANGE.consecutiveDetectionThreshold();

    }

    //TODO POC DELETE AFTER
    private void log(String label, double ratio) {
        Path logPath = Path.of("C:\\Users\\Mimi\\Documents\\dev\\quest-detection.log");

        String logLine = LocalDateTime.now() + " | " + label + " | ratio=" + ratio + System.lineSeparator();
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
