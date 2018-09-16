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
import java.sql.Types;

import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;

public class CheckProgress {
    private static String startingBlockHash;
    private static int limitBlockCount;

    public final String getBlockHash() {
        return startingBlockHash;
    }

    public final int getBlockCount() {
        return limitBlockCount;
    }

    private static String getStandardProcedureName() {
        return "CheckProgress";
    }

    private static String getProcedureParamMask() {
        return "?, ?, ?";
    }

    public CheckProgress(String blockHash, int blockCount) {
        startingBlockHash = blockHash;
        limitBlockCount = blockCount;
    }

    private static String CALL_QUERY = "CALL " + getStandardProcedureName() + "(" + getProcedureParamMask() + ")";

    public static CheckProgress call(Connection connection, String blockchainCode) throws SQLException {
        CallableStatement cs = connection.prepareCall(CALL_QUERY);

        cs.setString(1, blockchainCode);
        cs.registerOutParameter(2, Types.VARCHAR);
        cs.registerOutParameter(3, Types.INTEGER);

        cs.execute();

        return new CheckProgress(cs.getString(2), cs.getInt(3));
    }
}