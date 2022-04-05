package com.xust.entity.form;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/5/11 15:36
 * @Version
 **/
@TableName(value = "software_list")
@Data
public class SoftwareForm {

    @TableId(type = IdType.AUTO)
    private long id;

    private String ip;

    private String softwareName;
}
