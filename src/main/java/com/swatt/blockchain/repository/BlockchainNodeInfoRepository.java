package com.swatt.blockchain.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.swatt.blockchain.entity.BlockchainNodeInfo;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.sql.ConnectionPool;

public class BlockchainNodeInfoRepository extends Repository {

    private static final String GET_BY_ENABLED_WHERE_CLAUSE = "ENABLED = ?";
    private static final String GET_BY_PLATFORM_CODE_WHERE_CLAUSE = "PLATFORM_CODE = ?";

    public BlockchainNodeInfoRepository(ConnectionPool connectionPool) {
        super(connectionPool);
    }

    public List<BlockchainNodeInfo> findAllByEnabled(boolean enabled) throws OperationFailedException, SQLException {
        try (Connection connection = this.connectionPool.getConnection()) {
            return BlockchainNodeInfo.getBlockchainNodeInfos(connection, GET_BY_ENABLED_WHERE_CLAUSE, enabled);
        }
    }

    public BlockchainNodeInfo findByCode(String code) throws OperationFailedException, SQLException {
        try (Connection connection = this.connectionPool.getConnection()) {
            return BlockchainNodeInfo.getBlockchainNodeInfo(connection, code);
        }
    }

    public List<BlockchainNodeInfo> findAllByPlatformCode(String platformCode) throws OperationFailedException, SQLException {
        try (Connection connection = this.connectionPool.getConnection()) {
            return BlockchainNodeInfo.getBlockchainNodeInfos(connection, GET_BY_PLATFORM_CODE_WHERE_CLAUSE, platformCode);
        }
    }
}
