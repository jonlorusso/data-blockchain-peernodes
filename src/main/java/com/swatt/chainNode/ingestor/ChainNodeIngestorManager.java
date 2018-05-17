package com.swatt.chainNode.ingestor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swatt.chainNode.ChainNode;
import com.swatt.chainNode.dao.BlockchainNodeInfo;
import com.swatt.chainNode.service.ChainNodeManager;
import com.swatt.util.general.ConcurrencyUtilities;
import com.swatt.util.sql.ConnectionPool;

public class ChainNodeIngestorManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChainNodeIngestorManager.class);
    
    private static final long ACTIVE_NODE_WATCHER_SLEEP_TIME = 60 * 1000;

    private ChainNodeManager chainNodeManager;
    private ConnectionPool connectionPool;
    
    private Map<String, ChainNodeIngestor> chainNodeIngestors = new HashMap<>();
    
    public ChainNodeIngestorManager(ConnectionPool connectionPool, ChainNodeManager chainNodeManager) {
    		this.connectionPool = connectionPool;
    		this.chainNodeManager = chainNodeManager;
    }

    public void startChainNodeIngestor(ChainNode chainNode) {
        if (chainNode != null) {
            ChainNodeIngestor chainNodeIngestor = chainNodeIngestors.get(chainNode.getCode());
            if (chainNodeIngestor == null) {
                LOGGER.info("Starting ChainNodeIngestor for " + chainNode.getCode());
                chainNodeIngestor = new ChainNodeIngestor(connectionPool, chainNode);
                chainNodeIngestors.put(chainNode.getBlockchainCode(), chainNodeIngestor);
            }
            
            chainNodeIngestor.startHistoricalIngestion();
            chainNodeIngestor.startNewBlockIngestion();
        }
    }
	
	// FIXME all active nodes will be started in each process
	public void startActiveNodeWatcher() {
		new Thread(() -> {
			LOGGER.info("Starting ActiveNodeWatcher Thread.");

			while (true) {
			    try (Connection connection = connectionPool.getConnection()) {
			        BlockchainNodeInfo.getAllBlockchainNodeInfos(connection).stream()
			        .filter(b -> b.isEnabled())
			        .map(b -> b.getCode())
			        .forEach(c -> startChainNodeIngestor(chainNodeManager.getChainNode(connection, c)));
			    } catch (SQLException e) {
			        LOGGER.error("SQLException caught in activeNodeWatcher thread.", e);
			    }
			    
			    ConcurrencyUtilities.sleep(ACTIVE_NODE_WATCHER_SLEEP_TIME);
			}
		}, "ActiveNodeWatcher").start();
	}
}
