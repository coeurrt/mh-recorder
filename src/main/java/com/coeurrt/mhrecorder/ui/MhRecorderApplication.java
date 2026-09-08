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

        Label statusLabel = new Label("Status: disconnected");
        root.getChildren().add(statusLabel);
        obsClient.getObsHandler().setStatusCallback(status -> {
            Platform.runLater(() -> {
                statusLabel.setText("Status: " + status);
            });
        });
        Button startRecordButton = new Button("Record");
        root.getChildren().add(startRecordButton);
        Button stopRecordButton = new Button("Stop");
        root.getChildren().add(stopRecordButton);

        startRecordButton.setOnAction(event -> {
            obsClient.startRecording();
        });

        stopRecordButton.setOnAction(event -> {
            obsClient.stopRecording();
        });

        stage.setTitle("MH Recorder");
        stage.setScene(scene);
        stage.show();
    }
}
