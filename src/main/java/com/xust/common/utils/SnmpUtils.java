package com.xust.common.utils;

import com.xust.common.exception.DefinitionException;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/4/21 15:43
 * @Version
 **/
@Component
@Slf4j
public class SnmpUtils {

    public final int DEFAULT_VERSION = SnmpConstants.version2c;
    public final String DEFAULT_PROTOCOL = "udp";
    public final int DEFAULT_PORT = 161;
    public final long DEFAULT_TIMEOUT = 3 * 1000L;
    public final int DEFAULT_RETRY = 3;

    private Snmp snmp = null;

    private Address targetAddress = null;

    public void initComm() throws IOException {
        // 设置Agent方的IP和端口
        targetAddress = GenericAddress.parse("udp:127.0.0.1/161");
        TransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        transport.listen();
    }

    /**
     * 封装target
     **/
    public CommunityTarget createDefault(String ip, String community) {
        Address address = GenericAddress.parse(DEFAULT_PROTOCOL + ":" + ip
                + "/" + DEFAULT_PORT);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(address);
        target.setVersion(DEFAULT_VERSION);
        target.setTimeout(DEFAULT_TIMEOUT);
        target.setRetries(DEFAULT_RETRY);
        return target;
    }

    /**
     * 遍历OID树
     **/
    public List<String> snmpWalk(String ip, String community, String targetOid) {
        List<String> list = new ArrayList<>();
        CommunityTarget target = createDefault(ip, community);
        TransportMapping transport = null;
        Snmp snmp = null;
        try {
            transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            transport.listen();

            PDU pdu = new PDU();
            OID targetOID = new OID(targetOid);
            pdu.add(new VariableBinding(targetOID));
            boolean finished = false;
            while (!finished) {
                VariableBinding vb = null;
                ResponseEvent respEvent = snmp.getNext(pdu, target);
                PDU response = respEvent.getResponse();
                if (null == response) {
                    finished = true;
                    break;
                } else {
                    vb = response.get(0);
                }
                // check finish
                finished = checkWalkFinished(targetOID, pdu, vb);
                if (!finished) {
                    String s = vb.getVariable().toString();
                    if (getChinese(s) == null) {
                        list.add(s);
                    } else {
                        //转为中文
                        list.add(getChinese(s));
                    }
                    pdu.setRequestID(new Integer32(0));
                    pdu.set(0, vb);
                } else {
                    snmp.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (snmp != null) {
                try {
                    snmp.close();
                } catch (IOException ex1) {
                    snmp = null;
                }
            }
        }
        return list;
    }

    /**
     * 处理中文
     **/
    public String getChinese(String octetString) {
        try {
            String[] temps = octetString.split(":");
            byte[] bs = new byte[temps.length];
            for (int i = 0; i < temps.length; i++) {
                bs[i] = (byte) Integer.parseInt(temps[i], 16);
            }
            //GB2312  ISO-8859-1 ASCII
            return new String(bs, "GB2312");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查是否遍历结束
     **/
    public boolean checkWalkFinished(OID targetOid, PDU pdu, VariableBinding vb) {
        boolean finished = false;
        if (pdu.getErrorStatus() != 0) {
            log.info(pdu.getErrorStatusText());
            finished = true;
        } else if (vb.getOid() == null) {
            log.info("[true] vb.getOid() == null");
            finished = true;
        } else if (vb.getOid().size() < targetOid.size()) {
            log.info("[true] vb.getOid().size() < targetOID.size()");
            finished = true;
        } else if (targetOid.leftMostCompare(targetOid.size(), vb.getOid()) != 0) {
            log.info("[true] targetOID.leftMostCompare() != 0");
            finished = true;
        } else if (Null.isExceptionSyntax(vb.getVariable().getSyntax())) {
            log.info("[true] Null.isExceptionSyntax(vb.getVariable().getSyntax())");
            finished = true;
        } else if (vb.getOid().compareTo(targetOid) <= 0) {
            log.info("[true] Variable received is not lexicographic successor of requested " + "one:");
            log.info(vb.toString() + " <= " + targetOid);
            finished = true;
        }
        return finished;

    }

    public ResponseEvent sendPDU(PDU pdu) throws IOException {
        // 设置 target
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("private"));
        target.setAddress(targetAddress);
        // 通信不成功时的重试次数
        target.setRetries(2);
        // 超时时间
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version1);
        // 向Agent发送PDU，并返回Response
        return snmp.send(pdu, target);
    }

    public void setPDU() throws IOException {
        // set PDU
        //WWW-CB67457E260--sysName   SNMPTEST
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(new int[]{1, 3, 6, 1, 2, 1, 1, 5, 0}), new OctetString("WWW-CB67457E260")));
        pdu.setType(PDU.SET);
        sendPDU(pdu);
    }

    public void getPDU() throws IOException {
        // get PDU
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(new int[]{1, 3, 6, 1, 2, 1, 1, 5, 0})));
        pdu.setType(PDU.GET);
        readResponse(sendPDU(pdu));
    }

    /**
     * @return void
     * @Param
     * @Date 13:41 2021/5/8
     * @Description: 解析响应的数据
     **/
    public void readResponse(ResponseEvent respEvnt) {
        // 解析Response
        if (respEvnt != null && respEvnt.getResponse() != null) {
            Vector<VariableBinding> recVBs = (Vector<VariableBinding>) respEvnt.getResponse().getVariableBindings();
            for (int i = 0; i < recVBs.size(); i++) {
                VariableBinding recVB = recVBs.elementAt(i);
                System.out.println(recVB.getOid() + " : " + recVB.getVariable());

            }
        }
    }

    /**
     * 测网络通不通 类似 ping ip
     *
     * @param ip
     * @return
     * @throws IOException
     */
    public boolean isEthernetConnection(String ip) {
        log.info("测试网络连通性，IP地址为：{}",ip);
        InetAddress ad = null;
        try {
            ad = InetAddress.getByName(ip);
            // 测试是否可以达到该地址 2秒超时
            return ad.isReachable(3000);
        } catch (IOException e) {
            throw new DefinitionException(e.getMessage());
        }
    }
}
