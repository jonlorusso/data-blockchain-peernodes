package com.swatt.blockchain.node.xmr;

import static com.swatt.util.general.CollectionsUtilities.newHashMap;

import java.time.Instant;
import java.util.function.Supplier;
import java.util.logging.Logger;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.node.NodeTransaction;
import com.swatt.util.general.CollectionsUtilities;
import com.swatt.util.general.ConcurrencyUtilities;
import com.swatt.util.general.KeepNewestHash;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.json.HttpClientPool;
import com.swatt.util.json.JsonRpcHttpClientPool;

// TODO exten PollingNode and/or JsonRpcHttpClientNode
public class MoneroNode extends Node {
    private static final Logger LOGGER = Logger.getLogger(MoneroNode.class.getName());

    private static final int NEW_BLOCK_POLLING_FREQ_MS = 10 * 1000;
    private static final int JSON_RPC_POOL = 10; // TODO: Should get from chainNodeConfig
    private static final int HTTP_POOL = 10; // TODO: Should get from chainNodeConfig
    private static final int TRANSACTION_BUFFER_SIZE = 1000;

    private static KeepNewestHash transactions;
    
    private static final String RPC_URL_SUFFIX = "/json_rpc";
    private static final String GET_TRANSACTIONS_URL_SUFFIX = "/gettransactions";
    
    public static final int POWX_ATOMIC_UNITS = 12;
    
    private String url;
    private Thread newBlocksThread;

    private HttpClientPool httpClientPool;
    private JsonRpcHttpClientPool jsonRpcHttpClientPool;
    
    public MoneroNode() {
        transactions = new KeepNewestHash(TRANSACTION_BUFFER_SIZE);
    }

    @Override
    public void init() {
        url = String.format("http://%s:%d", blockchainNodeInfo.getIp(), blockchainNodeInfo.getPort());
        jsonRpcHttpClientPool = new JsonRpcHttpClientPool(url + RPC_URL_SUFFIX, null, null, JSON_RPC_POOL);
        
        httpClientPool = new HttpClientPool(HTTP_POOL);
    }

    private <T> T invokeMethod(String method, Object argument, Class<T> clazz) {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();
        
        try {
            return jsonRpcHttpClient.invoke(method, argument, clazz);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }
    
    @Override
    public long fetchBlockCount() throws OperationFailedException {
        return invokeMethod(RpcMethodsMonero.GET_BLOCK_COUNT, null, RpcResultBlockCount.class).count - 1;
    }
    
    private BlockData fetchBlockData(Supplier<RpcResultBlock> supplier) {
        try {
            long start = Instant.now().getEpochSecond();

            RpcResultBlock rpcResultBlock = supplier.get();
            BlockData blockData = toBlockData(rpcResultBlock);
            
            long indexingDuration = Instant.now().getEpochSecond() - start;
            long now = Instant.now().toEpochMilli();

            blockData.setIndexed(now);
            blockData.setIndexingDuration(indexingDuration);

            return blockData;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
    
    @Override
    public BlockData fetchBlockData(long height) throws OperationFailedException {
        return fetchBlockData(() -> fetchRpcResultBlock("height", height));
    }

    @Override
    public BlockData fetchBlockDataByHash(String hash) throws OperationFailedException {
        return fetchBlockData(() -> fetchRpcResultBlock("hash", hash));
    }
    
    private RpcResultBlock fetchRpcResultBlock(String key, Object value) {
        return invokeMethod(RpcMethodsMonero.GET_BLOCK, newHashMap(key, value), RpcResultBlock.class);
    }
    
    @Override
    public NodeTransaction fetchTransactionByHash(String hash, boolean calculate) throws OperationFailedException {
        MoneroTransaction moneroTransaction = new MoneroTransaction(url + GET_TRANSACTIONS_URL_SUFFIX, hash, httpClientPool);
        RpcResultBlock rpcBlock = fetchRpcResultBlock("height", moneroTransaction.getHeight());
        moneroTransaction.setTimestamp(rpcBlock.block_header.timestamp);
        moneroTransaction.setBlockHash(rpcBlock.block_header.hash);
        return moneroTransaction;
    }

    private BlockData toBlockData(RpcResultBlock rpcResultBlock) throws OperationFailedException {
        BlockData blockData = new BlockData();
        blockData.setScalingPowers(super.getDifficultyScaling(), super.getRewardScaling(), super.getFeeScaling(), super.getAmountScaling());
        blockData.setHash(rpcResultBlock.block_header.hash);
        blockData.setSize(rpcResultBlock.block_header.block_size);
        blockData.setHeight(rpcResultBlock.block_header.height);
        blockData.setTimestamp(rpcResultBlock.block_header.timestamp);
        blockData.setNonce(String.valueOf(rpcResultBlock.block_header.nonce));
        blockData.setDifficulty(rpcResultBlock.block_header.difficulty);
        blockData.setPrevHash(rpcResultBlock.block_header.prev_hash);

        // VERSION
        int versionMajor = rpcResultBlock.block_header.major_version;
        int versionMinor = rpcResultBlock.block_header.minor_version;
        int version = (versionMajor * 10000) + versionMinor;
        String versionHex = Integer.toHexString(version);
        blockData.setVersionHex(versionHex);

        blockData.setReward(rpcResultBlock.block_header.reward);
        blockData.setTransactionCount(rpcResultBlock.block_header.num_txes);
        blockData.setBlockchainCode(getBlockchainCode());

        calculate(blockData, rpcResultBlock);

        return blockData;
    }

    private void calculate(BlockData blockData, RpcResultBlock rpcBlock) throws OperationFailedException {
        double totalFee = 0.0;
        double totalFeeRate = 0.0;

        double largestFee = 0.0;
        double smallestFee = Double.MAX_VALUE;
        double largestTxAmount = 0.0;
        String largestTxHash = null;

        int transactionCount = 0;

        if (CollectionsUtilities.isNullOrEmpty(rpcBlock.tx_hashes)) {
            blockData.setTransactionCount(0);            
            return;
        }
       
        for (String transactionHash : rpcBlock.tx_hashes) {
            MoneroTransaction transaction = (MoneroTransaction) transactions.get(transactionHash);

            if (transaction == null) {
                transaction = new MoneroTransaction(url + GET_TRANSACTIONS_URL_SUFFIX, transactionHash, httpClientPool);
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
    public void fetchNewBlocks() {
        if (newBlocksThread != null)
            return;

        newBlocksThread = new Thread(() -> {
            LOGGER.info("Starting fetchNewBlocks thread.");

            try {
                long height = fetchBlockCount();
                
                while (true) {
                    long newHeight = fetchBlockCount();
                    
                    if (newHeight > height) {
                        BlockData blockData = fetchBlockData(newHeight);
                        nodeListeners.stream().forEach(n -> n.newBlockAvailable(this, blockData));
                        height = blockData.getHeight();
                    }

                    ConcurrencyUtilities.sleep(NEW_BLOCK_POLLING_FREQ_MS);
                }
            } catch (Throwable t) {
                newBlocksThread = null;
            }
        }, "BlockListener-" + getCode());

        newBlocksThread.start();
    }
}
