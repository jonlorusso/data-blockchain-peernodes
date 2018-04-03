package com.swatt.chainNode.btc;

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

public class BitcoinChainNode extends ChainNode {
    private static final Logger LOGGER = Logger.getLogger(BitcoinChainNode.class.getName());
    private static final double BITCOIN_BLOCK_REWARD_BTC = 12.5;
    private static final int TRANSACTION_BUFFER_SIZE = 1000;
    private static KeepNewestHash transactions;

    private JsonRpcHttpClientPool jsonRpcHttpClientPool;

    public BitcoinChainNode() {
        transactions = new KeepNewestHash(TRANSACTION_BUFFER_SIZE);
    }

    @Override
    public void init() {
        String url = chainNodeConfig.getURL();
        String user = chainNodeConfig.getRpcUser();
        String password = chainNodeConfig.getRpcPassword();
        int maxSize = 10; // TODO: Should get from chainNodeConfig

        jsonRpcHttpClientPool = new JsonRpcHttpClientPool(url, user, password, maxSize);
    }

    @Override
    public BlockData fetchBlockDataByHash(String blockHash) throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        try {
            if (blockHash == null) {
                return fetchLatestBlock(jsonRpcHttpClient);
            } else {
                return fetchBlockByHash(jsonRpcHttpClient, blockHash);
            }
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }

    @Override
    public ChainNodeTransaction fetchTransactionByHash(String transactionHash, boolean calculate)
            throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        try {
            BitcoinTransaction transaction = new BitcoinTransaction(jsonRpcHttpClient, transactionHash, calculate);
            return transaction;
        } catch (OperationFailedException e) {
            throw e;
        } catch (Throwable t) {
            OperationFailedException e = new OperationFailedException("Error fetching latest Block: ", t);
            LOGGER.log(Level.SEVERE, e.toString(), e);
            throw e;
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }

    private BlockData fetchLatestBlock(JsonRpcHttpClient jsonRpcHttpClient) throws OperationFailedException {
        long blockNumber = 0;

        try {
            Object parameters[] = new Object[] {};
            blockNumber = jsonRpcHttpClient.invoke(RpcMethodsBitcoin.GET_BLOCK_COUNT, parameters, Long.class);
        } catch (Throwable t) {
            OperationFailedException e = new OperationFailedException("Error fetching latest Block: ", t);
            LOGGER.log(Level.SEVERE, e.toString(), e);
            throw e;
        }

        return fetchBlockByBlockNumber(jsonRpcHttpClient, blockNumber); // We keep this out of the above try/catch so we
                                                                        // don't double catch exceptions on this call
    }

    private BlockData fetchBlockByBlockNumber(JsonRpcHttpClient jsonrpcClient, long blockNumber)
            throws OperationFailedException {
        String blockHash = null;

        try {
            Object parameters[] = new Object[] { new Long(blockNumber) };
            blockHash = jsonrpcClient.invoke(RpcMethodsBitcoin.GET_BLOCK_HASH, parameters, String.class);
        } catch (Throwable t) {
            OperationFailedException e = new OperationFailedException("Error fetching latest Block: ", t);
            LOGGER.log(Level.SEVERE, e.toString(), e);
            throw e;
        }

        return fetchBlockByHash(jsonrpcClient, blockHash); // We keep this out of the above try/catch so we don't double
                                                           // catch exceptions on this call
    }

    private BlockData fetchBlockByHash(JsonRpcHttpClient jsonrpcClient, String blockHash)
            throws OperationFailedException {

        try {
            long start = Instant.now().getEpochSecond();

            Object parameters[] = new Object[] { blockHash };
            RpcResultBlock rpcBlock = jsonrpcClient.invoke(RpcMethodsBitcoin.GET_BLOCK, parameters,
                    RpcResultBlock.class);

            BlockData blockData = new BlockData();
            blockData.setHash(rpcBlock.hash);
            blockData.setSize(rpcBlock.size);
            blockData.setHeight(rpcBlock.height);
            blockData.setVersionHex(rpcBlock.versionHex);
            blockData.setMerkleRoot(rpcBlock.merkleroot);
            blockData.setTimestamp(rpcBlock.time);
            blockData.setNonce(rpcBlock.nonce);
            blockData.setBits(rpcBlock.bits);
            blockData.setDifficulty(rpcBlock.difficulty);
            blockData.setPrevHash(rpcBlock.previousblockhash);
            blockData.setNextHash(rpcBlock.nextblockhash);

            blockData.setReward(BITCOIN_BLOCK_REWARD_BTC);

            blockData.setBlockchainCode(blockchainCode);

            System.out.println("CALCULATING BLOCK: " + rpcBlock.hash);

            calculate(jsonrpcClient, blockData, rpcBlock);

            long indexingDuration = Instant.now().getEpochSecond() - start;
            long now = Instant.now().toEpochMilli();

            blockData.setIndexed(now);
            blockData.setIndexingDuration(indexingDuration);

            return blockData;

        } catch (Throwable t) {
            if (t instanceof OperationFailedException)
                throw (OperationFailedException) t;
            else {
                OperationFailedException e = new OperationFailedException("Error fetching latest Block: ", t);
                LOGGER.log(Level.SEVERE, e.toString(), e);
                throw e;
            }
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

        int transactionCount = 1; // rpcBlock.tx.size();

        for (String transactionHash : rpcBlock.tx) {
            BitcoinTransaction transaction = (BitcoinTransaction) transactions.get(transactionHash);

            if (transaction == null) {
                transaction = new BitcoinTransaction(jsonrpcClient, transactionHash, true);
                transactions.put(transactionHash, transaction);
            }

            if (!transaction.isNewlyMinted()) {
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

                if (transactionFee <= 0.0) {
                    System.out.println("Initial transaction: " + transactionHash);
                }
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