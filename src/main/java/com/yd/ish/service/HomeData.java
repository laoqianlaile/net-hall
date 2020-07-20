package com.yd.ish.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yd.biz.engine.TransEngine;
import com.yd.ish.biz.comp.dw.CompDwInfo;
import com.yd.ish.common.util.TuoMinUtil;
import com.yd.ish.service.impl.tuomin.TuoMinServiceImpl;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.context.UserContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;

import javax.annotation.Resource;

/**
 * 名称：PersonHomeData.java
 * <p>功能： 首页展示信息<br>
 * @author 张洪超
 * @version 0.1	2017年11月8日	张洪超创建
 *                 0.2  2019年10月28日王赫 添加获得首页显示的单位临时用户信息
 */
public class HomeData {
	/**
	 * 获得首页展示的个人信息
	 * 
	 * @param datapool
	 * @param user
	 * @return
	 */
	public static String getPersonHomeData(DataPool datapool, UserContext user) {
		
		List<Map<String,Object>> homeData = new ArrayList<Map<String,Object>>();
		Map<String,Object> m1=new HashMap<String,Object>();
		m1.put("label", "证件号码");
		m1.put("value",datapool.get("zjhm").toString());
		m1.put("cols", 4);
		homeData.add(m1);
		Map<String,Object> m2=new HashMap<String,Object>();
		m2.put("label", "手机号码");
		m2.put("value", datapool.get("sjhm").toString());
		m2.put("cols", 4);
		homeData.add(m2);
		Map<String,Object> m3=new HashMap<String,Object>();
		m3.put("label","电子邮箱");
		m3.put("value", TuoMinUtil.getTMValue("yanghongmin@163.com",TuoMinServiceImpl.EMAIL));
		m3.put("cols", 4);
		homeData.add(m3);
		Map<String,Object> m4=new HashMap<String,Object>();
		m4.put("label","单位登记号");
		m4.put("value", datapool.getString("dwbh"));
		m4.put("cols", 4);
		homeData.add(m4);
		Map<String,Object> m5=new HashMap<String,Object>();
		m5.put("label","单位名称");
		m5.put("value", datapool.getString("instname"));
		m5.put("cols", 6);
		homeData.add(m5);
		
		Map<String,Object> m=new HashMap<String,Object>();
		m.put("name", datapool.getString("xingming"));
		m.put("info", homeData);
		
		return JsonUtil.getJsonString(m);
	}
	/**
	 * 获得首页展示的单位信息
	 * 
	 * @param datapool
	 * @param user
	 * @return
	 */
	public static String getOrgHomeData(DataPool datapool, UserContext user) {
		TransEngine.getInstance().execute("TranHomeData", MainContext.currentMainContext());
		List<Map<String,Object>> homeData = new ArrayList<Map<String,Object>>();
		Map<String,Object> m1=new HashMap<String,Object>();
		m1.put("label", "单位地址");
		m1.put("value",datapool.getString("dwdz"));
		m1.put("cols", 4);
		homeData.add(m1);
		Map<String,Object> m2=new HashMap<String,Object>();
		m2.put("label", "社会诚信代码");
//		m2.put("value", datapool.getString("shcxdm"));
		m2.put("value", datapool.getString("cocietycode"));
		m2.put("cols", 4);
		homeData.add(m2);
		Map<String,Object> m3=new HashMap<String,Object>();
		m3.put("label","单位登记号");

		//m3.put("value", toConceal(user.getOrgId()));
		m3.put("value", datapool.getString("dwdjh"));
		m3.put("cols", 4);
		homeData.add(m3);
		Map<String,Object> m4=new HashMap<String,Object>();
		m4.put("label","单位邮编");
		m4.put("value", datapool.getString("dwyb"));
		m4.put("cols", 4);
		homeData.add(m4);
		Map<String,Object> m5=new HashMap<String,Object>();
		m5.put("label","单位成立时间");
		m5.put("value", datapool.getString("dwslrq"));
		m5.put("cols", 6);
		homeData.add(m5);
		
		Map<String,Object> m=new HashMap<String,Object>();
		m.put("name",  datapool.getString("dwmc"));
		m.put("info", homeData);
		
		return JsonUtil.getJsonString(m);
	}

	/**
	 *  获得首页展示的单位临时用户信息   0.2 王赫添加  start
	 *
	 * @param datapool
	 * @param user
	 * @return
	 */
	public static String getOrgTempHomeData(DataPool datapool, UserContext user) {

		List<Map<String,Object>> homeData = new ArrayList<Map<String,Object>>();
		Map<String,Object> m1=new HashMap<String,Object>();
		m1.put("label", "经办人姓名");
		m1.put("value",datapool.getString("jbrxm"));
		m1.put("cols", 4);
		homeData.add(m1);
		Map<String,Object> m2=new HashMap<String,Object>();
		m2.put("label","经办人证件号码");
		m2.put("value", datapool.getString("jbrzjhm"));
		m2.put("cols", 4);
		homeData.add(m2);
		Map<String,Object> m3=new HashMap<String,Object>();
		m3.put("label","经办人手机号码");
		m3.put("value", datapool.getString("jbrsjhm"));
		m3.put("cols", 4);
		homeData.add(m3);

		Map<String,Object> m=new HashMap<String,Object>();
//		m.put("name",  datapool.getString("dwmc"));
		m.put("name",  datapool.getString("jbrxm"));
		m.put("info", homeData);

		return JsonUtil.getJsonString(m);
	}
	
	/**
	 * 获得首页展示的开发商信息
	 * 
	 * @param datapool
	 * @param user
	 * @return
	 */
	public static String getKfsHomeData(DataPool datapool, UserContext user) {
		
		List<Map<String,Object>> homeData = new ArrayList<Map<String,Object>>();
		Map<String,Object> m1=new HashMap<String,Object>();
		m1.put("label", "开发商登记号");
		m1.put("value", datapool.getString("kfsdjh"));
		m1.put("cols", 4);
		homeData.add(m1);
		Map<String,Object> m2=new HashMap<String,Object>();
		m2.put("label","电话号码");
		m2.put("value", datapool.getString("kfsdh"));
		m2.put("cols", 4);
		homeData.add(m2);
		Map<String,Object> m3=new HashMap<String,Object>();
		m3.put("label","邮政编码");
		m3.put("value", datapool.getString("yzbm"));
		m3.put("cols", 4);
		homeData.add(m3);
		Map<String,Object> m4=new HashMap<String,Object>();
		m4.put("label","经办人姓名");
		m4.put("value", datapool.getString("jbrxm"));
		m4.put("cols", 4);
		homeData.add(m4);
		Map<String,Object> m5=new HashMap<String,Object>();
		m5.put("label", "开发商地址");
		m5.put("value", datapool.getString("kfsdz"));
		m5.put("cols", 6);
		homeData.add(m5);
		Map<String,Object> m=new HashMap<String,Object>();
		m.put("name", datapool.getString("kfsmc"));
		m.put("info", homeData);
		
		return JsonUtil.getJsonString(m);
	}
	
	public static void main(String[] args) {
		System.out.println(toConceal("123112"));
	}
	/**
	 * 脱敏字符
	 */
    private static final String SYMBOL = "*";
    /**
     * 脱敏处理
     * @param value
     * @return
     */
    public static String toConceal(String value) {
        if (null == value || "".equals(value)) {
            return value;
        }
        //数据长度
        int len = value.length();
        int SIZE = 5;
        //身份类
        if(len >= 18){
        	SIZE = 10;
        }
        // 第一部分长度
        int pamaone = len / 2;
        // 第二部分
        int pamatwo = pamaone - 1;
        // 第三部分
        int pamathree = len % 2;
        StringBuilder stringBuilder = new StringBuilder();
        if (len <= 2) {
            if (pamathree == 1) {
                return SYMBOL;
            }
            stringBuilder.append(SYMBOL);
            stringBuilder.append(value.charAt(len - 1));
        } else {
            if (pamatwo <= 0) {
                stringBuilder.append(value.substring(0, 1));
                stringBuilder.append(SYMBOL);
                stringBuilder.append(value.substring(len - 1, len));

            } else if (pamatwo >= SIZE / 2 && SIZE + 1 != len) {
                int pamafive = (len - SIZE) / 2;
                stringBuilder.append(value.substring(0, pamafive));
                for (int i = 0; i < SIZE; i++) {
                    stringBuilder.append(SYMBOL);
                }
                if ((pamathree == 0 && SIZE / 2 == 0) || (pamathree != 0 && SIZE % 2!=0)) {
                    stringBuilder.append(value.substring(len - pamafive, len));
                } else {
                    stringBuilder.append(value.substring(len - (pamafive + 1), len));
                }
            } else {
                int pamafour = len - 2;
                stringBuilder.append(value.substring(0, 1));
                for (int i = 0; i < pamafour; i++) {
                    stringBuilder.append(SYMBOL);
                }
                stringBuilder.append(value.substring(len - 1, len));
            }
        }
        return stringBuilder.toString();
    }
}
