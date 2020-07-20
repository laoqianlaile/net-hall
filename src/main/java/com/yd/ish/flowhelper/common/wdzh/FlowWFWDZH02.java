package com.yd.ish.flowhelper.common.wdzh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.workflow.FlowHelperI;
import com.yd.svrplatform.comm_mdl.context.MainContext;

/**
 * 名称：FlowWFWDZH02
 * <p>功能： 进入单位视图我的账户页面<br>
 * @brief 功能简述
 * @author
 * @version 版本号	修改人	修改时间	地点	原因
 * @note    0.1      许永峰   20191204 长春 新建
 */
@Component
public class FlowWFWDZH02 implements FlowHelperI {

	private static final Logger logger = LoggerFactory.getLogger(FlowWFWDZH02.class);
	
	private static final String STEP_STEP1="step1";//单位视图我的账户
	
	/*交易调用示例，trancode为具体交易代码
	TransEngine.getInstance().execute("trancode",mainContext);
	*/
	
	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {

		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {

		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		
		return null;
	}
	

}
