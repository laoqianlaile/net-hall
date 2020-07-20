package com.yd.ish.flowhelper.dp;

import org.springframework.stereotype.Component;

import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.workflow.FlowHelperI;

 /**
 * 名称：FlowWFDWTSXX01
 * <p>功能： 单位托收信息查询流程助手<br>
 * @brief 查询单位托收信息 
 * @author  许永峰
 * @version 版本号	修改人	修改时间	            地点	  原因 
 * @note     0.1    许永峰     2018年6月19日      长春   新建
 */
@Component
public class FlowWFDWTSXX01 implements FlowHelperI {

	//private static final Logger logger = LoggerFactory.getLogger(FlowWFDWTSXX01.class);
	
	private static final String STEP_STEP1="step1";//单位托收信息
	
	/*交易调用示例，trancode为具体交易代码
	TransEngine.getInstance().execute("trancode",mainContext);
	*/
	
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
		TransEngine.getInstance().execute("TranDWTSXX01",mainContext);
		return true;
	}

}
