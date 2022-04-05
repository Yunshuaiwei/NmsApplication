package com.xust.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xust.common.utils.Utils;
import com.xust.dao.UserDao;
import com.xust.entity.UserEntity;
import com.xust.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/4/20 16:39
 * @Version
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private Utils utils;


    @Override
    public UserEntity queryByUserName(String username) {
        return userDao.selectOne(new QueryWrapper<UserEntity>().eq("username",username));
    }

    @Override
    public Map getAllUsers(String query,long pageNum, long pageSize) {
        Page<UserEntity> page = new Page<UserEntity>(pageNum, pageSize);
        Page<UserEntity> userEntityPage = null;
        if (!"".equals(query)){
            QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("username",query)
                    .or()
                    .eq("role_name",query)
                    .or()
                    .eq("mobile",query);
            userEntityPage=userDao.selectPage(page,wrapper);
        }else{
            userEntityPage=userDao.selectPage(page,null);
        }
        List<UserEntity> records = userEntityPage.getRecords();

        HashMap<String, Object> res = new HashMap<>();
        List<UserEntity> list = new ArrayList<>();
        if (records!=null){
            for (UserEntity record : records) {
                if (record.getStatus()==1){
                    record.setMgStatus(true);
                }
                record.setPassword(null);
                list.add(record);
            }
        }
        res.put("pageNum",pageNum);
        res.put("total",userEntityPage.getTotal());
        res.put("users",list);
        return res;
    }

    @Override
    public void setUserStatus(long userId, Boolean mgStatus) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        UserEntity user = userDao.selectOne(wrapper.eq("user_id", userId));
        if (mgStatus){
            user.setStatus(1);
        }else{
            user.setStatus(0);
        }
        userDao.updateById(user);
    }

    @Override
    public void addUser(UserEntity user) {
        user.setPassword(utils.getPassword(user.getPassword()));
        user.setStatus(1);
        userDao.insert(user);
    }

    @Override
    public UserEntity getUserById(long id) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        UserEntity user = userDao.selectOne(wrapper.eq("user_id", id));
        if (user.getStatus()==1){
            user.setMgStatus(true);
        }else{
            user.setMgStatus(false);
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public void updateUser(long id ,UserEntity user) {
        UserEntity userEntity = userDao.selectById(id);
        userEntity.setEmail(user.getEmail());
        userEntity.setMobile(user.getMobile());
        userDao.updateById(userEntity);
    }

    @Override
    public void deleteUserById(long id) {
        userDao.deleteById(id);
    }
}
