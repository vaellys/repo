package cn.zthz.tool.common;

import cn.zthz.actor.rest.ErrorCodes;

public class HzRuntimeException extends RuntimeException implements ErrorCode{

	private static final long serialVersionUID = 1L;
	protected int errorCode = ErrorCodes.SERVER_INNER_ERROR;

	public int errorCode() {
		return errorCode;
	}

	public HzRuntimeException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public HzRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public HzRuntimeException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public HzRuntimeException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
	public HzRuntimeException(int errorCode) {
		this.errorCode = errorCode;
	}
	public HzRuntimeException(int errorCode , String message) {
		super(message);
		this.errorCode = errorCode;
	}
	public HzRuntimeException(int errorCode , String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public HzRuntimeException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
