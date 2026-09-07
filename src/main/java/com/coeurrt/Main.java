package com.coeurrt;

import com.coeurrt.mhrecorder.obs.ObsWebSocketClient;

import java.net.URI;

public class Main {

    public static void main(String[] args) throws Exception {

        URI obsUri = new URI("ws://localhost:4455");

        ObsWebSocketClient client = new ObsWebSocketClient(obsUri);

        client.connectBlocking();
    }
}