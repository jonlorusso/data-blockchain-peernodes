package com.swatt.blockchain.btc;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.blockchain.Utility;

public class BlockchainBlock extends com.swatt.blockchain.BlockchainBlock {
    JsonRpcHttpClient jsonrpcClient = null;
    RPCBlock block;

    public BlockchainBlock(String blockHash) {
        if (jsonrpcClient == null) {
            jsonrpcClient = Utility.initJSONRPC();
        }

        if (blockHash == null) {
            block = findLatestBlock();
        } else {
            block = findBlockByHash(blockHash);
        }
    }

    private RPCBlock findLatestBlock() {
        RPCBlock block = null;
        Long blockCount;

        try {
            blockCount = jsonrpcClient.invoke(BTCMethods.GET_BLOCK_COUNT, new Object[] {}, Long.class);
            block = findBlockById(blockCount);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return block;
    }

    private RPCBlock findBlockById(Long blockId) {
        RPCBlock block = null;
        String blockHash = null;

        try {
            blockHash = jsonrpcClient.invoke(BTCMethods.GET_BLOCK_HASH, new Object[] { blockId }, String.class);
            block = findBlockByHash(blockHash);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return block;
    }

    private RPCBlock findBlockByHash(String blockHash) {
        RPCBlock block = null;

        try {
            block = jsonrpcClient.invoke(BTCMethods.GET_BLOCK, new Object[] { blockHash }, RPCBlock.class);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return block;
    }

    @Override
    public Double getAverageFee() {
        BlockchainTransaction transaction = null;
        Double runningSumFee = 0.0;
        Double fee;
        int transactionCount = 0;
        int numTransactions = block.tx.size();

        for (String transactionHash : block.tx) {
            transaction = new BlockchainTransaction(transactionHash);
            fee = transaction.getFee();
            transactionCount++;
            runningSumFee += fee;

            if (fee < 0) {
                System.out.println("Transaction: " + transactionHash + "; fee: " + Double.toString(fee));
            }
        }

        return runningSumFee / transactionCount;
    }
}