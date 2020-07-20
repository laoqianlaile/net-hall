package com.yd.ish.flowhelper.dp;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.basic.util.YDVoucherUtil;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.workflow.FlowHelperI;

/**
 * 名称：FlowWFJCJSTZ01
 * <p>
 * 功能： 缴存基数调整流程助手
 * 
 * @brief 调整公积金缴存基数
 * @author 许永峰
 * @version 版本号 修改人 修改时间 地点 原因
 * @note 0.1 许永峰 18年7月2日 长春 创建
 */
@Component
public class FlowWFJCJSTZ01 implements FlowHelperI {

	private static final String STEP_STEP1 = "step1";// 缴存基数调整
	private static final String STEP_STEP2 = "step2";// 缴存基数调整

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
		if (STEP_STEP1.equals(stepid) && "queryGRJCXX".equals(task)) {
			return queryGRJCXX(mainContext);
		}
		if (STEP_STEP1.equals(stepid) && "JYTJXX".equals(task)) {

			return validateTJXX(mainContext);

		}
		if (STEP_STEP1.equals(stepid) && "hqyjce".equals(task)) {

			return getYjce(mainContext);

		}
		return null;
	}

	/**
	 * 获取缴存比例及缴存基数调低标志
	 * 
	 * @param type
	 * @param mainContext
	 * @return
	 */
	private boolean in_step1(String type, MainContext mainContext) {
		// 获取缴存比例信息
		DataPool datapool = mainContext.getDataPool();
		TransEngine.getInstance().execute("TranHQDWXX01", mainContext);
		//获取缴存基数调低标志
		datapool.put("flag", "hqtdflag");
		TransEngine.getInstance().execute("TranJCJSTZ01", mainContext);
		return true;
	}

	private boolean in_step2(String type, MainContext mainContext) {
		// 获取打印模板
		String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "JCJSTZ01");
		mainContext.getDataPool().put("pdfKey", poolkey);

		return true;
	}

	/**
	 * 提交缴存基数调整信息
	 * 
	 * @param mainContext
	 * @return
	 */
	private boolean out_step1(MainContext mainContext) {
		DataPool datapool = mainContext.getDataPool();
		datapool.put("flag", "tjxx");
		TransEngine.getInstance().execute("TranJCJSTZ01", mainContext);
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
	/**
	 * 获取月缴存额信息
	 * 
	 * @param mainContext
	 * @return
	 */
	private String getYjce(MainContext mainContext) {
		DataPool dataPool = mainContext.getDataPool();
		dataPool.put("flag", "hqyjce");
		// 调用交易TranJCJSTZ01
		HashMap<String, Object> map = TransEngine.getInstance().execute("TranJCJSTZ01", mainContext);
		return JsonUtil.getJsonString(map);
	}

	
	/**
	 * 校验提交信息
	 * 
	 * @param mainContext
	 * @return
	 */
	private String validateTJXX(MainContext mainContext) {
		DataPool datapool = mainContext.getDataPool();
		datapool.put("flag", "jyxx");
		TransEngine.getInstance().execute("TranJCJSTZ01", mainContext);
		return null;
	}

}
