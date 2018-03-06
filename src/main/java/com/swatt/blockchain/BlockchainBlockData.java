package com.swatt.blockchain;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.swatt.blockchain.persistence.BlockchainDB;

public class BlockchainBlockData {
    private int height;
    private Double averageFee;
    private Double averageFeeRate;
    private Double largestFee;
    private Double smallestFee;
    private Double difficulty;
    private Double largestTxAmount;
    private String largestTxHash;
    private Long transactionCount;
    private Long timestamp;
    private Long size;

    private String prevhash;
    private String nexthash;

    public BlockchainBlockData(BlockchainNode node, String blockHash) {
        BlockchainDB db = new BlockchainDB();
        String blockchainTicker = node.getTicker();

        CallableStatement preparedStatement = null;

        try {
            preparedStatement = db.connection.prepareCall("{CALL BlockInfo(?, ?)}");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            preparedStatement.setString(BlockInfoColumns.BLOCKCHAIN_TICKER.ordinal(), blockchainTicker);
            preparedStatement.setString(BlockInfoColumns.HASH.ordinal(), blockHash);

            ResultSet recordsetBlock = preparedStatement.executeQuery();

            while (recordsetBlock.next()) {
                this.averageFee = recordsetBlock.getDouble(BlockInfoColumns.AVG_FEE.toString());
                this.averageFeeRate = recordsetBlock.getDouble(BlockInfoColumns.AVG_FEE_RATE.toString());
                this.largestFee = recordsetBlock.getDouble(BlockInfoColumns.LARGEST_FEE.toString());
                this.smallestFee = recordsetBlock.getDouble(BlockInfoColumns.SMALLEST_FEE.toString());
                this.largestTxHash = recordsetBlock.getString(BlockInfoColumns.LARGEST_TX_HASH.toString());
                this.transactionCount = recordsetBlock.getLong(BlockInfoColumns.TRANSACTION_COUNT.toString());
                this.height = recordsetBlock.getInt(BlockInfoColumns.HEIGHT.toString());
                this.difficulty = recordsetBlock.getDouble(BlockInfoColumns.DIFFICULTY.toString());
                this.timestamp = recordsetBlock.getLong(BlockInfoColumns.TIMESTAMP.toString());
                this.size = recordsetBlock.getLong(BlockInfoColumns.SIZE.toString());
                this.prevhash = recordsetBlock.getString(BlockInfoColumns.PREV_HASH.toString());
                this.nexthash = recordsetBlock.getString(BlockInfoColumns.NEXT_HASH.toString());
            }
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

    public Double getAverageFee() {
        return this.averageFee;
    }

    public Double getAverageFeeRate() {
        return this.averageFeeRate;
    }

    public Double getLargestFee() {
        return this.largestFee;
    }

    public Double getSmallestFee() {
        return this.smallestFee;
    }

    public String getLargestTxHash() {
        return this.largestTxHash;
    }

    public Long getTransactionCount() {
        return this.transactionCount;
    }

    public int getHeight() {
        return this.height;
    }

    public Double getDifficulty() {
        return this.difficulty;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public Long getSize() {
        return this.size;
    }

    public String getPrevHash() {
        return this.prevhash;
    }

    public String getNextHash() {
        return this.nexthash;
    }

}
