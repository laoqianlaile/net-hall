package com.yd.ish.biz.comp.dw;

import com.yd.basic.expression.IshExpression;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;

 /**
 * 名称：CompGMZZZF01
 * <p>功能：购买自住住房提取提交<br>
 * @brief 购买自住住房提取提交
 * @author 柏慧敏
 * @version 0.1 2018年8月10日 柏慧敏创建
 * @note
 */
@Component("CompGMZZZF01")
public class CompGMZZZF01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompGMZZZF01.class);
@Autowired
	 ParamConfigImp paramConfigImp;
	@Override
	public int execute() {

		/*入口参数赋值开始*/
		String ygfrgx = getString("ygfrgx");
    	/*入口参数赋值结束*/
		// 如果购房人为本人，将登录人的信息赋值给购房人
		if(ygfrgx.equals(paramConfigImp.getValByMask("bsp.dw.type","1"))){
			super.setValue("gfrxm",IshExpression.getRealUserExtInfo("xingming"));
			super.setValue("gfrzjhm",IshExpression.getRealUserExtInfo("zjhm"));
		}
		logger.info("[+]调用接口提交购买自住住房提取信息开始");
		// 调用接口提交购买自住住房提取信息
		XmlResObj data = super.sendExternal("BSP_DW_GMZZZF_01");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}
		
		logger.info("[-]调用接口提交购买自住住房提取信息结束");

    	/*出口参数赋值开始*/

    	/*出口参数赋值结束*/

    	return 0;
   }

}
