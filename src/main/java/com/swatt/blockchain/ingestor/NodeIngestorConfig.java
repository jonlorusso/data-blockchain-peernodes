package com.swatt.blockchain.ingestor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.swatt.blockchain.entity.BlockchainNodeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeIngestorConfig {

	private String blockchainCode;
	private int numberOfThreads = 1;
	private boolean overwriteExisting = false;
	private Long startHeight;
	private Long endHeight;
	
	public NodeIngestorConfig() {
		super();
	}

	public int getNumberOfThreads() {
		return numberOfThreads;
	}

	public void setNumberOfThreads(int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}

	public boolean isOverwriteExisting() {
		return overwriteExisting;
	}

	public void setOverwriteExisting(boolean overwriteExisting) {
		this.overwriteExisting = overwriteExisting;
	}

	public Long getStartHeight() {
		return startHeight;
	}

	public void setStartHeight(Long startHeight) {
		this.startHeight = startHeight;
	}

	public Long getEndHeight() {
		return endHeight;
	}

	public void setEndHeight(Long endHeight) {
		this.endHeight = endHeight;
	}

	public String getBlockchainCode() {
		return blockchainCode;
	}

	public void setBlockchainCode(String blockchainCode) {
		this.blockchainCode = blockchainCode;
	}
}
