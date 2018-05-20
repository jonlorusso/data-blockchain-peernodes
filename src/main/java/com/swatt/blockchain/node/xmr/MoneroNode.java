package com.swatt.blockchain.node.xmr;

import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.blockchain.NodeTransaction;
import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.node.Node;
import com.swatt.util.general.KeepNewestHash;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.json.JsonRpcHttpClientPool;

public class MoneroNode extends Node {
    private static final Logger LOGGER = Logger.getLogger(MoneroNode.class.getName());
    private static final int JSON_RPC_POOL = 10; // TODO: Should get from chainNodeConfig
    private static final int TRANSACTION_BUFFER_SIZE = 1000;
    private static final String RPC_URL_SUFFIX = "/json_rpc";
    private static KeepNewestHash transactions;
    private String url;

    public static final int POWX_ATOMIC_UNITS = 12;

    private JsonRpcHttpClientPool jsonRpcHttpClientPool;

    public MoneroNode() {
        transactions = new KeepNewestHash(TRANSACTION_BUFFER_SIZE);
    }

    @Override
    public void init() {
        String url = String.format("http://%s:%d", blockchainNodeInfo.getIp(), blockchainNodeInfo.getPort());
        jsonRpcHttpClientPool = new JsonRpcHttpClientPool(url + RPC_URL_SUFFIX, null, null, JSON_RPC_POOL);
    }

    public class RpcBlockHashCall {
        public String hash;
    }

    public class RpcBlockHeightCall {
        public long height;
    }

    @Override
    public BlockData fetchBlockDataByHash(String hash) throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        try {
            return fetchBlock(jsonRpcHttpClient, 0, hash, true);
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

    private BlockData fetchBlock(JsonRpcHttpClient jsonrpcClient, long blockHeight, String blockHash, boolean calculate) throws OperationFailedException {
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
            blockData.setScalingPowers(super.getDifficultyScaling(), super.getRewardScaling(), super.getFeeScaling(), super.getAmountScaling());
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
            blockData.setBlockchainCode(getBlockchainCode());

            if (calculate) {
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
    public NodeTransaction fetchTransactionByHash(String hash, boolean calculate)  throws OperationFailedException {
        try {
            return new MoneroTransaction(this, url, hash, true);
        } catch (Throwable t) {
            OperationFailedException e = new OperationFailedException("Error fetching Transaction: ", t);
            LOGGER.log(Level.SEVERE, e.toString(), e);
            throw e;
        }
    }

    private void calculate(JsonRpcHttpClient jsonrpcClient, BlockData blockData, RpcResultBlock rpcBlock) throws OperationFailedException {
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
        blockData.setAvgFeeBase(averageFee);
        blockData.setAvgFeeRateBase(averageFeeRate);

        blockData.setSmallestFeeBase(smallestFee);
        blockData.setLargestFeeBase(largestFee);

        blockData.setLargestTxAmountBase(largestTxAmount);
        blockData.setLargestTxHash(largestTxHash);
    }

	@Override
	public long fetchBlockCount() throws OperationFailedException {
	    throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public BlockData fetchBlockData(long blockNumber) throws OperationFailedException {
	    throw new UnsupportedOperationException("Not implemented yet.");
	}

    @Override
    public void fetchNewTransactions() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void fetchNewBlocks() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}