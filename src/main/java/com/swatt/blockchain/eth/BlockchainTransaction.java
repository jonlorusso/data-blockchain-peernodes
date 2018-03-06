package com.swatt.blockchain.eth;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.jsonrpc4j.JsonRpcClientException;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.blockchain.btc.Utility;
import com.swatt.blockchain.btc.Vin;

public class BlockchainTransaction extends com.swatt.blockchain.BlockchainTransaction {
    private static final Logger LOGGER = Logger.getLogger(BlockchainTransaction.class.getName());

    JsonRpcHttpClient jsonrpcClient = null;
    RPCTransaction rpcTransaction;

    private String hash;
    private Double fee;
    private Double inValue;
    public Boolean minted = false;

    public BlockchainTransaction(String hash) {
        super(hash);

        this.hash = hash;

        jsonrpcClient = Utility.initJSONRPC();

        fetchFromBlockchain(hash);
    }

    public BlockchainTransaction(String hash, boolean calculate) {
        this(hash);

        if (calculate)
            calculate();
    }

    private void calculate() {
        Double outValue = this.getTransactionAmount();

        inValue = 0.0;
        this.fee = 0.0;

        Vin inTransaction = null;
        com.swatt.blockchain.btc.BlockchainTransaction transaction = null;

        if (inValue > 0) {
            this.fee = inValue - outValue;
        } else {
            this.minted = true;
        }
    }

    @Override
    public Double getTransactionFee() {
        return this.fee;
    }

    public Double getInValue() {
        return inValue;
    }

    @Override
    public Double getTransactionAmount() {
        return rpcTransaction.value;
    }

    @Override
    public Long getTimestamp() {
        return null;
    }

    private void fetchFromBlockchain(String transactionHash) {
        try {
            rpcTransaction = jsonrpcClient.invoke(ETHMethods.GET_TRANSACTION_BYHASH, new Object[] { transactionHash },
                    RPCTransaction.class);

        } catch (JsonRpcClientException e) {
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
