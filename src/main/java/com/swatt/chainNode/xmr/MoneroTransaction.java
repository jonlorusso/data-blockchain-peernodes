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
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.chainNode.dao.BlockData;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.json.HttpClientPool;

public class MoneroTransaction extends ChainNodeTransaction {
	private static final Logger LOGGER = Logger.getLogger(MoneroTransaction.class.getName());

	private static final String TXN_URL_SUFFIX = "/gettransactions";
	private static final int HTTP_POOL = 10; // TODO: Should get from chainNodeConfig

	private String url;
    private long blockHeight;

    private MoneroChainNode node;
    private HttpClientPool httpClientPool;

    public MoneroTransaction(MoneroChainNode node, String url, String hash, boolean calculateTimestamp) {
        super(hash);

        this.node = node;
        this.url = url + TXN_URL_SUFFIX;

        httpClientPool = new HttpClientPool(this.url, HTTP_POOL);

        try {
            HttpResultTransaction httpTransaction = fetchFromBlockchain(hash);

            if (calculateTimestamp)
                calculateTimestamp();
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

    private HttpResultTransaction fetchFromBlockchain(String transactionHash)
            throws URISyntaxException, UnsupportedEncodingException, IOException {
        try {
            String params = "{\"txs_hashes\":[\"" + transactionHash + "\"], \"decode_as_json\": true} ";

            CloseableHttpResponse response = httpClientPool.execute(params);
            String responseString = readResponse(response);

            ObjectMapper mapper = new ObjectMapper();
            HttpResultTransaction httpResultTransaction = mapper.readValue(responseString, HttpResultTransaction.class);

            long inAmount = 0;
            long outAmount = 0;

            for (String transactionJSON : httpResultTransaction.txs_as_json) {
                HttpResultTransactionInternal httpResultTransactionInternal = mapper.readValue(transactionJSON,
                        HttpResultTransactionInternal.class);

                for (HttpResultVin vin : httpResultTransactionInternal.vin) {
                    inAmount += vin.key.amount;
                    ;
                }

                for (HttpResultVout vout : httpResultTransactionInternal.vout) {
                    outAmount += vout.amount;
                }
            }

            double inAmountXmr = inAmount * Math.pow(10, (-1 * MoneroChainNode.POWX_ATOMIC_UNITS));
            double outAmountXmr = outAmount * Math.pow(10, (-1 * MoneroChainNode.POWX_ATOMIC_UNITS));

            double fee = inAmountXmr - outAmountXmr;

            this.blockHeight = httpResultTransaction.txs.get(0).block_height;
            String blockHash = Long.toString(blockHeight);

            setBlockHash(blockHash);
            setFee(fee);
            setFeeRate(fee);
            setAmount(outAmountXmr);

            return null;

        } catch (

        Throwable t) {
            OperationFailedException e = new OperationFailedException(
                    "Error fetching transaction from Blockchain: " + transactionHash, t);
            LOGGER.log(Level.SEVERE, e.toString(), e);
            throw t;
        }
    }

    private String readResponse(CloseableHttpResponse response) {
        String responseString = "";

        // int statusCode = response.getStatusLine().getStatusCode();
        // String message = response.getStatusLine().getReasonPhrase();

        HttpEntity responseHttpEntity = response.getEntity();

        InputStream content;
        try {
            content = responseHttpEntity.getContent();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
            String line;

            while ((line = buffer.readLine()) != null) {
                responseString += line;
            }

            EntityUtils.consume(responseHttpEntity);
        } catch (UnsupportedOperationException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return responseString;
    }

    private void calculateTimestamp() {
        BlockData block;
        try {
            block = node.fetchBlockDataByHeight(this.blockHeight, false);
            setTimestamp(block.getTimestamp());
        } catch (OperationFailedException e) {
            e.printStackTrace();
        }
    }
}
