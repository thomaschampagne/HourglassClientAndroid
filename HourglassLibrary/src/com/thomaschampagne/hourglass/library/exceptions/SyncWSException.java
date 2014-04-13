package com.thomaschampagne.hourglass.library.exceptions;

public class SyncWSException extends SyncException {

	private static final long serialVersionUID = 1;

	public SyncWSException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
