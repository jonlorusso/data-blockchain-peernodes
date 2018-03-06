package com.swatt.blockchain.btc;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

public class BlockchainBlock extends com.swatt.blockchain.BlockchainBlock {
    private static final Logger LOGGER = Logger.getLogger(BlockchainBlock.class.getName());

    private JsonRpcHttpClient jsonrpcClient = null;

    private RPCBlock block;
    private Double averageFee;
    private Double largestFee;
    private Double smallestFee;
    private Double totalFee;
    private Double largestTxAmount;
    private String largestTxHash;
    private Long transactionCount;
    private Long totalSize;

    public BlockchainBlock(BlockchainNode node, String blockHash) {
        if (jsonrpcClient == null) {
            jsonrpcClient = Utility.initJSONRPC();
        }

        if (blockHash == null) {
            this.block = findLatestBlock();
        } else {
            this.block = findBlockByHash(blockHash);
        }

        this.calculate();
    }

    private RPCBlock findLatestBlock() {
        RPCBlock block = null;
        Long blockCount;

        try {
            blockCount = jsonrpcClient.invoke(BTCMethods.GET_BLOCK_COUNT, new Object[] {}, Long.class);
            block = findBlockById(blockCount);
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }

        return block;
    }

    private RPCBlock findBlockById(Long blockId) {
        RPCBlock block = null;
        String blockHash = null;

        try {
            blockHash = jsonrpcClient.invoke(BTCMethods.GET_BLOCK_HASH, new Object[] { blockId }, String.class);
            block = findBlockByHash(blockHash);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return block;
    }

    private RPCBlock findBlockByHash(String blockHash) {
        RPCBlock block = null;

        try {
            block = jsonrpcClient.invoke(BTCMethods.GET_BLOCK, new Object[] { blockHash }, RPCBlock.class);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return block;
    }

    @Override
    public Double getAverageFee() {
        return this.averageFee;
    }

    private void calculate() {
        BlockchainTransaction transaction = null;

        Double transactionFee;
        Double transactionAmount;

        this.transactionCount = 0L; // block.tx.size();

        this.largestTxAmount = 0.0;
        this.totalSize = 0L;

        this.smallestFee = Double.MAX_VALUE;
        this.largestFee = 0.0;
        this.totalFee = 0.0;

        for (String transactionHash : block.tx) {
            transaction = new com.swatt.blockchain.btc.BlockchainTransaction(transactionHash, true);

            if (!transaction.minted) {
                transactionFee = transaction.getTransactionFee();
                transactionAmount = transaction.getTransactionAmount();

                this.smallestFee = Math.min(this.smallestFee, transactionFee);
                this.largestFee = Math.max(this.largestFee, transactionFee);

                this.transactionCount++;
                this.totalFee += transactionFee;
                this.totalSize += transaction.getSize();

                if (transactionAmount > this.largestTxAmount) {
                    this.largestTxAmount = transactionAmount;
                    this.largestTxHash = transactionHash;
                }

                if (transactionFee <= 0.0) {
                    System.out.println("Initial transaction: " + transactionHash);
                }
            }
        }

        this.averageFee = this.totalFee / this.transactionCount;
    }

    @Override
    public String getHash() {
        return this.block.hash;
    }

    @Override
    public Double getLargestFee() {
        return this.largestFee;
    }

    @Override
    public Double getSmallestFee() {
        return this.smallestFee;
    }

    @Override
    public Double getTotalFee() {
        return this.totalFee;
    }

    @Override
    public Double getLargestTxAmount() {
        return this.largestTxAmount;
    }

    @Override
    public String getLargestTxHash() {
        return this.largestTxHash;
    }

    @Override
    public Long getTransactionCount() {
        return this.transactionCount;
    }

    @Override
    public Long getTotalSize() {
        return this.totalSize;
    }

    @Override
    public int getHeight() {
        return this.block.height;
    }

    @Override
    public Double getDifficulty() {
        return this.block.difficulty;
    }

    @Override
    public String getMerkleRoot() {
        return this.block.merkleroot;
    }

    @Override
    public Long getTimeStamp() {
        return this.block.time;
    }

    @Override
    public String getBits() {
        return this.block.bits;
    }

    @Override
    public int getSize() {
        return this.block.size;
    }

    @Override
    public String getVersionHex() {
        return this.block.versionHex;
    }

    @Override
    public Long getNonce() {
        return this.block.nonce;
    }

    @Override
    public String getPrevHash() {
        return this.block.previousblockhash;
    }

    @Override
    public String getNextHash() {
        return this.block.nextblockhash;
    }
}