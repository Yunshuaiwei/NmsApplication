package com.xust.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.xust.common.exception.DefinitionException;
import com.xust.common.exception.HttpStatusCode;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/4/20 12:02
 * @Version
 **/
@Component
public class JwtUtils {

    private static final String SING="$%^@$^*((^&#%$@^$^%&#23!543";

    /**
     * @return java.lang.String
     * @Param payload中需要保存的信息
     * @Date 12:17 2021/4/20
     * @Description: 获取token
     **/
    public String getToken(Map<String,Object> map){
        Calendar instance=Calendar.getInstance();
        //默认7天过期
        instance.add(Calendar.DATE,7);

        //创建jwt builder
        JWTCreator.Builder builder = JWT.create();

        //payload
        map.forEach((k,v)->{
            builder.withClaim(k,v.toString());
        });
        return builder.withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC256(SING));
    }

    /**
     * 验证token
     **/
    public void verify(String token){
        try {
            DecodedJWT verify = JWT.require(Algorithm.HMAC256(SING)).build().verify(token);
        }catch (Exception e){
            throw new DefinitionException(HttpStatusCode.TOKEN_VERIFY_ERROR);
        }
    }

    /**
     * 获取token信息
     **/
    public DecodedJWT getTokenInfo(String token){
        try {
            DecodedJWT verify = JWT.require(Algorithm.HMAC256(SING)).build().verify(token);
            return verify;
        }catch (Exception e){
            throw new DefinitionException(HttpStatusCode.TOKEN_VERIFY_ERROR);
        }
    }

    /**
     * 获取过期时间
     **/
    public Date getExpiresAt(String token){
        try{
            DecodedJWT verify = JWT.require(Algorithm.HMAC256(SING)).build().verify(token);
            return verify.getExpiresAt();
        }catch (Exception e){
            throw new DefinitionException(HttpStatusCode.TOKEN_VERIFY_ERROR);
        }

    }
}
