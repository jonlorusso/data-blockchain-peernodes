//package com.swatt.chainNode.btc;
//
//import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
//import com.swatt.blockchain.BlockchainNodeInfo;
//import com.swatt.blockchain.BlockchainTransaction;
//
//public class BlockchainNode extends com.swatt.blockchain.BlockchainNode {
//    private String blockchainTicker;
//    private JsonRpcHttpClient jsonrpcClient = null;
//
//    public BlockchainNode() {
//        this(null, null);
//    }
//
//    public BlockchainNode(JsonRpcHttpClient jsonrpcClient, String blockchainTicker) {
//        super(blockchainTicker);
//        this.jsonrpcClient = jsonrpcClient;
//
//        this.blockchainTicker = blockchainTicker;
//    }
//
//    @Override
//    public BlockchainNodeInfo getInfo() {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public BlockchainTransaction findTransactionByHash(String transactionHash) {
//        return findTransactionByHash(transactionHash, false);
//    }
//
//    @Override
//    public com.swatt.blockchain.BlockchainBlock findBlockByHash(String blockHash) {
//        BlockchainBlock block = new com.swatt.chainNode.btc.BlockchainBlock(jsonrpcClient, this, blockHash);
//        return block;
//    }
//
//    @Override
//    public BlockchainTransaction findTransactionByHash(String transactionHash, boolean calculate) {
//        BlockchainTransaction transaction = new com.swatt.blockchain.btc.BlockchainTransaction(jsonrpcClient,
//                transactionHash, calculate);
//
//        return transaction;
//    }
//
//    @Override
//    public String getTicker() {
//        return this.blockchainTicker;
//    }
//}
