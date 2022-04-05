package com.xust.common.utils;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xust.common.exception.DefinitionException;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/4/20 16:46
 * @Version
 **/
public abstract class Assert {
    public static void isBlank(String str, String message) {
        if (StringUtils.isBlank(str)) {
            throw new DefinitionException(message);
        }
    }

    public static void isNull(Object object, String message) {
        if (object == null) {
            throw new DefinitionException(message);
        }
    }

}
