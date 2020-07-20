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
 * 名称：FlowWFGRJXDZ01
 * <p>功能： 单位下个人结息对账单查询流程助手<br>
 * @brief 功能简述 单位下个人结息对账单查询
 * @author 许永峰
 * @version 版本号	修改人	修改时间			地点	原因
 * @note    0.1		许永峰	2018年11月27日	长春   创建
 */
@Component
public class FlowWFGRJXDZ01 implements FlowHelperI {

	private static final Logger logger = LoggerFactory.getLogger(FlowWFGRJXDZ01.class);
	
	private static final String STEP_STEP1="step1";//个人结息对账单查询
	private static final String STEP_STEP2 = "step2";// 个人结息对账单打印
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
		if (stepid.equals(STEP_STEP2)) {
			return in_step2(type, mainContext);
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
		//调用单位下个人结息对账单查询交易
		TransEngine.getInstance().execute("TranGRJXDZ01", mainContext);
		return true;
	}
	
	private boolean in_step2(String type, MainContext mainContext) {
		// 获取打印模板
		String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "GRJXDZ01");
		mainContext.getDataPool().put("pdfKey", poolkey);
		return true;
	}

}
