package com.swatt.blockchain.node.zec;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

import static com.swatt.util.general.CollectionsUtilities.isNullOrEmpty;
import static java.util.stream.Collectors.toList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RpcResultBlock extends com.swatt.blockchain.node.btc.RpcResultBlock {
    @JsonProperty("tx")
    private List<JsonNode> transactionsInternal;

    private List<String> transactionStrings;
    public List<RpcResultTransaction> transactions;

    public List<com.swatt.blockchain.node.btc.RpcResultTransaction> getTransactions() {
        if (!isNullOrEmpty(transactionsInternal)) {
            return transactionsInternal.stream().map(t -> t.isObject() ? new RpcResultTransaction(t) : null).collect(toList());
        }

        return null;
    }
}
