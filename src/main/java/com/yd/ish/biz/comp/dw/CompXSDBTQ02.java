package com.yd.ish.biz.comp.dw;

import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;

 /**
 * 名称：CompXSDBTQ02
 * <p>功能：享受低保提取提交<br>
 * @brief 享受低保提取提交
 * @author 柏慧敏
 * @version 0.1	2019年10月17日	柏慧敏创建
 * @note
 */
@Component("CompXSDBTQ02")
public class CompXSDBTQ02 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompXSDBTQ02.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/

    	/*入口参数赋值结束*/

		logger.info("[+]调用接口提交享受低保提取信息开始");
		// 调用接口提交物业费提取信息
		XmlResObj data = super.sendExternal("BSP_DW_TJXSDBTQXX_01");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}

		logger.info("[-]调用接口提交享受低保提取信息结束");

    	/*出口参数赋值开始*/

    	/*出口参数赋值结束*/

    	return 0;
   }

}
