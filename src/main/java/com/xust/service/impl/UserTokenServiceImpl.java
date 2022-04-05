package com.xust.service.impl;

import com.xust.common.utils.JwtUtils;
import com.xust.common.utils.Result;
import com.xust.dao.UserTokenDao;
import com.xust.entity.UserTokenEntity;
import com.xust.service.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/4/20 21:17
 * @Version
 **/
@Service
public class UserTokenServiceImpl implements UserTokenService {

    /**
     * 7天后过期
     **/
    private final static int EXPIRE = 3600 * 24 * 7;

    @Autowired
    private UserTokenDao userTokenDao;

    @Autowired
    private JwtUtils jwtUtils;


    @Override
    public Result createToken(long userId) {
        HashMap<String, Object> map = new HashMap<>(1);
        map.put("userId", userId);
        String token = jwtUtils.getToken(map);
        Date now = new Date();
        //token的过期时间
        Date expireTime = jwtUtils.getExpiresAt(token);
        //判断是否生成过token
        UserTokenEntity tokenEntity = userTokenDao.selectById(userId);
        if (tokenEntity == null) {
            tokenEntity = new UserTokenEntity();
            tokenEntity.setUserId(userId);
            tokenEntity.setToken(token);
            tokenEntity.setUpdateTime(now);
            tokenEntity.setExpireTime(expireTime);
            //保存token
            userTokenDao.insert(tokenEntity);
        } else {
            tokenEntity.setToken(token);
            tokenEntity.setUpdateTime(now);
            tokenEntity.setExpireTime(expireTime);
            //更新token
            userTokenDao.updateById(tokenEntity);
        }
        HashMap<String, Object> res = new HashMap<>(2);
        res.put("token", token);
        res.put("expire", EXPIRE);
        return new Result(true, 200, "ok", res);
    }

    @Override
    public void logout(long userId) {
        HashMap<String, Object> map = new HashMap<>(1);
        map.put("userId", userId);
        //生成一个token
        String token = jwtUtils.getToken(map);
        //修改token
        UserTokenEntity tokenEntity = new UserTokenEntity();
        tokenEntity.setUserId(userId);
        tokenEntity.setToken(token);
        userTokenDao.updateById(tokenEntity);
    }
}
