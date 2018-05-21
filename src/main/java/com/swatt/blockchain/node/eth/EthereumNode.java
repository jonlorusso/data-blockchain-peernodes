package com.swatt.blockchain.node.eth;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionResult;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.node.NodeTransaction;
import com.swatt.util.general.OperationFailedException;

import rx.Observable;
import rx.Subscriber;

public class EthereumNode extends Node {
    private static final Logger LOGGER = LoggerFactory.getLogger(EthereumNode.class.getName());
    
    private static final double ETHEREUM_BASE_BLOCK_REWARD_ETH = 3.0;

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

    private Web3j web3j;
    private Observable<EthBlock> blockObservable;

    @Override
    public void init() {
        String url = String.format("http://%s:%d", blockchainNodeInfo.getIp(), blockchainNodeInfo.getPort());
        web3j = Web3j.build(new HttpService(url));
        
        try {
            LOGGER.info("[ETH] Connected to Ethereum client version: " + web3j.web3ClientVersion().send().getWeb3ClientVersion());
        } catch (IOException e) {
            LOGGER.error("[ETH] Could not connect to Ethereum client: " + e.getMessage());
        }
    }

    @Override
    public BlockData fetchBlockDataByHash(String hash) throws OperationFailedException {
    		if (hash == null)
			return null;
    		
        try {
            long start = Instant.now().getEpochSecond();

            EthBlock ethBlock = web3j.ethGetBlockByHash(hash, true).send();
            BlockData blockData = toBlockData(ethBlock);

            long indexingDuration = Instant.now().getEpochSecond() - start;
            long now = Instant.now().toEpochMilli();

            blockData.setIndexed(now);
            blockData.setIndexingDuration(indexingDuration);

            return blockData;
        } catch (Throwable t) {
        		throw new OperationFailedException(t);
        }
    }

	@Override
	public BlockData fetchBlockData(long blockNumber) throws OperationFailedException {
        try {
            long start = Instant.now().getEpochSecond();

            EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNumber)), true).send();
            BlockData blockData = toBlockData(ethBlock);

            long indexingDuration = Instant.now().getEpochSecond() - start;
            long now = Instant.now().toEpochMilli();

            blockData.setIndexed(now);
            blockData.setIndexingDuration(indexingDuration);

            return blockData;
        } catch (Throwable t) {
        		throw new OperationFailedException(t);
        }
	}

    @Override
    public NodeTransaction fetchTransactionByHash(String hash, boolean calculate) throws OperationFailedException {
        try {
            return new EthereumTransaction(web3j, hash);
        } catch (Throwable t) {
        		throw new OperationFailedException(t);
        }
    }

    private BlockData toBlockData(EthBlock ethBlock) {
        Block block = ethBlock.getBlock();

        BlockData blockData = new BlockData();
        blockData.setScalingPowers(super.getDifficultyScaling(), super.getRewardScaling(), super.getFeeScaling(), super.getAmountScaling());
        blockData.setHash(block.getHash());
        blockData.setTransactionCount(block.getTransactions().size());
        blockData.setHeight(block.getNumber().longValue());
        blockData.setTimestamp(block.getTimestamp().longValue());
        blockData.setNonce(block.getNonce().longValue());
        blockData.setDifficultyBase(block.getDifficulty().doubleValue());
        blockData.setPrevHash(block.getParentHash());
        blockData.setBlockchainCode(blockchainNodeInfo.getCode());
        blockData.setSize(block.getSize().intValue());

        try {
            calculate(blockData, block);
        } catch (OperationFailedException e) {
            // FIXME better exception handling
            throw new IllegalStateException(e);
        }
        return blockData;
    }
    
    @SuppressWarnings("unchecked")
    private void calculate(BlockData blockData, Block block) throws OperationFailedException {
        double totalFee = 0.0;
        double totalFeeRate = 0.0;

        double largestFee = 0.0;
        double smallestFee = Double.MAX_VALUE;
        double largestTxAmount = 0.0;
        String largestTxHash = null;

        int transactionCount = 0; // rpcBlock.tx.size();

        for (TransactionResult<Transaction> transactionResult : block.getTransactions()) {
            Transaction transaction = transactionResult.get();
            String transactionHash = transaction.getHash();

            EthGetTransactionReceipt ethTransactionReceipt = null;
            try {
                ethTransactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).send();
            } catch (IOException e1) {
                throw new OperationFailedException(e1);
            }

            TransactionReceipt transactionReceipt = ethTransactionReceipt.getTransactionReceipt().get();

            double transactionFee = transactionReceipt.getGasUsed().doubleValue();

            BigInteger priceWei = transaction.getGasPrice();
            double priceEther = priceWei.doubleValue() * Math.pow(10, (-1 * EthereumNode.POWX_ETHER_WEI));
            double transactionFeeRate = priceEther * transactionReceipt.getGasUsed().doubleValue();

            BigInteger valueWei = transaction.getValue();
            double valueEther = valueWei.doubleValue() * Math.pow(10, (-1 * EthereumNode.POWX_ETHER_WEI));
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

        double reward = ETHEREUM_BASE_BLOCK_REWARD_ETH + totalFeeRate
                + (ETHEREUM_BASE_BLOCK_REWARD_ETH * block.getUncles().size() / 32);
        blockData.setRewardBase(reward);

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
        try {
            EthBlockNumber ethBlockNumber = web3j.ethBlockNumber().send();
            return ethBlockNumber.getBlockNumber().longValueExact();
        } catch (Throwable e) {
        		throw new OperationFailedException(e);
        }
	}

    @Override
    public void fetchNewBlocks() {
        if (blockObservable != null) {
            return;
        }

        LOGGER.info("Starting fetchNewBlocks thread.");

        blockObservable = web3j.blockObservable(true);
        blockObservable.subscribe(new Subscriber<EthBlock>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                LOGGER.error("newBlock ingestion failed: " + e.getMessage());
                blockObservable = null;
            }

            @Override
            public void onNext(EthBlock b) {
                nodeListeners.stream().forEach(c -> c.newBlockAvailable(EthereumNode.this, toBlockData(b)));
            }
        });
    }

    @Override
    public void fetchNewTransactions() {
        web3j.pendingTransactionObservable().subscribe(t -> nodeListeners.forEach(c -> c.newTransactionsAvailable(this, new NodeTransaction[] { new EthereumTransaction(web3j, t) })));
    }
}
