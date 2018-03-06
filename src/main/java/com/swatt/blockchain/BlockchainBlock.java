package com.swatt.blockchain;

import java.sql.CallableStatement;
import java.sql.SQLException;

import com.swatt.blockchain.persistence.BlockchainDB;

public abstract class BlockchainBlock {
    private BlockchainNode node;
    private String hash;

    public BlockchainBlock(BlockchainNode node, String blockHash) {
        this.node = node;
        this.hash = blockHash;
    }

    public BlockchainBlock(BlockchainNode node) {
        this(node, null);
    }

    public BlockchainBlock() {
        this(null, null);
    }

    public String getHash() {
        return this.hash;
    }

    public abstract Double getAverageFee();

    public abstract Double getAverageFeeRate();

    public abstract Double getLargestFee();

    public abstract Double getSmallestFee();

    public abstract String getLargestTxHash();

    public abstract Long getTransactionCount();

    public abstract int getHeight();

    public abstract Double getDifficulty();

    public abstract String getMerkleRoot();

    public abstract Long getTimestamp();

    public abstract String getBits();

    public abstract int getSize();

    public abstract String getVersionHex();

    public abstract Long getNonce();

    public abstract String getPrevHash();

    public abstract String getNextHash();

    public Double getLargestTxAmount() {
        // TODO Auto-generated method stub
        return null;
    }

    public void persist(BlockchainDB db, Long duration) {
        CallableStatement preparedStatement = null;

        try {
            preparedStatement = db.connection
                    .prepareCall("{CALL AddBlock(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            preparedStatement.setString(BlockColumns.BLOCKCHAIN_TICKER.ordinal(), node.getTicker());
            preparedStatement.setString(BlockColumns.HASH.ordinal(), this.getHash());
            preparedStatement.setLong(BlockColumns.TRANSACTION_COUNT.ordinal(), this.getTransactionCount());
            preparedStatement.setInt(BlockColumns.HEIGHT.ordinal(), this.getHeight());
            preparedStatement.setDouble(BlockColumns.DIFFICULTY.ordinal(), this.getDifficulty());
            preparedStatement.setString(BlockColumns.MERKLE_ROOT.ordinal(), this.getMerkleRoot());
            preparedStatement.setLong(BlockColumns.TIMESTAMP.ordinal(), this.getTimestamp());
            preparedStatement.setString(BlockColumns.BITS.ordinal(), this.getBits());
            preparedStatement.setLong(BlockColumns.SIZE.ordinal(), this.getSize());
            preparedStatement.setString(BlockColumns.VERSION_HEX.ordinal(), this.getVersionHex());
            preparedStatement.setDouble(BlockColumns.NONCE.ordinal(), this.getNonce());
            preparedStatement.setString(BlockColumns.PREV_HASH.ordinal(), this.getPrevHash());
            preparedStatement.setString(BlockColumns.NEXT_HASH.ordinal(), this.getNextHash());
            preparedStatement.setDouble(BlockColumns.AVG_FEE.ordinal(), this.getAverageFee());
            preparedStatement.setDouble(BlockColumns.AVG_FEE_RATE.ordinal(), this.getAverageFeeRate());

            preparedStatement.setString(BlockColumns.LARGEST_TX_HASH.ordinal(), this.getLargestTxHash());
            preparedStatement.setDouble(BlockColumns.LARGEST_TX_AMOUNT.ordinal(), this.getLargestTxAmount());
            preparedStatement.setDouble(BlockColumns.LARGEST_FEE.ordinal(), this.getLargestFee());
            preparedStatement.setDouble(BlockColumns.SMALLEST_FEE.ordinal(), this.getSmallestFee());
            preparedStatement.setLong(BlockColumns.INDEXING_DURATION.ordinal(), duration);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}