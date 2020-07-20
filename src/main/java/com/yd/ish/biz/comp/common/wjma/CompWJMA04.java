package com.yd.ish.biz.comp.common.wjma;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 名称：CompWJMA04.java
 * <p>功能：单位临时用户忘记密码保存信息 <br>
 * @author 王赫
 * @version 0.1	2019年10月30日	王赫创建
 */
@Component("CompWJMA04")
public class CompWJMA04 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompWJMA04.class);
	
	private static final String INTERFACE_ID="BSP-OAPI-508";//调用忘记密码的接口

	@Override
	public int execute() {

		/*入口参数赋值开始*/
		String jbrxm=getString("jbrxm");//经办人姓名
		String jbrzjlx=getString("jbrzjlx");//经办人证件类型
		String jbrzjhm=getString("jbrzjhm");//经办人证件号码
		String dlmm=getString("dlmm");//登录密码
    	/*入口参数赋值结束*/
		
		logger.info("[+]调用单位临时用户忘记密码接口"+INTERFACE_ID+"开始");
		//接口调用
		XmlResObj data =  super.sendExternal(INTERFACE_ID,false);
	    XmlResHead head=data.getHead();
	    if (XmlResHead.TR_SUCCESS.equals(head.getParticular_code())){
			
	    	logger.info("[-]调用单位临时用户忘记密码接口"+INTERFACE_ID+"成功");
	    }else{
	    	logger.info("[-]调用单位临时用户忘记密码接口"+INTERFACE_ID+"失败");
	        throw new TransSingleException(head.getParticular_code(),head.getParticular_info());
	    }
    	/*出口参数赋值开始*/
    	/*出口参数赋值结束*/

    	return 0;
   }

}
