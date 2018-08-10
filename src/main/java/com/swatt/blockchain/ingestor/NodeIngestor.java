package com.swatt.blockchain.ingestor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.entity.CheckProgress;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.node.NodeListener;
import com.swatt.blockchain.repository.BlockDataRepository;
import com.swatt.blockchain.repository.BlockchainNodeInfoRepository;
import com.swatt.blockchain.service.NodeManager;
import com.swatt.blockchain.util.DatabaseUtils;
import com.swatt.util.general.CollectionsUtilities;
import com.swatt.util.general.ConcurrencyUtilities;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.general.SystemUtilities;
import com.swatt.util.log.LoggerController;
import com.swatt.util.sql.ConnectionPool;

public class NodeIngestor implements NodeListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeIngestor.class);

    private Thread historicalIngestionThread;
    
    private Node node;
    private ConnectionPool connectionPool;
    private BlockDataRepository blockDataRepository;

    private boolean overwriteExisting = false;
    
    public NodeIngestor(Node node, ConnectionPool connectionPool, BlockDataRepository blockDataRepository) {
        super();

        this.node = node;
        this.connectionPool = connectionPool;
        this.blockDataRepository = blockDataRepository;
        
        node.addNodeListener(this);
    }
    
    public void setOverwriteExisting(boolean overwriteExisting) {
        this.overwriteExisting = overwriteExisting;
    }

    private boolean existsBlockData(long height) throws OperationFailedException, SQLException {
        return blockDataRepository.findByBlockchainCodeAndHeight(node.getBlockchainCode(), height) != null;
    }

    @Override
    public void newBlockAvailable(Node node, BlockData blockData) {
        try {
            if (!existsBlockData(blockData.getHeight()) || overwriteExisting) {
                blockDataRepository.insert(blockData);
                LOGGER.info(String.format("Synced new block: %d", blockData.getHeight()));
            }
        } catch (OperationFailedException | SQLException e) {
            // FIXME
            LOGGER.error("Exception caught while storing new block: " + e.getMessage());
        }
    }
    
    public boolean ingestBlock(long height) throws OperationFailedException, SQLException {
        if (existsBlockData(height) && overwriteExisting) {
            BlockData blockData = node.fetchBlockData(height);
            blockDataRepository.replace(blockData);
            LOGGER.info(String.format("Re-ingested block: %d", height));
            return true;
        } else if (!existsBlockData(height)) {
            BlockData blockData = node.fetchBlockData(height);
            blockDataRepository.insert(blockData);
            LOGGER.info(String.format("Ingested block: %d", height));
            return true;
        }
        
        return false;
    }

    public void start() {
        if (historicalIngestionThread == null) {
            historicalIngestionThread = new Thread(() -> {
                try (Connection connection = connectionPool.getConnection()) {
                    
                    long height = node.fetchBlockCount();
                    CheckProgress checkProgress = CheckProgress.call(connection, node.getCode());
                    long stopHeight = checkProgress.getBlockCount();
                    
                    if (height > stopHeight) 
                        LOGGER.info(String.format("Historical ingestion running for blocks: %d through %s", height, stopHeight));
                    
                    while (height > stopHeight) {
                        ingestBlock(height);
                        height = height - 1;
                    }
                    
                } catch (Throwable t) {
                    LOGGER.error("[" + node.getCode() + "] Historical ingestion failed.", t);
                    historicalIngestionThread = null;
                }
            }, "HistoricalIngestion-" + node.getCode());
            
            try {
                historicalIngestionThread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        node.fetchNewBlocks();
    }
    
    public static void ingest(String code, int start, int end, int numberOfThreads) throws IOException {
        Properties properties = CollectionsUtilities.loadProperties("config.properties");
        LoggerController.init(properties);
        
        ConnectionPool connectionPool = DatabaseUtils.configureConnectionPoolFromEnvironment(properties);

        int blocksPerThread = Math.abs(start - end) / numberOfThreads;
        LOGGER.info("Blocks per thread: " + blocksPerThread);
                
        if (start < end) {
            for (int i = 0; i < numberOfThreads; i++) {
                final int threadNumber = i;
                
                ConcurrencyUtilities.startThread(() -> {
                    long startms = Instant.now().toEpochMilli();
                    long blocksIngested = 0;
                    
                    LOGGER.info("Start nodeIngestion thread: " + (threadNumber * blocksPerThread) + " -> " + ((threadNumber + 1) * blocksPerThread - 1));
                    
                    BlockchainNodeInfoRepository blockchainNodeInfoRepository = new BlockchainNodeInfoRepository(connectionPool);
                    BlockDataRepository blockDataRepository = new BlockDataRepository(connectionPool);
                    
                    NodeManager nodeManager = new NodeManager(blockchainNodeInfoRepository);
                    Node node = nodeManager.getNode(code);
    
                    NodeIngestor nodeIngestor = new NodeIngestor(node, connectionPool, blockDataRepository);
                    nodeIngestor.setOverwriteExisting(SystemUtilities.getEnv("OVERWRITE_EXISTING", "false").equals("true"));
                    
                    for (long height = (threadNumber * blocksPerThread); height < ((threadNumber + 1) * blocksPerThread); height++) {
                        try {
                            if (nodeIngestor.ingestBlock(height)) {
                                blocksIngested++;
                                long now = Instant.now().toEpochMilli();
                                System.out.println("blocks/second: " + (blocksIngested / ((now - startms) / 1000)));
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } else {
            for (int i = numberOfThreads; i > 0; i--) {
                final int threadNumber = i;
                
                ConcurrencyUtilities.startThread(() -> {
                    long startms = Instant.now().toEpochMilli();
                    long blocksIngested = 0;
                    
                    LOGGER.info("Start nodeIngestion thread: " + (threadNumber * blocksPerThread - 1) + " -> " + ((threadNumber - 1) * blocksPerThread));
                    
                    BlockchainNodeInfoRepository blockchainNodeInfoRepository = new BlockchainNodeInfoRepository(connectionPool);
                    BlockDataRepository blockDataRepository = new BlockDataRepository(connectionPool);
                    
                    NodeManager nodeManager = new NodeManager(blockchainNodeInfoRepository);
                    Node node = nodeManager.getNode(code);
    
                    NodeIngestor nodeIngestor = new NodeIngestor(node, connectionPool, blockDataRepository);
                    nodeIngestor.setOverwriteExisting(SystemUtilities.getEnv("OVERWRITE_EXISTING", "false").equals("true"));

                    for (long height = (threadNumber * blocksPerThread - 1); height > ((threadNumber - 1) * blocksPerThread); height--) {
                        try {
                            if (nodeIngestor.ingestBlock(height)) {
                                blocksIngested++;
                                long now = Instant.now().toEpochMilli();
                                System.out.println("blocks/second: " + (blocksIngested / ((now - startms) / 1000)));
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }
    
    public static void main(String[] args) throws OperationFailedException, SQLException, IOException {
        try {
            String code = args[0];
            int start = Integer.valueOf(args[1]);
            int end = Integer.valueOf(args[2]);
            int numberOfThreads = Integer.valueOf(args[3]);

            LOGGER.info("Ingesting " + code + " blocks: " + start + " to " + end + ", " + numberOfThreads + " threads.");
            ingest(code, start, end, numberOfThreads);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
