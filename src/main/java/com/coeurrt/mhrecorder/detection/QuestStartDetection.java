package com.coeurrt.mhrecorder.detection;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class QuestStartDetection {

    private final static double X_START_PERCENT = 0.42;
    private final static double X_END_PERCENT = 0.58;
    private final static double Y_START_PERCENT = 0.05;
    private final static double Y_END_PERCENT = 0.32;
    private static final double DETECTION_THRESHOLD = 0.15;

    public boolean detect(Path imagePath) {

        //TODO POC test
        Path writePath = imagePath.getParent().resolve("test.png");

        try {
            BufferedImage image = ImageIO.read(imagePath.toFile());
            int width = image.getWidth();
            int height = image.getHeight();
            int xStart = (int) (width * X_START_PERCENT);
            int xEnd = (int) (width * X_END_PERCENT);
            int yStart = (int) (height * Y_START_PERCENT);
            int yEnd = (int) (height * Y_END_PERCENT);

            BufferedImage roiImage = image.getSubimage(xStart, yStart, xEnd - xStart, yEnd - yStart);
            ImageIO.write(roiImage, "png", writePath.toFile());

            int cyanPixelCounter = 0;

            for (int y = 0; y < roiImage.getHeight(); y++) {
                for (int x = 0; x < roiImage.getWidth(); x++) {
                    int rgb = roiImage.getRGB(x, y);
                    float[] hsb = Color.RGBtoHSB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, null);
                    if (hsb[0] > 0.47 && hsb[0] < 0.53 && hsb[2] > 0.70)
                        cyanPixelCounter++;
                }
            }

            double cyanRatio = (double) cyanPixelCounter / (roiImage.getWidth()* roiImage.getHeight());
            return (cyanRatio > DETECTION_THRESHOLD);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
