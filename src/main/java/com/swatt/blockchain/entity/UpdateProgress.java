package com.swatt.blockchain.entity;

import java.sql.CallableStatement;
/*  =============  DO NOT EDIT ANY OF THIS FILE (UNLESS YOU REALLY WANT TO)  ================= 
 * 
 *   THIS IS MANUALLY-GENERATED CODE WAS CREATED LIKE gerrySeidman.tools.sql.ExcelSqlCodegen
 * 
 *  =================================  EDIT ANY OF THIS FILE  ================================ 
 */
import java.sql.Connection;
import java.sql.SQLException;

public class UpdateProgress {
    private static String getStandardProcedureName() {
        return "UpdateProgress";
    }

    private static String getProcedureParamMask() {
        return "?, ?, ?";
    }

    private static String CALL_QUERY = "CALL " + getStandardProcedureName() + "(" + getProcedureParamMask() + ")";

    public static void call(Connection connection, String blockchainCode, String startingBlockHash, int limitBlockCount)
            throws SQLException {
        CallableStatement cs = connection.prepareCall(CALL_QUERY);

        cs.setString(1, blockchainCode);
        cs.setString(2, startingBlockHash);
        cs.setInt(3, limitBlockCount);

        cs.execute();
    }
}