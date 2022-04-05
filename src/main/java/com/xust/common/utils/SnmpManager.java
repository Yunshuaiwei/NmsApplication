package com.xust.common.utils;

import com.xust.common.exception.DefinitionException;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MessageProcessingModel;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.PDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import snmp.SNMPObject;
import snmp.SNMPSequence;
import snmp.SNMPVarBindList;
import snmp.SNMPv1CommunicationInterface;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * SNMP管理类
 *
 * @author ysw
 */
@Slf4j
@Component
public class SnmpManager {
    @Autowired
    private SnmpUtils snmpUtils;

    /**
     * SNMP版本, 0表示版本1
     **/
    @Value(value = "${snmp.version}")
    private int version;

    /**
     * 监控时使用的协议
     **/
    private final String protocol = "udp";

    /**
     * 监控时使用的端口
     **/
    private String port = "161";

    /**
     * 获取SNMP节点值
     *
     * @param ipAddress 目标IP地址
     * @param community 公同体
     * @param oid       对象ID
     * @return String 监控结果代号
     * @throws DefinitionException
     */
    @SuppressWarnings("unchecked")
    public String snmpGet(String ipAddress, String community, String oid) throws DefinitionException {
        // 监控结果状态
        String resultStat = null;
        StringBuffer address = new StringBuffer();
        address.append(protocol);
        address.append(":");
        address.append(ipAddress);
        address.append("/");
        address.append(port);
        Address targetAddress = GenericAddress.parse(address.toString());
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oid)));
        pdu.setType(PDU.GET);

        // 创建共同体对象CommunityTarget
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(targetAddress);
        target.setVersion(SnmpConstants.version1);
        target.setTimeout(2000);
        target.setRetries(1);

        DefaultUdpTransportMapping udpTransportMap = null;
        Snmp snmp = null;
        try {
            // 发送同步消息
            udpTransportMap = new DefaultUdpTransportMapping();
            udpTransportMap.listen();
            snmp = new Snmp(udpTransportMap);
            ResponseEvent response = snmp.send(pdu, target);
            PDU resposePdu = response.getResponse();

            if (resposePdu == null) {
                log.info(ipAddress + ": Request timed out.");
            } else {
                Object obj = resposePdu.getVariableBindings().firstElement();
                VariableBinding variable = (VariableBinding) obj;
                resultStat = variable.getVariable().toString();
            }
        } catch (Exception e) {
            throw new DefinitionException("获取SNMP节点状态时发生错误!", e);
        } finally {
            if (snmp != null) {
                try {
                    snmp.close();
                } catch (IOException e) {
                    snmp = null;
                }
            }
            if (udpTransportMap != null) {
                try {
                    udpTransportMap.close();
                } catch (IOException e) {
                    udpTransportMap = null;
                }
            }
        }
        if (log.isInfoEnabled()) {
            log.info("IP:" + ipAddress + " resultStat:" + resultStat);
        }
        return resultStat;
    }


    /**
     * 走访SNMP节点
     *
     * @param ipAddress 目标IP地址
     * @param community 共同体
     * @param oid       节点起始对象标志符
     * @return String[] 走方结果
     * @throws DefinitionException
     */
    public String[] snmpWalk(String ipAddress, String community, String oid) throws DefinitionException {
        // oid走访结果数组
        String[] returnValueString;
        SNMPv1CommunicationInterface comInterface = null;
        try {
            Snmp snmp = new Snmp();
            InetAddress hostAddress = InetAddress.getByName(ipAddress);
            comInterface = new SNMPv1CommunicationInterface(
                    version, hostAddress, community);
            comInterface.setSocketTimeout(2000);
            // 返回所有以oid开始的管理信息库变量值
            SNMPVarBindList tableVars = comInterface.retrieveMIBTable(oid);
            returnValueString = new String[tableVars.size()];

            // 循环处理所有以oid开始的节点的返回值
            for (int i = 0; i < tableVars.size(); i++) {
                // 获取SNMP序列对象, 即(OID,value)对
                SNMPSequence pair = (SNMPSequence) tableVars.getSNMPObjectAt(i);
                // 获取某个节点的返回值
                SNMPObject snmpValue = pair.getSNMPObjectAt(1);
                // 获取SNMP值类型名
                String typeString = snmpValue.getClass().getName();
                // 设置返回值
                if (typeString.equals("snmp.SNMPOctetString")) {
                    String snmpString = snmpValue.toString();
                    int nullLocation = snmpString.indexOf('\0');
                    if (nullLocation >= 0) {
                        snmpString = snmpString.substring(0, nullLocation);
                    }
                    returnValueString[i] = snmpString;
                } else {
                    returnValueString[i] = snmpValue.toString();
                }
            }
        } catch (SocketTimeoutException ste) {
            if (log.isErrorEnabled()) {
                log.error("走访IP为" + ipAddress + ", OID为" + oid + " 时超时!");
            }
            returnValueString = null;
            throw new DefinitionException("访问节点超时");
        } catch (Exception e) {
            throw new DefinitionException("SNMP走访节点时发生错误!", e);
        } finally {
            if (comInterface != null) {
                try {
                    comInterface.closeConnection();
                } catch (SocketException e) {
                    comInterface = null;
                }
            }
        }
        return returnValueString;
    }

    /**
     * 获取指定OID对应的table值
     *
     * @param oid
     * @param ip
     * @return
     */
    private List<String> walkByTable(String oid, String ip) {
        Snmp snmp = null;
        CommunityTarget target;
        List<String> result = new ArrayList<String>();
        try {
            DefaultUdpTransportMapping dm = new DefaultUdpTransportMapping();
            snmp = new Snmp(dm);
            snmp.listen();
            target = new CommunityTarget();
            target.setCommunity(new OctetString("public"));
            target.setVersion(version);
            target.setAddress(new UdpAddress(ip + "/" + "161"));
            target.setTimeout(1000);
            target.setRetries(1);

            TableUtils utils = new TableUtils(snmp, new DefaultPDUFactory(PDU.GETBULK));
            OID[] columns = new OID[1];
            columns[0] = new VariableBinding(new OID(oid)).getOid();
            List<TableEvent> list = (List<TableEvent>) utils.getTable(target, columns, null, null);
            for (TableEvent e : list) {
                VariableBinding[] vb = e.getColumns();
                if (null == vb) {
                    continue;
                }
                result.add(vb[0].getVariable().toString());
            }
            snmp.close();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new DefinitionException("获取信息失败！", e);
        } finally {
            try {
                if (snmp != null) {
                    snmp.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return result;
    }

    /**
     * 获取CPU使用率
     *
     * @param ip
     * @return 正常返回CPU当前使用率，否则返回-1
     */
    public Integer getCpuUtilization(String ip) {
        log.info("获取系统CPU数据，IP地址为：{}",ip);
        List<String> result = walkByTable(
                ".1.3.6.1.2.1.25.3.3.1.2", ip);
        if (result.size() == 0) {
            return -1;
        }
        double sum = 0;
        for (String s : result) {
            sum += Double.parseDouble(s);
        }
        return (int) (sum / result.size());
    }

    /**
     * 获取Memory占用率
     *
     * @param ip
     * @return 正常返回当前内存使用率，否则返回-1
     * @throws IOException
     */
    public Integer getMemoryUtilization(String ip) {
        log.info("获取系统内存数据，IP地址为：{}",ip);
        // 使用
        try {
            List<String> usedresultList = walkByTable(".1.3.6.1.2.1.25.2.3.1.6", ip);
            // 总
            List<String> allresultList = walkByTable(".1.3.6.1.2.1.25.2.3.1.5", ip);
            if (usedresultList.size() > 0 && allresultList.size() > 0) {
                double used = 0;
                String usedStr = usedresultList.get(usedresultList.size() - 1);
                used = Double.parseDouble(usedStr);
                double all = 0;
                String allStr = allresultList.get(allresultList.size() - 1);
                all = Double.parseDouble(allStr);
                return (int) ((used / all) * 100);
            }
        } catch (Exception e) {
            log.error("获取Memory占用率:" + e.getMessage());
        }
        return -1;
    }

    /**
     * 获取磁盘相关信息
     **/
    public List<String> collectDisk(String ip) {
        log.info("获取系统磁盘数据，IP地址为：{}",ip);
        TransportMapping transport = null;
        ArrayList<String> res = new ArrayList<>();
        Snmp snmp = null;
        CommunityTarget target;
        String DISK_OID = "1.3.6.1.2.1.25.2.1.4";
        //type 存储单元类型
        String[] oids = {"1.3.6.1.2.1.25.2.3.1.2",
                //descr
                "1.3.6.1.2.1.25.2.3.1.3",
                //unit 存储单元大小
                "1.3.6.1.2.1.25.2.3.1.4",
                //size 总存储单元数
                "1.3.6.1.2.1.25.2.3.1.5",
                //used 使用存储单元数;
                "1.3.6.1.2.1.25.2.3.1.6"};
        try {
            transport = new DefaultUdpTransportMapping();
            //创建snmp
            snmp = new Snmp(transport);
            snmp.listen();//监听消息
            target = new CommunityTarget();
            target.setCommunity(new OctetString("public"));
            target.setRetries(2);
            target.setAddress(GenericAddress.parse("udp:" + ip + "/161"));
            target.setTimeout(8000);
            target.setVersion(SnmpConstants.version2c);
            TableUtils tableUtils = new TableUtils(snmp, new PDUFactory() {
                @Override
                public PDU createPDU(Target arg0) {
                    PDU request = new PDU();
                    request.setType(PDU.GET);
                    return request;
                }

                @Override
                public PDU createPDU(MessageProcessingModel messageProcessingModel) {
                    PDU request = new PDU();
                    request.setType(PDU.GET);
                    return request;
                }
            });
            OID[] columns = new OID[oids.length];
            for (int i = 0; i < oids.length; i++) {
                columns[i] = new OID(oids[i]);
            }
            @SuppressWarnings("unchecked")
            List<TableEvent> list = tableUtils.getTable(target, columns, null, null);
            if (list.size() == 1 && list.get(0).getColumns() == null) {
                System.out.println(" null");
            } else {
                for (TableEvent event : list) {
                    VariableBinding[] values = event.getColumns();
                    if (values == null || !DISK_OID.equals(values[0].getVariable().toString())) {
                        continue;
                    }
                    //unit 存储单元大小
                    int unit = Integer.parseInt(values[2].getVariable().toString());
                    //size 总存储单元数
                    int totalSize = Integer.parseInt(values[3].getVariable().toString());
                    //used  使用存储单元数
                    int usedSize = Integer.parseInt(values[4].getVariable().toString());

                    //磁盘大小（G）
                    String diskSize = ((long) totalSize * unit / (1024 * 1024 * 1024)) + "";
                    //磁盘使用率（%）
                    String diskUsed = ((long) usedSize * 100 / totalSize) + "";
                    res.add(diskSize + "-" + diskUsed);
                    log.info("----磁盘:{}----磁盘大小为:{}G----磁盘使用率为:{}%", values[1].getVariable().toString(), (long) totalSize * unit / (1024 * 1024 * 1024) + "", (long) usedSize * 100 / totalSize + "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (transport != null) {
                    transport.close();
                }
                if (snmp != null) {
                    snmp.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    /**
     * @return java.lang.String[]
     * @Param
     * @Date 16:19 2021/5/9
     * @Description: 获取MAC地址
     **/
    public String [] getMacAddress(String ip,String community){
        log.info("获取系统所有接口MAC地址，IP地址为：{}",ip);
        List<String> list = new ArrayList<>();
        CommunityTarget target = snmpUtils.createDefault(ip, community);
        TransportMapping transport = null;
        Snmp snmp = null;
        try {
            transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            transport.listen();
            PDU pdu = new PDU();
            OID targetOID = new OID("1.3.6.1.2.1.2.2.1.6");
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
                finished = snmpUtils.checkWalkFinished(targetOID, pdu, vb);
                if (!finished) {
                    list.add(vb.getVariable().toString());
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
        return list.toArray(new String[0]);
    }


    /**
     * @return java.util.List<java.lang.String>
     * @Param
     * @Date 16:19 2021/5/9
     * @Description: 获取软件列表
     **/
    public List<String> getSoftwareList(String ip,String community){
        log.info("获取系统软件列表，IP地址为：{}",ip);
        return snmpUtils.snmpWalk(ip, community, "1.3.6.1.2.1.25.6.3.1.2");
    }

    /**
     * @return java.lang.String
     * @Param
     * @Date 16:27 2021/5/9
     * @Description: 获取系统运行时间
     **/
    public String getSysRuntime(String ip,String community){
        log.info("获取系统运行时间，IP地址为：{}",ip);
        return snmpUtils.snmpWalk(ip, community, "1.3.6.1.2.1.1.3").get(0);
    }
}

