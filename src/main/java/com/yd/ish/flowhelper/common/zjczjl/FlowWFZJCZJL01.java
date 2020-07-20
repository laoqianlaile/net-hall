package com.yd.ish.flowhelper.common.zjczjl;

import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.util.DataPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.workflow.FlowHelperI;
import com.yd.svrplatform.comm_mdl.context.MainContext;

 /**
 * 名称：FlowWFZJCZJL01
 * <p>功能： 个人最近操作记录查询流程助手<br>
 * @brief 功能简述
 * @author 
 * @version 版本号	修改人	修改时间	地点	原因
 * @note    v0.1    许永峰   20191217 长春 创建
 */
@Component
public class FlowWFZJCZJL01 implements FlowHelperI {

	private static final Logger logger = LoggerFactory.getLogger(FlowWFZJCZJL01.class);
	
	private static final String STEP_STEP1="step1";//个人最近操作记录
	
	/*交易调用示例，trancode为具体交易代码
	TransEngine.getInstance().execute("trancode",mainContext);
	*/
	@Autowired
	ParamConfigImp paramConfigImp;
	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		if(stepid.equals(STEP_STEP1)){
			return in_step1(type,mainContext);
		}
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
	
	private boolean in_step1(String type,MainContext mainContext){
		DataPool  pool = mainContext.getDataPool();
		String mask = "WTGR";
		String maskVal = paramConfigImp.getValByMask("ish.gg.func.systemtype" , mask);
		pool.put("systemtype",maskVal);
		return true;
	}

}
