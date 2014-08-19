package org.mas.codehaus.finder.common.web.exception;

import org.mas.codehaus.finder.common.ErrorCodes;


public class FinderException extends Exception{
	
	private static final long serialVersionUID = -6475053347213360658L;
	protected int errorCode = ErrorCodes.SERVER_INNER_ERROR;

	public int errorCode() {
		return errorCode;
	}
	public FinderException() {
		super();
	}
	public FinderException(int errorCode , String message) {
		super(message);
		this.errorCode = errorCode;
	}
	public FinderException(int errorCode , String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}
	public FinderException(String message, Throwable cause) {
		super(message, cause);
	}
	public FinderException(String message, FinderException cause) {
		super(message, cause);
		this.errorCode = cause.errorCode;
	}
	public FinderException(String message) {
		super(message);
	}
	public FinderException(Throwable cause) {
		super(cause);
	}
	public FinderException(FinderException cause) {
		super(cause);
		this.errorCode = cause.errorCode;
	}
}
