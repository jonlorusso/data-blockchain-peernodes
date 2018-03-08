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

public class HourlyChainSummaryData  {
	private int chainchainNodeId;
	private long startTime;
	private long endTime;
	private int numBlocks;
	private int numTransactions;
	private double largestTxFee;
	private double smallestTxFee;
	private double avgNumTxPerBlock;

	public HourlyChainSummaryData() { }

	public HourlyChainSummaryData(int chainchainNodeId, long startTime, long endTime, int numBlocks, int numTransactions, double largestTxFee, double smallestTxFee, double avgNumTxPerBlock) {
		this.chainchainNodeId = chainchainNodeId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.numBlocks = numBlocks;
		this.numTransactions = numTransactions;
		this.largestTxFee = largestTxFee;
		this.smallestTxFee = smallestTxFee;
		this.avgNumTxPerBlock = avgNumTxPerBlock;
	}

	public final int getChainchainNodeId() { return chainchainNodeId; }
	public final long getStartTime() { return startTime; }
	public final long getEndTime() { return endTime; }
	public final int getNumBlocks() { return numBlocks; }
	public final int getNumTransactions() { return numTransactions; }
	public final double getLargestTxFee() { return largestTxFee; }
	public final double getSmallestTxFee() { return smallestTxFee; }
	public final double getAvgNumTxPerBlock() { return avgNumTxPerBlock; }

	public final void setChainchainNodeId(int chainchainNodeId) { this.chainchainNodeId = chainchainNodeId; }
	public final void setStartTime(long startTime) { this.startTime = startTime; }
	public final void setEndTime(long endTime) { this.endTime = endTime; }
	public final void setNumBlocks(int numBlocks) { this.numBlocks = numBlocks; }
	public final void setNumTransactions(int numTransactions) { this.numTransactions = numTransactions; }
	public final void setLargestTxFee(double largestTxFee) { this.largestTxFee = largestTxFee; }
	public final void setSmallestTxFee(double smallestTxFee) { this.smallestTxFee = smallestTxFee; }
	public final void setAvgNumTxPerBlock(double avgNumTxPerBlock) { this.avgNumTxPerBlock = avgNumTxPerBlock; }

	public static String getSqlColumnList() { return "CHAINCHAIN_NODE_ID, START_TIME, END_TIME, NUM_BLOCKS, NUM_TRANSACTIONS, LARGEST_TX_FEE, SMALLEST_TX_FEE, AVG_NUM_TX_PER_BLOCK"; }
	public static String getStandardTableName() { return "HOURLY_CHAIN_SUMMARY_DATA"; } 

	public HourlyChainSummaryData(ResultSet rs) throws SQLException { 
		chainchainNodeId = rs.getInt(1);

		java.sql.Timestamp startTimeAsTimestamp = rs.getTimestamp(2);

		if (startTimeAsTimestamp != null)
			startTime = startTimeAsTimestamp.getTime();


		java.sql.Timestamp endTimeAsTimestamp = rs.getTimestamp(3);

		if (endTimeAsTimestamp != null)
			endTime = endTimeAsTimestamp.getTime();

		numBlocks = rs.getInt(4);
		numTransactions = rs.getInt(5);
		largestTxFee = rs.getDouble(6);
		smallestTxFee = rs.getDouble(7);
		avgNumTxPerBlock = rs.getDouble(8);
	}

	 public static Collection getHourlyChainSummaryDatas(PreparedStatement ps) throws SQLException {
		ResultSet rs = ps.executeQuery();

		return getHourlyChainSummaryDatas(rs);
	}

	 public static Collection getHourlyChainSummaryDatas(ResultSet rs) throws SQLException {
		ArrayList results = new ArrayList(100);

		while(rs.next())
			results.add(new HourlyChainSummaryData(rs));

		return results;
	}

	private static String SELECT_ALL_QUERY = "SELECT " + getSqlColumnList() + " FROM " + getStandardTableName();

	public static Collection getHourlyChainSummaryDatas(Connection connection) throws SQLException {
		return getHourlyChainSummaryDatas(connection, null);
	}

	public static Collection getHourlyChainSummaryDatas(Connection connection, String where) throws SQLException {
		String query = SELECT_ALL_QUERY;

		if (where != null)
			query += " WHERE " + where;

		PreparedStatement ps = connection.prepareStatement(query);
		ResultSet rs = ps.executeQuery();

		return getHourlyChainSummaryDatas(rs);
	}

	public static Collection getHourlyChainSummaryDatas(DataSource dataSource, String where) throws SQLException {
		Connection connection = dataSource.getConnection();
		Collection results = getHourlyChainSummaryDatas(connection, where);
		connection.close();
		return results;
	}

	public static Collection getHourlyChainSummaryDatas(String jndiName, String where) throws SQLException, NamingException {
		InitialContext ctx = new InitialContext();
		DataSource dataSource = (DataSource) ctx.lookup(jndiName);
		return getHourlyChainSummaryDatas(dataSource, where);
	}

	public static Collection getHourlyChainSummaryDatas(String jndiName) throws SQLException, NamingException {
		return getHourlyChainSummaryDatas(jndiName, null);
	}

	public static Collection getHourlyChainSummaryDatas(DataSource dataSource) throws SQLException {
		return getHourlyChainSummaryDatas(dataSource, null);
	}

	public static HourlyChainSummaryData createHourlyChainSummaryData(Connection connection, int chainchainNodeId, long startTime, long endTime, int numBlocks, int numTransactions, double largestTxFee, double smallestTxFee, double avgNumTxPerBlock) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("INSERT INTO HOURLY_CHAIN_SUMMARY_DATA (CHAINCHAIN_NODE_ID, START_TIME, END_TIME, NUM_BLOCKS, NUM_TRANSACTIONS, LARGEST_TX_FEE, SMALLEST_TX_FEE, AVG_NUM_TX_PER_BLOCK) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

		ps.setInt(1, chainchainNodeId);
		ps.setTimestamp(2, new java.sql.Timestamp(startTime));
		ps.setTimestamp(3, new java.sql.Timestamp(endTime));
		ps.setInt(4, numBlocks);
		ps.setInt(5, numTransactions);
		ps.setDouble(6, largestTxFee);
		ps.setDouble(7, smallestTxFee);
		ps.setDouble(8, avgNumTxPerBlock);
		ps.executeUpdate();

		return new HourlyChainSummaryData(chainchainNodeId, startTime, endTime, numBlocks, numTransactions, largestTxFee, smallestTxFee, avgNumTxPerBlock);
	}

	public static HourlyChainSummaryData updateHourlyChainSummaryData(Connection connection, int chainchainNodeId, long startTime, long endTime, int numBlocks, int numTransactions, double largestTxFee, double smallestTxFee, double avgNumTxPerBlock) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("UPDATE HOURLY_CHAIN_SUMMARY_DATA SET CHAINCHAIN_NODE_ID = ?, START_TIME = ?, END_TIME = ?, NUM_BLOCKS = ?, NUM_TRANSACTIONS = ?, LARGEST_TX_FEE = ?, SMALLEST_TX_FEE = ?, AVG_NUM_TX_PER_BLOCK = ?");

		ps.setInt(1, chainchainNodeId);
		ps.setTimestamp(2, new java.sql.Timestamp(startTime));
		ps.setTimestamp(3, new java.sql.Timestamp(endTime));
		ps.setInt(4, numBlocks);
		ps.setInt(5, numTransactions);
		ps.setDouble(6, largestTxFee);
		ps.setDouble(7, smallestTxFee);
		ps.setDouble(8, avgNumTxPerBlock);
		ps.executeUpdate();

		return new HourlyChainSummaryData(chainchainNodeId, startTime, endTime, numBlocks, numTransactions, largestTxFee, smallestTxFee, avgNumTxPerBlock);
	}

	public static HourlyChainSummaryData createHourlyChainSummaryData(Connection connection, HourlyChainSummaryData hourlyChainSummaryData) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("INSERT INTO HOURLY_CHAIN_SUMMARY_DATA (CHAINCHAIN_NODE_ID, START_TIME, END_TIME, NUM_BLOCKS, NUM_TRANSACTIONS, LARGEST_TX_FEE, SMALLEST_TX_FEE, AVG_NUM_TX_PER_BLOCK) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

		ps.setInt(1, hourlyChainSummaryData.chainchainNodeId);
		ps.setTimestamp(2, new java.sql.Timestamp(hourlyChainSummaryData.startTime));
		ps.setTimestamp(3, new java.sql.Timestamp(hourlyChainSummaryData.endTime));
		ps.setInt(4, hourlyChainSummaryData.numBlocks);
		ps.setInt(5, hourlyChainSummaryData.numTransactions);
		ps.setDouble(6, hourlyChainSummaryData.largestTxFee);
		ps.setDouble(7, hourlyChainSummaryData.smallestTxFee);
		ps.setDouble(8, hourlyChainSummaryData.avgNumTxPerBlock);
		ps.executeUpdate();

		return hourlyChainSummaryData;
	}

	public static HourlyChainSummaryData updateHourlyChainSummaryData(Connection connection, HourlyChainSummaryData hourlyChainSummaryData) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("UPDATE HOURLY_CHAIN_SUMMARY_DATA SET CHAINCHAIN_NODE_ID = ?, START_TIME = ?, END_TIME = ?, NUM_BLOCKS = ?, NUM_TRANSACTIONS = ?, LARGEST_TX_FEE = ?, SMALLEST_TX_FEE = ?, AVG_NUM_TX_PER_BLOCK = ?");

		ps.setInt(1, hourlyChainSummaryData.chainchainNodeId);
		ps.setTimestamp(2, new java.sql.Timestamp(hourlyChainSummaryData.startTime));
		ps.setTimestamp(3, new java.sql.Timestamp(hourlyChainSummaryData.endTime));
		ps.setInt(4, hourlyChainSummaryData.numBlocks);
		ps.setInt(5, hourlyChainSummaryData.numTransactions);
		ps.setDouble(6, hourlyChainSummaryData.largestTxFee);
		ps.setDouble(7, hourlyChainSummaryData.smallestTxFee);
		ps.setDouble(8, hourlyChainSummaryData.avgNumTxPerBlock);
		ps.executeUpdate();

		return hourlyChainSummaryData;
	}

	public static void deleteAll(Connection connection) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("TRUNCATE TABLE HOURLY_CHAIN_SUMMARY_DATA");
		ps.executeUpdate();
	}


}