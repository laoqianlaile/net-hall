package com.yd.ish.service.impl.external;

import com.yd.ish.common.interfaces.IEssCommonItem;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.svrplatform.util.ReadProperty;
import com.yd.workflow.util.Constants;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 名称：SocketCommonItemImpl.java
 * <p>功能： 获取socket公共参数实现类<br> 
 * @author 柏慧敏
 * @version 0.1	2018年4月18日	bhm创建
 *                0.2 2019年7月3日    王赫修改重载getParams方法添加MainContext参数
 */
@Component("SocketCommonItemImpl")
public class SocketCommonItemImpl implements IEssCommonItem {

	//中心ID
	public static final String SubBank = "aaa";
	//设备区分
	public static final String HostBank = "aaa";
	//当前版本
	public static final String OppOperList = "12310";
	//业务类型
	public static final String AuthFlag = "1231";
	//应用标识
	public static final String TranIP = "";

	//应用TOKEN
	public static final String AuthCode1 = "1231";
	
	public static final String AuthCode2 = "1231";
	
	public static final String AuthCode3 = "1231";
	
	public static final String ChkCode = "1231";
	
	public static final String TranCode = "";
	
	public static final String BrcCode = ReadProperty.getString("corp_no");
	
	public static final String TellCode = ReadProperty.getString("user_no");


	public String getParams(String a) {
		return a;
		
	}


	@Override
	public String getParams(MainContext mainContext) {
//		MainContext mainContext = MainContext.currentMainContext();
		DataPool datapool = mainContext.getDataPool();
		Map<String,String> map = new HashMap<String,String>();
		
		//channelSeq
		String channelSeq = datapool.get(Constants._WF_IS) == null || "".equals(datapool.get(Constants._WF_IS)) ? "-1" : datapool.getString(Constants._WF_IS);
		String FinancialDate=getDT(0, 0, "yyyy-MM-dd");
		String TranIP=mainContext.getRemoteAddr();
		String TranDate=getDT(0, 0, "yyyy-MM-dd");
		String STimeStamp=getDT(0, 0, "yyyy-MM-dd kk:mm:ss ");
		String MTimeStamp=getDT(0, 0, "yyyy-MM-dd kk:mm:ss ");
		
		map.put("SubBank", SubBank);
		map.put("HostBank", HostBank);
		map.put("FinancialDate", FinancialDate);
		map.put("OppOperList", OppOperList);
		map.put("AuthFlag", AuthFlag);
		map.put("ChannelSeq", channelSeq);
		map.put("TranIP", TranIP);
		map.put("AuthCode1", AuthCode1);
		map.put("AuthCode2", AuthCode2);
		map.put("AuthCode3", AuthCode3);
		map.put("ChkCode", ChkCode);
		map.put("TellCode", TellCode);
		map.put("BrcCode", BrcCode);
		map.put("TranDate", TranDate);
		map.put("STimeStamp", STimeStamp);
		map.put("MTimeStamp", MTimeStamp);
	
		String jsonString = JsonUtil.getJsonString(map);
		return jsonString;
	}

	@Override
	public String encrypt(String encType, String value) {
		return null;
	}

	public static String getDT(int date, int hour, String format) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(new Date().getTime());
		c.add(Calendar.HOUR_OF_DAY, hour);
		c.add(Calendar.DAY_OF_YEAR, date);
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(c.getTime()).trim();
	}
	
}
