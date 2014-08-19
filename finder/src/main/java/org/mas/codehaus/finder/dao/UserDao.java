package org.mas.codehaus.finder.dao;

import org.mas.codehaus.finder.entity.User;

public interface UserDao {
    int deleteByPrimaryKey(Integer id);

    int register(User user);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    
    public User getUserByUserId(Integer id);
    
    public User getUserInfoByUserId(Integer id);
    
    public int getUserByUserName(String username);
    
    public User findUserByUserName(String username);
}