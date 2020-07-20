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
 * 名称：CompDWTSXX01
 * <p>
 * 功能：获取单位托收信息<br>
 * 
 * @brief 获取单位收托信息
 * @author 许永峰
 * @version 版本号 修改人 修改时间 地点 原因
 * @note 0.1 许永峰 2018年6月19日 长春 新建
 */
@Component("CompDWTSXX01")
public class CompDWTSXX01 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompDWTSXX01.class);

	@Override
	public int execute() {

		/* 入口参数赋值开始 */

		/* 入口参数赋值结束 */

		// 调用接口获取单位托收信息
		logger.info("[+]调用接口获取单位托收信息开始");
		XmlResObj data = super.sendExternal("BSP_DP_DWTSXX_01");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}
		Map<String,Object> body = data.getBody();
		
		
		String wtskxybh = returnString(body.get("wtskxybh"));
		String wtskxyzt = returnString(body.get("wtskxyzt"));
		String fkdwmc = returnString(body.get("fkdwmc"));
		String fkdwyhmc = returnString(body.get("fkdwyhmc"));
		String fkdwyhzh = returnString(body.get("fkdwyhzh"));
		String fkhtcjhh = returnString(body.get("fkhtcjhh"));
		String tsr = returnString(body.get("tsr"));
		String qyjbrq = returnString(body.get("qyjbrq"));
		String qyjbjg = returnString(body.get("qyjbjg"));
		String qyjbr = returnString(body.get("qyjbr"));
		logger.info("[-]调用接口获取单位托收信息结束");
		
		/* 出口参数赋值开始 */
		setOutParam("wtskxybh", wtskxybh);// 委托收款协议编号
		setOutParam("wtskxyzt", wtskxyzt);// 委托收款协议状态
		setOutParam("fkdwmc", fkdwmc);// 付款单位名称
		setOutParam("fkdwyhmc", fkdwyhmc);// 付款单位银行名称
		setOutParam("fkdwyhzh", fkdwyhzh);// 付款单位银行账号
		setOutParam("fkhtcjhh", fkhtcjhh);// 付款行同城交换号
		setOutParam("tsr", tsr);// 托收日
		setOutParam("qyjbrq", qyjbrq);// 签约经办日期
		setOutParam("qyjbjg", qyjbjg);// 签约经办机构
		setOutParam("qyjbr", qyjbr);// 签约经办人
		/* 出口参数赋值结束 */

		return 0;
	}
	
	public String returnString(Object str){
		return str ==null || str.toString().isEmpty() ? "" : str.toString();
	}
}
