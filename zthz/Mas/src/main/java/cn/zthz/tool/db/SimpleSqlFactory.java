package cn.zthz.tool.db;

import java.util.LinkedList;
import java.util.List;

public class SimpleSqlFactory {
	public static String createNamedInsertSql(String table , Iterable<String> names){
		StringBuilder sql = new StringBuilder();
		sql.append("insert into ");
		sql.append(table);
		sql.append("(");
		for (String name : names) {
			sql.append(name);
			sql.append(',');
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(')');
		sql.append(" values ");
		sql.append("(");
		for (String name : names) {
			sql.append(':');
			sql.append(name);
			sql.append(',');
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(')');
		return sql.toString();
	}
	
	public static String createNamedDeleteSql(String table , List<String> columnNames ){
		StringBuilder sql = new StringBuilder();
		sql.append("delete from ");
		sql.append(table);
		sql.append(" where ");
		for (String name : columnNames) {
			sql.append(name);
			sql.append("=:");
			sql.append(name);
			sql.append(" and ");
		}
		sql.delete(sql.length()-5 , sql.length());
		return sql.toString();
	}
	public static String createNamedUpdateSql(String table , Iterable<String> updateColumnNames , Iterable<String> conditionNames ){
		StringBuilder sql = new StringBuilder();
		sql.append("update ");
		sql.append(table);
		sql.append(" set ");
		for (String name : updateColumnNames) {
			sql.append(name);
			sql.append("=:");
			sql.append(name);
			sql.append(",");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(" where ");
		for (String name : conditionNames) {
			sql.append(name);
			sql.append("=:");
			sql.append(name);
			sql.append(" and ");
		}
		sql.delete(sql.length()-5 , sql.length());
		return sql.toString();
	}
	public static String createNamedQuerySql(String table , Iterable<String> columnNames , Iterable<String> conditionNames ){
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		if(null == columnNames){
			sql.append('*');
		}else{
			for (String column : columnNames) {
				sql.append(column);
				sql.append(' ');
			}
		}
		sql.append(" from ");
		sql.append(table);
		sql.append(" where ");
		for (String name : conditionNames) {
			sql.append(name);
			sql.append("=:");
			sql.append(name);
			sql.append(" and ");
		}
		sql.delete(sql.length()-5 , sql.length());
		return sql.toString();
	}
	
	public static void main(String[] args) {
		List<String> updateColumnNames = new LinkedList<>();
		updateColumnNames.add("add1");
		updateColumnNames.add("add2");
		System.out.println(createNamedQuerySql("Person", null, updateColumnNames));
	}
	
}
