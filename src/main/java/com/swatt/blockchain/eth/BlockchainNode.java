package com.swatt.blockchain.eth;

import com.swatt.blockchain.BlockchainBlock;
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
    public BlockchainBlock findBlockByHash(String blockHash) {
        // TODO Auto-generated method stub
        return null;
    }
}