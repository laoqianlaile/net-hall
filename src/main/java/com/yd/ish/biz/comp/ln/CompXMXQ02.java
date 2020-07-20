package com.yd.ish.biz.comp.ln;

import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;

import java.util.Map;

/**
 * 名称：CompXMXQ02
 * <p>功能：获取协议有效时间参数<br>
 * @brief 获取协议有效时间参数
 * @author 柏慧敏
 * @version V0.1 柏慧敏 2019-06-05 长春 新建
 * @note
 */
@Component("CompXMXQ02")
public class CompXMXQ02 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompXMXQ02.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/

    	/*入口参数赋值结束*/
		//调用接口获取协议有限时间
		XmlResObj data = super.sendExternal("BSP_LN_HQXYYXSJ_01");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}
		Map<String, Object> body = data.getBody();
		String yxsj = body.get("yxsj") == null ? "" : body.get("yxsj").toString();

    	/*出口参数赋值开始*/
		setOutParam("yxsj",yxsj);//有效时间
    	/*出口参数赋值结束*/

    	return 0;
   }

}
