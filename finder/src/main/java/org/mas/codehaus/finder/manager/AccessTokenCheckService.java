package org.mas.codehaus.finder.manager;

import org.mas.codehaus.finder.manager.exception.UserException;

public interface AccessTokenCheckService {
	public void checkWeiboAccessToken(String weiboUid, String accessToken)
			throws UserException;

	public void checkQqAccessToken(String qqUid, String accessToken)
			throws UserException;
}
