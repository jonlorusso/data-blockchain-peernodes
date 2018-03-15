package com.swatt.chainNode.dao;

/*  =============================  DO NOT EDIT ANY OF THIS FILE  ============================= 
 * 
 *     THIS IS AUTO-GENERATED CODE WAS CREATED BY gerrySeidman.tools.sql.ExcelSqlCodegen
 *
 *     Based on Excel File: /Users/gloverwilson/eclipse-workspace/internal-blockchain-access/files/Blockchain Node Schema.xls
 * 
 *  =============================  DO NOT EDIT ANY OF THIS FILE  ============================= 
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class BlockchainNodeInfo {
    private int id;
    private String code;
    private String forkCode;
    private String name;
    private String description;
    private String units;
    private String txFeeUnits;
    private String ip;
    private int port;
    private int forwardedPort;
    private String rpcUn;
    private String rpcPw;
    private String className;

    public BlockchainNodeInfo() {
    }

    public BlockchainNodeInfo(int id, String code, String forkCode, String name, String description, String units,
            String txFeeUnits, String ip, int port, int forwardedPort, String rpcUn, String rpcPw, String className) {
        this.id = id;
        this.code = code;
        this.forkCode = forkCode;
        this.name = name;
        this.description = description;
        this.units = units;
        this.txFeeUnits = txFeeUnits;
        this.ip = ip;
        this.port = port;
        this.forwardedPort = forwardedPort;
        this.rpcUn = rpcUn;
        this.rpcPw = rpcPw;
        this.className = className;
    }

    public final int getId() {
        return id;
    }

    public final String getCode() {
        return code;
    }

    public final String getForkCode() {
        return forkCode;
    }

    public final String getName() {
        return name;
    }

    public final String getDescription() {
        return description;
    }

    public final String getUnits() {
        return units;
    }

    public final String getTxFeeUnits() {
        return txFeeUnits;
    }

    public final String getIp() {
        return ip;
    }

    public final int getPort() {
        return port;
    }

    public final int getForwardedPort() {
        return forwardedPort;
    }

    public final String getRpcUn() {
        return rpcUn;
    }

    public final String getRpcPw() {
        return rpcPw;
    }

    public final String getClassName() {
        return className;
    }

    public final void setCode(String code) {
        this.code = code;
    }

    public final void setForkCode(String forkCode) {
        this.forkCode = forkCode;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public final void setUnits(String units) {
        this.units = units;
    }

    public final void setTxFeeUnits(String txFeeUnits) {
        this.txFeeUnits = txFeeUnits;
    }

    public final void setIp(String ip) {
        this.ip = ip;
    }

    public final void setPort(int port) {
        this.port = port;
    }

    public final void setForwardedPort(int forwardedPort) {
        this.forwardedPort = forwardedPort;
    }

    public final void setRpcUn(String rpcUn) {
        this.rpcUn = rpcUn;
    }

    public final void setRpcPw(String rpcPw) {
        this.rpcPw = rpcPw;
    }

    public final void setClassName(String className) {
        this.className = className;
    }

    public static String getSqlColumnList() {
        return "ID, CODE, FORK_CODE, NAME, DESCRIPTION, UNITS, TX_FEE_UNITS, IP, PORT, FORWARDED_PORT, RPC_UN, RPC_PW, CLASS_NAME";
    }

    private final static String primaryKeySelect = "SELECT ID, CODE, FORK_CODE, NAME, DESCRIPTION, UNITS, TX_FEE_UNITS, IP, PORT, FORWARDED_PORT, RPC_UN, RPC_PW, CLASS_NAME FROM BLOCKCHAIN_NODE_INFO WHERE ID = ?";

    public static String getStandardTableName() {
        return "BLOCKCHAIN_NODE_INFO";
    }

    public BlockchainNodeInfo(ResultSet rs) throws SQLException {
        id = rs.getInt(1);
        code = rs.getString(2);
        forkCode = rs.getString(3);
        name = rs.getString(4);
        description = rs.getString(5);
        units = rs.getString(6);
        txFeeUnits = rs.getString(7);
        ip = rs.getString(8);
        port = rs.getInt(9);
        forwardedPort = rs.getInt(10);
        rpcUn = rs.getString(11);
        rpcPw = rs.getString(12);
        className = rs.getString(13);
    }

    public static ArrayList<BlockchainNodeInfo> getBlockchainNodeInfos(PreparedStatement ps) throws SQLException {
        ResultSet rs = ps.executeQuery();

        return getBlockchainNodeInfos(rs);
    }

    public static BlockchainNodeInfo getNextBlockchainNodeInfo(ResultSet rs) throws SQLException {
        if (rs.next())
            return new BlockchainNodeInfo(rs);

        else
            return null;
    }

    public static ArrayList<BlockchainNodeInfo> getBlockchainNodeInfos(ResultSet rs) throws SQLException {
        ArrayList<BlockchainNodeInfo> results = new ArrayList<BlockchainNodeInfo>(100);

        while (rs.next())
            results.add(new BlockchainNodeInfo(rs));

        return results;
    }

    public static ArrayList<BlockchainNodeInfo> getBlockchainNodeInfos(ResultSet rs, int max) throws SQLException {
        ArrayList<BlockchainNodeInfo> results = new ArrayList<BlockchainNodeInfo>(100);

        for (int i = 0; (i < max) && rs.next(); i++)
            results.add(new BlockchainNodeInfo(rs));

        return results;
    }

    private static String SELECT_ALL_QUERY = "SELECT " + getSqlColumnList() + " FROM " + getStandardTableName();

    public static ArrayList<BlockchainNodeInfo> getAllBlockchainNodeInfos(Connection connection) throws SQLException {
        return getBlockchainNodeInfos(connection, null);
    }

    public static ArrayList<BlockchainNodeInfo> getAllBlockchainNodeInfos(Connection connection, int max)
            throws SQLException {
        return getBlockchainNodeInfos(connection, null, max);
    }

    public static ArrayList<BlockchainNodeInfo> getBlockchainNodeInfos(Connection connection, String where)
            throws SQLException {
        String query = SELECT_ALL_QUERY;

        if (where != null)
            query += " WHERE " + where;

        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        return getBlockchainNodeInfos(rs);
    }

    public static ArrayList<BlockchainNodeInfo> getBlockchainNodeInfos(Connection connection, String where, int max)
            throws SQLException {
        String query = SELECT_ALL_QUERY;

        if (where != null)
            query += " WHERE " + where;

        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        return getBlockchainNodeInfos(rs, max);
    }

    public static BlockchainNodeInfo getFirstBlockchainNodeInfo(Connection connection, String where)
            throws SQLException {
        String query = SELECT_ALL_QUERY;

        if (where != null)
            query += " WHERE " + where;

        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        return getNextBlockchainNodeInfo(rs);
    }

    public static ArrayList<BlockchainNodeInfo> getBlockchainNodeInfos(DataSource dataSource, String where)
            throws SQLException {
        Connection connection = dataSource.getConnection();
        ArrayList<BlockchainNodeInfo> results = getBlockchainNodeInfos(connection, where);
        connection.close();
        return results;
    }

    public static ArrayList<BlockchainNodeInfo> getBlockchainNodeInfos(String jndiName, String where)
            throws SQLException, NamingException {
        InitialContext ctx = new InitialContext();
        DataSource dataSource = (DataSource) ctx.lookup(jndiName);
        return getBlockchainNodeInfos(dataSource, where);
    }

    public static ArrayList<BlockchainNodeInfo> getBlockchainNodeInfos(String jndiName)
            throws SQLException, NamingException {
        return getBlockchainNodeInfos(jndiName, null);
    }

    public static ArrayList<BlockchainNodeInfo> getBlockchainNodeInfos(DataSource dataSource) throws SQLException {
        return getBlockchainNodeInfos(dataSource, null);
    }

    public static BlockchainNodeInfo insertBlockchainNodeInfo(Connection connection, String code, String forkCode,
            String name, String description, String units, String txFeeUnits, String ip, int port, int forwardedPort,
            String rpcUn, String rpcPw, String className) throws SQLException {
        boolean storedAutoCommitValue = connection.getAutoCommit();

        if (storedAutoCommitValue)
            connection.setAutoCommit(false);

        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO BLOCKCHAIN_NODE_INFO (CODE, FORK_CODE, NAME, DESCRIPTION, UNITS, TX_FEE_UNITS, IP, PORT, FORWARDED_PORT, RPC_UN, RPC_PW, CLASS_NAME) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        ps.setString(1, code);
        ps.setString(2, forkCode);
        ps.setString(3, name);
        ps.setString(4, description);
        ps.setString(5, units);
        ps.setString(6, txFeeUnits);
        ps.setString(7, ip);
        ps.setInt(8, port);
        ps.setInt(9, forwardedPort);
        ps.setString(10, rpcUn);
        ps.setString(11, rpcPw);
        ps.setString(12, className);
        ps.executeUpdate();

        int autoGeneratedKey = 0;

        ps = connection.prepareStatement("Select MAX(ID) FROM BLOCKCHAIN_NODE_INFO");
        ResultSet rs = ps.executeQuery();

        if (rs.next())
            autoGeneratedKey = rs.getInt(1);

        if (storedAutoCommitValue) {
            connection.commit();
            connection.setAutoCommit(true);
        }

        return new BlockchainNodeInfo(autoGeneratedKey, code, forkCode, name, description, units, txFeeUnits, ip, port,
                forwardedPort, rpcUn, rpcPw, className);
    }

    public static BlockchainNodeInfo updateBlockchainNodeInfo(Connection connection, int id, String code,
            String forkCode, String name, String description, String units, String txFeeUnits, String ip, int port,
            int forwardedPort, String rpcUn, String rpcPw, String className) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "UPDATE BLOCKCHAIN_NODE_INFO SET CODE = ?, FORK_CODE = ?, NAME = ?, DESCRIPTION = ?, UNITS = ?, TX_FEE_UNITS = ?, IP = ?, PORT = ?, FORWARDED_PORT = ?, RPC_UN = ?, RPC_PW = ?, CLASS_NAME = ? WHERE ID = ?");

        ps.setString(1, code);
        ps.setString(2, forkCode);
        ps.setString(3, name);
        ps.setString(4, description);
        ps.setString(5, units);
        ps.setString(6, txFeeUnits);
        ps.setString(7, ip);
        ps.setInt(8, port);
        ps.setInt(9, forwardedPort);
        ps.setString(10, rpcUn);
        ps.setString(11, rpcPw);
        ps.setString(12, className);
        ps.executeUpdate();

        return new BlockchainNodeInfo(id, code, forkCode, name, description, units, txFeeUnits, ip, port, forwardedPort,
                rpcUn, rpcPw, className);
    }

    public static BlockchainNodeInfo insertBlockchainNodeInfo(Connection connection,
            BlockchainNodeInfo blockchainNodeInfo) throws SQLException {
        boolean storedAutoCommitValue = connection.getAutoCommit();

        if (storedAutoCommitValue)
            connection.setAutoCommit(false);

        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO BLOCKCHAIN_NODE_INFO (CODE, FORK_CODE, NAME, DESCRIPTION, UNITS, TX_FEE_UNITS, IP, PORT, FORWARDED_PORT, RPC_UN, RPC_PW, CLASS_NAME) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        ps.setString(1, blockchainNodeInfo.code);
        ps.setString(2, blockchainNodeInfo.forkCode);
        ps.setString(3, blockchainNodeInfo.name);
        ps.setString(4, blockchainNodeInfo.description);
        ps.setString(5, blockchainNodeInfo.units);
        ps.setString(6, blockchainNodeInfo.txFeeUnits);
        ps.setString(7, blockchainNodeInfo.ip);
        ps.setInt(8, blockchainNodeInfo.port);
        ps.setInt(9, blockchainNodeInfo.forwardedPort);
        ps.setString(10, blockchainNodeInfo.rpcUn);
        ps.setString(11, blockchainNodeInfo.rpcPw);
        ps.setString(12, blockchainNodeInfo.className);
        ps.executeUpdate();

        int autoGeneratedKey = 0;

        ps = connection.prepareStatement("Select MAX(ID) FROM BLOCKCHAIN_NODE_INFO");
        ResultSet rs = ps.executeQuery();

        if (rs.next())
            autoGeneratedKey = rs.getInt(1);

        if (storedAutoCommitValue) {
            connection.commit();
            connection.setAutoCommit(true);
        }

        blockchainNodeInfo.id = autoGeneratedKey;
        return blockchainNodeInfo;
    }

    public static BlockchainNodeInfo replaceBlockchainNodeInfo(Connection connection,
            BlockchainNodeInfo blockchainNodeInfo) throws SQLException {
        boolean storedAutoCommitValue = connection.getAutoCommit();

        if (storedAutoCommitValue)
            connection.setAutoCommit(false);

        PreparedStatement ps = connection.prepareStatement(
                "REPLACE INTO BLOCKCHAIN_NODE_INFO (CODE, FORK_CODE, NAME, DESCRIPTION, UNITS, TX_FEE_UNITS, IP, PORT, FORWARDED_PORT, RPC_UN, RPC_PW, CLASS_NAME) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        ps.setString(1, blockchainNodeInfo.code);
        ps.setString(2, blockchainNodeInfo.forkCode);
        ps.setString(3, blockchainNodeInfo.name);
        ps.setString(4, blockchainNodeInfo.description);
        ps.setString(5, blockchainNodeInfo.units);
        ps.setString(6, blockchainNodeInfo.txFeeUnits);
        ps.setString(7, blockchainNodeInfo.ip);
        ps.setInt(8, blockchainNodeInfo.port);
        ps.setInt(9, blockchainNodeInfo.forwardedPort);
        ps.setString(10, blockchainNodeInfo.rpcUn);
        ps.setString(11, blockchainNodeInfo.rpcPw);
        ps.setString(12, blockchainNodeInfo.className);
        ps.executeUpdate();

        int autoGeneratedKey = 0;

        ps = connection.prepareStatement("Select MAX(ID) FROM BLOCKCHAIN_NODE_INFO");
        ResultSet rs = ps.executeQuery();

        if (rs.next())
            autoGeneratedKey = rs.getInt(1);

        if (storedAutoCommitValue) {
            connection.commit();
            connection.setAutoCommit(true);
        }

        blockchainNodeInfo.id = autoGeneratedKey;
        return blockchainNodeInfo;
    }

    public static BlockchainNodeInfo updateBlockchainNodeInfo(Connection connection,
            BlockchainNodeInfo blockchainNodeInfo) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "UPDATE BLOCKCHAIN_NODE_INFO SET CODE = ?, FORK_CODE = ?, NAME = ?, DESCRIPTION = ?, UNITS = ?, TX_FEE_UNITS = ?, IP = ?, PORT = ?, FORWARDED_PORT = ?, RPC_UN = ?, RPC_PW = ?, CLASS_NAME = ? WHERE ID = ?");

        ps.setString(1, blockchainNodeInfo.code);
        ps.setString(2, blockchainNodeInfo.forkCode);
        ps.setString(3, blockchainNodeInfo.name);
        ps.setString(4, blockchainNodeInfo.description);
        ps.setString(5, blockchainNodeInfo.units);
        ps.setString(6, blockchainNodeInfo.txFeeUnits);
        ps.setString(7, blockchainNodeInfo.ip);
        ps.setInt(8, blockchainNodeInfo.port);
        ps.setInt(9, blockchainNodeInfo.forwardedPort);
        ps.setString(10, blockchainNodeInfo.rpcUn);
        ps.setString(11, blockchainNodeInfo.rpcPw);
        ps.setString(12, blockchainNodeInfo.className);
        ps.setInt(13, blockchainNodeInfo.id);
        ps.executeUpdate();

        return blockchainNodeInfo;
    }

    public static void deleteAll(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("TRUNCATE TABLE BLOCKCHAIN_NODE_INFO");
        ps.executeUpdate();
    }

    public static BlockchainNodeInfo getBlockchainNodeInfo(Connection connection, int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(primaryKeySelect);

        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next())
            return new BlockchainNodeInfo(rs);
        else
            return null;
    }

}