package com.swatt.chainNode.xmr;

import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.chainNode.dao.BlockData;
import com.swatt.util.JsonRpcHttpClientPool;
import com.swatt.util.OperationFailedException;
import com.swatt.util.general.KeepNewestHash;

public class MoneroChainNode extends ChainNode {
    private static final Logger LOGGER = Logger.getLogger(MoneroChainNode.class.getName());
    private static final int POWX_ATOMIC_UNITS = 12;
    private static final int TRANSACTION_BUFFER_SIZE = 1000;
    private static final String RPC_URL_SUFFIX = "/json_rpc";
    private static KeepNewestHash transactions;
    private String url;

    private JsonRpcHttpClientPool jsonRpcHttpClientPool;

    public MoneroChainNode() {
        transactions = new KeepNewestHash(TRANSACTION_BUFFER_SIZE);
    }

    @Override
    public void init() {
        url = chainNodeConfig.getURL();
        System.out.println(url);

        int maxSize = 10; // TODO: Should get from chainNodeConfig
        // chainNodeManagerConfig.getIntAttribute("jsonPoolSize ", 10);

        jsonRpcHttpClientPool = new JsonRpcHttpClientPool(url + RPC_URL_SUFFIX, null, null, maxSize);
    }

    public class RPCBlockCall {
        public String hash;
    }

    @Override
    public BlockData fetchBlockDataByHash(String blockHash) throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        try {
            return fetchBlockByHash(jsonRpcHttpClient, blockHash);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }

    private BlockData fetchBlockByHash(JsonRpcHttpClient jsonrpcClient, String blockHash)
            throws OperationFailedException {

        try {
            long start = Instant.now().getEpochSecond();

            RPCBlockCall blockCall = new RPCBlockCall();
            blockCall.hash = blockHash;

            RPCBlock rpcBlock = jsonrpcClient.invoke(XMRMethods.GET_BLOCK, blockCall, RPCBlock.class);

            BlockData blockData = new BlockData();

            blockData.setHash(rpcBlock.block_header.hash);
            blockData.setSize(rpcBlock.block_header.block_size);
            blockData.setHeight(rpcBlock.block_header.height);
            blockData.setTimestamp(rpcBlock.block_header.timestamp);
            blockData.setNonce(rpcBlock.block_header.nonce);
            blockData.setDifficulty(rpcBlock.block_header.difficulty);
            blockData.setPrevHash(rpcBlock.block_header.prev_hash);

            // VERSION
            int versionMajor = rpcBlock.block_header.major_version;
            int versionMinor = rpcBlock.block_header.minor_version;
            int version = (versionMajor * 10000) + versionMinor;
            String versionHex = Integer.toHexString(version);
            blockData.setVersionHex(versionHex);

            blockData.setReward(rpcBlock.block_header.reward);
            blockData.setTransactionCount(rpcBlock.block_header.num_txes);
            blockData.setBlockchainCode(blockchainCode);

            System.out.println("CALCULATING BLOCK: " + rpcBlock.block_header.hash);

            calculate(jsonrpcClient, blockData, rpcBlock);

            long indexingDuration = Instant.now().getEpochSecond() - start;
            long now = Instant.now().toEpochMilli();

            blockData.setIndexed(now);
            blockData.setIndexingDuration(indexingDuration);

            return blockData;

        } catch (Throwable t) {
            t.printStackTrace();
            if (t instanceof OperationFailedException)
                throw (OperationFailedException) t;
            else {
                OperationFailedException e = new OperationFailedException("Error fetching Block: ", t);
                LOGGER.log(Level.SEVERE, e.toString(), e);
                throw e;
            }
        }
    }

    @Override
    public ChainNodeTransaction fetchTransactionByHash(String transactionHash, boolean calculateFee)
            throws OperationFailedException {

        try {
            MoneroTransaction transaction = new MoneroTransaction(url, transactionHash);
            return transaction;
        } catch (Throwable t) {
            OperationFailedException e = new OperationFailedException("Error fetching Transaction: ", t);
            LOGGER.log(Level.SEVERE, e.toString(), e);
            throw e;
        }
    }

    private void calculate(JsonRpcHttpClient jsonrpcClient, BlockData blockData, RPCBlock rpcBlock)
            throws OperationFailedException {
        double totalFee = 0.0;
        double totalFeeRate = 0.0;

        double largestFee = 0.0;
        double smallestFee = Double.MAX_VALUE;
        double largestTxAmount = 0.0;
        String largestTxHash = null;

        int transactionCount = 1;

        for (String transactionHash : rpcBlock.tx_hashes) {
            System.out.println(transactionHash);
            MoneroTransaction transaction = (MoneroTransaction) transactions.get(transactionHash);

            if (transaction == null) {
                System.out.println("Let's at least try and fetch");
                transaction = new MoneroTransaction(url, transactionHash);
                transactions.put(transactionHash, transaction);
            }
        }

        if (smallestFee == Double.MAX_VALUE)
            smallestFee = 0;

        double averageFee = totalFee / transactionCount;
        double averageFeeRate = totalFeeRate / transactionCount;

        blockData.setTransactionCount(transactionCount);
        blockData.setAvgFee(averageFee);
        blockData.setAvgFeeRate(averageFeeRate);

        blockData.setSmallestFee(smallestFee);
        blockData.setLargestFee(largestFee);

        blockData.setLargestTxAmount(largestTxAmount);
        blockData.setLargestTxHash(largestTxHash);
    }
}