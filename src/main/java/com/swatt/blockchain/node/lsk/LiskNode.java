package com.swatt.blockchain.node.lsk;

import static java.lang.String.format;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MappingIterator;
import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.entity.BlockchainNodeInfo;
import com.swatt.blockchain.node.HttpClientNode;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.node.NodeListener;
import com.swatt.blockchain.node.NodeTransaction;
import com.swatt.util.general.OperationFailedException;

public class LiskNode extends HttpClientNode<HttpResultBlock, HttpResultTransaction> {

    private static long genesisBlockTime = 1464094800;

    @Override
    protected BlockData toBlockData(HttpResultBlock httpResultBlock) throws OperationFailedException {
        BlockData blockData = new BlockData();
        
        if (httpResultBlock.numberOfTransactions > 0)
            blockData.setAvgFee(httpResultBlock.totalFee / httpResultBlock.numberOfTransactions);
        
        blockData.setBlockchainCode(blockchainNodeInfo.getCode());
        blockData.setHash(httpResultBlock.id);
        blockData.setHeight(httpResultBlock.height);
        blockData.setTimestamp(genesisBlockTime + httpResultBlock.timestamp);

        double totalFee = 0.0;
        double largestFee = 0.0;
        double smallestFee = Double.MAX_VALUE;
        double largestTxAmount = 0.0;
        String largestTxHash = null;
        
        int transactionCount = 0;
      
        MappingIterator<HttpResultTransaction> httpResultTransactions = fetchIterator(String.format("/api/transactions?blockId=%s", httpResultBlock.id), "/transactions", HttpResultTransaction.class);
        while (httpResultTransactions.hasNext()) {
            HttpResultTransaction httpResultTransaction = httpResultTransactions.next();
        
            double transactionFee = httpResultTransaction.fee;
            double transactionAmount = httpResultTransaction.amount;

            largestFee = Math.max(largestFee, transactionFee);
            smallestFee = Math.min(smallestFee, transactionFee);

            transactionCount++;
            totalFee += transactionFee;

            if (transactionAmount > largestTxAmount) {
                largestTxAmount = transactionAmount;
                largestTxHash = httpResultTransaction.hash;
            }
        }

        if (smallestFee == Double.MAX_VALUE)
            smallestFee = 0;

        if (transactionCount > 0) {
            double averageFee = totalFee / transactionCount;
            blockData.setAvgFeeBase(averageFee);
        }
        
        blockData.setTransactionCount(transactionCount);
        blockData.setSmallestFeeBase(smallestFee);
        blockData.setLargestFeeBase(largestFee);
        blockData.setLargestTxAmountBase(largestTxAmount);
        blockData.setLargestTxHash(largestTxHash);
        
        blockData.setPrevHash(httpResultBlock.previousBlockId);

        return blockData;
    }

    @Override
    protected NodeTransaction toNodeTransaction(HttpResultTransaction httpResultTransaction) throws OperationFailedException {
        NodeTransaction nodeTransaction = new NodeTransaction(httpResultTransaction.hash);
        nodeTransaction.setAmount(httpResultTransaction.amount);
        nodeTransaction.setFee(httpResultTransaction.fee);
        return nodeTransaction;
    }

    @Override
    protected String getTransactionByHashUrl(String transactionHash) {
        return String.format("/api/transactions?blockId=%d&limit=1", transactionHash);
    }

    @Override
    protected String getBlockByHashUrl(String blockHash) {
        return String.format("/api/blocks?blockId=%d&limit=1", blockHash);
    }

    @Override
    public long fetchBlockCount() throws OperationFailedException {
        Map<String, Object> nodeStatus = fetch("api/node/status", "/data", new TypeReference<Map<String, Object>>() {});
        return ((Integer)nodeStatus.get("height")).longValue();
    }

    @Override
    protected String getBlockByHeightUrl(long height) {
        return format("/api/blocks?height=%d&limit=1", height);
    }
    
    @Override
    public BlockData fetchBlockData(long blockNumber, boolean notifyListeners) throws OperationFailedException {
        long start = Instant.now().getEpochSecond();
        MappingIterator<HttpResultBlock> httpResultBlocks = fetchIterator(getBlockByHeightUrl(blockNumber), "/data", HttpResultBlock.class);
        BlockData blockData = toBlockData(httpResultBlocks.next());
        blockData.setIndexingDuration(Instant.now().getEpochSecond() - start);
        blockData.setIndexed(Instant.now().toEpochMilli());

        if (notifyListeners)
            nodeListeners.stream().forEach(n -> n.blockFetched(this, blockData));

        return blockData;
    }
}
