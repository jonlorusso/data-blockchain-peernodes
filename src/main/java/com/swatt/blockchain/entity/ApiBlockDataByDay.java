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

public class ApiBlockDataByDay {
    private static final String QUERY = "CALL DailyBlockDataByDay(?, ?, ?)";

    private String blockchainName;
    private double avgReward;
    private double avgFee;
    private double avgFeeRate;
    private double avgDifficulty;
    private double largestFee;
    private double smallestFee;
    private int transactionCount;
    private int avgTransactionCount;
    private int blockCount;
    private String dayTimestamp;
    private int fromTimestamp;
    private int toTimestamp;

    public final double getAvgReward() {
        return avgReward;
    }

    public final double getAvgFee() {
        return avgFee;
    }

    public final String getBlockchainName() {
        return blockchainName;
    }

    public final double getAvgFeeRate() {
        return avgFeeRate;
    }

    public final double getAvgDifficulty() {
        return avgDifficulty;
    }

    public final double getFromTimestamp() {
        return fromTimestamp;
    }

    public final String getDatetime() {
        return dayTimestamp;
    }

    public final double getToTimestamp() {
        return toTimestamp;
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

    public final int getAvgTransactionCount() {
        return avgTransactionCount;
    }

    public final int getBlockCount() {
        return blockCount;
    }

    public ApiBlockDataByDay(ResultSet rs) throws SQLException {
        blockchainName = rs.getString(1);
        dayTimestamp = rs.getString(2);
        fromTimestamp = rs.getInt(3);
        toTimestamp = rs.getInt(4);
        avgReward = rs.getDouble(5);
        avgFee = rs.getDouble(6);
        avgFeeRate = rs.getDouble(7);
        transactionCount = rs.getInt(8);
        avgTransactionCount = rs.getInt(9);
        blockCount = rs.getInt(10);
        avgDifficulty = rs.getDouble(11);
    }

    public static ArrayList<ApiBlockDataByDay> call(Connection connection, String blockchainCode, long fromTimestamp, long toTimestamp) throws SQLException {
        CallableStatement cs = connection.prepareCall(QUERY);

        cs.setString(1, blockchainCode);
        cs.setLong(2, fromTimestamp);
        cs.setLong(3, toTimestamp);

        ResultSet rs = cs.executeQuery();

        ArrayList<ApiBlockDataByDay> results = new ArrayList<ApiBlockDataByDay>(100);

        while (rs.next())
            results.add(new ApiBlockDataByDay(rs));

        return results;
    }
}