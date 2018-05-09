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

public class ApiTime {
    private long time;

    public final long getTime() {
        return time;
    }

    private static String getProcedureName() {
        return "Time";
    }

    public ApiTime(ResultSet rs) throws SQLException {
        time = rs.getLong(1);
    }

    private static String QUERY = "CALL " + getProcedureName() + "()";

    public static ApiTime authCredentials(Connection connection) throws SQLException {
        CallableStatement cs = connection.prepareCall(QUERY);

        ResultSet rs = cs.executeQuery();

        if (rs.next())
            return new ApiTime(rs);
        else
            return null;
    }
}