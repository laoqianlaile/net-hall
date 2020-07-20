package com.yd.ish.biz.comp.dw;

import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;

import java.util.Map;

/**
 * 名称：CompHQSBJG01
 * <p>功能：查询申报机构<br>
 * @brief 查询申报机构
 * @author 柏慧敏
 * @version 0.1	2018年10月22日	柏慧敏创建
 * @note
 */
@Component("CompHQSBJG01")
public class CompHQSBJG01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompHQSBJG01.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/

    	/*入口参数赋值结束*/

		// 调用接口查询申报机构
		XmlResObj data = super.sendExternal("BSP_GG_HQSBJG_01", false);
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			logger.error("查询申报机构接口：BSP_GG_HQSBJG_01异常：" + head.getParticular_info());
			throw new TransSingleException(head.getParticular_code(), head.getParticular_info());
		}
		Map<String, Object> body = data.getBody();
		String zdjg = body.get("zdjg") == null ? "" : body.get("zdjg").toString();

    	/*出口参数赋值开始*/
		setOutParam("zdjg",zdjg);//指定机构
    	/*出口参数赋值结束*/

    	return 0;
   }

}
