package com.swatt.blockchain.node;

import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.repository.BlockchainToken;
import com.swatt.util.general.OperationFailedException;

import java.util.List;

public abstract class PlatformNode extends Node {

    protected List<BlockchainToken> tokens;

    public PlatformNode() {
        super();
    }

    public void setTokens(List<BlockchainToken> tokens) {
        this.tokens = tokens;
    }

    public abstract List<BlockData> fetchTokenBlockDatas(long blockNumber) throws OperationFailedException;
}
