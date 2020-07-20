package com.yd.ish.flowhelper.dp;

import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.workflow.FlowHelperI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class FlowJCLDHTTQ implements FlowHelperI{
	private static Logger logger = LoggerFactory.getLogger(FlowJCLDHTTQ.class);
	private static final String STEP_ONE="step1";
	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		if (stepid.equals(STEP_ONE)) {
			TransEngine.getInstance().execute("TranGRXXCX", mainContext);
		}
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
		if(stepid.equals(STEP_ONE)) {
			TransEngine.getInstance().execute("TranXXHTQ1", mainContext);
		}
		if(stepid.equals("step2")) {
			TransEngine.getInstance().execute("TranDXYZM",mainContext);
			TransEngine.getInstance().execute("TranXXHTQ2", mainContext);
		}
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		if ("dxyzm".equals(task)) {
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranDXYZM",mainContext);
			return JsonUtil.getJsonString(ret);
		}
		return null;
	}

}
