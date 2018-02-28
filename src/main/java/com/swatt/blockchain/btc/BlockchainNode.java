package com.swatt.blockchain.btc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.swatt.blockchain.BlockchainNodeData;
import com.swatt.blockchain.BlockchainNodeInfo;
import com.swatt.blockchain.BlockchainTransaction;
import com.thetransactioncompany.jsonrpc2.*;
import com.thetransactioncompany.jsonrpc2.client.*;

public class BlockchainNode extends com.swatt.blockchain.BlockchainNode {
	private URL uri;
	
	public BlockchainNode(){
		try {
		    Properties prop = new Properties();
		    InputStream input = null;

	        input = new FileInputStream("config.properties");

	        prop.load(input);

	        uri = new URL(prop.getProperty("url"));
	        
			final String rpcuser = prop.getProperty("rpcuser");
			final String rpcpassword = prop.getProperty("rpcpassword");
 
			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication (rpcuser, rpcpassword.toCharArray());
				}
			});
		} catch (FileNotFoundException e) {
			// TODO error to developer if no props file
			e.printStackTrace();
		} catch (IOException e) {
			// TODO error to developer if props file doesn't load
			e.printStackTrace();
		}	
	}
	
    public BlockchainNodeInfo getInfo() {
		String ticker = "BTC";
		String name = "Bitcoin";
    		String description = "Full blockchain node for Bitcoin";
    		BlockchainNodeInfo info = new BlockchainNodeInfo(ticker, name, description);
    		
    		return info;
    }

	public BlockchainTransaction findTransactionByHash(String hash) {
		JSONObject blockchainTransaction;
		String blockHash = null;
		String[] inputs = null;
		String[] outputs = null;
		double[] outputValues = null;
		
		try {
			blockchainTransaction = makeRequest(BTCMethods.GET_RAW_TRANSACTION, hash);
			
			JSONArray vin = (JSONArray) blockchainTransaction.get("vin");
			JSONArray vout = (JSONArray) blockchainTransaction.get("vout");
			
			inputs = new String[vin.size()];
			outputs = new String[vout.size()];
			outputValues = new double[2];
			
			@SuppressWarnings("unchecked")
			Iterator<String> it = vin.iterator();
			while (it.hasNext()) {
				System.out.println("leg = " + it.next());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BlockchainTransaction rtn = new BlockchainTransaction(hash, blockHash, inputs, outputValues);
		return rtn;
   }

	private JSONObject makeRequest(String method, String input) throws IOException {			
		// Create new JSON-RPC 2.0 client session
		JSONRPC2Session rpcSession = new JSONRPC2Session(uri);
		rpcSession.getOptions().setRequestContentType("text/plain");
		rpcSession.getOptions().trustAllCerts(true);
		rpcSession.getOptions().ignoreVersion(true);
		rpcSession.getOptions().parseNonStdAttributes(true);
		RPCInspector inspector = new RPCInspector();
		
		rpcSession.setRawResponseInspector(inspector);
		
		List<Object> params = Arrays.asList(input, true);

		String id = "req-001";

		// Create a new JSON-RPC 2.0 request
		JSONRPC2Request reqOut = new JSONRPC2Request(method, params, id);

		// Serialize the request to a JSON-encoded string
		String jsonString = reqOut.toString();
		System.out.println(jsonString);

		JSONRPC2Response response = null;
		String contents = null;

        try {
            response = rpcSession.send(reqOut);
        } catch (JSONRPC2SessionException e) {
        		contents = inspector.contents;
        		//throw new IOException("JSONRPC request failed");
        }

		JSONObject json = null;
        try {
        		JSONParser parser = new JSONParser();
        		json = (JSONObject) parser.parse(contents);
        } catch (Exception e) {
        		//
        }
        
		return json;
	}
	
	public BlockchainTransaction findTransactionByAddress(String address) {
		return null;
	}

	public BlockchainNodeData getDataForInterval(long fromTime, long toTime) {
		// TODO Auto-generated method stub
		return null;
	}
}

class LocalParser extends com.thetransactioncompany.jsonrpc2.JSONRPC2Parser{
	
}