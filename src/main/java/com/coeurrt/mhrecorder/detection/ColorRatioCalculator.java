package com.coeurrt.mhrecorder.detection;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorRatioCalculator {
    public static  double calculateRatio(BufferedImage roiImage,ColorRange colorRange) {
        int pixelCounter = 0;

        for (int y = 0; y < roiImage.getHeight(); y++) {
            for (int x = 0; x < roiImage.getWidth(); x++) {
                int rgb = roiImage.getRGB(x, y);
                float[] hsb = Color.RGBtoHSB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, null);
                if (hsb[0] >= colorRange.minHue()
                        && hsb[0] <= colorRange.maxHue()
                        && hsb[1] >= colorRange.minSaturation()
                        && hsb[1] <= colorRange.maxSaturation()
                        && hsb[2] >= colorRange.minBrightness()
                        && hsb[2] <= colorRange.maxBrightness()) {
                    pixelCounter++;
                }
            }
        }

        return (double) pixelCounter / (roiImage.getWidth() * roiImage.getHeight());
    }
}
