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
 * 名称：CompDLYZ204.java
 * <p>功能：登录功能-个人扫码用户登录验证返回用户信息 <br> 
 * @author 张洪超
 * @version 0.1	2017年8月31日	张洪超创建
 * 		   V0.2 2018年10月23日     柏慧敏修改    1.根据贯标信息项修改字段id 2.角色编码由detail标签中返回改为在body标签中返回
 *
 */
@Component("CompDLYZ204")
public class CompDLYZ204 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDLYZ204.class);

	private static final String INTERFACE_ID="BSP-OAPI-203";//调用的接口

	@Override
	public int execute() {

		/*入口参数赋值开始*/
		String sfzh=getString("sfzh");//身份证号
		String xingming=getString("xingming");//姓名
    	/*入口参数赋值结束*/
		String grzh="";
		String sjhm="";
		String zjlx="";
		String zjhm="";
		String instcode="";
		String instname="";
		String roleCodes="";
		//登录验证返回用户和角色信息
		logger.info("[+]调用核心用户验证返回用户信息接口"+INTERFACE_ID+"开始");
		DataPool dataPool = MainContext.currentMainContext().getDataPool();
		dataPool.put("sfzh", sfzh);
		dataPool.put("xingming", xingming);
		XmlResObj data =  super.sendExternal(INTERFACE_ID,false);
	    XmlResHead head=data.getHead();
	    if (XmlResHead.TR_SUCCESS.equals(head.getParticular_code())){
	    	Map<String,Object> body=data.getBody();
	    	//将个人账号赋值给个人登记号
	    	grzh=body.get("grzh").toString();
	    	xingming=body.get("xingming").toString();
	    	sjhm=body.get("sjhm").toString();
	    	zjlx=body.get("zjlx").toString();
	    	zjhm=body.get("zjhm").toString();
			instcode=body.get("instcode").toString();
			instname=body.get("instname").toString();
			roleCodes=body.get("rolecode").toString();
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
		setOutParam("grzh",grzh);//个人账号
		setOutParam("xingming",xingming);//姓名
		setOutParam("sjhm",sjhm);//用户手机号
		setOutParam("zjlx",zjlx);//证件类型
		setOutParam("zjhm",zjhm);//证件号码
		setOutParam("instcode",instcode);//机构号
		setOutParam("instname",instname);//机构名
		setOutParam("rolecodes",roleCodes);//角色集合
    	/*出口参数赋值结束*/

    	return 0;
   }

}
