package com.swatt.blockchain.node.strat;

import static java.lang.String.format;

import java.io.IOException;
import java.time.Instant;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.node.NodeTransaction;
import com.swatt.util.general.ConcurrencyUtilities;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.general.StringUtilities;
import com.swatt.util.general.TimeUtilities;
import com.swatt.util.io.IoUtilities;
import com.swatt.util.json.HttpClientPool;

public class StratisNode extends Node {
    private static final Logger LOGGER = Logger.getLogger(StratisNode.class.getName());

    private static final long NEW_BLOCK_POLLING_FREQ_MS = TimeUtilities.TEN_SECONDS;
    private static final double BLOCK_REWARD = 1.0;
    
    private Thread newBlocksThread;
    private HttpClientPool httpClientPool;
    private ObjectMapper objectMapper;
    private Object baseUrl;

    public StratisNode() {
        super();
    }
    
    @Override
    public void init() {
        objectMapper = new ObjectMapper();
        
        baseUrl = String.format("http://%s:%d/api", blockchainNodeInfo.getIp(), blockchainNodeInfo.getPort());
        int maxSize = 10; // TODO: Should get from nodeConfig
        httpClientPool = new HttpClientPool(maxSize);
    }

    private <T> T execute(String path, Class<T> clazz) throws OperationFailedException {
        return execute(path, null, clazz);
    }

    private <T> T execute(String path, String params, Class<T> clazz) throws OperationFailedException {
        CloseableHttpResponse response = httpClientPool.execute(format("%s/%s", baseUrl, path), params);

        try {
            return objectMapper.readValue(readResponse(response), clazz);
        } catch (IOException e) {
            throw new OperationFailedException(e);
        }
    }

    @Override
    public long fetchBlockCount() throws OperationFailedException {
        return execute("BlockStore/getblockcount", long.class);
    }

    @Override
    public NodeTransaction fetchTransactionByHash(String hash, boolean calculate) throws OperationFailedException {
        HttpResultTransaction httpResultTransaction = execute(format("Node/getrawtransaction?trxid=%s&verbose=true", hash), HttpResultTransaction.class);
        
        double amount = httpResultTransaction.getVout().stream().mapToDouble(v -> v.getValue()).sum();
        
        double inValue = 0.0;
        for (HttpResultVin vin : httpResultTransaction.getVin()) {
            if (!StringUtilities.isNullOrAllWhiteSpace(vin.getTxid())) {
                HttpResultTransaction uxto = execute(format("Node/getrawtransaction?trxid=%s&verbose=true", vin.getTxid()), HttpResultTransaction.class);
                inValue += uxto.getVout().get(vin.getVout()).getValue();
            }
        }
        
        double fee = inValue - amount;
        double feeRate = (1000.0 * fee) / httpResultTransaction.getSize();
        
        NodeTransaction nodeTransaction = new NodeTransaction(hash);
        nodeTransaction.setFee(fee);
        nodeTransaction.setFeeRate(feeRate);
        nodeTransaction.setAmount(amount);
        return nodeTransaction;
    }
    
    private String fetchBlockHash(long height) throws OperationFailedException {
        return execute(format("Consensus/getblockhash?height=%d", height), String.class);
    }
    
    private HttpResultBlock fetchBlock(String hash) throws OperationFailedException {
        return execute(format("BlockStore/block?hash=%s&OutputJson=true", hash), HttpResultBlock.class);  
    }
    
    private HttpResultBlock fetchBlock(long blockNumber) throws OperationFailedException {
        return fetchBlock(fetchBlockHash(blockNumber));
    }
    
    @Override
    public BlockData fetchBlockDataByHash(String blockHash) throws OperationFailedException {
        long start = Instant.now().getEpochSecond();
        BlockData blockData = toBlockData(0, fetchBlock(blockHash));
        blockData.setIndexingDuration(Instant.now().getEpochSecond() - start);
        blockData.setIndexed(Instant.now().toEpochMilli());
        return blockData;
    }
    
    @Override
    public BlockData fetchBlockData(long blockNumber) throws OperationFailedException {
        long start = Instant.now().getEpochSecond();
        BlockData blockData = toBlockData(blockNumber, fetchBlock(blockNumber));
        blockData.setIndexingDuration(Instant.now().getEpochSecond() - start);
        blockData.setIndexed(Instant.now().toEpochMilli());
        return blockData;
    }

    private BlockData toBlockData(long blockNumber, HttpResultBlock httpResultBlock) throws OperationFailedException {
        BlockData blockData = new BlockData();
        blockData.setScalingPowers(super.getDifficultyScaling(), super.getRewardScaling(), super.getFeeScaling(), super.getAmountScaling());
        blockData.setHash(httpResultBlock.getHash());
        blockData.setSize(httpResultBlock.getSize());
        blockData.setHeight(blockNumber);
        blockData.setVersionHex(httpResultBlock.getVersion());
        blockData.setMerkleRoot(httpResultBlock.getMerkleRoot());
        blockData.setTimestamp(httpResultBlock.getTime());
        blockData.setNonce(httpResultBlock.getNonce());
        blockData.setBits(httpResultBlock.getBits());
        blockData.setDifficultyBase(httpResultBlock.getDifficulty());
        blockData.setPrevHash(httpResultBlock.getPreviousBlockHash());
        blockData.setRewardBase(BLOCK_REWARD);
        blockData.setBlockchainCode(blockchainNodeInfo.getCode());
        calculate(blockData, httpResultBlock);
        return blockData;
    }
    
    private String readResponse(CloseableHttpResponse response) throws OperationFailedException {
        try {
            HttpEntity responseHttpEntity = response.getEntity();
            String responseString = IoUtilities.streamToString(responseHttpEntity.getContent());
            EntityUtils.consume(responseHttpEntity);
            return responseString;
        } catch (UnsupportedOperationException | IOException e) {
            throw new OperationFailedException(e);
        }
    }
    
    
    /**
     * Every stratis block has at least 2 transactions, the generation transaction (index = 0) and the reward
     * transaction (index = 1).  The reward transaction will generally be a transaction from the staker to herself
     * where the out value is the in value plus any transaction fees plus the block reward (1 STRAT).
     * 
     * As a result, the fee on the reward transaction is negative (and should be disregarded in our calculations below.
     */
    protected void calculate(BlockData blockData, HttpResultBlock httpResultBlock) throws OperationFailedException {
        double totalFee = 0.0;
        double totalFeeRate = 0.0;

        double largestFee = 0.0;
        double smallestFee = Double.MAX_VALUE;
        
        double largestTxAmount = 0.0;
        String largestTxHash = null;

        int transactionCount = 0; // rpcBlock.tx.size();
        
        int transactionIndex = 0;
        for (String transaction : httpResultBlock.getTransactions()) {
            NodeTransaction nodeTransaction = fetchTransactionByHash(transaction, false);

            if (transactionIndex > 0) {
                transactionCount++;

                double transactionFee = nodeTransaction.getFee();
                double transactionAmount = nodeTransaction.getAmount();

                largestFee = Math.max(largestFee, transactionFee);
                
                if (transactionFee > 0) {
                    smallestFee = Math.min(smallestFee, transactionFee);
                    totalFee += transactionFee;
                    totalFeeRate += nodeTransaction.getFeeRate();
                }

                if (transactionAmount > largestTxAmount) {
                    largestTxAmount = transactionAmount;
                    largestTxHash = nodeTransaction.getHash();
                }
            }
            
            transactionIndex++;
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

        newBlocksThread = ConcurrencyUtilities.startThread(() -> {
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
        }, "BlockListener-" + getBlockchainCode());
    }
}
