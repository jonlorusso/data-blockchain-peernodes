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
    private double avgFee;
    private double avgFeeRate;
    private double largestFee;
    private double smallestFee;
    private int transactionCount;
    private int avgTransactionCount;
    private int blockCount;

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

    private static String getStandardProcedureName() {
        return "BlockDataByInterval";
    }

    private static String getProcedureParamMask() {
        return "?, ?, ?";
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