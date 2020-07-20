package com.yd.ish.flowhelper.ln;

import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.yd.workflow.FlowHelperI;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;

/**
 * 名称：FlowWFDKSS01
 * <p>
 * 功能：贷款试算流程助手<br>
 * 
 * @author 柏慧敏
 * @version 0.1 2018年6月22日 柏慧敏创建
 * @note
 */
@Component
public class FlowWFDKSS01 implements FlowHelperI {

	private static final String STEP_STEP1 = "step1";// 公积金贷款试算

	/*
	 * 交易调用示例，trancode为具体交易代码
	 * TransEngine.getInstance().execute("trancode",mainContext);
	 */

	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		if (stepid.equals(STEP_STEP1)) {
			return in_step1(type, mainContext);
		}
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {

		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		DataPool pool = mainContext.getDataPool();
		// 进行贷款试算，获取结果
		if ("dkss".equals(task)) {
			pool.put("flag", "dkss");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranDKSS01", mainContext);
			return JsonUtil.getJsonString(ret);
		} // 获取贷款账户信息
		else if ("hqdkxx".equals(task)) {
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranHQDKXX01", mainContext);
			return JsonUtil.getJsonString(ret);
		} // 计算最高贷款额度及年限
		else if ("jsdked".equals(task)) {
			pool.put("flag", "jsdked");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranDKSS01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		// 获取还款计划
		else if ("getdqrq".equals(task)) {
			pool.put("flag", "getdqrq");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranDKSS01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		return null;
	}

	private boolean in_step1(String type, MainContext mainContext) {
		DataPool pool = mainContext.getDataPool();
		pool.put("dkzt", "2");// 0-已还清的贷款；1-申请中的贷款；2-已放款未还清的贷款；3-已放款
		// 获取借款合同编号
		TransEngine.getInstance().execute("TranJKHTBH01", mainContext);
		// 获取最高贷款额度年限参数
		TransEngine.getInstance().execute("TranZGEDNX01", mainContext);
		return true;
	}

}
