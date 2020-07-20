package com.yd.ish.flowhelper.dp;

import com.alibaba.fastjson.JSON;
import com.yd.basic.util.YDVoucherUtil;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.workflow.FlowHelperI;
import com.yd.workflow.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 名称：FlowGRQF.java
 * <p>
 * 功能：个人启封流程助手 <br>
 */
@Component
public class FlowGRQF implements FlowHelperI {

	private static final Logger logger = LoggerFactory.getLogger(FlowGRQF.class);

	private static final String STEP_STEP1 = "step1";// 启封
	private static final String STEP_STEP2 = "step2";// 凭证打印
	private static final String STEP_STEP3 = "step3";
	@Autowired
	YDVoucherUtil yDVoucherUtil;

	/*
	 * 交易调用示例，trancode为具体交易代码
	 * TransEngine.getInstance().execute("trancode",mainContext);
	 */

	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		// 调用获取单位公积金信息
		if (STEP_STEP1.equals(stepid)) {
			return in_step1(type, mainContext);
		}
		if(STEP_STEP2.equals(stepid)){
			return in_step2(mainContext,type);
		}
		if(STEP_STEP3.equals(stepid)){
			return in_step3(mainContext,type);
		}
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
		if (STEP_STEP1.equals(stepid)) {
			return out_step1(mainContext);
		}
		if(STEP_STEP2.equals(stepid)){
			TransEngine.getInstance().execute("TranGRQF2", mainContext);
		}
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		if(task.equals("cmdGRXXCX")){
			Map<String,Object> rtnMsg = TransEngine.getInstance().execute("TranGRXXCX", mainContext);
			return JSON.toJSONString(rtnMsg);
		}
		return null;
	}
	/**
	 * 获取单位信息
	 */
	private boolean in_step1(String type, MainContext mainContext) {
		//"unitprop" -> "12.000"
		TransEngine.getInstance().execute("TranDWJBXXCX", mainContext);
		return true;
	}

	/**
	 * 获取个人缴存信息
	 *
	 * @param mainContext
	 * @return
	 */
	private String queryGRJCXX(MainContext mainContext) {
		DataPool dataPool = mainContext.getDataPool();
		dataPool.put("taskFlag", "queryGRJCXX");
		// 调用交易TranGRJCXX01
		HashMap<String, Object> map = TransEngine.getInstance().execute("TranZGJBXXCX", mainContext);
		return JsonUtil.getJsonString(map);
	}

	private boolean out_step1(MainContext mainContext) {
		// 调用批量异常获取接口
		TransEngine.getInstance().execute("TranGRQF1", mainContext);
		return true;
	}
	//添加凭证打印
	private boolean in_step2(MainContext mainContext,String type) {
	    //查询封存人员信息
		HashMap<String, Object> map = new HashMap<String, Object>();
		//获取打印模板
		String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "GRQFDY");
		map.put("pdfKey", poolkey);
		DataPool pool = MainContext.currentMainContext().getDataPool();
		pool.put("pdfKey",poolkey);
		return true;

	}
	private boolean in_step3(MainContext mainContext,String type) {
		//查询封存人员信息
		HashMap<String, Object> map = new HashMap<String, Object>();
		//获取打印模板
		String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "GRQFDY");
		map.put("pdfKey", poolkey);
		DataPool pool = MainContext.currentMainContext().getDataPool();
		pool.put("pdfKey",poolkey);
		return true;

	}
}
