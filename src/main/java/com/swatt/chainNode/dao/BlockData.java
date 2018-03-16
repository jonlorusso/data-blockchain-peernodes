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

public class BlockData {
    private int id;
    private String blockchainCode;
    private String hash;
    private int transactionCount;
    private long height;
    private double difficulty;
    private double reward;
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
    private long indexed;
    private String largestTxHash;
    private double largestTxAmount;
    private double largestFee;
    private double smallestFee;
    private long indexingDuration;

    public BlockData() {
    }

    public BlockData(int id, String blockchainCode, String hash, int transactionCount, int height, double difficulty,
            double reward, String merkleRoot, long timestamp, String bits, int size, String versionHex, long nonce,
            String prevHash, String nextHash, double avgFee, double avgFeeRate, long indexed, String largestTxHash,
            double largestTxAmount, double largestFee, double smallestFee, long indexingDuration) {
        this.id = id;
        this.blockchainCode = blockchainCode;
        this.hash = hash;
        this.transactionCount = transactionCount;
        this.height = height;
        this.difficulty = difficulty;
        this.reward = reward;
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
        this.largestFee = largestFee;
        this.smallestFee = smallestFee;
        this.indexingDuration = indexingDuration;
    }

    public final int getId() {
        return id;
    }

    public final String getBlockchainCode() {
        return blockchainCode;
    }

    public final String getHash() {
        return hash;
    }

    public final int getTransactionCount() {
        return transactionCount;
    }

    public final long getHeight() {
        return height;
    }

    public final double getDifficulty() {
        return difficulty;
    }

    public final double getReward() {
        return reward;
    }

    public final String getMerkleRoot() {
        return merkleRoot;
    }

    public final long getTimestamp() {
        return timestamp;
    }

    public final String getBits() {
        return bits;
    }

    public final int getSize() {
        return size;
    }

    public final String getVersionHex() {
        return versionHex;
    }

    public final long getNonce() {
        return nonce;
    }

    public final String getPrevHash() {
        return prevHash;
    }

    public final String getNextHash() {
        return nextHash;
    }

    public final double getAvgFee() {
        return avgFee;
    }

    public final double getAvgFeeRate() {
        return avgFeeRate;
    }

    public final long getIndexed() {
        return indexed;
    }

    public final String getLargestTxHash() {
        return largestTxHash;
    }

    public final double getLargestTxAmount() {
        return largestTxAmount;
    }

    public final double getLargestFee() {
        return largestFee;
    }

    public final double getSmallestFee() {
        return smallestFee;
    }

    public final long getIndexingDuration() {
        return indexingDuration;
    }

    public final void setId(int id) {
        this.id = id;
    }

    public final void setBlockchainCode(String blockchainCode) {
        this.blockchainCode = blockchainCode;
    }

    public final void setHash(String hash) {
        this.hash = hash;
    }

    public final void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }

    public final void setHeight(long height) {
        this.height = height;
    }

    public final void setDifficulty(double difficulty) {
        this.difficulty = difficulty;
    }

    public final void setReward(double reward) {
        this.reward = reward;
    }

    public final void setMerkleRoot(String merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    public final void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public final void setBits(String bits) {
        this.bits = bits;
    }

    public final void setSize(int size) {
        this.size = size;
    }

    public final void setVersionHex(String versionHex) {
        this.versionHex = versionHex;
    }

    public final void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public final void setPrevHash(String prevHash) {
        this.prevHash = prevHash;
    }

    public final void setNextHash(String nextHash) {
        this.nextHash = nextHash;
    }

    public final void setAvgFee(double avgFee) {
        this.avgFee = avgFee;
    }

    public final void setAvgFeeRate(double avgFeeRate) {
        this.avgFeeRate = avgFeeRate;
    }

    public final void setIndexed(long indexed) {
        this.indexed = indexed;
    }

    public final void setLargestTxHash(String largestTxHash) {
        this.largestTxHash = largestTxHash;
    }

    public final void setLargestTxAmount(double largestTxAmount) {
        this.largestTxAmount = largestTxAmount;
    }

    public final void setLargestFee(double largestFee) {
        this.largestFee = largestFee;
    }

    public final void setSmallestFee(double smallestFee) {
        this.smallestFee = smallestFee;
    }

    public final void setIndexingDuration(long indexingDuration) {
        this.indexingDuration = indexingDuration;
    }

    public static String getSqlColumnList() {
        return "ID, BLOCKCHAIN_CODE, HASH, TRANSACTION_COUNT, HEIGHT, DIFFICULTY, REWARD, MERKLE_ROOT, TIMESTAMP, BITS, SIZE, VERSION_HEX, NONCE, PREV_HASH, NEXT_HASH, AVG_FEE, AVG_FEE_RATE, INDEXED, LARGEST_TX_HASH, LARGEST_TX_AMOUNT, LARGEST_FEE, SMALLEST_FEE, INDEXING_DURATION";
    }

    private final static String primaryKeySelect = "SELECT ID, BLOCKCHAIN_CODE, HASH, TRANSACTION_COUNT, HEIGHT, DIFFICULTY, REWARD, MERKLE_ROOT, TIMESTAMP, BITS, SIZE, VERSION_HEX, NONCE, PREV_HASH, NEXT_HASH, AVG_FEE, AVG_FEE_RATE, INDEXED, LARGEST_TX_HASH, LARGEST_TX_AMOUNT, LARGEST_FEE, SMALLEST_FEE, INDEXING_DURATION FROM BLOCK_DATA WHERE ID = ?";

    public static String getStandardTableName() {
        return "BLOCK_DATA";
    }

    public BlockData(ResultSet rs) throws SQLException {
        id = rs.getInt(1);
        blockchainCode = rs.getString(2);
        hash = rs.getString(3);
        transactionCount = rs.getInt(4);
        height = rs.getInt(5);
        difficulty = rs.getDouble(6);
        reward = rs.getDouble(7);
        merkleRoot = rs.getString(8);
        timestamp = rs.getLong(9);
        bits = rs.getString(10);
        size = rs.getInt(11);
        versionHex = rs.getString(12);
        nonce = rs.getLong(13);
        prevHash = rs.getString(14);
        nextHash = rs.getString(15);
        avgFee = rs.getDouble(16);
        avgFeeRate = rs.getDouble(17);
        indexed = rs.getLong(18);
        largestTxHash = rs.getString(19);
        largestTxAmount = rs.getDouble(20);
        largestFee = rs.getDouble(21);
        smallestFee = rs.getDouble(22);
        indexingDuration = rs.getLong(23);
    }

    public static ArrayList<BlockData> getBlockDatas(PreparedStatement ps) throws SQLException {
        ResultSet rs = ps.executeQuery();

        return getBlockDatas(rs);
    }

    public static BlockData getNextBlockData(ResultSet rs) throws SQLException {
        if (rs.next())
            return new BlockData(rs);

        else
            return null;
    }

    public static ArrayList<BlockData> getBlockDatas(ResultSet rs) throws SQLException {
        ArrayList<BlockData> results = new ArrayList<BlockData>(100);

        while (rs.next())
            results.add(new BlockData(rs));

        return results;
    }

    public static ArrayList<BlockData> getBlockDatas(ResultSet rs, int max) throws SQLException {
        ArrayList<BlockData> results = new ArrayList<BlockData>(100);

        for (int i = 0; (i < max) && rs.next(); i++)
            results.add(new BlockData(rs));

        return results;
    }

    private static String SELECT_ALL_QUERY = "SELECT " + getSqlColumnList() + " FROM " + getStandardTableName();

    public static ArrayList<BlockData> getAllBlockDatas(Connection connection) throws SQLException {
        return getBlockDatas(connection, null);
    }

    public static ArrayList<BlockData> getAllBlockDatas(Connection connection, int max) throws SQLException {
        return getBlockDatas(connection, null, max);
    }

    public static ArrayList<BlockData> getBlockDatas(Connection connection, String where) throws SQLException {
        String query = SELECT_ALL_QUERY;

        if (where != null)
            query += " WHERE " + where;

        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        return getBlockDatas(rs);
    }

    public static ArrayList<BlockData> getBlockDatas(Connection connection, String where, int max) throws SQLException {
        String query = SELECT_ALL_QUERY;

        if (where != null)
            query += " WHERE " + where;

        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        return getBlockDatas(rs, max);
    }

    public static BlockData getFirstBlockData(Connection connection, String where) throws SQLException {
        String query = SELECT_ALL_QUERY;

        if (where != null)
            query += " WHERE " + where;

        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        return getNextBlockData(rs);
    }

    public static ArrayList<BlockData> getBlockDatas(DataSource dataSource, String where) throws SQLException {
        Connection connection = dataSource.getConnection();
        ArrayList<BlockData> results = getBlockDatas(connection, where);
        connection.close();
        return results;
    }

    public static ArrayList<BlockData> getBlockDatas(String jndiName, String where)
            throws SQLException, NamingException {
        InitialContext ctx = new InitialContext();
        DataSource dataSource = (DataSource) ctx.lookup(jndiName);
        return getBlockDatas(dataSource, where);
    }

    public static ArrayList<BlockData> getBlockDatas(String jndiName) throws SQLException, NamingException {
        return getBlockDatas(jndiName, null);
    }

    public static ArrayList<BlockData> getBlockDatas(DataSource dataSource) throws SQLException {
        return getBlockDatas(dataSource, null);
    }

    public static BlockData insertBlockData(Connection connection, String blockchainCode, String hash,
            int transactionCount, int height, double difficulty, double reward, String merkleRoot, long timestamp,
            String bits, int size, String versionHex, long nonce, String prevHash, String nextHash, double avgFee,
            double avgFeeRate, long indexed, String largestTxHash, double largestTxAmount, double largestFee,
            double smallestFee, long indexingDuration) throws SQLException {
        boolean storedAutoCommitValue = connection.getAutoCommit();

        if (storedAutoCommitValue)
            connection.setAutoCommit(false);

        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO BLOCK_DATA (BLOCKCHAIN_CODE, HASH, TRANSACTION_COUNT, HEIGHT, DIFFICULTY, REWARD, MERKLE_ROOT, TIMESTAMP, BITS, SIZE, VERSION_HEX, NONCE, PREV_HASH, NEXT_HASH, AVG_FEE, AVG_FEE_RATE, INDEXED, LARGEST_TX_HASH, LARGEST_TX_AMOUNT, LARGEST_FEE, SMALLEST_FEE, INDEXING_DURATION) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        ps.setString(1, blockchainCode);
        ps.setString(2, hash);
        ps.setInt(3, transactionCount);
        ps.setInt(4, height);
        ps.setDouble(5, difficulty);
        ps.setDouble(6, reward);
        ps.setString(7, merkleRoot);
        ps.setLong(8, timestamp);
        ps.setString(9, bits);
        ps.setInt(10, size);
        ps.setString(11, versionHex);
        ps.setLong(12, nonce);
        ps.setString(13, prevHash);
        ps.setString(14, nextHash);
        ps.setDouble(15, avgFee);
        ps.setDouble(16, avgFeeRate);
        ps.setLong(17, indexed);
        ps.setString(18, largestTxHash);
        ps.setDouble(19, largestTxAmount);
        ps.setDouble(20, largestFee);
        ps.setDouble(21, smallestFee);
        ps.setLong(22, indexingDuration);
        ps.executeUpdate();

        int autoGeneratedKey = 0;

        ps = connection.prepareStatement("Select MAX(ID) FROM BLOCK_DATA");
        ResultSet rs = ps.executeQuery();

        if (rs.next())
            autoGeneratedKey = rs.getInt(1);

        if (storedAutoCommitValue) {
            connection.commit();
            connection.setAutoCommit(true);
        }

        return new BlockData(autoGeneratedKey, blockchainCode, hash, transactionCount, height, difficulty, reward,
                merkleRoot, timestamp, bits, size, versionHex, nonce, prevHash, nextHash, avgFee, avgFeeRate, indexed,
                largestTxHash, largestTxAmount, largestFee, smallestFee, indexingDuration);
    }

    public static BlockData updateBlockData(Connection connection, int id, String blockchainCode, String hash,
            int transactionCount, int height, double difficulty, double reward, String merkleRoot, long timestamp,
            String bits, int size, String versionHex, long nonce, String prevHash, String nextHash, double avgFee,
            double avgFeeRate, long indexed, String largestTxHash, double largestTxAmount, double largestFee,
            double smallestFee, long indexingDuration) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "UPDATE BLOCK_DATA SET BLOCKCHAIN_CODE = ?, HASH = ?, TRANSACTION_COUNT = ?, HEIGHT = ?, DIFFICULTY = ?, REWARD = ?, MERKLE_ROOT = ?, TIMESTAMP = ?, BITS = ?, SIZE = ?, VERSION_HEX = ?, NONCE = ?, PREV_HASH = ?, NEXT_HASH = ?, AVG_FEE = ?, AVG_FEE_RATE = ?, INDEXED = ?, LARGEST_TX_HASH = ?, LARGEST_TX_AMOUNT = ?, LARGEST_FEE = ?, SMALLEST_FEE = ?, INDEXING_DURATION = ? WHERE ID = ?");

        ps.setString(1, blockchainCode);
        ps.setString(2, hash);
        ps.setInt(3, transactionCount);
        ps.setInt(4, height);
        ps.setDouble(5, difficulty);
        ps.setDouble(6, reward);
        ps.setString(7, merkleRoot);
        ps.setLong(8, timestamp);
        ps.setString(9, bits);
        ps.setInt(10, size);
        ps.setString(11, versionHex);
        ps.setLong(12, nonce);
        ps.setString(13, prevHash);
        ps.setString(14, nextHash);
        ps.setDouble(15, avgFee);
        ps.setDouble(16, avgFeeRate);
        ps.setLong(17, indexed);
        ps.setString(18, largestTxHash);
        ps.setDouble(19, largestTxAmount);
        ps.setDouble(20, largestFee);
        ps.setDouble(21, smallestFee);
        ps.setLong(22, indexingDuration);
        ps.executeUpdate();

        return new BlockData(id, blockchainCode, hash, transactionCount, height, difficulty, reward, merkleRoot,
                timestamp, bits, size, versionHex, nonce, prevHash, nextHash, avgFee, avgFeeRate, indexed,
                largestTxHash, largestTxAmount, largestFee, smallestFee, indexingDuration);
    }

    public static BlockData insertBlockData(Connection connection, BlockData blockData) throws SQLException {
        boolean storedAutoCommitValue = connection.getAutoCommit();

        if (storedAutoCommitValue)
            connection.setAutoCommit(false);

        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO BLOCK_DATA (BLOCKCHAIN_CODE, HASH, TRANSACTION_COUNT, HEIGHT, DIFFICULTY, REWARD, MERKLE_ROOT, TIMESTAMP, BITS, SIZE, VERSION_HEX, NONCE, PREV_HASH, NEXT_HASH, AVG_FEE, AVG_FEE_RATE, INDEXED, LARGEST_TX_HASH, LARGEST_TX_AMOUNT, LARGEST_FEE, SMALLEST_FEE, INDEXING_DURATION) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        String sql = "INSERT INTO BLOCK_DATA (BLOCKCHAIN_CODE, HASH, TRANSACTION_COUNT, HEIGHT, DIFFICULTY, REWARD, MERKLE_ROOT, TIMESTAMP, BITS, SIZE, VERSION_HEX, NONCE, PREV_HASH, NEXT_HASH, AVG_FEE, AVG_FEE_RATE, INDEXED, LARGEST_TX_HASH, LARGEST_TX_AMOUNT, LARGEST_FEE, SMALLEST_FEE, INDEXING_DURATION) VALUES ("
                + "'" + blockData.blockchainCode + "'," + "'" + blockData.hash + "'," + blockData.transactionCount + ","
                + blockData.height + "," + blockData.difficulty + "," + blockData.reward + "," + "'"
                + blockData.merkleRoot + "'," + blockData.timestamp + "," + blockData.bits + "," + blockData.size + ","
                + "'" + blockData.versionHex + "'," + blockData.nonce + "," + "'" + blockData.prevHash + "'," + "'"
                + blockData.nextHash + "'," + blockData.avgFee + "," + blockData.avgFeeRate + "," + blockData.indexed
                + "," + "'" + blockData.largestTxHash + "'," + blockData.largestTxAmount + "," + blockData.largestFee
                + "," + blockData.smallestFee + "," + blockData.indexingDuration + ")";
        System.out.println(sql);

        ps.setString(1, blockData.blockchainCode);
        ps.setString(2, blockData.hash);
        ps.setInt(3, blockData.transactionCount);
        ps.setLong(4, blockData.height);
        ps.setDouble(5, blockData.difficulty);
        ps.setDouble(6, blockData.reward);
        ps.setString(7, blockData.merkleRoot);
        ps.setLong(8, blockData.timestamp);
        ps.setString(9, blockData.bits);
        ps.setInt(10, blockData.size);
        ps.setString(11, blockData.versionHex);
        ps.setLong(12, blockData.nonce);
        ps.setString(13, blockData.prevHash);
        ps.setString(14, blockData.nextHash);
        ps.setDouble(15, blockData.avgFee);
        ps.setDouble(16, blockData.avgFeeRate);
        ps.setLong(17, blockData.indexed);
        ps.setString(18, blockData.largestTxHash);
        ps.setDouble(19, blockData.largestTxAmount);
        ps.setDouble(20, blockData.largestFee);
        ps.setDouble(21, blockData.smallestFee);
        ps.setLong(22, blockData.indexingDuration);
        ps.executeUpdate();

        int autoGeneratedKey = 0;

        ps = connection.prepareStatement("Select MAX(ID) FROM BLOCK_DATA");
        ResultSet rs = ps.executeQuery();

        if (rs.next())
            autoGeneratedKey = rs.getInt(1);

        if (storedAutoCommitValue) {
            connection.commit();
            connection.setAutoCommit(true);
        }

        blockData.id = autoGeneratedKey;
        return blockData;
    }

    public static BlockData replaceBlockData(Connection connection, BlockData blockData) throws SQLException {
        boolean storedAutoCommitValue = connection.getAutoCommit();

        if (storedAutoCommitValue)
            connection.setAutoCommit(false);

        PreparedStatement ps = connection.prepareStatement(
                "REPLACE INTO BLOCK_DATA (BLOCKCHAIN_CODE, HASH, TRANSACTION_COUNT, HEIGHT, DIFFICULTY, REWARD, MERKLE_ROOT, TIMESTAMP, BITS, SIZE, VERSION_HEX, NONCE, PREV_HASH, NEXT_HASH, AVG_FEE, AVG_FEE_RATE, INDEXED, LARGEST_TX_HASH, LARGEST_TX_AMOUNT, LARGEST_FEE, SMALLEST_FEE, INDEXING_DURATION) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        ps.setString(1, blockData.blockchainCode);
        ps.setString(2, blockData.hash);
        ps.setInt(3, blockData.transactionCount);
        ps.setLong(4, blockData.height);
        ps.setDouble(5, blockData.difficulty);
        ps.setDouble(6, blockData.reward);
        ps.setString(7, blockData.merkleRoot);
        ps.setLong(8, blockData.timestamp);
        ps.setString(9, blockData.bits);
        ps.setInt(10, blockData.size);
        ps.setString(11, blockData.versionHex);
        ps.setLong(12, blockData.nonce);
        ps.setString(13, blockData.prevHash);
        ps.setString(14, blockData.nextHash);
        ps.setDouble(15, blockData.avgFee);
        ps.setDouble(16, blockData.avgFeeRate);
        ps.setLong(17, blockData.indexed);
        ps.setString(18, blockData.largestTxHash);
        ps.setDouble(19, blockData.largestTxAmount);
        ps.setDouble(20, blockData.largestFee);
        ps.setDouble(21, blockData.smallestFee);
        ps.setLong(22, blockData.indexingDuration);
        ps.executeUpdate();

        int autoGeneratedKey = 0;

        ps = connection.prepareStatement("Select MAX(ID) FROM BLOCK_DATA");
        ResultSet rs = ps.executeQuery();

        if (rs.next())
            autoGeneratedKey = rs.getInt(1);

        if (storedAutoCommitValue) {
            connection.commit();
            connection.setAutoCommit(true);
        }

        blockData.id = autoGeneratedKey;
        return blockData;
    }

    public static BlockData updateBlockData(Connection connection, BlockData blockData) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "UPDATE BLOCK_DATA SET BLOCKCHAIN_CODE = ?, HASH = ?, TRANSACTION_COUNT = ?, HEIGHT = ?, DIFFICULTY = ?, REWARD = ?, MERKLE_ROOT = ?, TIMESTAMP = ?, BITS = ?, SIZE = ?, VERSION_HEX = ?, NONCE = ?, PREV_HASH = ?, NEXT_HASH = ?, AVG_FEE = ?, AVG_FEE_RATE = ?, INDEXED = ?, LARGEST_TX_HASH = ?, LARGEST_TX_AMOUNT = ?, LARGEST_FEE = ?, SMALLEST_FEE = ?, INDEXING_DURATION = ? WHERE ID = ?");

        ps.setString(1, blockData.blockchainCode);
        ps.setString(2, blockData.hash);
        ps.setInt(3, blockData.transactionCount);
        ps.setLong(4, blockData.height);
        ps.setDouble(5, blockData.difficulty);
        ps.setDouble(6, blockData.reward);
        ps.setString(7, blockData.merkleRoot);
        ps.setLong(8, blockData.timestamp);
        ps.setString(9, blockData.bits);
        ps.setInt(10, blockData.size);
        ps.setString(11, blockData.versionHex);
        ps.setLong(12, blockData.nonce);
        ps.setString(13, blockData.prevHash);
        ps.setString(14, blockData.nextHash);
        ps.setDouble(15, blockData.avgFee);
        ps.setDouble(16, blockData.avgFeeRate);
        ps.setLong(17, blockData.indexed);
        ps.setString(18, blockData.largestTxHash);
        ps.setDouble(19, blockData.largestTxAmount);
        ps.setDouble(20, blockData.largestFee);
        ps.setDouble(21, blockData.smallestFee);
        ps.setLong(22, blockData.indexingDuration);
        ps.setInt(23, blockData.id);
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