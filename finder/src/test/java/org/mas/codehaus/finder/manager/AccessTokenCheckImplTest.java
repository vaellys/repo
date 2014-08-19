package org.mas.codehaus.finder.manager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mas.codehaus.finder.manager.exception.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml",
		"classpath:spring-mybatis.xml" })
public class AccessTokenCheckImplTest {
	private AccessTokenCheckService atcs;
	private static Logger log = LoggerFactory.getLogger(AccessTokenCheckImplTest.class);
	@Test
	public void testCheckWeiboAccessToken() {
		try {
			atcs.checkWeiboAccessToken("3226203843", "2.00rzo1WDRYT4DB9cd5086de00lHtEv");
		} catch (UserException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCheckQqAccessToken() {
		try {
			atcs.checkQqAccessToken("3C2C7441F12B00FA26B9941399860906", "E8A64730669F4A781C4B464372647B0E");
		} catch (UserException e) {
			e.printStackTrace();
		}
	}

	public AccessTokenCheckService getAtcs() {
		return atcs;
	}
	@Autowired
	public void setAtcs(AccessTokenCheckService atcs) {
		this.atcs = atcs;
	}
}
