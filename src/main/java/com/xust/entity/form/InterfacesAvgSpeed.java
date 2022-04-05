package com.xust.entity.form;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/5/11 10:24
 * @Version
 **/
@TableName(value = "interfaces_avg_speed")
@Data
public class InterfacesAvgSpeed {

    private String ip;

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 横坐标下标
     **/
    private String xData;

    private String inData;

    private String outData;

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
