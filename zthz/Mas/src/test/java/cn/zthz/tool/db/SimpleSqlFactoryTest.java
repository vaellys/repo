package cn.zthz.tool.db;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;


public class SimpleSqlFactoryTest {

	@Test
	public void testCreateNamedInsertSql(){
		
		String table ="Person";
		List<String> names = new LinkedList<>();
		names.add("id");
		names.add("name");
		String sql = SimpleSqlFactory.createNamedInsertSql(table , names);
		System.out.println(sql);
	}
	@Test
	public void testCreateNameQueryDeleteSql(){
		String table = "Person";
		List<String> names = new LinkedList<>();
		names.add("id1");
		names.add("id2");
		//String sql = SimpleSqlFactory.createNameQueryDeleteSql(table , names );
		//System.out.println(sql);
	}
	
	@Test
	public void testCreateUpdateSql(){
		
		String table = "Person";
		List<String> updateKeys = new LinkedList<>();
		List<String> conditionKeys = new LinkedList<>();
		updateKeys.add("name1");
		updateKeys.add("name2");
		conditionKeys.add("id1");
		conditionKeys.add("id2");
//		String sql = SimpleSqlFactory.createUpdateSql(table , updateKeys, conditionKeys);
//		System.out.println(sql);
	}
}
