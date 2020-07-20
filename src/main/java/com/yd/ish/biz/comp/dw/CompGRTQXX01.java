package com.yd.ish.biz.comp.dw;

import java.util.HashMap;
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
 * 
 * 名称：获取个人提取信息CompGRTQXX01.java
 * <p>
 * 功能： <br>
 * 
 * @author 王赫
 * @version 0.1 2018年8月01日 王赫创建
 */
@Component("CompGRTQXX01")
public class CompGRTQXX01 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompGRTQXX01.class);

	@Override
	public int execute() {

		/* 入口参数赋值开始 */
		/* 入口参数赋值结束 */
		XmlResObj data = super.sendExternal("BSP_DW_GETGRTQXX_01", false);
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			String errMsg = "code=[" + head.getParticular_code() + "]，info=[" + head.getParticular_info() + "]。";
			logger.info("[-]调用获取个人提取信息接口BSP_DW_GETGRTQXX_01失败" + errMsg);
			throw new TransSingleException(head.getParticular_code(), head.getParticular_info());
		}
		Map<String, Object> body = data.getBody();
		String grzhzt = body.get("grzhzt") == null ? "" : body.get("grzhzt").toString();
		String hjbz = body.get("hjbz") == null ? "" : body.get("hjbz").toString();
		String grzhye = body.get("grzhye") == null ? "" : body.get("grzhye").toString();
		String sfydk = body.get("sfydk") == null ? "" : body.get("sfydk").toString();
		String ssjg = body.get("ssjg") == null ? "" : body.get("ssjg").toString();
		String xhtqyy = body.get("xhtqyy") == null ? "" : body.get("xhtqyy").toString();
		String sctqrq = body.get("sctqrq") == null ? "" : body.get("sctqrq").toString();
		String sjhm = body.get("sjhm") == null ? "" : body.get("sjhm").toString();
		String djje = body.get("djje") == null ? "" : body.get("djje").toString();
		String kyye = body.get("kyye") == null ? "" : body.get("kyye").toString();

		/* 出口参数赋值开始 */
		setOutParam("grzhzt",grzhzt);// 个人账户状态
		setOutParam("hjbz", hjbz);// 户籍标志
		setOutParam("grzhye", grzhye);// 账户余额
		setOutParam("sfydk", sfydk);// 是否有贷款
		setOutParam("ssjg", ssjg);// 所属机构
		setOutParam("xhtqyy", xhtqyy);// 销户提取原因
		setOutParam("sctqrq", sctqrq);// 上次提取日期
		setOutParam("sjhm", sjhm);// 手机号码
		setOutParam("djje", djje);// 冻结金额
		setOutParam("kyye", kyye);// 可用余额
		/* 出口参数赋值结束 */

		return 0;
	}

}
