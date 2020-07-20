package com.yd.ish.flowhelper.common.plmbxz;


import org.springframework.stereotype.Component;

import com.yd.workflow.FlowHelperI;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
/**
* 名称：FlowISH880502
* <p>功能：批量模板下载<br>
* @brief 用户升级
* @author 王站龙
* @version 1.0 王站龙  2018年01月03日 长春 新增
* @note
*/
@Component
public class FlowWFPLMBXZ01 implements FlowHelperI {

	
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
		TransEngine.getInstance().execute("TranWDSCGL03", mainContext, false);
		return null;
	}
	

}
