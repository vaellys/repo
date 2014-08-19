package org.mas.codehaus.finder.manager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mas.codehaus.finder.common.web.session.SessionProvider;
import org.mas.codehaus.finder.entity.User;
import org.mas.codehaus.finder.manager.exception.UserException;

public interface UserService {
	public boolean register(User user) throws UserException;

	public User login(HttpServletRequest request, HttpServletResponse response,
			SessionProvider session, User user) throws UserException;

}
