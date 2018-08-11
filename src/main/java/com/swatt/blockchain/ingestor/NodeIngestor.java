package com.swatt.blockchain.ingestor;

import static java.util.stream.IntStream.range;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.IntStream;

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
    
    static IntStream reverseRange(int from, int to) {
        return IntStream.range(from, to).map(i -> to - i + from - 1);
    }
    
    public static void ingest(String code, int start, int end, int numberOfThreads) throws IOException {
        Properties properties = CollectionsUtilities.loadProperties("config.properties");
        LoggerController.init(properties);
        
        ConnectionPool connectionPool = DatabaseUtils.configureConnectionPoolFromEnvironment(properties);

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads, new ThreadFactory() {
        	private int i = 0;

        	@Override
			public Thread newThread(Runnable r) {
				i++;
				return new Thread(r, code + "-NodeIngestor-" + i);
			}
		});
        
        BlockchainNodeInfoRepository blockchainNodeInfoRepository = new BlockchainNodeInfoRepository(connectionPool);
        BlockDataRepository blockDataRepository = new BlockDataRepository(connectionPool);
        
        NodeManager nodeManager = new NodeManager(blockchainNodeInfoRepository);
        Node node = nodeManager.getNode(code);

        NodeIngestor nodeIngestor = new NodeIngestor(node, connectionPool, blockDataRepository);
        nodeIngestor.setOverwriteExisting(SystemUtilities.getEnv("OVERWRITE_EXISTING", "false").equals("true"));
        
        IntStream intStream = start < end ? range(start, end) : reverseRange(end, start);
		intStream.forEach(height -> {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						nodeIngestor.ingestBlock(height);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			});
		});
		
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
