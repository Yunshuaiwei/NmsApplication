package com.xust.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/5/8 14:52
 * @Version
 **/
@Data
@TableName("interfaces_type")
public class InterfacesTypeEntity {
    @TableId
    private Integer id;

    /**
     * 接口类型
     **/
    private String ifType;

    /**
     * 接口描述信息
     **/
    private String ifDescribe;

}
