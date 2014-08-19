package org.mas.codehaus.finder.common;

public class ErrorCodes {
	/**
	 * server error
	 */
	public static final int PARAMETER_INVALID = 1;
	public static final int SERVER_INNER_ERROR = 3;
	/**
	 * user error [100,200)
	 */
	public static final int USER_NAME_EMPTY = 101;
	public static final int USER_NAME_EXIST = 102;
	public static final int USER_NAME_INVALID = 103;
	public static final int PASSWORD_INVALID = 104;
	public static final int USER_UNLOGIN = 105;
	public static final int TOKEN_EXPIRED = 106;
}
