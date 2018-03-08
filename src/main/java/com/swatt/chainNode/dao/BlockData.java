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

public class BlockData  {
	private int id;
	private String chainName;
	private String hash;
	private int transactionCount;
	private int height;
	private double difficulty;
	private String merkleRoot;
	private long timestamp;
	private String bits;
	private int size;
	private String versionHex;
	private long nonce;
	private String prevHash;
	private String nextHash;
	private double avgFee;
	private double avgFeeRate;
	private int indexed;
	private String largestTxHash;
	private double largestTxAmount;
	private double largestTxValue;
	private long largestTxTimestamp;
	private long totalSize;
	private double totalFee;
	private double largestFee;
	private double smallestFee;
	private int indexingDuration;
	private int firstTxTimestamp;
	private int lastTxTimestamp;

	public BlockData() { }

	public BlockData(int id, String chainName, String hash, int transactionCount, int height, double difficulty, String merkleRoot, long timestamp, String bits, int size, String versionHex, long nonce, String prevHash, String nextHash, double avgFee, double avgFeeRate, int indexed, String largestTxHash, double largestTxAmount, double largestTxValue, long largestTxTimestamp, long totalSize, double totalFee, double largestFee, double smallestFee, int indexingDuration, int firstTxTimestamp, int lastTxTimestamp) {
		this.id = id;
		this.chainName = chainName;
		this.hash = hash;
		this.transactionCount = transactionCount;
		this.height = height;
		this.difficulty = difficulty;
		this.merkleRoot = merkleRoot;
		this.timestamp = timestamp;
		this.bits = bits;
		this.size = size;
		this.versionHex = versionHex;
		this.nonce = nonce;
		this.prevHash = prevHash;
		this.nextHash = nextHash;
		this.avgFee = avgFee;
		this.avgFeeRate = avgFeeRate;
		this.indexed = indexed;
		this.largestTxHash = largestTxHash;
		this.largestTxAmount = largestTxAmount;
		this.largestTxValue = largestTxValue;
		this.largestTxTimestamp = largestTxTimestamp;
		this.totalSize = totalSize;
		this.totalFee = totalFee;
		this.largestFee = largestFee;
		this.smallestFee = smallestFee;
		this.indexingDuration = indexingDuration;
		this.firstTxTimestamp = firstTxTimestamp;
		this.lastTxTimestamp = lastTxTimestamp;
	}

	public final int getId() { return id; }
	public final String getChainName() { return chainName; }
	public final String getHash() { return hash; }
	public final int getTransactionCount() { return transactionCount; }
	public final int getHeight() { return height; }
	public final double getDifficulty() { return difficulty; }
	public final String getMerkleRoot() { return merkleRoot; }
	public final long getTimestamp() { return timestamp; }
	public final String getBits() { return bits; }
	public final int getSize() { return size; }
	public final String getVersionHex() { return versionHex; }
	public final long getNonce() { return nonce; }
	public final String getPrevHash() { return prevHash; }
	public final String getNextHash() { return nextHash; }
	public final double getAvgFee() { return avgFee; }
	public final double getAvgFeeRate() { return avgFeeRate; }
	public final int getIndexed() { return indexed; }
	public final String getLargestTxHash() { return largestTxHash; }
	public final double getLargestTxAmount() { return largestTxAmount; }
	public final double getLargestTxValue() { return largestTxValue; }
	public final long getLargestTxTimestamp() { return largestTxTimestamp; }
	public final long getTotalSize() { return totalSize; }
	public final double getTotalFee() { return totalFee; }
	public final double getLargestFee() { return largestFee; }
	public final double getSmallestFee() { return smallestFee; }
	public final int getIndexingDuration() { return indexingDuration; }
	public final int getFirstTxTimestamp() { return firstTxTimestamp; }
	public final int getLastTxTimestamp() { return lastTxTimestamp; }

	public final void setId(int id) { this.id = id; }
	public final void setChainName(String chainName) { this.chainName = chainName; }
	public final void setHash(String hash) { this.hash = hash; }
	public final void setTransactionCount(int transactionCount) { this.transactionCount = transactionCount; }
	public final void setHeight(int height) { this.height = height; }
	public final void setDifficulty(double difficulty) { this.difficulty = difficulty; }
	public final void setMerkleRoot(String merkleRoot) { this.merkleRoot = merkleRoot; }
	public final void setTimestamp(long timestamp) { this.timestamp = timestamp; }
	public final void setBits(String bits) { this.bits = bits; }
	public final void setSize(int size) { this.size = size; }
	public final void setVersionHex(String versionHex) { this.versionHex = versionHex; }
	public final void setNonce(long nonce) { this.nonce = nonce; }
	public final void setPrevHash(String prevHash) { this.prevHash = prevHash; }
	public final void setNextHash(String nextHash) { this.nextHash = nextHash; }
	public final void setAvgFee(double avgFee) { this.avgFee = avgFee; }
	public final void setAvgFeeRate(double avgFeeRate) { this.avgFeeRate = avgFeeRate; }
	public final void setIndexed(int indexed) { this.indexed = indexed; }
	public final void setLargestTxHash(String largestTxHash) { this.largestTxHash = largestTxHash; }
	public final void setLargestTxAmount(double largestTxAmount) { this.largestTxAmount = largestTxAmount; }
	public final void setLargestTxValue(double largestTxValue) { this.largestTxValue = largestTxValue; }
	public final void setLargestTxTimestamp(long largestTxTimestamp) { this.largestTxTimestamp = largestTxTimestamp; }
	public final void setTotalSize(long totalSize) { this.totalSize = totalSize; }
	public final void setTotalFee(double totalFee) { this.totalFee = totalFee; }
	public final void setLargestFee(double largestFee) { this.largestFee = largestFee; }
	public final void setSmallestFee(double smallestFee) { this.smallestFee = smallestFee; }
	public final void setIndexingDuration(int indexingDuration) { this.indexingDuration = indexingDuration; }
	public final void setFirstTxTimestamp(int firstTxTimestamp) { this.firstTxTimestamp = firstTxTimestamp; }
	public final void setLastTxTimestamp(int lastTxTimestamp) { this.lastTxTimestamp = lastTxTimestamp; }

	public static String getSqlColumnList() { return "ID, CHAIN_NAME, HASH, TRANSACTION_COUNT, HEIGHT, DIFFICULTY, MERKLE_ROOT, TIMESTAMP, BITS, SIZE, VERSION_HEX, NONCE, PREV_HASH, NEXT_HASH, AVG_FEE, AVG_FEE_RATE, INDEXED, LARGEST_TX_HASH, LARGEST_TX_AMOUNT, LARGEST_TX_VALUE, LARGEST_TX_TIMESTAMP, TOTAL_SIZE, TOTAL_FEE, LARGEST_FEE, SMALLEST_FEE, INDEXING_DURATION, FIRST_TX_TIMESTAMP, LAST_TX_TIMESTAMP"; }
	private final static String primaryKeySelect = "SELECT ID, CHAIN_NAME, HASH, TRANSACTION_COUNT, HEIGHT, DIFFICULTY, MERKLE_ROOT, TIMESTAMP, BITS, SIZE, VERSION_HEX, NONCE, PREV_HASH, NEXT_HASH, AVG_FEE, AVG_FEE_RATE, INDEXED, LARGEST_TX_HASH, LARGEST_TX_AMOUNT, LARGEST_TX_VALUE, LARGEST_TX_TIMESTAMP, TOTAL_SIZE, TOTAL_FEE, LARGEST_FEE, SMALLEST_FEE, INDEXING_DURATION, FIRST_TX_TIMESTAMP, LAST_TX_TIMESTAMP FROM BLOCK_DATA WHERE ID = ?";

	public static String getStandardTableName() { return "BLOCK_DATA"; } 

	public BlockData(ResultSet rs) throws SQLException { 
		id = rs.getInt(1);
		chainName = rs.getString(2);
		hash = rs.getString(3);
		transactionCount = rs.getInt(4);
		height = rs.getInt(5);
		difficulty = rs.getDouble(6);
		merkleRoot = rs.getString(7);
		timestamp = rs.getLong(8);
		bits = rs.getString(9);
		size = rs.getInt(10);
		versionHex = rs.getString(11);
		nonce = rs.getLong(12);
		prevHash = rs.getString(13);
		nextHash = rs.getString(14);
		avgFee = rs.getDouble(15);
		avgFeeRate = rs.getDouble(16);
		indexed = rs.getInt(17);
		largestTxHash = rs.getString(18);
		largestTxAmount = rs.getDouble(19);
		largestTxValue = rs.getDouble(20);
		largestTxTimestamp = rs.getLong(21);
		totalSize = rs.getLong(22);
		totalFee = rs.getDouble(23);
		largestFee = rs.getDouble(24);
		smallestFee = rs.getDouble(25);
		indexingDuration = rs.getInt(26);
		firstTxTimestamp = rs.getInt(27);
		lastTxTimestamp = rs.getInt(28);
	}

	 public static Collection getBlockDatas(PreparedStatement ps) throws SQLException {
		ResultSet rs = ps.executeQuery();

		return getBlockDatas(rs);
	}

	 public static Collection getBlockDatas(ResultSet rs) throws SQLException {
		ArrayList results = new ArrayList(100);

		while(rs.next())
			results.add(new BlockData(rs));

		return results;
	}

	private static String SELECT_ALL_QUERY = "SELECT " + getSqlColumnList() + " FROM " + getStandardTableName();

	public static Collection getBlockDatas(Connection connection) throws SQLException {
		return getBlockDatas(connection, null);
	}

	public static Collection getBlockDatas(Connection connection, String where) throws SQLException {
		String query = SELECT_ALL_QUERY;

		if (where != null)
			query += " WHERE " + where;

		PreparedStatement ps = connection.prepareStatement(query);
		ResultSet rs = ps.executeQuery();

		return getBlockDatas(rs);
	}

	public static Collection getBlockDatas(DataSource dataSource, String where) throws SQLException {
		Connection connection = dataSource.getConnection();
		Collection results = getBlockDatas(connection, where);
		connection.close();
		return results;
	}

	public static Collection getBlockDatas(String jndiName, String where) throws SQLException, NamingException {
		InitialContext ctx = new InitialContext();
		DataSource dataSource = (DataSource) ctx.lookup(jndiName);
		return getBlockDatas(dataSource, where);
	}

	public static Collection getBlockDatas(String jndiName) throws SQLException, NamingException {
		return getBlockDatas(jndiName, null);
	}

	public static Collection getBlockDatas(DataSource dataSource) throws SQLException {
		return getBlockDatas(dataSource, null);
	}

	public static BlockData createBlockData(Connection connection, String chainName, String hash, int transactionCount, int height, double difficulty, String merkleRoot, long timestamp, String bits, int size, String versionHex, long nonce, String prevHash, String nextHash, double avgFee, double avgFeeRate, int indexed, String largestTxHash, double largestTxAmount, double largestTxValue, long largestTxTimestamp, long totalSize, double totalFee, double largestFee, double smallestFee, int indexingDuration, int firstTxTimestamp, int lastTxTimestamp) throws SQLException {
		boolean storedAutoCommitValue = connection.getAutoCommit();

		if(storedAutoCommitValue)
			connection.setAutoCommit(false);

		PreparedStatement ps = connection.prepareStatement("INSERT INTO BLOCK_DATA (CHAIN_NAME, HASH, TRANSACTION_COUNT, HEIGHT, DIFFICULTY, MERKLE_ROOT, TIMESTAMP, BITS, SIZE, VERSION_HEX, NONCE, PREV_HASH, NEXT_HASH, AVG_FEE, AVG_FEE_RATE, INDEXED, LARGEST_TX_HASH, LARGEST_TX_AMOUNT, LARGEST_TX_VALUE, LARGEST_TX_TIMESTAMP, TOTAL_SIZE, TOTAL_FEE, LARGEST_FEE, SMALLEST_FEE, INDEXING_DURATION, FIRST_TX_TIMESTAMP, LAST_TX_TIMESTAMP) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		ps.setString(1, chainName);
		ps.setString(2, hash);
		ps.setInt(3, transactionCount);
		ps.setInt(4, height);
		ps.setDouble(5, difficulty);
		ps.setString(6, merkleRoot);
		ps.setLong(7, timestamp);
		ps.setString(8, bits);
		ps.setInt(9, size);
		ps.setString(10, versionHex);
		ps.setLong(11, nonce);
		ps.setString(12, prevHash);
		ps.setString(13, nextHash);
		ps.setDouble(14, avgFee);
		ps.setDouble(15, avgFeeRate);
		ps.setInt(16, indexed);
		ps.setString(17, largestTxHash);
		ps.setDouble(18, largestTxAmount);
		ps.setDouble(19, largestTxValue);
		ps.setLong(20, largestTxTimestamp);
		ps.setLong(21, totalSize);
		ps.setDouble(22, totalFee);
		ps.setDouble(23, largestFee);
		ps.setDouble(24, smallestFee);
		ps.setInt(25, indexingDuration);
		ps.setInt(26, firstTxTimestamp);
		ps.setInt(27, lastTxTimestamp);
		ps.executeUpdate();

		int autoGeneratedKey = 0;

		ps = connection.prepareStatement("Select MAX(ID) FROM BLOCK_DATA");
		ResultSet rs = ps.executeQuery();

		if (rs.next())
			autoGeneratedKey = rs.getInt(1);

		if(storedAutoCommitValue) {
			connection.commit();
			connection.setAutoCommit(true);
		}

		return new BlockData(autoGeneratedKey, chainName, hash, transactionCount, height, difficulty, merkleRoot, timestamp, bits, size, versionHex, nonce, prevHash, nextHash, avgFee, avgFeeRate, indexed, largestTxHash, largestTxAmount, largestTxValue, largestTxTimestamp, totalSize, totalFee, largestFee, smallestFee, indexingDuration, firstTxTimestamp, lastTxTimestamp);
	}

	public static BlockData updateBlockData(Connection connection, int id, String chainName, String hash, int transactionCount, int height, double difficulty, String merkleRoot, long timestamp, String bits, int size, String versionHex, long nonce, String prevHash, String nextHash, double avgFee, double avgFeeRate, int indexed, String largestTxHash, double largestTxAmount, double largestTxValue, long largestTxTimestamp, long totalSize, double totalFee, double largestFee, double smallestFee, int indexingDuration, int firstTxTimestamp, int lastTxTimestamp) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("UPDATE BLOCK_DATA SET CHAIN_NAME = ?, HASH = ?, TRANSACTION_COUNT = ?, HEIGHT = ?, DIFFICULTY = ?, MERKLE_ROOT = ?, TIMESTAMP = ?, BITS = ?, SIZE = ?, VERSION_HEX = ?, NONCE = ?, PREV_HASH = ?, NEXT_HASH = ?, AVG_FEE = ?, AVG_FEE_RATE = ?, INDEXED = ?, LARGEST_TX_HASH = ?, LARGEST_TX_AMOUNT = ?, LARGEST_TX_VALUE = ?, LARGEST_TX_TIMESTAMP = ?, TOTAL_SIZE = ?, TOTAL_FEE = ?, LARGEST_FEE = ?, SMALLEST_FEE = ?, INDEXING_DURATION = ?, FIRST_TX_TIMESTAMP = ?, LAST_TX_TIMESTAMP = ? WHERE ID = ?");

		ps.setString(1, chainName);
		ps.setString(2, hash);
		ps.setInt(3, transactionCount);
		ps.setInt(4, height);
		ps.setDouble(5, difficulty);
		ps.setString(6, merkleRoot);
		ps.setLong(7, timestamp);
		ps.setString(8, bits);
		ps.setInt(9, size);
		ps.setString(10, versionHex);
		ps.setLong(11, nonce);
		ps.setString(12, prevHash);
		ps.setString(13, nextHash);
		ps.setDouble(14, avgFee);
		ps.setDouble(15, avgFeeRate);
		ps.setInt(16, indexed);
		ps.setString(17, largestTxHash);
		ps.setDouble(18, largestTxAmount);
		ps.setDouble(19, largestTxValue);
		ps.setLong(20, largestTxTimestamp);
		ps.setLong(21, totalSize);
		ps.setDouble(22, totalFee);
		ps.setDouble(23, largestFee);
		ps.setDouble(24, smallestFee);
		ps.setInt(25, indexingDuration);
		ps.setInt(26, firstTxTimestamp);
		ps.setInt(27, lastTxTimestamp);
		ps.executeUpdate();

		return new BlockData(id, chainName, hash, transactionCount, height, difficulty, merkleRoot, timestamp, bits, size, versionHex, nonce, prevHash, nextHash, avgFee, avgFeeRate, indexed, largestTxHash, largestTxAmount, largestTxValue, largestTxTimestamp, totalSize, totalFee, largestFee, smallestFee, indexingDuration, firstTxTimestamp, lastTxTimestamp);
	}

	public static BlockData createBlockData(Connection connection, BlockData blockData) throws SQLException {
		boolean storedAutoCommitValue = connection.getAutoCommit();

		if(storedAutoCommitValue)
			connection.setAutoCommit(false);

		PreparedStatement ps = connection.prepareStatement("INSERT INTO BLOCK_DATA (CHAIN_NAME, HASH, TRANSACTION_COUNT, HEIGHT, DIFFICULTY, MERKLE_ROOT, TIMESTAMP, BITS, SIZE, VERSION_HEX, NONCE, PREV_HASH, NEXT_HASH, AVG_FEE, AVG_FEE_RATE, INDEXED, LARGEST_TX_HASH, LARGEST_TX_AMOUNT, LARGEST_TX_VALUE, LARGEST_TX_TIMESTAMP, TOTAL_SIZE, TOTAL_FEE, LARGEST_FEE, SMALLEST_FEE, INDEXING_DURATION, FIRST_TX_TIMESTAMP, LAST_TX_TIMESTAMP) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		ps.setString(1, blockData.chainName);
		ps.setString(2, blockData.hash);
		ps.setInt(3, blockData.transactionCount);
		ps.setInt(4, blockData.height);
		ps.setDouble(5, blockData.difficulty);
		ps.setString(6, blockData.merkleRoot);
		ps.setLong(7, blockData.timestamp);
		ps.setString(8, blockData.bits);
		ps.setInt(9, blockData.size);
		ps.setString(10, blockData.versionHex);
		ps.setLong(11, blockData.nonce);
		ps.setString(12, blockData.prevHash);
		ps.setString(13, blockData.nextHash);
		ps.setDouble(14, blockData.avgFee);
		ps.setDouble(15, blockData.avgFeeRate);
		ps.setInt(16, blockData.indexed);
		ps.setString(17, blockData.largestTxHash);
		ps.setDouble(18, blockData.largestTxAmount);
		ps.setDouble(19, blockData.largestTxValue);
		ps.setLong(20, blockData.largestTxTimestamp);
		ps.setLong(21, blockData.totalSize);
		ps.setDouble(22, blockData.totalFee);
		ps.setDouble(23, blockData.largestFee);
		ps.setDouble(24, blockData.smallestFee);
		ps.setInt(25, blockData.indexingDuration);
		ps.setInt(26, blockData.firstTxTimestamp);
		ps.setInt(27, blockData.lastTxTimestamp);
		ps.executeUpdate();

		int autoGeneratedKey = 0;

		ps = connection.prepareStatement("Select MAX(ID) FROM BLOCK_DATA");
		ResultSet rs = ps.executeQuery();

		if (rs.next())
			autoGeneratedKey = rs.getInt(1);

		if(storedAutoCommitValue) {
			connection.commit();
			connection.setAutoCommit(true);
		}

		blockData.id = autoGeneratedKey;
		return blockData;
	}

	public static BlockData updateBlockData(Connection connection, BlockData blockData) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("UPDATE BLOCK_DATA SET CHAIN_NAME = ?, HASH = ?, TRANSACTION_COUNT = ?, HEIGHT = ?, DIFFICULTY = ?, MERKLE_ROOT = ?, TIMESTAMP = ?, BITS = ?, SIZE = ?, VERSION_HEX = ?, NONCE = ?, PREV_HASH = ?, NEXT_HASH = ?, AVG_FEE = ?, AVG_FEE_RATE = ?, INDEXED = ?, LARGEST_TX_HASH = ?, LARGEST_TX_AMOUNT = ?, LARGEST_TX_VALUE = ?, LARGEST_TX_TIMESTAMP = ?, TOTAL_SIZE = ?, TOTAL_FEE = ?, LARGEST_FEE = ?, SMALLEST_FEE = ?, INDEXING_DURATION = ?, FIRST_TX_TIMESTAMP = ?, LAST_TX_TIMESTAMP = ? WHERE ID = ?");

		ps.setString(1, blockData.chainName);
		ps.setString(2, blockData.hash);
		ps.setInt(3, blockData.transactionCount);
		ps.setInt(4, blockData.height);
		ps.setDouble(5, blockData.difficulty);
		ps.setString(6, blockData.merkleRoot);
		ps.setLong(7, blockData.timestamp);
		ps.setString(8, blockData.bits);
		ps.setInt(9, blockData.size);
		ps.setString(10, blockData.versionHex);
		ps.setLong(11, blockData.nonce);
		ps.setString(12, blockData.prevHash);
		ps.setString(13, blockData.nextHash);
		ps.setDouble(14, blockData.avgFee);
		ps.setDouble(15, blockData.avgFeeRate);
		ps.setInt(16, blockData.indexed);
		ps.setString(17, blockData.largestTxHash);
		ps.setDouble(18, blockData.largestTxAmount);
		ps.setDouble(19, blockData.largestTxValue);
		ps.setLong(20, blockData.largestTxTimestamp);
		ps.setLong(21, blockData.totalSize);
		ps.setDouble(22, blockData.totalFee);
		ps.setDouble(23, blockData.largestFee);
		ps.setDouble(24, blockData.smallestFee);
		ps.setInt(25, blockData.indexingDuration);
		ps.setInt(26, blockData.firstTxTimestamp);
		ps.setInt(27, blockData.lastTxTimestamp);
		ps.setInt(28, blockData.id);
		ps.executeUpdate();

		return blockData;
	}

	public static void deleteAll(Connection connection) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("TRUNCATE TABLE BLOCK_DATA");
		ps.executeUpdate();
	}

	public static BlockData getBlockData(Connection connection, int id) throws SQLException {
		PreparedStatement ps = connection.prepareStatement(primaryKeySelect);

		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();

		if (rs.next())
			return new BlockData(rs);
		else
			return null;
	}


}