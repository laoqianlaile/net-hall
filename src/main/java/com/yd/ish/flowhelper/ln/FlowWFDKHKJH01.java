package com.yd.ish.flowhelper.ln;

import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.yd.workflow.FlowHelperI;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;

 /**
 * 名称：FlowWFDKHKJH01
 * <p>功能：贷款还款计划查询流程助手<br>
 * @author 柏慧敏
 * @version 0.1	2018年6月15日	柏慧敏创建
 * @note
 */
@Component
public class FlowWFDKHKJH01 implements FlowHelperI {
	
	private static final String STEP_STEP1="step1";//贷款还款计划查询
	
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
		//调用交易获取贷款信息
		if("hqdkxx".equals(task)){
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranHQDKXX01",mainContext);
			return JsonUtil.getJsonString(ret);
		}
		//调用交易获取还款计划信息
		if("hqhkjh".equals(task)){
			TransEngine.getInstance().execute("TranDKHKJH01",mainContext);
		}
		return null;
		
	}
	
	private boolean in_step1(String type,MainContext mainContext){
		DataPool pool = mainContext.getDataPool();
		pool.put("dkzt", "2");//0-已还清的贷款；1-申请中的贷款；2-已放款未还清的贷款；3-已放款
		//调用交易获取借款合同编号
		TransEngine.getInstance().execute("TranJKHTBH01",mainContext);
		return true;
	}

}
