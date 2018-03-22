package com.swatt.chainNode.eth;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionResult;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.chainNode.dao.BlockData;
import com.swatt.util.OperationFailedException;

public class EthereumChainNode extends ChainNode {
    private static final Logger LOGGER = Logger.getLogger(EthereumChainNode.class.getName());
    private static final double ETHEREUM_BASE_BLOCK_REWARD_BTC = 3.0;
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
    public ChainNodeTransaction fetchTransactionByHash(String transactionHash, boolean calculate)
            throws OperationFailedException {

        try {
            EthereumTransaction transaction = new EthereumTransaction(web3j, transactionHash);

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

            EthBlock ethBlock = web3j.ethGetBlockByHash(blockHash, false).send();
            Block block = ethBlock.getBlock();

            BlockData blockData = new BlockData();

            blockData.setHash(blockHash);
            blockData.setTransactionCount(block.getTransactions().size());

            blockData.setHeight(block.getNumber().longValue());
            blockData.setTimestamp(block.getTimestamp().longValue());
            blockData.setNonce(block.getNonce().longValue());
            blockData.setDifficulty(block.getDifficulty().doubleValue());
            blockData.setPrevHash(block.getParentHash());
            blockData.setBlockchainCode(blockchainCode);
            blockData.setSize(block.getSize().intValue());

            System.out.println("CALCULATING BLOCK: " + blockHash);

            calculate(blockData, block);

            long indexingDuration = Instant.now().getEpochSecond() - start;
            long now = Instant.now().toEpochMilli();

            blockData.setIndexed(now);
            blockData.setIndexingDuration(indexingDuration);

            return blockData;
        } catch (Throwable t) {
            t.printStackTrace();
            OperationFailedException e = new OperationFailedException(
                    "Error fetching block from Blockchain: " + blockHash, t);
            LOGGER.log(Level.SEVERE, e.toString(), e);
            throw e;
        }
    }

    private void calculate(BlockData blockData, Block block) throws OperationFailedException {

        double totalFee = 0.0;
        double totalFeeRate = 0.0;

        double largestFee = 0.0;
        double smallestFee = Double.MAX_VALUE;
        double largestTxAmount = 0.0;
        String largestTxHash = null;

        int transactionCount = 0; // rpcBlock.tx.size();

        for (TransactionResult<?> transactionResult : block.getTransactions()) {
            String transactionHash = transactionResult.get().toString();

            EthTransaction ethTransaction = null;
            EthGetTransactionReceipt ethTransactionReceipt = null;
            try {
                ethTransaction = web3j.ethGetTransactionByHash(transactionHash).send();
                ethTransactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).send();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            Transaction transaction = ethTransaction.getTransaction().get();
            TransactionReceipt transactionReceipt = ethTransactionReceipt.getTransactionReceipt().get();

            double transactionFee = transactionReceipt.getGasUsed().doubleValue();

            BigInteger priceWei = transaction.getGasPrice();
            double priceEther = priceWei.doubleValue() * Math.pow(10, (-1 * EthereumChainNode.POWX_ETHER_WEI));
            double transactionFeeRate = priceEther * transactionReceipt.getGasUsed().doubleValue();

            BigInteger valueWei = transaction.getValue();
            double valueEther = valueWei.doubleValue() * Math.pow(10, (-1 * EthereumChainNode.POWX_ETHER_WEI));
            double transactionAmount = valueEther;

            largestFee = Math.max(largestFee, transactionFee);
            smallestFee = Math.min(smallestFee, transactionFee);

            transactionCount++;
            totalFee += transactionFee;
            totalFeeRate += transactionFeeRate;

            if (transactionAmount > largestTxAmount) {
                largestTxAmount = transactionAmount;
                largestTxHash = transactionHash;
            }
        }

        double reward = ETHEREUM_BASE_BLOCK_REWARD_BTC + totalFeeRate
                + (ETHEREUM_BASE_BLOCK_REWARD_BTC * block.getUncles().size() / 32);
        blockData.setReward(reward);

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
