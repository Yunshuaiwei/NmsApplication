package com.xust.common.exception;

import lombok.Getter;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/4/20 15:04
 * @Version
 **/
@Getter
public enum HttpStatusCode {
    //自定义的错误
    SUCCESS(200,"ok"),
    NO_PERMISSION(403,"权限不足"),
    NO_AUTH(403,"未登录"),
    NOT_FOUND(404,"未找到资源"),
    LOGIN_FAIL(405,"用户名或密码错误"),
    TOKEN_ERROR(406,"生成token失败"),
    TOKEN_VERIFY_ERROR(407,"验证token失败"),
    IPADDRESS_ERROR(408,"IP地址格式错误"),
    INTERNAL_SERVER_ERROR(500,"服务器内部异常");


    private Integer errorCode;

    private String errorMsg;

    HttpStatusCode(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }


}
