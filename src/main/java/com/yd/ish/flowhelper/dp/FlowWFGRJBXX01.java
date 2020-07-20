package com.yd.ish.flowhelper.dp;

import java.util.Map;

import com.yd.svrplatform.util.DataPool;
import org.springframework.stereotype.Component;

import com.alibaba.druid.support.json.JSONUtils;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.workflow.FlowHelperI;

/**
 * 
 * 名称：FlowWFGRJBXX01.java
 * <p>功能：单位下个人基本信息查询<br> 
 * @author 王赫
 * @version 0.1	2018年6月26日	王赫创建
 */
@Component
public class FlowWFGRJBXX01 implements FlowHelperI {

	private static final String STEP_STEP1="step1";//单位下个人基本信息查询
	private static final String STEP_SUBPAGE01="subpage01";//个人详细信息
	
	/*交易调用示例，trancode为具体交易代码
	TransEngine.getInstance().execute("trancode",mainContext);
	*/
	
	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		if(STEP_SUBPAGE01.equals(stepid)){
			return in_subpage01(type,mainContext);
		}
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
		
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		//查询单位下个人基本信息
		if(STEP_STEP1.equals(stepid)&&"DWXGRXX".equals(task)){
			TransEngine.getInstance().execute("TranGRJBXX01",mainContext);
		//调用数据校验交易
		}else if(STEP_STEP1.equals(stepid)&&"GRXX".equals(task)){
			DataPool pool = mainContext.getDataPool();
			//要校验的数据
			pool.put("checkValue", pool.getString("grzh"));
			//对应的实体DP077的属性名
			pool.put("entityName", "accnum1");
			//调用数据校验交易
			Map<String,Object> map = TransEngine.getInstance().execute("TranSJJY01", mainContext);
			return JSONUtils.toJSONString(map);
		}
		return null;
	}
	private boolean in_subpage01(String type, MainContext mainContext) {
		TransEngine.getInstance().execute("TranGRJBXX02",mainContext);
		return true;
	}
}
