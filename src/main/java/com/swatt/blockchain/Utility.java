package com.swatt.blockchain;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

public class Utility {
    public static JsonRpcHttpClient initJSONRPC(String url, String rpcUser, String rpcPassword) {
        URL uri;
        JsonRpcHttpClient client = null;

        try {
            uri = new URL(url);

            if (rpcUser != null) {
                Authenticator.setDefault(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(rpcUser, rpcPassword.toCharArray());
                    }
                });
            }

            client = new JsonRpcHttpClient(uri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return client;
    }
}