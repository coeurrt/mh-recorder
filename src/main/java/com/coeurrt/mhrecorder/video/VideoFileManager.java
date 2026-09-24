package com.coeurrt.mhrecorder.video;

import com.coeurrt.mhrecorder.AppConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VideoFileManager {

    public Path moveToRecorderFolder(Path sourcePath) {
        Path destinationPath = Path.of(AppConfig.VIDEO_PATH);

        try {
            Files.createDirectories(destinationPath);

            String fileName = sourcePath.getFileName().toString();
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

            Path finalPath = destinationPath.resolve(
                    formatter.format(now) + "_unknown-monster" + fileExtension
            );

            Files.move(sourcePath, finalPath);

            return finalPath;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
