package com.yd.ish.biz.comp.dp;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.ReadProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 名称：CompDWJCZM01.java
 * <p>
 * 功能：获取单位缴存证明信息 <br>
 * 
 * @author 王赫
 * @version 0.1 2018年8月14日 王赫创建
 */
@Component("CompDWJCZM01")
public class CompDWJCZM01 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompDWJCZM01.class);
	/** 读核心返回文件编码格式 */
	private static final String encoding = ReadProperty.getString("readfile_encoding_bsp");
	/** 启用凭证系统标记 **/
	private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");

	@Override
	public int execute() {

		// 入口参数赋值开始
		// 入口参数赋值结束
		logger.info("[+]调用获取单位缴存证明信息接口BSP_DP_DWJCZM_01开始");
		XmlResObj data = super.sendExternal("BSP_DP_DWJCZM_01");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			logger.error("调用获取单位缴存证明信息失败：" + head.getParticular_info());
			throw new TransSingleException(head.getParticular_code(), head.getParticular_info());
		}
		Map<String, Object> body = data.getBody();
		String khrq = body.get("khrq") == null ? "" : body.get("khrq").toString();
		String dwzhzt = body.get("dwzhzt") == null ? "" : body.get("dwzhzt").toString();
		String zhjg = body.get("zhjg") == null ? "" : body.get("zhjg").toString();
		String dwjcbl = body.get("dwjcbl") == null ? "" : body.get("dwjcbl").toString();
		String grjcbl = body.get("grjcbl") == null ? "" : body.get("grjcbl").toString();
		String jcqsny = body.get("jcqsny") == null ? "" : body.get("jcqsny").toString();
		String jzny = body.get("jzny") == null ? "" : body.get("jzny").toString();
		String zjly = body.get("zjly") == null ? "" : body.get("zjly").toString();
		String zhye = body.get("zhye") == null ? "" : body.get("zhye").toString();
		String fcrs = body.get("fcrs") == null ? "" : body.get("fcrs").toString();
		String zcrs = body.get("zcrs") == null ? "" : body.get("zcrs").toString();
		logger.debug("[-]调用获取单位缴存证明信息接口BSP_DP_DWJCZM_01结束");

		/* 出口参数赋值开始 */
		setOutParam("khrq", khrq);// 开户日期
		setOutParam("dwzhzt", dwzhzt);// 单位账户状态
		setOutParam("zhjg", zhjg);// 账户机构
		setOutParam("dwjcbl", dwjcbl);// 单位缴存比例
		setOutParam("grjcbl", grjcbl);// 个人缴存比例
		setOutParam("jcqsny", jcqsny);// 缴存起始年月
		setOutParam("jzny", jzny);// 缴至年月
		setOutParam("zjly", zjly);// 资金来源
		setOutParam("zhye", zhye);// 账户余额
		setOutParam("fcrs", fcrs);// 封存人数
		setOutParam("zcrs", zcrs);// 正常人数
		if ("1".equals(pzsystemFlag)) { // 启用凭证系统时设置出口参数
			String pzfilename = body.get("filename") == null ? "" : body.get("filename").toString();
			setOutParam("filename", pzfilename);// 凭证文件名称
		}
		/* 出口参数赋值结束 */

		return 0;
	}

}
