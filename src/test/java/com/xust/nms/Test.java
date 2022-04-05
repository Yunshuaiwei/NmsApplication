package com.xust.nms;

import com.xust.common.utils.SnmpUtils;

import java.util.List;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/4/21 16:09
 * @Version
 **/
public class Test {
    @org.junit.jupiter.api.Test
    public void test() throws Exception {
        SnmpUtils utils = new SnmpUtils();
        List<String> aPublic = utils.snmpWalk("127.0.0.1", "public", "1.3.6.1.4.1.9.2.1.56.0");
        for (String s : aPublic) {
            System.out.println(s);
        }

    }
}
