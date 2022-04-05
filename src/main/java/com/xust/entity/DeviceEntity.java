package com.xust.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/5/3 16:28
 * @Version
 **/
@Data
@TableName("device")
public class DeviceEntity {
    private static final long serialVersionUID = 2L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 设备用户名称
     **/
    private String deviceUserName;

    /**
     * 设备系统信息
     **/
    private String deviceSysOperation;

    /**
     * 设备用内存大小
     **/
    private Integer deviceRam;

    /**
     * 设备运行时间
     **/
    private Long deviceRuntime;

    /**
     * 设备磁盘使用情况
     **/
    private String deviceDiskUsed;

    /**
     * 设备磁盘未使用情况
     **/
    private String deviceDiskUnused;

    /**
     * 设备磁盘大小
     **/
    @TableField(exist = false)
    private String deviceDisk;


    @TableField(exist = false)
    private Integer cpu;


    @TableField(exist = false)
    private Integer memory;

    /**
     * 设备软件个数
     **/
    private Integer deviceSoftwareNumber;

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
