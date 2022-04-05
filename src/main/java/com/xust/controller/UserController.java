package com.xust.controller;

import com.xust.common.utils.Result;
import com.xust.entity.UserEntity;
import com.xust.service.UserService;
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
 * @Date 2021/4/28 22:28
 * @Version
 **/
@RestController
@RequestMapping("/nms")
@Api("用户接口")
@Slf4j
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    @ApiOperation("获取所有用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询参数", required = false, dataType = "String"),
            @ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显式条数", required = true, dataType = "String")
    })
    public Map getAllUsers(@RequestParam(value = "query",required = false) String query,
                           @RequestParam long pageNum,
                           @RequestParam long pageSize) {
        log.info("获取用户列表,query= {},pageNum= {},pageSize= {}",query,pageNum,pageSize);
        Map res = userService.getAllUsers(query,pageNum, pageSize);
        HashMap<String, Object> map = new HashMap<>(4);
        map.put("code",200);
        map.put("msg","获取管理员列表成功");
        map.put("success",true);
        map.put("data",res);
        return map;
    }

    @RequestMapping(value = "users/{id}/status/{mgStatus}",method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "mgStatus", value = "用户状态", required = true, dataType = "Boolean"),
    })
    @ApiOperation("修改用户状态")
    public Result setUserStatus(@PathVariable long id,@PathVariable Boolean mgStatus){
        userService.setUserStatus(id,mgStatus);
        return new Result(true,200,"操作成功");
    }

    @PostMapping("/users")
    @ApiOperation("添加用户")
    public Result addUser(@RequestBody UserEntity user){
        log.info("添加用户 {}",user);
        userService.addUser(user);
        return new Result(true,200,"添加成功");
    }

    @GetMapping("/users/{id}")
    @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "String")
    @ApiOperation("根据id查询用户")
    public Map<String, Object> getUserById(@PathVariable long id){
        UserEntity user = userService.getUserById(id);
        HashMap<String, Object> res = new HashMap<>(4);
        res.put("code",200);
        res.put("success",true);
        res.put("data",user);
        res.put("msg","获取用户信息成功");
        return res;
    }

    @RequestMapping(value = "/users/{id}",method = RequestMethod.PUT)
    @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "String")
    @ApiOperation("根据id修改用户信息")
    public Result updateUser(@RequestBody UserEntity user,@PathVariable long id){
        userService.updateUser(id,user);
        return new Result(true,200,"修改成功");
    }

    @RequestMapping(value = "/users/{id}",method = RequestMethod.DELETE)
    @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "String")
    @ApiOperation("根据id删除用户")
    public Result deleteUser(@PathVariable long id){
        userService.deleteUserById(id);
        return new Result(true,200,"操作成功！");
    }
}
