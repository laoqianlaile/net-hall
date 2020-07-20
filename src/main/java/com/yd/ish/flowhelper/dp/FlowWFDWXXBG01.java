package com.yd.ish.flowhelper.dp;

import com.yd.ish.common.util.TuoMinUtil;
import com.yd.svrplatform.util.ReadProperty;
import org.springframework.stereotype.Component;

import com.yd.basic.expression.IshExpression;
import com.yd.basic.service.ILoginService;
import com.yd.biz.engine.TransEngine;
import com.yd.ish.service.HomeData;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.context.UserContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.workflow.FlowHelperI;
import com.yd.workflow.util.Constants;
import org.springframework.stereotype.Component;

/**
 * 
 * 名称：FlowWFDWXXBG01.java
 * <p>功能：单位基本信息变更流程助手 <br> 
 * @author 王赫
 * @version 0.1 2018年6月1日 王赫创建
 * @version 0.1	2018年6月1日	王赫创建
 *
 */
@Component
public class FlowWFDWXXBG01 implements FlowHelperI {

	private static final String STEP_STEP1 = "step1";// 单位基本信息变更

	/*
	 * 交易调用示例，trancode为具体交易代码
	 * TransEngine.getInstance().execute("trancode",mainContext);
	 */

	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		// 获取单位信息 根据单位名称获取单位基本信息与经办人信息
		if (STEP_STEP1.equals(stepid)) {
			return in_step1(type, mainContext);
		}
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
		if (STEP_STEP1.equals(stepid)) {
			return out_step1(mainContext);
		}
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		if("cmdJYJBR".equals(task)){
			TransEngine.getInstance().execute("TranDWXXBG03",mainContext);
		}
		return null;
	}

	/**
	 * 获取单位基本信息和经办人信息
	 *
	 * @param type
	 * @param mainContext
	 * @return
	 */
	private boolean in_step1(String type, MainContext mainContext) {
		DataPool pool = mainContext.getDataPool();
		TransEngine.getInstance().execute("TranHQDWXX01", mainContext);
		return true;
	}

	/**
	 * 提交单位和经办人变更信息
	 *
	 * @param mainContext
	 * @return
	 */
	private boolean out_step1(MainContext mainContext) {
		TransEngine.getInstance().execute("TranDWXXBG02", mainContext);

		// 若修改了首页展示的信息项，改变首页展示信息项的值
		DataPool pool = mainContext.getDataPool();
		UserContext user = MainContext.currentMainContext().getUserContext();
		// 获取登录时的user中的总线
        DataPool loginpool = (DataPool) user.getAttribute(ILoginService.EXTINFOKEY);
        // 业务中修改的字段id若与首页展示字段id相同，或与登录时user中赋值字段id相同的，需要重新进行赋值
        loginpool.put("dwslrq", pool.getString("dwslrq"));
        loginpool.put("dwyb", pool.getString("dwyb"));
		//脱敏功能使用
		String tuomin_sfqy = ReadProperty.getString("tuomin_sfqy");
		if("1".equals(tuomin_sfqy)){
			TuoMinUtil.tMDataPool(loginpool);
		}
		user.setAttribute(ILoginService.EXTINFOKEY, loginpool);
		String homedata = HomeData.getOrgHomeData(loginpool, user);
		user.setAttribute("homedata", homedata);
		MainContext.currentMainContext().setUserContext(user);
		return true;
	}
}
