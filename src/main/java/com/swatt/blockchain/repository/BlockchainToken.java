package com.swatt.blockchain.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BlockchainToken {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockchainToken.class.getName());

    private String code;
    private String platformCode;
    private String name;
    private String smartContractAddress;
    private int decimals;

    private static final int COL_CODE = 1;
    private static final int COL_PLATFORM_CODE = 2;
    private static final int COL_NAME = 3;
    private static final int COL_SMART_CONTRACT_ADDRESS = 4;
    private static final int COL_DECIMALS = 5;

    public BlockchainToken(ResultSet rs) {
        try {
            this.code = rs.getString(COL_CODE);
            this.platformCode = rs.getString(COL_PLATFORM_CODE);
            this.name = rs.getString(COL_NAME);
            this.smartContractAddress = rs.getString(COL_SMART_CONTRACT_ADDRESS);
            this.decimals = rs.getInt(COL_DECIMALS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getCode() { return code; }
    public String getPlatformCode() { return platformCode; }
    public String getSmartContractAddress() { return smartContractAddress; }
    public String getName() { return name; }
    public int getDecimals() { return decimals; }
}
