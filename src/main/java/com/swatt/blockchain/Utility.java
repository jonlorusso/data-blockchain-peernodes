package com.swatt.blockchain;

import java.nio.ByteBuffer;

public class Utility {
   private int ByteArrayToInt(Byte[] Bytes){
      byte[] bytes = new byte[Bytes.length];
      int i = 0;
      for(Byte b : Bytes){
         bytes[i] = b.byteValue();
         i++;
      }
      int Integer = ByteBuffer.wrap(bytes).getInt();
      return Integer;
   }
}