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
 * 名称：CompDLYZ601
 * <p>功能：开发商用户登录验证返回用户信息<br>
 * @brief 开发商用户登录验证返回用户信息
 * @author 柏慧敏
 * @version 0.1	2018年10月18日	柏慧敏创建
 * @note
 */
@Component("CompDLYZ601")
public class CompDLYZ601 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDLYZ601.class);
	private static final String INTERFACE_ID="BSP-OAPI-700";//调用的接口
	
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
		String kfsdjh="";
		String kfsmc="";
		String kfsdz="";
		String jbrxm="";
		String yzbm="";
		String kfsdh="";
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
	    	//将个人账号的值赋给个人登记号
	    	kfsdjh=body.get("kfsdjh").toString();
	    	kfsmc=body.get("kfsmc").toString();
	    	kfsdz=body.get("kfsdz").toString();
	    	jbrxm=body.get("jbrxm").toString();
	    	yzbm=body.get("yzbm").toString();
	    	kfsdh=body.get("kfsdh").toString();
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
		setOutParam("kfsdjh",kfsdjh);//开发商登记号
		setOutParam("kfsmc",kfsmc);//开发商名称
		setOutParam("kfsdz",kfsdz);//开发商地址
		setOutParam("jbrxm", jbrxm);//经办人姓名
		setOutParam("yzbm",yzbm);//邮编
		setOutParam("kfsdh",kfsdh);//电话
		setOutParam("rolecodes",roleCodes);//角色集合
    	/*出口参数赋值结束*/

    	return 0;
   }

}
