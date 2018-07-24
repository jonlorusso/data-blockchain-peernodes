package com.swatt.blockchain.node.lsk;

import static java.lang.String.format;

import com.fasterxml.jackson.databind.MappingIterator;
import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.entity.BlockchainNodeInfo;
import com.swatt.blockchain.node.HttpClientNode;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.node.NodeListener;
import com.swatt.blockchain.node.NodeTransaction;
import com.swatt.util.general.OperationFailedException;

public class LiskNode extends HttpClientNode<HttpResultBlock, HttpResultTransaction> {

    @Override
    protected BlockData toBlockData(HttpResultBlock httpResultBlock) throws OperationFailedException {
        BlockData blockData = new BlockData();
        blockData.setAvgFee(httpResultBlock.totalFee / httpResultBlock.numberOfTransactions);
        
        blockData.setBlockchainCode(blockchainNodeInfo.getCode());
        blockData.setHash(httpResultBlock.id);
        blockData.setHeight(httpResultBlock.height);
        
        double totalFee = 0.0;
        double largestFee = 0.0;
        double smallestFee = Double.MAX_VALUE;
        double largestTxAmount = 0.0;
        String largestTxHash = null;
        
        int transactionCount = 0;
      
        MappingIterator<HttpResultTransaction> httpResultTransactions = fetchIterator(String.format("/api/transactions?blockId=%s", httpResultBlock.id), HttpResultTransaction.class);
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
        return fetch("/api/node/status", HttpResultNodeStatus.class).height;
    }

    @Override
    protected String getBlockByHeightUrl(long height) {
        return format("/api/blocks?height=%d&limit=1", height);
    }
    
    public static void main(String[] args) throws OperationFailedException {
        BlockchainNodeInfo blockchainNodeInfo = new BlockchainNodeInfo();
        blockchainNodeInfo.setIp("127.0.0.1");
        blockchainNodeInfo.setPort(2014);
        
        LiskNode liskNode = new LiskNode();
        liskNode.setBlockchainNodeInfo(blockchainNodeInfo);
        liskNode.init();
        
        liskNode.addNodeListener(new NodeListener() {
            @Override
            public void newBlockAvailable(Node node, BlockData blockData) {
                System.out.println(blockData);
            }
        });
        
        liskNode.fetchNewBlocks();
        System.out.println(liskNode.fetchBlockCount());
    }
}
