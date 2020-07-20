package com.yd.ish.flowhelper.dp;

import org.springframework.stereotype.Component;

import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.workflow.FlowHelperI;

/**
 * 名称：FlowWFDWGZCX01
 * <p>
 * 功能： 单位挂账信息查询<br>
 * 
 * @brief 功能简述 单位挂账信息查询
 * @author 许永峰
 * @version 版本号 修改人 修改时间 地点 原因
 * @note 0.1 许永峰 2018年6月20日 长春 新建
 */
@Component
public class FlowWFDWGZCX01 implements FlowHelperI {

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
		if ("cmdCX".equals(task)) {
			TransEngine.getInstance().execute("TranDWGZCX01", mainContext);
		}

		return null;
	}
}
