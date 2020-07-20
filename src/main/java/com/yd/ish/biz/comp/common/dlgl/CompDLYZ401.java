package com.yd.ish.biz.comp.common.dlgl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.org.util.CommonErrorCode;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.DataPool;

/**
 * 名称：单位登录
 * <p>功能：登录功能-单位用户登录验证返回用户信息 <br> 
 * @author 张洪超
 * @version 0.1	2017年8月31日	张洪超创建
 */
@Component("CompDLYZ401")
public class CompDLYZ401 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDLYZ401.class);

	private static final String INTERFACE_ID="getDWDL";//调用的接口

	@Override
	public int execute() {

		/*入口参数赋值开始*/
		String loginId=getString("loginId");//登录id
		String password=getString("password");//密码
    	/*入口参数赋值结束*/
		if(StringUtils.isBlank(loginId)){
			throw new TransSingleException(CommonErrorCode.ERROR_NOT_NULL,"用户名");
		}
		if(StringUtils.isBlank(password)){
			throw new TransSingleException(CommonErrorCode.ERROR_NOT_NULL,"密码");
		}
		String jbrxm="";
		String dwdjh="";
		String dwmc="";
		String instcode="";
		String instname="";
		String dwdz="";
		String dwslrq="";
		String dwyb="";
		String shcxdm="";
		String roleCodes="";
		String dwzh="";
		String wtinstcode = "";
		String BrcCode = "";
		//登录验证返回用户和角色信息
		logger.info("[+]调用核心用户验证返回用户信息接口"+INTERFACE_ID+"开始");
		DataPool dataPool = MainContext.currentMainContext().getDataPool();
		dataPool.put("dlyyh", loginId);
		dataPool.put("dlmm", password);
		XmlResObj data =  super.sendExternal(INTERFACE_ID,false);
	    XmlResHead head=data.getHead();
	    if (XmlResHead.TR_SUCCESS.equals(head.getParticular_code())){
	    	Map<String,Object> body=data.getBody();
	    	jbrxm=body.get("jbrxm").toString();
	    	dwdjh=body.get("dwdjh").toString();
	    	dwmc=body.get("dwmc").toString();
			instcode=body.get("instcode").toString();
			instname=body.get("instname").toString();
			dwdz=body.get("dwdz").toString();
			dwslrq=body.get("dwslrq").toString();
			dwyb=body.get("dwyb").toString();
			shcxdm=body.get("shcxdm").toString();
			roleCodes=body.get("rolecode").toString();
			dwzh=body.get("dwzh").toString();
			wtinstcode = body.get("jbjg").toString();
			BrcCode = body.get("jbjg").toString();
			if(StringUtils.isBlank(instcode)||StringUtils.isBlank(instname)){
				throw new TransSingleException(CommonErrorCode.ERROR_YHWSZJG,"");
			}
			if(StringUtils.isBlank(roleCodes)){
				throw new TransSingleException(CommonErrorCode.ERROR_YHWSZJS,"");
			}
			
	    	logger.info("[-]调用核心用户验证返回用户信息接口"+INTERFACE_ID+"结束");
	    }else{
	    	logger.info("[-]调用核心用户验证返回用户信息接口"+INTERFACE_ID+"结束");
	        throw new TransSingleException(head.getParticular_code(),head.getParticular_info());
	    }
    	/*出口参数赋值开始*/
		setOutParam("jbrxm",jbrxm);//经办人姓名
		setOutParam("dwdjh",dwdjh);//单位登记号
		setOutParam("dwmc",dwmc);//单位名称
		setOutParam("instcode",instcode);//机构号
		setOutParam("instname",instname);//机构名
		setOutParam("dwdz", dwdz);//单位地址
		setOutParam("dwslrq", dwslrq);//单位设立日期
		setOutParam("dwyb", dwyb);//单位邮编
		setOutParam("shcxdm", shcxdm);//社会诚信代码
		setOutParam("rolecodes",roleCodes);//角色集合
		setOutParam("dwzh",dwzh);//角色集合
		setOutParam("wtinstcode",wtinstcode);
		setOutParam("BrcCode",wtinstcode);
    	/*出口参数赋值结束*/

    	return 0;
   }

	
}