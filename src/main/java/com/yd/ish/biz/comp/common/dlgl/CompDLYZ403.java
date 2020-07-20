package com.yd.ish.biz.comp.common.dlgl;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.org.util.CommonErrorCode;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.DataPool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 名称：CompDLYZ403.java
 * <p>功能：登录功能-单位临时用户登录验证返回用户信息 <br>
 * @author 王赫
 * @version 0.1	2019年10月23日	王赫创建
 */
@Component("CompDLYZ403")
public class CompDLYZ403 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDLYZ403.class);

	private static final String INTERFACE_ID="BSP-OAPI-402";//调用的接口

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
		String jbrzjlx="";
		String jbrzjhm="";
		String jbrsjhm="";
//		String dwmc="";
		String roleCodes="";
		
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
	    	jbrzjlx=body.get("jbrzjlx").toString();
			jbrzjhm=body.get("jbrzjhm").toString();
			jbrsjhm=body.get("jbrsjhm").toString();
//	    	dwmc=body.get("dwmc").toString();
			roleCodes=body.get("rolecode").toString();
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
		setOutParam("jbrzjlx",jbrzjlx);//经办人证件类型
		setOutParam("jbrzjhm",jbrzjhm);//经办人证件号码
		setOutParam("jbrsjhm",jbrsjhm);//经办人手机号码
//		setOutParam("dwmc",dwmc);//单位名称
		setOutParam("rolecodes",roleCodes);//角色集合
    	/*出口参数赋值结束*/

    	return 0;
   }

	
}