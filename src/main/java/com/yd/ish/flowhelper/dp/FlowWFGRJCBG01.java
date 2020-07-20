package com.yd.ish.flowhelper.dp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.workflow.FlowHelperI;

/**
 * 
 * 名称：FlowWFGRJCBG01.java
 * <p>功能： 单位下个人缴存变更明细查询流程助手<br> 
 * @author 王赫
 * @version 0.1	2018年7月18日	王赫创建
 */
@Component
public class FlowWFGRJCBG01 implements FlowHelperI {

	private static final Logger logger = LoggerFactory.getLogger(FlowWFGRJCBG01.class);
	
	private static final String STEP_STEP1="step1";//单位下个人缴存变更明细查询
	
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
		if(stepid.equals(STEP_STEP1)){
			return out_step1(mainContext);
		}
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		if("GRJCBGMX".equals(task)){
			TransEngine.getInstance().execute("TranGRJCBG01",mainContext);
		}
		return null;
	}
	
	private boolean in_step1(String type,MainContext mainContext){

		return true;
	}

	private boolean out_step1(MainContext mainContext){

		return true;
	}

}
