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
 * 名称：CompDLYZ101.java
 * <p>功能：登录功能-管理员用户登录验证返回用户信息 <br> 
 * @author 张洪超
 * @version 0.1	2017年8月31日	张洪超创建
 *  	   V0.2 2018年10月22日     柏慧敏修改    角色编码由detail标签中返回改为在body标签中返回
 *
 */
@Component("CompDLYZ101")
public class CompDLYZ101 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDLYZ101.class);
	
	private static final String INTERFACE_ID="BSP-OAPI-100";//调用的接口

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
		String managerId="";
		String managerName="";
		String instcode="";
		String instname="";
		String rolecode="";
		//登录验证返回用户和角色信息
		logger.info("[+]调用核心用户验证返回用户信息接口"+INTERFACE_ID+"开始");
		DataPool dataPool = MainContext.currentMainContext().getDataPool();
		dataPool.put("dlyyh", loginId);
		dataPool.put("dlmm", password);
		XmlResObj data =  super.sendExternal(INTERFACE_ID,false);
	    XmlResHead head=data.getHead();
	    if (XmlResHead.TR_SUCCESS.equals(head.getParticular_code())){
	    	Map<String,Object> body=data.getBody();
	    	managerId=body.get("managerId").toString();
	    	managerName=body.get("managerName").toString();
			instcode=body.get("instcode").toString();
			instname=body.get("instname").toString();
			rolecode=body.get("rolecode").toString();
			if(StringUtils.isBlank(instcode)||StringUtils.isBlank(instname)){
				throw new TransSingleException(CommonErrorCode.ERROR_YHWSZJG,"");
			}
			if(StringUtils.isBlank(rolecode)){
				throw new TransSingleException(CommonErrorCode.ERROR_YHWSZJS,"");
			}
	    	logger.info("[-]调用核心用户验证返回用户信息接口"+INTERFACE_ID+"结束");
	    }else{
	    	logger.info("[-]调用核心用户验证返回用户信息接口"+INTERFACE_ID+"结束");
	        throw new TransSingleException(head.getParticular_code(),head.getParticular_info());
	    }
    	/*出口参数赋值开始*/
		setOutParam("managerId",managerId);//序号
		setOutParam("managerName",managerName);//名称
		setOutParam("instcode",instcode);//机构号
		setOutParam("instname",instname);//机构名
		setOutParam("rolecodes",rolecode);//角色集合
    	/*出口参数赋值结束*/

    	return 0;
   }

}
