package com.swatt.blockchain.node.xrp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {

	@JsonProperty("Amount")
	private JsonNode amount;

	@JsonProperty("Fee")
	private Long fee;

	private String hash;

	@JsonProperty("TransactionType")
	private String transactionType;

	public Long getAmount() {
	    if (amount != null) {
	    	if (amount.isTextual()) {
	    		return amount.asLong();
			}
		}
	    return null;
	}

	public long getFee() {
		return fee;
	}

	public String getHash() {
		return hash;
	}

	public String getTransactionType() {
		return transactionType;
	}
}
