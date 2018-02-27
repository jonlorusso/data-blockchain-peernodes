package com.swatt.blockchain;

/** Information about a specific Blockchain Transaction
*/
public class BlockchainTransaction {
   String hash;
   String blockHash;
   String[] inputs;
   double[] outputValues;
   Long fee;

   public BlockchainTransaction(String hash, String blockHash, String[] inputs, double[] outputs){
      this.hash = hash;
      this.blockHash = blockHash;
      this.inputs = inputs;
      this.outputValues = outputs;
   }

   //Fee for transaction, calculated or ...
   public Long getFee() { return fee; }
}