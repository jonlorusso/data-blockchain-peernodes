package com.swatt.blockchain.node.xrp;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LedgerResult {

    private Ledger ledger;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Ledger {
        private boolean accepted;

        @JsonProperty("transactions")
        private ArrayList<Transaction> transactions;

        private boolean closed;
        private String hash;

        @JsonProperty("account_hash")
        private String accountHash;

        @JsonProperty("close_flags")
        private int closeFlags;

        @JsonProperty("close_time")
        private int closeTime;

        @JsonProperty("close_time_human")
        private String closeTimeHuman;

        @JsonProperty("ledger_hash")
        private String ledgerHash;

        @JsonProperty("ledger_index")
        private int ledgerIndex;

        @JsonProperty("total_coins")
        private String totalCoins;

        @JsonProperty("tx_count")
        private int txCount;

        @JsonProperty("close_time_resolution")
        private int closeTimeResolution;

        @JsonProperty("parent_close_time")
        private int parentCloseTime;

        @JsonProperty("parent_hash")
        private String parentHash;

        @JsonProperty("seq_num")
        private int seqNum;

        @JsonProperty("transaction_hash")
        private String transactionHash;

        public boolean isAccepted() {
            return accepted;
        }

        public void setAccepted(boolean accepted) {
            this.accepted = accepted;
        }

        public List<Transaction> getTransactions() {
            return transactions;
        }

        public void setTransactions(ArrayList<Transaction> transactions) {
            this.transactions = transactions;
        }

        public boolean getClosed() {
            return closed;
        }

        public void setClosed(boolean closed) {
            this.closed = closed;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public String getAccountHash() {
            return accountHash;
        }

        public void setAccountHash(String accountHash) {
            this.accountHash = accountHash;
        }

        public int getCloseFlags() {
            return closeFlags;
        }

        public void setCloseFlags(int closeFlags) {
            this.closeFlags = closeFlags;
        }

        public int getCloseTime() {
            return closeTime;
        }

        public void setCloseTime(int closeTime) {
            this.closeTime = closeTime;
        }

        public String getCloseTimeHuman() {
            return closeTimeHuman;
        }

        public void setCloseTimeHuman(String closeTimeHuman) {
            this.closeTimeHuman = closeTimeHuman;
        }

        public String getLedgerHash() {
            return ledgerHash;
        }

        public void setLedgerHash(String ledgerHash) {
            this.ledgerHash = ledgerHash;
        }

        public int getLedgerIndex() {
            return ledgerIndex;
        }

        public void setLedgerIndex(int ledgerIndex) {
            this.ledgerIndex = ledgerIndex;
        }

        public String getTotalCoins() {
            return totalCoins;
        }

        public void setTotalCoins(String totalCoins) {
            this.totalCoins = totalCoins;
        }

        public int getTxCount() {
            return txCount;
        }

        public void setTxCount(int txCount) {
            this.txCount = txCount;
        }

        public int getCloseTimeResolution() {
            return closeTimeResolution;
        }

        public void setCloseTimeResolution(int closeTimeResolution) {
            this.closeTimeResolution = closeTimeResolution;
        }

        public int getParentCloseTime() {
            return parentCloseTime;
        }

        public void setParentCloseTime(int parentCloseTime) {
            this.parentCloseTime = parentCloseTime;
        }

        public String getParentHash() {
            return parentHash;
        }

        public void setParentHash(String parentHash) {
            this.parentHash = parentHash;
        }

        public int getSeqNum() {
            return seqNum;
        }

        public void setSeqNum(int seqNum) {
            this.seqNum = seqNum;
        }

        public String getTransactionHash() {
            return transactionHash;
        }

        public void setTransactionHash(String transactionHash) {
            this.transactionHash = transactionHash;
        }
    }

    public Ledger getLedger() {
        return ledger;
    }

    public void setLedger(Ledger ledger) {
        this.ledger = ledger;
    }
}
