package com.swatt.chainNode.xmr;

import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.chainNode.dao.BlockData;
import com.swatt.util.general.KeepNewestHash;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.json.JsonRpcHttpClientPool;

public class MoneroChainNode extends ChainNode {
    private static final Logger LOGGER = Logger.getLogger(MoneroChainNode.class.getName());
    private static final int JSON_RPC_POOL = 10; // TODO: Should get from chainNodeConfig
    private static final int TRANSACTION_BUFFER_SIZE = 1000;
    private static final String RPC_URL_SUFFIX = "/json_rpc";
    private static KeepNewestHash transactions;
    private String url;

    public static final int POWX_ATOMIC_UNITS = 12;

    private JsonRpcHttpClientPool jsonRpcHttpClientPool;

    public MoneroChainNode() {
        transactions = new KeepNewestHash(TRANSACTION_BUFFER_SIZE);
    }

    @Override
    public void init() {
        url = chainNodeConfig.getURL();
        jsonRpcHttpClientPool = new JsonRpcHttpClientPool(url + RPC_URL_SUFFIX, null, null, JSON_RPC_POOL);
    }

    public class RpcBlockHashCall {
        public String hash;
    }

    public class RpcBlockHeightCall {
        public long height;
    }

    @Override
    public BlockData fetchBlockDataByHash(String blockHash) throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        try {
            return fetchBlock(jsonRpcHttpClient, 0, blockHash, true);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }

    public BlockData fetchBlockDataByHeight(long blockHeight, boolean calculate) throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        try {
            return fetchBlock(jsonRpcHttpClient, blockHeight, null, calculate);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }

    private BlockData fetchBlock(JsonRpcHttpClient jsonrpcClient, long blockHeight, String blockHash, boolean calculate)
            throws OperationFailedException {

        try {
            long start = Instant.now().getEpochSecond();

            RpcResultBlock rpcBlock = null;

            if (blockHash != null) {
                RpcBlockHashCall blockCallHash = new RpcBlockHashCall();
                blockCallHash.hash = blockHash;

                rpcBlock = jsonrpcClient.invoke(RpcMethodsMonero.GET_BLOCK, blockCallHash, RpcResultBlock.class);
            } else {
                RpcBlockHeightCall blockCallHeight = new RpcBlockHeightCall();
                blockCallHeight.height = blockHeight;

                rpcBlock = jsonrpcClient.invoke(RpcMethodsMonero.GET_BLOCK, blockCallHeight, RpcResultBlock.class);
            }

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

            if (calculate) {
                System.out.println("CALCULATING BLOCK: " + rpcBlock.block_header.hash);
                calculate(jsonrpcClient, blockData, rpcBlock);
            }

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
    public ChainNodeTransaction fetchTransactionByHash(String transactionHash, boolean calculate)
            throws OperationFailedException {

        try {
            MoneroTransaction transaction = new MoneroTransaction(this, url, transactionHash, true);
            return transaction;
        } catch (Throwable t) {
            OperationFailedException e = new OperationFailedException("Error fetching Transaction: ", t);
            LOGGER.log(Level.SEVERE, e.toString(), e);
            throw e;
        }
    }

    private void calculate(JsonRpcHttpClient jsonrpcClient, BlockData blockData, RpcResultBlock rpcBlock)
            throws OperationFailedException {
        double totalFee = 0.0;
        double totalFeeRate = 0.0;

        double largestFee = 0.0;
        double smallestFee = Double.MAX_VALUE;
        double largestTxAmount = 0.0;
        String largestTxHash = null;

        int transactionCount = 0;

        for (String transactionHash : rpcBlock.tx_hashes) {
            MoneroTransaction transaction = (MoneroTransaction) transactions.get(transactionHash);

            if (transaction == null) {
                transaction = new MoneroTransaction(this, url, transactionHash, false);
                transactions.put(transactionHash, transaction);
            }

            double transactionFee = transaction.getFee();
            double transactionAmount = transaction.getAmount();

            largestFee = Math.max(largestFee, transactionFee);
            smallestFee = Math.min(smallestFee, transactionFee);

            transactionCount++;
            totalFee += transactionFee;
            totalFeeRate += transaction.getFeeRate();

            if (transactionAmount > largestTxAmount) {
                largestTxAmount = transactionAmount;
                largestTxHash = transactionHash;
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