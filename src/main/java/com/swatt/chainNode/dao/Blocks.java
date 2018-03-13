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

public class Blocks {
    private int id;
    private String blockchainTicker;
    private String hash;
    private int transactionCount;
    private int height;
    private double difficulty;
    private String merkleRoot;
    private int timestamp;
    private String bits;
    private int size;
    private String versionHex;
    private double nonce;
    private String prevHash;
    private String nextHash;
    private double avgFee;
    private double avgFeeRate;
    private int indexed;
    private String largestTxHash;
    private double largestTxAmount;
    private double largestFee;
    private double smallestFee;
    private int indexingDuration;

    public Blocks() {
    }

    public Blocks(int id, String blockchainTicker, String hash, int transactionCount, int height, double difficulty,
            String merkleRoot, int timestamp, String bits, int size, String versionHex, double nonce, String prevHash,
            String nextHash, double avgFee, double avgFeeRate, int indexed, String largestTxHash,
            double largestTxAmount, double largestFee, double smallestFee, int indexingDuration) {
        this.id = id;
        this.blockchainTicker = blockchainTicker;
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
        this.largestFee = largestFee;
        this.smallestFee = smallestFee;
        this.indexingDuration = indexingDuration;
    }

    public final int getId() {
        return id;
    }

    public final String getBlockchainTicker() {
        return blockchainTicker;
    }

    public final String getHash() {
        return hash;
    }

    public final int getTransactionCount() {
        return transactionCount;
    }

    public final int getHeight() {
        return height;
    }

    public final double getDifficulty() {
        return difficulty;
    }

    public final String getMerkleRoot() {
        return merkleRoot;
    }

    public final int getTimestamp() {
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

    public final double getNonce() {
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

    public final int getIndexed() {
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

    public final int getIndexingDuration() {
        return indexingDuration;
    }

    public final void setId(int id) {
        this.id = id;
    }

    public final void setBlockchainTicker(String blockchainTicker) {
        this.blockchainTicker = blockchainTicker;
    }

    public final void setHash(String hash) {
        this.hash = hash;
    }

    public final void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }

    public final void setHeight(int height) {
        this.height = height;
    }

    public final void setDifficulty(double difficulty) {
        this.difficulty = difficulty;
    }

    public final void setMerkleRoot(String merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    public final void setTimestamp(int timestamp) {
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

    public final void setNonce(double nonce) {
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

    public final void setIndexed(int indexed) {
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

    public final void setIndexingDuration(int indexingDuration) {
        this.indexingDuration = indexingDuration;
    }

    public static String getSqlColumnList() {
        return "ID, BLOCKCHAIN_CODE, HASH, TRANSACTION_COUNT, HEIGHT, DIFFICULTY, MERKLE_ROOT, TIMESTAMP, BITS, SIZE, VERSION_HEX, NONCE, PREV_HASH, NEXT_HASH, AVG_FEE, AVG_FEE_RATE, INDEXED, LARGEST_TX_HASH, LARGEST_TX_AMOUNT, LARGEST_FEE, SMALLEST_FEE, INDEXING_DURATION";
    }

    private final static String primaryKeySelect = "SELECT ID, BLOCKCHAIN_CODE, HASH, TRANSACTION_COUNT, HEIGHT, DIFFICULTY, MERKLE_ROOT, TIMESTAMP, BITS, SIZE, VERSION_HEX, NONCE, PREV_HASH, NEXT_HASH, AVG_FEE, AVG_FEE_RATE, INDEXED, LARGEST_TX_HASH, LARGEST_TX_AMOUNT, LARGEST_FEE, SMALLEST_FEE, INDEXING_DURATION FROM BLOCKS WHERE ID = ?";

    public static String getStandardTableName() {
        return "BLOCKS";
    }

    public Blocks(ResultSet rs) throws SQLException {
        id = rs.getInt(1);
        blockchainTicker = rs.getString(2);
        hash = rs.getString(3);
        transactionCount = rs.getInt(4);
        height = rs.getInt(5);
        difficulty = rs.getDouble(6);
        merkleRoot = rs.getString(7);
        timestamp = rs.getInt(8);
        bits = rs.getString(9);
        size = rs.getInt(10);
        versionHex = rs.getString(11);
        nonce = rs.getDouble(12);
        prevHash = rs.getString(13);
        nextHash = rs.getString(14);
        avgFee = rs.getDouble(15);
        avgFeeRate = rs.getDouble(16);
        indexed = rs.getInt(17);
        largestTxHash = rs.getString(18);
        largestTxAmount = rs.getDouble(19);
        largestFee = rs.getDouble(20);
        smallestFee = rs.getDouble(21);
        indexingDuration = rs.getInt(22);
    }

    public static ArrayList<Blocks> getBlockss(PreparedStatement ps) throws SQLException {
        ResultSet rs = ps.executeQuery();

        return getBlockss(rs);
    }

    public static Blocks getNextBlocks(ResultSet rs) throws SQLException {
        if (rs.next())
            return new Blocks(rs);

        else
            return null;
    }

    public static ArrayList<Blocks> getBlockss(ResultSet rs) throws SQLException {
        ArrayList<Blocks> results = new ArrayList<Blocks>(100);

        while (rs.next())
            results.add(new Blocks(rs));

        return results;
    }

    public static ArrayList<Blocks> getBlockss(ResultSet rs, int max) throws SQLException {
        ArrayList<Blocks> results = new ArrayList<Blocks>(100);

        for (int i = 0; (i < max) && rs.next(); i++)
            results.add(new Blocks(rs));

        return results;
    }

    private static String SELECT_ALL_QUERY = "SELECT " + getSqlColumnList() + " FROM " + getStandardTableName();

    public static ArrayList<Blocks> getAllBlockss(Connection connection) throws SQLException {
        return getBlockss(connection, null);
    }

    public static ArrayList<Blocks> getAllBlockss(Connection connection, int max) throws SQLException {
        return getBlockss(connection, null, max);
    }

    public static ArrayList<Blocks> getBlockss(Connection connection, String where) throws SQLException {
        String query = SELECT_ALL_QUERY;

        if (where != null)
            query += " WHERE " + where;

        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        return getBlockss(rs);
    }

    public static ArrayList<Blocks> getBlockss(Connection connection, String where, int max) throws SQLException {
        String query = SELECT_ALL_QUERY;

        if (where != null)
            query += " WHERE " + where;

        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        return getBlockss(rs, max);
    }

    public static Blocks getFirstBlocks(Connection connection, String where) throws SQLException {
        String query = SELECT_ALL_QUERY;

        if (where != null)
            query += " WHERE " + where;

        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        return getNextBlocks(rs);
    }

    public static ArrayList<Blocks> getBlockss(DataSource dataSource, String where) throws SQLException {
        Connection connection = dataSource.getConnection();
        ArrayList<Blocks> results = getBlockss(connection, where);
        connection.close();
        return results;
    }

    public static ArrayList<Blocks> getBlockss(String jndiName, String where) throws SQLException, NamingException {
        InitialContext ctx = new InitialContext();
        DataSource dataSource = (DataSource) ctx.lookup(jndiName);
        return getBlockss(dataSource, where);
    }

    public static ArrayList<Blocks> getBlockss(String jndiName) throws SQLException, NamingException {
        return getBlockss(jndiName, null);
    }

    public static ArrayList<Blocks> getBlockss(DataSource dataSource) throws SQLException {
        return getBlockss(dataSource, null);
    }

    public static Blocks insertBlocks(Connection connection, String blockchainTicker, String hash, int transactionCount,
            int height, double difficulty, String merkleRoot, int timestamp, String bits, int size, String versionHex,
            double nonce, String prevHash, String nextHash, double avgFee, double avgFeeRate, int indexed,
            String largestTxHash, double largestTxAmount, double largestFee, double smallestFee, int indexingDuration)
            throws SQLException {
        boolean storedAutoCommitValue = connection.getAutoCommit();

        if (storedAutoCommitValue)
            connection.setAutoCommit(false);

        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO BLOCKS (BLOCKCHAIN_CODE, HASH, TRANSACTION_COUNT, HEIGHT, DIFFICULTY, MERKLE_ROOT, TIMESTAMP, BITS, SIZE, VERSION_HEX, NONCE, PREV_HASH, NEXT_HASH, AVG_FEE, AVG_FEE_RATE, INDEXED, LARGEST_TX_HASH, LARGEST_TX_AMOUNT, LARGEST_FEE, SMALLEST_FEE, INDEXING_DURATION) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        ps.setString(1, blockchainTicker);
        ps.setString(2, hash);
        ps.setInt(3, transactionCount);
        ps.setInt(4, height);
        ps.setDouble(5, difficulty);
        ps.setString(6, merkleRoot);
        ps.setInt(7, timestamp);
        ps.setString(8, bits);
        ps.setInt(9, size);
        ps.setString(10, versionHex);
        ps.setDouble(11, nonce);
        ps.setString(12, prevHash);
        ps.setString(13, nextHash);
        ps.setDouble(14, avgFee);
        ps.setDouble(15, avgFeeRate);
        ps.setInt(16, indexed);
        ps.setString(17, largestTxHash);
        ps.setDouble(18, largestTxAmount);
        ps.setDouble(19, largestFee);
        ps.setDouble(20, smallestFee);
        ps.setInt(21, indexingDuration);
        ps.executeUpdate();

        int autoGeneratedKey = 0;

        ps = connection.prepareStatement("Select MAX(ID) FROM BLOCKS");
        ResultSet rs = ps.executeQuery();

        if (rs.next())
            autoGeneratedKey = rs.getInt(1);

        if (storedAutoCommitValue) {
            connection.commit();
            connection.setAutoCommit(true);
        }

        return new Blocks(autoGeneratedKey, blockchainTicker, hash, transactionCount, height, difficulty, merkleRoot,
                timestamp, bits, size, versionHex, nonce, prevHash, nextHash, avgFee, avgFeeRate, indexed,
                largestTxHash, largestTxAmount, largestFee, smallestFee, indexingDuration);
    }

    public static Blocks updateBlocks(Connection connection, int id, String blockchainTicker, String hash,
            int transactionCount, int height, double difficulty, String merkleRoot, int timestamp, String bits,
            int size, String versionHex, double nonce, String prevHash, String nextHash, double avgFee,
            double avgFeeRate, int indexed, String largestTxHash, double largestTxAmount, double largestFee,
            double smallestFee, int indexingDuration) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "UPDATE BLOCKS SET BLOCKCHAIN_CODE = ?, HASH = ?, TRANSACTION_COUNT = ?, HEIGHT = ?, DIFFICULTY = ?, MERKLE_ROOT = ?, TIMESTAMP = ?, BITS = ?, SIZE = ?, VERSION_HEX = ?, NONCE = ?, PREV_HASH = ?, NEXT_HASH = ?, AVG_FEE = ?, AVG_FEE_RATE = ?, INDEXED = ?, LARGEST_TX_HASH = ?, LARGEST_TX_AMOUNT = ?, LARGEST_FEE = ?, SMALLEST_FEE = ?, INDEXING_DURATION = ? WHERE ID = ?");

        ps.setString(1, blockchainTicker);
        ps.setString(2, hash);
        ps.setInt(3, transactionCount);
        ps.setInt(4, height);
        ps.setDouble(5, difficulty);
        ps.setString(6, merkleRoot);
        ps.setInt(7, timestamp);
        ps.setString(8, bits);
        ps.setInt(9, size);
        ps.setString(10, versionHex);
        ps.setDouble(11, nonce);
        ps.setString(12, prevHash);
        ps.setString(13, nextHash);
        ps.setDouble(14, avgFee);
        ps.setDouble(15, avgFeeRate);
        ps.setInt(16, indexed);
        ps.setString(17, largestTxHash);
        ps.setDouble(18, largestTxAmount);
        ps.setDouble(19, largestFee);
        ps.setDouble(20, smallestFee);
        ps.setInt(21, indexingDuration);
        ps.executeUpdate();

        return new Blocks(id, blockchainTicker, hash, transactionCount, height, difficulty, merkleRoot, timestamp, bits,
                size, versionHex, nonce, prevHash, nextHash, avgFee, avgFeeRate, indexed, largestTxHash,
                largestTxAmount, largestFee, smallestFee, indexingDuration);
    }

    public static Blocks insertBlocks(Connection connection, Blocks blocks) throws SQLException {
        boolean storedAutoCommitValue = connection.getAutoCommit();

        if (storedAutoCommitValue)
            connection.setAutoCommit(false);

        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO BLOCKS (BLOCKCHAIN_CODE, HASH, TRANSACTION_COUNT, HEIGHT, DIFFICULTY, MERKLE_ROOT, TIMESTAMP, BITS, SIZE, VERSION_HEX, NONCE, PREV_HASH, NEXT_HASH, AVG_FEE, AVG_FEE_RATE, INDEXED, LARGEST_TX_HASH, LARGEST_TX_AMOUNT, LARGEST_FEE, SMALLEST_FEE, INDEXING_DURATION) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        ps.setString(1, blocks.blockchainTicker);
        ps.setString(2, blocks.hash);
        ps.setInt(3, blocks.transactionCount);
        ps.setInt(4, blocks.height);
        ps.setDouble(5, blocks.difficulty);
        ps.setString(6, blocks.merkleRoot);
        ps.setInt(7, blocks.timestamp);
        ps.setString(8, blocks.bits);
        ps.setInt(9, blocks.size);
        ps.setString(10, blocks.versionHex);
        ps.setDouble(11, blocks.nonce);
        ps.setString(12, blocks.prevHash);
        ps.setString(13, blocks.nextHash);
        ps.setDouble(14, blocks.avgFee);
        ps.setDouble(15, blocks.avgFeeRate);
        ps.setInt(16, blocks.indexed);
        ps.setString(17, blocks.largestTxHash);
        ps.setDouble(18, blocks.largestTxAmount);
        ps.setDouble(19, blocks.largestFee);
        ps.setDouble(20, blocks.smallestFee);
        ps.setInt(21, blocks.indexingDuration);
        ps.executeUpdate();

        int autoGeneratedKey = 0;

        ps = connection.prepareStatement("Select MAX(ID) FROM BLOCKS");
        ResultSet rs = ps.executeQuery();

        if (rs.next())
            autoGeneratedKey = rs.getInt(1);

        if (storedAutoCommitValue) {
            connection.commit();
            connection.setAutoCommit(true);
        }

        blocks.id = autoGeneratedKey;
        return blocks;
    }

    public static Blocks replaceBlocks(Connection connection, Blocks blocks) throws SQLException {
        boolean storedAutoCommitValue = connection.getAutoCommit();

        if (storedAutoCommitValue)
            connection.setAutoCommit(false);

        PreparedStatement ps = connection.prepareStatement(
                "REPLACE INTO BLOCKS (BLOCKCHAIN_CODE, HASH, TRANSACTION_COUNT, HEIGHT, DIFFICULTY, MERKLE_ROOT, TIMESTAMP, BITS, SIZE, VERSION_HEX, NONCE, PREV_HASH, NEXT_HASH, AVG_FEE, AVG_FEE_RATE, INDEXED, LARGEST_TX_HASH, LARGEST_TX_AMOUNT, LARGEST_FEE, SMALLEST_FEE, INDEXING_DURATION) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        ps.setString(1, blocks.blockchainTicker);
        ps.setString(2, blocks.hash);
        ps.setInt(3, blocks.transactionCount);
        ps.setInt(4, blocks.height);
        ps.setDouble(5, blocks.difficulty);
        ps.setString(6, blocks.merkleRoot);
        ps.setInt(7, blocks.timestamp);
        ps.setString(8, blocks.bits);
        ps.setInt(9, blocks.size);
        ps.setString(10, blocks.versionHex);
        ps.setDouble(11, blocks.nonce);
        ps.setString(12, blocks.prevHash);
        ps.setString(13, blocks.nextHash);
        ps.setDouble(14, blocks.avgFee);
        ps.setDouble(15, blocks.avgFeeRate);
        ps.setInt(16, blocks.indexed);
        ps.setString(17, blocks.largestTxHash);
        ps.setDouble(18, blocks.largestTxAmount);
        ps.setDouble(19, blocks.largestFee);
        ps.setDouble(20, blocks.smallestFee);
        ps.setInt(21, blocks.indexingDuration);
        ps.executeUpdate();

        int autoGeneratedKey = 0;

        ps = connection.prepareStatement("Select MAX(ID) FROM BLOCKS");
        ResultSet rs = ps.executeQuery();

        if (rs.next())
            autoGeneratedKey = rs.getInt(1);

        if (storedAutoCommitValue) {
            connection.commit();
            connection.setAutoCommit(true);
        }

        blocks.id = autoGeneratedKey;
        return blocks;
    }

    public static Blocks updateBlocks(Connection connection, Blocks blocks) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "UPDATE BLOCKS SET BLOCKCHAIN_CODE = ?, HASH = ?, TRANSACTION_COUNT = ?, HEIGHT = ?, DIFFICULTY = ?, MERKLE_ROOT = ?, TIMESTAMP = ?, BITS = ?, SIZE = ?, VERSION_HEX = ?, NONCE = ?, PREV_HASH = ?, NEXT_HASH = ?, AVG_FEE = ?, AVG_FEE_RATE = ?, INDEXED = ?, LARGEST_TX_HASH = ?, LARGEST_TX_AMOUNT = ?, LARGEST_FEE = ?, SMALLEST_FEE = ?, INDEXING_DURATION = ? WHERE ID = ?");

        ps.setString(1, blocks.blockchainTicker);
        ps.setString(2, blocks.hash);
        ps.setInt(3, blocks.transactionCount);
        ps.setInt(4, blocks.height);
        ps.setDouble(5, blocks.difficulty);
        ps.setString(6, blocks.merkleRoot);
        ps.setInt(7, blocks.timestamp);
        ps.setString(8, blocks.bits);
        ps.setInt(9, blocks.size);
        ps.setString(10, blocks.versionHex);
        ps.setDouble(11, blocks.nonce);
        ps.setString(12, blocks.prevHash);
        ps.setString(13, blocks.nextHash);
        ps.setDouble(14, blocks.avgFee);
        ps.setDouble(15, blocks.avgFeeRate);
        ps.setInt(16, blocks.indexed);
        ps.setString(17, blocks.largestTxHash);
        ps.setDouble(18, blocks.largestTxAmount);
        ps.setDouble(19, blocks.largestFee);
        ps.setDouble(20, blocks.smallestFee);
        ps.setInt(21, blocks.indexingDuration);
        ps.setInt(22, blocks.id);
        ps.executeUpdate();

        return blocks;
    }

    public static void deleteAll(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("TRUNCATE TABLE BLOCKS");
        ps.executeUpdate();
    }

    public static Blocks getBlocks(Connection connection, int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(primaryKeySelect);

        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next())
            return new Blocks(rs);
        else
            return null;
    }

}