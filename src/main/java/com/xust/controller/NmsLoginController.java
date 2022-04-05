package com.xust.controller;

import com.xust.common.form.LoginForm;
import com.xust.common.utils.Result;
import com.xust.entity.UserEntity;
import com.xust.service.UserService;
import com.xust.service.UserTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/4/20 15:59
 * @Version
 **/
@RestController
@RequestMapping("/nms")
@Api("nms登录接口")
@Slf4j
@CrossOrigin
public class NmsLoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserTokenService userTokenService;

    @PostMapping("/login")
    @ApiOperation("登录")
    public Result login(@RequestBody LoginForm form) {
        log.info("登录用户信息 {}", form.toString());
        UserEntity user = userService.queryByUserName(form.getUsername());
        if (user == null || !user.getPassword().equals(DigestUtils.sha256Hex(form.getPassword()))) {
            return new Result(false,"账号或密码不正确");
        }
        //账号锁定
        if (user.getStatus() == 0) {
            return new Result(false,"账号已被锁定,请联系管理员");
        }
        //生成token，并保存到数据库
        return userTokenService.createToken(user.getUserId());
    }

    @PostMapping("/logout")
    @ApiOperation("退出")
    public Result logout() {
        log.info("退出系统......");
        System.out.println("退出");
//        userTokenService.logout();
        return new Result(true, 200, "ok");
    }

}
