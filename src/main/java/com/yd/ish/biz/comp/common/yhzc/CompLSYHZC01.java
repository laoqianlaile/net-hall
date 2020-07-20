package com.yd.ish.biz.comp.common.yhzc;

import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;

import java.util.HashMap;
import java.util.Map;

/**
 * 名称：CompLSYHZC01
 * <p>功能：单位临时用户注册<br>
 * @author  王赫
 * @version 0.1  2019年10月21日 王赫创建
 * @note
 */
@Component("CompLSYHZC01")
public class CompLSYHZC01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompLSYHZC01.class);

	 private static final String INTERFACE_ID="BSP-OAPI-506";//临时用户注册接口

	@Override
	public int execute() {

		/*入口参数赋值开始*/
		String jbrxm=getString("jbrxm");//经办人姓名
		String jbrzjlx=getString("jbrzjlx");//经办人证件类型
		String jbrzjhm=getString("jbrzjhm");//经办人证件号码
		String jbrsjhm=getString("jbrsjhm");//经办人手机号码
		String dlmm=getString("dlmm");//登录密码
		String dxyzm=getString("dxyzm");//短信验证码
    	/*入口参数赋值结束*/

		//接口调用
		String zcjg = "";
		String zcxx = "";
		logger.info("[+]调用临时用户注册接口"+INTERFACE_ID+"开始。");
		XmlResObj data =  super.sendExternal(INTERFACE_ID,false);
		XmlResHead head = data.getHead();
		if(XmlResHead.TR_SUCCESS.equals(head.getParticular_code())){
			logger.info("[-]调用临时用户注册接口"+INTERFACE_ID+"成功。");
		}else {
			logger.info("[-]调用临时用户注册接口"+INTERFACE_ID+"失败。");
			throw new TransSingleException(head.getParticular_code(),head.getParticular_info());
		}



    	/*出口参数赋值开始*/
    	/*出口参数赋值结束*/

    	return 0;
   }

}
