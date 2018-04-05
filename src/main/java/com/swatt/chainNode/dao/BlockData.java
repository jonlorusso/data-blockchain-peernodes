package com.swatt.chainNode.dao;

import java.io.DataInput;
import java.io.DataOutput;
/*  =============================  DO NOT EDIT ANY OF THIS FILE  ============================= 
 * 
 *     THIS IS AUTO-GENERATED CODE CREATED BY gerrySeidman.tools.sql.ExcelSqlCodegen
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

import com.swatt.util.io.DataStreamSerializable;
import com.swatt.util.io.DataStreamUtilities;
import com.swatt.util.io.SerializationException;

public class BlockData implements DataStreamSerializable {
    private int id;
    private String blockchainCode;
    private String hash;
    private int transactionCount;
    private long height;
    private long difficulty;
    private int difficultyScale;
    private long reward;
    private int rewardScale;
    private String merkleRoot;
    private long timestamp;
    private String bits;
    private int size;
    private String versionHex;
    private long nonce;
    private String prevHash;
    private String nextHash;
    private long avgFee;
    private int avgFeeScale;
    private long avgFeeRate;
    private int avgFeeRateScale;
    private long indexed;
    private String largestTxHash;
    private long largestTxAmount;
    private int largestTxAmountScale;
    private long largestFee;
    private int largestFeeScale;
    private long smallestFee;
    private int smallestFeeScale;
    private long indexingDuration;

    public BlockData() {
    }

    public BlockData(int id, String blockchainCode, String hash, int transactionCount, int height, long difficulty,
            int difficultyScale, long reward, int rewardScale, String merkleRoot, long timestamp, String bits, int size,
            String versionHex, long nonce, String prevHash, String nextHash, long avgFee, int avgFeeScale,
            long avgFeeRate, int avgFeeRateScale, int indexed, String largestTxHash, long largestTxAmount,
            int largestTxAmountScale, long largestFee, int largestFeeScale, long smallestFee, int smallestFeeScale,
            int indexingDuration) {
        this.id = id;
        this.blockchainCode = blockchainCode;
        this.hash = hash;
        this.transactionCount = transactionCount;
        this.height = height;
        this.difficulty = difficulty;
        this.difficultyScale = difficultyScale;
        this.reward = reward;
        this.rewardScale = rewardScale;
        this.merkleRoot = merkleRoot;
        this.timestamp = timestamp;
        this.bits = bits;
        this.size = size;
        this.versionHex = versionHex;
        this.nonce = nonce;
        this.prevHash = prevHash;
        this.nextHash = nextHash;
        this.avgFee = avgFee;
        this.avgFeeScale = avgFeeScale;
        this.avgFeeRate = avgFeeRate;
        this.avgFeeRateScale = avgFeeRateScale;
        this.indexed = indexed;
        this.largestTxHash = largestTxHash;
        this.largestTxAmount = largestTxAmount;
        this.largestTxAmountScale = largestTxAmountScale;
        this.largestFee = largestFee;
        this.largestFeeScale = largestFeeScale;
        this.smallestFee = smallestFee;
        this.smallestFeeScale = smallestFeeScale;
        this.indexingDuration = indexingDuration;
    }

    public void setScalingPowers(int difficultyScale, int rewardScale, int feeScale, int amountScale) {
        this.difficultyScale = difficultyScale;
        this.rewardScale = rewardScale;
        this.avgFeeScale = feeScale;
        this.avgFeeRateScale = feeScale;
        this.largestTxAmountScale = amountScale;
        this.largestFeeScale = feeScale;
        this.smallestFeeScale = feeScale;

    }

    public final int getId() {
        return id;
    }

    public final String getblockchainCode() {
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
        return difficulty / Math.pow(10, difficultyScale);
    }

    public final int getDifficultyScale() {
        return difficultyScale;
    }

    public final double getReward() {
        return reward / Math.pow(10, rewardScale);
    }

    public final int getRewardScale() {
        return rewardScale;
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
        return avgFee / Math.pow(10, avgFeeScale);
    }

    public final int getAvgFeeScale() {
        return avgFeeScale;
    }

    public final double getAvgFeeRate() {
        return avgFeeRate / Math.pow(10, avgFeeRateScale);
    }

    public final int getAvgFeeRateScale() {
        return avgFeeRateScale;
    }

    public final long getIndexed() {
        return indexed;
    }

    public final String getLargestTxHash() {
        return largestTxHash;
    }

    public final double getLargestTxAmount() {
        return largestTxAmount / Math.pow(10, largestTxAmountScale);
    }

    public final int getLargestTxAmountScale() {
        return largestTxAmountScale;
    }

    public final double getLargestFee() {
        return largestFee / Math.pow(10, largestFeeScale);
    }

    public final int getLargestFeeScale() {
        return largestFeeScale;
    }

    public final double getSmallestFee() {
        return smallestFee / Math.pow(10, smallestFeeScale);
    }

    public final int getSmallestFeeScale() {
        return smallestFeeScale;
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

    public final void setDifficultyBase(double difficulty) {
        this.difficulty = Double.valueOf(difficulty * Math.pow(10, difficultyScale)).longValue();
    }

    public final void setDifficulty(long difficulty) {
        this.difficulty = difficulty;
    }

    public final void setDifficultyScale(int difficultyScale) {
        this.difficultyScale = difficultyScale;
    }

    public final void setRewardBase(double reward) {
        this.reward = Double.valueOf(reward * Math.pow(10, rewardScale)).longValue();
    }

    public final void setReward(long reward) {
        this.reward = reward;
    }

    public final void setRewardScale(int rewardScale) {
        this.rewardScale = rewardScale;
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

    public final void setAvgFeeBase(double avgFee) {
        this.avgFee = Double.valueOf(avgFee * Math.pow(10, avgFeeScale)).longValue();
    }

    public final void setAvgFee(long avgFee) {
        this.avgFee = avgFee;
    }

    public final void setAvgFeeScale(int avgFeeScale) {
        this.avgFeeScale = avgFeeScale;
    }

    public final void setAvgFeeRateBase(double avgFeeRate) {
        this.avgFeeRate = Double.valueOf(avgFeeRate * Math.pow(10, avgFeeRateScale)).longValue();
    }

    public final void setAvgFeeRate(long avgFeeRate) {
        this.avgFeeRate = avgFeeRate;
    }

    public final void setAvgFeeRateScale(int avgFeeRateScale) {
        this.avgFeeRateScale = avgFeeRateScale;
    }

    public final void setIndexed(long indexed) {
        this.indexed = indexed;
    }

    public final void setLargestTxHash(String largestTxHash) {
        this.largestTxHash = largestTxHash;
    }

    public final void setLargestTxAmountBase(double largestTxAmount) {
        this.largestTxAmount = Double.valueOf(largestTxAmount * Math.pow(10, largestTxAmountScale)).longValue();
    }

    public final void setLargestTxAmount(long largestTxAmount) {
        this.largestTxAmount = largestTxAmount;
    }

    public final void setLargestTxAmountScale(int largestTxAmountScale) {
        this.largestTxAmountScale = largestTxAmountScale;
    }

    public final void setLargestFeeBase(double largestFee) {
        this.largestFee = Double.valueOf(largestFee * Math.pow(10, largestFeeScale)).longValue();
    }

    public final void setLargestFee(long largestFee) {
        this.largestFee = largestFee;
    }

    public final void setLargestFeeScale(int largestFeeScale) {
        this.largestFeeScale = largestFeeScale;
    }

    public final void setSmallestFeeBase(double smallestFee) {
        this.smallestFee = Double.valueOf(smallestFee * Math.pow(10, smallestFeeScale)).longValue();
    }

    public final void setSmallestFee(long smallestFee) {
        this.smallestFee = smallestFee;
    }

    public final void setSmallestFeeScale(int smallestFeeScale) {
        this.smallestFeeScale = smallestFeeScale;
    }

    public final void setIndexingDuration(long indexingDuration) {
        this.indexingDuration = indexingDuration;
    }

    public static String getSqlColumnList() {
        return "ID, BLOCKCHAIN_TICKER, HASH, TRANSACTION_COUNT, HEIGHT, DIFFICULTY, DIFFICULTY_SCALE, REWARD, REWARD_SCALE, MERKLE_ROOT, TIMESTAMP, BITS, SIZE, VERSION_HEX, NONCE, PREV_HASH, NEXT_HASH, AVG_FEE, AVG_FEE_SCALE, AVG_FEE_RATE, AVG_FEE_RATE_SCALE, INDEXED, LARGEST_TX_HASH, LARGEST_TX_AMOUNT, LARGEST_TX_AMOUNT_SCALE, LARGEST_FEE, LARGEST_FEE_SCALE, SMALLEST_FEE, SMALLEST_FEE_SCALE, INDEXING_DURATION";
    }

    private final static String primaryKeySelect = "SELECT ID, BLOCKCHAIN_TICKER, HASH, TRANSACTION_COUNT, HEIGHT, DIFFICULTY, DIFFICULTY_SCALE, REWARD, REWARD_SCALE, MERKLE_ROOT, TIMESTAMP, BITS, SIZE, VERSION_HEX, NONCE, PREV_HASH, NEXT_HASH, AVG_FEE, AVG_FEE_SCALE, AVG_FEE_RATE, AVG_FEE_RATE_SCALE, INDEXED, LARGEST_TX_HASH, LARGEST_TX_AMOUNT, LARGEST_TX_AMOUNT_SCALE, LARGEST_FEE, LARGEST_FEE_SCALE, SMALLEST_FEE, SMALLEST_FEE_SCALE, INDEXING_DURATION FROM BLOCK_DATA WHERE ID = ?";

    public static String getStandardTableName() {
        return "BLOCK_DATA";
    }

    public BlockData(ResultSet rs) throws SQLException {
        id = rs.getInt(1);
        blockchainCode = rs.getString(2);
        hash = rs.getString(3);
        transactionCount = rs.getInt(4);
        height = rs.getInt(5);
        difficulty = rs.getLong(6);
        difficultyScale = rs.getInt(7);
        reward = rs.getLong(8);
        rewardScale = rs.getInt(9);
        merkleRoot = rs.getString(10);
        timestamp = rs.getLong(11);
        bits = rs.getString(12);
        size = rs.getInt(13);
        versionHex = rs.getString(14);
        nonce = rs.getLong(15);
        prevHash = rs.getString(16);
        nextHash = rs.getString(17);
        avgFee = rs.getLong(18);
        avgFeeScale = rs.getInt(19);
        avgFeeRate = rs.getLong(20);
        avgFeeRateScale = rs.getInt(21);
        indexed = rs.getInt(22);
        largestTxHash = rs.getString(23);
        largestTxAmount = rs.getLong(24);
        largestTxAmountScale = rs.getInt(25);
        largestFee = rs.getLong(26);
        largestFeeScale = rs.getInt(27);
        smallestFee = rs.getLong(28);
        smallestFeeScale = rs.getInt(29);
        indexingDuration = rs.getInt(30);
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
            int transactionCount, int height, long difficulty, int difficultyScale, long reward, int rewardScale,
            String merkleRoot, long timestamp, String bits, int size, String versionHex, long nonce, String prevHash,
            String nextHash, long avgFee, int avgFeeScale, long avgFeeRate, int avgFeeRateScale, int indexed,
            String largestTxHash, long largestTxAmount, int largestTxAmountScale, long largestFee, int largestFeeScale,
            long smallestFee, int smallestFeeScale, int indexingDuration) throws SQLException {
        boolean storedAutoCommitValue = connection.getAutoCommit();

        if (storedAutoCommitValue)
            connection.setAutoCommit(false);

        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO BLOCK_DATA (BLOCKCHAIN_TICKER, HASH, TRANSACTION_COUNT, HEIGHT, DIFFICULTY, DIFFICULTY_SCALE, REWARD, REWARD_SCALE, MERKLE_ROOT, TIMESTAMP, BITS, SIZE, VERSION_HEX, NONCE, PREV_HASH, NEXT_HASH, AVG_FEE, AVG_FEE_SCALE, AVG_FEE_RATE, AVG_FEE_RATE_SCALE, INDEXED, LARGEST_TX_HASH, LARGEST_TX_AMOUNT, LARGEST_TX_AMOUNT_SCALE, LARGEST_FEE, LARGEST_FEE_SCALE, SMALLEST_FEE, SMALLEST_FEE_SCALE, INDEXING_DURATION) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        ps.setString(1, blockchainCode);
        ps.setString(2, hash);
        ps.setInt(3, transactionCount);
        ps.setInt(4, height);
        ps.setLong(5, difficulty);
        ps.setInt(6, difficultyScale);
        ps.setLong(7, reward);
        ps.setInt(8, rewardScale);
        ps.setString(9, merkleRoot);
        ps.setLong(10, timestamp);
        ps.setString(11, bits);
        ps.setInt(12, size);
        ps.setString(13, versionHex);
        ps.setLong(14, nonce);
        ps.setString(15, prevHash);
        ps.setString(16, nextHash);
        ps.setLong(17, avgFee);
        ps.setInt(18, avgFeeScale);
        ps.setLong(19, avgFeeRate);
        ps.setInt(20, avgFeeRateScale);
        ps.setInt(21, indexed);
        ps.setString(22, largestTxHash);
        ps.setLong(23, largestTxAmount);
        ps.setInt(24, largestTxAmountScale);
        ps.setLong(25, largestFee);
        ps.setInt(26, largestFeeScale);
        ps.setLong(27, smallestFee);
        ps.setInt(28, smallestFeeScale);
        ps.setInt(29, indexingDuration);
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

        return new BlockData(autoGeneratedKey, blockchainCode, hash, transactionCount, height, difficulty,
                difficultyScale, reward, rewardScale, merkleRoot, timestamp, bits, size, versionHex, nonce, prevHash,
                nextHash, avgFee, avgFeeScale, avgFeeRate, avgFeeRateScale, indexed, largestTxHash, largestTxAmount,
                largestTxAmountScale, largestFee, largestFeeScale, smallestFee, smallestFeeScale, indexingDuration);
    }

    public static BlockData updateBlockData(Connection connection, int id, String blockchainCode, String hash,
            int transactionCount, int height, long difficulty, int difficultyScale, long reward, int rewardScale,
            String merkleRoot, long timestamp, String bits, int size, String versionHex, long nonce, String prevHash,
            String nextHash, long avgFee, int avgFeeScale, long avgFeeRate, int avgFeeRateScale, int indexed,
            String largestTxHash, long largestTxAmount, int largestTxAmountScale, long largestFee, int largestFeeScale,
            long smallestFee, int smallestFeeScale, int indexingDuration) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "UPDATE BLOCK_DATA SET BLOCKCHAIN_TICKER = ?, HASH = ?, TRANSACTION_COUNT = ?, HEIGHT = ?, DIFFICULTY = ?, DIFFICULTY_SCALE = ?, REWARD = ?, REWARD_SCALE = ?, MERKLE_ROOT = ?, TIMESTAMP = ?, BITS = ?, SIZE = ?, VERSION_HEX = ?, NONCE = ?, PREV_HASH = ?, NEXT_HASH = ?, AVG_FEE = ?, AVG_FEE_SCALE = ?, AVG_FEE_RATE = ?, AVG_FEE_RATE_SCALE = ?, INDEXED = ?, LARGEST_TX_HASH = ?, LARGEST_TX_AMOUNT = ?, LARGEST_TX_AMOUNT_SCALE = ?, LARGEST_FEE = ?, LARGEST_FEE_SCALE = ?, SMALLEST_FEE = ?, SMALLEST_FEE_SCALE = ?, INDEXING_DURATION = ? WHERE ID = ?");

        ps.setString(1, blockchainCode);
        ps.setString(2, hash);
        ps.setInt(3, transactionCount);
        ps.setInt(4, height);
        ps.setLong(5, difficulty);
        ps.setInt(6, difficultyScale);
        ps.setLong(7, reward);
        ps.setInt(8, rewardScale);
        ps.setString(9, merkleRoot);
        ps.setLong(10, timestamp);
        ps.setString(11, bits);
        ps.setInt(12, size);
        ps.setString(13, versionHex);
        ps.setLong(14, nonce);
        ps.setString(15, prevHash);
        ps.setString(16, nextHash);
        ps.setLong(17, avgFee);
        ps.setInt(18, avgFeeScale);
        ps.setLong(19, avgFeeRate);
        ps.setInt(20, avgFeeRateScale);
        ps.setInt(21, indexed);
        ps.setString(22, largestTxHash);
        ps.setLong(23, largestTxAmount);
        ps.setInt(24, largestTxAmountScale);
        ps.setLong(25, largestFee);
        ps.setInt(26, largestFeeScale);
        ps.setLong(27, smallestFee);
        ps.setInt(28, smallestFeeScale);
        ps.setInt(29, indexingDuration);
        ps.executeUpdate();

        return new BlockData(id, blockchainCode, hash, transactionCount, height, difficulty, difficultyScale, reward,
                rewardScale, merkleRoot, timestamp, bits, size, versionHex, nonce, prevHash, nextHash, avgFee,
                avgFeeScale, avgFeeRate, avgFeeRateScale, indexed, largestTxHash, largestTxAmount, largestTxAmountScale,
                largestFee, largestFeeScale, smallestFee, smallestFeeScale, indexingDuration);
    }

    public static BlockData insertBlockData(Connection connection, BlockData blockData) throws SQLException {
        boolean storedAutoCommitValue = connection.getAutoCommit();

        if (storedAutoCommitValue)
            connection.setAutoCommit(false);

        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO BLOCK_DATA (BLOCKCHAIN_TICKER, HASH, TRANSACTION_COUNT, HEIGHT, DIFFICULTY, DIFFICULTY_SCALE, REWARD, REWARD_SCALE, MERKLE_ROOT, TIMESTAMP, BITS, SIZE, VERSION_HEX, NONCE, PREV_HASH, NEXT_HASH, AVG_FEE, AVG_FEE_SCALE, AVG_FEE_RATE, AVG_FEE_RATE_SCALE, INDEXED, LARGEST_TX_HASH, LARGEST_TX_AMOUNT, LARGEST_TX_AMOUNT_SCALE, LARGEST_FEE, LARGEST_FEE_SCALE, SMALLEST_FEE, SMALLEST_FEE_SCALE, INDEXING_DURATION) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        ps.setString(1, blockData.blockchainCode);
        ps.setString(2, blockData.hash);
        ps.setInt(3, blockData.transactionCount);
        ps.setLong(4, blockData.height);
        ps.setLong(5, blockData.difficulty);
        ps.setInt(6, blockData.difficultyScale);
        ps.setLong(7, blockData.reward);
        ps.setInt(8, blockData.rewardScale);
        ps.setString(9, blockData.merkleRoot);
        ps.setLong(10, blockData.timestamp);
        ps.setString(11, blockData.bits);
        ps.setInt(12, blockData.size);
        ps.setString(13, blockData.versionHex);
        ps.setLong(14, blockData.nonce);
        ps.setString(15, blockData.prevHash);
        ps.setString(16, blockData.nextHash);
        ps.setLong(17, blockData.avgFee);
        ps.setInt(18, blockData.avgFeeScale);
        ps.setLong(19, blockData.avgFeeRate);
        ps.setInt(20, blockData.avgFeeRateScale);
        ps.setLong(21, blockData.indexed);
        ps.setString(22, blockData.largestTxHash);
        ps.setLong(23, blockData.largestTxAmount);
        ps.setInt(24, blockData.largestTxAmountScale);
        ps.setLong(25, blockData.largestFee);
        ps.setInt(26, blockData.largestFeeScale);
        ps.setLong(27, blockData.smallestFee);
        ps.setInt(28, blockData.smallestFeeScale);
        ps.setLong(29, blockData.indexingDuration);
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
                "REPLACE INTO BLOCK_DATA (BLOCKCHAIN_TICKER, HASH, TRANSACTION_COUNT, HEIGHT, DIFFICULTY, DIFFICULTY_SCALE, REWARD, REWARD_SCALE, MERKLE_ROOT, TIMESTAMP, BITS, SIZE, VERSION_HEX, NONCE, PREV_HASH, NEXT_HASH, AVG_FEE, AVG_FEE_SCALE, AVG_FEE_RATE, AVG_FEE_RATE_SCALE, INDEXED, LARGEST_TX_HASH, LARGEST_TX_AMOUNT, LARGEST_TX_AMOUNT_SCALE, LARGEST_FEE, LARGEST_FEE_SCALE, SMALLEST_FEE, SMALLEST_FEE_SCALE, INDEXING_DURATION) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        ps.setString(1, blockData.blockchainCode);
        ps.setString(2, blockData.hash);
        ps.setInt(3, blockData.transactionCount);
        ps.setLong(4, blockData.height);
        ps.setLong(5, blockData.difficulty);
        ps.setInt(6, blockData.difficultyScale);
        ps.setLong(7, blockData.reward);
        ps.setInt(8, blockData.rewardScale);
        ps.setString(9, blockData.merkleRoot);
        ps.setLong(10, blockData.timestamp);
        ps.setString(11, blockData.bits);
        ps.setInt(12, blockData.size);
        ps.setString(13, blockData.versionHex);
        ps.setLong(14, blockData.nonce);
        ps.setString(15, blockData.prevHash);
        ps.setString(16, blockData.nextHash);
        ps.setLong(17, blockData.avgFee);
        ps.setInt(18, blockData.avgFeeScale);
        ps.setLong(19, blockData.avgFeeRate);
        ps.setInt(20, blockData.avgFeeRateScale);
        ps.setLong(21, blockData.indexed);
        ps.setString(22, blockData.largestTxHash);
        ps.setLong(23, blockData.largestTxAmount);
        ps.setInt(24, blockData.largestTxAmountScale);
        ps.setLong(25, blockData.largestFee);
        ps.setInt(26, blockData.largestFeeScale);
        ps.setLong(27, blockData.smallestFee);
        ps.setInt(28, blockData.smallestFeeScale);
        ps.setLong(29, blockData.indexingDuration);
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
                "UPDATE BLOCK_DATA SET BLOCKCHAIN_TICKER = ?, HASH = ?, TRANSACTION_COUNT = ?, HEIGHT = ?, DIFFICULTY = ?, DIFFICULTY_SCALE = ?, REWARD = ?, REWARD_SCALE = ?, MERKLE_ROOT = ?, TIMESTAMP = ?, BITS = ?, SIZE = ?, VERSION_HEX = ?, NONCE = ?, PREV_HASH = ?, NEXT_HASH = ?, AVG_FEE = ?, AVG_FEE_SCALE = ?, AVG_FEE_RATE = ?, AVG_FEE_RATE_SCALE = ?, INDEXED = ?, LARGEST_TX_HASH = ?, LARGEST_TX_AMOUNT = ?, LARGEST_TX_AMOUNT_SCALE = ?, LARGEST_FEE = ?, LARGEST_FEE_SCALE = ?, SMALLEST_FEE = ?, SMALLEST_FEE_SCALE = ?, INDEXING_DURATION = ? WHERE ID = ?");

        ps.setString(1, blockData.blockchainCode);
        ps.setString(2, blockData.hash);
        ps.setInt(3, blockData.transactionCount);
        ps.setLong(4, blockData.height);
        ps.setLong(5, blockData.difficulty);
        ps.setInt(6, blockData.difficultyScale);
        ps.setLong(7, blockData.reward);
        ps.setInt(8, blockData.rewardScale);
        ps.setString(9, blockData.merkleRoot);
        ps.setLong(10, blockData.timestamp);
        ps.setString(11, blockData.bits);
        ps.setInt(12, blockData.size);
        ps.setString(13, blockData.versionHex);
        ps.setLong(14, blockData.nonce);
        ps.setString(15, blockData.prevHash);
        ps.setString(16, blockData.nextHash);
        ps.setLong(17, blockData.avgFee);
        ps.setInt(18, blockData.avgFeeScale);
        ps.setLong(19, blockData.avgFeeRate);
        ps.setInt(20, blockData.avgFeeRateScale);
        ps.setLong(21, blockData.indexed);
        ps.setString(22, blockData.largestTxHash);
        ps.setLong(23, blockData.largestTxAmount);
        ps.setInt(24, blockData.largestTxAmountScale);
        ps.setLong(25, blockData.largestFee);
        ps.setInt(26, blockData.largestFeeScale);
        ps.setLong(27, blockData.smallestFee);
        ps.setInt(28, blockData.smallestFeeScale);
        ps.setLong(29, blockData.indexingDuration);
        ps.setInt(30, blockData.id);
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

    @Override
    public void write(DataOutput dout) throws SerializationException {
        try {
            DataStreamUtilities.writeInt(dout, id);
            DataStreamUtilities.writeString(dout, blockchainCode);

            DataStreamUtilities.writeString(dout, hash);

            DataStreamUtilities.writeInt(dout, transactionCount);
            DataStreamUtilities.writeLong(dout, height);
            DataStreamUtilities.writeLong(dout, difficulty);
            DataStreamUtilities.writeInt(dout, difficultyScale);
            DataStreamUtilities.writeLong(dout, reward);
            DataStreamUtilities.writeInt(dout, rewardScale);
            DataStreamUtilities.writeString(dout, merkleRoot);

            DataStreamUtilities.writeLong(dout, timestamp);
            DataStreamUtilities.writeString(dout, bits);

            DataStreamUtilities.writeInt(dout, size);
            DataStreamUtilities.writeString(dout, versionHex);

            DataStreamUtilities.writeLong(dout, nonce);
            DataStreamUtilities.writeString(dout, prevHash);

            DataStreamUtilities.writeString(dout, nextHash);

            DataStreamUtilities.writeLong(dout, avgFee);
            DataStreamUtilities.writeInt(dout, avgFeeScale);
            DataStreamUtilities.writeLong(dout, avgFeeRate);
            DataStreamUtilities.writeInt(dout, avgFeeRateScale);
            DataStreamUtilities.writeLong(dout, indexed);
            DataStreamUtilities.writeString(dout, largestTxHash);

            DataStreamUtilities.writeLong(dout, largestTxAmount);
            DataStreamUtilities.writeInt(dout, largestTxAmountScale);
            DataStreamUtilities.writeLong(dout, largestFee);
            DataStreamUtilities.writeInt(dout, largestFeeScale);
            DataStreamUtilities.writeLong(dout, smallestFee);
            DataStreamUtilities.writeInt(dout, smallestFeeScale);
            DataStreamUtilities.writeLong(dout, indexingDuration);
        } catch (Throwable t) {
            throw new SerializationException("Unable To Serialize", t);
        }
    }

    @Override
    public void read(DataInput din) throws SerializationException {
        try {
            id = DataStreamUtilities.readInt(din);
            blockchainCode = DataStreamUtilities.readString(din);
            hash = DataStreamUtilities.readString(din);
            transactionCount = DataStreamUtilities.readInt(din);
            height = DataStreamUtilities.readLong(din);
            difficulty = DataStreamUtilities.readLong(din);
            difficultyScale = DataStreamUtilities.readInt(din);
            reward = DataStreamUtilities.readLong(din);
            rewardScale = DataStreamUtilities.readInt(din);
            merkleRoot = DataStreamUtilities.readString(din);
            timestamp = DataStreamUtilities.readLong(din);
            bits = DataStreamUtilities.readString(din);
            size = DataStreamUtilities.readInt(din);
            versionHex = DataStreamUtilities.readString(din);
            nonce = DataStreamUtilities.readLong(din);
            prevHash = DataStreamUtilities.readString(din);
            nextHash = DataStreamUtilities.readString(din);
            avgFee = DataStreamUtilities.readLong(din);
            avgFeeScale = DataStreamUtilities.readInt(din);
            avgFeeRate = DataStreamUtilities.readLong(din);
            avgFeeRateScale = DataStreamUtilities.readInt(din);
            indexed = DataStreamUtilities.readInt(din);
            largestTxHash = DataStreamUtilities.readString(din);
            largestTxAmount = DataStreamUtilities.readLong(din);
            largestTxAmountScale = DataStreamUtilities.readInt(din);
            largestFee = DataStreamUtilities.readLong(din);
            largestFeeScale = DataStreamUtilities.readInt(din);
            smallestFee = DataStreamUtilities.readLong(din);
            smallestFeeScale = DataStreamUtilities.readInt(din);
            indexingDuration = DataStreamUtilities.readLong(din);
        } catch (Throwable t) {
            throw new SerializationException("Unable To Serialize", t);
        }
    }

}