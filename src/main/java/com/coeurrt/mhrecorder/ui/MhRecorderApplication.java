package com.coeurrt.mhrecorder.ui;

import com.coeurrt.mhrecorder.obs.ObsWebSocketClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MhRecorderApplication extends Application {

    private static ObsWebSocketClient obsClient;

    public static void setObsClient(ObsWebSocketClient client) {
        obsClient = client;
    }

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();

        Scene scene = new Scene(root, 400, 250);

        Button startRecordButton = new Button("Record");
        root.getChildren().add(startRecordButton);
        Button stopRecordButton = new Button("Stop");
        root.getChildren().add(stopRecordButton);
        startRecordButton.setDisable(true);
        stopRecordButton.setDisable(true);

        Label connectionLabel = new Label("Connection: Disconnected");
        root.getChildren().add(connectionLabel);
        obsClient.setConnectionCallback(connected -> {
            Platform.runLater(() -> {
                connectionLabel.setText("Connection: " + (connected ? "Connected" : "Disconnected"));
            });
            startRecordButton.setDisable(!connected);
            stopRecordButton.setDisable(!connected);
        });

        Label statusLabel = new Label("Recording: Unknown");
        root.getChildren().add(statusLabel);
        obsClient.setRecordingStatusCallback(recording -> {
            Platform.runLater(() -> {
                statusLabel.setText("Recording: " + (recording ? "Recording" : "Stopped"));
                startRecordButton.setDisable(recording);
                stopRecordButton.setDisable(!recording);
            });
        });

        Label pathLabel = new Label("Last record path: Unknown");
        root.getChildren().add(pathLabel);
        obsClient.setPathCallback(path -> {
            Platform.runLater(() -> {
                pathLabel.setText("Last record path: " + path.toString());
            });
        });

        startRecordButton.setOnAction(event -> {
            obsClient.startRecording();
        });

        stopRecordButton.setOnAction(event -> {
            obsClient.stopRecording();
        });

        stage.setTitle("MH Recorder");
        stage.setScene(scene);
        stage.show();

        obsClient.connect();
    }

    @Override
    public void stop() {
        if (obsClient != null && obsClient.isOpen()) {
            obsClient.close();
        }
    }
}
