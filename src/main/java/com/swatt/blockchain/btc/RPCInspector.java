package com.swatt.blockchain.btc;

import com.thetransactioncompany.jsonrpc2.client.RawResponse;
import com.thetransactioncompany.jsonrpc2.client.RawResponseInspector;

public class RPCInspector implements RawResponseInspector {
	public String contents = null;
	
	public void inspect(RawResponse response) {
		contents = response.getContent();
		
		// print the HTTP status code
		//System.out.println("HTTP status: " + response.getStatusCode());
		
		// print the contents
		//System.out.println("Content: " + response.getContent());
	
		// print the value of the "Date" HTTP header
		//System.out.println("Date: " + response.getHeaderField("Date"));
	}
}
