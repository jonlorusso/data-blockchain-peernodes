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

public class APIPair {
    private String fromCcy;
    private String toCcy;
    private Date fromDate;
    private Date toDate;

    public final String getFromCcy() {
        return fromCcy;
    }

    public final String getToCcy() {
        return toCcy;
    }

    public final Date getFromDate() {
        return fromDate;
    }

    public final Date getToDate() {
        return toDate;
    }

    private static String getStandardProcedureName() {
        return "GetPairs";
    }

    public APIPair(ResultSet rs) throws SQLException {
        fromCcy = rs.getString(1);
        toCcy = rs.getString(2);
        fromDate = rs.getDate(3);
        toDate = rs.getDate(4);
    }

    private static String CALL_QUERY = "CALL " + getStandardProcedureName() + "()";

    public static ArrayList<APIPair> call(Connection connection) throws SQLException {
        CallableStatement cs = connection.prepareCall(CALL_QUERY);

        ResultSet rs = cs.executeQuery();

        ArrayList<APIPair> results = new ArrayList<APIPair>(100);

        while (rs.next())
            results.add(new APIPair(rs));

        return results;
    }
}