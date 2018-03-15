package com.swatt.chainNode.eth;

import java.io.IOException;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.chainNode.dao.BlockData;
import com.swatt.util.OperationFailedException;

public class EthereumChainNode extends ChainNode {
    private static final Logger LOGGER = Logger.getLogger(EthereumChainNode.class.getName());
    private static Web3j web3j;

    public static final int POWX_ETHER_WEI = 18;
    public static final int POWX_ETHER_KWEI = 15;
    public static final int POWX_ETHER_MWEI = 12;
    public static final int POWX_ETHER_GWEI = 9;
    public static final int POWX_ETHER_SZABO = 6;
    public static final int POWX_ETHER_FINNEY = 3;
    public static final int POWX_ETHER_ETHER = 0;
    public static final int POWX_ETHER_KETHER = -3;
    public static final int POWX_ETHER_METHER = -6;
    public static final int POWX_ETHER_GETHER = -9;
    public static final int POWX_ETHER_TETHER = -12;

    public EthereumChainNode() {
    }

    @Override
    public void init() {
        String url = chainNodeConfig.getURL();

        web3j = Web3j.build(new HttpService(url));
        try {
            System.out.println(
                    "Connected to Ethereum client version: " + web3j.web3ClientVersion().send().getWeb3ClientVersion());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public BlockData fetchBlockDataByHash(String blockHash) throws OperationFailedException {
        try {
            if (blockHash != null) {
                return fetchBlockByHash(blockHash);
            } else {
                return null;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    @Override
    public ChainNodeTransaction fetchTransactionByHash(String transactionHash, boolean calculateFee)
            throws OperationFailedException {

        try {
            EthereumTransaction transaction = new EthereumTransaction(web3j, transactionHash, true);

            return transaction;
        } catch (Throwable t) {
            OperationFailedException e = new OperationFailedException("Error fetching latest Block: ", t);
            LOGGER.log(Level.SEVERE, e.toString(), e);
            throw e;
        }
    }

    private BlockData fetchBlockByHash(String blockHash) throws OperationFailedException {

        try {
            long start = Instant.now().getEpochSecond();

            Object parameters[] = new Object[] { blockHash };

            /*
             * RPCBlock rpcBlock = jsonrpcClient.invoke(ETHMethods.GET_BLOCK_BYHASH,
             * parameters, RPCBlock.class);
             * 
             * BlockData blockData = new BlockData(); blockData.setHash(rpcBlock.hash);
             * blockData.setSize(rpcBlock.size); blockData.setHeight(rpcBlock.height);
             * blockData.setVersionHex(rpcBlock.versionHex);
             * blockData.setMerkleRoot(rpcBlock.merkleroot);
             * blockData.setTimestamp(rpcBlock.time); blockData.setNonce(rpcBlock.nonce);
             * blockData.setBits(rpcBlock.bits);
             * blockData.setDifficulty(rpcBlock.difficulty);
             * blockData.setPrevHash(rpcBlock.previousblockhash);
             * blockData.setNextHash(rpcBlock.nextblockhash);
             * 
             * blockData.setBlockchainCode(blockchainCode);
             * 
             * System.out.println("CALCULATING BLOCK: " + rpcBlock.hash);
             * 
             * calculate(jsonrpcClient, blockData, rpcBlock);
             * 
             * long indexingDuration = Instant.now().getEpochSecond() - start; long now =
             * Instant.now().toEpochMilli();
             * 
             * blockData.setIndexed(now); blockData.setIndexingDuration(indexingDuration);
             */

            return new BlockData();

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

    private void calculate(BlockData blockData, RPCBlock rpcBlock) throws OperationFailedException {

        double totalFee = 0.0;
        double totalFeeRate = 0.0;

        double largestFee = 0.0;
        double smallestFee = Double.MAX_VALUE;
        double largestTxAmount = 0.0;
        String largestTxHash = null;

        int transactionCount = 1; // rpcBlock.tx.size();

        for (String transactionHash : rpcBlock.tx) {
            EthereumTransaction transaction = new EthereumTransaction(web3j, transactionHash, true);
            // System.out.println(transactionHash + " " + ((double) transactionCount /
            // (double) rpcBlock.tx.size()));

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
