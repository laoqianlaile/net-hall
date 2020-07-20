package com.yd.ish.flowhelper.common.exa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.basic.util.YDVoucherUtil;
import com.yd.ish.common.service.mybatis.CPLHelpService;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.workflow.FlowHelperI;

/**
 * 
 * 名称：FlowWFYBTJSL02.java
 * <p>
 * 功能： 异步提交显示凭证示例功能流程助手<br>
 * 
 * @author 张洪超
 * @version 0.1 2018年1月30日 张洪超创建
 */
@Component
public class FlowWFYBTJSL02 implements FlowHelperI {

	private static final Logger logger = LoggerFactory.getLogger(FlowWFYBTJSL02.class);

	private static final String STEP_STEP1 = "step1";// 异步提交信息录入
	private static final String STEP_STEP2 = "step2";// 异步提交信息继续录入
	/*
	 * 交易调用示例，trancode为具体交易代码
	 * TransEngine.getInstance().execute("trancode",mainContext);
	 */
	@Autowired
	CPLHelpService cplHelpService;
	@Autowired
	YDVoucherUtil yDVoucherUtil;

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

		return null;
	}

	private boolean out_step1(MainContext mainContext) {
		logger.debug("---------------");
		logger.debug("异步提交step1");
		return true;
	}

	private boolean out_step2(MainContext mainContext) {
		logger.debug("---------------");
		logger.debug("异步提交后step2");
		// 测试异步提交线程单独执行，提交回滚
//		cplHelpService.deleteHelp("1111", "1111");
//		CPLHelp temp = new CPLHelp();
//		temp.setXh("1111");
//		temp.setBzxx("213213");
//		temp.setFzbz("1");
//		temp.setHqfs("1");
//		temp.setHqz("123");
//		temp.setJdbh("1");
//		temp.setLbid("YFDD");
//		temp.setLbwz("1");
//		temp.setYwlc("1111");
//		temp.setJdbh("1111");
//		cplHelpService.insertHelp(temp);
//		// 测试异步提交线程单独执行，提交回滚
//		CPLHelp temp1 = new CPLHelp();
//		temp1.setXh("1112");
//		temp1.setBzxx("213213");
//		temp1.setFzbz("1");
//		temp1.setHqfs("1");
//		temp1.setHqz("123");
//		temp1.setJdbh("1");
//		temp1.setLbid("YFDD");
//		temp1.setLbwz("1");
//		temp1.setYwlc("1111");
//		temp1.setJdbh("1111");
//		cplHelpService.insertHelp(temp1);
		
		// 获取打印模板
		String poolkey = yDVoucherUtil.saveWordVoucher(mainContext.getDataPool(), "YBTJSL01");
		mainContext.getDataPool().put("wordKey", poolkey);
		
		// 获取打印模板1
		String poolkey1 = yDVoucherUtil.saveWordVoucher(mainContext.getDataPool(), "YBTJSL02");
		mainContext.getDataPool().put("wordKey1", poolkey1);
		return true;
		
	}

}
