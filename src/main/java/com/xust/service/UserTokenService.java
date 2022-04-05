package com.xust.service;

import com.xust.common.utils.Result;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/4/20 21:14
 * @Version
 **/
public interface UserTokenService {

    /**
     * 生成token
     **/
    Result createToken(long userId);

    /**
     * 退出，修改token
     **/
    void logout(long userId);
}
