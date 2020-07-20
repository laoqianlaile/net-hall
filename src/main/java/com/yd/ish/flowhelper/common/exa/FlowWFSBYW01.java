package com.yd.ish.flowhelper.common.exa;

import org.springframework.stereotype.Component;

import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.workflow.FlowHelperI;

/**
 * 名称：FlowWFSBYW01.java
 * <p>功能：申报业务预办理-示例流程助手 <br> 
 * @author 张洪超
 * @version 0.1	2017年10月18日	张洪超创建
 */
@Component
public class FlowWFSBYW01 implements FlowHelperI {

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
