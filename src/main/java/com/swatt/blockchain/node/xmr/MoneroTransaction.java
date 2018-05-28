package com.swatt.blockchain.node.xmr;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swatt.blockchain.node.NodeTransaction;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.io.IoUtilities;
import com.swatt.util.json.HttpClientPool;

public class MoneroTransaction extends NodeTransaction {
	private static final Logger LOGGER = Logger.getLogger(MoneroTransaction.class.getName());

    private long blockHeight;
    
    public MoneroTransaction(String url, String hash, HttpClientPool httpClientPool) {
        super(hash);
        fetchFromBlockchain(url, hash, httpClientPool);
    }
    
    public long getHeight() {
        return blockHeight;
    }
    
    private void fetchFromBlockchain(String url, String transactionHash, HttpClientPool httpClientPool) {
        try {
            String params = "{\"txs_hashes\":[\"" + transactionHash + "\"], \"decode_as_json\": true}";

            CloseableHttpResponse response = httpClientPool.execute(url, params);
            String responseString = readResponse(response);

            ObjectMapper mapper = new ObjectMapper();
            HttpResultTransaction httpResultTransaction = mapper.readValue(responseString, HttpResultTransaction.class);

            long inAmount = 0;
            long outAmount = 0;

            for (String transactionJSON : httpResultTransaction.txs_as_json) {
                HttpResultTransactionInternal httpResultTransactionInternal = mapper.readValue(transactionJSON, HttpResultTransactionInternal.class);

                for (HttpResultVin vin : httpResultTransactionInternal.vin) {
                    inAmount += vin.key.amount;
                }

                for (HttpResultVout vout : httpResultTransactionInternal.vout) {
                    outAmount += vout.amount;
                }
            }

            double inAmountXmr = inAmount * Math.pow(10, (-1 * MoneroNode.POWX_ATOMIC_UNITS));
            double outAmountXmr = outAmount * Math.pow(10, (-1 * MoneroNode.POWX_ATOMIC_UNITS));

            double fee = inAmountXmr - outAmountXmr;

            this.blockHeight = httpResultTransaction.txs.get(0).block_height;
            
            setFee(fee);
            setFeeRate(fee);
            setAmount(outAmountXmr);
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, t.toString(), t);
        }
    }

    private String readResponse(CloseableHttpResponse response) throws OperationFailedException {
        try {
            HttpEntity responseHttpEntity = response.getEntity();
            String responseString = IoUtilities.streamToString(responseHttpEntity.getContent());
            EntityUtils.consume(responseHttpEntity);
            return responseString;
        } catch (UnsupportedOperationException | IOException e) {
            throw new OperationFailedException(e);
        }
    }
}
