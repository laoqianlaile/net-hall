package com.yd.ish.biz.comp.common.yhzc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;

/**
 * 名称：CompYHYZ02.java
 * <p>功能：用户银行卡&手机号信息验证 <br> 
 * @author 张洪超
 * @version 0.1	2018年01月10日	张洪超创建
 */
@Component("CompYHYZ02")
public class CompYHYZ02 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompYHYZ02.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/
    	/*入口参数赋值结束*/
		
		logger.info("[+]调用用户银行卡&手机号信息验证验证接口BSP-OAPI-501开始");
		//调用个人用户验证接口同时查询是否初始密码
		XmlResObj data =  super.sendExternal("BSP-OAPI-501",false);
	    XmlResHead head=data.getHead();
	    if (XmlResHead.TR_SUCCESS.equals(head.getParticular_code())){
	    	logger.info("[-]调用用户银行卡&手机号信息验证接口成功");
	    }else{
	    	logger.info("[-]调用用户银行卡&手机号信息验证接口失败");
	        throw new TransSingleException(head.getParticular_code(),head.getParticular_info());
	    }
    	/*出口参数赋值开始*/
    	/*出口参数赋值结束*/

    	return 0;
   }

}
