package qileke.jer.server;

public class JerException extends RuntimeException {

	private static final long serialVersionUID = -5944295649906121982L;

	public JerException() {
		super();
		
	}

	public JerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

	public JerException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public JerException(String message) {
		super(message);
		
	}

	public JerException(Throwable cause) {
		super(cause);
		
	}

}
