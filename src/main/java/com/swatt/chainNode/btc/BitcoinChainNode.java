package com.swatt.chainNode.btc;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.Transaction;
import com.swatt.chainNode.dao.BlockData;
import com.swatt.util.JsonRpcHttpClientPool;
import com.swatt.util.OperationFailedException;

public class BitcoinChainNode extends ChainNode {
    private static final String CODE = "btc";
    private static final Logger LOGGER = Logger.getLogger(BitcoinChainNode.class.getName());

    public static final String DEFAULT_BLOCKCHAIN_URL = "http://127.0.0.1"; // TODO: Should get from chainNodeConfig

    private JsonRpcHttpClientPool jsonRpcHttpClientPool;

    public BitcoinChainNode() {
    }

    @Override
    public void init() {
        // String url = chainNodeConfig.getAttribute("xxx", null); // TODO: Should get
        // from chainNodeConfig
        // String user = chainNodeConfig.getAttribute("xxx", null); // TODO: Should get
        // from chainNodeConfig
        // String password = chainNodeConfig.getAttribute("xxx", null); // TODO: Should
        // get from chainNodeConfig
        // int maxSize = chainNodeConfig.getAttribute("xxx", null); // TODO: Should get
        // from chainNodeConfig

        String url = "http://127.0.0.1:8332"; // TODO: Should get from chainNodeConfig
        String user = "hello"; // TODO: Should get from chainNodeConfig
        String password = "letstest"; // TODO: Should get from chainNodeConfig
        int maxSize = 10; // TODO: Should get from chainNodeConfig

        jsonRpcHttpClientPool = new JsonRpcHttpClientPool(url, user, password, maxSize);

        System.out.println("In BitcoinChainNode.init: " + jsonRpcHttpClientPool);
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
    public Transaction fetchTransactionByHash(String transactionHash, boolean calculateFee)
            throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        try {
            BitcoinTransaction transaction = new BitcoinTransaction(jsonRpcHttpClient, transactionHash, calculateFee);
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
            blockNumber = jsonRpcHttpClient.invoke(BTCMethods.GET_BLOCK_COUNT, parameters, Long.class);
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
            blockHash = jsonrpcClient.invoke(BTCMethods.GET_BLOCK_HASH, parameters, String.class);
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
            Object parameters[] = new Object[] { blockHash };
            RPCBlock rpcBlock = jsonrpcClient.invoke(BTCMethods.GET_BLOCK, parameters, RPCBlock.class);

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

            // FIXME: I see the following fields Are NOT used from the RpcBlock
            // int confirmations;
            // int strippedsize;
            // int weight;
            // int version;
            // int mediantime;
            // String chainwork;

            blockData.setChainName(chainName);

            calculate(jsonrpcClient, blockData, rpcBlock);

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

    private void calculate(JsonRpcHttpClient jsonrpcClient, BlockData blockData, RPCBlock rpcBlock)
            throws OperationFailedException {

        long totalSize = 0; // Will this ever exceed a 32 bit int?
        double totalFee = 0.0;
        double totalFeeRate = 0.0;

        double largestFee = 0.0;
        double smallestFee = Double.MAX_VALUE;
        double largestTxAmount = 0.0;
        String largestTxHash = null;

        int transactionCount = 0; // block.tx.size();

        for (String transactionHash : rpcBlock.tx) {
            BitcoinTransaction transaction = new BitcoinTransaction(jsonrpcClient, transactionHash, true);

            if (!transaction.isMinted()) {
                double transactionFee = transaction.getFee();
                double transactionAmount = transaction.getAmount();

                smallestFee = Math.min(smallestFee, transactionFee);
                largestFee = Math.max(largestFee, transactionFee);

                transactionCount++;
                totalFee += transactionFee;
                totalFeeRate += transaction.getFeeRate();
                totalSize += transaction.getSize();

                if (transactionAmount > largestTxAmount) {
                    largestTxAmount = transactionAmount;
                    largestTxHash = transactionHash;
                }

                if (transactionFee <= 0.0) {
                    System.out.println("Initial transaction: " + transactionHash);
                }
            }
        }

        double averageFee = totalFee / transactionCount;
        double averageFeeRate = totalFeeRate / transactionCount;

        blockData.setTransactionCount(transactionCount);
        blockData.setAvgFee(averageFee);
        blockData.setAvgFeeRate(averageFeeRate);
        blockData.setTotalSize(totalSize);

        blockData.setTotalFee(totalFee);

        blockData.setLargestFee(largestFee);
        blockData.setLargestTxAmount(largestTxAmount);
        blockData.setLargestTxHash(largestTxHash);
    }

}
