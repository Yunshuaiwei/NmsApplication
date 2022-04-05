package com.xust.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/5/14 10:01
 * @Version
 **/
@TableName(value = "device")
@Data
public class SystemEntity {

    @TableId(type = IdType.AUTO)
    private long id;

    private String sysDescr;

    private String ip;

    private String sysObjectId;

    private String sysUpTime;

    private String sysContact;

    private String sysName;

    private String sysLocation;

    private String sysServices;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     **/
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
