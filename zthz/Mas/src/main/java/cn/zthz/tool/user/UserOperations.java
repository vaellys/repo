package cn.zthz.tool.user;

import java.util.List;
import java.util.Map;

public interface UserOperations {

	public String save(User user) throws UserException;
	public boolean checkExists(String uid, String accessToken ,String type) throws UserException;

	public void update(String userId, Map<String, Object> newVules) throws UserException;

	public User get(String userId) throws UserException;
	
	public List<User> query(Map<String, Object> args) throws UserException ;

	void checkUserToken(String userId, String userToken) throws UserException;
	
}
