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
 * 名称：FlowWFDKSQ02
  * <p>功能：贷款申请流程助手<br>
  * @author 柏慧敏
  * @version 0.1	2019年6月24日	柏慧敏创建
 * @note
 */
@Component
public class FlowWFDKSQ02 implements FlowHelperI {

	private static final Logger logger = LoggerFactory.getLogger(FlowWFDKSQ02.class);
	
	private static final String STEP_STEP1="step1";//贷款申请资格确认
	private static final String STEP_STEP2="step2";//贷款申请
	
	/*交易调用示例，trancode为具体交易代码
	TransEngine.getInstance().execute("trancode",mainContext);
	*/
	
	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		if(stepid.equals(STEP_STEP1)){
			return in_step1(type,mainContext);
		}
		if(stepid.equals(STEP_STEP2)){
			return in_step2(type,mainContext);
		}
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
		if(stepid.equals(STEP_STEP1)){
			return out_step1(mainContext);
		}
		if(stepid.equals(STEP_STEP2)){
			return out_step2(mainContext);
		}
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		DataPool pool = mainContext.getDataPool();
		if("getGrxx".equals(task)){
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranGRJBXX02", mainContext);
			return JsonUtil.getJsonString(ret);
		}// 获取所选行借款人信息
		else if ("CMD01".equals(task)) {
			pool.put("flag", "hqdqhxx");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranDKSQ01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		// 删除所选行借款人信息
		else if ("CMD02".equals(task)) {
			pool.put("flag", "delete");
			TransEngine.getInstance().execute("TranDKSQ01", mainContext);
		}
		// 添加借款人信息
		else if ("CMD03".equals(task)) {
			pool.put("flag", "add");
			TransEngine.getInstance().execute("TranDKSQ01", mainContext);
		}
		// 根据开发商信息获取项目信息
		else if ("CMD04".equals(task)) {
			pool.put("flag", "hqlp");
			TransEngine.getInstance().execute("TranFYLPXX01", mainContext);
		}
		// 计算最高贷款金额年限
		else if ("CMD05".equals(task)) {
			pool.put("flag", "jsdked");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranDKSQ01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		// 计算月还款额
		if ("CMD06".equals(task)) {
			pool.put("flag", "getyhke");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranDKSQ01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		// 共有产权人信息检查
		else if ("CMD07".equals(task)) {
			pool.put("flag", "checkgycqr");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranDKSQ01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		// 复制借款人至共有产权人或抵押人
		else if ("CMD08".equals(task)) {
			pool.put("flag", "copyjkr");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranDKSQ01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		// 抵押信息检查
		else if ("CMD09".equals(task)) {
			pool.put("flag", "checkdyxx");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranDKSQ01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		// 保证信息检查
		else if ("CMD10".equals(task)) {
			pool.put("flag", "checkbzxx");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranDKSQ01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		// 项目信息查询
		else if ("CMD11".equals(task)) {
			pool.put("flag", "getxmxx");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranXMCX01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		return null;
	}
	private boolean in_step1(String type,MainContext mainContext){
		DataPool pool = mainContext.getDataPool();
		// 获取开发商信息
		pool.put("flag", "hqkfs");
		TransEngine.getInstance().execute("TranFYLPXX01", mainContext);
		return true;
	}

	private boolean in_step2(String type,MainContext mainContext){
		DataPool pool = mainContext.getDataPool();
		pool.put("flag","instep2");
		TransEngine.getInstance().execute("TranDKSQ02", mainContext);
		// 获取开发商信息
		pool.put("flag", "hqkfs");
		TransEngine.getInstance().execute("TranFYLPXX01", mainContext);
		return true;
	}

	private boolean out_step1(MainContext mainContext){
		DataPool pool = mainContext.getDataPool();
		pool.put("flag","outstep1");
		TransEngine.getInstance().execute("TranDKSQ02", mainContext);
		return true;
	}

	private boolean out_step2(MainContext mainContext){
		DataPool pool = mainContext.getDataPool();
		pool.put("flag", "tjxx");
		//调用接口提交申请信息
		TransEngine.getInstance().execute("TranDKSQ01", mainContext);
		return true;
	}
}
