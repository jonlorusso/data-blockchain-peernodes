package com.swatt.blockchain;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.swatt.blockchain.persistence.BlockchainDB;

/**
 * BlockchainNodeData is the result of a query about transactions over a
 * specified period of time
 */
public class BlockchainNodeData {
    int numBlocks;
    int transactionCount;
    int averageTransactionCount;
    double largestFee;
    double smallestFee;
    double averageFee;
    double averageFeeRate;

    public BlockchainNodeData(BlockchainNode node, Long fromTime, Long toTime) {
        BlockchainDB db = new BlockchainDB();
        String blockchainTicker = node.getTicker();

        CallableStatement preparedStatement = null;

        try {
            preparedStatement = db.connection.prepareCall("{CALL BlockData(?, ?, ?)}");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            preparedStatement.setString(BlockDataRequestColumns.BLOCKCHAIN_TICKER.ordinal(), blockchainTicker);
            preparedStatement.setLong(BlockDataRequestColumns.FROM.ordinal(), fromTime);
            preparedStatement.setLong(BlockDataRequestColumns.TO.ordinal(), toTime);

            ResultSet recordsetBlock = preparedStatement.executeQuery();

            while (recordsetBlock.next()) {
                this.averageFee = recordsetBlock.getDouble(BlockDataColumns.AVG_FEE.toString());
                this.averageFeeRate = recordsetBlock.getDouble(BlockDataColumns.AVG_FEE_RATE.toString());
                this.largestFee = recordsetBlock.getDouble(BlockDataColumns.LARGEST_FEE.toString());
                this.smallestFee = recordsetBlock.getDouble(BlockDataColumns.SMALLEST_FEE.toString());
                this.transactionCount = recordsetBlock.getInt(BlockDataColumns.TRANSACTION_COUNT.toString());
                this.averageTransactionCount = recordsetBlock.getInt(BlockDataColumns.AVG_TRANSACTION_COUNT.toString());
                this.numBlocks = recordsetBlock.getInt(BlockDataColumns.NUM_BLOCKS.toString());
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

    public int getNumBlocks() {
        return numBlocks;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public double getLargestFee() {
        return largestFee;
    }

    public double getSmallestFee() {
        return smallestFee;
    }

    public double getAverageFee() {
        return averageFee;
    }

    public double getAverageFeeRate() {
        return averageFeeRate;
    }

    public int getAverageTransactionCount() {
        return averageTransactionCount;
    }

}
