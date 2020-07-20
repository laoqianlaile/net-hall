package com.yd.ish.flowhelper.ln;

import com.yd.biz.engine.TransEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.workflow.FlowHelperI;
import com.yd.svrplatform.comm_mdl.context.MainContext;

 /**
 * 名称：FlowWFKFSXX01
 * <p>功能： 开发商信息查询流程助手
 * @brief 开发商信息查询流程助手
 * @author 柏慧敏
 * @version 0.1	2019年7月12日	柏慧敏创建
 * @note
 */
@Component
public class FlowWFKFSXX01 implements FlowHelperI {

	private static final Logger logger = LoggerFactory.getLogger(FlowWFKFSXX01.class);
	
	private static final String STEP_STEP1="step1";//开发商信息查询
	
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
		
		return null;
	}
	
	private boolean in_step1(String type,MainContext mainContext){
		// 调用交易查询开发商信息
		TransEngine.getInstance().execute("TranKFSXX01",mainContext);
		return true;
	}

}
