package com.swatt.chainNode.btc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.zeromq.ZFrame;
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
    private static final Logger LOGGER = Logger.getLogger(BitcoinChainNode.class.getName());
    private static final String GENESIS_BLOCK_HASH = "000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f";
    private static final double BITCOIN_BLOCK_REWARD_BTC = 12.5;
    private static final int TRANSACTION_BUFFER_SIZE = 1000;
    private static KeepNewestHash transactions;

    private JsonRpcHttpClientPool jsonRpcHttpClientPool;
    private Context context;

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
            RpcResultTransaction rpcTranaction = BitcoinTransaction.fetchFromBlockchain(jsonRpcHttpClient,
                    transactionHash);
            BitcoinTransaction transaction = new BitcoinTransaction(jsonRpcHttpClient, rpcTranaction, calculate);
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

            Object parameters[] = new Object[] { blockHash, 2 };
            RpcResultBlock rpcBlock = jsonrpcClient.invoke(RpcMethodsBitcoin.GET_BLOCK, parameters,
                    RpcResultBlock.class);

            BlockData blockData = new BlockData();

            blockData.setScalingPowers(super.getDifficultyScaling(), super.getRewardScaling(), super.getFeeScaling(),
                    super.getAmountScaling());

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
        for (RpcResultTransaction rpcTransaction : rpcBlock.tx) {
            BitcoinTransaction transaction = (BitcoinTransaction) transactions.get(rpcTransaction.txid);

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

                if (transactionFee <= 0.0) {
                    System.out.println("Initial transaction: " + rpcTransaction.txid);
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

    @Override
    public String getGenesisHash() {
        return GENESIS_BLOCK_HASH;
    }

    protected final static char[] hexArray = "0123456789abcdef".toCharArray();

    public static String hexlify(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private int unpackBinary(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(bytes[0]);
        byteBuffer.put(bytes[1]);
        byteBuffer.put(bytes[2]);
        byteBuffer.put(bytes[3]);
        return byteBuffer.getInt(0);
    }

    // FIXME this method is handling hashtx at the moment
    private void handleIncomingRawBlock(ZMsg message) {
        ZFrame body = message.pop();
        ZFrame last = message.getLast();

        int sequenceNumber = 0;

        byte[] sequenceBytes = last.getData();
        if (sequenceBytes.length == 4) {
            sequenceNumber = unpackBinary(sequenceBytes);
        }

        String transactionHash = hexlify(body.getData());
        System.out.println(String.format("hashtx (%d) : %s", sequenceNumber, transactionHash));

        try {
            ChainNodeTransaction chainNodeTransaction = fetchTransactionByHash(transactionHash, true);
            chainNodeListeners.stream().forEach(
                    t -> t.newTransactionsAvailable(this, new ChainNodeTransaction[] { chainNodeTransaction }));
        } catch (OperationFailedException e) {
            e.printStackTrace();
        }
    }

    // FIXME this method is handling hashtx at the moment
    private void handleIncomingRawTransaction(ZMsg message) {
        ZFrame body = message.pop();
        ZFrame last = message.getLast();

        int sequenceNumber = 0;

        byte[] sequenceBytes = last.getData();
        if (sequenceBytes.length == 4) {
            sequenceNumber = unpackBinary(sequenceBytes);
        }

        String transactionHash = hexlify(body.getData());
        System.out.println(String.format("hashtx (%d) : %s", sequenceNumber, transactionHash));

        try {
            ChainNodeTransaction chainNodeTransaction = fetchTransactionByHash(transactionHash, true);
            chainNodeListeners.stream().forEach(
                    t -> t.newTransactionsAvailable(this, new ChainNodeTransaction[] { chainNodeTransaction }));
        } catch (OperationFailedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fetchNewTransactions() {
        if (context == null)
            context = ZMQ.context(1);

        Socket subscriber = context.socket(ZMQ.SUB);
        subscriber.connect("tcp://127.0.0.1:28332"); // FIXME store port in blockchain_node_info?
        subscriber.subscribe("rawtransaction");

        Thread transactionListener = new Thread(() -> {
            while (true) {
                ZMsg message = ZMsg.recvMsg(subscriber, 0);
                String topic = message.popString();

                if (topic.equals("rawtx"))
                    handleIncomingRawTransaction(message);
            }

        }, "TransactionListener-" + getCode());

        transactionListener.start();
    }

    @Override
    public void fetchNewBlocks() {
        if (context == null)
            context = ZMQ.context(1);

        Socket subscriber = context.socket(ZMQ.SUB);
        subscriber.connect("tcp://127.0.0.1:28332"); // FIXME store port in blockchain_node_info?
        subscriber.subscribe("rawblock");

        Thread blockListener = new Thread(() -> {
            while (true) {
                ZMsg message = ZMsg.recvMsg(subscriber, 0);
                String topic = message.popString();

                if (topic.equals("rawblock"))
                    handleIncomingRawBlock(message);
            }

        }, "BlockListener-" + getCode());

        blockListener.start();
    }
}
