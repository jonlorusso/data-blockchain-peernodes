package com.swatt.chainNode;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.swatt.chainNode.dao.APIBlockData;
import com.swatt.chainNode.dao.APIBlockDataByDay;
import com.swatt.chainNode.dao.APIBlockDataByInterval;
import com.swatt.chainNode.dao.BlockData;
import com.swatt.chainNode.dao.CheckProgress;
import com.swatt.chainNode.dao.UpdateProgress;
import com.swatt.chainNode.service.ChainNodeConfig;
import com.swatt.util.general.OperationFailedException;

public abstract class ChainNode {
    protected ChainNodeConfig chainNodeConfig;
    protected String blockchainCode;
    protected ArrayList<ChainNodeListener> chainNodeListeners = new ArrayList<ChainNodeListener>();

    public ChainNode() {
    }

    public void setChainNodeConfig(ChainNodeConfig chainNodeConfig) {
        if (this.chainNodeConfig == null)
            this.chainNodeConfig = chainNodeConfig;
        else
            throw new RuntimeException("ChainNodeConfig may not be reset for a ChainNode");
    }

    public void init() {
    }

    public void destroy() {
    }

    public abstract BlockData fetchBlockDataByHash(String blockHash) throws OperationFailedException; // Fetches
                                                                                                      // directly the
                                                                                                      // Blockchain Node
                                                                                                      // itself (not via
                                                                                                      // DB)

    public abstract ChainNodeTransaction fetchTransactionByHash(String transactionHash, boolean calculate)
            throws OperationFailedException; // Fetches directly from the Blockchain Node

    public final APIBlockData getBlockDataByHash(Connection conn, String blockHash) throws SQLException { // This will
                                                                                                          // only
                                                                                                          // return
                                                                                                          // items
                                                                                                          // that are
                                                                                                          // already in
                                                                                                          // the
                                                                                                          // DB

        APIBlockData results = APIBlockData.call(conn, chainNodeConfig.getCode(), blockHash);
        return results;
    }

    public final ArrayList<APIBlockData> getBlocks(Connection conn, long fromTimestamp, long toTimestamp)
            throws SQLException {
        ArrayList<APIBlockData> results = APIBlockData.call(conn, chainNodeConfig.getCode(), fromTimestamp,
                toTimestamp);
        return results;
    }

    public final ArrayList<APIBlockDataByDay> getBlocksByDay(Connection conn, long fromTimestamp, long toTimestamp)
            throws SQLException {
        ArrayList<APIBlockDataByDay> results = APIBlockDataByDay.call(conn, chainNodeConfig.getCode(), fromTimestamp,
                toTimestamp);
        return results;
    }

    public final APIBlockDataByInterval getDataForInterval(Connection conn, long fromTimestamp, long toTimestamp)
            throws SQLException {

        APIBlockDataByInterval results = APIBlockDataByInterval.call(conn, chainNodeConfig.getCode(), fromTimestamp,
                toTimestamp);

        return results;
    }

    public final CheckProgress getCheckProgress(Connection conn, String blockchainCode) throws SQLException {
        CheckProgress results = CheckProgress.call(conn, blockchainCode);

        return results;
    }

    public abstract void fetchNewTransactions();

    public abstract void fetchNewBlocks();

    public abstract String getGenesisHash();

    public abstract long fetchBlockCount() throws OperationFailedException;

    public abstract BlockData fetchBlockData(long blockNumber) throws OperationFailedException;

    public final void setUpdateProgress(Connection conn, String blockchainCode, String blockHash, int limitBlockCount)
            throws SQLException {

        UpdateProgress.call(conn, blockchainCode, blockHash, limitBlockCount);
    }

    public final String getCode() {
        return chainNodeConfig.getCode();
    }

    public void startMonitoringNewActivity() {
        System.out.println("NIY: Start monitoring new Activity (Blocks, pendingTransactions");
    }

    public void stopMonitoringNewActivity() {
        System.out.println("NIY: Stop monitoring new Activity (Blocks, pendingTransactions");
    }

    public void addChainNodeListener(ChainNodeListener chainNodeListener) {
        chainNodeListeners.add(chainNodeListener);
    }

    public ChainNodeSummaryData getChainNodePeriodSummary(long from, long to) {
        return null;
    }

    public int getDifficultyScaling() {
        return chainNodeConfig.getDifficultyScaling();
    }

    public int getRewardScaling() {
        return chainNodeConfig.getRewardScaling();
    }

    public int getFeeScaling() {
        return chainNodeConfig.getFeeScaling();
    }

    public int getAmountScaling() {
        return chainNodeConfig.getAmountScaling();
    }
}
