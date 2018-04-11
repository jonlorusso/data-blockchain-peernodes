package com.swatt.chainNode.dao;

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

public class APIBlockData {
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

    private static String getStandardProcedureName(boolean List) {
        if (List)
            return "GetBlocks";
        else
            return "GetBlock";
    }

    private static String getProcedureParamMask(boolean List) {
        if (List)
            return "?, ?, ?";
        else
            return "?, ?";
    }

    public APIBlockData(ResultSet rs) throws SQLException {
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

    private static String CALL_QUERY = "CALL " + getStandardProcedureName(false) + "(" + getProcedureParamMask(false)
            + ")";
    private static String LIST_QUERY = "CALL " + getStandardProcedureName(true) + "(" + getProcedureParamMask(true)
            + ")";

    public static APIBlockData call(Connection connection, String blockchainCode, String blockHash)
            throws SQLException {
        CallableStatement cs = connection.prepareCall(CALL_QUERY);

        cs.setString(1, blockchainCode);
        cs.setString(2, blockHash);

        ResultSet rs = cs.executeQuery();

        if (rs.next())
            return new APIBlockData(rs);
        else
            return null;
    }

    public static ArrayList<APIBlockData> call(Connection connection, String blockchainCode, long fromTimestamp,
            long toTimestamp) throws SQLException {
        CallableStatement cs = connection.prepareCall(LIST_QUERY);

        cs.setString(1, blockchainCode);
        cs.setLong(2, fromTimestamp);
        cs.setLong(3, toTimestamp);

        ResultSet rs = cs.executeQuery();

        ArrayList<APIBlockData> results = new ArrayList<APIBlockData>(100);

        while (rs.next())
            results.add(new APIBlockData(rs));

        return results;
    }
}