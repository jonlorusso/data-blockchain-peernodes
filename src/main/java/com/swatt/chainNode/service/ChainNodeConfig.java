package com.swatt.chainNode.service;

import java.util.Properties;

import com.swatt.util.Attributable;

public class ChainNodeConfig extends Attributable {
	private String name;			// Name of this chain
	private String chainApiType;	// Chain Node API type (Bitcoin, Ethereum, etc)
	
	public ChainNodeConfig(String name, String chainApiType, Properties properties) {
		this.name = name;
		this.chainApiType = chainApiType;
		
		this.setFromProperties(properties);
		this.lockAttributes();
	}

	public final String getName() { return name; }
	public final String getChainApiType() { return chainApiType; }
	
	public final String getClassName() {			// FIXME: This is a temporary implementation until we finalize the ChainNode configuration formats
		String key = name + "_ClassName";
		return getAttribute(key, null);
	}
	
}
