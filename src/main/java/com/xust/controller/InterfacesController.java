package com.xust.controller;

import com.xust.common.utils.Result;
import com.xust.service.InterfacesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/5/6 10:32
 * @Version
 **/
@RestController
@RequestMapping("/nms")
@Api("设备接口管理")
@Slf4j
@CrossOrigin
public class InterfacesController {

    @Autowired
    private InterfacesService interfacesService;

    @GetMapping("/interfaces")
    @ApiOperation("获取设备接口信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ip", value = "ip地址", defaultValue = "127.0.0.1", required = false, dataType = "String"),
            @ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显式条数", required = true, dataType = "String")
    })
    public Result getInterfacesInfo(@RequestParam(value = "ip", required = false) String ip,
                                    @RequestParam long pageNum,
                                    @RequestParam long pageSize) {
        Map<String, Object> map = interfacesService.getInterfacesInfo(ip, pageNum, pageSize);
        return new Result(true, 200, "OK", map);
    }

}
