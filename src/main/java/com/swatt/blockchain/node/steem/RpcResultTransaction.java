package com.swatt.blockchain.node.steem;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class RpcResultTransaction {

    private List<List<Object>> operations;

    public List<List<Object>> getOperations() {
        return operations;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, Object> getOperation(String operationType) {
        for (List<Object> operation : operations) {
            if (operation.get(0).equals(operationType)) {
                return (Map<String, Object>)operation.get(1);
            }
        }
        
        return null;
    }
    
    public long getTransferAmount() {
        Map<String, Object> transfer = getOperation("transfer");
        if (transfer != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> amount = (Map<String, Object>)transfer.get("amount");
            long transactionAmount = (long)amount.get("amount");
            System.out.println("transactionAmount: " + transactionAmount);
            return transactionAmount;
        }
        return 0;
    }
}
