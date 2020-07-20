package com.yd.ish.flowhelper.dp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.basic.util.YDVoucherUtil;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.workflow.FlowHelperI;

/**
 * 名称：FlowWFJCBLTZ01
 * <p>
 * 功能： 缴存比例调整流程助手<br>
 * 
 * @brief 缴存比例调整
 * @author
 * @version 版本号 修改人 修改时间 地点 原因
 * @note 0.1 许永峰 2018年6月28日 长春 创建
 */
@Component
public class FlowWFJCBLTZ01 implements FlowHelperI {

	private static final String STEP_STEP1 = "step1";// 缴存比例调整
	private static final String STEP_STEP2 = "step2";// 缴存比例调整
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

		return null;
	}

	/**
	 * 获取缴存比例
	 * 
	 * @param type
	 * @param mainContext
	 * @return
	 */
	private boolean in_step1(String type, MainContext mainContext) {
		TransEngine.getInstance().execute("TranHQDWXX01", mainContext);
		return true;
	}

	/**
	 * 打印凭证
	 * 
	 * @param type
	 * @param mainContext
	 * @return
	 */
	private boolean in_step2(String type, MainContext mainContext) {
		// 获取打印模板
		String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "JCBLTZ01");
		mainContext.getDataPool().put("pdfKey", poolkey);
		return true;
	}

	/**
	 * 提交变更后的缴存比例
	 * 
	 * @param mainContext
	 * @return
	 */
	private boolean out_step1(MainContext mainContext) {
		TransEngine.getInstance().execute("TranJCBLTZ01", mainContext);
		return true;
	}

}
