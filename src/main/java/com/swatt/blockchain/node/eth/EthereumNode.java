package com.swatt.blockchain.node.eth;

import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.entity.BlockchainNodeInfo;
import com.swatt.blockchain.node.NodeTransaction;
import com.swatt.blockchain.node.PlatformNode;
import com.swatt.util.general.OperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionResult;
import org.web3j.protocol.http.HttpService;
import rx.Observable;
import rx.Subscriber;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class EthereumNode extends PlatformNode {
    private static final Logger LOGGER = LoggerFactory.getLogger(EthereumNode.class.getName());

    // FIXME can this be retrieved from the node?
    private static final double ETHEREUM_BASE_BLOCK_REWARD_ETH = 3.0;

    public static final int POWX_ETHER_WEI = 18;

    private Web3j web3j;
    private Observable<EthBlock> blockObservable;

    private Map<String, BlockchainNodeInfo> tokensByAddress = new HashMap<>();

    @Override
    public void init() {
        String url = format("http://%s:%d", blockchainNodeInfo.getIp(), blockchainNodeInfo.getPort());
        web3j = Web3j.build(new HttpService(url));

        try {
            String web3ClientVersion =  web3j.web3ClientVersion().send().getWeb3ClientVersion();
            LOGGER.debug(format("[%s] Connected to Ethereum client version: %s", getBlockchainCode(), web3ClientVersion));
        } catch (IOException e) {
            throw new IllegalStateException("Could not connect to Ethereum node.", e);
        }

        tokens.stream().forEach(t -> tokensByAddress.put(t.getSmartContractAddress(), t));
    }

    @Override
    public BlockData fetchBlockDataByHash(String hash) throws OperationFailedException {
        if (hash == null)
            return null;

        try {
            long start = Instant.now().getEpochSecond();

            EthBlock ethBlock = web3j.ethGetBlockByHash(hash, true).send();
            BlockData blockData = toBlockData(ethBlock.getBlock());

            long indexingDuration = Instant.now().getEpochSecond() - start;
            long now = Instant.now().toEpochMilli();

            blockData.setIndexed(now);
            blockData.setIndexingDuration(indexingDuration);

            nodeListeners.stream().forEach(n -> n.newBlockAvailable(this, blockData));

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
            BlockData blockData = toBlockData(ethBlock.getBlock());

            long indexingDuration = Instant.now().getEpochSecond() - start;
            long now = Instant.now().toEpochMilli();

            blockData.setIndexed(now);
            blockData.setIndexingDuration(indexingDuration);

            nodeListeners.stream().forEach(n -> n.blockFetched(this, blockData));

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

    private BlockData toBlockData(Block block) {
        BlockData blockData = initializeBlockData(block, null);
        block.getTransactions().stream().map(TransactionResult<Transaction>::get).forEach(t -> processTransaction(t, blockData, null));
        return blockData;
    }

    private List<BlockData> toBlockDatas(Block block) {
        Map<String, BlockData> tokenBlockDatas = new HashMap<>();

        for (TransactionResult<Transaction> transactionResult : block.getTransactions()) {
            Transaction transaction = transactionResult.get();

            BlockchainNodeInfo tokenBlockchainNodeInfo = tokensByAddress.get(transaction.getTo());
            if (tokenBlockchainNodeInfo != null) {
                String tokenCode = tokenBlockchainNodeInfo.getCode();
                BlockData blockData = tokenBlockDatas.get(tokenCode);
                blockData = blockData != null ? blockData : initializeBlockData(block, tokenBlockchainNodeInfo);
                tokenBlockDatas.put(tokenCode, processTransaction(transaction, blockData, tokenBlockchainNodeInfo));
            }
        }

        return tokenBlockDatas.values().stream().peek(b -> {
            if (b.getSmallestFee() == Double.MAX_VALUE)
                b.setSmallestFeeBase(0.0);
        }).collect(Collectors.toList());
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
        if (blockObservable != null)
            return;

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
            public void onNext(EthBlock ethBlock) {
                nodeListeners.stream().forEach(c -> c.newBlockAvailable(EthereumNode.this, toBlockData(ethBlock.getBlock())));
                toBlockDatas(ethBlock.getBlock()).stream().forEach(b -> nodeListeners.stream().forEach(c -> c.newBlockAvailable(EthereumNode.this, b)));
            }
        });
    }

    private BlockData processTransaction(Transaction transaction, BlockData blockData, BlockchainNodeInfo blockchainToken) {
        String transactionHash = transaction.getHash();
        int transactionCount = blockData.getTransactionCount();

        try {
            EthGetTransactionReceipt ethTransactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).send();
            TransactionReceipt transactionReceipt = ethTransactionReceipt.getTransactionReceipt().get();

            double transactionFee = transactionReceipt.getGasUsed().doubleValue();
            blockData.setSmallestFeeBase(Math.min(blockData.getSmallestFee(), transactionFee));
            blockData.setLargestFeeBase(Math.max(blockData.getLargestFee(), transactionFee));
            blockData.setAvgFeeBase((blockData.getAvgFee() * transactionCount + transactionFee) / (transactionCount + 1));

            BigInteger priceWei = transaction.getGasPrice();
            double priceEther = priceWei.doubleValue() * Math.pow(10, (-1 * EthereumNode.POWX_ETHER_WEI));
            double transactionFeeRate = priceEther * transactionReceipt.getGasUsed().doubleValue();
            blockData.setAvgFeeRateBase((blockData.getAvgFeeRate() * transactionCount + transactionFeeRate) / (transactionCount + 1));
            blockData.setRewardBase(blockData.getReward() + transactionFeeRate);

            double transactionAmount = tokenTransferAmount(transaction, blockchainToken);
            if (transactionAmount > blockData.getLargestTxAmount()) {
                blockData.setLargestTxHash(transactionHash);
                blockData.setLargestTxAmountBase(transactionAmount);
            }

            blockData.setTransactionCount(blockData.getTransactionCount() + 1);
        } catch (IOException e) {
            LOGGER.error("Exception caugh processing block transaction.", e);
        }

        return blockData;
    }

    private BlockData initializeBlockData(Block block, BlockchainNodeInfo tokenBlockchainNodeInfo) {
        BlockData blockData = new BlockData();

        blockData.setScalingPowers(super.getDifficultyScaling(), super.getRewardScaling(), super.getFeeScaling(), super.getAmountScaling());
        blockData.setBlockchainCode(tokenBlockchainNodeInfo != null ? tokenBlockchainNodeInfo.getCode() : blockchainNodeInfo.getCode());
        blockData.setDifficultyBase(0);

        if (tokenBlockchainNodeInfo == null) {
            blockData.setRewardBase(ETHEREUM_BASE_BLOCK_REWARD_ETH + (ETHEREUM_BASE_BLOCK_REWARD_ETH * block.getUncles().size() / 32));
            blockData.setDifficultyBase(block.getDifficulty().doubleValue());
        }

        blockData.setHash(block.getHash());
        blockData.setTransactionCount(0);
        blockData.setHeight(block.getNumber().longValue());
        blockData.setTimestamp(block.getTimestamp().longValue());
        blockData.setNonce(block.getNonce().toString());
        blockData.setPrevHash(block.getParentHash());
        blockData.setSize(block.getSize().intValue());

        blockData.setLargestFeeBase(0.0);
        blockData.setSmallestFeeBase(Double.MAX_VALUE);
        blockData.setLargestTxAmountBase(0.0);
        blockData.setLargestTxHash(null);

        return blockData;
    }

    @Override
    public List<BlockData> fetchTokenBlockDatas(long blockNumber) throws OperationFailedException {
        try {
            long start = Instant.now().getEpochSecond();

            EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNumber)), true).send();
            List<BlockData> blockDatas = toBlockDatas(ethBlock.getBlock());

            long indexingDuration = Instant.now().getEpochSecond() - start;
            long now = Instant.now().toEpochMilli();

            for (BlockData blockData : blockDatas) {
                blockData.setIndexed(now);
                blockData.setIndexingDuration(indexingDuration);
            }

            return blockDatas;
        } catch (Throwable t) {
            throw new OperationFailedException(t);
        }
    }

    private static double tokenTransferAmount(Transaction transaction, BlockchainNodeInfo blockchainToken) {
        if (blockchainToken != null) {
            String input = transaction.getInput();

            // the amount is always the last argument regardless of transfter method type (transfer or transferFrom)
            BigInteger amountBigInteger = new BigInteger(input.substring(input.length() - 64), 16);
            return amountBigInteger.doubleValue() * Math.pow(10, (-1 * blockchainToken.getDecimals()));
        }

        return transaction.getValue().doubleValue() * Math.pow(10, (-1 * POWX_ETHER_WEI));
    }
}
