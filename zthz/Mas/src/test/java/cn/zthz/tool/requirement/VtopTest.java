package cn.zthz.tool.requirement;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;

import org.junit.Test;

import cn.zthz.tool.common.HzException;
import cn.zthz.tool.db.Connections;

public class VtopTest {

	@Test
	public void testUpdate() throws HzException {
		Connection connection = null;
		Statement statement = null;
		try{
			connection = Connections.instance.get();
			statement = connection.createStatement();
			Vtop.instance.update(statement,"402880173f0e6534013f1c4f6ea50070", "402880173f0e6534013185cdf340060");
			
		}catch(Exception e){
			
		}finally{
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
		
	}
	
	@Test
	public void dateTo(){
		long time = 1371425229658l;
		Timestamp t = new Timestamp(time);
		System.out.println(t);
	}

}
