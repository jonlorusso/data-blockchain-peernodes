package com.swatt.blockchain.node;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.blockchain.entity.BlockData;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.json.JsonRpcHttpClientPool;

import java.lang.reflect.ParameterizedType;
import java.time.Instant;
import java.util.concurrent.ExecutionException;

public abstract class JsonRpcHttpClientNode<T, S> extends PollingBlockNode {

    protected JsonRpcHttpClientPool jsonRpcHttpClientPool;
    
    private Class<T> rpcResultBlockClass;
    private Class<S> rpcResultTransasctionClass;

    LoadingCache<String, S> transactionCache = CacheBuilder.newBuilder()
            .maximumSize(5000)
            .build(new CacheLoader<String, S>() {
                public S load(String hash) {
                    return _fetchTransaction(hash);
                }
            });

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

    protected Object[] getBlockByHashRpcMethodParameters(String hash) throws OperationFailedException {
        return new Object[] { hash };
    }
    
    @Override
    public BlockData fetchBlockDataByHash(String blockHash) throws OperationFailedException {
        long start = Instant.now().getEpochSecond();

        BlockData blockData = toBlockData(fetchBlock(blockHash));

        blockData.setIndexed(Instant.now().toEpochMilli());
        blockData.setIndexingDuration(Instant.now().getEpochSecond() - start);
        
        // FIXME provide a setBlockchainNodeInfo method on BlockData which copies all relevant info
        blockData.setBlockchainCode(blockchainNodeInfo.getCode());
        blockData.setScalingPowers(super.getDifficultyScaling(), super.getRewardScaling(), super.getFeeScaling(), super.getAmountScaling());

        nodeListeners.stream().forEach(n -> n.blockFetched(this, blockData));
        return blockData;
    }
    
    protected Object[] getBlockByHashRpcParameters(String hash) {
        return new Object[] { hash };
    }
    
    protected T fetchBlock(String hash) throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();
        
        try {
            return jsonRpcHttpClient.invoke(getBlockByHashRpcMethodName(), getBlockByHashRpcMethodParameters(hash), this.rpcResultBlockClass);
        } catch (Throwable e) {
            throw new OperationFailedException("Error fetching block", e);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }
    
    protected abstract String getTransactionRpcMethodName() throws OperationFailedException;

    protected Object[] getTransactionRpcMethodParameters(String hash) throws OperationFailedException {
        return new Object[] { hash };
    }

    protected S _fetchTransaction(String hash) {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        try {
            return jsonRpcHttpClient.invoke(getTransactionRpcMethodName(), getTransactionRpcMethodParameters(hash), this.rpcResultTransasctionClass);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }

    protected S fetchTransaction(String hash) {
        try {
            return transactionCache.get(hash);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public NodeTransaction fetchTransactionByHash(String hash, boolean calculate) throws OperationFailedException {
        return toNodeTransaction(fetchTransaction(hash));
    }
    
    protected abstract NodeTransaction toNodeTransaction(S rpcResultTransaction) throws OperationFailedException;

    protected abstract String getBlockByHeightRpcMethodName() throws OperationFailedException;

    protected Object[] getBlockByHeightRpcMethodParameters(long blockNumber) throws OperationFailedException {
        return new Object[] { blockNumber };
    }
    
    protected T fetchBlock(long blockNumber) throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();
        
        try {
            return jsonRpcHttpClient.invoke(getBlockByHeightRpcMethodName(), getBlockByHeightRpcMethodParameters(blockNumber), this.rpcResultBlockClass);
        } catch (Throwable e) {
            throw new OperationFailedException("Error fetching block", e);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }

    @Override
    public BlockData fetchBlockData(long blockNumber, boolean notifyListeners) throws OperationFailedException {
        long start = Instant.now().getEpochSecond();

        BlockData blockData = toBlockData(blockNumber, fetchBlock(blockNumber));

        // FIXME this needs to happen before any fields are set.
        //blockData.setScalingPowers(super.getDifficultyScaling(), super.getRewardScaling(), super.getFeeScaling(), super.getAmountScaling());
        
        blockData.setBlockchainCode(blockchainNodeInfo.getCode());
        blockData.setIndexed(Instant.now().toEpochMilli());
        blockData.setIndexingDuration(Instant.now().getEpochSecond() - start);

        if (notifyListeners)
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
