package com.swatt.blockchain.persistence;

import java.sql.CallableStatement;
import java.sql.SQLException;

import com.swatt.blockchain.BlockchainBlock;

public class Ingestor {
    private static BlockchainDB db;
    public static final String DEFAULT_BLOCKCHAIN_TICKER = "btc";

    public static void main(String[] args) {
        String ticker = null;

        db = new BlockchainDB();

        if (args.length == 0)
            ticker = DEFAULT_BLOCKCHAIN_TICKER;
        else
            ticker = args[0];

        com.swatt.blockchain.BlockchainNode blockchain = null;

        switch (ticker) {
        case "btc":
            blockchain = new com.swatt.blockchain.btc.BlockchainNode();
            break;
        case "eth":
            blockchain = new com.swatt.blockchain.eth.BlockchainNode();
            break;
        }

        // BlockchainTransaction tx = null;
        BlockchainBlock block = null;

        try {
            block = blockchain.findBlockByHash("0000000000000000007962066dcd6675830883516bcf40047d42740a85eb2919");

            persistBlock(ticker, block);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void persistBlock(String ticker, BlockchainBlock block) {
        CallableStatement preparedStatement = null;

        try {
            preparedStatement = db.connection.prepareCall(
                    "{CALL AddBlock(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            preparedStatement.setString(1, ticker);
            preparedStatement.setString(2, block.getHash());
            preparedStatement.setLong(3, block.getTransactionCount());
            preparedStatement.setInt(4, block.getHeight());
            preparedStatement.setDouble(5, block.getDifficulty());
            preparedStatement.setString(6, block.getMerkleRoot());
            preparedStatement.setLong(7, block.getTimeStamp());
            preparedStatement.setString(8, block.getBits());
            preparedStatement.setLong(9, block.getSize());
            preparedStatement.setString(10, block.getVersionHex());
            preparedStatement.setDouble(11, block.getNonce());
            preparedStatement.setString(12, block.getPrevHash());
            preparedStatement.setString(13, block.getNextHash());
            preparedStatement.setDouble(14, block.getAverageFee());

            preparedStatement.setString(15, block.getLargestTxHash());
            preparedStatement.setDouble(16, block.getLargestTxValue());
            preparedStatement.setLong(17, block.getLargestTxTimestamp());
            preparedStatement.setLong(18, block.getTotalSize());
            preparedStatement.setDouble(19, block.getTotalFee());
            preparedStatement.setDouble(20, block.getLargestFee());
            preparedStatement.setDouble(21, block.getSmallestFee());
            preparedStatement.setLong(22, block.getFirstTimestamp());
            preparedStatement.setLong(23, block.getLastTimestamp());

            preparedStatement.executeUpdate();

            System.out.println(BlockColumns.HASH);

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
