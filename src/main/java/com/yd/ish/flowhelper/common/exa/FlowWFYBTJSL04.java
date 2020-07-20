package com.yd.ish.flowhelper.common.exa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.jdbc.PersistentBiz;
import com.yd.workflow.FlowHelperI;

/**
 * 
 * 名称：FlowWFYBTJSL04.java
 * <p>
 * 功能：批量异常文件处理示例流程助手<br>
 * 
 * @author 张洪超
 * @version 0.1 2018年1月30日 张洪超创建
 */
@Component
public class FlowWFYBTJSL04 implements FlowHelperI {

	private static final Logger logger = LoggerFactory.getLogger(FlowWFYBTJSL04.class);

	private static final String STEP_STEP1 = "step1";// 批量异常文件处理信息录入
	/*
	 * 交易调用示例，trancode为具体交易代码
	 * TransEngine.getInstance().execute("trancode",mainContext);
	 */
	@Autowired
	PersistentBiz persistentBiz;

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
		logger.debug("-----调用批量异常文件获取接口----------");
		// 调用批量异常文件获取接口
		TransEngine.getInstance().execute("TranPLYC03",mainContext);
		return true;
	}
}
