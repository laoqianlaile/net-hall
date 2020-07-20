package com.yd.ish.flowhelper.dp;

import org.springframework.stereotype.Component;

import com.yd.workflow.FlowHelperI;
import com.yd.basic.expression.IshExpression;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;

 /**
 * 名称：FlowWFJCDJCX01
 * <p>功能：缴存登记撤销流程助手<br>
 * @author 柏慧敏
 * @version 0.1 2018年9月28日 柏慧敏创建
 * @note
 */
@Component
public class FlowWFJCDJCX01 implements FlowHelperI {

	private static final String STEP_STEP1="step1";//缴存登记撤销
		
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
		
		return null;
	}
	
	private boolean in_step1(String type,MainContext mainContext){
		DataPool pool = mainContext.getDataPool();
		pool.put("flag", "hqjcdjxx");
		TransEngine.getInstance().execute("TranJCDJCX01",mainContext);
		return true;
	}

	private boolean out_step1(MainContext mainContext){
		DataPool pool = mainContext.getDataPool();
		pool.put("flag", "cxjcdjxx");
		TransEngine.getInstance().execute("TranJCDJCX01",mainContext);
		return true;
	}

}
