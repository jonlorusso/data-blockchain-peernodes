package com.swatt.blockchain;

public interface IBlockchainNode {
    //abstract void onboardBlockchain() throws BlockchainNodeException;       // This is a one time onboarding to seed our timeseries data.  If already done it will throw an exception
     
    /* Get the general information about this Blockchain Node
     *
     */
    abstract BlockchainNodeInfo getInfo();
 
    /*
     *  Get data about the transactions on this Blockchain over a specified time interval
     */
    abstract BlockchainNodeData getDataForInterval(long fromTime, long toTime) ; // Assume EPOCH times in millisecs
 
    /*
     *    NOTE: These are CONCRETE (not abstract) implementations that are getting info from our internal time-series aggregate DB
     *          To be clear, the only time we're hitting the Blockchain is to get current data and/or to populate our time-series
    */
    //public final BlockchainNodeData getDaysData(int day) { ... } ; // TBD if this is the right time unit parameters
    //public final BlockchainNodeData[] getDays(long fromTime, long toTime) { ... } // Assume EPOCH times in millisecs
 
 
 
    abstract BlockchainTransaction findTransactionByHash(String hash);
    abstract BlockchainTransaction findTransactionByAddress(String address);
}