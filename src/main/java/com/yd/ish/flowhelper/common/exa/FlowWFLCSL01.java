package com.yd.ish.flowhelper.common.exa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.workflow.FlowHelperI;

/**
 * 名称：FlowWFLCSL01.java
 * <p>功能：流程可视化示例流程助手<br> 
 * @author 张洪超
 * @version 0.1	2017年9月26日	张洪超创建
 */
@Component
public class FlowWFLCSL01 implements FlowHelperI {

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
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		
		return null;
	}
}
