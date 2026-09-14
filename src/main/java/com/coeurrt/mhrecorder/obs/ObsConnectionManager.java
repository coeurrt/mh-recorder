package com.coeurrt.mhrecorder.obs;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ObsConnectionManager {
    private static final int RECONNECT_INTERVAL = 5000;
    private final URI obsUri;
    private boolean isShuttingDown = false;
    private boolean isConnected = false;
    private ObsWebSocketClient client;
    private ScheduledExecutorService reconnectScheduler;
    private Consumer<Boolean> connectionCallback;
    private Consumer<Boolean> statusCallback;
    private Consumer<BufferedImage> screenshotCallback;
    private Consumer<Path> pathCallback;

    public ObsConnectionManager(URI obsUri) {
        this.obsUri = obsUri;
    }

    public void setConnectionCallback(Consumer<Boolean> connectionCallback) {
        this.connectionCallback = connectionCallback;

        connectionCallback.accept(isConnected);
    }

    public void setRecordingStatusCallback(Consumer<Boolean> statusCallback) {
        this.statusCallback = statusCallback;

        if (client != null) {
            client.setRecordingStatusCallback(statusCallback);
        }
    }

    public void setScreenshotCallback(Consumer<BufferedImage> screenshotCallback) {
        this.screenshotCallback = screenshotCallback;

        if (client != null) {
            client.setScreenshotCallback(screenshotCallback);
        }
    }

    public void setPathCallback(Consumer<Path> pathCallback) {
        this.pathCallback = pathCallback;

        if (client != null) {
            client.setPathCallback(pathCallback);
        }
    }

    public void createObsClient() {
        client = new ObsWebSocketClient(obsUri);
        client.setConnectionCallback(connected -> {
            isConnected = connected;
            if (!connected && !isShuttingDown) {
                startReconnectScheduler();
            } else {
                stopReconnectScheduler();
            }
            if (connectionCallback != null) {
                connectionCallback.accept(connected);
            }
        });
        client.setRecordingStatusCallback(statusCallback);
        client.setScreenshotCallback(screenshotCallback);
        client.setPathCallback(pathCallback);
    }

    public void connect() {
        createObsClient();
        client.connect();

    }

    private void stopReconnectScheduler() {
        if (reconnectScheduler != null && !reconnectScheduler.isShutdown()) {
            reconnectScheduler.shutdownNow();
            reconnectScheduler = null;
        }
    }

    private void startReconnectScheduler() {
        if (reconnectScheduler == null || reconnectScheduler.isShutdown()) {
            reconnectScheduler = Executors.newSingleThreadScheduledExecutor();

            reconnectScheduler.scheduleAtFixedRate(
                    () -> {
                        try {
                            System.out.println("Reconnecting...");
                            connect();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    },
                    0,
                    RECONNECT_INTERVAL,
                    TimeUnit.MILLISECONDS
            );
        }
    }

    public void startRecording() {
        if (client != null) {
            client.startRecording();
        }
    }

    ;

    public void stopRecording() {
        if (client != null) {
            client.stopRecording();
        }
    }

    ;

    public void shutdown() {
        stopReconnectScheduler();
        isShuttingDown = true;
        if (client != null && client.isOpen()) {
            client.close();
        }
    }
}
