package com.xust.dao;

import com.adventnet.snmp.beans.DataException;
import com.adventnet.snmp.beans.SnmpTarget;
import com.xust.entity.po.GetSnmpPo;
import com.xust.entity.po.SetSnmpPo;
import com.xust.entity.po.SnmpInterf;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author ysw
 */
@Slf4j
@Component
public class SnmpDao implements SnmpInterf {

	/**
	 * 取单个属性值方法
	 **/
	@Override
	public String getSnmpRequest(GetSnmpPo po) throws IOException {
		log.info("获取单个属性值！！！");
		String message = null;
		// 通过属性名字获取OID
		String OID = null;
		OID = po.getOID();
		if (OID == null) {
			try {
				OID = MibDao.getOID(po.getIsmName());
				log.info("========OID========{}",OID);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Address targetAddress = GenericAddress.parse("udp:"
				+ po.getIsm3IpAddr() + "/161");
		// 解析设备IP
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(po.getCommunity()));
		target.setAddress(targetAddress);
		target.setTimeout(po.getTimeOut());
		target.setVersion(po.getIsm3Version());

		// creating PDU
		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(OID + "." + po.getModelId())));
		pdu.setType(PDU.GET);
		TransportMapping transport = new DefaultUdpTransportMapping();
		transport.listen();
		Snmp snmp = new Snmp(transport);
		ResponseEvent response = snmp.send(pdu, target);
		if (response != null) {
			PDU respPdu = response.getResponse();
			for (int i = 0; i < respPdu.size(); i++) {
				VariableBinding varBinding = respPdu.get(i);
				Variable var = varBinding.getVariable();
				// 字符转换问题
				message = var.toString();
			}
		}
		return message;
	}

	/**
	 * 设置某个属性值方法
	 **/
	@Override
	public boolean setSnmpRequest(SetSnmpPo po) throws IOException {
		boolean flag = false;
		// 通过属性名字获取OID
		String OID = null;
		OID = po.getOID();
		if (OID == null) {
			try {
				OID = MibDao.getOID(po.getIsmName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		SnmpTarget target = new SnmpTarget();
		// 设置设备属性
		// IP地址
		target.setTargetHost(po.getIsm3IpAddr());
		// 端口号161
		target.setTargetPort(161);
		target.setObjectID(OID + "." + po.getModelID());
		target.setCommunity(po.getCommunity());
		target.setWriteCommunity(po.getWriteCommunity());
		// 进行编译的MIB库
		target.setMibOperations(MibDao.mibOps);

		if (po.getWriteCommunity().equals("") || po.getValue().equals("")) {
			log.error("No WriteCommunity Or Set Value !,Error!");
		} else {
			try {
				String result = target.snmpSet(po.getValue());
				if (result == null) {
					System.err.println("Failed: " + target.getErrorString());
					if (target.getErrorString().length() > 80) {
						flag = true;
						if (target.getErrorIndex() == 1) {
							// 端口好有问题
							flag = false;
						}
					}
					// 连接失败
				} else {
					System.out.println("Response: " + result);
					log.info("属性修改成功!!!");
				}

			} catch (DataException e) {
				// TODO Auto-generated catch block
				log.warn("严重警告：此项属性不能修改！");
				e.printStackTrace();
				// 属性不可修改
			}
		}
		return flag;
	}
}
