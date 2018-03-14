package com.swatt.chainNode;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.swatt.chainNode.dao.BlockData;
import com.swatt.chainNode.dao.BlockDataByInterval;
import com.swatt.chainNode.dao.CheckProgress;
import com.swatt.chainNode.service.ChainNodeConfig;
import com.swatt.util.OperationFailedException;

public abstract class ChainNode {
    protected ChainNodeConfig chainNodeConfig;
    protected String blockchainCode;
    private ArrayList<ChainNodeListener> chainNodeListeners = new ArrayList<ChainNodeListener>();

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

    public abstract Transaction fetchTransactionByHash(String transactionHash, boolean calculateFee)
            throws OperationFailedException; // Fetches directly from the Blockchain Node

    public final BlockData getBlockDataByHash(Connection conn, String blockHash) throws SQLException { // This will only
                                                                                                       // return items
                                                                                                       // that are
                                                                                                       // already in the
                                                                                                       // DB
        // TODO: Add Some Caching for a proscribed number of blocks.

        String where = "BLOCKCHAIN_CODE = '" + getCode() + "' AND HASH = '" + blockHash + "'";

        ArrayList<BlockData> results = (ArrayList<BlockData>) BlockData.getBlockDatas(conn, where); // TODO: Augment SQL
                                                                                                    // Autogenerator to
                                                                                                    // add single return
                                                                                                    // where

        if (results.size() > 0)
            return results.get(0);
        else
            return null;
    }

    public final BlockDataByInterval getDataForInterval(Connection conn, String blockchainCode, long fromTimestamp,
            long toTimestamp) throws SQLException {

        BlockDataByInterval results = BlockDataByInterval.call(conn, blockchainCode, fromTimestamp, toTimestamp);

        return results;
    }

    public final CheckProgress getCheckProgress(Connection conn, String blockchainCode) throws SQLException {
        CheckProgress results = CheckProgress.call(conn, blockchainCode);

        return results;
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
}
