package com.swatt.blockchain.node;

import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.entity.BlockchainNodeInfo;
import com.swatt.util.general.OperationFailedException;

import java.util.List;

public abstract class PlatformNode extends Node {

    protected List<BlockchainNodeInfo> tokens;

    public PlatformNode() {
        super();
    }

    public void setTokens(List<BlockchainNodeInfo> tokens) {
        this.tokens = tokens;
    }

    public abstract List<BlockData> fetchTokenBlockDatas(long blockNumber) throws OperationFailedException;
}
