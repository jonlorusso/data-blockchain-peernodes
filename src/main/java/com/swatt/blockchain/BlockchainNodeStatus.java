package com.swatt.blockchain;

public class BlockchainNodeStatus {
   Integer transactionRate;
   Integer supportedRate;
   Integer poolSize;
   Long averagePendingTransactionFee;
   BlockchainTransaction oldestPendingTransaction;
   BlockchainTransaction highestPendingTransaction;

   public BlockchainNodeStatus(){}
   
   //Average number of transactions per second
   public Integer getTransactionRate() { return transactionRate; }

   //Maximum supported transactions per second
   public Integer getSupportedTransactionRate() { return supportedRate; }

   //Maximum supported transactions per second
   //TODO date range?
   public Integer getPoolSize() { return poolSize; }

   public BlockchainTransaction getOldestPendingTransaction() { return oldestPendingTransaction; } 
   public BlockchainTransaction getHighestPendingTransaction() { return highestPendingTransaction; }
   public Long averagePendingTransactionFee() { return averagePendingTransactionFee; } 
}
