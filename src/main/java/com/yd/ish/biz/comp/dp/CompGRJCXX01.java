package com.yd.ish.biz.comp.dp;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.druid.support.json.JSONUtils;
import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.org.util.CommonErrorCode;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.DataPool;

/**
 * 
 * 名称：CompGRJCXX01.java
 * <p>
 * 功能： 获取个人缴存信息 <br>
 * 
 * @author 王赫
 * @version 0.1 2018年6月8日 王赫创建
 */
@Component("CompGRJCXX01")
public class CompGRJCXX01 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompGRJCXX01.class);

	@Override
	public int execute() {

		/* 入口参数赋值开始 */
		String grzh = getString("grzh"); // 个人账号
		/* 入口参数赋值结束 */
		super.setValue("grzh", grzh);
		logger.info("[+]调用接口BSP_DP_GETGRXX_01获取个人缴存信息");
		XmlResObj data = super.sendExternal("BSP_DP_GETGRXX_01", false);
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}
		Map<String, Object> body = data.getBody();
		String xingming = body.get("xingming") == null ? "" : body.get("xingming").toString();
		String grjcjs = body.get("grjcjs") == null ? "" : body.get("grjcjs").toString();
		String yjce = body.get("yjce") == null ? "" : body.get("yjce").toString();
		String zjhm = body.get("zjhm") == null ? "" : body.get("zjhm").toString();
		String zjlx = body.get("zjlx") == null ? "" : body.get("zjlx").toString();
		String dwyjce = body.get("dwyjce") == null ? "" : body.get("dwyjce").toString();
		String gryjce = body.get("gryjce") == null ? "" : body.get("gryjce").toString();
		logger.info("[-]调用接口获取个人缴存信息成功");

		/* 出口参数赋值开始 */
		setOutParam("xingming", xingming);// 姓名
		setOutParam("grjcjs", grjcjs);// 个人缴存基数
		setOutParam("yjce", yjce);// 月缴存额
		setOutParam("zjhm", zjhm);// 证件号码
		setOutParam("zjlx", zjlx);// 证件类型
		setOutParam("dwyjce", dwyjce);// 单位月缴存额
		setOutParam("gryjce", gryjce);// 个人月缴存额
		/* 出口参数赋值结束 */

		return 0;
	}
}
