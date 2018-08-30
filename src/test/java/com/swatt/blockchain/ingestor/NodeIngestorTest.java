package com.swatt.blockchain.ingestor;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.repository.BlockDataRepository;
import com.swatt.util.general.ConcurrencyUtilities;
import com.swatt.util.sql.ConnectionPool;

@RunWith(MockitoJUnitRunner.class)
public class NodeIngestorTest {

	@Mock
	private Connection connection;
	
	@Mock
	private Node node;
	
	@Mock
	private ConnectionPool connectionPool;
	
	@Mock
	private BlockDataRepository blockDataRepository;
	
	private NodeIngestor createNodeIngestor(String code) {
		return createNodeIngestor(code, false, 0L, 100L);
	}
	
	private NodeIngestor createNodeIngestor(String code, Long start, Long end) {
		return createNodeIngestor(code, false, start, end);
	}
	
	private NodeIngestor createNodeIngestor(String code, boolean overwrite) {
		return createNodeIngestor(code, overwrite, 0L, 100L);
	}
	
	private NodeIngestor createNodeIngestor(String code, boolean overwrite, Long start, Long end) {
		NodeIngestorConfig nodeIngestorConfig = new NodeIngestorConfig();
		nodeIngestorConfig.setBlockchainCode(code);
		nodeIngestorConfig.setStartHeight(start);
		nodeIngestorConfig.setEndHeight(end);
		nodeIngestorConfig.setOverwriteExisting(overwrite);
		
		NodeIngestor nodeIngestor = new NodeIngestor(node, connectionPool, blockDataRepository, nodeIngestorConfig);
		nodeIngestor.init();
		
		return nodeIngestor;
	}
	
	@Test
	public void testNodeIngestor() {
		NodeIngestor nodeIngestor = new NodeIngestor(node, connectionPool, blockDataRepository, new NodeIngestorConfig());
		verify(node).addNodeListener(nodeIngestor);
	}

	@Test
	public void testInit() {
		NodeIngestor nodeIngestor = new NodeIngestor(node, connectionPool, blockDataRepository, new NodeIngestorConfig());
		nodeIngestor.init();
	}

	@Test
	public void testNewBlockAvailable() throws Exception {
		NodeIngestor nodeIngestor = createNodeIngestor("BTC");
		
		BlockData blockData = new BlockData();
		nodeIngestor.newBlockAvailable(node, blockData);
		
		verify(blockDataRepository).insert(blockData);
	}

	@Test
	public void testIngestBlockNotExists() throws Exception {
		String code = "XLM";
		long height = 100L;
		
		BlockData blockData = new BlockData();

		NodeIngestor nodeIngestor = createNodeIngestor(code);
		when(node.fetchBlockData(height)).thenReturn(blockData);
		nodeIngestor.ingestBlock(height);
		verify(blockDataRepository).insert(blockData);
	}
	
	@Test
	public void testIngestBlockExists() throws Exception {
		String code = "BTC";
		long height = 101L;
		
		BlockData blockData = new BlockData();
		
		NodeIngestor nodeIngestor = createNodeIngestor(code, true);
		when(node.getBlockchainCode()).thenReturn(code);
		when(blockDataRepository.findByBlockchainCodeAndHeight(code, height)).thenReturn(blockData);
		when(node.fetchBlockData(height)).thenReturn(blockData);
		nodeIngestor.ingestBlock(height);
		verify(blockDataRepository).replace(blockData);
	}

	@Test
	public void testStart() throws Exception {
		String code = "ETH";
		
		Long start = 1L;
		Long end = 5L;
		
		NodeIngestor nodeIngestor = createNodeIngestor(code, start, end);

		BlockData blockData1 = new BlockData();
		BlockData blockData2 = new BlockData();
		BlockData blockData3 = new BlockData();
		BlockData blockData4 = new BlockData();
		
		when(node.fetchBlockData(1L)).thenReturn(blockData1);
		when(node.fetchBlockData(2L)).thenReturn(blockData2);
		when(node.fetchBlockData(3L)).thenReturn(blockData3);
		when(node.fetchBlockData(4L)).thenReturn(blockData4);
		
		when(connectionPool.getConnection()).thenReturn(connection);
		when(node.getBlockchainCode()).thenReturn(code);

		nodeIngestor.start();
		
		// sleep is necessary to allow threads to finish (test will not succeed without this)
		ConcurrencyUtilities.sleep(1);

		verify(blockDataRepository).findByBlockchainCodeAndHeight(code, 1L);
		verify(blockDataRepository).findByBlockchainCodeAndHeight(code, 2L);
		verify(blockDataRepository).findByBlockchainCodeAndHeight(code, 3L);
		verify(blockDataRepository).findByBlockchainCodeAndHeight(code, 4L);

		verify(blockDataRepository).insert(blockData1);
		verify(blockDataRepository).insert(blockData2);
		verify(blockDataRepository).insert(blockData3);
		verify(blockDataRepository).insert(blockData4);
		
		verify(node).fetchNewBlocks();
	}

}
