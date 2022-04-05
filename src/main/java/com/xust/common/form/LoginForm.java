package com.xust.common.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/4/20 16:22
 * @Version
 **/
@Data
@ApiModel(value = "登录表单")
public class LoginForm {

    private String username;
    private String password;
}
