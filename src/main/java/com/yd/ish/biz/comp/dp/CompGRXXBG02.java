package com.yd.ish.biz.comp.dp;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;

 /**
 * 名称：CompGRXXBG02
 * <p>功能：提交个人基本信息<br>
 * @brief 功能简述 提交变更后的个人基本信息
 * @author 
 * @version 版本号	修改人	修改时间		地点		原因	
 * @note	0.1		许永峰	2018年8月1日	长春		创建
 */
@Component("CompGRXXBG02")
public class CompGRXXBG02 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompGRXXBG02.class);

	@Override
	public int execute() {

		logger.info("[+]调用接口提交个人基本信息变更开始");
		XmlResObj data = super.sendExternal("BSP_DP_GRXXBG_02");
		XmlResHead head = data.getHead();
		if( !XmlResHead.TR_SUCCESS.equals(head.getParticular_code()) ){
			throw new TransSingleException(head.getParticular_info()); 	
		}
		logger.debug("[-]调用接口提交个人基本信息变更结束");


    	return 0;
   }

}
