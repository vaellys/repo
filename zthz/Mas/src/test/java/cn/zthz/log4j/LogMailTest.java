package cn.zthz.log4j;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class LogMailTest {
	private static final Log log = LogFactory.getLog(LogMailTest.class);
	@Test
	public void testMail(){
		try{
		throw new RuntimeException();
		}catch (Exception e ){
		log.error("hello" ,e);
		}
	}

}
