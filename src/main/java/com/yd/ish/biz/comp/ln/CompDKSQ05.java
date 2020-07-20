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
 * 名称：CompDKSQ05
 * <p>功能：获取贷款利率参数<br>
 * @brief 获取贷款利率参数
 * @author 柏慧敏
 * @version 0.1 2018年7月16日 柏慧敏创建
 * @note
 */
@Component("CompDKSQ05")
public class CompDKSQ05 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDKSQ05.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/

    	/*入口参数赋值结束*/

		logger.info("[+]调用接口获取贷款利率参数，开始");
		// 调用接口获取获取贷款利率参数
		XmlResObj data = super.sendExternal("BSP_LN_HQDKLL_01");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}
		// 调用综服接口从body中获取贷款利率参数
		Map<String,Object> body = data.getBody();
		String downFiveRate=body.get("downFiveRate").toString();
		String upFiveRate=body.get("upFiveRate").toString();
		String llfdbl=body.get("llfdbl").toString();
		logger.info("[-]调用接口获取获取贷款利率参数，结束");
		
    	/*出口参数赋值开始*/
		setOutParam("downFiveRate",downFiveRate);//五年以下利率
		setOutParam("upFiveRate",upFiveRate);//五年以上利率
		setOutParam("llfdbl",llfdbl);//利率浮动比例
    	/*出口参数赋值结束*/

    	return 0;
   }

}
