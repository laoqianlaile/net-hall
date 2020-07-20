package com.yd.ish.flowhelper.common.wjma;

import com.yd.basic.util.AesUtil;
import com.yd.biz.engine.TransEngine;
import com.yd.org.util.EncryptionUtil;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.workflow.FlowHelperI;
import com.yd.workflow.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;

/**
 * 名称：FlowWFWJMA02.java
 * <p>功能：单位临时用户忘记密码流程助手 <br>
 * @author 王赫
 * @version 0.1	2019年10月30日	王赫创建
 */
@Component
public class FlowWFWJMA02 implements FlowHelperI {

	private static final String STEP_STEP1="step1";//个人信息录入
	private static final String STEP_STEP2="step2";//重置密码

	private static final Logger logger = LoggerFactory.getLogger(FlowWFWJMA02.class);
	
	/* 交易调用示例，trancode为具体交易代码
		TransEngine.getInstance().execute("trancode",mainContext);
	*/
	
	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		DataPool dataPool = mainContext.getDataPool();
		dataPool.put("token",getRandomString(16));
		mainContext.setDataPool(dataPool);
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
		if(STEP_STEP1.equals(stepid)&&"cmd001".equals(task)){
			return cmd001(mainContext);
		}
		return null;
	}

	private String cmd001(MainContext mainContext){
		DataPool pool=mainContext.getDataPool();
		// 用户预留手机号
		pool.put("ylsjh",pool.getString("jbrsjhm"));
		//调用发送短信验证码交易 ,会调用KF00-API-COM-001接口
		Map<String,Object> map = TransEngine.getInstance().execute("GGTrans_0002",mainContext);
		logger.info("---发送短信验证码返回数据："+ JsonUtil.getJsonString(map));
		return JsonUtil.getJsonString(map);
	}
	/**
	 * 单位临时用户信息录入提交后校验
	 * @param mainContext
	 * @return
	 */
	private boolean out_step1(MainContext mainContext){
		DataPool dataPool = mainContext.getDataPool();
		//获取加密后的短信验证码
		String hdxyzm = dataPool.getString("hdxyzm");
		//获取密钥
		String token = dataPool.getString("token");
		//用密钥将短信验证码解密
		String dxyzm = AesUtil.deCode(hdxyzm,token);
		dataPool.put("dxyzm",dxyzm);
		dataPool.put("ylsjh",dataPool.getString("jbrsjhm"));
		//校验手机验证码
		TransEngine.getInstance().execute("GGTrans_0003",mainContext);
        // 单位临时用户信息真实性校验
        TransEngine.getInstance().execute("TranWJMA03",mainContext);
        
		return true;
	}
	 
	/**
	 * 密码修改成功
	 * @param mainContext
	 * @return
	 */
	private boolean out_step2(MainContext mainContext){
		// 调用密码重置成功接口处理
		DataPool pool=mainContext.getDataPool();
		if (pool.getString(Constants._WF_APPLY).equals(Constants.APP_YES)) {
			//获取前台加密后的密码
			String hdlmm = pool.getString("hdlmm");
			//获取密钥
			String token = pool.getString("token");
			//用密钥将密码解密
			String dlmm = AesUtil.deCode(hdlmm, token);
			//将登录密码用md5加密后传递到后台
			if (StringUtils.isNotBlank(dlmm)) {
				pool.put("dlmm", EncryptionUtil.MD5Encode(dlmm));
			}
			TransEngine.getInstance().execute("TranWJMA04", mainContext);
		}
		return true;
	}

	/**生成随机字符串
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length) {
		String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

}
