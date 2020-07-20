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
 * 名称：CompJCJSTZ04
 * <p>功能：获取月缴存额<br>
 * @brief 获取月缴存额
 * @author 许永峰
 * @version 版本号	修改人	 修改时间	  	地点		原因	
 * @note	0.1		许永峰   2018年12月25日	长春		创建
 */
@Component("CompJCJSTZ04")
public class CompJCJSTZ04 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompJCJSTZ04.class);

	@Override
	public int execute() {
	
		logger.debug("-----调用接口获取月缴存额开始-----");
		
		//调用接口获取月缴存额
		XmlResObj data = super.sendExternal("BSP_DP_JCJSTZ_03");
		XmlResHead head = data.getHead();
		if( !XmlResHead.TR_SUCCESS.equals(head.getParticular_code()) ){
			throw new TransSingleException(head.getParticular_info()); 	
		}
		Map<String,Object> body = data.getBody();
		String tzdwyjce = body.get("tzdwyjce")==null ? "":body.get("tzdwyjce").toString();
		String tzgryjce = body.get("tzgryjce")==null ? "":body.get("tzgryjce").toString();
		String tzhyjce = body.get("tzhyjce")==null ? "":body.get("tzhyjce").toString();
		logger.debug("-----调用接口获取月缴存额结束-----");

    	/*出口参数赋值开始*/
		setOutParam("tzdwyjce",tzdwyjce);//调整后单位月缴存额
		setOutParam("tzgryjce",tzgryjce);//调整后个人月缴存额
		setOutParam("tzhyjce",tzhyjce);//调整后月缴存额
		
    	/*出口参数赋值结束*/

    	return 0;
   }

}
