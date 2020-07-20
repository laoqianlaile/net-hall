package com.yd.ish.flowhelper.ln;

import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.workflow.FlowHelperI;
import com.yd.svrplatform.comm_mdl.context.MainContext;

import java.util.HashMap;

/**
 * 名称：FlowWFDKXXCX01
 * <p>功能： TODO<br>
 * @brief 功能简述 TODO
 * @author 
 * @version 版本号	修改人	修改时间	地点	原因 TODO
 * @note
 */
@Component
public class FlowWFDKXXCX01 implements FlowHelperI {

	private static final Logger logger = LoggerFactory.getLogger(FlowWFDKXXCX01.class);
	
	private static final String STEP_STEP1="step1";//贷款信息查询
	
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
		DataPool pool = mainContext.getDataPool();
		if("CMD01".equals(task)){
			//获取贷款详细信息
			pool.put("flag","getdkxx");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranDKXXCX01",mainContext);
			return JsonUtil.getJsonString(ret);
		}
		if("CMD02".equals(task)){
			//获取贷款详细信息
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranHQDKXX01",mainContext);
			return JsonUtil.getJsonString(ret);
		}
		return null;
	}
	
	private boolean in_step1(String type,MainContext mainContext){
		DataPool pool = mainContext.getDataPool();
		pool.put("flag","instep1");
		TransEngine.getInstance().execute("TranDKXXCX01",mainContext);
		return true;
	}

}
