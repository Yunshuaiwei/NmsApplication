package com.xust.controller;

import com.xust.common.utils.Result;
import com.xust.entity.form.DeviceForm;
import com.xust.service.DeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/5/3 16:10
 * @Version
 **/
@RestController
@RequestMapping("/nms")
@Api("设备管理接口")
@Slf4j
@CrossOrigin
public class DeviceController {
    @Autowired
    private DeviceService deviceService;

    @ApiOperation("获取设备信息")
    @GetMapping("/devices")
    @ApiImplicitParam(name = "ip", value = "ip地址", defaultValue = "127.0.0.1", required = false, dataType = "String")
    public Map<String, Object> getDevicesInfo(@RequestParam(value = "ip", required = false) String ip) {
        log.info("获取设备信息，请求的IP地址为：{}", ip);
        HashMap<String, Object> res = new HashMap<>(4);
        DeviceForm device = deviceService.getDevicesInfo(ip);
        res.put("code", 200);
        res.put("success", true);
        res.put("data", device);
        res.put("msg", "获取数据成功！");
        return res;
    }

    @ApiOperation("获取软件列表")
    @GetMapping("/softwareList")
    public Result getSoftwareList(@RequestParam("pageNum") String pageNum,
                                  @RequestParam("pageSize") String pageSize,
                                  @RequestParam("ip") String ip) {
        Map<String, Object> softwareList = deviceService.getSoftwareList(pageNum, pageSize, ip);

        return new Result(true, 200, "获取数据成功", softwareList);
    }


    @GetMapping("/device")
    @ApiOperation("获取系统运行时间和接口速率")
    @ApiImplicitParam(name = "ip", value = "IP地址", required = false, defaultValue = "127.0.0.1", dataType = "String")
    public Result getDeviceInfo(@RequestParam(value = "ip", required = false) String ip) {
        DeviceForm deviceInfo = deviceService.getDeviceInfo(ip);
        return new Result(true, 200, "获取数据成功", deviceInfo);
    }

    @GetMapping("/ipList")
    @ApiOperation("获取ip列表")
    public Result getIpList() {
        Map[] ipList = deviceService.getIpList();
        return new Result(true, 200, "获取数据成功", ipList);
    }

    @GetMapping("/systemInfo")
    @ApiOperation("获取系统信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询参数", required = false, dataType = "String"),
            @ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显式条数", required = true, dataType = "String")}
    )
    public Result getSystemInfo(@RequestParam(value = "ip") String ip,
                                @RequestParam(value = "pageNum") long pageNum,
                                @RequestParam(value = "pageSize") long pageSize) {

        Map<String, Object> data = deviceService.getSystemInfo(ip, pageNum, pageSize);
        return new Result(true,200,"获取数据成功",data);
    }
}
