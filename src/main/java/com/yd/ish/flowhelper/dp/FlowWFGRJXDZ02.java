package com.yd.ish.flowhelper.dp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.workflow.FlowHelperI;
import com.yd.basic.util.YDVoucherUtil;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;

 /**
 * 名称：FlowWFGRJXDZ02
 * <p>功能： 个人结息对账单流程助手<br>
 * @brief 个人结息对账单流程助手
 * @author 柏慧敏
 * @version 0.1	2018年12月13日	柏慧敏创建
 * @note
 */
@Component
public class FlowWFGRJXDZ02 implements FlowHelperI {

	private static final Logger logger = LoggerFactory.getLogger(FlowWFGRJXDZ02.class);
	
	private static final String STEP_STEP1="step1";//个人结息对账单
	private static final String STEP_STEP2="step2";//个人结息对账单打印
	
	/*交易调用示例，trancode为具体交易代码
	TransEngine.getInstance().execute("trancode",mainContext);
	*/
	@Autowired
	YDVoucherUtil yDVoucherUtil;
	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		if(stepid.equals(STEP_STEP1)){
			return in_step1(type,mainContext);
		}
		if(stepid.equals(STEP_STEP2)){
			return in_step2(type,mainContext);
		}
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {

		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		
		return null;
	}
	
	private boolean in_step1(String type,MainContext mainContext){
		//调用个人结息对账单查询交易
		TransEngine.getInstance().execute("TranGRJXDZ02", mainContext);
		return true;
	}

	private boolean in_step2(String type,MainContext mainContext){
		// 获取打印模板
		String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "GRJXDZ02");
		mainContext.getDataPool().put("pdfKey", poolkey);
		return true;
	}

}
