package com.yd.ish.biz.comp.dp;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.org.util.CommonErrorCode;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;

 /**
 * 名称：CompSJZHGL02
 * <p>功能：获取个人交易密码<br>
 * @brief 功能简述 获取个人交易密码
 * @author 
 * @version 版本号	修改人	修改时间			地点	原因	
 * @note    0.1		许永峰	2018年7月27日		长春	创建
 */
@Component("CompSJZHGL02")
public class CompSJZHGL02 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompSJZHGL02.class);

	@Override
	public int execute() {

		logger.info("[+]调用校验个人交易密码接口开始");
		XmlResObj data = super.sendExternal("BSP_DP_SJZHGL_02");
		XmlResHead head = data.getHead();
		if( !XmlResHead.TR_SUCCESS.equals(head.getParticular_code()) ){
			throw new TransSingleException(head.getParticular_info()); 	
		}
		Map<String, Object> body = data.getBody();
		String jymmCorrect = body.get("jymmCorrect") == null ? "" : body.get("jymmCorrect").toString() ;
		logger.debug("[-]调用校验个人交易密码接口结束");
    	/*出口参数赋值开始*/
		setOutParam("jymmCorrect", jymmCorrect);// 交易密码
    	/*出口参数赋值结束*/

    	return 0;
   }

}
