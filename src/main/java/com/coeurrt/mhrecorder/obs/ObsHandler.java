package com.coeurrt.mhrecorder.obs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class ObsHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

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
        if (node.get("d").get("requestStatus").get("result").asBoolean()) {
            String requestType = node.get("d").get("requestType").asText();
            switch (requestType) {
                case "StopRecord":
                    System.out.println("Output Path: " + node.get("d").get("responseData").get("outputPath").asText());
            }
        } else System.out.println("Request failed");
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
}
