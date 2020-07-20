package com.yd.ish.flowhelper.ln;

import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.yd.workflow.FlowHelperI;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;

/**
 * 名称：FlowWFDKSQ01
 * <p>功能：贷款申请流程助手<br>
 * @author 柏慧敏
 * @version 0.1	2018年7月16日	柏慧敏创建
 * @note
 */
@Component
public class FlowWFDKSQ01 implements FlowHelperI {

	private static final String STEP_STEP1 = "step1";// 贷款申请

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
		if (stepid.equals(STEP_STEP1)) {
			return out_step1(mainContext);
		}
		return true;
	}

	private boolean out_step1(MainContext mainContext) {
		DataPool pool = mainContext.getDataPool();
		pool.put("flag", "tjxx");
		//调用接口提交申请信息
		TransEngine.getInstance().execute("TranDKSQ01", mainContext);
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		DataPool pool = mainContext.getDataPool();
		// 获取所选行借款人信息
		if ("CMD01".equals(task)) {
			pool.put("flag", "hqdqhxx");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranDKSQ01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		// 删除所选行借款人信息
		if ("CMD02".equals(task)) {
			pool.put("flag", "delete");
			TransEngine.getInstance().execute("TranDKSQ01", mainContext);
		}
		// 添加借款人信息
		if ("CMD03".equals(task)) {
			pool.put("flag", "add");
			TransEngine.getInstance().execute("TranDKSQ01", mainContext);
		}
		// 根据开发商信息获取项目信息
		if ("CMD04".equals(task)) {
			pool.put("flag", "hqlp");
			TransEngine.getInstance().execute("TranFYLPXX01", mainContext);
		}
		// 计算最高贷款金额年限
		if ("CMD05".equals(task)) {
			pool.put("flag", "jsdked");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranDKSQ01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		// 计算月还款额
		if ("CMD06".equals(task)) {
			pool.put("flag", "getyhke");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranDKSQ01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		// 共有产权人信息检查
		if ("CMD07".equals(task)) {
			pool.put("flag", "checkgycqr");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranDKSQ01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		// 复制借款人至共有产权人或抵押人
		if ("CMD08".equals(task)) {
			pool.put("flag", "copyjkr");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranDKSQ01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		// 抵押信息检查
		if ("CMD09".equals(task)) {
			pool.put("flag", "checkdyxx");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranDKSQ01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		// 保证信息检查
		if ("CMD10".equals(task)) {
			pool.put("flag", "checkbzxx");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranDKSQ01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		// 项目信息查询
		if ("CMD11".equals(task)) {
			pool.put("flag", "getxmxx");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranXMCX01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		return null;
	}

	private boolean in_step1(String type, MainContext mainContext) {
		DataPool pool = mainContext.getDataPool();
		// 获取登录人的个人信息和公积金信息、贷款利率参数信息
		pool.put("flag", "instep1");
		TransEngine.getInstance().execute("TranDKSQ01", mainContext);
		// 获取开发商信息
		pool.put("flag", "hqkfs");
		TransEngine.getInstance().execute("TranFYLPXX01", mainContext);
		return true;
	}

}
