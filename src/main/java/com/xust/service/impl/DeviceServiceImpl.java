package com.xust.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xust.common.exception.DefinitionException;
import com.xust.common.exception.HttpStatusCode;
import com.xust.common.utils.SnmpManager;
import com.xust.common.utils.SnmpUtils;
import com.xust.common.utils.Utils;
import com.xust.dao.*;
import com.xust.entity.InterfacesEntity;
import com.xust.entity.IpEntity;
import com.xust.entity.SystemEntity;
import com.xust.entity.form.DeviceForm;
import com.xust.entity.form.InterfacesAvgSpeed;
import com.xust.entity.form.SoftwareForm;
import com.xust.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/5/3 16:52
 * @Version
 **/
@Service
@Slf4j
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private SnmpManager snmpManager;

    @Autowired
    private SoftwareFormDao softwareFormDao;

    @Autowired
    private InterfacesDao interfacesDao;

    @Autowired
    private SnmpUtils snmpUtils;

    @Autowired
    private SystemDao systemDao;

    @Autowired
    private IpEntityDao ipEntityDao;

    private static String COMMUNITY = "public";

    @Autowired
    private InterfacesAvgSpeedDao interfacesAvgSpeedDao;

    @Autowired
    private Utils utils;


    @Override
    public DeviceForm getDevicesInfo(String ip) {
        log.info("getDevicesInfo()方法执行。。。");
        if (ip == null) {
            ip = "127.0.0.1";
        } else {
            if (!utils.matchIpAddress(ip)) {
                throw new DefinitionException(HttpStatusCode.IPADDRESS_ERROR);
            }
            //将ip地址存入数据库
            IpEntity ipEntity = ipEntityDao.selectOne(new QueryWrapper<IpEntity>().eq("ip", ip));
            if (ipEntity == null) {
                ipEntity = new IpEntity();
                ipEntity.setIp(ip);
                ipEntityDao.insert(ipEntity);
                getSpeedData(ip);
            }
        }
        DeviceForm entity = new DeviceForm();
        //获取CPU数据
        Integer cpu = snmpManager.getCpuUtilization(ip);
        //获取内存数据
        Integer memory = snmpManager.getMemoryUtilization(ip);
        //获取磁盘数据
        List<String> strings = snmpManager.collectDisk(ip);
        //获取系统运行时间
        String runTime = snmpManager.getSysRuntime(ip, "public");
        String[] res = runTime.split("days,");
        String day = (res.length > 1 ? res[0].trim() : "0");
        String hour;
        String minute;
        if (res.length > 1) {
            hour = res[1].trim().split(":")[0];
            minute = res[1].trim().split(":")[1];
        } else {
            hour = res[0].trim().split(":")[0];
            minute = res[0].trim().split(":")[1];
        }
        String[] diskTotal = new String[strings.size()];
        String[] diskUsed = new String[strings.size()];
        for (int i = 0; i < strings.size(); i++) {
            String s = strings.get(i);
            String[] split = s.split("-");
            diskTotal[i] = split[0];
            diskUsed[i] = split[1];
        }
        entity.setCpu(cpu == -1 ? 0 : cpu);
        entity.setMemory(memory == -1 ? 0 : memory);
        entity.setDiskTotal(diskTotal);
        entity.setDiskUsed(diskUsed);
        entity.setDay(day);
        entity.setHour(hour);
        entity.setMinute(minute);
        List<InterfacesAvgSpeed> entityList = interfacesAvgSpeedDao.selectLimt(ip);

        String[] xData = new String[entityList.size()];
        String[] inData = new String[entityList.size()];
        String[] outData = new String[entityList.size()];
        int j = 0;
        for (int i = entityList.size() - 1; i >= 0; i--) {
            xData[j] = entityList.get(i).getXData();
            inData[j] = entityList.get(i).getInData();
            outData[j++] = entityList.get(i).getOutData();
        }
        entity.setXData(xData);
        entity.setInData(inData);
        entity.setOutData(outData);
        return entity;
    }

    @Override
    public Map<String, Object> getSoftwareList(String pageNum, String pageSize, String ip) {
        QueryWrapper<SoftwareForm> softwareFormQueryWrapper = new QueryWrapper<>();
        softwareFormQueryWrapper.select("distinct ip");
        SoftwareForm softwareForm1 = softwareFormDao.selectOne(softwareFormQueryWrapper);
        softwareFormQueryWrapper.clear();
        softwareFormQueryWrapper.eq("ip", ip);
        HashMap<String, Object> res = new HashMap<>(3);
        Page<SoftwareForm> page = new Page<SoftwareForm>(Long.parseLong(pageNum), Long.parseLong(pageSize));
        if (softwareForm1 != null) {
            Page<SoftwareForm> softwareFormPage = softwareFormDao.selectPage(page, softwareFormQueryWrapper);
            List<SoftwareForm> records = softwareFormPage.getRecords();
            res.put("software", records);
            res.put("total", softwareFormPage.getTotal());
            res.put("pageNum", pageNum);
            return res;
        }
        List<String> softwareList = snmpManager.getSoftwareList(ip, COMMUNITY);
        for (String s : softwareList) {
            SoftwareForm form = new SoftwareForm();
            form.setIp(ip);
            form.setSoftwareName(s);
            QueryWrapper<SoftwareForm> wrapper = new QueryWrapper<>();
            wrapper.eq("ip", ip)
                    .eq("software_name", s);
            SoftwareForm softwareForm = softwareFormDao.selectOne(wrapper);
            if (softwareForm == null) {
                softwareFormDao.insert(form);
            }
        }
        Page<SoftwareForm> softwareFormPage = softwareFormDao.selectPage(page, softwareFormQueryWrapper);
        List<SoftwareForm> records = softwareFormPage.getRecords();
        res.put("software", records);
        res.put("total", softwareFormPage.getTotal());
        res.put("pageNum", pageNum);
        return res;
    }

    /**
     * @return com.xust.entity.form.DeviceForm
     * @Param
     * @Date 11:00 2021/5/12
     * @Description: 获取系统运行时间和接口速率
     **/
    @Override
    public DeviceForm getDeviceInfo(String ip) {
        log.info("getDeviceInfo()方法执行，获取系统运行时间和接口速率。。");
        //默认请求本机数据
        if (ip == null || "".equals(ip)) {
            ip = "127.0.0.1";
        } else {
            if (!utils.matchIpAddress(ip)) {
                throw new DefinitionException(HttpStatusCode.IPADDRESS_ERROR);
            }
            //将ip地址存入数据库
            IpEntity ipEntity = ipEntityDao.selectOne(new QueryWrapper<IpEntity>().eq("ip", ip));
            if (ipEntity == null) {
                ipEntity = new IpEntity();
                ipEntity.setIp(ip);
                ipEntityDao.insert(ipEntity);
                getSpeedData(ip);
            }
        }
        DeviceForm entity = new DeviceForm();
        //获取系统运行时间
        String runTime = snmpManager.getSysRuntime(ip, "public");
        String[] res = runTime.split("days,");
        String day = (res.length > 1 ? res[0].trim() : "0");
        String hour;
        String minute;
        if (res.length > 1) {
            hour = res[1].trim().split(":")[0];
            minute = res[1].trim().split(":")[1];
        } else {
            hour = res[0].trim().split(":")[0];
            minute = res[0].trim().split(":")[1];
        }
        entity.setDay(day);
        entity.setHour(hour);
        entity.setMinute(minute);
        List<InterfacesAvgSpeed> entityList = interfacesAvgSpeedDao.selectLimt(ip);

        String[] xData = new String[entityList.size()];
        String[] inData = new String[entityList.size()];
        String[] outData = new String[entityList.size()];
        int j = 0;
        for (int i = entityList.size() - 1; i >= 0; i--) {
            xData[j] = entityList.get(i).getXData();
            inData[j] = entityList.get(i).getInData();
            outData[j++] = entityList.get(i).getOutData();
        }
        entity.setXData(xData);
        entity.setInData(inData);
        entity.setOutData(outData);
        return entity;
    }

    @Override
    public Map[] getIpList() {
        List<IpEntity> list = ipEntityDao.selectList(null);
        Map[] strings = new HashMap[list.size()];
        for (int i = 0; i < strings.length; i++) {
            HashMap<String, String> res = new HashMap<>();
            res.put("value", list.get(i).getIp());
            strings[i] = res;
        }
        return strings;
    }

    @Override
    public Map<String, Object> getSystemInfo(String ip, long pageNum, long pageSize) {
        HashMap<String, Object> res = new HashMap<>();
        if (ip == null || "".equals(ip)) {
            systemInfo("127.0.0.1");
            Page<SystemEntity> page = new Page<>(pageNum, pageSize);
            Page<SystemEntity> selectPage = systemDao.selectPage(page, null);
            List<SystemEntity> records = selectPage.getRecords();
            res.put("system", records);
            res.put("total", selectPage.getTotal());
            return res;
        } else {
            if (!utils.matchIpAddress(ip)) {
                throw new DefinitionException(HttpStatusCode.IPADDRESS_ERROR);
            }
            //将ip地址存入数据库
            IpEntity ipEntity = ipEntityDao.selectOne(new QueryWrapper<IpEntity>().eq("ip", ip));
            if (ipEntity == null) {
                ipEntity = new IpEntity();
                ipEntity.setIp(ip);
                ipEntityDao.insert(ipEntity);
                systemInfo(ip);
            }
        }
        systemInfo(ip);
        Page<SystemEntity> page = new Page<>(pageNum, pageSize);
        Page<SystemEntity> selectPage = systemDao.selectPage(page, new QueryWrapper<SystemEntity>().eq("ip", ip));
        List<SystemEntity> records = selectPage.getRecords();
        res.put("system", records);
        res.put("total", selectPage.getTotal());
        return res;
    }

    /**
     * @return void
     * @Param
     * @Date 10:17 2021/5/14
     * @Description: 读取系统数据并存入数据库
     **/
    private void systemInfo(String ip) {
        String[] walk = snmpManager.snmpWalk(ip, COMMUNITY, "1.3.6.1.2.1.1");
        SystemEntity entity = new SystemEntity();
        entity.setIp(ip);
        entity.setSysDescr(walk[0]);
        entity.setSysObjectId(walk[1]);
        entity.setSysUpTime(snmpManager.getSysRuntime(ip, COMMUNITY));
        entity.setSysContact(walk[3]);
        entity.setSysName(walk[4]);
        entity.setSysLocation(walk[5]);
        entity.setSysServices(walk[6]);
        SystemEntity ip1 = systemDao.selectOne(new QueryWrapper<SystemEntity>().eq("ip", ip));
        if (ip1 == null) {
            systemDao.insert(entity);
        } else {
            entity.setId(ip1.getId());
            systemDao.updateById(entity);
        }
    }

    /**
     * @return void
     * @Param
     * @Date 10:27 2021/5/14
     * @Description: 00:00:00执行一次
     **/
    @Scheduled(cron = "0 0 0 * * 1-7")
    public void updateSystemInfo() {
        log.info("[定时任务]——>定时更新系统信息以及IP表，updateSystemInfo()");
        List<IpEntity> ipList = ipEntityDao.selectList(null);
        for (IpEntity ipEntity : ipList) {
            String ip = ipEntity.getIp();
            if (!snmpUtils.isEthernetConnection(ip)) {
                ipEntityDao.delete(new QueryWrapper<IpEntity>().eq("ip", ip));
                systemDao.delete(new QueryWrapper<SystemEntity>().eq("ip", ip));
                interfacesAvgSpeedDao.delete(new QueryWrapper<InterfacesAvgSpeed>().eq("ip", ip));
                interfacesDao.delete(new QueryWrapper<InterfacesEntity>().eq("ip", ip));
            }
        }
        List<IpEntity> list = ipEntityDao.selectList(null);
        if (list == null || list.size() == 0) {
            systemInfo("127.0.0.1");
            return;
        }
        for (IpEntity entity : list) {
            String ip = entity.getIp();
            systemInfo(ip);
        }
    }

    /**
     * @return void
     * @Param
     * @Date 11:38 2021/5/11
     * @Description: 从系统读取数据存入数据库
     **/
    private void getSpeedData(String ip) {
        log.info("getSpeedData()，IP地址为：{}", ip);
        long time = Long.parseLong(snmpManager.snmpWalk(ip, COMMUNITY, "1.3.6.1.2.1.1.3")[0]) / 100;
        time = TimeUnit.SECONDS.toMinutes(time);
        String[] bytes = snmpManager.snmpWalk(ip, COMMUNITY, "1.3.6.1.2.1.2.2.1.10");
        long totalByte = 0;
        for (String s : bytes) {
            totalByte += Long.parseLong(s);
        }
        long inData = totalByte / 1024 / time;
        //发出的字节
        bytes = snmpManager.snmpWalk(ip, COMMUNITY, "1.3.6.1.2.1.2.2.1.16");
        totalByte = 0;
        for (String s : bytes) {
            totalByte += Long.parseLong(s);
        }
        long outData = totalByte / 1024 / time;
        String s = new Time(System.currentTimeMillis()).toString();
        InterfacesAvgSpeed entity = new InterfacesAvgSpeed();
        entity.setIp(ip);
        entity.setInData(inData + "");
        entity.setOutData(outData + "");
        entity.setXData(s);
        QueryWrapper<InterfacesAvgSpeed> wrapper = new QueryWrapper<>();
        wrapper.eq("ip", ip)
                .eq("x_data", s);
        InterfacesAvgSpeed avgSpeed = interfacesAvgSpeedDao.selectOne(wrapper);
        if (avgSpeed == null) {
            interfacesAvgSpeedDao.insert(entity);
        } else {
            interfacesAvgSpeedDao.updateById(entity);
        }
        log.info("接口的收发字节速率——>{}", entity.toString());
    }

    /**
     * @return void
     * @Param
     * @Date 11:00 2021/5/11
     * @Description: 10分钟运行一次
     **/
    @Scheduled(cron = "0 0/10 * * * 1-7")
    private void getInterfacesSpeed() {
        log.info("[定时任务]——>定时更新接口收发数据包的速率，getInterfacesSpeed()");
        List<IpEntity> list = ipEntityDao.selectList(null);
        if (list == null || list.size() == 0) {
            getSpeedData("127.0.0.1");
            return;
        }
        for (IpEntity interfacesAvgSpeed : list) {
            String ip = interfacesAvgSpeed.getIp();
            getSpeedData(ip);
        }
    }
}
