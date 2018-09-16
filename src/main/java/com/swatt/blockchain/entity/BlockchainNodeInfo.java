package com.swatt.blockchain.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.swatt.util.general.StringUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockchainNodeInfo {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockchainNodeInfo.class.getName());

    private static final int COL_CODE = 1;
    private static final int COL_FORK_CODE = 2;
    private static final int COL_PLATFORM_CODE = 3;
    private static final int COL_NAME = 4;
    private static final int COL_DESCRIPTION = 5;
    private static final int COL_UNITS = 6;
    private static final int COL_TX_FEE_UNITS = 7;
    private static final int COL_IP = 8;
    private static final int COL_PORT = 9;
    private static final int COL_FORWARDED_PORT = 10;
    private static final int COL_RPC_UN = 11;
    private static final int COL_RPC_PW = 12;
    private static final int COL_CLASS_NAME = 13;
    private static final int COL_DIFFICULTY_SCALING = 14;
    private static final int COL_REWARD_SCALING = 15;
    private static final int COL_FEE_SCALING = 16;
    private static final int COL_AMOUNT_SCALING = 17;
    private static final int COL_ZMQ_PORT = 18;
    private static final int COL_ENABLED = 19;
    private static final int COL_SMART_CONTRACT_ADDRESS = 20;
    private static final int COL_DECIMALS = 21;

    private String code;
    private String forkCode;
    private String platformCode;
    private String name;
    private String description;
    private String units;
    private String txFeeUnits;
    private String ip;
    private int port;
    private int forwardedPort;
    private String rpcUn;
    private String rpcPw;
    private String className;
    private int difficultyScaling;
    private int rewardScaling;
    private int feeScaling;
    private int amountScaling;
    private int zmqPort;
    private boolean enabled;
    private String smartContractAddress;
    private int decimals;

    public BlockchainNodeInfo() {
        super();
    }

    public BlockchainNodeInfo(ResultSet rs) {
        try {
            this.code = rs.getString(COL_CODE);
            this.forkCode = rs.getString(COL_FORK_CODE);
            this.platformCode = rs.getString(COL_PLATFORM_CODE);
            this.name = rs.getString(COL_NAME);
            this.description = rs.getString(COL_DESCRIPTION);
            this.units = rs.getString(COL_UNITS);
            this.txFeeUnits = rs.getString(COL_TX_FEE_UNITS);
            this.ip = rs.getString(COL_IP);
            this.port = rs.getInt(COL_PORT);
            this.forwardedPort = rs.getInt(COL_FORWARDED_PORT);
            this.rpcUn = rs.getString(COL_RPC_UN);
            this.rpcPw = rs.getString(COL_RPC_PW);
            this.className = rs.getString(COL_CLASS_NAME);
            this.difficultyScaling = rs.getInt(COL_DIFFICULTY_SCALING);
            this.rewardScaling = rs.getInt(COL_REWARD_SCALING);
            this.feeScaling = rs.getInt(COL_FEE_SCALING);
            this.amountScaling = rs.getInt(COL_AMOUNT_SCALING);
            this.zmqPort = rs.getInt(COL_ZMQ_PORT);
            this.enabled = rs.getBoolean(COL_ENABLED);
            this.smartContractAddress = rs.getString(COL_SMART_CONTRACT_ADDRESS);
            this.decimals = rs.getInt(COL_DECIMALS);
        } catch (SQLException e) {
            LOGGER.error("Unable to read BlockchainToken from resultSet", e);
        }
    }

    public String getCode() {
        return code;
    }
    public String getForkCode() {
        return forkCode;
    }

    public String getPlatformCode() {
        return platformCode;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUnits() {
        return units;
    }

    public String getTxFeeUnits() {
        return txFeeUnits;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getForwardedPort() {
        return forwardedPort;
    }

    public String getRpcUn() {
        return rpcUn;
    }

    public String getRpcPw() {
        return rpcPw;
    }

    public String getClassName() {
        return className;
    }

    public int getDifficultyScaling() {
        return difficultyScaling;
    }

    public int getRewardScaling() {
        return rewardScaling;
    }

    public int getFeeScaling() {
        return feeScaling;
    }

    public int getAmountScaling() {
        return amountScaling;
    }

    public int getZmqPort() {
        return zmqPort;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getSmartContractAddress() {
        return smartContractAddress;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static String getSqlColumnList() {
        return "CODE, FORK_CODE, PLATFORM_CODE, NAME, DESCRIPTION, UNITS, TX_FEE_UNITS, IP, PORT, FORWARDED_PORT, RPC_UN, RPC_PW, CLASS_NAME, DIFFICULTY_SCALING, REWARD_SCALING, FEE_SCALING, AMOUNT_SCALING, ZMQ_PORT, ENABLED, SMART_CONTRACT_ADDRESS, DECIMALS";
    }

    public boolean isToken() {
        return !StringUtilities.isNullOrAllWhiteSpace(platformCode);
    }
}