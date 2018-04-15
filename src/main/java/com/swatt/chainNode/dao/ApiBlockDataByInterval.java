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

public class ApiBlockDataByInterval {
    private String blockchainName;
    private double avgReward;
    private double avgFee;
    private double avgFeeRate;
    private double largestFee;
    private double smallestFee;
    private int transactionCount;
    private int avgTransactionCount;
    private int blockCount;
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

    public final double getFromTimestamp() {
        return fromTimestamp;
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

    private static String getStandardProcedureName() {
        return "BlockDataByInterval";
    }

    private static String getProcedureParamMask() {
        return "?, ?, ?";
    }

    public ApiBlockDataByInterval(ResultSet rs) throws SQLException {
        blockchainName = rs.getString(1);
        fromTimestamp = rs.getInt(2);
        toTimestamp = rs.getInt(3);
        avgReward = rs.getDouble(4);
        avgFee = rs.getDouble(5);
        avgFeeRate = rs.getDouble(6);
        largestFee = rs.getDouble(7);
        smallestFee = rs.getDouble(8);
        transactionCount = rs.getInt(9);
        avgTransactionCount = rs.getInt(10);
        blockCount = rs.getInt(11);
    }

    private static String CALL_QUERY = "CALL " + getStandardProcedureName() + "(" + getProcedureParamMask() + ")";

    public static ApiBlockDataByInterval call(Connection connection, String blockchainCode, long fromTimestamp,
            long toTimestamp) throws SQLException {
        CallableStatement cs = connection.prepareCall(CALL_QUERY);

        cs.setString(1, blockchainCode);
        cs.setLong(2, fromTimestamp);
        cs.setLong(3, toTimestamp);

        ResultSet rs = cs.executeQuery();

        if (rs.next())
            return new ApiBlockDataByInterval(rs);
        else
            return null;
    }
}