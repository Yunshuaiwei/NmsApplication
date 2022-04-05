package com.xust.entity.form;

import lombok.Data;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/5/7 21:53
 * @Version
 **/
@Data
public class DeviceForm {

    private Integer cpu;

    private Integer memory;

    private String[] diskTotal;

    private String[] diskUsed;

    private String day;

    private String hour;

    private String minute;


    private String [] xData;

    private String [] inData;

    private String [] outData;

}
