package com.swatt.chainNode.btc;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.chainNode.dao.BlockData;
import com.swatt.util.general.KeepNewestHash;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.json.JsonRpcHttpClientPool;

public class BitcoinChainNode extends ChainNode {
    private static final Logger LOGGER = LoggerFactory.getLogger(BitcoinChainNode.class.getName());
    
    private final static char[] hexArray = "0123456789abcdef".toCharArray();

    private static final double BITCOIN_BLOCK_REWARD_BTC = 12.5;
    private static final int TRANSACTION_BUFFER_SIZE = 1000;
    private static KeepNewestHash transactions;

    private JsonRpcHttpClientPool jsonRpcHttpClientPool;
    private Socket blockSubscriber;
    
    public BitcoinChainNode() {
        transactions = new KeepNewestHash(TRANSACTION_BUFFER_SIZE);
    }

    @Override
    public void init() {
        String url = String.format("http://%s:%d", blockchainNodeInfo.getIp(), blockchainNodeInfo.getPort());
        String user = blockchainNodeInfo.getRpcUn();
        String password = blockchainNodeInfo.getRpcPw();
        int maxSize = 10; // TODO: Should get from chainNodeConfig

        Context context = ZMQ.context(1);
        blockSubscriber = context.socket(ZMQ.SUB);
        blockSubscriber.connect(String.format("tcp://%s:28335", blockchainNodeInfo.getIp())); // FIXME store port in blockchain_node_info?
        blockSubscriber.subscribe("hashblock");
        
        jsonRpcHttpClientPool = new JsonRpcHttpClientPool(url, user, password, maxSize);
    }

    @Override
    public BlockData fetchBlockDataByHash(String blockHash) throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();
        
        try {
            return blockHash == null ? fetchLatestBlock(jsonRpcHttpClient) : fetchBlockByHash(jsonRpcHttpClient, blockHash);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }

    @Override
    public ChainNodeTransaction fetchTransactionByHash(String transactionHash, boolean calculate) throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        try {
            RpcResultTransaction rpcTranaction = BitcoinTransaction.fetchFromBlockchain(jsonRpcHttpClient, transactionHash);
            return new BitcoinTransaction(jsonRpcHttpClient, rpcTranaction, calculate);
        } catch (OperationFailedException e) {
            throw e;
        } catch (Throwable t) {
            LOGGER.error(String.format("[BTC] Exception caught fetching transaction: [%s]", t.getMessage()));
            throw new OperationFailedException("Error fetching latest Block: ", t);
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
            LOGGER.error(String.format("[BTC] Exception caught fetching block: [%s]", t.getMessage()));
            throw new OperationFailedException("Error fetching latest Block: ", t);
        }

        return fetchBlockByBlockNumber(jsonRpcHttpClient, blockNumber); // We keep this out of the above try/catch so we
                                                                        // don't double catch exceptions on this call
    }

    private BlockData fetchBlockByBlockNumber(JsonRpcHttpClient jsonrpcClient, long blockNumber) throws OperationFailedException {
        String blockHash = null;

        try {
            blockHash = jsonrpcClient.invoke(RpcMethodsBitcoin.GET_BLOCK_HASH, new Object[] { blockNumber }, String.class);
        } catch (Throwable t) {
            LOGGER.error(String.format("[BTC] Exception caught fetching block: [%s]", t.getMessage()));
            throw new OperationFailedException("Error fetching latest Block: ", t);
        }

        // We keep this out of the above try/catch so we don't double catch exceptions on this call
        return fetchBlockByHash(jsonrpcClient, blockHash); 
    }

    private BlockData fetchBlockByHash(JsonRpcHttpClient jsonrpcClient, String blockHash) throws OperationFailedException {
        try {
            long start = Instant.now().getEpochSecond();

            RpcResultBlock rpcBlock = jsonrpcClient.invoke(RpcMethodsBitcoin.GET_BLOCK, new Object[] { blockHash, 2 }, RpcResultBlock.class);

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

            blockData.setRewardBase(BITCOIN_BLOCK_REWARD_BTC);

            blockData.setBlockchainCode(blockchainNodeInfo.getCode());

//            LOGGER.info("Calculating block: " + rpcBlock.hash);

            calculate(jsonrpcClient, blockData, rpcBlock);

            long indexingDuration = Instant.now().getEpochSecond() - start;
            long now = Instant.now().toEpochMilli();

            blockData.setIndexed(now);
            blockData.setIndexingDuration(indexingDuration);

            return blockData;
        } catch (OperationFailedException e) {
            throw e;
        } catch (Throwable t) {
            LOGGER.error(String.format("[BTC] Exception caught fetching block: [%s]", t.getMessage()));
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
        for (RpcResultTransaction rpcTransaction : rpcBlock.tx) {
            BitcoinTransaction transaction = (BitcoinTransaction)transactions.get(rpcTransaction.txid);

            if (transaction == null) {
                transaction = new BitcoinTransaction(jsonrpcClient, rpcTransaction, true);
                transactions.put(rpcTransaction.txid, transaction);
            }

            if (!transaction.getCoinbase()) {
                double transactionFee = transaction.getFee();
                double transactionAmount = transaction.getAmount();

                largestFee = Math.max(largestFee, transactionFee);
                smallestFee = Math.min(smallestFee, transactionFee);

                transactionCount++;
                totalFee += transactionFee;
                totalFeeRate += transaction.getFeeRate();

                if (transactionAmount > largestTxAmount) {
                    largestTxAmount = transactionAmount;
                    largestTxHash = rpcTransaction.txid;
                }
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
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        try {
            return jsonRpcHttpClient.invoke(RpcMethodsBitcoin.GET_BLOCK_COUNT, new Object[] {}, Long.class);
        } catch (Throwable t) {
            throw new OperationFailedException("Error fetching latest Block: ", t);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }

    @Override
    public BlockData fetchBlockData(long blockNumber) throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        try {
            return fetchBlockByBlockNumber(jsonRpcHttpClient, blockNumber);
        } catch (Throwable t) {
            throw new OperationFailedException("Error fetching block: ", t);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }

    public static String hexlify(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    private void handleIncomingRawBlock(ZMsg message) throws OperationFailedException {
        BlockData blockData = fetchBlockDataByHash(hexlify(message.pop().getData()));
        chainNodeListeners.stream().forEach(t -> t.newBlockAvailable(this, blockData));
    }

    @Override
    public void fetchNewTransactions() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
    
    @Override
    public void fetchNewBlocks() {
        Thread blockListener = new Thread(() -> {
            LOGGER.info("Starting fetchNewBlocks thread.");
            
            while (true) {
                ZMsg message = ZMsg.recvMsg(blockSubscriber, 0);
                String topic = message.popString();
                
                if (topic.equals("hashblock")) {
                    try {
                        handleIncomingRawBlock(message);
                    } catch (OperationFailedException e) {
                        LOGGER.error("Exception caught processing new block: " + e.getMessage());
                    }
                }
            }

        }, "BlockListener-BTC");

        blockListener.start();
    }
}
