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

        blockFetchCountdown--;

        try {
            block = blockchain.findBlockByHash(blockHash);

            persistBlock(block);

            if (blockFetchCountdown > 0) {
                fetchBlock(block.getPrevHash());
            } else {
                updateProgress(block.getPrevHash());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void persistBlock(BlockchainBlock block) {
        CallableStatement preparedStatement = null;

        try {
            preparedStatement = db.connection.prepareCall(
                    "{CALL AddBlock(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
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
            preparedStatement.setLong(BlockColumns.TIMESTAMP.ordinal(), block.getTimeStamp());
            preparedStatement.setString(BlockColumns.BITS.ordinal(), block.getBits());
            preparedStatement.setLong(BlockColumns.SIZE.ordinal(), block.getSize());
            preparedStatement.setString(BlockColumns.VERSION_HEX.ordinal(), block.getVersionHex());
            preparedStatement.setDouble(BlockColumns.NONCE.ordinal(), block.getNonce());
            preparedStatement.setString(BlockColumns.PREV_HASH.ordinal(), block.getPrevHash());
            preparedStatement.setString(BlockColumns.NEXT_HASH.ordinal(), block.getNextHash());
            preparedStatement.setDouble(BlockColumns.AVG_FEE.ordinal(), block.getAverageFee());

            preparedStatement.setString(BlockColumns.LARGEST_TX_HASH.ordinal(), block.getLargestTxHash());
            preparedStatement.setDouble(BlockColumns.LARGEST_TX_VALUE.ordinal(), block.getLargestTxValue());
            preparedStatement.setLong(BlockColumns.LARGEST_TX_TIMESTAMP.ordinal(), block.getLargestTxTimestamp());
            preparedStatement.setLong(BlockColumns.TOTAL_SIZE.ordinal(), block.getTotalSize());
            preparedStatement.setDouble(BlockColumns.TOTAL_FEE.ordinal(), block.getTotalFee());
            preparedStatement.setDouble(BlockColumns.LARGEST_FEE.ordinal(), block.getLargestFee());
            preparedStatement.setDouble(BlockColumns.SMALLEST_FEE.ordinal(), block.getSmallestFee());
            preparedStatement.setLong(BlockColumns.FIRST_TX_TIMESTAMP.ordinal(), block.getFirstTimestamp());
            preparedStatement.setLong(BlockColumns.LAST_TX_TIMESTAMP.ordinal(), block.getLastTimestamp());

            preparedStatement.executeUpdate();

            System.out.println("Record is inserted into BLOCKS table");
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
            preparedStatement = db.connection.prepareCall("{CALL UpdateProgress(?, ?)}");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            preparedStatement.setString(UpdateProgressColumns.BLOCKCHAIN_TICKER.ordinal(), blockchainTicker);
            preparedStatement.setString(UpdateProgressColumns.BLOCK_HASH.ordinal(), blockHash);

            preparedStatement.executeUpdate();

            System.out.println("Progress updated");
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

            if (db.connection != null) {
                try {
                    db.connection.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
