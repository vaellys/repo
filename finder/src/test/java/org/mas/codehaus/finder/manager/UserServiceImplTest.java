package org.mas.codehaus.finder.manager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mas.codehaus.finder.common.LogUtils;
import org.mas.codehaus.finder.entity.User;
import org.mas.codehaus.finder.manager.exception.UserException;
import org.mas.codehaus.finder.manager.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml",
		"classpath:spring-mybatis.xml" })
public class UserServiceImplTest {
	private UserService userService;
	private static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

	@Test
	public void testRegister() {
		User user = new User();
		user.setName("617907867121111@qq.com");
		user.setPassword("123456");
		try {
			boolean result = userService.register(user);
			if (log.isDebugEnabled()) {
				log.debug(LogUtils.format("r", user));
			}
		} catch (UserException e) {
			e.printStackTrace();
		}
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
