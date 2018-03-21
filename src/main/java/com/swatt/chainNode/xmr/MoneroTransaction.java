package com.swatt.chainNode.xmr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
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
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.util.OperationFailedException;

public class MoneroTransaction extends ChainNodeTransaction {
    private String url;
    private static final Logger LOGGER = Logger.getLogger(MoneroTransaction.class.getName());
    private static final String TXN_URL_SUFFIX = "/gettransactions";

    public MoneroTransaction(String url, String hash) {
        super(hash);

        this.url = url + TXN_URL_SUFFIX;

        try {
            RPCTransaction rpcTransaction = fetchFromBlockchain(hash);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public class RPCTransactionCall {
        public String txs_hashes;
    }

    private RPCTransaction fetchFromBlockchain(String transactionHash)
            throws URISyntaxException, UnsupportedEncodingException, IOException {
        try {

            // Build the server URI together with the parameters you wish to pass
            URIBuilder uriBuilder = new URIBuilder(url);

            HttpPost postRequest = new HttpPost(uriBuilder.build());
            postRequest.setHeader("Content-Type", "application/json");

            String paramJSON = "{\"txs_hashes\":[\"" + transactionHash + "\"], \"decode_as_json\": true} ";

            // pass the json string request in the entity
            HttpEntity entity = new ByteArrayEntity(paramJSON.getBytes("UTF-8"));
            postRequest.setEntity(entity);

            // create a socketfactory in order to use an http connection manager
            PlainConnectionSocketFactory plainSocketFactory = PlainConnectionSocketFactory.getSocketFactory();
            Registry<ConnectionSocketFactory> connSocketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory>create().register("http", plainSocketFactory).build();

            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
                    connSocketFactoryRegistry);

            connManager.setMaxTotal(20);
            connManager.setDefaultMaxPerRoute(20);

            /*
             * RequestConfig defaultRequestConfig = RequestConfig.custom();
             * .setSocketTimeout(HttpClientPool.connTimeout)
             * .setConnectTimeout(HttpClientPool.connTimeout)
             * .setConnectionRequestTimeout(HttpClientPool.readTimeout) .build();
             */

            // Build the http client.
            CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(connManager).build();
            // .setDefaultRequestConfig(defaultRequestConfig)

            CloseableHttpResponse response = httpclient.execute(postRequest);

            // Read the response
            String responseString = "";

            int statusCode = response.getStatusLine().getStatusCode();
            String message = response.getStatusLine().getReasonPhrase();

            HttpEntity responseHttpEntity = response.getEntity();

            InputStream content = responseHttpEntity.getContent();

            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
            String line;

            while ((line = buffer.readLine()) != null) {
                responseString += line;
            }

            ObjectMapper mapper = new ObjectMapper();
            RPCTransaction rpcTransaction = mapper.readValue(responseString, RPCTransaction.class);

            RPCTransactionInternal rpcTransactionInternal = mapper.readValue(rpcTransaction.txs_as_json.get(0),
                    RPCTransactionInternal.class);

            RPCVin vin = rpcTransactionInternal.vin.get(0);

            System.out.println(vin.key.amount);

            // release all resources held by the responseHttpEntity
            EntityUtils.consume(responseHttpEntity);

            // close the stream
            response.close();

            // Close the connection manager.
            connManager.close();

            return null;

        } catch (

        Throwable t) {
            OperationFailedException e = new OperationFailedException(
                    "Error fetching transaction from Blockchain: " + transactionHash, t);
            LOGGER.log(Level.SEVERE, e.toString(), e);
            throw t;
        }
    }
}
