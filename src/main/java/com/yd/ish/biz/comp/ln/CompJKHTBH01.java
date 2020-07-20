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
 * 名称：CompJKHTBH01
 * <p>
 * 功能：获取借款合同编号<br>
 * 
 * @brief 获取借款合同编号
 * @author 柏慧敏
 * @version 0.1 2018年6月15日 柏慧敏创建
 */
@Component("CompJKHTBH01")
public class CompJKHTBH01 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompJKHTBH01.class);

	@Override
	public int execute() {

		/* 入口参数赋值开始 */

		/* 入口参数赋值结束 */
		logger.info("[+]调用接口获取借款合同编号开始");
		XmlResObj data = super.sendExternal("BSP_LN_HQJKHTBH_01");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}
		Map<String, Object> body = data.getBody();
		// 返回的借款合同编号信息，多个时以逗号分隔的形式返回
		String fhxx = body.get("jkhtbh") == null ? "" : body.get("jkhtbh").toString();
		// 若返回的批量信息为空，提示用户："没有查询到您的贷款信息"
		if ("".equals(fhxx)) {
			logger.error("未获取到借款编号");
			throw new TransSingleException("没有查询到您的贷款信息");
		}
		// 借款合同编号
		StringBuffer jkhtbhsz = new StringBuffer();
		// 将返回借款合同编号以逗号拆分成数组
		String[] fhxxsz = fhxx.split(",");
		if (fhxxsz != null) {
			// 借款合同编号数量
			int fhxxszlength = fhxxsz.length;
			for (int i = 0; i < fhxxsz.length; i++) {
				// 获取批量信息中的借款合同编号，用逗号进行拼接，拼接后返回至页面上
				jkhtbhsz.append(fhxxsz[i]);
				// 借款合同编号多于一个时，用逗号进行分隔
				if (i < fhxxszlength - 1) {
					jkhtbhsz.append(",");
				}
			}
		}

		logger.info("[-]调用接口获取借款合同编号结束");
		/* 出口参数赋值开始 */
		setOutParam("jkhtbhsz", jkhtbhsz);// 借款合同编号
		/* 出口参数赋值结束 */

		return 0;
	}

}
