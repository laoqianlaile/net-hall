package com.yd.ish.flowhelper.dp;

import org.springframework.stereotype.Component;

import com.yd.workflow.FlowHelperI;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;

 /**
 * 名称：FlowWFZHJBXX01
 * <p>功能：公积金账户基本信息流程助手<br>
 * @author 柏慧敏
 * @version 0.1	2018年5月31日	柏慧敏创建
 * @note
 */
@Component
public class FlowWFZHJBXX01 implements FlowHelperI {
	
	private static final String STEP_STEP1="step1";//公积金账户基本信息
	
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
		//获取账户基本信息
		TransEngine.getInstance().execute("TranZHJBXX01",mainContext);
		return true;
	}

}
