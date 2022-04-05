package com.xust.service;

import java.util.Map;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/5/6 10:34
 * @Version
 **/
public interface InterfacesService {

    /**
     * 获取接口信息
     *
     * @return*/
    Map<String,Object> getInterfacesInfo(String ip, long pageNum, long pageSize);
}
