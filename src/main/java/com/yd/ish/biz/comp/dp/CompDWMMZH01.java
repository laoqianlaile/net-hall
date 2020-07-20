package com.yd.ish.biz.comp.dp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;

 /**
 * 名称：CompDWMMZH01
 * <p>功能：单位登录密码找回<br>
 * @brief 功能简述 找回单位登录密码
 * @author 
 * @version 版本号	修改人	修改时间			地点	原因	
 * @note	0.1		许永峰	2018年9月12日		长春	创建
 */
@Component("CompDWMMZH01")
public class CompDWMMZH01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDWMMZH01.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/
		/*入口参数赋值结束*/
		logger.info("[+]调用提交单位登录密码接口信息开始");
		XmlResObj data = super.sendExternal("BSP_DP_DWMMZH_01");
		XmlResHead head = data.getHead();
		if( !XmlResHead.TR_SUCCESS.equals(head.getParticular_code()) ){
			throw new TransSingleException(head.getParticular_info()); 	
		}
		logger.debug("[-]调用提交单位登录密码接口信息结束");

    	/*出口参数赋值开始*/

    	/*出口参数赋值结束*/

    	return 0;
   }

}
