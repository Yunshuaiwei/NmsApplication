package com.xust.dao;

import com.adventnet.snmp.mibs.MibModule;
import com.adventnet.snmp.mibs.MibOperations;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/5/5 18:17
 * @Version
 **/
public class MibDao {
    public static MibOperations mibOps;
    private static MibModule mib;

    static {
        mibOps = new MibOperations();
        try {
            mib = mibOps.loadMibModule("RFC1213-MIB.mib");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * 根据名称获取OID
     **/
    public static String getOID(String mibName) throws Exception {
        return mib.getMibNode(mibName).getNumberedOIDString();
    }

    /**
     * 根据OID获取名称
     **/
    public static String getIsmName(String oid) {
        String name = "";
        try {
            name = mib.getMibNode(oid).toString();
        } catch (Exception e) {
            name = "此MID库不能解析的OID";
        }
        return name;
    }
}
