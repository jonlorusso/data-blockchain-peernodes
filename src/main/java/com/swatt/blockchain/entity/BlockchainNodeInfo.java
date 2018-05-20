package com.swatt.blockchain.entity;

import javax.persistence.Column;
import javax.persistence.Id;

public class BlockchainNodeInfo extends Entity {
    
    @Id
    @Column
    private String code;
    
    @Column
    private String forkCode;
    
    @Column
    private String name;
    
    @Column
    private String description;
    
    @Column
    private String units;
    
    @Column
    private String txFeeUnits;
    
    @Column
    private String ip;
    
    @Column
    private int port;
    
    @Column
    private int forwardedPort;
    
    @Column
    private String rpcUn;
    
    @Column
    private String rpcPw;
    
    @Column
    private String className;
    
    @Column
    private int difficultyScaling;
    
    @Column
    private int rewardScaling;
    
    @Column
    private int feeScaling;
    
    @Column
    private int amountScaling;
    
    @Column
    private int zmqPort;
    
    @Column
    private boolean enabled;

    public BlockchainNodeInfo() {
    }

    public BlockchainNodeInfo(String code, String forkCode, String name, String description, String units,
            String txFeeUnits, String ip, int port, int forwardedPort, String rpcUn, String rpcPw, String className,
            int difficultyScaling, int rewardScaling, int feeScaling, int amountScaling, int zmqPort, boolean enabled) {
        this.code = code;
        this.forkCode = forkCode;
        this.name = name;
        this.description = description;
        this.units = units;
        this.txFeeUnits = txFeeUnits;
        this.ip = ip;
        this.port = port;
        this.forwardedPort = forwardedPort;
        this.rpcUn = rpcUn;
        this.rpcPw = rpcPw;
        this.className = className;
        this.difficultyScaling = difficultyScaling;
        this.rewardScaling = rewardScaling;
        this.feeScaling = feeScaling;
        this.amountScaling = amountScaling;
        this.zmqPort = zmqPort;
        this.enabled = enabled;
    }

    public final String getCode() {
        return code;
    }

    public final String getForkCode() {
        return forkCode;
    }

    public final String getName() {
        return name;
    }

    public final String getDescription() {
        return description;
    }

    public final String getUnits() {
        return units;
    }

    public final String getTxFeeUnits() {
        return txFeeUnits;
    }

    public final String getIp() {
        return ip;
    }

    public final int getPort() {
        return port;
    }

    public final int getForwardedPort() {
        return forwardedPort;
    }

    public final String getRpcUn() {
        return rpcUn;
    }

    public final String getRpcPw() {
        return rpcPw;
    }

    public final String getClassName() {
        return className;
    }

    public final int getDifficultyScaling() {
        return difficultyScaling;
    }

    public final int getRewardScaling() {
        return rewardScaling;
    }

    public final int getFeeScaling() {
        return feeScaling;
    }

    public final int getAmountScaling() {
        return amountScaling;
    }

    public int getZmqPort() {
        return zmqPort;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public final void setCode(String code) {
        this.code = code;
    }

    public final void setForkCode(String forkCode) {
        this.forkCode = forkCode;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public final void setUnits(String units) {
        this.units = units;
    }

    public final void setTxFeeUnits(String txFeeUnits) {
        this.txFeeUnits = txFeeUnits;
    }

    public final void setIp(String ip) {
        this.ip = ip;
    }

    public final void setPort(int port) {
        this.port = port;
    }

    public final void setForwardedPort(int forwardedPort) {
        this.forwardedPort = forwardedPort;
    }

    public final void setRpcUn(String rpcUn) {
        this.rpcUn = rpcUn;
    }

    public final void setRpcPw(String rpcPw) {
        this.rpcPw = rpcPw;
    }

    public final void setClassName(String className) {
        this.className = className;
    }

    public final void setDifficultyScaling(int difficultyScaling) {
        this.difficultyScaling = difficultyScaling;
    }

    public final void setRewardScaling(int rewardScaling) {
        this.rewardScaling = rewardScaling;
    }

    public final void setFeeScaling(int feeScaling) {
        this.feeScaling = feeScaling;
    }

    public final void setAmountScaling(int amountScaling) {
        this.amountScaling = amountScaling;
    }

    public void setZmqPort(int zmqPort) {
        this.zmqPort = zmqPort;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "BlockchainNodeInfo [code=" + code + ", forkCode=" + forkCode + ", name=" + name + ", description=" + description + ", units=" + units + ", txFeeUnits=" + txFeeUnits + ", ip=" + ip + ", port=" + port + ", forwardedPort=" + forwardedPort + ", rpcUn=" + rpcUn + ", rpcPw=" + rpcPw + ", className=" + className + ", difficultyScaling=" + difficultyScaling + ", rewardScaling=" + rewardScaling + ", feeScaling=" + feeScaling + ", amountScaling=" + amountScaling + ", zmqPort=" + zmqPort + ", enabled=" + enabled + "]";
    }
}
