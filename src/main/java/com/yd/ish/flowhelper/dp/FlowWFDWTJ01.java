package com.yd.ish.flowhelper.dp;

import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.yd.workflow.FlowHelperI;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;

/**
 * 名称：FlowWFDWTJ01
 * <p>功能：退缴流程助手<br>
 * @author 柏慧敏
 * @version 0.1	2018年9月3日	柏慧敏创建
 * @note
 */
@Component
public class FlowWFDWTJ01 implements FlowHelperI {

	private static final String STEP_STEP1 = "step1";// 退缴

	/*
	 * 交易调用示例，trancode为具体交易代码
	 * TransEngine.getInstance().execute("trancode",mainContext);
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
		if (stepid.equals(STEP_STEP1)) {
			return out_step1(mainContext);
		}
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		DataPool pool = mainContext.getDataPool();
		// 根据个人账号获取个人信息
		if ("CMD01".equals(task)) {
			pool.put("flag", "hqgrxx");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranGRJBXX02", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		// 校验退缴 批量信息
		if ("CMD02".equals(task)) {
			pool.put("flag", "jyxx");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranDWTJ01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		return null;
	}
	
	private boolean in_step1(String type,MainContext mainContext){
		//获取归集行信息
		TransEngine.getInstance().execute("TranHQGJYH01",mainContext);
		return true;
	}
	
	private boolean out_step1(MainContext mainContext) {
		DataPool pool = mainContext.getDataPool();
		pool.put("flag", "tjxx");
		TransEngine.getInstance().execute("TranDWTJ01", mainContext);
		return true;
	}
}
