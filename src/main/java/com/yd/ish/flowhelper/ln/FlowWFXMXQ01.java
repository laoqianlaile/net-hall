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
 * 名称：FlowWFXMXQ01
 * <p>功能： 开发商项目新签流程助手<br>
 * @brief 开发商项目新签流程助手
 * @author 柏慧敏
 * @version 0.1	2019年6月4日	柏慧敏创建
 * @note
 */
@Component
public class FlowWFXMXQ01 implements FlowHelperI {

	private static final Logger logger = LoggerFactory.getLogger(FlowWFXMXQ01.class);
	
	private static final String STEP_STEP1="step1";//开发商项目新签
	
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
		// 检查楼栋信息
		if("CMD01".equals(task)){
			pool.put("flag", "checkldxx");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranXMXQ01",mainContext);
			return JsonUtil.getJsonString(ret);
		}// 检查楼盘信息
		else if("CMD02".equals(task)){
			pool.put("flag", "checklpxx");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranXMXQ01",mainContext);
			return JsonUtil.getJsonString(ret);
		}
		return null;
	}
	
	private boolean in_step1(String type,MainContext mainContext){
		DataPool dataPool = mainContext.getDataPool();
		dataPool.put("flag","instep1");
		// 获取开发商信息
		TransEngine.getInstance().execute("TranXMXQ01",mainContext);
		return true;
	}

	private boolean out_step1(MainContext mainContext){
		DataPool dataPool = mainContext.getDataPool();
		dataPool.put("flag","outstep1");
		// 提交项目新签信息
		TransEngine.getInstance().execute("TranXMXQ01",mainContext);
		return true;
	}

}
