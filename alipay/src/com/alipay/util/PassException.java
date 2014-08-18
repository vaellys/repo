package com.alipay.util;

public class PassException extends RuntimeException{
	private static final long serialVersionUID = -3401561795165672579L;

//	public PassException() {
//		super();
//	}

//	public PassException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
//		super(message, cause, enableSuppression, writableStackTrace);
//	}

	public PassException(String message, Throwable cause) {
		super(message, cause);
	}

	public PassException(String message) {
		super(message);
	}

	public PassException(Throwable cause) {
		super(cause);
	}
}
