package com.coeurrt.mhrecorder;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigLoader {

    public static Map<String, String> load(Path path) throws IOException {
        Map<String, String> config = new HashMap<>();

        for (String line : Files.readAllLines(path)) {
            if (line.isBlank() || line.startsWith("#")) {
                continue;
            }

            String[] parts = line.split("=", 2);

            if (parts.length == 2) {
                config.put(parts[0].trim(), parts[1].trim());
            }
        }

        return config;
    }
}
