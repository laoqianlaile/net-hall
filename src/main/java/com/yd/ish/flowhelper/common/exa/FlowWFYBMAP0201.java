package com.yd.ish.flowhelper.common.exa;

import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.workflow.FlowHelperI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 名称：FlowWFYBMAP0201.java
 * <p>功能：综服2.0测试示例流程助手 <br>
 * @author 武丹
 * @version 0.1	2018年5月15日	武丹创建
 */
@Component
public class FlowWFYBMAP0201 implements FlowHelperI {

	@Autowired
	ParamConfigImp paramConfigImp;
	
	/*交易调用示例，trancode为具体交易代码
		TransEngine.getInstance().execute("trancode",mainContext);
	*/
	
	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
		TransEngine.getInstance().execute("TranYBMAP0201",mainContext);
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		
		return null;
	}

}
