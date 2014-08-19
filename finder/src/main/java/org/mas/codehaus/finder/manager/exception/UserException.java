package org.mas.codehaus.finder.manager.exception;

import org.mas.codehaus.finder.common.web.exception.FinderException;

public class UserException extends FinderException {

	private static final long serialVersionUID = 6774300689862865272L;

	public UserException() {
		super();
	}

	public UserException(FinderException cause) {
		super(cause);
	}

	public UserException(int errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	public UserException(int errorCode, String message) {
		super(errorCode, message);
	}

	public UserException(String message, FinderException cause) {
		super(message, cause);
	}

	public UserException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserException(String message) {
		super(message);
	}

	public UserException(Throwable cause) {
		super(cause);
	}
}
