package com.swatt.blockchain.node.xrp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.node.HttpClientNode;
import com.swatt.blockchain.node.NodeTransaction;
import com.swatt.blockchain.node.xrp.Transaction.Tx;
import com.swatt.util.general.ConcurrencyUtilities;
import com.swatt.util.general.OperationFailedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static java.lang.String.format;

public class RippleNode extends HttpClientNode<LedgerResult, Transaction> {

    private static final long RATE_LIMIT_WAIT = 30000;
    private static final int RATE_LIMIT_STATUS_CODE = 429;

    private HttpClientBuilder httpClientBuilder;

    @Override
    public void init() {
        super.init();

        baseUrl = format("https://%s:%d", blockchainNodeInfo.getIp(), blockchainNodeInfo.getPort());

        httpClientBuilder = HttpClients.custom();

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() { return null; }
            public void checkClientTrusted(X509Certificate[] certs, String authType) { }
            public void checkServerTrusted(X509Certificate[] certs, String authType) { }
        }};

        // FIXME
        // Install the all-trusting trust manager
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            httpClientBuilder.setSSLContext(sslContext);
        } catch (Exception e) {
        }
    }

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
        HttpResponse httpResponse = execute(String.format("%s/%s", baseUrl, "v2/ledgers"));

        try {
            LedgersResponse ledgersResponse = objectMapper.readValue(readResponse(httpResponse), LedgersResponse.class);
            return ledgersResponse.ledger.ledger_index;
        } catch (IOException e) {
            throw new OperationFailedException("Unable to fetch block count.", e);
        }
    }

    @Override
    protected NodeTransaction toNodeTransaction(Transaction transaction) throws OperationFailedException {
        Tx tx = transaction.getTx();

        NodeTransaction nodeTransaction = new NodeTransaction(transaction.getHash());
        nodeTransaction.setFee(tx.getFee());
        nodeTransaction.setTimestamp(LocalDateTime.parse(transaction.getDate()).toEpochSecond(ZoneOffset.UTC));

        if (tx.getTransactionType().equals("Payment")) {
            if (tx.getAmount() != null) {
                Double amountValue = Double.valueOf(tx.getAmount().getValue());
                nodeTransaction.setAmount(amountValue);
            }
        }

        return nodeTransaction;
    }

    @Override
    protected String getTransactionByHashUrl(String transactionHash) {
        return format("v2/transactions/%s", transactionHash);
    }

    @Override
    protected String getBlockByHashUrl(String blockHash) {
        return format("v2/ledgers/%s?transactions=true&binary=false&expand=true", blockHash);
    }

    @Override
    protected String getBlockByHeightUrl(long height) {
        return format("v2/ledgers/%d?transactions=true&binary=false&expand=true", height);
    }

    private void processTransaction(Transaction transaction, BlockData blockData) {
        Tx tx = transaction.getTx();

        if (StringUtils.equals(tx.getTransactionType(), "Payment")) {
            String transactionHash = transaction.getHash();
            int transactionCount = blockData.getTransactionCount();

            long transactionFee = tx.getFee();
            blockData.setSmallestFeeBase(Math.min(blockData.getSmallestFee(), transactionFee));
            blockData.setLargestFeeBase(Math.max(blockData.getLargestFee(), transactionFee));
            blockData.setAvgFeeBase((blockData.getAvgFee() * transactionCount + transactionFee) / (transactionCount + 1));

            if (StringUtils.isNotEmpty(tx.getAmountString())) {
                Double amountValue = Double.valueOf(tx.getAmountString());
                if (amountValue > blockData.getLargestTxAmount()) {
                    blockData.setLargestTxHash(transactionHash);
                    blockData.setLargestTxAmountBase(amountValue);
                }
            }

            blockData.setTransactionCount(blockData.getTransactionCount() + 1);
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
    protected HttpResponse execute(String url) {
        CloseableHttpClient client = httpClientBuilder.build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");

        try {
            HttpResponse httpResponse = client.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == RATE_LIMIT_STATUS_CODE) {
                ConcurrencyUtilities.sleep(RATE_LIMIT_WAIT);
                return execute(url);
            }
            return httpResponse;
        } catch (IOException e) {
            throw new RuntimeException(e); // FIXME
        }
    }
}
