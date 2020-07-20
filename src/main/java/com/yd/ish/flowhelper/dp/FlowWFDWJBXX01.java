package com.yd.ish.flowhelper.dp;

import com.alibaba.druid.support.json.JSONUtils;
import org.springframework.stereotype.Component;

import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.workflow.FlowHelperI;

import java.util.Map;

/**
 * 
 * 名称：FlowWFDWJBXX01.java
 * <p>功能：单位基本信息查询 <br> 
 * @author 王赫
 * @version 0.1	2018年6月20日	王赫创建
 */
@Component
public class FlowWFDWJBXX01 implements FlowHelperI {

	private static final String STEP_STEP1 = "step1";

	/*
	 * 交易调用示例，trancode为具体交易代码
	 * TransEngine.getInstance().execute("trancode",mainContext);
	 */

	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		if(STEP_STEP1.equals(stepid)){
			return in_step1(mainContext);
		}
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
	/**
	 * 查询单位基本信息
	 * @param mainContext
	 * @return
	 */
	private boolean in_step1(MainContext mainContext){
		TransEngine.getInstance().execute("TranHQDWXX01",mainContext);
		return true;
	}
}
