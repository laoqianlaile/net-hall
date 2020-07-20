package com.yd.ish.flowhelper.dp;

import java.util.Map;


import com.yd.basic.service.ILoginService;
import com.yd.ish.common.util.TuoMinUtil;
import com.yd.ish.service.HomeData;
import com.yd.svrplatform.comm_mdl.context.UserContext;
import com.yd.svrplatform.util.ReadProperty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.engine.TransEngine;
import com.yd.biz.exception.TransSingleException;
import com.yd.org.util.CommonErrorCode;
import com.yd.org.util.EncryptionUtil;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.workflow.FlowHelperI;

 /**
 * 名称：FlowWFSJZHGL01
 * <p>功能： 手机号码与公积金账号关联<br>
 * @brief 手机号码与公积金账号关联 
 * @author 
 * @version 版本号	修改人	修改时间		地点	原因 
 * @note    0.1		许永峰	2018年7月27日	长春 	创建
 */
@Component
public class FlowWFSJZHGL01 implements FlowHelperI {

	private static final Logger logger = LoggerFactory.getLogger(FlowWFZFMMZH01.class);
	private static final String STEP_STEP1="step1";//手机号码与公积金账号关联
	
	/*交易调用示例，trancode为具体交易代码
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
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		if (StringUtils.equals("cmdhqyzm", task)) {
			return cmdhqyzm(mainContext);
		}
		return null;
	}
	
	private boolean out_step1(MainContext mainContext){
		DataPool datapool = mainContext.getDataPool();
		// 用户预留手机号
		datapool.put("ylsjh",datapool.getString("sjhm") );
		// 调用验证码录入是否正确校验
		TransEngine.getInstance().execute("GGTrans_0003", mainContext);
		//验证通过后提交关联信息
		datapool.put("flag", "tjxx");
		String jymm = datapool.getString("jymm");
		if(StringUtils.isNotBlank(jymm)){
			datapool.put("jymm", EncryptionUtil.MD5Encode(jymm));
		}
		TransEngine.getInstance().execute("TranSJZHGL01", mainContext);
		// 若修改了登录总线中或者首页展示的信息项，需更新登录总线及页面展示项的值
		UserContext user = MainContext.currentMainContext().getUserContext();
		// 获取登录时的user中的总线
		DataPool loginpool = (DataPool) user.getAttribute(ILoginService.EXTINFOKEY);
		// 业务中修改的字段id若与首页展示字段id相同，或与登录时user中赋值字段id相同的，需要重新进行赋值
		loginpool.put("sjhm", datapool.getString("sjhm"));
		//脱敏功能使用
		String tuomin_sfqy = ReadProperty.getString("tuomin_sfqy");
		if("1".equals(tuomin_sfqy)){
			TuoMinUtil.tMDataPool(loginpool);
		}
		user.setAttribute(ILoginService.EXTINFOKEY, loginpool);
		String homedata = HomeData.getPersonHomeData(loginpool, user);
		user.setAttribute("homedata", homedata);
		MainContext.currentMainContext().setUserContext(user);
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
		String ylsjh = pool.getString("sjhm");
		pool.put("flag", "jyjymm");
		//调用TranSJZHGL01 校验交易密码是否正确
		String jymm = pool.getString("jymm");
		if(StringUtils.isNotBlank(jymm)){
			pool.put("jymm", EncryptionUtil.MD5Encode(jymm));
		}
		TransEngine.getInstance().execute("TranSJZHGL01", mainContext);
		if(!"true".equals(pool.getString("jymmCorrect"))){
			throw new TransSingleException(CommonErrorCode.ERROR_INPUT,"交易密码");
		}
		// 用户预留手机号    
		pool.put("ylsjh",ylsjh);
		// 调用发送短信验证码交易 ,会调用KF00-API-COM-001接口
		Map<String, Object> map = TransEngine.getInstance().execute("GGTrans_0002", mainContext);
		logger.info("---发送短信验证码返回数据：" + JsonUtil.getJsonString(map));
		return JsonUtil.getJsonString(map);
	}
}
