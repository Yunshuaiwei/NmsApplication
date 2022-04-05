package com.xust.entity.po;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author ysw
 */
@Data
public class GetSnmpPo {

	/**
	 * 属性名字
	 **/
	private String ismName;

	/**
	 * 设备号
	 **/
	private int modelId;

	/**
	 * IP地址
	 **/
	private String ism3IpAddr;
	/**
	 * 团体字
	 **/
	private String community = "public";

	/**
	 * 版本号
	 **/
	@Value(value = "${snmp.version}")
	private int ism3Version;

	/**
	 * 超时时间
	 **/
	private int timeOut = 10000;

	/**
	 * 属性OID（它有着比ismName更高的权限）
	 **/
	private String OID;
}
