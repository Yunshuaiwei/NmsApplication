package com.xust.common.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: 公共工具类
 * @Author YunShuaiWei
 * @Date 2021/4/29 15:11
 * @Version
 **/
@Component
public class Utils {

    public String getPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }

    /**
     * @return java.lang.Boolean
     * @Param
     * @Date 10:47 2021/5/14
     * @Description: 正则匹配IP地址
     **/
    public Boolean matchIpAddress(String ip){
        String pattern = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(ip);
        return m.matches();
    }
}
