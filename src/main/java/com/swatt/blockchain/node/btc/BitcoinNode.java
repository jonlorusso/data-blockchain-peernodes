package com.swatt.blockchain.node.btc;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.node.JsonRpcHttpClientNode;
import com.swatt.blockchain.node.NodeTransaction;
import com.swatt.util.general.ConcurrencyUtilities;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.json.JsonRpcHttpClientPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;

import java.util.stream.Stream;

import static com.swatt.util.general.CollectionsUtilities.isNullOrEmpty;
import static java.lang.String.format;

public class BitcoinNode<S, T> extends JsonRpcHttpClientNode<RpcResultBlock, RpcResultTransaction> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BitcoinNode.class.getName());

    private final static char[] hexArray = "0123456789abcdef".toCharArray();

    private static final double BITCOIN_BLOCK_REWARD_BTC = 12.5;

    private Socket blockSubscriber;

    private boolean running = false;

    public BitcoinNode() {
        super();
    }

    @Override
    public void init() {
        String url = format("http://%s:%d", blockchainNodeInfo.getIp(), blockchainNodeInfo.getPort());
        String user = blockchainNodeInfo.getRpcUn();
        String password = blockchainNodeInfo.getRpcPw();
        int maxSize = 10; // TODO: Should get from nodeConfig

        Context context = ZMQ.context(1);
        blockSubscriber = context.socket(ZMQ.SUB);
        blockSubscriber.connect(format("tcp://%s:%d", blockchainNodeInfo.getIp(), blockchainNodeInfo.getZmqPort()));
        blockSubscriber.subscribe("hashblock");

        jsonRpcHttpClientPool = new JsonRpcHttpClientPool(url, user, password, maxSize);
    }

    @Override
    protected Object[] getBlockByHashRpcMethodParameters(String hash) throws OperationFailedException {
        return new Object[] { hash, true };
    }

    @Override
    protected Object[] getTransactionRpcMethodParameters(String hash) throws OperationFailedException {
        return new Object[] { hash, true };
    }

    private synchronized BlockData processTransaction(RpcResultTransaction rpcResultTransaction, BlockData blockData) {
        try {
            int transactionCount = blockData.getTransactionCount();

            NodeTransaction nodeTransaction = toNodeTransaction(rpcResultTransaction);
            double transactionFee = nodeTransaction.getFee();
            double transactionAmount = nodeTransaction.getAmount();

            BitcoinTransaction bitcoinTransaction = (BitcoinTransaction)nodeTransaction;
            if (!bitcoinTransaction.getCoinbase()) {
                blockData.setSmallestFeeBase(Math.min(blockData.getSmallestFee(), transactionFee));
                blockData.setLargestFeeBase(Math.max(blockData.getLargestFee(), transactionFee));
                blockData.setAvgFeeBase((blockData.getAvgFee() * transactionCount + transactionFee) / (transactionCount + 1));

                double transactionFeeRate = nodeTransaction.getFeeRate();
                blockData.setAvgFeeRateBase((blockData.getAvgFeeRate() * transactionCount + transactionFeeRate) / (transactionCount + 1));

                if (transactionAmount > blockData.getLargestTxAmount()) {
                    blockData.setLargestTxHash(nodeTransaction.getHash());
                    blockData.setLargestTxAmountBase(transactionAmount);
                }

                blockData.setTransactionCount(blockData.getTransactionCount() + 1);
            }


            return blockData;
        } catch (OperationFailedException e) {
            throw new RuntimeException(e);
        }
    }

    protected Object[] getBlockCountRpcMethodParameters() {
        return new Object[] {};
    }

    @Override
    public long fetchBlockCount() throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        try {
            return jsonRpcHttpClient.invoke(RpcMethodsBitcoin.GET_BLOCK_COUNT, getBlockCountRpcMethodParameters(), Long.class);
        } catch (Throwable t) {
            throw new OperationFailedException("Error fetching block count.", t);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }

    @Override
    protected String getBlockByHashRpcMethodName() throws OperationFailedException {
        return "getblock";
    }

    @Override
    protected String getTransactionRpcMethodName() throws OperationFailedException {
        return "getrawtransaction";
    }

    @Override
    protected BlockData toBlockData(RpcResultBlock rpcResultBlock) throws OperationFailedException {
        BlockData blockData = new BlockData(blockchainNodeInfo);

        blockData.setHash(rpcResultBlock.getHash());
        blockData.setSize(rpcResultBlock.getSize());
        blockData.setHeight(rpcResultBlock.getHeight());
        blockData.setVersionHex(rpcResultBlock.getVersionHex());
        blockData.setMerkleRoot(rpcResultBlock.getMerkleroot());
        blockData.setTimestamp(rpcResultBlock.getTime());
        blockData.setNonce(rpcResultBlock.getNonce());
        blockData.setBits(rpcResultBlock.getBits());
        blockData.setDifficultyBase(rpcResultBlock.getDifficulty());
        blockData.setPrevHash(rpcResultBlock.getPreviousblockhash());
        blockData.setNextHash(rpcResultBlock.getNextblockhash());
        blockData.setRewardBase(BITCOIN_BLOCK_REWARD_BTC); // FIXME this does not account for halvenings

        // initialize some values
        blockData.setLargestFeeBase(Double.MIN_VALUE);
        blockData.setSmallestFeeBase(Double.MAX_VALUE);
        blockData.setLargestTxAmount(0);
        blockData.setTransactionCount(1);

        getBlockTransactions(rpcResultBlock).forEach(t -> processTransaction(t, blockData));

        if (blockData.getSmallestFee() == Double.MAX_VALUE)
            blockData.setSmallestFeeBase(0);

        return blockData;
    }

    protected Stream<RpcResultTransaction> getBlockTransactions(RpcResultBlock rpcResultBlock) {
        Stream<RpcResultTransaction> rpcResultTransactions;

        if (!isNullOrEmpty(rpcResultBlock.getTransactionStrings())) {
            rpcResultTransactions = rpcResultBlock.getTransactionStrings().stream().map(t -> fetchTransaction(t));
        } else {
            rpcResultTransactions = rpcResultBlock.getTransactions().stream();
        }

        return rpcResultTransactions;
    }

    @Override
    protected NodeTransaction toNodeTransaction(RpcResultTransaction rpcResultTransaction) throws OperationFailedException {
        BitcoinTransaction bitcoinTransaction = new BitcoinTransaction(rpcResultTransaction);

        double amount = rpcResultTransaction.vout.stream().mapToDouble(v -> v.value).sum();
        double inValue = 0.0;

        long size = 1; // FIXME

        if (rpcResultTransaction.vsize != null)
            size = rpcResultTransaction.vsize;
        if (rpcResultTransaction.size != null)
            size = rpcResultTransaction.size;

        for (int i = 0; i < rpcResultTransaction.vin.size(); i++) {
            RpcResultVin rpcResultVin = rpcResultTransaction.vin.get(i);

            if (rpcResultVin.txid != null) {
                RpcResultTransaction inputRpcResultTransaction = fetchTransaction(rpcResultVin.txid);
                inValue += inputRpcResultTransaction.vout.get(rpcResultVin.vout).value;
            }
        }

        if (inValue > 0) {
            bitcoinTransaction.setFee(inValue - amount);
            bitcoinTransaction.setFeeRate((1000.0 * bitcoinTransaction.getFee()) / size);
        } else {
            bitcoinTransaction.setCoinbase(true);
        }

        bitcoinTransaction.setAmount(amount);
        bitcoinTransaction.setBlockHash(rpcResultTransaction.blockhash);

        if (rpcResultTransaction.time != null)
            bitcoinTransaction.setTimestamp(rpcResultTransaction.time);

        return bitcoinTransaction;
    }

    @Override
    protected String getBlockByHeightRpcMethodName() throws OperationFailedException {
        throw new UnsupportedOperationException("Must call getblockhash and get block by hash.");
    }

    private String fetchBlockHash(long blockNumber) throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        try {
            return jsonRpcHttpClient.invoke(RpcMethodsBitcoin.GET_BLOCK_HASH, new Object[] { blockNumber }, String.class);
        } catch (Throwable e) {
            throw new OperationFailedException(format("Error fetching block hash for block %d:", blockNumber), e);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }

    @Override
    public BlockData fetchBlockData(long blockNumber) throws OperationFailedException {
        return fetchBlockDataByHash(fetchBlockHash(blockNumber));
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
        nodeListeners.stream().forEach(t -> t.newBlockAvailable(this, blockData));
    }

    @Override
    public void fetchNewBlocks() {
        if (running)
            return;

        ConcurrencyUtilities.startThread(() -> {
            LOGGER.info(format("[%s] Starting fetchNewBlocks thread.", getBlockchainCode()));

            try {
                while (true) {
                    ZMsg message = ZMsg.recvMsg(blockSubscriber, 0);
                    String topic = message.popString();

                    if (topic.equals("hashblock")) {
                        try {
                            handleIncomingRawBlock(message);
                        } catch (OperationFailedException e) {
                            LOGGER.error(String.format("[%s] Exception caught processing new block: ", getBlockchainCode()), e);
                        }
                    }
                }
            } finally {
                running = false;
            }
        }, "BlockListener-" + getBlockchainCode());
    }
}
