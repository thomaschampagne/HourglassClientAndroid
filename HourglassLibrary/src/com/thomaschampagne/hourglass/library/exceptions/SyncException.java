package com.thomaschampagne.hourglass.library.exceptions;

public class SyncException extends Exception {

	private static final long serialVersionUID = 1;
	
	protected String errorMessage;

	public SyncException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
