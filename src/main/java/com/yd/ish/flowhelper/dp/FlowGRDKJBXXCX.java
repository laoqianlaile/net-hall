package com.yd.ish.flowhelper.dp;

import com.alibaba.fastjson.JSON;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.workflow.FlowHelperI;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 名称：FlowGRDKJBXXCX
 * <p>
 * 功能： 个人贷款基本信息查询<br>
 * 
 * @brief 功能简述 个人贷款基本信息查询
 * @author 许永峰
 * @version 版本号 修改人 修改时间 地点 原因
 * @note 0.1 ypf 2020年03月20日 贵港新增
 */
@Component
public class FlowGRDKJBXXCX implements FlowHelperI {
	private static final String STEP_STEP1="step1";//公积金账户基本信息
	/*
	 * 交易调用示例，trancode为具体交易代码
	 * TransEngine.getInstance().execute("trancode",mainContext);
	 */

	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		if (stepid.equals(STEP_STEP1)) {
			return in_step1(type, mainContext);
		}
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {

		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		//TransEngine.getInstance().execute("TranZHJBXX01",mainContext);
		if ("dklb".equals(task)) {
			TransEngine.getInstance().execute("TranGRDKJBXXCX", mainContext);
		}
		if("cmdHTXX".equals(task)){
			Map<String,Object> rtnMap = TransEngine.getInstance().execute("TranHTXX", mainContext);
			return JSON.toJSONString(rtnMap);
		}
		return null;
	}
	private boolean in_step1(String type, MainContext mainContext) {

		// 获取公积金账户基本信息
		TransEngine.getInstance().execute("TranZHJBXX01", mainContext);
		return true;
	}
}
