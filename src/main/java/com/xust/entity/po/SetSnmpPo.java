package com.xust.entity.po;

import lombok.Data;

/**
 * @author ysw
 */
@Data
public class SetSnmpPo {

	/**
	 * 属性名字
	 **/
	private String ismName;

	/**
	 * IP地址
	 **/
	private String ism3IpAddr;

	/**
	 * 设备号
	 **/
	private int modelID;

	/**
	 * 团体字
	 **/
	private String community = "public";

	/**
	 * 写团体字
	 **/
	private String writeCommunity = "public";

	/**
	 * 属性的值
	 **/
	private String value;

	/**
	 * 属性OID（它有着比ismName更高的权限）
	 **/
	private String OID;
	
}
