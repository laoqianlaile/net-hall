package com.yd.ish.biz.comp.ln;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;

/**
 * 名称：CompDKSS02
 * <p>
 * 功能：计算最高贷款额度年限<br>
 * 
 * @brief 计算最高贷款额度年限
 * @author 柏慧敏
 * @version 0.1 2018年6月22日 柏慧敏创建
 * @note
 */
@Component("CompDKSS02")
public class CompDKSS02 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompDKSS02.class);

	@Override
	public int execute() {

		/* 入口参数赋值开始 */
		
		/* 入口参数赋值结束 */
		
		// 调用接口获取贷款详细信息
		logger.info("[+]调用接口计算最高贷款额度年限开始");
		XmlResObj data = super.sendExternal("BSP_LN_DKSS_02");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}
		Map<String, Object> body = data.getBody();
		String zgdked = body.get("zgdked") == null ? "" : body.get("zgdked").toString();
		String zgdknx = body.get("zgdknx") == null ? "" : body.get("zgdknx").toString();

		logger.info("[-]调用接口计算最高贷款额度年限结束");

		/* 出口参数赋值开始 */
		setOutParam("zgdked", zgdked);// 最高贷款额度
		setOutParam("zgdknx", zgdknx);// 最高贷款年限
		/* 出口参数赋值结束 */

		return 0;
	}

}
