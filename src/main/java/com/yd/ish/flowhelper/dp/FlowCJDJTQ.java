package com.yd.ish.flowhelper.dp;

import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.workflow.FlowHelperI;
import dm.jdbc.filter.stat.util.JSONUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 出境定居提取
 *
 *
 */
@Component
public class FlowCJDJTQ implements FlowHelperI {

	/*
	 * 交易调用示例，trancode为具体交易代码
	 * TransEngine.getInstance().execute("trancode",mainContext);
	 */

	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		//调取个人基本信息
		TransEngine.getInstance().execute("TranGRJBXXCX",mainContext);
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
		//提交购房提取
		TransEngine.getInstance().execute("TranGFTQ", mainContext);

		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		/*DataPool context = MainContext.currentMainContext().getDataPool();
		//获取提取依据号
		if ("getTqyjh".equals(task)) {
			Map map = TransEngine.getInstance().execute("TranTQYJH", mainContext);
			return JSONUtils.toJSONString(map);
		}
		//获取提取的房屋信息
		if ("getGfxx".equals(task)) {
			Map map = TransEngine.getInstance().execute("TranGFXX", mainContext);
			return JSONUtils.toJSONString(map);
		}
		//获取提取的房屋信息
		if ("jejs".equals(task)) {
			Map map = TransEngine.getInstance().execute("TranJEJS", mainContext);
			map.put("grzhye",context.get("grzhye"));
			return JSONUtils.toJSONString(map);
		}
		//获取短信信息
		if ("getDxyzm".equals(task)) {
			context.put("dxtype","01");
			Map map = TransEngine.getInstance().execute("TranDXYZM", mainContext);
			return JSONUtils.toJSONString(map);
		}*/
		if ("dxyzm".equals(task)) {
			Map map = TransEngine.getInstance().execute("TranDXYZM", mainContext);
			return JSONUtils.toJSONString(map);
		}
		return null;
	}
}
