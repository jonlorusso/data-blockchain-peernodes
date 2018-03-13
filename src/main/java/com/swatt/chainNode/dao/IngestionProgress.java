package com.swatt.chainNode.dao;

/*  =============================  DO NOT EDIT ANY OF THIS FILE  ============================= 
 * 
 *     THIS IS AUTO-GENERATED CODE WAS CREATED BY gerrySeidman.tools.sql.ExcelSqlCodegen
 *
 *     Based on Excel File: /Users/gloverwilson/Dropbox/Documents/Projects/Swatt Exchange/Blockchain Node Schema.xls
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

public class IngestionProgress {
    private int id;
    private String blockchainTicker;
    private String blockHashStart;
    private int blocksLimit;

    public IngestionProgress() {
    }

    public IngestionProgress(int id, String blockchainTicker, String blockHashStart, int blocksLimit) {
        this.id = id;
        this.blockchainTicker = blockchainTicker;
        this.blockHashStart = blockHashStart;
        this.blocksLimit = blocksLimit;
    }

    public final int getId() {
        return id;
    }

    public final String getBlockchainTicker() {
        return blockchainTicker;
    }

    public final String getBlockHashStart() {
        return blockHashStart;
    }

    public final int getBlocksLimit() {
        return blocksLimit;
    }

    public final void setId(int id) {
        this.id = id;
    }

    public final void setBlockchainTicker(String blockchainTicker) {
        this.blockchainTicker = blockchainTicker;
    }

    public final void setBlockHashStart(String blockHashStart) {
        this.blockHashStart = blockHashStart;
    }

    public final void setBlocksLimit(int blocksLimit) {
        this.blocksLimit = blocksLimit;
    }

    public static String getSqlColumnList() {
        return "ID, BLOCKCHAIN_CODE, BLOCK_HASH_START, BLOCKS_LIMIT";
    }

    private final static String primaryKeySelect = "SELECT ID, BLOCKCHAIN_CODE, BLOCK_HASH_START, BLOCKS_LIMIT FROM INGESTION_PROGRESS WHERE ID = ?";

    public static String getStandardTableName() {
        return "INGESTION_PROGRESS";
    }

    public IngestionProgress(ResultSet rs) throws SQLException {
        id = rs.getInt(1);
        blockchainTicker = rs.getString(2);
        blockHashStart = rs.getString(3);
        blocksLimit = rs.getInt(4);
    }

    public static ArrayList<IngestionProgress> getIngestionProgresss(PreparedStatement ps) throws SQLException {
        ResultSet rs = ps.executeQuery();

        return getIngestionProgresss(rs);
    }

    public static IngestionProgress getNextIngestionProgress(ResultSet rs) throws SQLException {
        if (rs.next())
            return new IngestionProgress(rs);

        else
            return null;
    }

    public static ArrayList<IngestionProgress> getIngestionProgresss(ResultSet rs) throws SQLException {
        ArrayList<IngestionProgress> results = new ArrayList<IngestionProgress>(100);

        while (rs.next())
            results.add(new IngestionProgress(rs));

        return results;
    }

    public static ArrayList<IngestionProgress> getIngestionProgresss(ResultSet rs, int max) throws SQLException {
        ArrayList<IngestionProgress> results = new ArrayList<IngestionProgress>(100);

        for (int i = 0; (i < max) && rs.next(); i++)
            results.add(new IngestionProgress(rs));

        return results;
    }

    private static String SELECT_ALL_QUERY = "SELECT " + getSqlColumnList() + " FROM " + getStandardTableName();

    public static ArrayList<IngestionProgress> getAllIngestionProgresss(Connection connection) throws SQLException {
        return getIngestionProgresss(connection, null);
    }

    public static ArrayList<IngestionProgress> getAllIngestionProgresss(Connection connection, int max)
            throws SQLException {
        return getIngestionProgresss(connection, null, max);
    }

    public static ArrayList<IngestionProgress> getIngestionProgresss(Connection connection, String where)
            throws SQLException {
        String query = SELECT_ALL_QUERY;

        if (where != null)
            query += " WHERE " + where;

        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        return getIngestionProgresss(rs);
    }

    public static ArrayList<IngestionProgress> getIngestionProgresss(Connection connection, String where, int max)
            throws SQLException {
        String query = SELECT_ALL_QUERY;

        if (where != null)
            query += " WHERE " + where;

        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        return getIngestionProgresss(rs, max);
    }

    public static IngestionProgress getFirstIngestionProgress(Connection connection, String where) throws SQLException {
        String query = SELECT_ALL_QUERY;

        if (where != null)
            query += " WHERE " + where;

        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        return getNextIngestionProgress(rs);
    }

    public static ArrayList<IngestionProgress> getIngestionProgresss(DataSource dataSource, String where)
            throws SQLException {
        Connection connection = dataSource.getConnection();
        ArrayList<IngestionProgress> results = getIngestionProgresss(connection, where);
        connection.close();
        return results;
    }

    public static ArrayList<IngestionProgress> getIngestionProgresss(String jndiName, String where)
            throws SQLException, NamingException {
        InitialContext ctx = new InitialContext();
        DataSource dataSource = (DataSource) ctx.lookup(jndiName);
        return getIngestionProgresss(dataSource, where);
    }

    public static ArrayList<IngestionProgress> getIngestionProgresss(String jndiName)
            throws SQLException, NamingException {
        return getIngestionProgresss(jndiName, null);
    }

    public static ArrayList<IngestionProgress> getIngestionProgresss(DataSource dataSource) throws SQLException {
        return getIngestionProgresss(dataSource, null);
    }

    public static IngestionProgress insertIngestionProgress(Connection connection, String blockchainTicker,
            String blockHashStart, int blocksLimit) throws SQLException {
        boolean storedAutoCommitValue = connection.getAutoCommit();

        if (storedAutoCommitValue)
            connection.setAutoCommit(false);

        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO INGESTION_PROGRESS (BLOCKCHAIN_CODE, BLOCK_HASH_START, BLOCKS_LIMIT) VALUES (?, ?, ?)");

        ps.setString(1, blockchainTicker);
        ps.setString(2, blockHashStart);
        ps.setInt(3, blocksLimit);
        ps.executeUpdate();

        int autoGeneratedKey = 0;

        ps = connection.prepareStatement("Select MAX(ID) FROM INGESTION_PROGRESS");
        ResultSet rs = ps.executeQuery();

        if (rs.next())
            autoGeneratedKey = rs.getInt(1);

        if (storedAutoCommitValue) {
            connection.commit();
            connection.setAutoCommit(true);
        }

        return new IngestionProgress(autoGeneratedKey, blockchainTicker, blockHashStart, blocksLimit);
    }

    public static IngestionProgress updateIngestionProgress(Connection connection, int id, String blockchainTicker,
            String blockHashStart, int blocksLimit) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "UPDATE INGESTION_PROGRESS SET BLOCKCHAIN_CODE = ?, BLOCK_HASH_START = ?, BLOCKS_LIMIT = ? WHERE ID = ?");

        ps.setString(1, blockchainTicker);
        ps.setString(2, blockHashStart);
        ps.setInt(3, blocksLimit);
        ps.executeUpdate();

        return new IngestionProgress(id, blockchainTicker, blockHashStart, blocksLimit);
    }

    public static IngestionProgress insertIngestionProgress(Connection connection, IngestionProgress ingestionProgress)
            throws SQLException {
        boolean storedAutoCommitValue = connection.getAutoCommit();

        if (storedAutoCommitValue)
            connection.setAutoCommit(false);

        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO INGESTION_PROGRESS (BLOCKCHAIN_CODE, BLOCK_HASH_START, BLOCKS_LIMIT) VALUES (?, ?, ?)");

        ps.setString(1, ingestionProgress.blockchainTicker);
        ps.setString(2, ingestionProgress.blockHashStart);
        ps.setInt(3, ingestionProgress.blocksLimit);
        ps.executeUpdate();

        int autoGeneratedKey = 0;

        ps = connection.prepareStatement("Select MAX(ID) FROM INGESTION_PROGRESS");
        ResultSet rs = ps.executeQuery();

        if (rs.next())
            autoGeneratedKey = rs.getInt(1);

        if (storedAutoCommitValue) {
            connection.commit();
            connection.setAutoCommit(true);
        }

        ingestionProgress.id = autoGeneratedKey;
        return ingestionProgress;
    }

    public static IngestionProgress replaceIngestionProgress(Connection connection, IngestionProgress ingestionProgress)
            throws SQLException {
        boolean storedAutoCommitValue = connection.getAutoCommit();

        if (storedAutoCommitValue)
            connection.setAutoCommit(false);

        PreparedStatement ps = connection.prepareStatement(
                "REPLACE INTO INGESTION_PROGRESS (BLOCKCHAIN_CODE, BLOCK_HASH_START, BLOCKS_LIMIT) VALUES (?, ?, ?)");

        ps.setString(1, ingestionProgress.blockchainTicker);
        ps.setString(2, ingestionProgress.blockHashStart);
        ps.setInt(3, ingestionProgress.blocksLimit);
        ps.executeUpdate();

        int autoGeneratedKey = 0;

        ps = connection.prepareStatement("Select MAX(ID) FROM INGESTION_PROGRESS");
        ResultSet rs = ps.executeQuery();

        if (rs.next())
            autoGeneratedKey = rs.getInt(1);

        if (storedAutoCommitValue) {
            connection.commit();
            connection.setAutoCommit(true);
        }

        ingestionProgress.id = autoGeneratedKey;
        return ingestionProgress;
    }

    public static IngestionProgress updateIngestionProgress(Connection connection, IngestionProgress ingestionProgress)
            throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "UPDATE INGESTION_PROGRESS SET BLOCKCHAIN_CODE = ?, BLOCK_HASH_START = ?, BLOCKS_LIMIT = ? WHERE ID = ?");

        ps.setString(1, ingestionProgress.blockchainTicker);
        ps.setString(2, ingestionProgress.blockHashStart);
        ps.setInt(3, ingestionProgress.blocksLimit);
        ps.setInt(4, ingestionProgress.id);
        ps.executeUpdate();

        return ingestionProgress;
    }

    public static void deleteAll(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("TRUNCATE TABLE INGESTION_PROGRESS");
        ps.executeUpdate();
    }

    public static IngestionProgress getIngestionProgress(Connection connection, int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(primaryKeySelect);

        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next())
            return new IngestionProgress(rs);
        else
            return null;
    }

}