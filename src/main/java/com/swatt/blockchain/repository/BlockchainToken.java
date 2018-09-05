package com.swatt.blockchain.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Token {

    private String code;
    private String platformCode;
    private String name;
    private String smartContractAddress;

    private static final int COL_CODE = 1;
    private static final int COL_PLATFORM_CODE = 2;
    private static final int COL_NAME = 3;
    private static final int COL_SMART_CONTRACT_ADDRESS = 4;

    public Token(ResultSet rs) {
        try {
            this.code = rs.getString(COL_CODE);
            this.platformCode = rs.getString(COL_PLATFORM_CODE);
            this.name = rs.getString(COL_NAME);
            this.smartContractAddress = rs.getString(COL_SMART_CONTRACT_ADDRESS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public String getPlatformCode() {
        return platformCode;
    }

    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode;
    }

    public String getSmartContractAddress() {
        return smartContractAddress;
    }

    public void setSmartContractAddress(String smartContractAddress) {
        this.smartContractAddress = smartContractAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
