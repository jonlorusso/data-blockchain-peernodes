package com.swatt.blockchain.eth;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.blockchain.BlockchainBlock;
import com.swatt.blockchain.BlockchainNodeInfo;
import com.swatt.blockchain.BlockchainTransaction;

public class BlockchainNode extends com.swatt.blockchain.BlockchainNode {
    private String blockchainTicker;
    private JsonRpcHttpClient jsonrpcClient = null;

    public BlockchainNode() {
        this(null, null);
    }

    public BlockchainNode(JsonRpcHttpClient jsonrpcClient, String blockchainTicker) {
        super(blockchainTicker);
        this.jsonrpcClient = jsonrpcClient;

        this.blockchainTicker = blockchainTicker;
    }

    @Override
    public BlockchainNodeInfo getInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockchainTransaction findTransactionByHash(String transactionHash) {
        return findTransactionByHash(transactionHash, false);
    }

    @Override
    public BlockchainTransaction findTransactionByHash(String transactionHash, boolean calculate) {
        BlockchainTransaction transaction = new com.swatt.blockchain.eth.BlockchainTransaction(jsonrpcClient,
                transactionHash, calculate);

        return transaction;
    }

    @Override
    public String getTicker() {
        return this.blockchainTicker;
    }

    @Override
    public BlockchainBlock findBlockByHash(String blockHash) {
        // TODO Auto-generated method stub
        return null;
    }
}