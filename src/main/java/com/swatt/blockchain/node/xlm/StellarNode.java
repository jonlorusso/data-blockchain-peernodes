package com.swatt.blockchain.node.xlm;

import static java.lang.String.format;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.glassfish.jersey.media.sse.EventSource;
import org.stellar.sdk.Server;
import org.stellar.sdk.requests.EventListener;
import org.stellar.sdk.requests.RequestBuilder.Order;
import org.stellar.sdk.requests.TooManyRequestsException;
import org.stellar.sdk.responses.LedgerResponse;
import org.stellar.sdk.responses.TransactionResponse;
import org.stellar.sdk.responses.operations.OperationResponse;
import org.stellar.sdk.responses.operations.PaymentOperationResponse;

import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.entity.BlockchainNodeInfo;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.node.NodeListener;
import com.swatt.blockchain.node.NodeTransaction;
import com.swatt.util.general.ConcurrencyUtilities;
import com.swatt.util.general.OperationFailedException;

public class StellarNode extends Node {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    
    private Server server;
    private EventSource newBlockEventSource;
    
    public StellarNode() {
        super();
    }
    
    @Override
    public void init() {
        server = new Server(format("http://%s:%d", blockchainNodeInfo.getIp(), blockchainNodeInfo.getPort()));
    }
    
    private BlockData toBlockData(LedgerResponse ledgerResponse) {
        BlockData blockData = new BlockData();
        
        blockData.setBlockchainCode(getCode());
        blockData.setHash(ledgerResponse.getHash());
        blockData.setTransactionCount(ledgerResponse.getTransactionCount());
        blockData.setHeight(ledgerResponse.getSequence());

        try {
            blockData.setTimestamp(simpleDateFormat.parse(ledgerResponse.getClosedAt()).getTime() / 1000);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        blockData.setPrevHash(ledgerResponse.getPrevHash());

        double totalFee = 0.0;
        double largestFee = 0.0;
        double smallestFee = Double.MAX_VALUE;
        double largestTxAmount = 0.0;
        String largestTxHash = null;

        try {
            List<TransactionResponse> transactionResponses = server.transactions().forLedger(ledgerResponse.getSequence()).execute().getRecords();
            for (TransactionResponse transactionResponse : transactionResponses) {
                NodeTransaction nodeTransaction = toNodeTransaction(transactionResponse);
                totalFee += nodeTransaction.getFee();

                double transactionFee = nodeTransaction.getFee();
                double transactionAmount = nodeTransaction.getAmount();

                largestFee = Math.max(largestFee, transactionFee);
                smallestFee = Math.min(smallestFee, transactionFee);

                if (transactionAmount > largestTxAmount) {
                    largestTxAmount = transactionAmount;
                    largestTxHash = nodeTransaction.getHash();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (ledgerResponse.getTransactionCount() > 0) {
            blockData.setAvgFeeBase(totalFee / ledgerResponse.getTransactionCount());

            if (smallestFee == Double.MAX_VALUE)
                smallestFee = 0;

            blockData.setSmallestFeeBase(smallestFee);
            blockData.setLargestFeeBase(largestFee);

            blockData.setLargestTxAmountBase(largestTxAmount);
            blockData.setLargestTxHash(largestTxHash);
        }

        return blockData;
    }
    
    @Override
    public BlockData fetchBlockDataByHash(String blockHash) throws OperationFailedException {
        throw new OperationFailedException("Fetching block by hash from Stellar Horizon is not supported.");
    }

    private NodeTransaction toNodeTransaction(TransactionResponse transactionResponse) {
        String hash = transactionResponse.getHash();
        
        NodeTransaction nodeTransaction = new NodeTransaction(hash);
        
        try {
            ArrayList<OperationResponse> operationResponses = server.payments().forTransaction(hash).execute().getRecords();
            
            for (OperationResponse operationResponse : operationResponses) {
                PaymentOperationResponse paymentOperationResponse = (PaymentOperationResponse)operationResponse;
                if (paymentOperationResponse.getAsset().getType().equals("native")) {
                    nodeTransaction.setAmount(nodeTransaction.getAmount() + Double.valueOf(paymentOperationResponse.getAmount()));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        nodeTransaction.setFee(transactionResponse.getFeePaid());
        nodeTransaction.setFeeRate(1);
        
        return nodeTransaction; 
    }
    
    @Override
    public NodeTransaction fetchTransactionByHash(String transactionHash, boolean calculate) throws OperationFailedException {
        try {
            return toNodeTransaction(server.transactions().transaction(transactionHash));
        } catch (IOException e) {
            throw new OperationFailedException(e);
        }
    }

    @Override
    public void fetchNewBlocks() {
        if (newBlockEventSource != null && newBlockEventSource.isOpen())
            return;
        
        newBlockEventSource = server.ledgers().cursor("now").stream(new EventListener<LedgerResponse>() {
            @Override
            public void onEvent(LedgerResponse ledgerResponse) {
                long start = Instant.now().getEpochSecond();
                
                BlockData blockData = toBlockData(ledgerResponse);
                blockData.setIndexingDuration(Instant.now().getEpochSecond() - start);
                blockData.setIndexed(Instant.now().toEpochMilli());

                nodeListeners.stream().forEach(n -> n.newBlockAvailable(StellarNode.this, toBlockData(ledgerResponse)));
            }
        });
        
        while (newBlockEventSource.isOpen()) {
            ConcurrencyUtilities.sleep(1000);
        }
        
        // FIXME throw exception if eventSource closes?
        newBlockEventSource = null;
    }

    @Override
    public long fetchBlockCount() throws OperationFailedException {
        try {
            return server.ledgers().limit(1).order(Order.DESC).execute().getRecords().get(0).getSequence();
        } catch (IOException | TooManyRequestsException e) {
            throw new OperationFailedException(e);
        }
    }

    @Override
    public BlockData fetchBlockData(long blockNumber) throws OperationFailedException {
        try {
            long start = Instant.now().getEpochSecond();
            BlockData blockData = toBlockData(server.ledgers().ledger(blockNumber)); 
            blockData.setIndexingDuration(Instant.now().getEpochSecond() - start);
            blockData.setIndexed(Instant.now().toEpochMilli());
            return blockData;
        } catch (IOException e) {
            throw new OperationFailedException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        BlockchainNodeInfo blockchainNodeInfo = new BlockchainNodeInfo();
        blockchainNodeInfo.setIp("127.0.0.1");
        blockchainNodeInfo.setPort(2011);
        StellarNode stellarNode = new StellarNode();
        stellarNode.setBlockchainNodeInfo(blockchainNodeInfo);
        stellarNode.init();
        
        stellarNode.addNodeListener(new NodeListener() {
            @Override
            public void newBlockAvailable(Node node, BlockData blockData) {
                System.out.println(blockData);
            }
        });
        
        stellarNode.fetchNewBlocks();
    }
}
