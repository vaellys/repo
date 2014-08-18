package cn.zthz.tool.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface DbGeneralOperation {
	
	/**
	 * @param object if object inherit from Collection ,save each object from collection
	 * @param connection
	 */
	public void save(Connection connection ,Object object ,boolean isAutoCommitBatch)throws SQLException;
	
	public void delete(Connection connection , Object id )throws SQLException;
	
	public int update(Connection connection ,String table,Map<String, Object> conditionMap ,Map<String, Object> newProperties)throws SQLException;
	
	public <T> T get(Connection connection ,Object id , Class<T> clazz)throws SQLException;
	
	public <T> List<T> gets(Connection connection ,Collection<T> ids , Class<T> clazz)throws SQLException;
	
	public <T> List<T> getBySql(Connection connection , String sql , Class<T> clazz , Map<String  , Object> args )throws SQLException;
	
	public <T> List<T> getBySql(Connection connection , String sql , Class<T> clazz, Object... args)throws SQLException;
	
	public void executeSql(Connection connection ,String sql, Object... args)throws SQLException;
	
	public void executeSql(Connection connection ,String sql,  Map<String  , Object> args )throws SQLException;
	
	public <T> List<T> query(Connection connection, String sql,	RowObjectMapping mapping ,  Class<T> clazz , int fetchSize,Object... args) throws SQLException;
	
	public <T> List<T> query(Connection connection , String sql , Map<String  , Object> args , Class<T> clazz , RowObjectMapping mapping, int fetchSize )throws SQLException;

	int executeUpdate(Connection connection, String sql, Object[] args) throws SQLException;

}
