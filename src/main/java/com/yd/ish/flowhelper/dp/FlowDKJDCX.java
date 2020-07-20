package com.yd.ish.flowhelper.dp;

import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.workflow.FlowHelperI;
import org.springframework.stereotype.Component;

/**
 * 名称：FlowDKJDCX
 * <p>
 * 功能： 贷款进度查询<br>
 * 
 * @brief 功能简述 贷款进度查询
 * @author 王书凡
 * @version 版本号 修改人 修改时间 地点 原因
 * @note 0.1 wsf 2020年04月15日 贵港新增
 */
@Component
public class FlowDKJDCX implements FlowHelperI {

	/*
	 * 交易调用示例，trancode为具体交易代码
	 * TransEngine.getInstance().execute("trancode",mainContext);
	 */

	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {

		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {

		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		if ("cmdDKJDCX".equals(task)) {
//			TransEngine.getInstance().execute("TranDKJDCX", mainContext);
			TransEngine.getInstance().execute("TranLoanProgress", mainContext);
		}

		return null;
	}
}
