package com.swatt.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

// Note: Annotated version of a TypedPropertyList (GS)

public class Attributable {
	private HashMap<String, String> attributes = new HashMap<String, String>();
	private boolean locked = false;
	
	public Attributable() { }
	
	public Attributable(String propertiesFileName) throws IOException {
    	setFromPropertiesFromFile(propertiesFileName);
	}
	
	public Attributable(Properties properties) {
		setFromProperties(properties);
	}
	
	public void setFromPropertiesFromFile(String propertiesFileName) throws IOException {
    	Properties properties = new Properties();
    	FileInputStream in = new FileInputStream(propertiesFileName);
    	properties.load(in);
    	
    	setFromProperties(properties);
    	
	}
	
	public void setFromProperties(Properties properties) {
		if (!locked) {
			
			for (Enumeration<?> e=properties.propertyNames() ; e.hasMoreElements(); ) {
				String name = (String) e.nextElement();
				
				String value = properties.getProperty(name);
				attributes.put(name, value);
			}
		} else
			throw new RuntimeException("Attribute List is Locked (trying to set with Properties list): " + this);
	}
	
	public void lockAttributes() {
		this.locked = true;
	}
	
	public boolean isAttributesLocked() {
		return locked;
	}
	
	public void setAttribute(String name, String value) {
		if (!locked)
			attributes.put(name, value); 
		else 
			throw new RuntimeException("Attribute List is Locked (trying to set '" + name + "'): " + this);
	}

	public String getAttribute(String name, String defaultValue) {
		String value = attributes.get(name);
		return (value != null) ? value : defaultValue;
	}
	
	public int getIntAttribute(String name, int defaultValue) {
		String value = attributes.get(name);
		
		if (value == null)
			return defaultValue;
		else
			return Integer.parseInt(value);
	}
	
	public void setIntAttribute(String name, int value) { 
		if (!locked)
			attributes.put(name, Integer.toString(value));
		else 
			throw new RuntimeException("Attribute List is Locked (trying to set '" + name + "'): " + this);
	}
	
	public long getLongAttribute(String name, long defaultValue) {
		String value = attributes.get(name);
		
		if (value == null)
			return defaultValue;
		else
			return Long.parseLong(value);
	}
	
	public void setLongAttribute(String name, long value) { 
		if (!locked)
			attributes.put(name, Long.toString(value));
		else 
			throw new RuntimeException("Attribute List is Locked (trying to set '" + name + "'): " + this);
	}

	public double getDoubleAttribute(String name, double defaultValue) {
		String value = attributes.get(name);
		
		if (value == null)
			return defaultValue;
		else
			return Double.parseDouble(value);
	}
	
	public void setDoubleAttribute(String name, double value) { 
		if (!locked)
			attributes.put(name, Double.toString(value));
		else 
			throw new RuntimeException("Attribute List is Locked (trying to set '" + name + "'): " + this);
	}
	
	public boolean getDoubleAttribute(String name, boolean defaultValue) {
		String value = attributes.get(name);
		
		if (value == null)
			return defaultValue;
		else
			return Boolean.parseBoolean(value);
	}
	
	public void setBooleanAttribute(String name, boolean value) { 
		if (!locked)
			attributes.put(name, Boolean.toString(value));
		else 
			throw new RuntimeException("Attribute List is Locked (trying to set '" + name + "'): " + this);
	}
}

