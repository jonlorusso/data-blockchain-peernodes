package com.swatt.blockchain.node.xrp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {

	private String hash;

	@JsonProperty("ledger_index")
	private int ledgerIndex;

	private String date;

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Amount {
		private String currency;
		private String value;

		public Amount(String currency, String value) {
			this.currency = currency;
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public String getCurrency() {
			return currency;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Tx {

		@JsonProperty("TransactionType")
		private String transactionType;

		@JsonProperty("Fee")
		private long fee;

		@JsonProperty("Amount")
		private JsonNode amountInternal;

		private String amountStr;
		private Amount amount;

		public String getTransactionType() {
			return transactionType;
		}

		public void setTransactionType(String transactionType) {
			this.transactionType = transactionType;
		}

		public long getFee() {
			return fee;
		}

		public void setFee(long fee) {
			this.fee = fee;
		}

		public String getAmountString() {
			if (amountStr == null && amountInternal != null) {
				if (amountInternal.isTextual()) {
					amountStr = amountInternal.asText();
				}
			}
			return amountStr;
		}

		public Amount getAmount() {
			if (amount == null && amountInternal != null) {
				if (amountInternal.isObject()) {
					amount = new Amount(amountInternal.get("currency").asText(), amountInternal.get("value").asText());
				}
			}
			return amount;

		}

		public void setAmount(JsonNode amount) {
			this.amountInternal = amount;
		}
	}

	private Tx tx;

	public Tx getTx() {
		return tx;
	}

	public void setTx(Tx tx) {
		this.tx = tx;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public int getLedgerIndex() {
		return ledgerIndex;
	}

	public void setLedgerIndex(int ledgerIndex) {
		this.ledgerIndex = ledgerIndex;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}

/**
{"hash":"30239D5F49FDD067A055995BC8DF7BA25B27188AA2C3BE985405B4191268DCE2"
"ledger_index":1132570
"date":"2013-06-25T05:35:00+00:00"

"tx":{"TransactionType":"Payment"
"Flags":0
"Sequence":53534
"DestinationTag":2
"Amount":"8000000"
"Fee":"10"
"SigningPubKey":"0350B9A9B0503084405ECA1672B25446D198B7A754F63C524723E543E4810A5585"
"TxnSignature":"3046022100C7E65303B4EA5F50EAFBEF8C57922BC0670FC80D2BC6614A2B1B4A4970DFD7EE022100DC85B3A1774B936FBB35D7FB12678EA85A4B3500679E3B0DBB94915EA15F7856"
"Account":"rUSdkSimxiENqKyrExauLatszy7mnG83fH"
"Destination":"rpyUV8W6XRvss6SBkAS8PyzGwMsSDxgNXW"}

"meta":{"TransactionIndex":0
"AffectedNodes":[{"ModifiedNode":{"LedgerEntryType":"AccountRoot"
"PreviousTxnLgrSeq":1132566
"PreviousTxnID":"568AAE44111099E36C1838363F093C34A395E58C3B902B2F9736884919EA0DC3"
"LedgerIndex":"A13F7BB67269513A3562E292078527CF70F0F9E9D181196A3AC3F6140B790C4D"
"PreviousFields":{"Sequence":53534
"Balance":"42769623356"}
"FinalFields":{"Flags":0
"Sequence":53535
"OwnerCount":0
"Balance":"42761623346"
"Account":"rUSdkSimxiENqKyrExauLatszy7mnG83fH"}}}
{"ModifiedNode":{"LedgerEntryType":"AccountRoot"
"PreviousTxnLgrSeq":1132566
"PreviousTxnID":"568AAE44111099E36C1838363F093C34A395E58C3B902B2F9736884919EA0DC3"
"LedgerIndex":"C754412E25ED3F4257524CED149B993DC1683BDEBE6E0514604008A9B259813B"
"PreviousFields":{"Balance":"1511936598630"}
"FinalFields":{"Flags":0
"Sequence":74286
"OwnerCount":0
"Balance":"1511944598630"
"Account":"rpyUV8W6XRvss6SBkAS8PyzGwMsSDxgNXW"}}}]
"TransactionResult":"tesSUCCESS"
"delivered_amount":"8000000"}}
 **/