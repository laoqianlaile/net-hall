package com.yd.ish.service.impl.external;

import com.yd.ish.common.interfaces.IEssCommonItem;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.context.UserContext;
import com.yd.svrplatform.comm_mdl.context.UserContextFactory;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.svrplatform.util.ReadProperty;
import com.yd.workflow.util.Constants;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 名称：Ybmap1CommonItemImpl.java
 * <p>功能： 获取综合服务平台1.0公共参数实现类<br> 
 * @author wanghe
 * @version 0.1	2018年4月12日	wanghe创建
 *                 0.2 2019年10月25日 王赫 添加单位临时用户设置其用户类型
 */
@Component("Ybmap1CommonItemImpl")
public class Ybmap1CommonItemImpl implements IEssCommonItem {

	//中心ID
	public static final String CENTERID = "00077500";
	//设备区分
	public static final String DEVICETYPE = "3";
	//设备识别码
	public static final String DEVICETOKEN = "";
	//当前版本
	public static final String CURRENVERSION = "1.0";
	//业务类型
	public static final String BUZTYPE = "";
	//渠道标识
	public static final String CHANNEL="40";
	//应用标识
	public static final String APPID = "yondervisionwebservice40";
	//应用KEY
	public static final String APPKEY = "bce74bf22440bf4ddebb6d7e56906cc4";
	//应用TOKEN
	public static final String APPTOKEN = "bce74bf22440bf4ddebb6d7e56906cc4";

	@Override
	public String getParams(MainContext mainContext) {
//		MainContext mainContext = MainContext.currentMainContext();
		if (mainContext == null) {
			mainContext = new MainContext(UserContextFactory.getInstance());
		}
		DataPool datapool = mainContext.getDataPool();
		if(datapool == null){
			datapool  = new DataPool();
		}
		UserContext usercontext  = mainContext.getUserContext();
		if(usercontext==null){
			usercontext = new UserContext();
		}
		//用户ID
		String userId = usercontext.getOperId()==null?"":usercontext.getOperId();
		String logintype = usercontext.getLoginTye()==null?"":usercontext.getLoginTye();
		//用户类型
		String usertype = "";
		//10-个人用户，20-缴存单位，21-开发商
		if("person".equals(logintype)){
			usertype = "10";
			// 0.2 start
//		}else if("orgauth".equals(logintype)){
		}else if("orgauth".equals(logintype)||"orgtemp".equals(logintype)){
		    // 0.2  end
			usertype = "20";
		}else if("developer".equals(logintype)){
			usertype = "21";
		}
			
		Map<String,String> map = new HashMap<String,String>();
	
		//客户端IP
		String  clientIp  = mainContext.getRemoteAddr(); 
		//tellCode
		String tellCode = ReadProperty.getString("user_no");
		//brcCode
		String brcCode = ReadProperty.getString("corp_no");
		//channelSeq
		String channelSeq = datapool.getString(Constants._WF_IS)==null?"":datapool.getString(Constants._WF_IS);
		//tranDate
		String tranDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		
		map.put("centerId", CENTERID);
		map.put("userId", userId);
		map.put("usertype", usertype);
		map.put("deviceType", DEVICETYPE);
		map.put("deviceToken", DEVICETOKEN);
		map.put("currenVersion", CURRENVERSION);
		map.put("buztype", BUZTYPE);
		map.put("channel", CHANNEL);
		map.put("appid", APPID);
		map.put("appkey", APPKEY);
		map.put("appToken", APPTOKEN);
		map.put("clientIp", clientIp);
		map.put("tellCode", tellCode);
		map.put("brcCode", brcCode);
		map.put("channelSeq", channelSeq);
		map.put("tranDate", tranDate);
		String jsonString = JsonUtil.getJsonString(map);
		return jsonString;
	}

	@Override
	public String encrypt(String encType, String value) {
		return null;
	}

}
