package com.yd.ish.biz.comp.dw;

import com.yd.basic.expression.IshExpression;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;

 /**
 * 名称：CompZJDXTQ02
 * <p>功能：建造、翻建、大修住房提取提交<br>
 * @brief 建造、翻建、大修住房提取提交
 * @author 柏慧敏
 * @version 0.1 2019年9月10日 柏慧敏创建
 * @note
 */
@Component("CompZJDXTQ02")
public class CompZJDXTQ02 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompZJDXTQ02.class);
	@Autowired
	ParamConfigImp paramConfigImp;

	@Override
	public int execute() {

		/*入口参数赋值开始*/
		String yxfrgx = getString("yxfrgx");
		/*入口参数赋值结束*/
		// 如果修房人为本人，将登录人的信息赋值给修房人
		if(yxfrgx.equals(paramConfigImp.getValByMask("bsp.dw.type","1"))){
			super.setValue("xfrxm",IshExpression.getRealUserExtInfo("xingming"));
			super.setValue("xfrzjhm",IshExpression.getRealUserExtInfo("zjhm"));
		}
		logger.info("[+]调用接口提交建造、翻建、大修住房提取信息开始");
		// 调用接口提交建造、翻建、大修住房提取信息
		XmlResObj data = super.sendExternal("BSP_DW_TJJZDXTQXX_01");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}

		logger.info("[-]调用接口提交建造、翻建、大修住房提取信息结束");

    	/*出口参数赋值开始*/

    	/*出口参数赋值结束*/

    	return 0;
   }

}
