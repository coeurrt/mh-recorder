package com.coeurrt.mhrecorder.obs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

public class ObsGameCapture {
    public BufferedImage decodeRequestToBufferedImage(String imageData) {
        imageData = imageData.substring(imageData.indexOf(',') + 1);
        byte[] imageBytes = Base64.getDecoder().decode(imageData);
        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
