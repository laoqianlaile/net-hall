package com.yd.ish.flowhelper.common.yhsj;

import java.util.Map;

import com.yd.org.util.GetChannelCode;
import com.yd.svrplatform.util.Datelet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.basic.expression.IshExpression;
import com.yd.biz.engine.TransEngine;
import com.yd.biz.exception.TransSingleException;
import com.yd.org.util.CommonErrorCode;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.workflow.FlowHelperI;
import com.yd.workflow.util.Constants;

/**
 * 名称：FlowWFYHSJ01.java
 * <p>功能：个人用户升级流程助手 <br> 
 * @author 张洪超
 * @version 0.1	2017年12月1日	张洪超创建
 *         V0.1 2018-9-26       王赫 修改签订协议调用接口改为调用交易
 */
@Component
public class FlowWFYHSJ01 implements FlowHelperI {

	private static final String STEP_STEP1="step1";//签订协议
	private static final String STEP_STEP2="step2";//实名认证
	private static final String STEP_STEP3="step3";//刷脸认证
	private static final String STEP_STEP4="step4";//修改密码
	
	private static final Logger logger = LoggerFactory.getLogger(FlowWFYHSJ01.class);

	@Autowired
	ParamConfigImp paramConfigImp;
	
	/*交易调用示例，trancode为具体交易代码
		TransEngine.getInstance().execute("trancode",mainContext);
	*/
	
	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		if(stepid.equals(STEP_STEP1)){
			return in_step1(type,mainContext);
		}
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
		if(stepid.equals(STEP_STEP2)){
			return out_step2(mainContext);
		}
		if(stepid.equals(STEP_STEP3)){
			return out_step3(mainContext);
		}
		if(stepid.equals(STEP_STEP4)){
			return out_step4(mainContext);
		}
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		if(StringUtils.equals("cmd001",task)){
            return cmd001(mainContext);
        }
		
		return null;
	}
	/**
	 * 发送验证码
	 * @param mainContext
	 * @return
	 */
    private String cmd001(MainContext mainContext){
    	 DataPool pool=mainContext.getDataPool();
		 // 调用校验用户输入的手机号和银行卡是否正确接口
		 pool.put("xingming",IshExpression.getRealUserExtInfo("xingming"));
		 pool.put("zjhm",IshExpression.getRealUserExtInfo("zjhm"));
		 TransEngine.getInstance().execute("TranYHYZ02",mainContext);
		 // 用户预留手机号
		 pool.put("ylsjh",pool.getString("yhylsjh"));
		 //调用发送短信验证码交易 ,会调用KF00-API-COM-001接口
		 Map<String,Object> map = TransEngine.getInstance().execute("GGTrans_0002",mainContext);
		 logger.info("---发送短信验证码返回数据："+JsonUtil.getJsonString(map));
		 return JsonUtil.getJsonString(map);
	 }
	
	private boolean in_step1(String type,MainContext mainContext){
		// 校验用户的证件类型是否为身份证并且身份证号必须为18位
		String sfz = paramConfigImp.getValByMask("ish.gg.user.zjlx","SFZ");
		if(!sfz.equals(IshExpression.getRealUserExtInfo("zjlx")) || IshExpression.getRealUserExtInfo("zjhm").length()!=18){
			throw new TransSingleException(CommonErrorCode.ERROR_YHWFSJ,"");
		}
		
		//获取个人注册的协议
		DataPool pool=mainContext.getDataPool();
		String systemtype=paramConfigImp.getValByMask("ish.gg.func.systemtype", IshConstants.SYSTEM_WTGR);
		pool.put("systemtype", systemtype);
		pool.put("flowid", pool.get(Constants._WF_FLOWID));
		pool.put("stepid", pool.get(Constants._WF_STEPID));
		pool.put("isPage", "0");
		TransEngine.getInstance().execute("TranXYBD03",mainContext);
		TransEngine.getInstance().execute("TranXYHQ01",mainContext);
		
		return true;
	}

	/**
	 * 录入用户银行卡信息&手机号信息后确认校验
	 * @param mainContext
	 * @return
	 */
	private boolean out_step2(MainContext mainContext){
		DataPool datapool = mainContext.getDataPool();
		// 调用校验用户输入的手机号和银行卡是否正确接口
		datapool.put("xingming",IshExpression.getRealUserExtInfo("yhxm"));//姓名
		datapool.put("zjlx",IshExpression.getRealUserExtInfo("zjlx"));//证件类型
		datapool.put("zjhm",IshExpression.getRealUserExtInfo("zjhm"));//证件号码
		TransEngine.getInstance().execute("TranYHYZ02",mainContext); 
		
	    // 用户预留手机号
		datapool.put("ylsjh",datapool.getString("yhylsjh"));
		// 调用验证码录入是否正确校验
		TransEngine.getInstance().execute("GGTrans_0003",mainContext);

		return true;
	}

	/**
	 * 刷脸认证通过
	 * @param mainContext
	 * @return
	 */
	private boolean out_step3(MainContext mainContext){
		DataPool dataPool = mainContext.getDataPool();
		// 用户信息真实性校验&&密码是否为初始密码校验
        TransEngine.getInstance().execute("TranYHYZ01",mainContext);
        
		// 判断用户是否需要修改密码，不需要修改直接注册成功&签订协议
		String no = paramConfigImp.getValByMask("ish.gg.other.sf", "NO");
		if(no.equals(dataPool.getString("isCscxmm")) && no.equals(dataPool.getString("isCsjymm"))){
			// 调用签订协议提交&注册接口
			this.success(mainContext);
		}
		return true;
	}
	/**
	 * 密码修改成功
	 * @param mainContext
	 * @return
	 */
	private boolean out_step4(MainContext mainContext){
		// 注册成功,调用签订协议提交&注册接口
		this.success(mainContext);
		return true;
	}

	/**
	 * 升级成功&用户同意签订协议提交处理
	 * @param mainContext
	 * @return
	 */
	private void success(MainContext mainContext){
		DataPool dataPool = mainContext.getDataPool();
		//以下5个字段为必输字段 （参数可以是界面上送也可以在数据总线获取）
        dataPool.put("khdjh",mainContext.getUserContext().getOperId());//客户登记号
        dataPool.put("xm",IshExpression.getUserExtInfo("yhxm"));//姓名
        dataPool.put("khbj","0");//客户标识	0-个人，1-单位
        dataPool.put("zjlx",IshExpression.getUserExtInfo("zjlx"));//证件类型
        dataPool.put("zjhm",IshExpression.getRealUserExtInfo("zjhm"));//证件号码

		//V0.1 start
		dataPool.put("ywbh",dataPool.getString("_WF"));//业务编号
		dataPool.put("ywmc",dataPool.getString("_FLOWNAME"));//业务名称
		dataPool.put("yhipdz",mainContext.getRemoteAddr());//用户IP地址
		dataPool.put("slh",dataPool.getString("_IS"));//实例号
		dataPool.put("czqd", GetChannelCode.getNP());//操作渠道
		dataPool.put("czrq", Datelet.getCurrentDate());//操作日期
		dataPool.put("xm", dataPool.getString("xingming"));//姓名
		//用户注册协议同意，调用用户签订协议接口记录用户签订的协议
//		TransEngine.getInstance().execute("TranXYQY01",mainContext);
		TransEngine.getInstance().execute("TranXYQD01",mainContext);
		//V0.1 end
		// 调用升级成功接口处理
		TransEngine.getInstance().execute("TranYHSJBC01",mainContext); 
	}

}
