package com.xust.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/5/13 17:45
 * @Version
 **/
@Data
@TableName(value = "ip_address")
public class IpEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String ip;
}
