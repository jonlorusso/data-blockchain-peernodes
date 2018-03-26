package com.swatt.chainNode.service;

import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swatt.util.CollectionsUtilities;

public class ChainNodeRestTester {
    private static String testTest = "{\"hash\":\"46575afff8d7ab38927bace94a343c18b2212dc2f6a3c353326a8986c0b0ba7f\",\"timestamp\":1452792495,\"fee\":0.03856904595600241,\"feeRate\":0.03856904595600241,\"amount\":34.07,\"blockHash\":\"912320\"}";
    private static String testURL = "/xmr/txn/46575afff8d7ab38927bace94a343c18b2212dc2f6a3c353326a8986c0b0ba7f";

    public static String runURL(String url) {
        try {
            HttpClientBuilder hcb = HttpClientBuilder.create();
            HttpClient client = hcb.build();
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Accept", "application/json");
            HttpResponse response = client.execute(httpGet);

            if (response.getStatusLine().getStatusCode() >= 400) {
                System.out.println("Service could not answer due to: " + response.getStatusLine());
                return null;
            } else {
                String entities = EntityUtils.toString(response.getEntity());
                return entities;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            String propertiesFileName = "config.properties";
            Properties properties = CollectionsUtilities.loadProperties(propertiesFileName);
            int port = Integer.parseInt(properties.getProperty("servicePort"));

            String url = "http://localhost:" + port + testURL;
            String returnedJson = runURL(url);

            ObjectMapper mapper = new ObjectMapper();

            ChainNodeRestTesterTest txTest = mapper.readValue(testTest, ChainNodeRestTesterTest.class);
            ChainNodeRestTesterTest txResult = mapper.readValue(returnedJson, ChainNodeRestTesterTest.class);

            if (txResult.equals(txTest))
                System.out.println("same!");
            else
                System.out.println("not same!");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
