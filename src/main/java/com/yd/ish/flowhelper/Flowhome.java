package com.yd.ish.flowhelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.workflow.FlowHelperI;

@Component
public class Flowhome implements FlowHelperI {

	private static final Logger logger = LoggerFactory.getLogger(Flowhome.class);

	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		logger.debug("type=" + type);

		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {

		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		logger.info("======home");
		return "";
	}
}
