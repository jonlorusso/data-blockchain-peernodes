package com.swatt.blockchain.node;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.swatt.blockchain.NodeSummaryData;
import com.swatt.blockchain.NodeTransaction;
import com.swatt.blockchain.entity.ApiBlockData;
import com.swatt.blockchain.entity.ApiBlockDataByDay;
import com.swatt.blockchain.entity.ApiBlockDataByInterval;
import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.entity.BlockchainNodeInfo;
import com.swatt.blockchain.entity.CheckProgress;
import com.swatt.blockchain.entity.UpdateProgress;
import com.swatt.util.general.OperationFailedException;

public abstract class Node {
    protected BlockchainNodeInfo blockchainNodeInfo;
    protected ArrayList<NodeListener> nodeListeners = new ArrayList<>();

    public Node() {
        super();
    }

    public void setBlockchainNodeInfo(BlockchainNodeInfo blockchainNodeInfo) {
        if (this.blockchainNodeInfo == null)
            this.blockchainNodeInfo = blockchainNodeInfo;
        else
            throw new RuntimeException("BlockchainNodeInfo may not be reset for a ChainNode");
    }

    public void init() {
    }

    public void destroy() {
    }

    // Fetches directly the Blockchain Node itself (not via DB)
    public abstract BlockData fetchBlockDataByHash(String blockHash) throws OperationFailedException;

    // Fetches directly from the Blockchain Node
    public abstract NodeTransaction fetchTransactionByHash(String transactionHash, boolean calculate)
            throws OperationFailedException;

    public final ApiBlockData fetchBlockDataByHash(Connection conn, String blockHash) throws SQLException {
        return ApiBlockData.fetchByHash(conn, blockchainNodeInfo.getCode(), blockHash);
    }

    public final ApiBlockData fetchBlockDataByHeight(Connection conn, long blockHeight) throws SQLException {
        return ApiBlockData.fetchByHeight(conn, blockchainNodeInfo.getCode(), blockHeight);
    }

    public final ArrayList<ApiBlockData> fetchBlocks(Connection conn, long fromTimestamp, long toTimestamp)
            throws SQLException {
        return ApiBlockData.call(conn, blockchainNodeInfo.getCode(), fromTimestamp, toTimestamp);
    }

    public final ArrayList<ApiBlockDataByDay> fetchBlocksByDay(Connection conn, long fromTimestamp, long toTimestamp)
            throws SQLException {
        return ApiBlockDataByDay.call(conn, blockchainNodeInfo.getCode(), fromTimestamp, toTimestamp);
    }

    public final ApiBlockDataByInterval getDataForInterval(Connection conn, long fromTimestamp, long toTimestamp)
            throws SQLException {
        return ApiBlockDataByInterval.call(conn, blockchainNodeInfo.getCode(), fromTimestamp, toTimestamp);
    }

    public final CheckProgress getCheckProgress(Connection conn, String blockchainCode) throws SQLException {
        return CheckProgress.call(conn, blockchainCode);
    }

    public abstract void fetchNewTransactions();

    public abstract void fetchNewBlocks();

    public abstract long fetchBlockCount() throws OperationFailedException;

    public abstract BlockData fetchBlockData(long blockNumber) throws OperationFailedException;

    public final void setUpdateProgress(Connection conn, String blockchainCode, String blockHash, int limitBlockCount)
            throws SQLException {
        UpdateProgress.call(conn, blockchainCode, blockHash, limitBlockCount);
    }

    public final String getCode() {
        return blockchainNodeInfo.getCode();
    }

    public void startMonitoringNewActivity() {
        throw new UnsupportedOperationException("NIY: Start monitoring new Activity (Blocks, pendingTransactions");
    }

    public void stopMonitoringNewActivity() {
        throw new UnsupportedOperationException("NIY: Stop monitoring new Activity (Blocks, pendingTransactions");
    }

    public void addNodeListener(NodeListener chainNodeListener) {
        nodeListeners.add(chainNodeListener);
    }

    public NodeSummaryData getChainNodePeriodSummary(long from, long to) {
        return null;
    }

    public int getDifficultyScaling() {
        return blockchainNodeInfo.getDifficultyScaling();
    }

    public int getRewardScaling() {
        return blockchainNodeInfo.getRewardScaling();
    }

    public int getFeeScaling() {
        return blockchainNodeInfo.getFeeScaling();
    }

    public int getAmountScaling() {
        return blockchainNodeInfo.getAmountScaling();
    }

    public String getBlockchainCode() {
        return blockchainNodeInfo.getCode();
    }
}
