package com.swatt.blockchain.node.steem;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.swatt.util.general.OperationFailedException;

@JsonIgnoreProperties(ignoreUnknown=true)
public class RpcResultBlock {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    
    @JsonProperty("block_id")
    public String hash;
    
    public int size = 0;
    public String versionHex = EMPTY;

    @JsonProperty("transaction_merkle_root")
    public String merkleroot;

    @JsonProperty("transaction_ids")
    public List<String> transactionIds;
    
    @JsonProperty("transactions")
    public List<RpcResultTransaction> transactions;

    public String timestamp;
    public Long nonce = 0L;
    public String bits = StringUtils.EMPTY;
    public double difficulty = 0.0;
    
    @JsonProperty("previous")
    public String previousblockhash;

    public long getTime() throws OperationFailedException {
        try {
            return simpleDateFormat.parse(timestamp).getTime();
        } catch (ParseException e) {
            throw new OperationFailedException(e);
        }
    }
}
