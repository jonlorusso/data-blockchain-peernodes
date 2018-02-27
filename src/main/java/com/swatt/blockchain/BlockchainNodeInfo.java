package com.swatt.blockchain;

/** BlockchainNode is the constant data corresponding to a specific Blockchain type (such as Bitcoin, Etherium, etc)
 */
public class BlockchainNodeInfo {
	String ticker;
	String name;
	String description;
	String transactionUnits;
	String transactionFeeUnits;
	
	public BlockchainNodeInfo(String ticker, String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public String getTicker() { return ticker; }
	public String getName() { return name; }
	public String getDescription() { return description; }
	public String getUnits() { return transactionUnits; }
	public void setUnits(String units) { transactionUnits = units; }
	
	public String getTransactionFeeUnits() { return transactionFeeUnits; }	
	public void setTransactionFeeUnits(String units) { transactionFeeUnits = units; }
}