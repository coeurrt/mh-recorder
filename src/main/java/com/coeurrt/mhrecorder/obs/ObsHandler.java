package com.coeurrt.mhrecorder.obs;

import com.coeurrt.mhrecorder.video.VideoFileManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.function.Consumer;

public class ObsHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final VideoFileManager videoFileManager = new VideoFileManager();
    private Consumer<Boolean> statusCallback;
    private Consumer<Path> pathCallback;
    private Consumer<BufferedImage> screenshotCallback;
    private final ObsGameCapture obsGameCapture = new ObsGameCapture();

    public ObjectNode handleHello(JsonNode node) {
        JsonNode data = node.get("d");

        ObjectNode rootNode = objectMapper.createObjectNode();

        rootNode.put("op", 1);

        ObjectNode dataNode = objectMapper.createObjectNode();
        dataNode.put("rpcVersion", 1);

        if (data.has("authentication")) {
            JsonNode authentication = data.get("authentication");
            String challenge = authentication.get("challenge").asText();
            String salt = authentication.get("salt").asText();
            String password = System.getenv("OBS_PASSWORD");
            if (password == null) {
                throw new IllegalStateException("OBS_PASSWORD is not defined");
            }
            dataNode.put("authentication", generateAuthentication(challenge, salt, password));
        }


        rootNode.set("d", dataNode);
        return rootNode;
    }

    public void handleRequestResponse(JsonNode node) {
        JsonNode data = node.get("d");
        if (data.get("requestStatus").get("result").asBoolean()) {
            String requestType = data.get("requestType").asText();
            switch (requestType) {
                case "GetSourceScreenshot":
                    if (screenshotCallback != null) {
                        screenshotCallback.accept(
                                obsGameCapture.decodeRequestToBufferedImage(data.get("responseData").get("imageData").asText()));
                    }
                    break;
                case "StopRecord":
                    Path path = Path.of(data.get("responseData").get("outputPath").asText());
                    pathCallback.accept(path);
                    break;
                case "GetRecordStatus":
                    statusCallback.accept(data.get("responseData").get("outputActive").asBoolean());
            }
        } else System.out.println("Request failed");
    }

    public void handleEvent(JsonNode node) {
        JsonNode data = node.get("d");

        String eventType = data.get("eventType").asText();

        if ("RecordStateChanged".equals(eventType) && statusCallback != null) {
            boolean active = data.get("eventData").get("outputActive").asBoolean();

            statusCallback.accept(active);
        }
        if ("RecordStateChanged".equals(eventType)
                && "OBS_WEBSOCKET_OUTPUT_STOPPED".equals(data.get("eventData").get("outputState").asText())) {
            videoFileManager.moveToRecorderFolder(Path.of(data.get("eventData").get("outputPath").asText()));
        }
    }

    private String generateAuthentication(String challenge, String salt, String password) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            String secretInput = password + salt;
            byte[] secretHash = md.digest(secretInput.getBytes(StandardCharsets.UTF_8));

            String authenticationInput = Base64.getEncoder().encodeToString(secretHash) + challenge;
            byte[] authenticationHash = md.digest(authenticationInput.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(authenticationHash);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate OBS authentication", e);
        }
    }

    public void setStatusCallback(Consumer<Boolean> callback) {
        this.statusCallback = callback;
    }

    public void setPathCallback(Consumer<Path> pathCallback) {
        this.pathCallback = pathCallback;
    }

    public void setScreenshotCallback(Consumer<BufferedImage> screenshotCallback) {
        this.screenshotCallback = screenshotCallback;
    }
}
