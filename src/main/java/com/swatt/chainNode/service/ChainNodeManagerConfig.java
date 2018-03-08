package com.swatt.chainNode.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import com.swatt.util.Attributable;
import com.swatt.util.CollectionsUtilities;

public class ChainNodeManagerConfig extends Attributable {
	private ArrayList<ChainNodeConfig> chainNodeConfigs = new ArrayList<ChainNodeConfig>();
	
	public ChainNodeManagerConfig(Properties properties) {
		super(properties);
	}
	
	public void addChainNodeConfig(ChainNodeConfig chainNodeConfig) { 
		chainNodeConfigs.add(chainNodeConfig);
	}
	
	public void addChainNodeConfig(String name, String chainApiType, Properties properties) {
		if (!isAttributesLocked()) {
			ChainNodeConfig chainNodeConfig = new ChainNodeConfig(name, chainApiType, properties);
			addChainNodeConfig(chainNodeConfig);
		} else
			throw new RuntimeException("ChainNodeManagerConfig is Locked (trying to add with Properties list): " + this);
			
	}
	
	public ChainNodeConfig getChainNodeConfig(String name) {
		for (ChainNodeConfig chainNodeConfig : chainNodeConfigs) {
			if (name.equals(chainNodeConfig.getName()))
				return chainNodeConfig;
		}
		return null;
	}
	
	public String[] getChainNodeNames() {
		String result[] = new String[chainNodeConfigs.size()];
		
		for (int i=0; i < result.length; i++) {
			ChainNodeConfig chainNodeConfig = chainNodeConfigs.get(i);
			result[i] = chainNodeConfig.getName();
		}
		
		return result;
	}
	
	public ArrayList<ChainNodeConfig> getChainNodeConfigs() {
		return chainNodeConfigs;
	}
	
	public static ChainNodeManagerConfig getDefaultChainNodeManager() throws IOException {
    	String propertiesFileName = "config.properties";
    	Properties properties = CollectionsUtilities.loadProperties(propertiesFileName);
    	
    	ChainNodeManagerConfig chainNodeManagerConfig = new ChainNodeManagerConfig(properties);		// TODO: This is not going to be the way configuration is stored
    	chainNodeManagerConfig.addChainNodeConfig("BTC", "BitCoin", properties);					// TODO: Certainly the node specific properties won't be the global properties
    			
    	chainNodeManagerConfig.lockAttributes();
    	return chainNodeManagerConfig;
	}
}
