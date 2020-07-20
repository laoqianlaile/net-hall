package com.yd.ish.flowhelper.dw;

import com.yd.basic.util.YDVoucherUtil;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.context.UserContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.workflow.FlowHelperI;
import dm.jdbc.filter.stat.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 职工基本信息变更
 */
@Component
public class FlowZGJBXXBG implements FlowHelperI{
	private static final Logger logger = LoggerFactory.getLogger(FlowZGJBXXBG.class);

	private static final String STEP_STEP1 = "step1";// 偿还公积金贷款提取

	@Autowired
	YDVoucherUtil yDVoucherUtil;

	/*
	 * 交易调用示例，trancode为具体交易代码
	 * TransEngine.getInstance().execute("trancode",mainContext);
	 */

	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		if("step1".equals(stepid)){
			TransEngine.getInstance().execute("TranDWJBXXCX", mainContext);
			return true;
		}
		if("step2".equals(stepid)){
			HashMap<String, Object> map = new HashMap<String, Object>();
			//获取打印模板
			String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "ZGXXBGDY");
			map.put("pdfKey", poolkey);
			DataPool pool = MainContext.currentMainContext().getDataPool();
			pool.put("pdfKey",poolkey);
			return true;
		}
		if("step3".equals(stepid)){
			HashMap<String, Object> map = new HashMap<String, Object>();
			//获取打印模板
			String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "ZGXXBGDY");
			map.put("pdfKey", poolkey);
			DataPool pool = MainContext.currentMainContext().getDataPool();
			pool.put("pdfKey",poolkey);
			return true;
		}
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
		if(STEP_STEP1.equals(stepid)){
			TransEngine.getInstance().execute("TranZGJBXXBG1", mainContext);
		}
		if("step2".equals(stepid)){
			TransEngine.getInstance().execute("TranZGJBXXBG2", mainContext);
		}

		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {

		//获取职工基本信息
		if("getZgjbxx".equals(task)){
			Map map = TransEngine.getInstance().execute("TranZGJBXXCX",mainContext);
			return JSONUtils.toJSONString(map);
		}
		return null;
	}
}
