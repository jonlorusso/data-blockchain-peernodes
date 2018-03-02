package com.swatt.blockchain.btc;

import java.net.URL;

import com.swatt.blockchain.BlockchainNodeData;
import com.swatt.blockchain.BlockchainNodeInfo;
import com.swatt.blockchain.BlockchainTransaction;

public class BlockchainNode extends com.swatt.blockchain.BlockchainNode {
    private URL uri;

    public BlockchainNode() {
    }

    @Override
    public String getLatestBlockTransactions() {
        BlockchainBlock block = new BlockchainBlock();
        String transactions = block.getTransactionHashes();

        return transactions;
    }

    @Override
    public String getBlockTransactionsById(Long blockId) {
        BlockchainBlock block = new BlockchainBlock(blockId);
        String transactions = block.getTransactionHashes();

        return transactions;
    }

    @Override
    public String getBlockTransactionsByHash(String blockHash) {
        BlockchainBlock block = new BlockchainBlock(blockHash);
        String transactions = block.getTransactionHashes();

        return transactions;
    }

    @Override
    public BlockchainNodeInfo getInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockchainNodeData getDataForInterval(long fromTime, long toTime) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockchainTransaction findTransactionByHash(String hash) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockchainTransaction findTransactionByAddress(String address) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTransaction(String transactionHash) {
        BlockchainTransaction transaction = new com.swatt.blockchain.btc.BlockchainTransaction(transactionHash, null,
                null, null);

        return transaction.getHash();
    }
}