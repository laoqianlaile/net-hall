package com.yd.ish.flowhelper.dp;

import org.springframework.stereotype.Component;

import com.yd.biz.engine.TransEngine;
import com.yd.org.util.EncryptionUtil;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.workflow.FlowHelperI;

/**
 * 名称：FlowWFDWMMZH01
 * <p>
 * 功能：单位登录密码找回<br>
 * 
 * @brief 找回单位登录密码
 * @author
 * @version 版本号 修改人 修改时间 地点 原因
 * @note 0.1 许永峰 2018年9月12日 长春
 */
@Component
public class FlowWFDWMMZH01 implements FlowHelperI {


	private static final String STEP_STEP1 = "step1";// 提交单位登录密码找回信息

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
		TransEngine.getInstance().execute("TranDWMMZH01", mainContext);
		return null;
	}

	private boolean out_step1(MainContext mainContext) {
		DataPool datapool = mainContext.getDataPool();
		datapool.put("xdlmm",  EncryptionUtil.MD5Encode(datapool.getString("xdlmm")));
		datapool.put("qrxdlmm",  EncryptionUtil.MD5Encode(datapool.getString("qrxdlmm")));
		// 调用单位登录密码找回交易
		TransEngine.getInstance().execute("TranDWMMZH01", mainContext);
		return true;

	}

}
