package com.yd.ish.flowhelper.dp;

import com.yd.basic.util.YDVoucherUtil;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.workflow.FlowHelperI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 *
 * 名称：FlowWFGRFC01.java
 * <p>功能： 封存流程助手<br>
 * @author wanghe
 * @version 0.1	2018年5月24日	wanghe创建
 */
@Component
public class FlowGRFC implements FlowHelperI {


	private static final String STEP_STEP1="step1";//封存
	private static final String STEP_STEP2="step2";//打印凭证
	private static final String STEP_STEP3="step3";
	@Autowired
	YDVoucherUtil yDVoucherUtil;

	@Autowired
	ParamConfigImp paramconfigImp;

	/*交易调用示例，trancode为具体交易代码
	TransEngine.getInstance().execute("trancode",mainContext);
	*/

	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		if(stepid.equals(STEP_STEP1)){
			TransEngine.getInstance().execute("TranDWJBXXCX", mainContext);
		}
		//调取打印页面
		if(stepid.equals("step2")){
			HashMap<String, Object> map = new HashMap<String, Object>();
			//获取打印模板
			String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "GRFCDY");
			map.put("pdfKey", poolkey);
			DataPool pool = MainContext.currentMainContext().getDataPool();
			pool.put("pdfKey",poolkey);
			return true;
		}
		if(stepid.equals("step3")){
			HashMap<String, Object> map = new HashMap<String, Object>();
			//获取打印模板
			String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "GRFCDY");
			map.put("pdfKey", poolkey);
			DataPool pool = MainContext.currentMainContext().getDataPool();
			pool.put("pdfKey",poolkey);
			return true;
		}

		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
		if (stepid.equals(STEP_STEP1)) {
			return out_step1(mainContext);
		}
		if (stepid.equals(STEP_STEP2)) {
			TransEngine.getInstance().execute("TranGRFCXXSC2",mainContext);
		}
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {

		if("queryGRJCXX".equals(task)){
			return queryGRJCXX(mainContext);
		}

		return null;
	}
	/**
	 * 获取个人缴存信息
	 * @param mainContext
	 * @return
	 */
	public String queryGRJCXX(MainContext mainContext) {

		DataPool dataPool = mainContext.getDataPool();
		dataPool.put("taskFlag", "queryGRJCXX");
		//调用交易TranGRJCXX01
		HashMap<String, Object> map = TransEngine.getInstance().execute("TranGRJBXXCX", mainContext);
		return JsonUtil.getJsonString(map);

	}
	//提交封存数据
	private boolean out_step1(MainContext mainContext) {
		// 调用批量异常获取接口
		//TransEngine.getInstance().execute("TranTJFCXX01",mainContext);
		TransEngine.getInstance().execute("TranFLOWS",mainContext);
		TransEngine.getInstance().execute("TranGRFCXXSC1",mainContext);
		return true;
	}



}
