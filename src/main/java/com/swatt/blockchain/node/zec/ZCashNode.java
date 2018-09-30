package com.swatt.blockchain.node.zec;


import com.swatt.blockchain.node.NodeTransaction;
import com.swatt.blockchain.node.btc.BitcoinNode;
import com.swatt.blockchain.node.btc.BitcoinTransaction;
import com.swatt.blockchain.node.btc.RpcResultVin;
import com.swatt.util.general.OperationFailedException;

public class ZCashNode extends BitcoinNode<RpcResultBlock, RpcResultTransaction> {

    @Override
    protected Object[] getBlockByHashRpcMethodParameters(String hash) throws OperationFailedException {
        return new Object[] { hash, 2 };
    }

    @Override
    protected Object[] getTransactionRpcMethodParameters(String hash) throws OperationFailedException {
        return new Object[] { hash, 1 };
    }

    @Override
    protected NodeTransaction toNodeTransaction(com.swatt.blockchain.node.btc.RpcResultTransaction btcRpcResultTransaction) throws OperationFailedException {
        RpcResultTransaction rpcResultTransaction = (RpcResultTransaction)btcRpcResultTransaction;

        BitcoinTransaction bitcoinTransaction = new BitcoinTransaction(rpcResultTransaction);
        bitcoinTransaction.setHash(rpcResultTransaction.txid);

        double amount = rpcResultTransaction.vout.stream().mapToDouble(v -> v.value).sum();
        double inValue = 0.0;

        for (int i = 0; i < rpcResultTransaction.vin.size(); i++) {
            RpcResultVin rpcResultVin = rpcResultTransaction.vin.get(i);

            if (rpcResultVin.txid != null) {
                RpcResultTransaction inputRpcResultTransaction = (RpcResultTransaction) fetchTransaction(rpcResultVin.txid);
                inValue += inputRpcResultTransaction.vout.get(rpcResultVin.vout).value;
            }
        }

        double publicInput = 0.0;
        double publicOutput = 0.0;

        if (rpcResultTransaction.vJoinSplits != null) {
            publicInput = rpcResultTransaction.vJoinSplits.stream().mapToDouble(v -> v.vPubOld).sum();
            publicOutput = rpcResultTransaction.vJoinSplits.stream().mapToDouble(v -> v.vPubNew).sum();
        }

        double fee;
        if (amount == 0.0) {
            fee = inValue - publicInput;
        } else if (inValue == 0.0 && publicOutput > 0.0) {
            fee = publicOutput - amount;
        } else {
            fee = inValue - amount;
        }

        if (inValue + publicInput + publicOutput > 0.0) {
            bitcoinTransaction.setFee(fee);
        } else {
            bitcoinTransaction.setCoinbase(true);
        }

        bitcoinTransaction.setAmount(amount);
        bitcoinTransaction.setBlockHash(rpcResultTransaction.blockhash);

        if (rpcResultTransaction.time != null)
            bitcoinTransaction.setTimestamp(rpcResultTransaction.time);

        return bitcoinTransaction;
    }
}
