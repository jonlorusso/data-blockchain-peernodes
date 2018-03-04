package com.swatt.blockchain.btc;

import com.googlecode.jsonrpc4j.JsonRpcClientException;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.blockchain.Utility;

public class BlockchainTransaction extends com.swatt.blockchain.BlockchainTransaction {
    JsonRpcHttpClient jsonrpcClient = null;
    RPCTransaction rpcTransaction;

    private String hash;

    public BlockchainTransaction(String hash) {
        this(hash, null, null, null);
    }

    public BlockchainTransaction(String hash, String blockHash, String[] inputs, double[] outputs) {
        super(hash, blockHash, inputs, outputs);
        this.hash = hash;

        jsonrpcClient = Utility.initJSONRPC();

        fetchFromBlockchain(hash);
    }

    @Override
    public Double getFee() {
        Double outValue = this.outputValue();

        Double inValue = 0.0;
        Double fee = 0.0;

        Vin inTransaction = null;
        com.swatt.blockchain.btc.BlockchainTransaction transaction = null;

        for (int i = 0; i < rpcTransaction.vin.size(); i++) {
            inTransaction = rpcTransaction.vin.get(i);

            if (inTransaction.txid != null) {
                transaction = new com.swatt.blockchain.btc.BlockchainTransaction(inTransaction.txid);
                inValue += transaction.outputValue(inTransaction.vout);
            }
        }

        if (inValue > 0) {
            fee = inValue - outValue;
        }

        return fee;
    }

    public Double outputValue() {
        Double outValue = 0.0;
        Vout outTransaction = null;

        // TODO clean up vout array once values read, no further need
        for (int i = 0; i < rpcTransaction.vout.size(); i++) {
            outTransaction = rpcTransaction.vout.get(i);
            outValue += outTransaction.value;
        }

        return outValue;
    }

    public Double outputValue(int i) {
        return rpcTransaction.vout.get(i).value;
    }

    private void fetchFromBlockchain(String transactionHash) {
        try {
            rpcTransaction = jsonrpcClient.invoke(BTCMethods.GET_RAW_TRANSACTION,
                    new Object[] { transactionHash, true }, RPCTransaction.class);

        } catch (JsonRpcClientException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getHash() {
        return rpcTransaction.hash;
    }
}
