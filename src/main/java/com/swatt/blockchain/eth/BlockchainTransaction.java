package com.swatt.blockchain.eth;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.jsonrpc4j.JsonRpcClientException;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

public class BlockchainTransaction extends com.swatt.blockchain.BlockchainTransaction {
    private static final Logger LOGGER = Logger.getLogger(BlockchainTransaction.class.getName());

    RPCTransaction rpcTransaction;

    private String hash;
    private Double fee;
    private Double inValue;
    public Boolean minted = false;

    public BlockchainTransaction(String hash) {
        this(null, hash, false);
    }

    public BlockchainTransaction(JsonRpcHttpClient jsonrpcClient, String hash, boolean calculate) {
        super(hash);

        this.hash = hash;

        fetchFromBlockchain(jsonrpcClient, hash);

        if (calculate)
            calculate(jsonrpcClient);
    }

    private void calculate(JsonRpcHttpClient jsonrpcClient) {
        Double outValue = this.getAmount();

        inValue = 0.0;
        this.fee = 0.0;

        com.swatt.blockchain.eth.BlockchainTransaction transaction = null;

        if (inValue > 0) {
            this.fee = inValue - outValue;
        } else {
            this.minted = true;
        }
    }

    @Override
    public Double getFee() {
        return this.fee;
    }

    public Double getInValue() {
        return inValue;
    }

    @Override
    public Double getAmount() {
        return Double.valueOf(rpcTransaction.value);
    }

    @Override
    public Long getTimestamp() {
        return null;
    }

    private void fetchFromBlockchain(JsonRpcHttpClient jsonrpcClient, String transactionHash) {
        try {
            rpcTransaction = jsonrpcClient.invoke(ETHMethods.GET_TRANSACTION_BYHASH,
                    new Object[] { transactionHash, true }, RPCTransaction.class);

        } catch (JsonRpcClientException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public String getHash() {
        return hash;
    }
}
