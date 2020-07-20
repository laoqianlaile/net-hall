package com.yd.ish.flowhelper.ln;

import org.springframework.stereotype.Component;

import com.yd.workflow.FlowHelperI;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;

 /**
 * 名称：FlowWFFYLPXX01
 * <p>功能：房源楼盘信息查询流程助手<br>
 * @author 柏慧敏
 * @version 0.1	2018年6月5日	柏慧敏创建
 * @note
 */
@Component
public class FlowWFFYLPXX01 implements FlowHelperI {
	
	private static final String STEP_STEP1="step1";//房源楼盘信息查询
	
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

	private boolean in_step1(String type, MainContext mainContext) {
		//获取开发商信息
		DataPool pool = mainContext.getDataPool();
		pool.put("flag", "hqkfs");
		TransEngine.getInstance().execute("TranFYLPXX01",mainContext);
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {

		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		//根据开发商获取楼盘信息
		if("CMD01".equals(task)){
			DataPool pool = mainContext.getDataPool();
			pool.put("flag", "hqlp");
			TransEngine.getInstance().execute("TranFYLPXX01",mainContext);
		}
		//获取房源楼盘信息
		if("CMD02".equals(task)){
			DataPool pool = mainContext.getDataPool();
			pool.put("flag", "hqfylp");
			TransEngine.getInstance().execute("TranFYLPXX01",mainContext);
		}
		//获取幢信息
		if("CMD03".equals(task)){
			DataPool pool = mainContext.getDataPool();
			pool.put("flag", "hqzxx");
			TransEngine.getInstance().execute("TranFYLPXX01",mainContext);
		}
		return null;
	}
	

}
