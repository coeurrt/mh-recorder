package com.coeurrt.mhrecorder.obs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class ObsWebSocketClient extends WebSocketClient {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObsRequestFactory obsRequestFactory = new ObsRequestFactory();
    private final ObsHandler obsHandler = new ObsHandler();

    public ObsWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    public ObsHandler getObsHandler() {
        return obsHandler;
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
                    sendRequest(obsHandler.handleHello((inputJson)));
                    break;
                case 2:
                    System.out.println("Authenticated to OBS");
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

        System.out.println("OBS -> " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from OBS");
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    private void sendRequest(ObjectNode request) {
        send(request.toString());
        System.out.println("Client -> OBS request opcode: " + request.get("op").toString());
    }

    public void startRecording() {
        sendRequest(obsRequestFactory.createStartRecordRequest("start-record-id"));
    }

    public void stopRecording() {
        sendRequest(obsRequestFactory.createStopRecordRequest("stop-record-id"));
    }
}
