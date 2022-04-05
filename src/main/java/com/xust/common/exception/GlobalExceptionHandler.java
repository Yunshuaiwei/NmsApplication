package com.xust.common.exception;

import com.xust.common.utils.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/4/20 14:55
 * @Version
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 处理自定义异常
     **/
    @ExceptionHandler(value = DefinitionException.class)
    @ResponseBody
    public Result bizExceptionHandler(DefinitionException e){
        return Result.defineError(e);
    }

    /**
     * 处理其他异常
     **/
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result exceptionHandler(Exception e){
        e.printStackTrace() ;
        return Result.otherError(HttpStatusCode.INTERNAL_SERVER_ERROR);
    }
}
