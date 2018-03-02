package com.swatt.blockchain.btc;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.blockchain.Utility;

public class BlockchainTransaction extends com.swatt.blockchain.BlockchainTransaction {
    JsonRpcHttpClient jsonrpcClient = null;
    RPCTransaction transaction;

    public BlockchainTransaction(String hash, String blockHash, String[] inputs, double[] outputs) {
        super(hash, blockHash, inputs, outputs);

        jsonrpcClient = Utility.initJSONRPC();

        findTransactionByHash(hash);
    }

    @Override
    public Long getFee() {
        // TODO Auto-generated method stub
        return null;
    }

    private void findTransactionByHash(String transactionHash) {
        try {
            transaction = jsonrpcClient.invoke(BTCMethods.GET_TRANSACTION, new Object[] { transactionHash },
                    RPCTransaction.class);
            System.out.println(transaction.hash);

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getHash() {
        return transaction.hash;
    }
}
