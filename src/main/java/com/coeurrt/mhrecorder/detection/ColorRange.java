package com.coeurrt.mhrecorder.detection;

public record ColorRange(
        double minHue,
        double maxHue,
        double minSaturation,
        double maxSaturation,
        double minBrightness,
        double maxBrightness,
        double detectionThreshold,
        int consecutiveDetectionThreshold
) {}
