package com.thomaschampagne.hourglass.library.exceptions;

public class SyncParsingException extends SyncException {

	private static final long serialVersionUID = 1;

	public SyncParsingException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}
}
