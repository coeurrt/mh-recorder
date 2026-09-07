package com.coeurrt.mhrecorder.obs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class ObsWebSocketClient extends WebSocketClient {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ObsWebSocketClient(URI serverUri) {
        super(serverUri);
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

            System.out.println("OBS -> opcode: " + op);

            switch (op) {
                case 0:
                    handleHello(inputJson);
                    break;
                case 2:
                    System.out.println("Authenticated to OBS");
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

    private void handleHello(JsonNode node) {
        JsonNode data = node.get("d");
        JsonNode authentication = data.get("authentication");

        String challenge = authentication.get("challenge").asText();
        String salt = authentication.get("salt").asText();
        String password = System.getenv("OBS_PASSWORD");

        if (password == null) {
            throw new IllegalStateException("OBS_PASSWORD is not defined");
        }

        sendIdentify(challenge, salt, password);

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

    private void sendIdentify(String challenge, String salt, String password) {

        ObjectNode rootNode = objectMapper.createObjectNode();

        rootNode.put("op", 1);

        ObjectNode dataNode = objectMapper.createObjectNode();
        dataNode.put("rpcVersion", 1);
        dataNode.put("authentication", generateAuthentication(challenge, salt, password));

        rootNode.set("d", dataNode);

        send(rootNode.toString());
    }
}
