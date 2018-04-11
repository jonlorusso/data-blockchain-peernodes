package com.swatt.chainNode.dao;

import java.sql.CallableStatement;
/*  =============  DO NOT EDIT ANY OF THIS FILE (UNLESS YOU REALLY WANT TO)  ================= 
 * 
 *   THIS IS MANUALLY-GENERATED CODE WAS CREATED LIKE gerrySeidman.tools.sql.ExcelSqlCodegen
 * 
 *  =================================  EDIT ANY OF THIS FILE  ================================ 
 */
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class APIRateDay {
    private String datasource;
    private String fromCcy;
    private String toCcy;
    private Date day;
    private double open;
    private double close;
    private double high;
    private double low;
    private long volume;
    private long marketCap;

    public final String getDatasource() {
        return datasource;
    }

    public final String getFromCcy() {
        return fromCcy;
    }

    public final String getToCcy() {
        return toCcy;
    }

    public final Date getDay() {
        return day;
    }

    public final double getOpen() {
        return open;
    }

    public final double getClose() {
        return close;
    }

    public final double getHigh() {
        return high;
    }

    public final double getLow() {
        return low;
    }

    public final long getVolume() {
        return volume;
    }

    public final long getMarketCap() {
        return marketCap;
    }

    private static String getStandardProcedureName(boolean List) {
        if (List)
            return "GetRateDays";
        else
            return "GetRateDay";
    }

    private static String getProcedureParamMask(boolean List) {
        if (List)
            return "?, ?, ?, ?";
        else
            return "?, ?, ?";
    }

    public APIRateDay(ResultSet rs) throws SQLException {
        datasource = rs.getString(1);
        fromCcy = rs.getString(2);
        toCcy = rs.getString(3);
        day = rs.getDate(4);

        open = rs.getDouble(5);
        close = rs.getDouble(6);
        high = rs.getDouble(7);
        low = rs.getDouble(8);
        volume = rs.getLong(9);
        marketCap = rs.getLong(10);
    }

    private static String CALL_QUERY = "CALL " + getStandardProcedureName(false) + "(" + getProcedureParamMask(false)
            + ")";
    private static String LIST_QUERY = "CALL " + getStandardProcedureName(true) + "(" + getProcedureParamMask(true)
            + ")";

    public static APIRateDay call(Connection connection, String fromCcy, String toCcy, Date day) throws SQLException {
        CallableStatement cs = connection.prepareCall(CALL_QUERY);

        cs.setString(1, fromCcy);
        cs.setString(2, toCcy);
        cs.setDate(3, day);

        ResultSet rs = cs.executeQuery();

        if (rs.next())
            return new APIRateDay(rs);
        else
            return null;
    }

    public static ArrayList<APIRateDay> call(Connection connection, String fromCcy, String toCcy, Date fromDay,
            Date toDay) throws SQLException {
        CallableStatement cs = connection.prepareCall(LIST_QUERY);

        cs.setString(1, fromCcy);
        cs.setString(2, toCcy);
        cs.setDate(3, fromDay);
        cs.setDate(4, toDay);

        ResultSet rs = cs.executeQuery();

        ArrayList<APIRateDay> results = new ArrayList<APIRateDay>(100);

        while (rs.next())
            results.add(new APIRateDay(rs));

        return results;
    }
}