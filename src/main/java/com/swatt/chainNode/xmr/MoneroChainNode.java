package com.swatt.chainNode.xmr;

import java.util.logging.Logger;

import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.chainNode.btc.BitcoinChainNode;
import com.swatt.chainNode.dao.BlockData;
import com.swatt.util.OperationFailedException;

public class MoneroChainNode extends ChainNode {
    private static final Logger LOGGER = Logger.getLogger(BitcoinChainNode.class.getName());

    @Override
    public BlockData fetchBlockDataByHash(String blockHash) throws OperationFailedException {
        return null;
    }

    @Override
    public ChainNodeTransaction fetchTransactionByHash(String transactionHash, boolean calculateFee)
            throws OperationFailedException {
        return null;
    }
}