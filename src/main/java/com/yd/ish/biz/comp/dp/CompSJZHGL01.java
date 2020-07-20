package com.yd.ish.biz.comp.dp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;

 /**
 * 名称：CompSJZHGL01
 * <p>功能：提交手机号码与公积金账号关联信息<br>
 * @brief 功能简述 提交手机号码与公积金账号关联
 * @author 
 * @version 版本号	修改人	修改时间			地点	原因	
 * @note    0.1		许永峰	2018年7月27日		长春	创建
 */
@Component("CompSJZHGL01")
public class CompSJZHGL01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompSJZHGL01.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/
		/*String grzh=getString("grzh");//个人账号
		String grgjjmm=getString("grgjjmm");//个人公积金密码
		String sjhm=getString("sjhm");//手机号码
*/    	/*入口参数赋值结束*/
		/*入口参数赋值结束*/
		logger.info("[+]调用接口提交手机号码与公积金账号关联信息开始");
		XmlResObj data = super.sendExternal("BSP_DP_SJZHGL_01");
		XmlResHead head = data.getHead();
		if( !XmlResHead.TR_SUCCESS.equals(head.getParticular_code()) ){
			throw new TransSingleException(head.getParticular_info()); 	
		}
		logger.debug("[-]调用接口提交手机号码与公积金账号关联信息结束");


    	/*出口参数赋值开始*/

    	/*出口参数赋值结束*/

    	return 0;
   }

}
