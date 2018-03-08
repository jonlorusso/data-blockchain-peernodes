package com.swatt.chainNode;

import java.util.logging.Logger;

public class Transaction  {
    private static final Logger LOGGER = Logger.getLogger(Transaction.class.getName());
    
    private String hash;				// All
    private double fee;					// All
    private double inValue;				// All
    private boolean minted = false;		// All
    private double feeRate;				// Bitcoin?
    private long timestamp;				// All
    private long size;					// All
    private double amount;				//  ?? Christian had this be lazily synthetic
    private double outputValue;			// Bitcoin?

    public Transaction(String hash) {
        this.hash = hash;
    }
    
	public final String getHash() { return hash; }
	public final void setHash(String hash) { this.hash = hash; }
	public final double getFee() { return fee; }
	public final void setFee(double fee) { this.fee = fee; }
	public final double getInValue() { return inValue; }
	public final void setInValue(double inValue) { this.inValue = inValue; }
	public final boolean isMinted() { return minted; }
	public final void setMinted(boolean minted) { this.minted = minted; }
	public final double getFeeRate() { return feeRate; }
	public final void setFeeRate(double feeRate) { this.feeRate = feeRate; }
	public final long getTimestamp() { return timestamp; }
	public final void setTimestamp(long timestamp) { this.timestamp = timestamp; }
	public final long getSize() { return size; }
	public final void setSize(long size) { this.size = size; }
	public final double getAmount() { return amount; }
	public final void setAmount(double amount) { this.amount = amount; }
	public final double getOutputValue() { return outputValue; }
	public final void setOutputValue(double outputValue) { this.outputValue = outputValue; }


//    public double getAmount() { return amount; }
//
//    public Long getTimestamp() {
//        return rpcTransaction.time;
//    }
//
//    public Double outputValue(int i) {
//        return rpcTransaction.vout.get(i).value;
//    }
//
//    public Long getSize() {
//        Long size = null;
//
//        if (rpcTransaction.vsize != null) {
//            size = rpcTransaction.vsize;
//        } else {
//            size = rpcTransaction.size;
//        }
//
//        return size;
//    }
//
//    public Double getFeeRate() {
//        Double fee = null;
//        try {
//            fee = 1000 * (this.getFee() / this.getSize());
//        } catch (Exception e) {
//            LOGGER.log(Level.SEVERE, e.getMessage(), e);
//        }
//
//        return fee;
//    }
//
//    public String getBlockhash() {
//        return rpcTransaction.blockhash;
//    }
//
//    private void fetchFromBlockchain(JsonRpcHttpClient jsonrpcClient, String transactionHash) {
//        try {
//            rpcTransaction = jsonrpcClient.invoke(BTCMethods.GET_RAW_TRANSACTION,
//                    new Object[] { transactionHash, true }, RPCTransaction.class);
//
//        } catch (JsonRpcClientException e) {
//            e.printStackTrace();
//            LOGGER.log(Level.SEVERE, e.getMessage(), e);
//        } catch (Throwable e) {
//            LOGGER.log(Level.SEVERE, e.getMessage(), e);
//        }
//    }

}
