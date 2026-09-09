package com.coeurrt.mhrecorder.video;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VideoFileManager {

    private static final String PARENT_DIR = "MHRecorder";

    public Path moveToRecorderFolder(Path sourcePath) {
        Path destinationPath = sourcePath.getParent().resolve(PARENT_DIR);

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
