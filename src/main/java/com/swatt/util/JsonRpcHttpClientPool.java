package com.swatt.util;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.LinkedList;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

public class JsonRpcHttpClientPool { // FIXME: Not Industrial Strength. Does not deal with un-returned connections
    private String url;
    private String user;
    private String password;
    private int maxSize;
    private LinkedList<JsonRpcHttpClient> freeJsonRpcHttpClients = new LinkedList<JsonRpcHttpClient>();
    private LinkedList<JsonRpcHttpClient> busyJsonRpcHttpClients = new LinkedList<JsonRpcHttpClient>();

    public JsonRpcHttpClientPool(String url, String user, String password, int maxSize) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.maxSize = maxSize;
    }

    public JsonRpcHttpClient getJsonRpcHttpClient() {
        synchronized (freeJsonRpcHttpClients) {
            JsonRpcHttpClient jsonRpcHttpClient = null;

            if (freeJsonRpcHttpClients.size() > 0) {
                jsonRpcHttpClient = freeJsonRpcHttpClients.removeFirst();
            } else if ((freeJsonRpcHttpClients.size() + busyJsonRpcHttpClients.size()) < maxSize) {
                if (user == null)
                    jsonRpcHttpClient = createJsonRpcHttpClient(url);
                else
                    jsonRpcHttpClient = createJsonRpcHttpClient(url, user, password);
            } else {
                while (freeJsonRpcHttpClients.size() == 0) {
                    ConcurrencyUtilities.waitOn(freeJsonRpcHttpClients);
                }

                jsonRpcHttpClient = freeJsonRpcHttpClients.removeFirst();
            }

            busyJsonRpcHttpClients.add(jsonRpcHttpClient);
            return jsonRpcHttpClient;
        }
    }

    public void returnConnection(JsonRpcHttpClient jsonRpcHttpClient) {
        synchronized (freeJsonRpcHttpClients) {
            busyJsonRpcHttpClients.remove(jsonRpcHttpClient);
            freeJsonRpcHttpClients.add(jsonRpcHttpClient);
            ConcurrencyUtilities.notifyAll(freeJsonRpcHttpClients);
        }
    }

    private JsonRpcHttpClient createJsonRpcHttpClient(String url, String rpcUser, String rpcPassword) {
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

    private JsonRpcHttpClient createJsonRpcHttpClient(String url) {
        return createJsonRpcHttpClient(url, null, null);
    }
}