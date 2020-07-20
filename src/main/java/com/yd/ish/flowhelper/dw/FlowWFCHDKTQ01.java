package com.yd.ish.flowhelper.dw;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.yd.basic.expression.IshExpression;
import com.yd.deplatform.util.PropertyUtil;
import com.yd.ish.common.util.StringUtils;
import com.yd.org.util.EncryptionUtil;
import com.yd.svrplatform.comm_mdl.context.UserContext;
import com.yd.svrplatform.comm_mdl.context.UserContextFactory;
import com.yd.svrplatform.util.DataPool;
import com.yd.workflow.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.druid.support.json.JSONUtils;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.workflow.FlowHelperI;

/**
 *
 * 名称：FlowWFCHDKTQ01.java
 * <p>功能： 偿还公积金贷款提取流程助手<br>
 * @author 王赫
 * @version 0.1	2018年8月2日	王赫创建
 */
@Component
public class FlowWFCHDKTQ01 implements FlowHelperI {

	private static final Logger logger = LoggerFactory.getLogger(FlowWFCHDKTQ01.class);

	private static final String STEP_STEP1 = "step1";// 偿还公积金贷款提取

	/*
	 * 交易调用示例，trancode为具体交易代码
	 * TransEngine.getInstance().execute("trancode",mainContext);
	 */

	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		if (stepid.equals(STEP_STEP1)){
			return in_step1(type, mainContext);
		}
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
		if (stepid.equals(STEP_STEP1)) {
			return out_step1(mainContext);
		}
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		if("hqyhzh".equals(task)){
			return cmd_step2(mainContext);
		}else if("hqtqje".equals(task)){
			return cmd_step3(mainContext);
		}else if("hqgjjhkxx".equals(task)){
			return cmd_step4(mainContext);
		}else if("hqsdhkxx".equals(task)){
			return cmd_step6(mainContext);
		}else if("cmdhqyzm".equals(task)){
			return cmd_step7(mainContext);
		}
		return null;

	}
	//获取绑定银行信息
	private String cmd_step2(MainContext mainContext){
		HashMap<String,Object> ret = TransEngine.getInstance().execute("TranHQBDYH01", mainContext);
		return JsonUtil.getJsonString(ret);
	}
	//获取公积金还款信息
	private String cmd_step4(MainContext mainContext){
		HashMap<String,Object> map = TransEngine.getInstance().execute("TranHQDKXX01",mainContext);
		return JSONUtils.toJSONString(map);
	}
	//获取商贷贷款还款信息
	private String cmd_step6(MainContext mainContext){
		HashMap<String,Object> map = TransEngine.getInstance().execute("TranHQHKXX02",mainContext);
		return JSONUtils.toJSONString(map);
	}

	/**
	 * 发送验证码
	 * @param mainContext
	 * @return
	 */
	private String cmd_step7(MainContext mainContext){
		//发送验证码之前校验用户输入的交易密码是否正确
		DataPool pool=mainContext.getDataPool();
		String jymm = pool.getString("jymm");
		// 用户预留手机号
		pool.put("ylsjh",pool.getString("sjhm"));
		pool.put("grzh",IshExpression.getRealUserExtInfo("grzh"));
		//将交易密码加密
		if(StringUtils.isNotBlank(jymm)) {
			pool.put("jymm", EncryptionUtil.MD5Encode(jymm));
			logger.info("加密前的密码：{},加密后的密码：{}", jymm, EncryptionUtil.MD5Encode(jymm));
		}
		TransEngine.getInstance().execute("GGTrans_0008",mainContext);
		// 调用校验用户输入的手机号和银行卡是否正确接口
		pool.put("yhylsjh",pool.get("sjhm"));
		pool.put("yh",pool.get("skrkhyh"));
		pool.put("xingming",IshExpression.getRealUserExtInfo("xingming"));
		pool.put("yhkh",pool.get("skrzhhm"));
		pool.put("zjhm",IshExpression.getRealUserExtInfo("zjhm"));
		pool.put("zjlx",IshExpression.getRealUserExtInfo("zjlx"));
		TransEngine.getInstance().execute("TranYHYZ02",mainContext);
		// 用户预留手机号
		pool.put("ylsjh",pool.getString("sjhm"));
		//调用发送短信验证码交易 ,会调用KF00-API-COM-001接口
		Map<String,Object> map = TransEngine.getInstance().execute("GGTrans_0002",mainContext);
		logger.info("---发送短信验证码返回数据："+JsonUtil.getJsonString(map));
		return JsonUtil.getJsonString(map);
	}

	//获取提取金额
	private String cmd_step3(MainContext mainContext){
		HashMap<String,Object> map = TransEngine.getInstance().execute("TranHQTQJE01",mainContext);
		return JSONUtils.toJSONString(map);
	}

	//
	private boolean in_step1(String type, MainContext mainContext) {
		// 获取个人信息
		TransEngine.getInstance().execute("TranGRTQXX01", mainContext);
		//获取归集行信息
		TransEngine.getInstance().execute("TranHQGJYH01",mainContext);
		DataPool pool = mainContext.getDataPool();
		pool.put("dkzt", "2");//0-已还清的贷款；1-申请中的贷款；2-已放款未还清的贷款；3-已放款
		//调用交易获取借款合同编号
		TransEngine.getInstance().execute("TranJKHTBH01",mainContext);
		return true;
	}
	//提交偿还公积金贷款提交
	private boolean out_step1(MainContext mainContext) {
		DataPool datapool = mainContext.getDataPool();
		String jymm = datapool.getString("jymm");
		//将交易密码加密
		if(StringUtils.isNotBlank(jymm)) {
			datapool.put("jymm", EncryptionUtil.MD5Encode(jymm));
			logger.info("加密前的密码：{},加密后的密码：{}", jymm, EncryptionUtil.MD5Encode(jymm));
		}
		// 用户预留手机号
		datapool.put("ylsjh",datapool.getString("sjhm"));
		// 调用验证码录入是否正确校验
		TransEngine.getInstance().execute("GGTrans_0003",mainContext);
		// 调用交易提交偿还贷款提取
		TransEngine.getInstance().execute("TranCHDKTQ01", mainContext);

		// 业务编号
		datapool.put("ywbh",datapool.get(Constants._WF_FLOWID));
		// 渠道编号
		datapool.put("qdbh",PropertyUtil.getString("system_no"));
		// 调用交易查询申报机构
		TransEngine.getInstance().execute("TranHQSBJG01",mainContext);
		return true;
	}

}
