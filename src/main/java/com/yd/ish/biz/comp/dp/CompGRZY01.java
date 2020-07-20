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
 * 名称：CompGRZY01
 * <p>功能：获取单位托管户账号<br>
 * @brief 获取单位托管户账号
 * @author 柏慧敏
 * @version 0.1 2018年8月20日 柏慧敏创建
 * @note
 */
@Component("CompGRZY01")
public class CompGRZY01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompGRZY01.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/
		
    	/*入口参数赋值结束*/
		
		logger.info("[+]调用接口获取单位托管户账号开始");
		XmlResObj data = super.sendExternal("BSP_DP_GETTGHZH_01");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}
		Map<String,Object> body = data.getBody();
		
		String tghzh=body.get("tghzh")==null?"":body.get("tghzh").toString();
		
		logger.info("[+]调用接口获取单位托管户账号结束");
    	/*出口参数赋值开始*/
		setOutParam("tghzh",tghzh);//托管户账号
    	/*出口参数赋值结束*/

    	return 0;
   }

}
