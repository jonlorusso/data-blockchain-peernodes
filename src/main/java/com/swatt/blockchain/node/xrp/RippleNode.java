package com.swatt.blockchain.node.xrp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.node.JsonRpcHttpClientNode;
import com.swatt.blockchain.node.NodeTransaction;
import com.swatt.util.general.OperationFailedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.HttpClientBuilder;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class RippleNode extends JsonRpcHttpClientNode<LedgerResult, Transaction> {

    private HttpClientBuilder httpClientBuilder;

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class LedgersResponse {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Ledger {
            public int ledger_index;
        }

        public Ledger ledger;
    }

    @Override
    public long fetchBlockCount() throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        try {
            synchronized (jsonRpcHttpClient) {
                Type returnType = new TypeReference<Map<String, Object>>() {}.getType();
                Map<String, Object> ledgerCurrent = (Map<String, Object>)jsonRpcHttpClient.invoke("ledger_current", new Object[] { null }, returnType);
                return Long.valueOf((Integer)ledgerCurrent.get("ledger_current_index"));
            }
        } catch (Throwable e) {
            throw new OperationFailedException("Error fetching block count", e);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }

    @Override
    protected NodeTransaction toNodeTransaction(Transaction transaction) throws OperationFailedException {
        NodeTransaction nodeTransaction = new NodeTransaction(transaction.getHash());
        nodeTransaction.setFee(transaction.getFee());

        if (StringUtils.equals(transaction.getTransactionType(), "Payment")) {
            if (transaction.getAmount() != null) {
                nodeTransaction.setAmount(transaction.getAmount());
            }
        }

        return nodeTransaction;
    }

    @Override
    protected String getBlockByHeightRpcMethodName() throws OperationFailedException {
        return "ledger";
    }


    @Override
    protected Object[] getBlockByHeightRpcMethodParameters(long blockNumber) throws OperationFailedException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ledger_index", blockNumber);
        parameters.put("transactions", true);
        parameters.put("expand", true);
        return new Object[]{parameters};
    }

    private void processTransaction(Transaction transaction, BlockData blockData) {
        try {
            NodeTransaction nodeTransaction = toNodeTransaction(transaction);

            int transactionCount = blockData.getTransactionCount();

            long transactionFee = transaction.getFee();
            blockData.setSmallestFeeBase(Math.min(blockData.getSmallestFee(), transactionFee));
            blockData.setLargestFeeBase(Math.max(blockData.getLargestFee(), transactionFee));
            blockData.setAvgFeeBase((blockData.getAvgFee() * transactionCount + transactionFee) / (transactionCount + 1));

            if (transaction.getAmount() != null) {
                Double amountValue = Double.valueOf(transaction.getAmount());
                if (amountValue > blockData.getLargestTxAmount()) {
                    blockData.setLargestTxHash(nodeTransaction.getHash());
                    blockData.setLargestTxAmountBase(amountValue);
                }
            }

            blockData.setTransactionCount(blockData.getTransactionCount() + 1);
        } catch (OperationFailedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected BlockData toBlockData(LedgerResult ledgerResult) throws OperationFailedException {
        BlockData blockData = new BlockData();

        blockData.setTimestamp(ledgerResult.getLedger().getCloseTime());
        blockData.setBlockchainCode(blockchainNodeInfo.getCode());

        blockData.setHeight(ledgerResult.getLedger().getLedgerIndex());
        blockData.setPrevHash(ledgerResult.getLedger().getParentHash());
        blockData.setRewardBase(0);
        blockData.setDifficultyBase(0);

        ledgerResult.getLedger().getTransactions().stream().forEach(t -> processTransaction(t, blockData));

        blockData.setSmallestFeeBase(blockData.getSmallestFee() == Long.MAX_VALUE ? 0 : blockData.getSmallestFee());
        blockData.setLargestFeeBase(blockData.getLargestFee() == Long.MIN_VALUE ? 0 : blockData.getLargestFee());

        return blockData;
    }




    @Override
    protected String getBlockByHashRpcMethodName() throws OperationFailedException {
        return "ledger";
    }

    @Override
    protected String getTransactionRpcMethodName() throws OperationFailedException {
        return "tx";
    }
}
