package com.swatt.blockchain.node.btc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RpcResultTransaction {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public String hash;
    public String txid;
    public Long version;
    public Long height;
    public Long size;
    public Long vsize;
    public List<RpcResultVin> vin;
    public List<RpcResultVout> vout;
    public String blockhash;
    public Long time;

    public RpcResultTransaction() {
        super();
    }

    public RpcResultTransaction(JsonNode jsonNode) {
        if (jsonNode.get("hash") != null)
            hash = jsonNode.get("hash").asText();

        if (jsonNode.get("txid") != null)
            hash = jsonNode.get("txid").asText();

        if (jsonNode.get("version") != null)
            version = jsonNode.get("version").asLong();

        if (jsonNode.get("height") != null)
            height = jsonNode.get("height").asLong();

        if (jsonNode.get("size") != null)
            size = jsonNode.get("size").asLong();

        if (jsonNode.get("vsize") != null)
            vsize = jsonNode.get("vsize").asLong();

        if (jsonNode.get("vin") != null) {
            vin = stream(spliteratorUnknownSize(jsonNode.get("vin").iterator(), ORDERED), false).map(n -> {
                try {
                    return objectMapper.treeToValue(n, RpcResultVin.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }).collect(toList());
        }

        if (jsonNode.get("vout") != null) {
            vout = stream(spliteratorUnknownSize(jsonNode.get("vout").iterator(), ORDERED), false).map(n -> {
                try {
                    return objectMapper.treeToValue(n, RpcResultVout.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }).collect(toList());
        }

        if (jsonNode.get("blockhash") != null)
            blockhash = jsonNode.get("blockhash").asText();

        if (jsonNode.get("time") != null)
            time = jsonNode.get("time").asLong();
    }
}
