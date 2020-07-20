package com.yd.ish.biz.comp.dw;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;

 /**
 * 名称：CompZFFZTQ02
 * <p>功能：计算可提取金额<br>
 * @brief 计算可提取金额
 * @author 柏慧敏
 * @version 0.1 2018年8月14日 柏慧敏创建
 * @note
 */
@Component("CompZFFZTQ02")
public class CompZFFZTQ02 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompZFFZTQ02.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/
		
    	/*入口参数赋值结束*/
		
		logger.info("[+]调用接口计算可提取金额开始");
		// 调用接口计算可提取金额
		XmlResObj data = super.sendExternal("BSP_DW_JSKTQED_01");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}
		Map<String,Object> body = data.getBody();
		String ktqje=body.get("ktqje")==null?"":body.get("ktqje").toString();
		
		logger.info("[-]调用接口计算可提取金额结束");

    	/*出口参数赋值开始*/
		setOutParam("ktqje",ktqje);//可提取金额
    	/*出口参数赋值结束*/

    	return 0;
   }

}
