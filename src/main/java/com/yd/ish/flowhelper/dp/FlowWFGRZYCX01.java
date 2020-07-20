package com.yd.ish.flowhelper.dp;

import org.springframework.stereotype.Component;

import com.yd.workflow.FlowHelperI;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;

 /**
 * 名称：FlowWFGRZYCX01
 * <p>功能： 单位下个人转移查询流程助手<br>
 * @author 柏慧敏
 * @version 0.1	2018年5月23日	柏慧敏创建
 * @note
 */
@Component
public class FlowWFGRZYCX01 implements FlowHelperI {
	
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
		//查询个人转移信息
		if ("cmdCX".equals(task)) {
			TransEngine.getInstance().execute("TranGRZYCX01",mainContext);
		}
		return null;
	}
	

}
