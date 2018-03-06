package com.swatt.blockchain.persistence;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.swatt.blockchain.BlockchainBlock;

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

        switch (blockchainTicker) {
        case "btc":
            blockchain = new com.swatt.blockchain.btc.BlockchainNode();
            break;
        case "eth":
            blockchain = new com.swatt.blockchain.eth.BlockchainNode();
            break;
        }

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
            // TODO Auto-generated catch block
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

            persistBlock(block, duration);

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

    private static void persistBlock(BlockchainBlock block, Long duration) {
        CallableStatement preparedStatement = null;

        try {
            preparedStatement = db.connection
                    .prepareCall("{CALL AddBlock(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            preparedStatement.setString(BlockColumns.BLOCKCHAIN_TICKER.ordinal(), blockchainTicker);
            preparedStatement.setString(BlockColumns.HASH.ordinal(), block.getHash());
            preparedStatement.setLong(BlockColumns.TRANSACTION_COUNT.ordinal(), block.getTransactionCount());
            preparedStatement.setInt(BlockColumns.HEIGHT.ordinal(), block.getHeight());
            preparedStatement.setDouble(BlockColumns.DIFFICULTY.ordinal(), block.getDifficulty());
            preparedStatement.setString(BlockColumns.MERKLE_ROOT.ordinal(), block.getMerkleRoot());
            preparedStatement.setLong(BlockColumns.TIMESTAMP.ordinal(), block.getTimestamp());
            preparedStatement.setString(BlockColumns.BITS.ordinal(), block.getBits());
            preparedStatement.setLong(BlockColumns.SIZE.ordinal(), block.getSize());
            preparedStatement.setString(BlockColumns.VERSION_HEX.ordinal(), block.getVersionHex());
            preparedStatement.setDouble(BlockColumns.NONCE.ordinal(), block.getNonce());
            preparedStatement.setString(BlockColumns.PREV_HASH.ordinal(), block.getPrevHash());
            preparedStatement.setString(BlockColumns.NEXT_HASH.ordinal(), block.getNextHash());
            preparedStatement.setDouble(BlockColumns.AVG_FEE.ordinal(), block.getAverageFee());
            preparedStatement.setDouble(BlockColumns.AVG_FEE_RATE.ordinal(), block.getAverageFeeRate());

            preparedStatement.setString(BlockColumns.LARGEST_TX_HASH.ordinal(), block.getLargestTxHash());
            preparedStatement.setDouble(BlockColumns.LARGEST_TX_AMOUNT.ordinal(), block.getLargestTxAmount());
            preparedStatement.setDouble(BlockColumns.LARGEST_FEE.ordinal(), block.getLargestFee());
            preparedStatement.setDouble(BlockColumns.SMALLEST_FEE.ordinal(), block.getSmallestFee());
            preparedStatement.setLong(BlockColumns.INDEXING_DURATION.ordinal(), duration);

            preparedStatement.executeUpdate();

            updateProgress(block.getPrevHash());

            System.out.println(block.getHash() + " block inserted into DB");
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
