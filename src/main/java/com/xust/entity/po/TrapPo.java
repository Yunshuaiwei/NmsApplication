package com.xust.entity.po;

import com.xust.dao.MibDao;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ysw
 */
@Data
@Slf4j
public class TrapPo {

	/**
	 * 设备IP
	 **/
	private String addressIp;

	/**
	 * trap产生时间
	 **/
	private String timeReceived;

	/**
	 * trap内容
	 **/
	private String message;

	/**
	 * 设备类型OID
	 **/
	private String enterprise;

	private int genericType;

	private int specificType;

	/**
	 * 具体trap的OID
	 **/
	private String OID;

	private String trapName;
	
	
	public String getTrapName() {
		//调用MibDao()方法获取trap的名字
		OID = "."+this.getEnterprise()+".6."+this.getSpecificType();
		MibDao dao = new MibDao();
		try {
			log.info("==========OID=========={}",OID);
			trapName = MibDao.getIsmName(OID);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return trapName;
	}
}
