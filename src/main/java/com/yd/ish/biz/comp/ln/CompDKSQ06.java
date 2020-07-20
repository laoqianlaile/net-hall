package com.yd.ish.biz.comp.ln;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;

 /**
 * 名称：CompDKSQ06
 * <p>功能：获取月还款额<br>
 * @brief 获取月还款额
 * @author 柏慧敏
 * @version 0.1 2018年7月18日 柏慧敏创建
 * @note
 */
@Component("CompDKSQ06")
public class CompDKSQ06 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDKSQ06.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/
		
    	/*入口参数赋值结束*/

		logger.info("[+]调用接口获取月还款额，开始");
		// 调用接口获取月还款额
		XmlResObj data = super.sendExternal("BSP_LN_GETYHKE_01");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}
		// 调用综服接口从body中获取月还款额
		Map<String,Object> body = data.getBody();
		String yhke=body.get("yhke")==null?"":body.get("yhke").toString();
		logger.info("[-]调用接口获取月还款额，结束");
		
    	/*出口参数赋值开始*/
		setOutParam("yhke",yhke);//月还款额
    	/*出口参数赋值结束*/

    	return 0;
   }

}
