package org.mas.codehaus.finder.common;

import org.mas.codehaus.finder.common.web.exception.FinderException;

public class CheckParams {
	
	public static String getString(String key, String value, boolean isCheckNull) throws FinderException {
		if (isCheckNull) {
			checkParameterNotNull(key, value);
		}
		return value;
	}
	
	public static void checkParameterNotNull(String key, String value) throws FinderException {
		if(StringUtils.isEmpty(value)){
			throw new FinderException(ErrorCodes.PARAMETER_INVALID, key + " is empty");
		}
	}
}
