package com.coeurrt.mhrecorder.obs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ObsWebSocketClient extends WebSocketClient {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObsRequestFactory obsRequestFactory = new ObsRequestFactory();
    private final ObsHandler obsHandler = new ObsHandler();
    private Consumer<Boolean> connectionCallback;
    private ScheduledExecutorService scheduler;

    public ObsWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    public void setRecordingStatusCallback(Consumer<Boolean> callback) {
        obsHandler.setStatusCallback(callback);
    }

    public void setPathCallback(Consumer<Path> callback) {
        obsHandler.setPathCallback(callback);
    }

    public void setConnectionCallback(Consumer<Boolean> connectionCallback) {
        this.connectionCallback = connectionCallback;
    }

    public void setScreenshotCallback(Consumer<BufferedImage> screenshotCallback) {
        obsHandler.setScreenshotCallback(screenshotCallback);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("Connected to OBS");
    }

    @Override
    public void onMessage(String message) {
        try {
            JsonNode inputJson = objectMapper.readTree(message);
            int op = inputJson.get("op").asInt();
            switch (op) {
                case 0:
                    // TODO Refactor Identify request creation into ObsRequestFactory
                    sendRequest(obsHandler.handleHello((inputJson)));
                    break;
                case 2:
                    System.out.println("Authenticated to OBS");
                    connectionCallback.accept(true);
                    startScreenshotScheduler();
                    sendRequest(obsRequestFactory.createGetRecordStatusRequest("get-record-status-on-connection"));
                    break;
                case 5:
                    obsHandler.handleEvent(inputJson);
                    break;
                case 7:
                    obsHandler.handleRequestResponse(inputJson);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from OBS");
        stopScreenshotScheduler();
        connectionCallback.accept(false);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    private void sendRequest(ObjectNode request) {
        send(request.toString());
        System.out.println("Client -> OBS request opcode: " + request.get("op").toString());
    }

    private void stopScreenshotScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }

    private void startScreenshotScheduler() {
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();

            scheduler.scheduleAtFixedRate(
                    this::getScreenshot,
                    0,
                    500,
                    TimeUnit.MILLISECONDS
            );
        }
    }

    public void startRecording() {
        sendRequest(obsRequestFactory.createStartRecordRequest("start-record-id"));
    }

    public void stopRecording() {
        sendRequest(obsRequestFactory.createStopRecordRequest("stop-record-id"));
    }

    public void getScreenshot() {
        sendRequest(obsRequestFactory.createGetSourceScreenshot("get-screenshot"));
    }
}
