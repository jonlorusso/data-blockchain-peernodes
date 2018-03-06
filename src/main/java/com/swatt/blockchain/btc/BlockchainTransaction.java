package com.swatt.blockchain.btc;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.jsonrpc4j.JsonRpcClientException;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

public class BlockchainTransaction extends com.swatt.blockchain.BlockchainTransaction {
    private static final Logger LOGGER = Logger.getLogger(BlockchainTransaction.class.getName());

    JsonRpcHttpClient jsonrpcClient = null;
    RPCTransaction rpcTransaction;

    private String hash;
    private Double fee;
    private Double inValue;
    public Boolean minted = false;

    public BlockchainTransaction(String hash) {
        this(hash, false);
    }

    public BlockchainTransaction(String hash, boolean calculate) {
        super(hash);

        this.hash = hash;

        System.out.println(hash);

        jsonrpcClient = Utility.initJSONRPC();

        fetchFromBlockchain(hash);

        if (calculate)
            calculate();
    }

    private void calculate() {
        Double outValue = this.getTransactionAmount();

        inValue = 0.0;
        this.fee = 0.0;

        Vin inTransaction = null;
        com.swatt.blockchain.btc.BlockchainTransaction transaction = null;

        for (int i = 0; i < rpcTransaction.vin.size(); i++) {
            inTransaction = rpcTransaction.vin.get(i);

            if (inTransaction.txid != null) {
                transaction = new com.swatt.blockchain.btc.BlockchainTransaction(inTransaction.txid, false);
                inValue += transaction.outputValue(inTransaction.vout);
            }
        }

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
        Double outValue = 0.0;
        Vout outTransaction = null;

        // TODO clean up vout array once values read, no further need
        for (int i = 0; i < rpcTransaction.vout.size(); i++) {
            outTransaction = rpcTransaction.vout.get(i);
            outValue += outTransaction.value;
        }

        return outValue;
    }

    @Override
    public Long getTimestamp() {
        return rpcTransaction.time;
    }

    public Double outputValue(int i) {
        return rpcTransaction.vout.get(i).value;
    }

    public Long getSize() {
        return rpcTransaction.vsize;
    }

    private void fetchFromBlockchain(String transactionHash) {
        try {
            rpcTransaction = jsonrpcClient.invoke(BTCMethods.GET_RAW_TRANSACTION,
                    new Object[] { transactionHash, true }, RPCTransaction.class);

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