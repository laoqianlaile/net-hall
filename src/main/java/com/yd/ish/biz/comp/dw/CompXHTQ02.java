package com.yd.ish.biz.comp.dw;

import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;

 /**
 * 名称：CompXHTQ02
 * <p>功能：销户提取提交<br>
 * @brief 销户提取提交
 * @author 柏慧敏
 * @version 0.1	2019年9月23日	柏慧敏创建
 * @note
 */
@Component("CompXHTQ02")
public class CompXHTQ02 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompXHTQ02.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/

    	/*入口参数赋值结束*/

		logger.info("[+]调用接口提交销户提取信息开始");
		// 调用接口提交租房提取信息
		XmlResObj data = super.sendExternal("BSP_DW_TJXHTQXX_01");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}

		logger.info("[-]调用接口提交销户提取信息结束");
    	/*出口参数赋值开始*/

    	/*出口参数赋值结束*/

    	return 0;
   }

}
