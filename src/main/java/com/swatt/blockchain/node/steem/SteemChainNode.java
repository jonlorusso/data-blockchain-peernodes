package com.swatt.blockchain.node.steem;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.node.NodeTransaction;
import com.swatt.util.general.KeepNewestHash;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.json.JsonRpcHttpClientPool;

public class SteemChainNode extends Node {
    private static final Logger LOGGER = LoggerFactory.getLogger(SteemChainNode.class.getName());
    private static final int TRANSACTION_BUFFER_SIZE = 1000;
    private static KeepNewestHash transactions;

    private JsonRpcHttpClientPool jsonRpcHttpClientPool;

    public SteemChainNode() {
        transactions = new KeepNewestHash(TRANSACTION_BUFFER_SIZE);
    }

    @Override
    public void init() {
        String url = String.format("http://%s:%d", blockchainNodeInfo.getIp(), blockchainNodeInfo.getPort());
        int maxSize = 10; // TODO: Should get from chainNodeConfig

        jsonRpcHttpClientPool = new JsonRpcHttpClientPool(url, null, null, maxSize);
    }

    @Override
    public BlockData fetchBlockDataByHash(String hash) throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        try {
            return fetchBlockByHash(jsonRpcHttpClient, hash);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }
    
    @Override
    public NodeTransaction fetchTransactionByHash(String hash, boolean calculate) throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        try {
            return new SteemTransaction(jsonRpcHttpClient, hash, calculate);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }

    private BlockData fetchBlockByHash(JsonRpcHttpClient jsonrpcClient, String blockHash)
            throws OperationFailedException {

        try {
            long start = Instant.now().getEpochSecond();

            Object parameters[] = new Object[] { blockHash };
            RpcResultBlock rpcBlock = jsonrpcClient.invoke(RpcMethodsSteem.GET_BLOCK, parameters, RpcResultBlock.class);

            BlockData blockData = new BlockData();
            blockData.setScalingPowers(super.getDifficultyScaling(), super.getRewardScaling(), super.getFeeScaling(), super.getAmountScaling());
            blockData.setHash(rpcBlock.hash);
            blockData.setSize(rpcBlock.size);
            blockData.setHeight(rpcBlock.height);
            blockData.setVersionHex(rpcBlock.versionHex);
            blockData.setMerkleRoot(rpcBlock.merkleroot);
            blockData.setTimestamp(rpcBlock.time);
            blockData.setNonce(rpcBlock.nonce);
            blockData.setBits(rpcBlock.bits);
            blockData.setDifficultyBase(rpcBlock.difficulty);
            blockData.setPrevHash(rpcBlock.previousblockhash);
            blockData.setNextHash(rpcBlock.nextblockhash);
            blockData.setBlockchainCode(getBlockchainCode());

            calculate(jsonrpcClient, blockData, rpcBlock);

            long indexingDuration = Instant.now().getEpochSecond() - start;
            long now = Instant.now().toEpochMilli();

            blockData.setIndexed(now);
            blockData.setIndexingDuration(indexingDuration);

            return blockData;
        } catch (OperationFailedException e) {
            throw e;
        } catch (Throwable t) {
            LOGGER.error("Error fetching latest block: ", t.getMessage());
            throw new OperationFailedException("Error fetching latest Block: ", t);
        }
    }

    private void calculate(JsonRpcHttpClient jsonrpcClient, BlockData blockData, RpcResultBlock rpcBlock) throws OperationFailedException {
        double totalFee = 0.0;
        double totalFeeRate = 0.0;

        double largestFee = 0.0;
        double smallestFee = Double.MAX_VALUE;
        double largestTxAmount = 0.0;
        String largestTxHash = null;

        int transactionCount = 1; // rpcBlock.tx.size();

        for (String transactionHash : rpcBlock.tx) {
            SteemTransaction transaction = (SteemTransaction) transactions.get(transactionHash);

            if (transaction == null) {
                transaction = new SteemTransaction(jsonrpcClient, transactionHash, true);
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
