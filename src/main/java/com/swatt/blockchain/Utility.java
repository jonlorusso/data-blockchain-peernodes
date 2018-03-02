package com.swatt.blockchain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Properties;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

public class Utility {
    private int ByteArrayToInt(Byte[] Bytes) {
        byte[] bytes = new byte[Bytes.length];
        int i = 0;
        for (Byte b : Bytes) {
            bytes[i] = b.byteValue();
            i++;
        }
        int Integer = ByteBuffer.wrap(bytes).getInt();
        return Integer;
    }

    public static JsonRpcHttpClient initJSONRPC() {
        Properties prop = new Properties();
        InputStream input;
        URL uri;
        JsonRpcHttpClient client = null;

        try {
            input = new FileInputStream("config.properties");
            prop.load(input);
            uri = new URL(prop.getProperty("url"));

            final String rpcuser = prop.getProperty("rpcuser");
            final String rpcpassword = prop.getProperty("rpcpassword");

            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(rpcuser, rpcpassword.toCharArray());
                }
            });

            client = new JsonRpcHttpClient(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return client;
    }
}