package org.mas.codehaus.finder.action.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mas.codehaus.finder.common.LogUtils;
import org.mas.codehaus.finder.common.web.BaseFunctional;
import org.mas.codehaus.finder.common.web.exception.FinderException;
import org.mas.codehaus.finder.common.web.session.SessionProvider;
import org.mas.codehaus.finder.entity.User;
import org.mas.codehaus.finder.manager.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserAction extends BaseFunctional {
	@Autowired
	private UserService userService;
	@Autowired
	private SessionProvider session;

	@RequestMapping("/register")
	public void register(HttpServletRequest request,
			HttpServletResponse response, User user) {
		try {
			boolean result = userService.register(user);
			if (result) {
				putSuccess(request, response);
			} else {
				return;
			}
		} catch (FinderException e) {
			log.error(
					LogUtils.format("requestParams", request.getParameterMap()),
					e);
			putError(request, response, e);
		}
	}

	@RequestMapping("/login")
	public void login(HttpServletRequest request, HttpServletResponse response,
			User user) {
		try {
			User bean = userService.login(request, response, session, user);
			if (null != bean) {
				putSuccess(request, response);
			} else {
				return;
			}
		} catch (FinderException e) {
			log.error(
					LogUtils.format("requestParams", request.getParameterMap()),
					e);
			putError(request, response, e);
		}
	}
}
