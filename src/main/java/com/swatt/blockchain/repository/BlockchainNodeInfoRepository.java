package com.swatt.blockchain.repository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.swatt.blockchain.entity.BlockchainNodeInfo;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.sql.ConnectionPool;

public class BlockchainNodeInfoRepository extends CrudRepository<BlockchainNodeInfo> {

    public BlockchainNodeInfoRepository(ConnectionPool connectionPool) {
        super(connectionPool);
    }
    
    public List<BlockchainNodeInfo> findAllByEnabled(boolean enabled) throws OperationFailedException, SQLException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ENABLED", enabled);
        return findAllBy(parameters);
    }

    public BlockchainNodeInfo findByCode(String code) throws OperationFailedException, SQLException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("CODE", code);
        return findBy(parameters);
    }
}
