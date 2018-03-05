package com.swatt.blockchain.btc;

import java.util.HashMap;
import java.util.Map;

import com.swatt.blockchain.BlockchainNodeData;
import com.swatt.blockchain.BlockchainNodeInfo;
import com.swatt.blockchain.BlockchainTransaction;

public class BlockchainNode extends com.swatt.blockchain.BlockchainNode {
    private Map<String, BlockchainTransaction> transactionMap;

    public BlockchainNode() {
        transactionMap = new HashMap<String, BlockchainTransaction>();
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
        BlockchainTransaction transaction = transactionMap.get(transactionHash);

        if (transaction == null) {
            transaction = new com.swatt.blockchain.btc.BlockchainTransaction(transactionHash);
            transactionMap.put(transactionHash, transaction);
        } else {
            System.out.println("Resued one! " + transactionHash);
        }

        return transaction;
    }

    @Override
    public BlockchainTransaction findTransactionByAddress(String address) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public com.swatt.blockchain.BlockchainBlock findBlockByHash(String blockHash) {
        BlockchainBlock block = new com.swatt.blockchain.btc.BlockchainBlock(this, blockHash);
        return block;
    }
}