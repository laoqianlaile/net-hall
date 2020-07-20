package com.yd.ish.flowhelper.ln;

import org.springframework.stereotype.Component;

import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.workflow.FlowHelperI;

 /**
 * 名称：FlowWFDKHKMX01
 * <p>功能：公积金贷款还款明细流程助手O<br>
 * @brief 公积金贷款还款明细
 * @author 
 * @version 版本号	修改人	修改时间			地点	原因 
 * @note 	0.1		许永峰	2018年6月22日		长春   创建
 */
@Component
public class FlowWFDKHKMX01 implements FlowHelperI {

	//private static final Logger logger = LoggerFactory.getLogger(FlowWFDKHKMX01.class);
	
	private static final String STEP_STEP1="step1";//公积金贷款还款明细
	
	/*交易调用示例，trancode为具体交易代码
	TransEngine.getInstance().execute("trancode",mainContext);
	*/
	
	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		if(stepid.equals(STEP_STEP1)){
			return in_step1(type,mainContext);
		}
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {

		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		if ("cmdCX".equals(task)) {
			TransEngine.getInstance().execute("TranDKHKMX01", mainContext);
		}
		
		return null;
	}
	
	private boolean in_step1(String type,MainContext mainContext){
		TransEngine.getInstance().execute("TranJKHTBH01",mainContext);
		return true;
	}

}
