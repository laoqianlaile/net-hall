package com.yd.ish.biz.comp.common.yhzc;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.org.util.EncryptionUtil;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.DataPool;

/**
 * 名称：CompYHZCBC01.java
 * <p>功能：个人用户注册保存信息 <br> 
 * @author 张洪超
 * @version 0.1	2017年9月21日	张洪超创建
 */
@Component("CompYHZCBC01")
public class CompYHZCBC01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompYHZCBC01.class);
	
	private static final String INTERFACE_ID="BSP-OAPI-502";//调用用户注册的接口

	@Override
	public int execute() {

		/*入口参数赋值开始*/
		String cxmm=getString("cxmm");//登录密码
		String jymm=getString("jymm");//交易密码
    	/*入口参数赋值结束*/
		
		logger.info("[+]调用个人用户注册接口"+INTERFACE_ID+"开始");
		DataPool dataPool = MainContext.currentMainContext().getDataPool();
		if(StringUtils.isNotBlank(cxmm)){
			dataPool.put("cxmm", EncryptionUtil.MD5Encode(cxmm));
		}
		if(StringUtils.isNotBlank(jymm)){
			dataPool.put("jymm", EncryptionUtil.MD5Encode(jymm));
		}
		//接口调用
		XmlResObj data =  super.sendExternal(INTERFACE_ID,false);
	    XmlResHead head=data.getHead();
	    if (XmlResHead.TR_SUCCESS.equals(head.getParticular_code())){
			
	    	logger.info("[-]调用个人用户注册接口"+INTERFACE_ID+"成功");
	    }else{
	    	logger.info("[-]调用个人用户注册接口"+INTERFACE_ID+"失败");
	        throw new TransSingleException(head.getParticular_code(),head.getParticular_info());
	    }
    	/*出口参数赋值开始*/
    	/*出口参数赋值结束*/

    	return 0;
   }

}
