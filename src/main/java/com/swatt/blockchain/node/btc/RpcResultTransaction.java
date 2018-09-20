package com.swatt.blockchain.node.btc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static java.util.stream.Collectors.toList;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class RpcResultTransaction {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public String hash;
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
        hash = jsonNode.get("hash").asText();
        version = jsonNode.get("version").asLong();
        height = jsonNode.get("height").asLong();
        size = jsonNode.get("size").asLong();
        vsize = jsonNode.get("vsize").asLong();

        vin = jsonNode.findValues("vin").stream().map(n -> {
            try {
                return objectMapper.treeToValue(n, RpcResultVin.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).collect(toList());

        vout = jsonNode.findValues("vout").stream().map(n -> {
            try {
                return objectMapper.treeToValue(n, RpcResultVout.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).collect(toList());

        blockhash = jsonNode.get("blockhash").asText();
        time = jsonNode.get("time").asLong();
    }
}
