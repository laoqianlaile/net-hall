package com.yd.ish.biz.comp.dp;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;

 /**
 * 名称：CompJCJSTZ01
 * <p>功能：获取缴存基数调低标志<br>
 * @brief 获取缴存基数调低标志
 * @author 许永峰
 * @version 版本号	修改人	修改时间	  	地点		原因	
 * @note	0.1		许永峰	2018年7月3日	长春		创建
 */
@Component("CompJCJSTZ01")
public class CompJCJSTZ01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompJCJSTZ01.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/
		//String dwzh=getString("dwzh");//单位账号
    	/*入口参数赋值结束*/
	
		logger.debug("-----调用接口获取缴存基数调低标志开始-----");
		
		//调用接口获取缴存基数调低标志
		XmlResObj data = super.sendExternal("BSP_DP_JCJSTZ_02");
		XmlResHead head = data.getHead();
		if( !XmlResHead.TR_SUCCESS.equals(head.getParticular_code()) ){
			throw new TransSingleException(head.getParticular_info()); 	
		}
		Map<String,Object> body = data.getBody();
		String tdflag = body.get("tdflag")==null ? "":body.get("tdflag").toString();
		logger.debug("-----调用接口获取缴存基数调低标志结束-----");

    	/*出口参数赋值开始*/
		setOutParam("tdflag",tdflag);//调低标志
    	/*出口参数赋值结束*/

    	return 0;
   }

}
