package com.yd.ish.biz.comp.dw;

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
 * 名称：CompXHTQ01
 * <p>功能：计算提取金额<br>
 * @brief 计算提取金额
 * @author 柏慧敏
 * @version 0.1	2019年9月23日	柏慧敏创建
 * @note
 */
@Component("CompXHTQ01")
public class CompXHTQ01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompXHTQ01.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/

    	/*入口参数赋值结束*/

		logger.info("[+]调用接口计算销户提取金额开始");
		// 调用接口提交租房提取信息
		XmlResObj data = super.sendExternal("BSP_DW_JSXHTQJE_01");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}
		Map<String,Object> body = data.getBody();
		String tqlx = body.get("tqlx") == null ? "" : body.get("tqlx").toString();
		String tqje = body.get("tqje") == null ? "" : body.get("tqje").toString();
		logger.info("[-]调用接口计算销户提取金额结束");

    	/*出口参数赋值开始*/
		setOutParam("tqlx",tqlx);//提取利息
		setOutParam("tqje",tqje);//提取金额
    	/*出口参数赋值结束*/

    	return 0;
   }

}
