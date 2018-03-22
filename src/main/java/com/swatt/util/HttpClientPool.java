package com.swatt.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.LinkedList;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class HttpClientPool { // FIXME: Not Industrial Strength. Does not deal with un-returned connections
    private String url;
    private int maxSize;
    private LinkedList<CloseableHttpClient> freeHttpClients = new LinkedList<CloseableHttpClient>();
    private LinkedList<CloseableHttpClient> busyHttpClients = new LinkedList<CloseableHttpClient>();
    PoolingHttpClientConnectionManager connManager;

    public HttpClientPool(String url, int maxSize) {
        this.url = url;
        this.maxSize = maxSize;

        PlainConnectionSocketFactory plainSocketFactory = PlainConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> connSocketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", plainSocketFactory).build();

        connManager = new PoolingHttpClientConnectionManager(connSocketFactoryRegistry);

        connManager.setMaxTotal(this.maxSize);
        connManager.setDefaultMaxPerRoute(this.maxSize);
    }

    public CloseableHttpResponse execute(String params) {
        URIBuilder uriBuilder = null;
        HttpPost httpPost = null;

        try {
            uriBuilder = new URIBuilder(url);
            httpPost = new HttpPost(uriBuilder.build());
            httpPost.setHeader("Content-Type", "application/json");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        try {
            httpPost.setEntity(new ByteArrayEntity(params.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        synchronized (freeHttpClients) {
            CloseableHttpClient httpClient = null;

            if (freeHttpClients.size() > 0) {
                httpClient = freeHttpClients.removeFirst();
            } else if ((freeHttpClients.size() + busyHttpClients.size()) < maxSize) {
                httpClient = createHttpClient();
            } else {
                while (freeHttpClients.size() == 0) {
                    ConcurrencyUtilities.waitOn(freeHttpClients);
                }

                httpClient = freeHttpClients.removeFirst();
            }

            busyHttpClients.add(httpClient);

            CloseableHttpResponse response = null;
            try {
                response = httpClient.execute(httpPost);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return response;
        }
    }

    public void returnConnection(CloseableHttpClient httpClient) {
        synchronized (freeHttpClients) {
            busyHttpClients.remove(httpClient);
            freeHttpClients.add(httpClient);
            ConcurrencyUtilities.notifyAll(freeHttpClients);
        }
    }

    private CloseableHttpClient createHttpClient() {
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connManager).build();

        return httpClient;
    }
}