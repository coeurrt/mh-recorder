package com.coeurrt.mhrecorder.detection;

import java.awt.*;
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
    private static final double MIN_HUE = 0.47;
    private static final double MAX_HUE = 0.53;
    private static final double MIN_SATURATION = 0.7;
    private static final double MIN_BRIGHTNESS = 0.7;
    private static final double DETECTION_THRESHOLD = 0.03;
    private static final int CONSECUTIVE_DETECTION_THRESHOLD = 5;
    private int consecutiveDetections = 0;

    public boolean detect(BufferedImage image) {

        BufferedImage roiImage = createRoiImage(image);

        double cyanRatio = calculateCyanRatio(roiImage);

        log(cyanRatio);

        if (cyanRatio > DETECTION_THRESHOLD) {
            consecutiveDetections++;
        } else {
            consecutiveDetections = 0;
        }
        return consecutiveDetections == CONSECUTIVE_DETECTION_THRESHOLD;

    }

    private double calculateCyanRatio(BufferedImage roiImage) {
        int cyanPixelCounter = 0;

        for (int y = 0; y < roiImage.getHeight(); y++) {
            for (int x = 0; x < roiImage.getWidth(); x++) {
                int rgb = roiImage.getRGB(x, y);
                float[] hsb = Color.RGBtoHSB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, null);
                if (hsb[0] > MIN_HUE
                        && hsb[0] < MAX_HUE && hsb[1] > MIN_SATURATION
                        && hsb[2] > MIN_BRIGHTNESS) cyanPixelCounter++;
            }
        }

        return (double) cyanPixelCounter / (roiImage.getWidth() * roiImage.getHeight());
    }

    //TODO POC DELETE AFTER
    private void log(double ratio) {
        Path logPath = Path.of("C:\\Users\\Mimi\\Documents\\dev\\quest-detection.log");

        String logLine = LocalDateTime.now() + " | ratio=" + ratio + " | above threshold=" + (ratio > DETECTION_THRESHOLD) + System.lineSeparator();
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
