package com.xust.service;

import com.xust.entity.UserEntity;

import java.util.Map;

/**
 * @author ysw
 */
public interface UserService {

    /**
     * 根据用户名查询
     **/
    UserEntity queryByUserName(String username);

    /**
     * 查询用户列表
     **/
    Map getAllUsers(String query,long pageNum, long pageSize);

    void setUserStatus(long userId, Boolean mgStatus);

    void addUser(UserEntity user);

    UserEntity getUserById(long id);

    void updateUser(long id,UserEntity user);

    void deleteUserById(long id);
}
