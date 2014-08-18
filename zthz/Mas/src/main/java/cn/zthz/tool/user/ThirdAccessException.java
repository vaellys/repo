package cn.zthz.tool.user;

public class ThirdAccessException extends UserException {
	private static final long serialVersionUID = 1822703054037808095L;

	public ThirdAccessException() {
		super();
	}

	public ThirdAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ThirdAccessException(String message, Throwable cause) {
		super(message, cause);
	}

	public ThirdAccessException(String message) {
		super(message);
	}

	public ThirdAccessException(Throwable cause) {
		super(cause);
	}

}
