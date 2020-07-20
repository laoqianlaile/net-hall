package com.yd.ish.service.impl.external;

import com.yd.basic.expression.IshHystirxExpression;
import com.yd.ish.common.interfaces.IEssCommonItem;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.context.UserContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.svrplatform.util.ReadProperty;
import com.yd.svrplatform.util.crypto.CryptoAes;
import com.yd.workflow.util.Constants;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 名称：Ybmap2CommonItemImpl.java
 * <p>功能： 获取综合服务平台2.0公共参数实现类<br> 
 * @author wanghe
 * @version 0.1	2018年4月12日	wanghe创建
 *                 0.2 2019年10月25日 王赫 添加单位临时用户获取userId判断分支，以及设置其用户类型
 */
@Component("Ybmap2CommonItemImpl")
public class Ybmap2CommonItemImpl implements IEssCommonItem {

	//网厅加密秘钥
	String encKey = ReadProperty.getString("ybmap_encryption_key");
	//中心ID
	public static final String CENTERID = "00077500";
	//渠道登录用户名类型
	public static final String USERIDTYPE = "1";
	//设备区分
	public static final String DEVICETYPE = "3";
	//设备识别码
	public static final String DEVICETOKEN = "1";
	//当前版本
	public static final String CURRENVERSION = "2.0";
	//业务类型
	public static final String BUZTYPE = "1";
	//渠道标识
	public static final String CHANNEL ="40";
	//应用标识
	public static final String APPID = "yondervisionwebservice40";
	//应用KEY
	public static final String APPKEY = "bce74bf22440bf4ddebb6d7e56906cc4";
	//应用TOKEN
	public static final String APPTOKEN = "bce74bf22440bf4ddebb6d7e56906cc4";
	//资金类业务资金发生额
	public static final String MONEY = "1";
	//原始报文标记
	public static final String ORIGINAL = "1";
	//加密标记
	public static final String ENCRYPTION = "0";
	//接口版本号默认上传:V1.0
	public static final String TRANSVERSION = "V2.0";

	public static final String tempUserId = "422123198805060987";
	public static final String tempLoginType = "2";
	@Override
	public String getParams(MainContext mainContext) {
//		MainContext mainContext = MainContext.currentMainContext();
		DataPool datapool = mainContext.getDataPool();
		UserContext usercontext  = mainContext.getUserContext();
		// 组装用户信息
		String userId = "";
		String logintype = "";
		if (usercontext == null || usercontext.getOperId() == null || "".equals(usercontext.getOperId())) {
			// 用户信息为空时，判定为登录交易，用用户输入的登录信息作为userid
			System.out.println("----登录交易-----userId---"+datapool.getString("loginId")+"----logintype-----"+datapool.getString("logintype"));
			userId = datapool.getString("loginId") == null ? tempUserId : datapool.getString("loginId");
			logintype = datapool.getString("logintype") == null ? tempLoginType : datapool.getString("logintype");
		} else {
			// 用户信息不为空时，判定为非登录交易，使用user中的信息作为userid
			//userId = usercontext.getOperId() ;
			
			logintype = usercontext.getLoginTye();
			if("person".equals(logintype)){
				userId = IshHystirxExpression.getRealUserExtInfoWithMainContext(mainContext,"zjhm");
			}else if("orgauth".equals(logintype)){
				userId = IshHystirxExpression.getRealUserExtInfoWithMainContext(mainContext,"dwdjh");
				// 0.2 start
			}else if("orgcert".equals(logintype)){
				userId = IshHystirxExpression.getRealUserExtInfoWithMainContext(mainContext,"dwdjh");
			}else if("orgtemp".equals(logintype)){
				userId = IshHystirxExpression.getRealUserExtInfoWithMainContext(mainContext,"jbrzjhm");
				// 0.2 end
			}else {
				userId = IshHystirxExpression.getRealUserExtInfoWithMainContext(mainContext,"zjhm");
			}
			
			System.out.println("----非登录交易--------userId---------"+userId+"---logintype------"+logintype);

		}
		//用户类型
		String usertype = "";
		//10-个人用户，20-缴存单位，21-开发商
		if("person".equals(logintype)){
			usertype = "10";
			//0.2 start
//		}else if("orgauth".equals(logintype)){
		}else if("orgauth".equals(logintype)||"orgtemp".equals(logintype)){
			// 0.2 end
			usertype = "20";
		}else if("developer".equals(logintype)){
			usertype = "21";
		}else{
			usertype = "00";
		}
			
		Map<String,String> map = new HashMap<String,String>();
		Map<String,String> ybmapMessageMap = new HashMap<String,String>();
	
		//客户端IP
		String  clientIp  = mainContext.getRemoteAddr(); 
		//tellCode
		String tellCode = ReadProperty.getString("user_no");
		//brcCode
		String brcCode = ReadProperty.getString("corp_no");
		//channelSeq
		//String channelSeq = datapool.getString(Constants._WF_IS);
		String channelSeq = datapool.get(Constants._WF_IS) == null || "".equals(datapool.get(Constants._WF_IS)) ? "-1" : datapool.getString(Constants._WF_IS);
			 
		//tranDate
		String tranDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		
		//公共参数加密
		userId=CryptoAes.encrypt(userId, encKey);
		usertype=CryptoAes.encrypt(usertype, encKey);
		String APPID001=CryptoAes.encrypt(APPID, encKey);
		String APPKEY001=CryptoAes.encrypt(APPKEY, encKey);
		String APPTOKEN001=CryptoAes.encrypt(APPTOKEN, encKey);
		String tel001=CryptoAes.encrypt("13069000055", encKey);
		
		map.put("centerId", CENTERID);
		map.put("userId", userId);
		map.put("userIdType", usertype);
		map.put("deviceType", DEVICETYPE);
		map.put("deviceToken", DEVICETOKEN);
		map.put("currenVersion", CURRENVERSION);
		map.put("channel", CHANNEL);
		map.put("appid", APPID001);
		map.put("appkey", APPKEY001);
		map.put("appToken", APPTOKEN001);
		map.put("money", MONEY);
		map.put("original", ORIGINAL);
		map.put("encryption", ENCRYPTION);
		map.put("transversion", TRANSVERSION);
		map.put("tellCode", tellCode);
		map.put("brcCode", brcCode);
		map.put("channelSeq", channelSeq);
		map.put("tranDate", tranDate);
		map.put("clientIp", clientIp);
		map.put("buztype", BUZTYPE);
		map.put("logintype", usertype);
		map.put("tel", tel001);
		map.put("ischeck", "2");

		/*ybmapMessageMap.put("flag", datapool.getString("flag"));
		ybmapMessageMap.put("grzh", datapool.getString("grzh"));
		ybmapMessageMap.put("zjhm", datapool.getString("zjhm"));
		ybmapMessageMap.put("pwd", datapool.getString("pwd"));
		
		
		map.put("ybmapMessage", JsonUtil.getJsonString(ybmapMessageMap));
		*/

		String jsonString = JsonUtil.getJsonString(map);
		return jsonString;
	}

	@Override
	public String encrypt(String encType, String value) {
		return CryptoAes.encrypt(value, "111111");
	}
}
