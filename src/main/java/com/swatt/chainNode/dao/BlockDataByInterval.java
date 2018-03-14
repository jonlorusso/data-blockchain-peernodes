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

public class BlockDataByInterval {
    private String blockchainCode;
    private long fromTimestamp;
    private long toTimestamp;

    private double avgFee;
    private double avgFeeRate;
    private double largestFee;
    private double smallestFee;
    private int transactionCount;
    private int avgTransactionCount;
    private int blockCount;

    public BlockDataByInterval(String blockchainCode, long fromTimestamp, long toTimestamp) {
        this.blockchainCode = blockchainCode;
        this.fromTimestamp = fromTimestamp;
        this.toTimestamp = toTimestamp;
    }

    public final String getBlockchainCode() {
        return blockchainCode;
    }

    public final long getFromTimestamp() {
        return fromTimestamp;
    }

    public final long getToTimestamp() {
        return toTimestamp;
    }

    public final double getAvgFee() {
        return avgFee;
    }

    public final double getAvgFeeRate() {
        return avgFeeRate;
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

    public final void setAvgFee(double avgFee) {
        this.avgFee = avgFee;
    }

    public final void setAvgFeeRate(double avgFeeRate) {
        this.avgFeeRate = avgFeeRate;
    }

    public final void setLargestFee(double largestFee) {
        this.largestFee = largestFee;
    }

    public final void setSmallestFee(double smallestFee) {
        this.smallestFee = smallestFee;
    }

    public final void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }

    public final void setAvgTransactionCount(int avgTransactionCount) {
        this.avgTransactionCount = avgTransactionCount;
    }

    public final void setBlockCount(int blockCount) {
        this.blockCount = blockCount;
    }

    private static String getStandardProcedureName() {
        return "BlockDataByInterval";
    }

    private static String getProcedureParamMask() {
        return "?, ?, ?";
    }

    public static String repeat(String str, int times) {
        return new String(new char[times]).replace("\0", str);
    }

    public BlockDataByInterval(ResultSet rs) throws SQLException {
        avgFee = rs.getDouble(1);
        avgFeeRate = rs.getDouble(2);
        largestFee = rs.getDouble(3);
        smallestFee = rs.getDouble(4);
        transactionCount = rs.getInt(5);
        avgTransactionCount = rs.getInt(6);
        blockCount = rs.getInt(7);
    }

    private static String CALL_QUERY = "CALL " + getStandardProcedureName() + "(" + getProcedureParamMask() + ")";

    public static BlockDataByInterval call(Connection connection, String blockchainCode, long fromTimestamp,
            long toTimestamp) throws SQLException {
        System.out.println(CALL_QUERY);

        CallableStatement cs = connection.prepareCall(CALL_QUERY);

        cs.setString(1, blockchainCode);
        cs.setLong(2, fromTimestamp);
        cs.setLong(3, toTimestamp);

        ResultSet rs = cs.executeQuery();

        if (rs.next())
            return new BlockDataByInterval(rs);
        else
            return null;
    }
}