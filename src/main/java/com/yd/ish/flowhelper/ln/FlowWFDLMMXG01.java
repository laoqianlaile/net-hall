package com.yd.ish.flowhelper.ln;

import com.yd.biz.engine.TransEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.workflow.FlowHelperI;
import com.yd.svrplatform.comm_mdl.context.MainContext;

 /**
 * 名称：FlowWFDLMMXG01
 * <p>功能： 登录密码修改流程助手
 * @brief 登录密码修改流程助手
 * @author 柏慧敏
 * @version 0.1	2019年6月20日	柏慧敏创建
 * @note
 */
@Component
public class FlowWFDLMMXG01 implements FlowHelperI {

	private static final Logger logger = LoggerFactory.getLogger(FlowWFDLMMXG01.class);
	
	private static final String STEP_STEP1="step1";//登录密码修改
	
	/*交易调用示例，trancode为具体交易代码
	TransEngine.getInstance().execute("trancode",mainContext);
	*/
	
	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
		if(stepid.equals(STEP_STEP1)){
			return out_step1(mainContext);
		}
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		
		return null;
	}
	
	private boolean out_step1(MainContext mainContext){
		// 调用登录密码修改交易
		TransEngine.getInstance().execute("TranDLMMXG01",mainContext);
		return true;
	}

}
