package com.xust.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xust.common.exception.DefinitionException;
import com.xust.common.exception.HttpStatusCode;
import com.xust.common.utils.SnmpManager;
import com.xust.common.utils.Utils;
import com.xust.dao.InterfacesDao;
import com.xust.dao.InterfacesTypeDao;
import com.xust.dao.IpEntityDao;
import com.xust.entity.InterfacesEntity;
import com.xust.entity.InterfacesTypeEntity;
import com.xust.entity.IpEntity;
import com.xust.service.InterfacesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/5/6 10:35
 * @Version
 **/
@Service
@Slf4j
public class InterfacesServiceImpl implements InterfacesService {
    @Autowired
    private SnmpManager snmpManager;

    @Autowired
    private InterfacesDao interfacesDao;

    @Autowired
    private InterfacesTypeDao interfacesTypeDao;

    @Autowired
    private IpEntityDao ipEntityDao;

    @Autowired
    private Utils utils;

    private static String COMMUNITY = "public";

    @Override
    public Map<String, Object> getInterfacesInfo(String ip, long pageNum, long pageSize) {
        log.info("getInterfacesInfo()接口,IP地址为 {}", ip);
        Page<InterfacesEntity> page = new Page<>(pageNum, pageSize);
        Map<String, Object> result = new HashMap<>();
        if ("".equals(ip)||ip==null) {
            Page<InterfacesEntity> interfacesEntityPage = interfacesDao.selectPage(page, null);
            List<InterfacesEntity> list = interfacesEntityPage.getRecords();
            result.put("interfaces", list);
            result.put("total", interfacesEntityPage.getTotal());
            result.put("pageNum", pageNum);
            return result;
        }
        if (!utils.matchIpAddress(ip)){
            throw new DefinitionException(HttpStatusCode.IPADDRESS_ERROR);
        }
        //将ip地址存入数据库
        IpEntity ipEntity = ipEntityDao.selectOne(new QueryWrapper<IpEntity>().eq("ip", ip));
        if (ipEntity==null){
            ipEntity=new IpEntity();
            ipEntity.setIp(ip);
            ipEntityDao.insert(ipEntity);
            getInterfacesData(ip);
        }
        QueryWrapper<InterfacesEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("ip",ip);
        Page<InterfacesEntity> interfacesEntityPage = interfacesDao.selectPage(page, wrapper);
        List<InterfacesEntity> list = interfacesEntityPage.getRecords();
        result.put("interfaces", list);
        result.put("total", interfacesEntityPage.getTotal());
        result.put("pageNum", pageNum);
        return result;
    }


    /**
     * 更新数据库中的接口数据
     **/
    public void getInterfacesData(String ip) {
        log.info("getInterfacesData() 更新数据库中的接口数据");
        String interfaces = "1.3.6.1.2.1.2.2.1.";
        List<String> list = new ArrayList<>();
        //1-8分别表示：对应各接口的索引、接口描述信息、接口类型、最大协议数据单元大小、接口当前速率、接口的MAC地址、接口的管理状态、接口的操作状态
        for (int i = 1; i < 9; i++) {
            list.add(interfaces + i);
        }
        //接口收到的总字节数
        list.add(interfaces + "10");
        //接口发出的总字节数
        list.add(interfaces + "16");
        List<List> res = new ArrayList<>();
        for (String s : list) {
            res.add(Arrays.asList(snmpManager.snmpWalk(ip, COMMUNITY, s)));
        }
        String[] macAddress = snmpManager.getMacAddress(ip, COMMUNITY);
        for (int i = 0; i < res.get(0).size(); i++) {
            InterfacesEntity interfacesEntity = new InterfacesEntity();
            for (int j = 0; j < res.size(); j++) {
                String o = (String) res.get(j).get(i);
                switch (j) {
                    case 0:
                        interfacesEntity.setIfIndex(Long.parseLong(o));
                        break;
                    case 1:
                        interfacesEntity.setIfDescr(o);
                        break;
                    case 2:
                        interfacesEntity.setIfType(o);
                        break;
                    case 3:
                        interfacesEntity.setIfMtu(Long.parseLong(o));
                        break;
                    case 4:
                        interfacesEntity.setIfSpeed(Long.parseLong(o));
                        break;
                    case 5:
                        interfacesEntity.setIfPhysAddress(macAddress[i]);
                        break;
                    case 6:
                        interfacesEntity.setIfAdminStatus(Integer.parseInt(o));
                        break;
                    case 7:
                        interfacesEntity.setIfOperStatus(Integer.parseInt(o));
                        break;
                    case 8:
                        interfacesEntity.setIfInOctets(Long.parseLong(o));
                        break;
                    case 9:
                        interfacesEntity.setIfOutOctets(Long.parseLong(o));
                        break;
                    default:
                }
            }
            interfacesEntity.setIp(ip);
            Integer ifOperStatus = interfacesEntity.getIfOperStatus();
            Integer ifAdminStatus = interfacesEntity.getIfAdminStatus();
            if (ifOperStatus == 1 & ifAdminStatus == 1) {
                interfacesEntity.setIfInterfaceStatus("正常");
            } else if (ifOperStatus == 2 & ifAdminStatus == 1) {
                interfacesEntity.setIfInterfaceStatus("故障");
            } else if (ifOperStatus == 2 & ifAdminStatus == 2) {
                interfacesEntity.setIfInterfaceStatus("停机");
            } else if (ifOperStatus == 3 & ifAdminStatus == 3) {
                interfacesEntity.setIfInterfaceStatus("测试");
            }
            String ifType = interfacesEntity.getIfType();
            InterfacesTypeEntity interfacesTypeEntity = interfacesTypeDao.selectById(Integer.parseInt(ifType));
            interfacesEntity.setIfTypeDescr(interfacesTypeEntity.getIfDescribe());
            QueryWrapper<InterfacesEntity> interfacesEntityQueryWrapper = new QueryWrapper<>();
            interfacesEntityQueryWrapper.eq("ip", ip)
                    .eq("if_index", interfacesEntity.getIfIndex());
            InterfacesEntity entity = interfacesDao.selectOne(interfacesEntityQueryWrapper);
            if (entity != null) {
                interfacesEntity.setId(entity.getId());
                interfacesDao.updateById(interfacesEntity);
            } else {
                interfacesDao.insert(interfacesEntity);
            }
        }
    }

    /**
     * @return void
     * @Param
     * @Date 15:49 2021/5/10
     * @Description: 5分钟执行一次，定时更新数据
     **/
    @Scheduled(cron = "0 0/5 * * * 1-7")
    public void getIpAddress() {
        log.info("[定时任务]——>每分钟执行一次------getIpAddress()");
        List<IpEntity> list = ipEntityDao.selectList(null);
        if (list == null || list.size() == 0) {
            //当数据库中没有数据时刷新本机的数据到数据库
            getInterfacesData("127.0.0.1");
            return;
        }
        for (IpEntity entity : list) {
            String ip = entity.getIp();
            getInterfacesData(ip);
        }
    }
}
