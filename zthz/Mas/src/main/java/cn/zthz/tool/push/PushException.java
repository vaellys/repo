package cn.zthz.tool.push;

import cn.zthz.tool.common.HzException;

public class PushException extends HzException {
	private static final long serialVersionUID = -2018322963042488983L;

	public PushException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PushException(HzException cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public PushException(int errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
		// TODO Auto-generated constructor stub
	}

	public PushException(int errorCode, String message) {
		super(errorCode, message);
		// TODO Auto-generated constructor stub
	}

	public PushException(String message, HzException cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public PushException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public PushException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public PushException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public PushException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
