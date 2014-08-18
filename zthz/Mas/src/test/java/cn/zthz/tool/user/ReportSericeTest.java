package cn.zthz.tool.user;

import static org.junit.Assert.*;

import org.junit.Test;

public class ReportSericeTest {

	@Test
	public void testReport() throws UserException {
		ReportService.instance.report("402880173cf01658013cf0215cf40001", "402880173d008032013d050a5c4b0043", "你真坏", 0);
	}

}
