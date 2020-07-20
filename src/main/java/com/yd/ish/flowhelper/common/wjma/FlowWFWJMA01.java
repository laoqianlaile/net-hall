package com.yd.ish.flowhelper.common.wjma;

import com.yd.svrplatform.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.yd.biz.engine.TransEngine;
import com.yd.biz.exception.TransSingleException;
import com.yd.org.util.CommonErrorCode;
import com.yd.org.util.EncryptionUtil;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.workflow.FlowHelperI;

import java.util.HashMap;

/**
 * 名称：FlowWFWJMA01.java
 * <p>功能：个人忘记密码流程助手 <br> 
 * @author 张洪超
 * @version 0.1	2017年9月27日	张洪超创建
 */
@Component
public class FlowWFWJMA01 implements FlowHelperI {

	private static final String STEP_STEP1="step1";//个人信息录入
	private static final String STEP_STEP2="step2";//重置密码
	
	/* 交易调用示例，trancode为具体交易代码
		TransEngine.getInstance().execute("trancode",mainContext);
	*/
	
	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
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
	/**
	 * 个人信息录入提交后校验
	 * @param mainContext
	 * @return
	 */
	private boolean out_step1(MainContext mainContext){
		//随机验证码校验
//		TransEngine.getInstance().execute("GGTrans_0001",mainContext);
//		DataPool datapool = mainContext.getDataPool();
//        String jg = String.valueOf(datapool.get("jg"));//获取验证结果
//        //获取验证结果
//        if(!StringUtils.equals("0",jg)) {
//        	throw new TransSingleException(CommonErrorCode.ERROR_INPUT,"验证码");
//        }
		TransEngine.getInstance().execute("TranDXYZM1",mainContext);
        // 用户信息真实性校验&&密码是否为初始密码校验
        TransEngine.getInstance().execute("TranWJMA01",mainContext); 
        
		return true;
	}
	 
	/**
	 * 密码修改成功
	 * @param mainContext
	 * @return
	 */
	private boolean out_step2(MainContext mainContext){
		// 调用密码重置成功接口处理
		DataPool pool = mainContext.getDataPool();
		String dlmm = pool.getString("dlmm");
//		if(StringUtils.isNotBlank(dlmm)){
//			pool.put("dlmm", EncryptionUtil.MD5Encode(dlmm));
//		}
		TransEngine.getInstance().execute("TranWJMA02",mainContext); 
		return true;
	}

}
