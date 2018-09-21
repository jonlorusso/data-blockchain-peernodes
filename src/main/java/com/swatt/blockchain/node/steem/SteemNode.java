package com.swatt.blockchain.node.steem;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.swatt.blockchain.entity.BlockData;
import com.swatt.blockchain.entity.BlockchainNodeInfo;
import com.swatt.blockchain.node.JsonRpcHttpClientNode;
import com.swatt.blockchain.node.Node;
import com.swatt.blockchain.node.NodeListener;
import com.swatt.blockchain.node.NodeTransaction;
import com.swatt.util.general.OperationFailedException;

public class SteemNode extends JsonRpcHttpClientNode<RpcResultBlock, RpcResultTransaction> {

    @Override
    protected String getBlockByHashRpcMethodName() throws OperationFailedException {
        throw new OperationFailedException("No block by hash call on STEEM");
    }

    @Override
    protected String getTransactionRpcMethodName() throws OperationFailedException {
        return "get_transaction";
    }

    @Override
    protected NodeTransaction toNodeTransaction(RpcResultTransaction rpcResultTransaction) {
        NodeTransaction nodeTransaction = new NodeTransaction("FIXME");
        nodeTransaction.setFee(0);
        nodeTransaction.setAmount(rpcResultTransaction.getTransferAmount());
        return nodeTransaction;
    }

    @Override
    protected String getBlockByHeightRpcMethodName() throws OperationFailedException {
        return "get_block";
    }

    @Override
    protected BlockData toBlockData(RpcResultBlock rpcResultBlock) throws OperationFailedException {
        BlockData blockData = new BlockData();
        blockData.setScalingPowers(super.getDifficultyScaling(), super.getRewardScaling(), super.getFeeScaling(), super.getAmountScaling());
        blockData.setHash(rpcResultBlock.hash);
        blockData.setSize(rpcResultBlock.size);
        blockData.setVersionHex(rpcResultBlock.versionHex);
        blockData.setMerkleRoot(rpcResultBlock.merkleroot);
        blockData.setTimestamp(rpcResultBlock.getTime());
        blockData.setNonce(rpcResultBlock.nonce.toString()); // FIXME
        blockData.setBits(rpcResultBlock.bits);
        blockData.setDifficultyBase(rpcResultBlock.difficulty);
        blockData.setPrevHash(rpcResultBlock.previousblockhash);
        // blockData.setNextHash(rpcResultBlock.nextblockhash); // FIXME need to do an update?
        blockData.setBlockchainCode(getBlockchainCode());

        double largestTxAmount = 0.0;
        String largestTxHash = null;

        int transactionCount = 0;
        for (int i = 0; i < rpcResultBlock.transactionIds.size(); i++) {
            String transactionId = rpcResultBlock.transactionIds.get(i);

            RpcResultTransaction rpcResultTransaction = rpcResultBlock.transactions.get(i);
            double transactionAmount = rpcResultTransaction.getTransferAmount();

            transactionCount++;

            if (transactionAmount > largestTxAmount) {
                largestTxAmount = transactionAmount;
                largestTxHash = transactionId;
            }
        }

        blockData.setTransactionCount(transactionCount);
        blockData.setLargestTxAmountBase(largestTxAmount);
        blockData.setLargestTxHash(largestTxHash);

        return blockData;
    }

    @Override
    public long fetchBlockCount() throws OperationFailedException {
        JsonRpcHttpClient jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();

        try {
            return jsonRpcHttpClient.invoke("get_dynamic_global_properties", new Object[] {}, RpcResultDynamicGlobalProperties.class).blockCount;
        } catch (Throwable e) {
            throw new OperationFailedException("Error fetching block count", e);
        } finally {
            jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient);
        }
    }
}
