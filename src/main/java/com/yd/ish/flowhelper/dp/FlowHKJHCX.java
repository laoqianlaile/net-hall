package com.yd.ish.flowhelper.dp;

import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.workflow.FlowHelperI;
import org.springframework.stereotype.Component;

/**
 * 名称：FlowHKJHCX
 * <p>
 * 功能： 还款计划查询<br>
 * 
 * @brief 功能简述 还款计划查询
 * @author 许永峰
 * @version 版本号 修改人 修改时间 地点 原因
 * @note 0.1 ypf 2020年03月20日 贵港新增
 */
@Component
public class FlowHKJHCX implements FlowHelperI {

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
		if("dkjbxx".equals(task)){
			TransEngine.getInstance().execute("TranGRDKJBXXCX", mainContext);
		}
		if ("hkjh".equals(task)) {
			TransEngine.getInstance().execute("TranHKJHCX", mainContext);
		}

		return null;
	}

}
