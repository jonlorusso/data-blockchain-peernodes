package com.swatt.blockchain.repository;

import com.swatt.util.sql.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BlockchainTokenRepository extends Repository {

    private static final String BY_PLATFORM_CODE_QUERY = "SELECT CODE, PLATFORM_CODE, NAME, SMART_CONTRACT_ADDRESS FROM blockchain_token WHERE PLATFORM_CODE = ?";

    public BlockchainTokenRepository(ConnectionPool connectionPool) {
        super(connectionPool);
    }

    public List<BlockchainToken> findAllByPlatformCode(String platformCode) throws SQLException {
        try (Connection connection = connectionPool.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(BY_PLATFORM_CODE_QUERY);

            ps.setString(1, platformCode);

            ResultSet rs = ps.executeQuery();

            List<BlockchainToken> results = new ArrayList<>();
            while (rs.next())
                results.add(new BlockchainToken(rs));

            return results;
        }
    }
}
