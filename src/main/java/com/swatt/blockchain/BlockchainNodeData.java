package com.swatt.blockchain;

/** BlockchainNodeData is the result of a query about transactions over a specified period of time
 */
public class BlockchainNodeData {
	int numBlocks;
	int numTransactions;
	double largestTransactionFee;
	double smallestTransactionFee;
	double averageTransactionFee;
	double averageNumTransactionsPerBlock;
	
	public BlockchainNodeData(int numBlocks, int numTransactions, double largestTransactionFee, double smallestTransactionFee, double averageTransactionFee, double averageNumTransactionsPerBlock) {
		this.numBlocks = numBlocks;
		this.numTransactions = numTransactions;
		this.largestTransactionFee = largestTransactionFee;
		this.smallestTransactionFee = smallestTransactionFee;
		this.averageTransactionFee = averageTransactionFee;
		this.averageNumTransactionsPerBlock = averageNumTransactionsPerBlock;
	}

	
	public int getNumBlocks() { return numBlocks; }
	public int getNumTransactions() { return numTransactions; }
	public double getLargestTransactionFee() { return largestTransactionFee; }
	public double getSmallestTransactionFee() { return smallestTransactionFee; }
	public double getAverageTransactionFee() { return averageTransactionFee; }
	public double getAverageNumTransactionsPerBlock() { return averageNumTransactionsPerBlock; }

}
