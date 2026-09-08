package com.coeurrt.mhrecorder.obs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ObsRequestFactory {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final int REQUEST_OPCODE = 6;

    public ObjectNode createStartRecordRequest(String requestId) {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("op", REQUEST_OPCODE);

        ObjectNode dataNode = objectMapper.createObjectNode();
        dataNode.put("requestType", "StartRecord");
        dataNode.put("requestId", requestId);

        request.set("d", dataNode);
        return request;
    }

    public ObjectNode createStopRecordRequest(String requestId) {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("op", REQUEST_OPCODE);

        ObjectNode dataNode = objectMapper.createObjectNode();
        dataNode.put("requestType", "StopRecord");
        dataNode.put("requestId", requestId);

        request.set("d", dataNode);
        return request;
    }
}
