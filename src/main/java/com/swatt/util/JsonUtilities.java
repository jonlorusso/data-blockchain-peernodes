package com.swatt.util;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

public class JsonUtilities {
	
    public static String objectToJsonString(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
        }

        return json;
    }
    
    
    public static JsonRpcHttpClient createJsonRpcHttpClient(String url, String rpcUser, String rpcPassword) {
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
