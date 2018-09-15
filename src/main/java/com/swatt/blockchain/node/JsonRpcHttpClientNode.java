package com.swatt.blockchain.node;

import java.lang.reflect.ParameterizedType;
import java.time.Instant;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.blockchain.entity.BlockData;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.json.JsonRpcHttpClientPool;

public abstract class JsonRpcHttpClientNode<T, S> extends PollingBlockNode {
    
    protected JsonRpcHttpClientPool jsonRpcHttpClientPool;
    
    private Class<T> rpcResultBlockClass;
    private Class<S> rpcResultTransasctionClass;
    
    @SuppressWarnings("unchecked")
    public JsonRpcHttpClientNode() {
        super();
        
        this.rpcResultBlockClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.rpcResultTransasctionClass = (Class<S>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    @Override
    public void init() {
        String url = String.format("http://%s:%d", blockchainNodeInfo.getIp(), blockchainNodeInfo.getPort());
        
        int maxSize = 10; // TODO: Should get from chainNodeConfig
        jsonRpcHttpClientPool = new JsonRpcHttpClientPool(url, blockchainNodeInfo.getRpcUn(), blockchainNodeInfo.getRpcPw(), maxSize);
    }

    protected abstract String getBlockByHashRpcMethodName() throws OperationFailedException;
    
    @Override
    public BlockData fetchBlockDataByHash(String blockHash) throws OperationFailedException {
        long start = Instant.now().getEpochSecond();

        BlockData blockData = toBlockData(fetchBlock(blockHash));

        blockData.setIndexed(Instant.now().toEpochMilli());
        blockData.setIndexingDuration(Instant.now().getEpochSecond() - start);

        nodeListeners.stream().forEach(n -> n.blockFetched(this, blockData));
        return blockData;
    }
    
    protected T fetchBlock(String hash) throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();
        
        try {
            return jsonRpcHttpClient.invoke(getBlockByHashRpcMethodName(), new Object[] { hash }, this.rpcResultBlockClass);
        } catch (Throwable e) {
            throw new OperationFailedException("Error fetching block", e);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }
    
    protected abstract String getRpcTransactionMethodName() throws OperationFailedException;

    protected S fetchTransaction(String hash) throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        try {
            return jsonRpcHttpClient.invoke(getRpcTransactionMethodName(), new Object[] { hash }, this.rpcResultTransasctionClass);
        } catch (Throwable e) {
            throw new OperationFailedException(e);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        } 
    }
    
    @Override
    public NodeTransaction fetchTransactionByHash(String hash, boolean calculate) throws OperationFailedException {
        return toNodeTransaction(fetchTransaction(hash));
    }
    
    protected abstract NodeTransaction toNodeTransaction(S rpcResultTransaction) throws OperationFailedException;

    protected abstract String getBlockByHeightRpcMethodName() throws OperationFailedException;
    
    protected T fetchBlock(long blockNumber) throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();
        
        try {
            return jsonRpcHttpClient.invoke(getBlockByHeightRpcMethodName(), new Object[] { blockNumber }, this.rpcResultBlockClass);
        } catch (Throwable e) {
            throw new OperationFailedException("Error fetching block", e);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }

    @Override
    public BlockData fetchBlockData(long blockNumber) throws OperationFailedException {
        long start = Instant.now().getEpochSecond();

        BlockData blockData = toBlockData(blockNumber, fetchBlock(blockNumber));

        blockData.setIndexed(Instant.now().toEpochMilli());
        blockData.setIndexingDuration(Instant.now().getEpochSecond() - start);

        nodeListeners.stream().forEach(n -> n.blockFetched(this, blockData));
        return blockData;
    }

    protected abstract BlockData toBlockData(T rpcResultBlock) throws OperationFailedException;

    protected BlockData toBlockData(long height, T resultBlock) throws OperationFailedException {
        BlockData blockData = toBlockData(resultBlock);
        blockData.setHeight(height);
        return blockData;
    }

    protected <V> V invoke(String methodName, Object[] params, Class<V> clazz) throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        try {
            return jsonRpcHttpClient.invoke(methodName, params, clazz);
        } catch (Throwable e) {
            throw new OperationFailedException(e);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }
}
