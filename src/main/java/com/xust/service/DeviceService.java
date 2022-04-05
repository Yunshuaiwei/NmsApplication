package com.xust.service;

import com.xust.entity.form.DeviceForm;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/5/3 16:51
 * @Version
 **/
public interface DeviceService {


    DeviceForm getDevicesInfo(String ip);

    Map<String,Object> getSoftwareList(String pageNum, String pageSize, String ip);

    /**
     * @return com.xust.entity.form.DeviceForm
     * @Param
     * @Date 11:00 2021/5/12
     * @Description: 查询系统运行时间和接口速率
     **/
    DeviceForm getDeviceInfo(String ip);

    Map[] getIpList();

    Map<String ,Object> getSystemInfo(String ip, long pageNum, long pageSize);
}
