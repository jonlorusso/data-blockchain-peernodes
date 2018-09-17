package com.swatt.blockchain.node;

import static java.lang.String.format;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.time.Instant;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swatt.blockchain.entity.BlockData;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.io.IoUtilities;
import com.swatt.util.json.HttpClientPool;

public abstract class HttpClientNode<T, S> extends PollingBlockNode {
    
    private Class<T> httpResultBlockClass;
    private Class<S> httpResultTransasctionClass;
    
    private HttpClientPool httpClientPool;
    protected String baseUrl;
    protected ObjectMapper objectMapper;

    @SuppressWarnings("unchecked")
    public HttpClientNode() {
        this.httpResultBlockClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.httpResultTransasctionClass = (Class<S>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    public void init() {
        int maxSize = 10; // TODO get from BlockchainNodeInfo
        httpClientPool = new HttpClientPool(maxSize);
        objectMapper = new ObjectMapper();
        baseUrl = format("http://%s:%d/",  blockchainNodeInfo.getIp(), blockchainNodeInfo.getPort());
    }

    protected HttpResponse execute(String url) {
        return httpClientPool.execute(url);
    }

    protected BlockData toBlockData(long height, T resultBlock) throws OperationFailedException {
        BlockData blockData = toBlockData(resultBlock);
        blockData.setHeight(height);
        return blockData;
    }
    
    protected abstract BlockData toBlockData(T resultBlock) throws OperationFailedException;
    protected abstract NodeTransaction toNodeTransaction(S resultTransaction) throws OperationFailedException;
    
    protected abstract String getTransactionByHashUrl(String transactionHash);
    protected abstract String getBlockByHashUrl(String blockHash);
    protected abstract String getBlockByHeightUrl(long height);
    
    protected <V> V fetch(String path, Class<V> clazz) throws OperationFailedException {
        HttpResponse response = execute(format("%s/%s", baseUrl, path));

        try {
            return objectMapper.readValue(readResponse(response), clazz);
        } catch (IOException e) {
            throw new OperationFailedException(e);
        }
    }
    
    protected <V> V fetch(String path, TypeReference<V> typeReference) throws OperationFailedException {
        HttpResponse response = execute(format("%s/%s", baseUrl, path));

        try {
            return objectMapper.readValue(readResponse(response), typeReference);
        } catch (IOException e) {
            throw new OperationFailedException(e);
        }
    }
    
    protected <V> V fetch(String path, String jsonPath, Class<V> clazz) throws OperationFailedException {
        HttpResponse response = execute(format("%s/%s", baseUrl, path));

        try {
            return objectMapper.readerFor(clazz).at(jsonPath).readValue(readResponse(response));
        } catch (IOException e) {
            throw new OperationFailedException(e);
        }
    }
    
    protected <V> V fetch(String path, String jsonPath, TypeReference<V> typeReference) throws OperationFailedException {
        HttpResponse response = execute(format("%s/%s", baseUrl, path));

        try {
            return objectMapper.readerFor(typeReference).at(jsonPath).readValue(readResponse(response));
        } catch (IOException e) {
            throw new OperationFailedException(e);
        }
    }
    
    protected <V> MappingIterator<V> fetchIterator(String path, Class<V> clazz) throws OperationFailedException {
        HttpResponse response = httpClientPool.execute(format("%s/%s", baseUrl, path));

        try {
            return objectMapper.readerFor(clazz).readValues(readResponse(response));
        } catch (IOException e) {
            throw new OperationFailedException(e);
        }
    }
    
    protected <V> MappingIterator<V> fetchIterator(String path, TypeReference<V> typeReference) throws OperationFailedException {
        HttpResponse response = execute(format("%s/%s", baseUrl, path));

        try {
            return objectMapper.readerFor(typeReference).readValues(readResponse(response));
        } catch (IOException e) {
            throw new OperationFailedException(e);
        }
    }
    
    protected <V> MappingIterator<V> fetchIterator(String path, String jsonPath, Class<V> clazz) throws OperationFailedException {
        HttpResponse response = execute(format("%s/%s", baseUrl, path));

        try {
            return objectMapper.readerFor(clazz).at(jsonPath).readValues(readResponse(response));
        } catch (IOException e) {
            throw new OperationFailedException(e);
        }
    }
    
    protected <V> MappingIterator<V> fetchIterator(String path, String jsonPath, TypeReference<V> typeReference) throws OperationFailedException {
        HttpResponse response = execute(format("%s/%s", baseUrl, path));

        try {
            return objectMapper.readerFor(typeReference).at(jsonPath).readValues(readResponse(response));
        } catch (IOException e) {
            throw new OperationFailedException(e);
        }
    }
    
    protected String readResponse(HttpResponse response) throws OperationFailedException {
        try {
            HttpEntity responseHttpEntity = response.getEntity();
            String responseString = IoUtilities.streamToString(responseHttpEntity.getContent());
            EntityUtils.consume(responseHttpEntity);
            return responseString;
        } catch (UnsupportedOperationException | IOException e) {
            throw new OperationFailedException(e);
        }
    }
    
    @Override
    public BlockData fetchBlockDataByHash(String blockHash) throws OperationFailedException {
        long start = Instant.now().getEpochSecond();
        BlockData blockData = toBlockData(fetch(getBlockByHashUrl(blockHash), this.httpResultBlockClass));
        blockData.setIndexingDuration(Instant.now().getEpochSecond() - start);
        blockData.setIndexed(Instant.now().toEpochMilli());
        return blockData;
    }
    
    @Override
    public NodeTransaction fetchTransactionByHash(String transactionHash, boolean calculate) throws OperationFailedException {
        return toNodeTransaction(fetch(getTransactionByHashUrl(transactionHash), this.httpResultTransasctionClass));
    }

    @Override
    public BlockData fetchBlockData(long blockNumber) throws OperationFailedException {
        long start = Instant.now().getEpochSecond();
        BlockData blockData = toBlockData(fetch(getBlockByHeightUrl(blockNumber), this.httpResultBlockClass));
        blockData.setIndexingDuration(Instant.now().getEpochSecond() - start);
        blockData.setIndexed(Instant.now().toEpochMilli());
        return blockData;
    }
}
