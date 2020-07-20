package com.yd.ish.flowhelper.dp;

import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.workflow.FlowHelperI;
import dm.jdbc.filter.stat.util.JSONUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 名称：FlowWFDWGZCX01
 * <p>
 * 功能： 贷款预审<br>
 *
 */
@Component
public class FlowDKYS implements FlowHelperI {

	/*
	 * 交易调用示例，trancode为具体交易代码
	 * TransEngine.getInstance().execute("trancode",mainContext);
	 */

	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {

//		TransEngine.getInstance().execute("TranGRJBXX01",mainContext);
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {

		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {

		return null;
	}
}
