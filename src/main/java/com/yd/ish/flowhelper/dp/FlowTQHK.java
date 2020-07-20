package com.yd.ish.flowhelper.dp;

import com.alibaba.fastjson.JSON;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.context.UserContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.workflow.FlowHelperI;
import dm.jdbc.filter.stat.util.JSONUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 名称：FlowTQHK
 * <p>
 * 功能： 提前还款<br>
 * 
 * @brief 功能简述 提前还款
 * @author 许永峰
 * @version 版本号 修改人 修改时间 地点 原因
 * @note 0.1 ypf 2020年03月20日 贵港新增
 */
@Component
public class FlowTQHK implements FlowHelperI {

	/*
	 * 交易调用示例，trancode为具体交易代码
	 * TransEngine.getInstance().execute("trancode",mainContext);
	 */

	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		//获取贷款信息
		TransEngine.getInstance().execute("TranDKJBXX", mainContext);
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
		//进行短信校验
		TransEngine.getInstance().execute("TranDXYZM", mainContext);
		//提交提前还款信息
		TransEngine.getInstance().execute("TranTQHK", mainContext);
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {

		//获取短信验证码
		if ("dxyzm".equals(task)) {
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranDXYZM",mainContext);
			return JsonUtil.getJsonString(ret);
		}
		if("cmdDKSQ".equals(task)){
			Map<String,Object> rtnMap = TransEngine.getInstance().execute("TranDKSQ",mainContext);
			return JSON.toJSONString(rtnMap);
		}
		if("getAllInfo".equals(task)){
			Map<String,Object> rtnMap = TransEngine.getInstance().execute("TranDKJBXX",mainContext);
			return JSON.toJSONString(rtnMap);
		}
		if("cmdTQHK".equals(task)){
			Map<String,Object> rtnMap = TransEngine.getInstance().execute("TranTQHK",mainContext);
			return JSON.toJSONString(rtnMap);
		}
		return null;
	}
}