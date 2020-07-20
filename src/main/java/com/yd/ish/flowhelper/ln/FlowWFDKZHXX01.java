package com.yd.ish.flowhelper.ln;

import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.yd.workflow.FlowHelperI;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.JsonUtil;

 /**
 * 名称：FlowWFDKZHXX01
 * <p>功能：公积金贷款账户信息流程助手<br>
 * @author 柏慧敏
 * @version 0.1	2018年6月5日	柏慧敏创建
 */
@Component
public class FlowWFDKZHXX01 implements FlowHelperI {
	
	private static final String STEP_STEP1="step1";//公积金贷款账户信息
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
		if("CMD01".equals(task)){
			//获取贷款详细信息
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranHQDKXX01",mainContext);
			return JsonUtil.getJsonString(ret);
		}
		return null;
	}
	
	private boolean in_step1(String type,MainContext mainContext){
		//获取全部贷款账户信息
		TransEngine.getInstance().execute("TranDKZHXX01",mainContext);
		return true;
	}
}
