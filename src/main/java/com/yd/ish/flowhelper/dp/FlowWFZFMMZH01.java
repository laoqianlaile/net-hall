package com.yd.ish.flowhelper.dp;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.workflow.FlowHelperI;
import com.yd.workflow.util.Constants;
import com.yd.basic.expression.IshExpression;
import com.yd.biz.engine.TransEngine;
import com.yd.org.util.EncryptionUtil;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;

/**
 * 名称：FlowWFZFMMZH01
 * <p>
 * 功能：支付密码找回流程做助手<br>
 * 
 * @brief 支付密码找回
 * @author
 * @version 版本号 修改人 修改时间 地点 原因
 * @note 0.1 许永峰 2018年06月27日 长春 创建
 */
@Component
public class FlowWFZFMMZH01 implements FlowHelperI {

	private static final Logger logger = LoggerFactory.getLogger(FlowWFZFMMZH01.class);

	private static final String STEP_STEP1 = "step1";// 获取个人信息
	private static final String STEP_STEP2 = "step2";// 重置支付密码

	/*
	 * 交易调用示例，trancode为具体交易代码
	 * TransEngine.getInstance().execute("trancode",mainContext);
	 */

	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {

		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
		if (stepid.equals(STEP_STEP1)) {
			return out_step1(mainContext);
		}

		if (stepid.equals(STEP_STEP2)) {
			return out_step2(mainContext);
		}
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		if (StringUtils.equals("cmdhqyzm", task)) {
			return cmdhqyzm(mainContext);
		}
		return null;
	}

	/**
	 * 短信验证码录入提交后校验
	 * 
	 * @param mainContext
	 * @return
	 */
	private boolean out_step1(MainContext mainContext) {

		DataPool datapool = mainContext.getDataPool();
		// 用户预留手机号
		datapool.put("ylsjh", IshExpression.getRealUserExtInfo("sjhm"));
		// 调用验证码录入是否正确校验
		TransEngine.getInstance().execute("GGTrans_0003", mainContext);
		return true;
	}

	/**
	 * 支付密码找回
	 * 
	 * @param mainContext
	 * @return
	 */
	private boolean out_step2(MainContext mainContext) {
		DataPool datapool = mainContext.getDataPool();
		if (datapool.getString(Constants._WF_APPLY).equals(Constants.APP_YES)) {
			datapool.put("xzfmm", EncryptionUtil.MD5Encode(datapool.getString("xzfmm")));
			datapool.put("qrxzfmm", EncryptionUtil.MD5Encode(datapool.getString("qrxzfmm")));
		}
		// 调用支付密码找回交易
		TransEngine.getInstance().execute("TranZFMMZH01", mainContext);

		return true;
	}

	/**
	 * 发送验证码
	 * 
	 * @param mainContext
	 * @return
	 */
	private String cmdhqyzm(MainContext mainContext) {

		DataPool pool = mainContext.getDataPool();
		// 用户预留手机号
		pool.put("ylsjh", IshExpression.getRealUserExtInfo("sjhm"));
		// 调用发送短信验证码交易 ,会调用KF00-API-COM-001接口
		Map<String, Object> map = TransEngine.getInstance().execute("GGTrans_0002", mainContext);
		logger.info("---发送短信验证码返回数据：" + JsonUtil.getJsonString(map));
		return JsonUtil.getJsonString(map);
	}

}
