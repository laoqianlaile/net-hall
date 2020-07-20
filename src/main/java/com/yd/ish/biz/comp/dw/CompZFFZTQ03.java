package com.yd.ish.biz.comp.dw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;

 /**
 * 名称：CompZFFZTQ03
 * <p>功能：提交租房提取信息<br>
 * @brief 提交租房提取信息
 * @author 柏慧敏
 * @version 0.1 2018年8月14日 柏慧敏创建
 * @note
 */
@Component("CompZFFZTQ03")
public class CompZFFZTQ03 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompZFFZTQ03.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/

    	/*入口参数赋值结束*/
		
		logger.info("[+]调用接口提交租房提取信息开始");
		// 调用接口提交租房提取信息
		XmlResObj data = super.sendExternal("BSP_DW_TJZFTQXX_01");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}
		
		logger.info("[-]调用接口提交租房提取信息结束");
    	/*出口参数赋值开始*/

    	/*出口参数赋值结束*/

    	return 0;
   }

}
