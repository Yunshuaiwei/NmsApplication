package com.xust.entity.po;

import java.io.IOException;


/**
 * @author ysw
 */
public interface SnmpInterf {

    /**
     * 取单个属性值方法
     **/
    String getSnmpRequest(GetSnmpPo po) throws IOException;

    /**
     * 设置某个属性值的方法
     **/
    boolean setSnmpRequest(SetSnmpPo po) throws IOException;

}
