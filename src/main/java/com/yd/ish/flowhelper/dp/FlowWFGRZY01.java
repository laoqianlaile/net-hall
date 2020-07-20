package com.yd.ish.flowhelper.dp;

import java.util.HashMap;


import com.yd.basic.expression.IshExpression;
import org.springframework.stereotype.Component;

import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.workflow.FlowHelperI;

 /**
 * 名称：FlowWFGRZY01
 * <p>功能：个人账户同城转移流程助手<br>
 * @author 柏慧敏
 * @version 0.1	2018年8月21日	柏慧敏创建
 * @note
 */
@Component
public class FlowWFGRZY01 implements FlowHelperI {

	private static final String STEP_STEP1="step1";//个人账户同城转移
	
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
		// 获取单位托管户账号
		if ("CMD01".equals(task)) {
			pool.put("flag", "gettghzh");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranGRZY01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		//根据个人账号获取个人信息
		if ("CMD02".equals(task)) {
			pool.put("flag", "hqgrxx");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranGRXXBG01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		//校验批量信息
		if("CMD03".equals(task)){
			pool.put("flag", "jyplxx");
			TransEngine.getInstance().execute("TranGRZY01", mainContext);
		}
		return null;
	}
	
	private boolean in_step1(String type,MainContext mainContext){
		DataPool pool = mainContext.getDataPool();
		// 将脱敏后的单位账号放到总线中
		pool.put("tmdwzh", IshExpression.getRealUserExtInfo("dwdjh"));
		return true;
	}

	private boolean out_step1(MainContext mainContext){
		DataPool pool = mainContext.getDataPool();
		//提交个人转移信息
		pool.put("flag", "tjzyxx");
		TransEngine.getInstance().execute("TranGRZY01", mainContext);
		return true;
	}

}
