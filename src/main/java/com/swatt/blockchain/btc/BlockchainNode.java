package com.swatt.blockchain.btc;

import com.swatt.blockchain.BlockchainNodeData;
import com.swatt.blockchain.BlockchainNodeInfo;
import com.swatt.blockchain.BlockchainTransaction;

public class BlockchainNode extends com.swatt.blockchain.BlockchainNode {
    public BlockchainNode() {
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
    public BlockchainTransaction findTransactionByHash(String transactionHash) {
        BlockchainTransaction transaction = new com.swatt.blockchain.btc.BlockchainTransaction(transactionHash);

        return transaction;
    }

    @Override
    public BlockchainBlock findBlockByHash(String blockHash) {
        BlockchainBlock block = new com.swatt.blockchain.btc.BlockchainBlock(blockHash);

        return block;
    }

    @Override
    public BlockchainTransaction findTransactionByAddress(String address) {
        // TODO Auto-generated method stub
        return null;
    }
}