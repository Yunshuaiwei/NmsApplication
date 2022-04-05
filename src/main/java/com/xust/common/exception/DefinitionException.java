package com.xust.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/4/20 14:51
 * @Version
 **/
@Getter
@Setter
public class DefinitionException extends RuntimeException{
    protected Integer errorCode;
    protected String errorMsg;

    public DefinitionException(){}

    public DefinitionException(String str,Exception e){
        new Exception(str, e);
        e.printStackTrace();
    }

    public DefinitionException(String msg){
        this.errorMsg=msg;
    }

    public DefinitionException(HttpStatusCode httpStatusCode){
        this.errorCode= httpStatusCode.getErrorCode();
        this.errorMsg= httpStatusCode.getErrorMsg();
    }
}
