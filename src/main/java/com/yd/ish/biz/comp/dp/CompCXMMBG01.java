package com.yd.ish.biz.comp.dp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;

 /**
 * 名称：CompCXMMBG01
 * <p>功能：查询密码变更<br>
 * @brief 功能简述  修改查询密码
 * @author 许永峰
 * @version 版本号	修改人	修改时间		地点	原因	
 * @note	0.1		许永峰	2018年7月27	长春 	创建
 */
@Component("CompCXMMBG01")
public class CompCXMMBG01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompCXMMBG01.class);

	@Override
	public int execute() {

		logger.info("[+]调用接口提交修改查询密码信息开始");
		XmlResObj data = super.sendExternal("BSP_DP_CXMMBG_01");
		XmlResHead head = data.getHead();
		if( !XmlResHead.TR_SUCCESS.equals(head.getParticular_code()) ){
			throw new TransSingleException(head.getParticular_info()); 	
		}
		logger.debug("[-]调用接口提交修改查询密码信息结束");

    	return 0;
   }

}
