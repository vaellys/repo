package cn.zthz.tool.common;

import cn.zthz.actor.rest.ErrorCodes;

public class HzException extends Exception implements ErrorCode{
	private static final long serialVersionUID = -6475053347213360658L;
	protected int errorCode = ErrorCodes.SERVER_INNER_ERROR;

	public int errorCode() {
		return errorCode;
	}

	public HzException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public HzException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public HzException(int errorCode , String message) {
		super(message);
		this.errorCode = errorCode;
	}
	public HzException(int errorCode , String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}
	public HzException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
	public HzException(String message, HzException cause) {
		super(message, cause);
		this.errorCode = cause.errorCode;
		// TODO Auto-generated constructor stub
	}

	public HzException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public HzException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	public HzException(HzException cause) {
		super(cause);
		this.errorCode = cause.errorCode;
	}

}
