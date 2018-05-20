package com.swatt.blockchain.entity;

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

public class ApiPair {
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

    public ApiPair(ResultSet rs) throws SQLException {
        fromCcy = rs.getString(1);
        toCcy = rs.getString(2);
        fromDate = rs.getDate(3);
        toDate = rs.getDate(4);
    }

    private static String CALL_QUERY = "CALL " + getStandardProcedureName() + "()";

    public static ArrayList<ApiPair> call(Connection connection) throws SQLException {
        CallableStatement cs = connection.prepareCall(CALL_QUERY);

        ResultSet rs = cs.executeQuery();

        ArrayList<ApiPair> results = new ArrayList<ApiPair>(100);

        while (rs.next())
            results.add(new ApiPair(rs));

        return results;
    }
}