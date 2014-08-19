package org.mas.codehaus.finder.manager.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mas.codehaus.finder.common.CheckParams;
import org.mas.codehaus.finder.common.Constants;
import org.mas.codehaus.finder.common.ErrorCodes;
import org.mas.codehaus.finder.common.JsonUtils;
import org.mas.codehaus.finder.common.LogUtils;
import org.mas.codehaus.finder.common.web.exception.FinderException;
import org.mas.codehaus.finder.common.web.session.SessionProvider;
import org.mas.codehaus.finder.dao.UserDao;
import org.mas.codehaus.finder.entity.User;
import org.mas.codehaus.finder.manager.UserService;
import org.mas.codehaus.finder.manager.exception.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

	private static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

	private UserDao dao;

	@Autowired
	public void setUserMapper(UserDao dao) {
		this.dao = dao;
	}

	@Override
	public boolean register(User user) throws UserException {
		log.debug(LogUtils.format("requestParams", JsonUtils.toJsonString(user)));
		try {
			// String userToken = HashUtils.uuid();
			if (null != user) {
				// check username and password is null
				String username = CheckParams.getString("username",
						user.getName(), true);
				String password = CheckParams.getString("password",
						user.getPassword(), true);
				// check username unique
				if (0 < dao.getUserByUserName(username)) {
					log.debug("username is exist!");
					throw new UserException(ErrorCodes.USER_NAME_EXIST,
							"username is exist");
				}
				user.setEmail(username);
				user.setName(username);
				user.setPassword(password);
				// user.setUserToken(userToken);
				int count = dao.register(user);
				if (0 < count) {
					return true;
				}
			}
		} catch (FinderException e) {
			log.error("register user failed! id {} " + user.getId(), e);
			throw new UserException(e);
		}
		return false;
	}

	@Override
	public User login(HttpServletRequest request,
			HttpServletResponse response, SessionProvider session, User user)
			throws UserException {
		log.debug(LogUtils.format("requestParams", JsonUtils.toJsonString(user)));
		try {
			String username = user.getName();
			String password = user.getPassword();
			User u = dao.findUserByUserName(username);
			if (null == u) {
				log.debug("username is invalid!");
				throw new UserException(ErrorCodes.USER_NAME_INVALID,
						"username is invalid");
			}
			if (!u.getPassword().equals(password)) {
				log.debug("password is invalid!");
				throw new UserException(ErrorCodes.PASSWORD_INVALID,
						"password is invalid");
			}
			session.setAttribute(request, response, Constants.USER_ID,
					u.getId());
			return u;
		} catch (UserException e) {
			log.error("login failed! id {} " + user.getId(), e);
			throw new UserException(e);
		}
	}
}
