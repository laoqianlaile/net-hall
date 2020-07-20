package com.yd.ish.biz.comp.common.wjma;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 名称：CompWJMA03.java
 * <p>功能：单位临时用户忘记密码用户信息验证 <br>
 * @author 王赫
 * @version 0.1	2019年10月30日      王赫创建
 */
@Component("CompWJMA03")
public class CompWJMA03 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompWJMA03.class);
	private static final String INTERFACE_ID = "BSP-OAPI-507";

	@Override
	public int execute() {

		/*入口参数赋值开始*/
		String jbrxm=getString("jbrxm");//经办人姓名
		String jbrzjlx=getString("jbrzjlx");//经办人证件类型
		String jbrzjhm=getString("jbrzjhm");//经办人证件号码
		String jbrsjhm=getString("jbrsjhm");//经办人手机号码
		String dxyzm=getString("dxyzm");//短息验证码
    	/*入口参数赋值结束*/
		logger.info("[+]单位临时用户忘记密码用户信息验证接口"+INTERFACE_ID+"开始");
		//调用个人用户验证接口同时查询是否初始密码
		XmlResObj data =  super.sendExternal(INTERFACE_ID);
	    XmlResHead head=data.getHead();
	    if (XmlResHead.TR_SUCCESS.equals(head.getParticular_code())){
	    	logger.info("[-]单位临时用户忘记密码用户信息验证接口成功");
	    }else{
	    	logger.info("[-]单位临时用户忘记密码用户信息验证接口失败");
	        throw new TransSingleException(head.getParticular_code(),head.getParticular_info());
	    }

    	/*出口参数赋值开始*/
    	/*出口参数赋值结束*/

    	return 0;
   }

}
