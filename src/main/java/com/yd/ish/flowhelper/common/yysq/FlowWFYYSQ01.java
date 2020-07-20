package com.yd.ish.flowhelper.common.yysq;

import java.util.HashMap;

import com.yd.ish.common.interfaces.IYYManager;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.basic.expression.IshExpression;
import com.yd.basic.util.YDVoucherUtil;
import com.yd.biz.engine.TransEngine;
import com.yd.org.util.GetChannelCode;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.key_generator.SimpleKeyGenerator;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.spring.ApplicationContextHelper;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.workflow.FlowHelperI;
import com.yd.workflow.util.Constants;

/**
 * 名称：FlowWFYYSQ01.java
 * <p>
 * 功能：预约申请流程助手  <br>
 * 
 * @author 张洪超
 * @version 0.1 2017年12月13日 张洪超创建
 */
@Component
public class FlowWFYYSQ01 implements FlowHelperI {

	private static final Logger logger = LoggerFactory.getLogger(FlowWFYYSQ01.class);

	/** 流程参数 **/
	private static final String STEP_STEP1 = "step1";// 预约业务选择
	private static final String STEP_STEP2 = "step2";// 预约网点选择、日期选择
	private static final String STEP_STEP3 = "step3";// 预约信息确认
	private static final String STEP_STEP4 = "step4";// 预约打印

	@Autowired
	ParamConfigImp paramConfigImp;

	@Autowired
	SimpleKeyGenerator sim;
	@Autowired
	YDVoucherUtil yDVoucherUtil;

	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		if (stepid.equals(STEP_STEP1)) {
			return in_step1(type, mainContext);
		} else if (stepid.equals(STEP_STEP2) && "task".equals(type)) {
			return in_step2(type, mainContext);
		} else if (stepid.equals(STEP_STEP4) && "task".equals(type)) {
			return in_step4(type, mainContext);
		}
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
			if (stepid.equals(STEP_STEP2)) {
				return out_step2(mainContext);
			} else if (stepid.equals(STEP_STEP3)) {
				return out_step3(mainContext);
			}
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {

		if ("checkYyyw".equals(task)) {
			// 返回已设置的业务信息
			return cmdCheckYyyw(mainContext);
		}else if ("sendMsg".equals(task)) {
			// 将预约结果发送短信
			return cmd_02(mainContext);
		} else if ("sendWeChat".equals(task)) {
			// 将预约结果发送微信
			return cmd_03(mainContext);
		} else if ("getCalendar".equals(task)) {
			// 重新获取日历
			return cmd_04(mainContext);
		} else if ("getSJD".equals(task)) {
			// 获取时间段
			return cmd_05(mainContext);
		}
		return null;
	}

	private boolean in_step1(String type, MainContext mainContext) {

		// 单日最大预约笔数（每用户）
		mainContext.getDataPool().put("zdyys", paramConfigImp.getVal("ish.gg.yy.yyggcs.04"));
		// 单笔预约最大业务数
		mainContext.getDataPool().put("zdyws", paramConfigImp.getVal("ish.gg.yy.yyggcs.07"));
		// 渠道标识
		mainContext.getDataPool().put("qdbs", GetChannelCode.getNP());
		// 使用标志
		mainContext.getDataPool().put("ywsybz", paramConfigImp.getValByMask("ish.gg.func.status", "ZC"));

		return true;
	}

	private boolean in_step2(String type, MainContext mainContext) {

		// 提前预约天数
		String id = paramConfigImp.getValByMask("ish.gg.yy.yyggcs", "ZDYYTS");
		int zdyyts = Integer.parseInt(paramConfigImp.getVal("ish.gg.yy.yyggcs." + id));
		mainContext.getDataPool().put("zdyyts",zdyyts);
		//调用预约业务和网点关联查询交易
		TransEngine.getInstance().execute("TranYYSQJK01", mainContext);
		// 获得所有可办理网点信息
		TransEngine.getInstance().execute("TranYYSQ0201", mainContext);
		// 获取日历
		TransEngine.getInstance().execute("TranYYSQ0202", mainContext);

		return true;
	}

	private boolean in_step4(String type, MainContext mainContext) {

		// 获取打印模板
		String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "YYSQ01");
		mainContext.getDataPool().put("pdfKey", poolkey);

		return true;
	}

	private String cmdCheckYyyw(MainContext mainContext) {

		DataPool pool = mainContext.getDataPool();
		// 调用业务具体校验
		IYYManager yyManager = (IYYManager) ApplicationContextHelper.getBean("YY" + pool.get("ywlch"));
		if (yyManager != null) {
			yyManager.checkYYData(pool);
		}
		pool.put("khdjh", IshExpression.getRealUserExtInfo("yhdjh"));
		pool.put("khbs", IshExpression.getUserExtInfo("khbs"));
		// 调用接口校验用户所选择的业务是否可以继续预约
		HashMap<String,Object> map = TransEngine.getInstance().execute("TranYYJYJK01", mainContext);
		return JsonUtil.getJsonString(map);
	}

	private boolean out_step2(MainContext mainContext) {
		DataPool pool = mainContext.getDataPool();
		if (mainContext.getDataPool().getString(Constants._WF_APPLY).equals(Constants.APP_YES)) {
			// 校验用户所选择的时间、网点是否可以继续预约
			pool.put("khdjh", IshExpression.getRealUserExtInfo("yhdjh"));
			pool.put("khbs", IshExpression.getUserExtInfo("khbs"));
			TransEngine.getInstance().execute("TranYYJYJK02", mainContext);
		}
		return true;
	}

	private boolean out_step3(MainContext mainContext) {
		DataPool pool = mainContext.getDataPool();
		// 调用预约成功对应业务的返回处理
		if (pool.getString(Constants._WF_APPLY).equals(Constants.APP_YES)) {
			IYYManager yyManager = (IYYManager) ApplicationContextHelper.getBean("YY" + pool.get("ywlch"));
			if (yyManager != null) {
				yyManager.callbackYYYW(pool);
			}
			// 渠道-网厅
			DataPool dataPool=mainContext.getDataPool();
			//渠道标识
			dataPool.put("qdbs", GetChannelCode.getNP());
			// 客户登记号
			dataPool.put("khdjh", IshExpression.getRealUserExtInfo("yhdjh"));
			// 客户标识
			dataPool.put("khbs", IshExpression.getUserExtInfo("khbs"));
			// 渠道-网厅
			dataPool.put("qdbs", GetChannelCode.getNP());// 实例号
			String instanceid = dataPool.getString(Constants._WF_IS);
			dataPool.put("instanceid", instanceid);
			// 是否上传材料
			dataPool.put("isarchives", StringUtils.isNotBlank(dataPool.getString("isarchives"))?dataPool.getString("isarchives"):paramConfigImp.getValByMask("ish.gg.other.sf", "NO"));
			// 调用预约申请接口
			TransEngine.getInstance().execute("TranYYSQJK04", mainContext);
		}
		return true;
	}

	/**
	 * 发送手机短信
	 * 
	 * @param mainContext
	 * @return
	 */
	private String cmd_02(MainContext mainContext) {

		logger.info("[+]调用外联接口发送预约成功短信开始。");

		logger.info("[-]调用外联接口发送预约成功短信结束。");

		return "{}";
	}

	/**
	 * 发送微信
	 * 
	 * @param mainContext
	 * @return
	 */
	private String cmd_03(MainContext mainContext) {

		logger.info("[+]调用外联接口发送预约成功微信开始。");

		logger.info("[-]调用外联接口发送预约成功微信结束。");

		return "{}";
	}
	
	/**
	 * 重新获取日历
	 * 
	 * @param mainContext
	 * @return
	 */
	private String cmd_04(MainContext mainContext) {
		TransEngine.getInstance().execute("TranYYSQJK02", mainContext);
		HashMap<String,Object> map  = TransEngine.getInstance().execute("TranYYSQ0203", mainContext);
		return map.get("calendarList").toString();
		
	}
	/**
	 * 获取时间段
	 * 
	 * @param mainContext
	 * @return
	 */
	private String cmd_05(MainContext mainContext) {
		TransEngine.getInstance().execute("TranYYSQJK03", mainContext);
		HashMap<String,Object> map  = TransEngine.getInstance().execute("TranYYSQ0204", mainContext);
		return JsonUtil.getJsonString(map);
		
	}
}
