package com.swatt.chainNode.eth;

/*
 * import java.time.Instant; import java.util.logging.Level; import
 * java.util.logging.Logger;
 * 
 * import com.googlecode.jsonrpc4j.JsonRpcHttpClient; import
 * com.swatt.chainNode.ChainNode; import com.swatt.chainNode.Transaction; import
 * com.swatt.chainNode.btc.RPCBlock; import com.swatt.chainNode.dao.BlockData;
 * import com.swatt.util.JsonRpcHttpClientPool; import
 * com.swatt.util.OperationFailedException;
 * 
 * public class EthereumChainNode extends ChainNode { private static final
 * Logger LOGGER = Logger.getLogger(EthereumChainNode.class.getName());
 * 
 * private JsonRpcHttpClientPool jsonRpcHttpClientPool;
 * 
 * public EthereumChainNode() { }
 * 
 * @Override public void init() { String url = chainNodeConfig.getURL(); int
 * maxSize = 10; // TODO: Should get from chainNodeConfig
 * 
 * jsonRpcHttpClientPool = new JsonRpcHttpClientPool(url, null, null, maxSize);
 * }
 * 
 * @Override public BlockData fetchBlockDataByHash(String blockHash) throws
 * OperationFailedException { JsonRpcHttpClient jsonRpcHttpClient =
 * jsonRpcHttpClientPool.getJsonRpcHttpClient();
 * 
 * try { if (blockHash != null) { return fetchBlockByHash(jsonRpcHttpClient,
 * blockHash); } } finally {
 * jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient); } }
 * 
 * @Override public Transaction fetchTransactionByHash(String transactionHash,
 * boolean calculateFee) throws OperationFailedException { JsonRpcHttpClient
 * jsonRpcHttpClient = jsonRpcHttpClientPool.getJsonRpcHttpClient();
 * 
 * try { EthereumTransaction transaction = new
 * EthereumTransaction(jsonRpcHttpClient, transactionHash, calculateFee); return
 * transaction; } catch (OperationFailedException e) { throw e; } catch
 * (Throwable t) { OperationFailedException e = new
 * OperationFailedException("Error fetching latest Block: ", t);
 * LOGGER.log(Level.SEVERE, e.toString(), e); throw e; } finally {
 * jsonRpcHttpClientPool.returnConnection(jsonRpcHttpClient); } }
 * 
 * private BlockData fetchBlockByHash(JsonRpcHttpClient jsonrpcClient, String
 * blockHash) throws OperationFailedException {
 * 
 * try { long start = Instant.now().getEpochSecond();
 * 
 * Object parameters[] = new Object[] { blockHash }; RPCBlock rpcBlock =
 * jsonrpcClient.invoke(ETHMethods.GET_BLOCK_BYHASH, parameters,
 * RPCBlock.class);
 * 
 * BlockData blockData = new BlockData(); blockData.setHash(rpcBlock.hash);
 * blockData.setSize(rpcBlock.size); blockData.setHeight(rpcBlock.height);
 * blockData.setVersionHex(rpcBlock.versionHex);
 * blockData.setMerkleRoot(rpcBlock.merkleroot);
 * blockData.setTimestamp(rpcBlock.time); blockData.setNonce(rpcBlock.nonce);
 * blockData.setBits(rpcBlock.bits);
 * blockData.setDifficulty(rpcBlock.difficulty);
 * blockData.setPrevHash(rpcBlock.previousblockhash);
 * blockData.setNextHash(rpcBlock.nextblockhash);
 * 
 * blockData.setBlockchainCode(blockchainCode);
 * 
 * System.out.println("CALCULATING BLOCK: " + rpcBlock.hash);
 * 
 * calculate(jsonrpcClient, blockData, rpcBlock);
 * 
 * long indexingDuration = Instant.now().getEpochSecond() - start; long now =
 * Instant.now().toEpochMilli();
 * 
 * blockData.setIndexed(now); blockData.setIndexingDuration(indexingDuration);
 * 
 * return blockData;
 * 
 * } catch (Throwable t) { if (t instanceof OperationFailedException) throw
 * (OperationFailedException) t; else { OperationFailedException e = new
 * OperationFailedException("Error fetching latest Block: ", t);
 * LOGGER.log(Level.SEVERE, e.toString(), e); throw e; } }
 * 
 * }
 * 
 * private void calculate(JsonRpcHttpClient jsonrpcClient, BlockData blockData,
 * RPCBlock rpcBlock) throws OperationFailedException {
 * 
 * long totalSize = 0; // Will this ever exceed a 32 bit int? double totalFee =
 * 0.0; double totalFeeRate = 0.0;
 * 
 * double largestFee = 0.0; double smallestFee = Double.MAX_VALUE; double
 * largestTxAmount = 0.0; String largestTxHash = null;
 * 
 * int transactionCount = 0; // block.tx.size();
 * 
 * for (String transactionHash : rpcBlock.tx) { EthereumTransaction transaction
 * = new EthereumTransaction(jsonrpcClient, transactionHash, true);
 * 
 * if (!transaction.isNewlyMinted()) { double transactionFee =
 * transaction.getFee(); double transactionAmount = transaction.getAmount();
 * 
 * smallestFee = Math.min(smallestFee, transactionFee); largestFee =
 * Math.max(largestFee, transactionFee);
 * 
 * transactionCount++; totalFee += transactionFee; totalFeeRate +=
 * transaction.getFeeRate(); totalSize += transaction.getSize();
 * 
 * if (transactionAmount > largestTxAmount) { largestTxAmount =
 * transactionAmount; largestTxHash = transactionHash; }
 * 
 * if (transactionFee <= 0.0) { System.out.println("Initial transaction: " +
 * transactionHash); } } }
 * 
 * double averageFee = totalFee / transactionCount; double averageFeeRate =
 * totalFeeRate / transactionCount;
 * 
 * blockData.setTransactionCount(transactionCount);
 * blockData.setAvgFee(averageFee); blockData.setAvgFeeRate(averageFeeRate);
 * 
 * blockData.setLargestFee(largestFee);
 * blockData.setLargestTxAmount(largestTxAmount);
 * blockData.setLargestTxHash(largestTxHash); }
 * 
 * }
 * 
 */