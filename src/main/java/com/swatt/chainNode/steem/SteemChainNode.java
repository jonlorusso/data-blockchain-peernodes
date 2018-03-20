package com.swatt.chainNode.steem;

import java.util.logging.Logger;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.chainNode.dao.BlockData;
import com.swatt.util.JsonRpcHttpClientPool;
import com.swatt.util.OperationFailedException;
import com.swatt.util.general.KeepNewestHash;

public class SteemChainNode extends ChainNode {
    private static final Logger LOGGER = Logger.getLogger(SteemChainNode.class.getName());
    private static final double BITCOIN_BLOCK_REWARD_BTC = 12.5;
    private static KeepNewestHash transactions;

    private JsonRpcHttpClientPool jsonRpcHttpClientPool;

    public SteemChainNode() {
    }

    @Override
    public void init() {
        String url = chainNodeConfig.getURL();
        int maxSize = 10; // TODO: Should get from chainNodeConfig

        jsonRpcHttpClientPool = new JsonRpcHttpClientPool(url, maxSize);
    }

    @Override
    public BlockData fetchBlockDataByHash(String blockHash) throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        return null;
    }

    @Override
    public ChainNodeTransaction fetchTransactionByHash(String transactionHash, boolean calculateFee)
            throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        return null;
    }
}
