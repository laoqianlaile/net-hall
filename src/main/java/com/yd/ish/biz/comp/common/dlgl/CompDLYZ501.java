package com.yd.ish.biz.comp.common.dlgl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;

 /**
 * 名称：CompDLYZ501
 * <p>功能：发送短信验证码<br>
 * @brief 发送短信验证码
 * @author  柏慧敏
 * @version  0.1 2018年10月11日 柏慧敏创建
 * @note
 */
@Component("CompDLYZ501")
public class CompDLYZ501 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDLYZ501.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/
		
    	/*入口参数赋值结束*/
		
		logger.info("[+]调用接口校验单位用户并发送验证码开始");
		XmlResObj data =  super.sendExternal("BSP-OAPI-601",false);
	    XmlResHead head=data.getHead();
		logger.info("[-]调用接口校验单位用户并发送验证码结束");   
		
    	/*出口参数赋值开始*/
	    setOutParam("returnCode", head.getParticular_code());
	    setOutParam("msg", head.getParticular_info());
    	/*出口参数赋值结束*/

    	return 0;
   }

}
