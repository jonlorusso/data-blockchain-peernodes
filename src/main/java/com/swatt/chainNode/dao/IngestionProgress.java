package com.swatt.chainNode.dao;

/*  =============================  DO NOT EDIT ANY OF THIS FILE  ============================= 
 * 
 *     THIS IS AUTO-GENERATED CODE WAS CREATED BY gerrySeidman.tools.sql.ExcelSqlCodegen
 * 
 *  =============================  DO NOT EDIT ANY OF THIS FILE  ============================= 
 */

import java.sql.*;
import java.util.*;
import java.io.*;
import javax.sql.DataSource;
import javax.naming.*;

public class IngestionProgress  {
	private int id;
	private String chainName;
	private String blockHashStart;
	private int blocksLimit;

	public IngestionProgress() { }

	public IngestionProgress(int id, String chainName, String blockHashStart, int blocksLimit) {
		this.id = id;
		this.chainName = chainName;
		this.blockHashStart = blockHashStart;
		this.blocksLimit = blocksLimit;
	}

	public final int getId() { return id; }
	public final String getChainName() { return chainName; }
	public final String getBlockHashStart() { return blockHashStart; }
	public final int getBlocksLimit() { return blocksLimit; }

	public final void setId(int id) { this.id = id; }
	public final void setChainName(String chainName) { this.chainName = chainName; }
	public final void setBlockHashStart(String blockHashStart) { this.blockHashStart = blockHashStart; }
	public final void setBlocksLimit(int blocksLimit) { this.blocksLimit = blocksLimit; }

	public static String getSqlColumnList() { return "ID, CHAIN_NAME, BLOCK_HASH_START, BLOCKS_LIMIT"; }
	private final static String primaryKeySelect = "SELECT ID, CHAIN_NAME, BLOCK_HASH_START, BLOCKS_LIMIT FROM INGESTION_PROGRESS WHERE ID = ?";

	public static String getStandardTableName() { return "INGESTION_PROGRESS"; } 

	public IngestionProgress(ResultSet rs) throws SQLException { 
		id = rs.getInt(1);
		chainName = rs.getString(2);
		blockHashStart = rs.getString(3);
		blocksLimit = rs.getInt(4);
	}

	 public static Collection getIngestionProgresss(PreparedStatement ps) throws SQLException {
		ResultSet rs = ps.executeQuery();

		return getIngestionProgresss(rs);
	}

	 public static Collection getIngestionProgresss(ResultSet rs) throws SQLException {
		ArrayList results = new ArrayList(100);

		while(rs.next())
			results.add(new IngestionProgress(rs));

		return results;
	}

	private static String SELECT_ALL_QUERY = "SELECT " + getSqlColumnList() + " FROM " + getStandardTableName();

	public static Collection getIngestionProgresss(Connection connection) throws SQLException {
		return getIngestionProgresss(connection, null);
	}

	public static Collection getIngestionProgresss(Connection connection, String where) throws SQLException {
		String query = SELECT_ALL_QUERY;

		if (where != null)
			query += " WHERE " + where;

		PreparedStatement ps = connection.prepareStatement(query);
		ResultSet rs = ps.executeQuery();

		return getIngestionProgresss(rs);
	}

	public static Collection getIngestionProgresss(DataSource dataSource, String where) throws SQLException {
		Connection connection = dataSource.getConnection();
		Collection results = getIngestionProgresss(connection, where);
		connection.close();
		return results;
	}

	public static Collection getIngestionProgresss(String jndiName, String where) throws SQLException, NamingException {
		InitialContext ctx = new InitialContext();
		DataSource dataSource = (DataSource) ctx.lookup(jndiName);
		return getIngestionProgresss(dataSource, where);
	}

	public static Collection getIngestionProgresss(String jndiName) throws SQLException, NamingException {
		return getIngestionProgresss(jndiName, null);
	}

	public static Collection getIngestionProgresss(DataSource dataSource) throws SQLException {
		return getIngestionProgresss(dataSource, null);
	}

	public static IngestionProgress createIngestionProgress(Connection connection, String chainName, String blockHashStart, int blocksLimit) throws SQLException {
		boolean storedAutoCommitValue = connection.getAutoCommit();

		if(storedAutoCommitValue)
			connection.setAutoCommit(false);

		PreparedStatement ps = connection.prepareStatement("INSERT INTO INGESTION_PROGRESS (CHAIN_NAME, BLOCK_HASH_START, BLOCKS_LIMIT) VALUES (?, ?, ?)");

		ps.setString(1, chainName);
		ps.setString(2, blockHashStart);
		ps.setInt(3, blocksLimit);
		ps.executeUpdate();

		int autoGeneratedKey = 0;

		ps = connection.prepareStatement("Select MAX(ID) FROM INGESTION_PROGRESS");
		ResultSet rs = ps.executeQuery();

		if (rs.next())
			autoGeneratedKey = rs.getInt(1);

		if(storedAutoCommitValue) {
			connection.commit();
			connection.setAutoCommit(true);
		}

		return new IngestionProgress(autoGeneratedKey, chainName, blockHashStart, blocksLimit);
	}

	public static IngestionProgress updateIngestionProgress(Connection connection, int id, String chainName, String blockHashStart, int blocksLimit) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("UPDATE INGESTION_PROGRESS SET CHAIN_NAME = ?, BLOCK_HASH_START = ?, BLOCKS_LIMIT = ? WHERE ID = ?");

		ps.setString(1, chainName);
		ps.setString(2, blockHashStart);
		ps.setInt(3, blocksLimit);
		ps.executeUpdate();

		return new IngestionProgress(id, chainName, blockHashStart, blocksLimit);
	}

	public static IngestionProgress createIngestionProgress(Connection connection, IngestionProgress ingestionProgress) throws SQLException {
		boolean storedAutoCommitValue = connection.getAutoCommit();

		if(storedAutoCommitValue)
			connection.setAutoCommit(false);

		PreparedStatement ps = connection.prepareStatement("INSERT INTO INGESTION_PROGRESS (CHAIN_NAME, BLOCK_HASH_START, BLOCKS_LIMIT) VALUES (?, ?, ?)");

		ps.setString(1, ingestionProgress.chainName);
		ps.setString(2, ingestionProgress.blockHashStart);
		ps.setInt(3, ingestionProgress.blocksLimit);
		ps.executeUpdate();

		int autoGeneratedKey = 0;

		ps = connection.prepareStatement("Select MAX(ID) FROM INGESTION_PROGRESS");
		ResultSet rs = ps.executeQuery();

		if (rs.next())
			autoGeneratedKey = rs.getInt(1);

		if(storedAutoCommitValue) {
			connection.commit();
			connection.setAutoCommit(true);
		}

		ingestionProgress.id = autoGeneratedKey;
		return ingestionProgress;
	}

	public static IngestionProgress updateIngestionProgress(Connection connection, IngestionProgress ingestionProgress) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("UPDATE INGESTION_PROGRESS SET CHAIN_NAME = ?, BLOCK_HASH_START = ?, BLOCKS_LIMIT = ? WHERE ID = ?");

		ps.setString(1, ingestionProgress.chainName);
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