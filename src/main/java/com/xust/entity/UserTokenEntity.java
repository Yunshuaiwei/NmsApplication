package com.xust.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/4/20 21:47
 * @Version
 **/
@Data
@TableName("sys_user_token")
public class UserTokenEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    //用户ID
    @TableId(type = IdType.INPUT)
    private Long userId;
    //token
    private String token;
    //过期时间
    private Date expireTime;
    //更新时间
    private Date updateTime;
}
