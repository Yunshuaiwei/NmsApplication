package com.xust.common.utils;


import com.xust.common.exception.DefinitionException;
import com.xust.common.exception.HttpStatusCode;
import lombok.Data;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/4/20 14:58
 * @Version
 **/
@Data
public class Result<T> {
    private Boolean success;

    private Integer code;

    private String msg;

    private T data;

    public Result() {
    }

    public Result(Boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public Result(String msg) {
        this.msg=msg;
    }

    public Result(Boolean success, Integer code) {
        this.success = success;
        this.code = code;
    }
    public Result(HttpStatusCode e, T data) {
        this.success=false;
        this.code = e.getErrorCode();
        this.msg = e.getErrorMsg();
        this.data=data;
    }

    public Result(Boolean success, Integer code, String msg) {
        this.success = success;
        this.code = code;
        this.msg = msg;
    }

    public Result(Boolean success, Integer code, String msg, T data) {
        this.success = success;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 自定义异常的返回结果
     **/
    public static Result defineError(DefinitionException e) {
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(e.getErrorCode());
        result.setMsg(e.getErrorMsg());
        result.setData(null);
        return result;
    }

    /**
     * 处理其他异常
     **/
    public static Result otherError(HttpStatusCode httpStatusCode) {
        Result result = new Result();
        result.setMsg(httpStatusCode.getErrorMsg());
        result.setCode(httpStatusCode.getErrorCode());
        result.setSuccess(false);
        result.setData(null);
        return result;
    }
}
