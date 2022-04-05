package com.xust.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/5/6 10:25
 * @Version
 **/
@Data
@TableName("interfaces")
public class InterfacesEntity implements Serializable {
    private static final long serialVersionUID = 2L;
    /**
     * 对应各接口的索引
     **/
    @TableId
    private long id;

    /**
     * 设备ip地址
     **/
    private String ip;

    /**
     * 接口索引
     **/
    private long ifIndex;

    /**
     * 接口的描述信息
     **/
    private String ifDescr;

    /**
     * 接口类型
     **/
    private String ifType;

    /**
     * 接口类型描述信息
     **/
    private String ifTypeDescr;

    /**
     * 最大协议数据单元
     **/
    private long ifMtu;

    /**
     * 接口速率
     **/
    private long ifSpeed;

    /**
     * 接口的MAC地址
     **/
    private String ifPhysAddress;

    /**
     * 接口的管理状态
     * up(1),down(2),testing(3)
     **/
    private Integer ifAdminStatus;

    /**
     * 接口的操作状态
     * up(1),down(2),testing(3)
     **/
    private Integer ifOperStatus;

    /**
     * 接口状态
     **/
    private String ifInterfaceStatus;

    /**
     * 接口收到的总字节数
     **/
    private long ifInOctets;

    /**
     * 接口发出的总字节数
     **/
    private long ifOutOctets;

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
