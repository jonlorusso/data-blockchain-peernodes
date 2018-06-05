package com.swatt.blockchain.entity;

import java.sql.CallableStatement;
/*  =============  DO NOT EDIT ANY OF THIS FILE (UNLESS YOU REALLY WANT TO)  ================= 
 * 
 *   THIS IS MANUALLY-GENERATED CODE WAS CREATED LIKE gerrySeidman.tools.sql.ExcelSqlCodegen
 * 
 *  =================================  EDIT ANY OF THIS FILE  ================================ 
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ApiBlockData {
    private String blockchainName;
    private String blockHash;
    private String prevBlock;
    private int timestamp;
    private int height;
    private int transactionCount;

    private double difficulty;
    private double reward;
    private String merkleRoot;
    private String bits;
    private int size;
    private long nonce;

    private double avgFee;
    private double avgFeeRate;
    private double largestFee;
    private double smallestFee;

    private String largestTxHash;
    private double largestTxAmount;

    public final double getReward() {
        return reward;
    }

    public final double getDifficulty() {
        return difficulty;
    }

    public final String getMerkleRoot() {
        return merkleRoot;
    }

    public final String getBits() {
        return bits;
    }

    public final double getSize() {
        return size;
    }

    public final String getLargestTxHash() {
        return largestTxHash;
    }

    public final double getLargestTxAmount() {
        return largestTxAmount;
    }

    public final long getNonce() {
        return nonce;
    }

    public final double getAvgFee() {
        return avgFee;
    }

    public final double getAvgFeeRate() {
        return avgFeeRate;
    }

    public final String getBlockchainName() {
        return blockchainName;
    }

    public final int getTimestamp() {
        return timestamp;
    }

    public final int getHeight() {
        return height;
    }

    public final String getBlockHash() {
        return blockHash;
    }

    public final String getPrevBlock() {
        return prevBlock;
    }

    public final double getLargestFee() {
        return largestFee;
    }

    public final double getSmallestFee() {
        return smallestFee;
    }

    public final int getTransactionCount() {
        return transactionCount;
    }

    private static String getStandardProcedureName() {
        return "GetBlock(?, ?)";
    }

    private static String getListProcedureName() {
        return "GetBlocks(?, ?, ?)";
    }

    public ApiBlockData(ResultSet rs) throws SQLException {
        blockchainName = rs.getString(1);
        blockHash = rs.getString(2);
        prevBlock = rs.getString(3);
        timestamp = rs.getInt(4);
        height = rs.getInt(5);
        transactionCount = rs.getInt(6);

        difficulty = rs.getDouble(7);
        reward = rs.getDouble(8);
        merkleRoot = rs.getString(9);
        bits = rs.getString(10);
        size = rs.getInt(11);
        nonce = rs.getLong(12);

        avgFee = rs.getDouble(13);
        avgFeeRate = rs.getDouble(14);
        largestFee = rs.getDouble(15);
        smallestFee = rs.getDouble(16);
        largestTxHash = rs.getString(17);
        largestTxAmount = rs.getDouble(18);
    }

    private static String BY_HASH_QUERY = "CALL GetBlockByHash(?, ?)";
    private static String BY_HEIGHT_QUERY = "CALL GetBlockByHeight(?, ?)";
    private static String LIST_QUERY = "CALL GetBlocks(?, ?, ?)";

    public static ApiBlockData fetchByHash(Connection connection, String blockchainCode, String blockHash) throws SQLException {
        CallableStatement cs = connection.prepareCall(BY_HASH_QUERY);

        cs.setString(1, blockchainCode);
        cs.setString(2, blockHash);

        ResultSet rs = cs.executeQuery();

        if (rs.next())
            return new ApiBlockData(rs);
        else
            return null;
    }

    public static ApiBlockData fetchByHeight(Connection connection, String blockchainCode, long blockHeight) throws SQLException {
        CallableStatement cs = connection.prepareCall(BY_HEIGHT_QUERY);

        cs.setString(1, blockchainCode);
        cs.setLong(2, blockHeight);

        System.out.println("3" + blockchainCode);

        ResultSet rs = cs.executeQuery();

        if (rs.next())
            return new ApiBlockData(rs);
        else
            return null;
    }

    public static ArrayList<ApiBlockData> call(Connection connection, String blockchainCode, long fromTimestamp, long toTimestamp) throws SQLException {
        CallableStatement cs = connection.prepareCall(LIST_QUERY);

        cs.setString(1, blockchainCode);
        cs.setLong(2, fromTimestamp);
        cs.setLong(3, toTimestamp);

        System.out.println(blockchainCode);

        ResultSet rs = cs.executeQuery();

        ArrayList<ApiBlockData> results = new ArrayList<ApiBlockData>(100);

        while (rs.next())
            results.add(new ApiBlockData(rs));

        return results;
    }
}