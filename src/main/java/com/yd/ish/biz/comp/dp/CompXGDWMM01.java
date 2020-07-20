package com.yd.ish.biz.comp.dp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;

 /**
 * 名称：CompXGDWMM01
 * <p>功能：单位密码修改<br>
 * @brief 提交密码修改信息
 * @author 柏慧敏
 * @version 0.1	2018年5月24日	柏慧敏创建
 * @note
 */
@Component("CompXGDWMM01")
public class CompXGDWMM01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompXGDWMM01.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/
		
    	/*入口参数赋值结束*/
		logger.info("[+]调用接口提交修改密码信息开始");
		XmlResObj data = super.sendExternal("BSP_DP_XGDWMM_01");
		XmlResHead head = data.getHead();
		if( !XmlResHead.TR_SUCCESS.equals(head.getParticular_code()) ){
			throw new TransSingleException(head.getParticular_info()); 	
		}
		logger.debug("[-]调用接口提交修改密码信息结束");
    	/*出口参数赋值开始*/

    	/*出口参数赋值结束*/

    	return 0;
   }

}
