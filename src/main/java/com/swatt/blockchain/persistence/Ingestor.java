package com.swatt.blockchain.persistence;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.swatt.blockchain.BlockchainBlock;
import com.swatt.blockchain.NodePicker;

public class Ingestor {
    static int blockFetchCountdown;
    static String blockchainTicker;
    static com.swatt.blockchain.BlockchainNode blockchain;
    private static BlockchainDB db;
    public static final String DEFAULT_BLOCKCHAIN_TICKER = "btc";

    public static void main(String[] args) {
        db = new BlockchainDB();

        if (args.length == 0)
            blockchainTicker = DEFAULT_BLOCKCHAIN_TICKER;
        else
            blockchainTicker = args[0];

        new NodePicker();
        blockchain = NodePicker.getBlockchain(blockchainTicker);

        try {
            startIngestion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startIngestion() {
        CallableStatement preparedStatement = null;

        try {
            preparedStatement = db.connection.prepareCall("{CALL CheckProgress(?, ?, ?)}");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            preparedStatement.setString(BlockProgressColumns.BLOCKCHAIN_TICKER.ordinal(), blockchainTicker);
            preparedStatement.registerOutParameter(BlockProgressColumns.START_BLOCK_HASH.ordinal(), Types.VARCHAR);
            preparedStatement.registerOutParameter(BlockProgressColumns.BLOCK_COUNT.ordinal(), Types.INTEGER);

            preparedStatement.execute();

            String startBlockHash = preparedStatement.getString(BlockProgressColumns.START_BLOCK_HASH.ordinal());
            int blockCount = preparedStatement.getInt(BlockProgressColumns.BLOCK_COUNT.ordinal());

            blockFetchCountdown = blockCount;
            fetchBlock(startBlockHash);
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

    private static void fetchBlock(String blockHash) {
        BlockchainBlock block = null;
        Long startTime = null;
        Long duration = null;

        blockFetchCountdown--;

        try {
            startTime = System.currentTimeMillis() / 1000;
            block = blockchain.findBlockByHash(blockHash);
            duration = (System.currentTimeMillis() / 1000) - startTime;

            block.persist(db, duration);
            updateProgress(blockHash);

            if (blockFetchCountdown > 0) {
                fetchBlock(block.getPrevHash());
            } else {
                if (db.connection != null) {
                    try {
                        db.connection.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateProgress(String blockHash) {
        CallableStatement preparedStatement = null;

        try {
            preparedStatement = db.connection.prepareCall("{CALL UpdateProgress(?, ?, ?)}");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            preparedStatement.setString(BlockProgressColumns.BLOCKCHAIN_TICKER.ordinal(), blockchainTicker);
            preparedStatement.setString(BlockProgressColumns.START_BLOCK_HASH.ordinal(), blockHash);
            preparedStatement.setInt(BlockProgressColumns.BLOCK_COUNT.ordinal(), blockFetchCountdown);

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
