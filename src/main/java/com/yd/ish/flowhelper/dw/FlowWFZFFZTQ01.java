package com.yd.ish.flowhelper.dw;

import java.util.HashMap;
import java.util.Map;

import com.yd.deplatform.util.PropertyUtil;
import com.yd.workflow.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.workflow.FlowHelperI;
import com.yd.basic.expression.IshExpression;
import com.yd.biz.engine.TransEngine;
import com.yd.biz.exception.TransSingleException;
import com.yd.org.util.EncryptionUtil;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;

/**
 * 名称：FlowWFZFFZTQ01
 * <p>
 * 功能：租房提取流程助手 <br>
 * 
 * @author 柏慧敏
 * @version 0.1 2018年8月13日 柏慧敏创建
 */
@Component
public class FlowWFZFFZTQ01 implements FlowHelperI {

	private static final Logger logger = LoggerFactory.getLogger(FlowWFZFFZTQ01.class);

	private static final String STEP_STEP1 = "step1";// 租房提取

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
		if (stepid.equals(STEP_STEP1)) {
			return out_step1(mainContext);
		}
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		DataPool pool = mainContext.getDataPool();
		// 获取所选银行下绑定的银行账号
		if ("hqyhzh".equals(task)) {
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranHQBDYH01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		// 获取公租房信息
		else if ("hqgzfxx".equals(task)) {
			pool.put("flag", "hqgzfxx");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranZFFZTQ01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		// 计算可提取金额
		else if ("jsktqje".equals(task)) {
			pool.put("flag", "jsktqje");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranZFFZTQ01", mainContext);
			return JsonUtil.getJsonString(ret);
		}else if("cmd001".equals(task)){
			 // 用户预留手机号
			 pool.put("ylsjh",pool.getString("sjhm"));
			 pool.put("grzh",IshExpression.getRealUserExtInfo("grzh"));
			 String jymm=pool.getString("jymm");
			 //对密码进行加密
			 if(StringUtils.isNotBlank(jymm)){
				 pool.put("jymm", EncryptionUtil.MD5Encode(jymm));
			 }
			 //调用交易校验交易密码
			 TransEngine.getInstance().execute("GGTrans_0008",mainContext);
			// 调用校验用户输入的手机号和银行卡是否正确接口
			pool.put("yhylsjh",pool.get("sjhm"));
			pool.put("yh",pool.get("skrkhyh"));
			pool.put("xingming",IshExpression.getRealUserExtInfo("xingming"));
			pool.put("yhkh",pool.get("skrzhhm"));
			pool.put("zjhm",IshExpression.getRealUserExtInfo("zjhm"));
			pool.put("zjlx",IshExpression.getRealUserExtInfo("zjlx"));
			TransEngine.getInstance().execute("TranYHYZ02",mainContext);
			 //调用发送短信验证码交易 ,会调用KF00-API-COM-001接口
			Map<String,Object> map = TransEngine.getInstance().execute("GGTrans_0002",mainContext);
			logger.info("---发送短信验证码返回数据："+JsonUtil.getJsonString(map));
			return JsonUtil.getJsonString(map);
		}
		return null;
	}

	private boolean in_step1(String type, MainContext mainContext) {
		// 获取个人账户信息
		TransEngine.getInstance().execute("TranGRTQXX01", mainContext);
		//获取归集行信息
		TransEngine.getInstance().execute("TranHQGJYH01",mainContext);
		return true;
	}

	private boolean out_step1(MainContext mainContext) {
		DataPool pool = mainContext.getDataPool();
		// 用户预留手机号
		pool.put("ylsjh", pool.getString("sjhm"));
		// 调用验证码录入是否正确校验
		TransEngine.getInstance().execute("GGTrans_0003", mainContext);
		// 提交租房提取信息
		pool.put("flag", "tjxx");
		String jymm=pool.getString("jymm");
		//对密码进行加密
		if(StringUtils.isNotBlank(jymm)){
			pool.put("jymm", EncryptionUtil.MD5Encode(jymm));
		}
		TransEngine.getInstance().execute("TranZFFZTQ01", mainContext);

		// 业务编号
		pool.put("ywbh",pool.get(Constants._WF_FLOWID));
		// 渠道编号
		pool.put("qdbh",PropertyUtil.getString("system_no"));
		// 调用交易查询申报机构
		TransEngine.getInstance().execute("TranHQSBJG01",mainContext);
		return true;
	}

}
