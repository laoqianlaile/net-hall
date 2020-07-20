package com.yd.ish.biz.comp.dp;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.ReadProperty;

/**
 * 名称：
 * <p>
 * 功能：获取公积金账户基本信息<br>
 * 
 * @brief 获取公积金账户基本信息 TODO
 * @author 柏慧敏
 * @version 0.1 2018年5月31日 柏慧敏创建
 * @note
 */
@Component("CompZHJBXX01")
public class CompZHJBXX01 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompZHJBXX01.class);
	/** 启用凭证系统标记 **/
	private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");

	@Override
	public int execute() {

		/* 入口参数赋值开始 */

		/* 入口参数赋值结束 */

		// 调用接口获取公积金账户基本信息
		logger.info("[+]调用接口获取公积金账户基本信息开始");
		XmlResObj data = super.sendExternal("BSP_DP_GETGRXX_01");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}
		Map<String, Object> body = data.getBody();

		String grjcjs = body.get("grjcjs") == null ? "" : body.get("grjcjs").toString();
		String dwjcbl = body.get("dwjcbl") == null ? "" : body.get("dwjcbl").toString();
		String grjcbl = body.get("grjcbl") == null ? "" : body.get("grjcbl").toString();
		String dwyjce = body.get("dwyjce") == null ? "" : body.get("dwyjce").toString();
		String gryjce = body.get("gryjce") == null ? "" : body.get("gryjce").toString();
		String yjce = body.get("yjce") == null ? "" : body.get("yjce").toString();
		String grzhye = body.get("grzhye") == null ? "" : body.get("grzhye").toString();
		String jzny = body.get("jzny") == null ? "" : body.get("jzny").toString();
		String grzhzt = body.get("grzhzt") == null ? "" : body.get("grzhzt").toString();
		String khrq = body.get("khrq") == null ? "" : body.get("khrq").toString();
		String jcyh = body.get("jcyh") == null ? "" : body.get("jcyh").toString();
		String glyh = body.get("glyh") == null ? "" : body.get("glyh").toString();
		String glyhkh = body.get("glyhkh") == null ? "" : body.get("glyhkh").toString();
		String djje = body.get("djje") == null ? "" : body.get("djje").toString();
		String kyye = body.get("kyye") == null ? "" : body.get("kyye").toString();
		String sjhm = body.get("sjhm") == null ? "" : body.get("sjhm").toString();
		String xingming = body.get("xingming") == null ? "" : body.get("xingming").toString();
		logger.info("[-]调用接口获取公积金账户基本信息结束");

		/* 出口参数赋值开始 */
		setOutParam("grjcjs", grjcjs); // 缴存基数
		setOutParam("dwjcbl", dwjcbl); // 单位缴存比例
		setOutParam("grjcbl", grjcbl); // 个人缴存比例
		setOutParam("dwyjce", dwyjce); // 单位月缴存额
		setOutParam("gryjce", gryjce); // 个人月缴存额
		setOutParam("yjce", yjce); // 月缴存额
		setOutParam("grzhye", grzhye); // 个人账户余额
		setOutParam("jzny", jzny); // 缴至年月
		setOutParam("grzhzt", grzhzt); // 个人账户状态
		setOutParam("khrq", khrq); // 开户日期
		setOutParam("jcyh", jcyh); // 缴存银行
		setOutParam("glyh", glyh); // 关联银行
		setOutParam("glyhkh", glyhkh); // 关联银行卡号
		setOutParam("djje", djje); // 冻结金额
		setOutParam("kyye", kyye); // 可用余额
		setOutParam("sjhm", sjhm); // 手机号码
		setOutParam("xingming", xingming); // 姓名
		/* 出口参数赋值结束 */

		return 0;
	}

}
