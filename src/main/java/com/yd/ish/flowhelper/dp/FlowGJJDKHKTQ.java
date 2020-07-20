package com.yd.ish.flowhelper.dp;

import java.util.HashMap;
import java.util.Map;

import com.yd.svrplatform.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.workflow.FlowHelperI;
/**
 * 名称：FlowGJJDKHKTQ
 * <p>
 * 功能： 贷款还贷提取<br>
 * 
 * @brief 功能简述  贷款还贷提取
 * @author 王书凡
 * @version 版本号 修改人 修改时间 地点 原因
 * @note 0.1 wsf 2020年04月20日 贵港新增
 */
@Component
public class FlowGJJDKHKTQ implements FlowHelperI{
	private static final Logger logger = LoggerFactory.getLogger(FlowGJJDKHKTQ.class);
	private static final String STEP_ONE = "step1";
	
	/*
	 * 交易调用示例，trancode为具体交易代码
	 * TransEngine.getInstance().execute("trancode",mainContext);
	 */

	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		if(stepid.equals(STEP_ONE)) {
			TransEngine.getInstance().execute("TranTQJYBM", mainContext);
		}
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
		if(stepid.equals(STEP_ONE)) {
			TransEngine.getInstance().execute("TranGJJDKHKTQ1", mainContext);
		}
		if(stepid.equals("step2")) {
			TransEngine.getInstance().execute("TranDXYZM",mainContext);
			TransEngine.getInstance().execute("TranGJJDKHKTQ2", mainContext);
		}
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		if ("cmdHDTQJESS".equals(task)) {
			Map<String,Object> reMap = TransEngine.getInstance().execute("TranHDTQJESS", mainContext);
			return JSON.toJSONString(reMap);
		}
		if ("dxyzm".equals(task)) {
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranDXYZM",mainContext);
			return JsonUtil.getJsonString(ret);
		}
		return null;
	}
}
