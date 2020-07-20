package com.yd.ish.biz.comp.common.wjma;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;

/**
 * 名称：CompWJMA02.java
 * <p>功能：个人用户忘记密码保存信息 <br> 
 * @author 张洪超
 * @version 0.1	2018年4月20日	张洪超创建
 */
@Component("CompWJMA02")
public class CompWJMA02 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompWJMA02.class);
	
	private static final String INTERFACE_ID = "BSP-OAPI-504";//调用忘记密码的接口
	private static final String FORGET_PWD_INTERFACE = "getForgetPwd";
	@Override
	public int execute() {

		/*入口参数赋值开始*/
//		String xm=getString("xm");//姓名
//		String zjlx=getString("zjlx");//证件类型
//		String zjhm=getString("zjhm");//证件号码
//		String dlmm=getString("dlmm");//登录密码
    	/*入口参数赋值结束*/
		
		logger.info("[+]调用个人忘记密码接口"+INTERFACE_ID+"开始");
		//接口调用
		XmlResObj data =  super.sendExternal(FORGET_PWD_INTERFACE,false);
	    XmlResHead head=data.getHead();
	    if (XmlResHead.TR_SUCCESS.equals(head.getParticular_code())){
			
	    	logger.info("[-]调用个人忘记密码接口"+INTERFACE_ID+"成功");
	    }else{
	    	logger.info("[-]调用个人忘记密码接口"+INTERFACE_ID+"失败");
	        throw new TransSingleException(head.getParticular_code(),head.getParticular_info());
	    }
    	/*出口参数赋值开始*/
    	/*出口参数赋值结束*/

    	return 0;
   }

}
