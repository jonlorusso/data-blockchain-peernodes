package com.swatt.blockchain.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.swatt.blockchain.entity.BlockchainNodeInfo;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.sql.ConnectionPool;

public class BlockchainNodeInfoRepository extends Repository {

    private static final String QUERY = "SELECT " + BlockchainNodeInfo.getSqlColumnList() + " FROM BLOCKCHAIN_NODE_INFO";

    private static final String GET_BY_ENABLED_WHERE_CLAUSE = "ENABLED = ?";
    private static final String GET_BY_PLATFORM_CODE_WHERE_CLAUSE = "PLATFORM_CODE = ?";
    private static final String GET_PLATFORMS_WHERE_CLAUSE = "CODE in (SELECT DISTINCT PLATFORM_CODE FROM BLOCKCHAIN_NODE_INFO)";

    public BlockchainNodeInfoRepository(ConnectionPool connectionPool) {
        super(connectionPool);
    }

    public List<BlockchainNodeInfo> findAllByEnabled(boolean enabled) throws SQLException {
        try (Connection connection = this.connectionPool.getConnection()) {
            return getBlockchainNodeInfos(connection, GET_BY_ENABLED_WHERE_CLAUSE, enabled);
        }
    }

    public BlockchainNodeInfo findByCode(String code) throws SQLException {
        try (Connection connection = this.connectionPool.getConnection()) {
            return getBlockchainNodeInfo(connection, code);
        }
    }

    public List<BlockchainNodeInfo> findAllByPlatformCode(String platformCode) throws SQLException {
        try (Connection connection = this.connectionPool.getConnection()) {
            return getBlockchainNodeInfos(connection, GET_BY_PLATFORM_CODE_WHERE_CLAUSE, platformCode);
        }
    }

    public List<BlockchainNodeInfo> findAllPlatforms() throws SQLException {
        try (Connection connection = this.connectionPool.getConnection()) {
            return getBlockchainNodeInfos(connection, GET_PLATFORMS_WHERE_CLAUSE);
        }
    }

    private BlockchainNodeInfo getBlockchainNodeInfo(Connection connection, String code) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(QUERY + " WHERE CODE = ?");

        ps.setString(1, code);

        ResultSet rs = ps.executeQuery();

        if (rs.next())
            return new BlockchainNodeInfo(rs);
        else
            return null;
    }

    private List<BlockchainNodeInfo> getBlockchainNodeInfos(Connection connection, String where, Object... params) throws SQLException {
        String query = QUERY;

        if (where != null)
            query += " WHERE " + where;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            int parameterIndex = 1;
            for (Object param : params) {
                ps.setObject(parameterIndex++, param);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return getBlockchainNodeInfos(rs);
            }
        }
    }

    private List<BlockchainNodeInfo> getBlockchainNodeInfos(ResultSet rs) throws SQLException {
        List<BlockchainNodeInfo> results = new ArrayList<BlockchainNodeInfo>(100);

        while (rs.next())
            results.add(new BlockchainNodeInfo(rs));

        return results;
    }
}
