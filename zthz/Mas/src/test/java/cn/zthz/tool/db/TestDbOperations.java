package cn.zthz.tool.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import cn.zthz.tool.common.HashUtils;
import cn.zthz.tool.common.LogUtils;

public class TestDbOperations {
	
	
	public static Person createPerson(){
		Person person = new Person();
		person.setAge(10);
		person.setBirthday(new Timestamp(System.currentTimeMillis()));
		person.setHeight(1.23);
		person.setId(HashUtils.uuid());
		person.setName("jim");
		person.setNote("this is a note");
		return person;
	}
	
	@Test
	public void testSave() throws SQLException {
		Connection connection =null;
		try{
			Person person = createPerson();
			connection = Connections.instance.get();
			DbOperations.instance.save(connection, person , false);
		}finally{
			if(null != connection){
				connection.close();
			}
		}
		
	}
	@Test
	public void testSave2() throws SQLException {
		Connection connection =null;
		try{
			List<Person> persons = new LinkedList<>();
			for(int i = 0 ; i <10 ; i++){
				Person person = createPerson();
				persons.add(person);
			}
			connection = Connections.instance.get();
			DbOperations.instance.save(connection, persons,true);
		}finally{
			if(null != connection){
				connection.close();
			}
		}
		
	}
	RowObjectMapping mapping = new SimpleObjectMapping();
	@Test
	public void testQuery() throws SQLException{
		Map<String, Object> args = null;
		String sql = "select * from Person limit 0 ,1";
		Connection connection =null;
		try{
			connection = Connections.instance.get();
			long t1 = System.currentTimeMillis();
			List<Person> persons = DbOperations.instance.query(connection, sql, args,Person.class ,mapping , 12);
			long t2 = System.currentTimeMillis();
			System.out.println("t2 cost:"+(t2-t1));
			System.out.println(LogUtils.format("persons", persons));
		}finally{
			if(null != connection){
				connection.close();
			}
		}
		
	}
	
	@Test
	public void testExecuteSql() throws SQLException{
		Connection connection =null;
		try{
			connection = Connections.instance.get();
			connection.setAutoCommit(false);
			String selectSql = "select * from Person where id=:id";
			Map<String, Object> args = new HashMap<>() ;
			args.put("id", "402880f53bdbe844013bdbe844bc0004");
			List<Person> ps = DbOperations.instance.query(connection, selectSql, args , Person.class, mapping, 12);
			System.out.println(LogUtils.format("ps",ps));
			String sql = "update Person set age=100 where id='402880f53bdbe844013bdbe844bc0004'";
			DbOperations.instance.executeSql(connection, sql );
		}finally{
			if(null != connection){
				connection.close();
			}
		}
	}

}
