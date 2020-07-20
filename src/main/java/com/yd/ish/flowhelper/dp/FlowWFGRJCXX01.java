package com.yd.ish.flowhelper.dp;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.druid.support.json.JSONUtils;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.ReadProperty;
import com.yd.workflow.FlowHelperI;

/**
 * 
 * 名称：FlowWFGRJCXX01.java
 * <p>功能：单位下个人汇补缴信息查询 <br> 
 * @author 王赫
 * @version 0.1	2018年6月30日	王赫创建
 */
@Component
public class FlowWFGRJCXX01 implements FlowHelperI {

	private static final Logger logger = LoggerFactory.getLogger(FlowWFGRJCXX01.class);
	
	private static final String STEP_STEP1="step1";//单位下个人汇补缴信息查询
	
	/*交易调用示例，trancode为具体交易代码
	TransEngine.getInstance().execute("trancode",mainContext);
	*/
	
	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {

		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {

		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		//查询单位下个人汇补缴信息
		if("DWXGRHBJXX".equals(task)){
			TransEngine.getInstance().execute("TranGRJCXX02",mainContext);
		}
		return null;
	}
	

}
