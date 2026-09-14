package com.coeurrt.mhrecorder.ui;

import com.coeurrt.mhrecorder.obs.ObsConnectionManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MhRecorderApplication extends Application {

    private static ObsConnectionManager obsConnectionManager;
    private boolean isConnected = false;
    private boolean isRecording = false;

    public static void setObsConnectionManager(ObsConnectionManager obsConnectionManager) {
        MhRecorderApplication.obsConnectionManager = obsConnectionManager;
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
        obsConnectionManager.setConnectionCallback(connected -> {
            Platform.runLater(() -> {
                isConnected = connected;

                connectionLabel.setText(
                        "Connection: " + (connected ? "Connected" : "Disconnected")
                );

                updateButtons(startRecordButton, stopRecordButton);
            });
        });

        Label statusLabel = new Label("Recording: Unknown");
        root.getChildren().add(statusLabel);
        obsConnectionManager.setRecordingStatusCallback(recording -> {
            Platform.runLater(() -> {
                isRecording = recording;

                statusLabel.setText(
                        "Recording: " + (recording ? "Recording" : "Stopped")
                );

                updateButtons(startRecordButton, stopRecordButton);
            });
        });

        Label pathLabel = new Label("Last record path: Unknown");
        root.getChildren().add(pathLabel);
        obsConnectionManager.setPathCallback(path -> {
            Platform.runLater(() -> {
                pathLabel.setText("Last record path: " + path.toString());
            });
        });

        startRecordButton.setOnAction(event -> {
            obsConnectionManager.startRecording();
        });

        stopRecordButton.setOnAction(event -> {
            obsConnectionManager.stopRecording();
        });

        stage.setTitle("MH Recorder");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        obsConnectionManager.shutdown();
    }

    private void updateButtons(Button startRecordButton, Button stopRecordButton) {
        if (!isConnected) {
            startRecordButton.setDisable(true);
            stopRecordButton.setDisable(true);
            return;
        }

        startRecordButton.setDisable(isRecording);
        stopRecordButton.setDisable(!isRecording);
    }
}
