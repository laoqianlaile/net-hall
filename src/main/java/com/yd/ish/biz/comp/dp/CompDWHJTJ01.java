package com.yd.ish.biz.comp.dp;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.ReadProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 名称：CompDWHJTJ01.java
 * <p>
 * 功能：单位汇缴-单位汇缴信息提交 <br>
 *
 * @author 王赫
 * @version 0.1 2018年8月28日 王赫创建
 */
@Component("CompDWHJTJ01")
public class CompDWHJTJ01 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompDWHJTJ01.class);
	/** 启用凭证系统标记 **/
	private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");
	@Autowired
	DP077Service dp077service;

	@Override
	public int execute() {
		/* 入口参数赋值开始 */
		/* 入口参数赋值结束 */
		logger.info("[+]获取单位汇缴信息接口BSP_DP_HQDWHJTJ_01开始...");
		XmlResObj data = super.sendExternal("BSP_DP_HQDWHJTJ_01");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_code(), head.getParticular_info());
		}
		Map<String, Object> body = data.getBody();
		String	skyhzh = body.get("skyhzh") == null ? "" : body.get("skyhzh").toString();
		String	skyhmc = body.get("skyhmc") == null ? "" : body.get("skyhmc").toString();
		String	jbjg = body.get("jbjg") == null ? "" : body.get("jbjg").toString();
		String	dwjcje = body.get("dwjcje") == null ? "" : body.get("dwjcje").toString();
		String	grjcje = body.get("grjcje") == null ? "" : body.get("grjcje").toString();
		String	jclx = body.get("jclx") == null ? "" : body.get("jclx").toString();
		String	rzlsh = body.get("jbjg") == null ? "" : body.get("rzlsh").toString();
		String	djh = body.get("djh") == null ? "" : body.get("djh").toString();
		String	pzfilename = body.get("filename") == null ? "" : body.get("filename").toString();
		logger.info("[-]获取单位汇缴信息接口BSP_DP_HQDWHJTJ_01结束...");
		/* 出口参数赋值开始 */
		setOutParam("skyhzh", skyhzh);// 收款银行账号
		setOutParam("skyhmc", skyhmc);// 收款银行名称
		setOutParam("jbjg", jbjg);// 经办机构
		setOutParam("dwjcje", dwjcje);// 单位汇缴金额
		setOutParam("grjcje", grjcje);// 个人汇缴金额
		setOutParam("jclx", jclx);// 缴存类型
		setOutParam("rzlsh", rzlsh);// 入账流水号
		setOutParam("djh", djh);// 登记号
		if ("1".equals(pzsystemFlag)) { // 启用凭证系统时设置出口参数
			setOutParam("filename", pzfilename);// 凭证文件名称
		}
		/* 出口参数赋值结束 */

		return 0;
	}
}
