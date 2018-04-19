package com.swatt.chainNode.eth;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.logging.Logger;

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

import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.ChainNodeTransaction;
import com.swatt.chainNode.dao.BlockData;
import com.swatt.util.general.OperationFailedException;

public class EthereumChainNode extends ChainNode {
    private static final Logger LOGGER = Logger.getLogger(EthereumChainNode.class.getName());
    public static final String ETHEREUM_GENESIS_BLOCK = "0xd4e56740f876aef8c010b86a40d5f56745a118d0906a34e69aec8c0db1cb8fa3";
    private static final double ETHEREUM_BASE_BLOCK_REWARD_ETH = 3.0;
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
    public ChainNodeTransaction fetchTransactionByHash(String hash, boolean calculate) throws OperationFailedException {
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
        blockData.setBlockchainCode(blockchainCode);
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
	public String getGenesisHash() {
		return "0xd4e56740f876aef8c010b86a40d5f56745a118d0906a34e69aec8c0db1cb8fa3";
	}
	
    @Override
    public void fetchNewBlocks() {
        web3j.blockObservable(true).subscribe(b -> chainNodeListeners.stream().forEach(c -> c.newBlockAvailable(this, toBlockData(b))));
    }

    @Override
    public void fetchNewTransactions() {
        web3j.pendingTransactionObservable().subscribe(t -> chainNodeListeners.forEach(c -> c.newTransactionsAvailable(this, new ChainNodeTransaction[] { new EthereumTransaction(web3j, t) })));
    }
}