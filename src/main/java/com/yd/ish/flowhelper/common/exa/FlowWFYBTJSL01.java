package com.yd.ish.flowhelper.common.exa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.workflow.FlowHelperI;

/**
 * 
 * 名称：FlowWFYBTJSL01.java
 * <p>
 * 功能： 异步批量提交示例功能流程助手<br>
 * 
 * @author 张洪超
 * @version 0.1 2018年1月30日 张洪超创建
 */
@Component
public class FlowWFYBTJSL01 implements FlowHelperI {

	private static final Logger logger = LoggerFactory.getLogger(FlowWFYBTJSL01.class);

	private static final String STEP_STEP1 = "step1";// 异步提交信息录入
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
		if (stepid.equals(STEP_STEP1)) {
			return out_step1(mainContext);
		}
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {

		return null;
	}

	private boolean out_step1(MainContext mainContext) {
		logger.debug("---------------");
		logger.debug("异步提交step1");
		// 调用批量异常获取接口
		DataPool dataPool = mainContext.getDataPool();
		dataPool.put("flag","2");
		TransEngine.getInstance().execute("TranPLYC02",mainContext);
		return true;
	}
}
