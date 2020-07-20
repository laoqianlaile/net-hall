package com.yd.ish.flowhelper.dp;

import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.yd.workflow.FlowHelperI;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;

 /**
 * 名称：FlowWFGRXXBG02
 * <p>功能： 单位下个人信息变更流程助手<br>
 * @author 柏慧敏
 * @version 0.1	2018年10月09日	柏慧敏创建
 * @note 
 */
@Component
public class FlowWFGRXXBG02 implements FlowHelperI {

	private static final String STEP_STEP1="step1";//单位下个人信息变更
	
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
		DataPool pool = mainContext.getDataPool();
		//根据个人账号获取个人信息
		if("CMD01".equals(task)){
			pool.put("flag", "hqgrxx");
			HashMap<String,Object> map= TransEngine.getInstance().execute("TranGRXXBG01",mainContext);
			return JsonUtil.getJsonString(map);
		}
		//校验批量信息
		if("CMD02".equals(task)){
			pool.put("flag", "jyxx");
			TransEngine.getInstance().execute("TranGRXXBG02",mainContext);
		}
		return null;
	}
	
	private boolean in_step1(String type,MainContext mainContext){

		return true;
	}

	private boolean out_step1(MainContext mainContext){
		//提交批量变更信息
		DataPool pool = mainContext.getDataPool();
		pool.put("flag", "tjxx");
		TransEngine.getInstance().execute("TranGRXXBG02",mainContext);
		return true;
	}

}
