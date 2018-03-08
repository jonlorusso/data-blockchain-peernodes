package com.swatt.util;

public class OperationFailedException extends Exception {
	private static final long	serialVersionUID	= 1L;
	
	public OperationFailedException(String message) { super(message); }
	public OperationFailedException(Throwable exception) { super(exception); }
	public OperationFailedException(String message, Throwable exception) { super(message, exception); }
}
