package com.yd.ish.flowhelper.dp;

import java.util.HashMap;

import com.yd.basic.util.YDVoucherUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.workflow.FlowHelperI;
import com.yd.workflow.util.Constants;

/**
 *
 * 名称：FlowWFGRQF01.java
 * <p>
 * 功能：个人启封流程助手 <br>
 *
 * @author 王赫
 * @version 0.1 2018年5月29日 王赫创建
 */
@Component
public class FlowWFGRQF01 implements FlowHelperI {

	private static final Logger logger = LoggerFactory.getLogger(FlowWFGRQF01.class);

	private static final String STEP_STEP1 = "step1";// 启封
	private static final String STEP_STEP2 = "step2";// 凭证打印

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
		}else if(STEP_STEP2.equals(stepid)){
			return in_step2(mainContext,type);
		}
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
		if (STEP_STEP1.equals(stepid)) {
			return out_step1(mainContext);
		}

		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		if (STEP_STEP1.equals(stepid) && "queryGRJCXX".equals(task)) {
			return queryGRJCXX(mainContext);
		}
		return null;
	}
	/**
	 * 获取单位缴存信息
	 * @param type
	 * @param mainContext
	 * @return
	 */
	private boolean in_step1(String type, MainContext mainContext) {
		DataPool pool = mainContext.getDataPool();
		pool.put("dwzh", pool.get(Constants._WF_ORGID));
		TransEngine.getInstance().execute("TranHQDWXX01", mainContext);
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
		HashMap<String, Object> map = TransEngine.getInstance().execute("TranGRJCXX01", mainContext);
		return JsonUtil.getJsonString(map);
	}

	private boolean out_step1(MainContext mainContext) {
		// 调用批量异常获取接口
		TransEngine.getInstance().execute("TranTJGRQF01", mainContext);
		return true;
	}
	//添加凭证打印
	private boolean in_step2(MainContext mainContext,String type) {
	    //查询封存人员信息
        DataPool dataPool = mainContext.getDataPool();
//		dataPool.put("slh",dataPool.getString("_IS"));
        String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "GRQFPZ01");
        mainContext.getDataPool().put("pdfKey", poolkey);

		return true;
	}
}
