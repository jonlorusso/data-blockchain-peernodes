package com.swatt.blockchain.btc;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.blockchain.Utility;

public class BlockchainBlock {
    JsonRpcHttpClient jsonrpcClient = null;
    RPCBlock block;

    public BlockchainBlock(String blockHash) {
        if (jsonrpcClient == null) {
            jsonrpcClient = Utility.initJSONRPC();
        }

        findBlockByHash(blockHash);
    }

    public BlockchainBlock(Long blockId) {
        if (jsonrpcClient == null) {
            jsonrpcClient = Utility.initJSONRPC();
        }

        findBlockById(blockId);
    }

    public BlockchainBlock() {
        if (jsonrpcClient == null) {
            jsonrpcClient = Utility.initJSONRPC();
        }

        findLatestBlock();
    }

    private void findBlockByHash(String blockHash) {
        try {
            block = jsonrpcClient.invoke(BTCMethods.GET_BLOCK, new Object[] { blockHash }, RPCBlock.class);
            System.out.println(block.tx);

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void findBlockById(Long blockId) {
        String blockHash;

        try {
            blockHash = jsonrpcClient.invoke(BTCMethods.GET_BLOCK_HASH, new Object[] { blockId }, String.class);

            // RPCTransaction tx = jsonrpcClient.invoke(BTCMethods.GET_RAW_TRANSACTION, new
            // Object[] {"this tx hash"}, RPCTransaction.class);
            findBlockByHash(blockHash);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void findLatestBlock() {
        Long blockCount;

        try {
            blockCount = jsonrpcClient.invoke(BTCMethods.GET_BLOCK_COUNT, new Object[] {}, Long.class);
            findBlockById(blockCount);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public String getTransactionHashes() {
        return block.tx.get(1);
    }

    public void processTransactions() {
        for (int i = 0; i < block.tx.size(); i++) {
            processTransaction(block.tx.get(i));
        }
    }

    public void processTransaction(String transactionHash) {

    }
}