package com.swatt.util;

import java.util.LinkedList;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

public class JsonRpcHttpClientPool {			// FIXME: Not Industrial Strength.  Does not deal with un-returned connections
	private String url;
	private String user;
	private String password;
	private int maxSize;
	private LinkedList<JsonRpcHttpClient> freeJsonRpcHttpClients = new LinkedList<JsonRpcHttpClient>();
	private LinkedList<JsonRpcHttpClient> busyJsonRpcHttpClients = new LinkedList<JsonRpcHttpClient>();
	
	public JsonRpcHttpClientPool(String url, String user, String password, int maxSize) {
		this.url = url;
		this.user = user;
		this.password = password;
		this.maxSize = maxSize;
	}
	
	public JsonRpcHttpClient getJsonRpcHttpClient()  {
		synchronized(freeJsonRpcHttpClients) {
			JsonRpcHttpClient jsonRpcHttpClient = null;
			
			if (freeJsonRpcHttpClients.size() > 0) {
				jsonRpcHttpClient = freeJsonRpcHttpClients.removeFirst();
			} else if ((freeJsonRpcHttpClients.size() + busyJsonRpcHttpClients.size()) < maxSize) {
				jsonRpcHttpClient = JsonUtilities.createJsonRpcHttpClient(url, user, password);
			} else {
				while (freeJsonRpcHttpClients.size() == 0) {
					ConcurrencyUtilities.waitOn(freeJsonRpcHttpClients);
				}
					
				jsonRpcHttpClient = freeJsonRpcHttpClients.removeFirst();
			}
			
			busyJsonRpcHttpClients.add(jsonRpcHttpClient);
			return jsonRpcHttpClient;
		}
	}
	
	public void returnConnection(JsonRpcHttpClient jsonRpcHttpClient) {
		synchronized(freeJsonRpcHttpClients) {
			busyJsonRpcHttpClients.remove(jsonRpcHttpClient);
			freeJsonRpcHttpClients.add(jsonRpcHttpClient);
			ConcurrencyUtilities.notifyAll(freeJsonRpcHttpClients);
		}
	}

}