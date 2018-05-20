package com.swatt.blockchain.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class BlockData extends Entity {

    @Id
    @GeneratedValue
    @Column
    private long id;

    @Column
    private String blockchainCode;

    @Column
    private String hash;

    @Column
    private int transactionCount;

    @Column
    private long height;

    @Column
    private long difficulty;

    @Column
    private int difficultyScale;

    @Column
    private long reward;

    @Column
    private int rewardScale;

    @Column
    private String merkleRoot;

    @Column
    private long timestamp;

    @Column
    private String bits;

    @Column
    private int size;

    @Column
    private String versionHex;

    @Column
    private long nonce;

    @Column
    private String prevHash;

    @Column
    private String nextHash;

    @Column
    private long avgFee;

    @Column
    private int avgFeeScale;

    @Column
    private long avgFeeRate;

    @Column
    private int avgFeeRateScale;

    @Column
    private long indexed;

    @Column
    private String largestTxHash;

    @Column
    private long largestTxAmount;

    @Column
    private int largestTxAmountScale;

    @Column
    private long largestFee;

    @Column
    private int largestFeeScale;

    @Column
    private long smallestFee;

    @Column
    private int smallestFeeScale;

    @Column
    private long indexingDuration;

    public BlockData() {
    }

    public BlockData(int id, String blockchainCode, String hash, int transactionCount, int height, long difficulty, int difficultyScale, long reward, int rewardScale, String merkleRoot, long timestamp, String bits, int size, String versionHex, long nonce, String prevHash, String nextHash, long avgFee, int avgFeeScale, long avgFeeRate, int avgFeeRateScale, int indexed, String largestTxHash, long largestTxAmount, int largestTxAmountScale, long largestFee, int largestFeeScale, long smallestFee, int smallestFeeScale, int indexingDuration) {
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

    public final long getId() {
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

    public final void setId(long id) {
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

    @Override
    public String toString() {
        return "BlockData [id=" + id + ", blockchainCode=" + blockchainCode + ", hash=" + hash + ", transactionCount=" + transactionCount + ", height=" + height + ", difficulty=" + difficulty + ", difficultyScale=" + difficultyScale + ", reward=" + reward + ", rewardScale=" + rewardScale + ", merkleRoot=" + merkleRoot + ", timestamp=" + timestamp + ", bits=" + bits + ", size=" + size + ", versionHex=" + versionHex + ", nonce=" + nonce + ", prevHash=" + prevHash + ", nextHash=" + nextHash + ", avgFee=" + avgFee + ", avgFeeScale=" + avgFeeScale + ", avgFeeRate=" + avgFeeRate + ", avgFeeRateScale=" + avgFeeRateScale + ", indexed=" + indexed + ", largestTxHash=" + largestTxHash + ", largestTxAmount=" + largestTxAmount + ", largestTxAmountScale=" + largestTxAmountScale + ", largestFee=" + largestFee + ", largestFeeScale=" + largestFeeScale + ", smallestFee=" + smallestFee + ", smallestFeeScale=" + smallestFeeScale + ", indexingDuration=" + indexingDuration + "]";
    }
}