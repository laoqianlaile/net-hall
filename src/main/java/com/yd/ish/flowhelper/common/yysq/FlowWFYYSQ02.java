package com.yd.ish.flowhelper.common.yysq;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.basic.expression.IshExpression;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.workflow.FlowHelperI;

/**
 * 名称：FlowWFYYSQ02.java
 * <p>功能：预约查询流程助手<br> 
 * @author 张洪超
 * @version 0.1	2018年1月2日	张洪超创建
 */
@Component
public class FlowWFYYSQ02 implements FlowHelperI {

//	private static final Logger logger = LoggerFactory.getLogger(FlowWFYYCX01.class);
	
//	private static final String STEP_STEP1="step1";//预约查询
	
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
		if ("yyywQx".equals(task)) {
			// 取消预约
			return cmd_01(mainContext);
		} 
		return null;
	}
	/**
	 * 预约取消
	 * 
	 * @param mainContext
	 * @return
	 */
	private String cmd_01(MainContext mainContext) {
		// 操作状态为取消 
		DataPool dataPool = mainContext.getDataPool();
		// 客户登记号
		dataPool.put("khdjh", IshExpression.getRealUserExtInfo("yhdjh"));
		// 客户标识
		dataPool.put("khbs", IshExpression.getUserExtInfo("khbs"));
		// 取消标志
		String czzt = paramConfigImp.getValByMask("ish.gg.yy.yyczzt", "CXYY");
		dataPool.put("czzt", czzt);
		// 调用预约取消接口
		HashMap<String, Object> map = TransEngine.getInstance().execute("TranYYSQJK06", mainContext);
		return JsonUtil.getJsonString(map);
	}

}
