package com.swatt.chainNode.steem;

import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.chainNode.dao.BlockData;
import com.swatt.util.OperationFailedException;

public class SteemChainNode extends ChainNode {
    public SteemChainNode() {
    }

    @Override
    public void init() {
    }

    @Override
    public BlockData fetchBlockDataByHash(String blockHash) throws OperationFailedException {

        return null;
    }

    @Override
    public ChainNodeTransaction fetchTransactionByHash(String transactionHash, boolean calculate)
            throws OperationFailedException {

        return null;
    }
}
