package com.swatt.blockchain.node.eth;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.*;

import com.swatt.blockchain.node.PlatformNode;
import com.swatt.blockchain.repository.BlockchainToken;
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
import com.swatt.blockchain.node.NodeTransaction;
import com.swatt.util.general.OperationFailedException;

import rx.Observable;
import rx.Subscriber;

import static java.util.stream.Collectors.toMap;

public class EthereumNode extends PlatformNode {
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

    private Map<String, BlockchainToken> tokensByAddress = new HashMap<>();

    @Override
    public void init() {
        String url = String.format("http://%s:%d", blockchainNodeInfo.getIp(), blockchainNodeInfo.getPort());
        web3j = Web3j.build(new HttpService(url));

        try {
            LOGGER.info("[ETH] Connected to Ethereum client version: " + web3j.web3ClientVersion().send().getWeb3ClientVersion());
        } catch (IOException e) {
            LOGGER.error("[ETH] Could not connect to Ethereum client: " + e.getMessage());
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
        BlockData blockData = initializeBlockData(block, null, null);
        block.getTransactions().stream().map(TransactionResult<Transaction>::get).forEach(t -> processTransaction(t, blockData, null));
        return blockData;
    }

    private List<BlockData> toBlockDatas(Block block) {
        Map<BlockchainToken, BlockData> tokenBlockDatas = new HashMap<>();

        for (TransactionResult<Transaction> transactionResult : block.getTransactions()) {
            Transaction transaction = transactionResult.get();
            BlockchainToken blockchainToken = tokensByAddress.get(transaction.getTo());
            BlockData blockData = initializeBlockData(block, blockchainToken, tokenBlockDatas.get(blockchainToken));
            tokenBlockDatas.put(blockchainToken, processTransaction(transaction, blockData, blockchainToken));
        }

        List<BlockData> blockDatas = new ArrayList<>();

        tokenBlockDatas.values().stream().forEach(b -> {
            if (b.getSmallestFee() == Double.MAX_VALUE)
                b.setSmallestFeeBase(0.0);
            blockDatas.add(b);
        });

        return blockDatas;
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

    private BlockData processTransaction(Transaction transaction, BlockData blockData, BlockchainToken blockchainToken) {
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

    private BlockData initializeBlockData(Block block, BlockchainToken blockchainToken, BlockData blockData) {
        if (blockData != null)
            return blockData;

        blockData = new BlockData();

        if (blockchainToken != null) {
            blockData.setBlockchainCode(blockchainToken.getCode());
            blockData.setDifficultyBase(0);

        } else {
            blockData.setRewardBase(ETHEREUM_BASE_BLOCK_REWARD_ETH); // FIXME
            blockData.setBlockchainCode(blockchainNodeInfo.getCode());
            blockData.setDifficultyBase(block.getDifficulty().doubleValue());
        }

        blockData.setScalingPowers(super.getDifficultyScaling(), super.getRewardScaling(), super.getFeeScaling(), super.getAmountScaling());
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

    private static double tokenTransferAmount(Transaction transaction, BlockchainToken blockchainToken) {
        if (blockchainToken != null) {
            String input = transaction.getInput();

            // the amount is always the last argument regardless of transfter method type (transfer or transferFrom)
            BigInteger amountBigInteger = new BigInteger(input.substring(input.length() - 64), 16);
            return amountBigInteger.doubleValue() * Math.pow(10, (-1 * blockchainToken.getDecimals()));
        }

        return transaction.getValue().doubleValue() * Math.pow(10, (-1 * POWX_ETHER_WEI));
    }
}
