package com.yd.ish.flowhelper.common.yhzc;

import com.yd.biz.engine.TransEngine;
import com.yd.biz.exception.TransSingleException;
import com.yd.org.util.CommonErrorCode;
import com.yd.org.util.GetChannelCode;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.Datelet;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.workflow.FlowHelperI;
import com.yd.workflow.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 名称：FlowWFYHZC01.java
 * <p>功能：个人用户注册流程助手 <br> 
 * @author 张洪超
 * @version 0.1	2017年9月27日	张洪超创建
 * 			#V0.1	2018-9-26	王赫		修改协议签订调用接口改为直接调用交易
 */
@Component
public class FlowWFYHZC01 implements FlowHelperI {

	private static final String STEP_STEP1="step1";//个人信息录入


	private static final Logger logger = LoggerFactory.getLogger(FlowWFYHZC01Old.class);

	@Autowired
	ParamConfigImp paramConfigImp;

	/* 交易调用示例，trancode为具体交易代码
		TransEngine.getInstance().execute("trancode",mainContext);
	*/

	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {

		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
		//随机验证码校验
//		TransEngine.getInstance().execute("GGTrans_0001",mainContext);
//		DataPool datapool = mainContext.getDataPool();
//		String jg = String.valueOf(datapool.get("jg"));//获取验证结果
//		//获取验证结果
//		if(!StringUtils.equals("0",jg)) {
//			throw new TransSingleException(CommonErrorCode.ERROR_INPUT,"验证码");
//		}
		TransEngine.getInstance().execute("TranDXYZM1",mainContext);
		//提交注册信息
		TransEngine.getInstance().execute("TranGRZC",mainContext);
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		//获取短信验证码
		if ("dxyzm".equals(task)) {
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranDXYZM1",mainContext);
			return JsonUtil.getJsonString(ret);
		}
		if("cmdGRXX".equals(task)){
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranGRJBXXCX2",mainContext);
			return JsonUtil.getJsonString(ret);
		}
		return null;
	}




}
