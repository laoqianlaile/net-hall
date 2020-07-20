package com.yd.ish.flowhelper.ln;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.workflow.FlowHelperI;
import com.yd.basic.util.YDVoucherUtil;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;

/**
 * 名称：FlowWFWTKHQY01
 * <p>
 * 功能：委托扣划签约流程助手<br>
 *
 * @author 柏慧敏
 * @version 0.1 2018年8月27日 柏慧敏创建
 * @note
 */
@Component
public class FlowWFWTKHQY01 implements FlowHelperI {

	private static final String STEP_STEP1 = "step1";// 委托扣划签约录入信息页面
	private static final String STEP_STEP2 = "step2";// 委托扣划签约打印页面

	/*
	 * 交易调用示例，trancode为具体交易代码
	 * TransEngine.getInstance().execute("trancode",mainContext);
	 */
	@Autowired
	YDVoucherUtil yDVoucherUtil;

	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		if (stepid.equals(STEP_STEP1)) {
			return in_step1(type, mainContext);
		}
		if (stepid.equals(STEP_STEP2)) {
			return in_step2(type, mainContext);
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
		// 获取借款人信息
		if ("hqjkrxx".equals(task)) {
			pool.put("flag", "hqjkrxx");
			TransEngine.getInstance().execute("TranWTKHQY01", mainContext);
		}
		// 获取贷款详细信息
		if ("getdkxxxx".equals(task)) {
			pool.put("flag", "hqjkrxx");
			TransEngine.getInstance().execute("TranWTKHQY01", mainContext);
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranHQDKXX01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		// 获取贷款详细信息
		else if ("checkqyxx".equals(task)) {
			pool.put("flag", "checkqyxx");
			TransEngine.getInstance().execute("TranWTKHQY01", mainContext);
		}
		return null;
	}

	private boolean in_step1(String type, MainContext mainContext) {
		DataPool pool = mainContext.getDataPool();
		// 获取借款合同编号
		pool.put("dkzt", "2");// 0-已还清的贷款；1-申请中的贷款；2-已放款未还清的贷款；3-已放款
		TransEngine.getInstance().execute("TranJKHTBH01", mainContext);
		// 获取公积金账户基本信息
		TransEngine.getInstance().execute("TranZHJBXX01", mainContext);
		return true;
	}

	private boolean in_step2(String type, MainContext mainContext) {
	    // 获取打印模板
        String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "WTKHQY01");
        mainContext.getDataPool().put("pdfKey", poolkey);
		return true;
	}

	private boolean out_step1(MainContext mainContext) {

		DataPool pool = mainContext.getDataPool();
		// 提交委托扣划签约信息
		pool.put("flag", "tjxx");
		TransEngine.getInstance().execute("TranWTKHQY01", mainContext);
		return true;
	}

}
