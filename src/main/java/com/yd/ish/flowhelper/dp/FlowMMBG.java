package com.yd.ish.flowhelper.dp;

import com.yd.svrplatform.util.DataPool;
import dm.jdbc.filter.stat.util.JSONUtils;
import org.springframework.stereotype.Component;

import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.workflow.FlowHelperI;

import java.util.Map;

@Component
public class FlowMMBG implements FlowHelperI  {
private static final String STEP_STEP1="step1";//
	
	/*交易调用示例，trancode为具体交易代码
	TransEngine.getInstance().execute("trancode",mainContext);
	*/
	
	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		if(stepid.equals(STEP_STEP1)){
			return in_step1(type,mainContext);
		}
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
		//短信验证
		Map dxmap = TransEngine.getInstance().execute("TranDXYZM",mainContext);
		//提交验证
		TransEngine.getInstance().execute("TranMMBG",mainContext);
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		DataPool context = MainContext.currentMainContext().getDataPool();
		//获取短信信息
		if ("getDxyzm".equals(task)) {
			context.put("dxtype","1");
			Map map = TransEngine.getInstance().execute("TranDXYZM", mainContext);
			return JSONUtils.toJSONString(map);
		}
		return null;
	}
	
	private boolean in_step1(String type,MainContext mainContext){
		//个人密码修改
		TransEngine.getInstance().execute("TranMMBG01",mainContext);
		return true;
	}
}
