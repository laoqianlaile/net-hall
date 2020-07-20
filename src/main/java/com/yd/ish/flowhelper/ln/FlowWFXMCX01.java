package com.yd.ish.flowhelper.ln;

import com.yd.basic.expression.IshExpression;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.workflow.FlowHelperI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;


/**
 * 名称：FlowWFXMCX01
 * <p>功能： 开发商项目查询流程助手<br>
 * @brief 开发商项目查询流程助手
 * @author 柏慧敏
 * @version 0.1	2019年6月10日	柏慧敏创建
 * @note
 */
@Component
public class FlowWFXMCX01 implements FlowHelperI {

	private static final Logger logger = LoggerFactory.getLogger(FlowWFXMCX01.class);
	
	private static final String STEP_STEP1="step1";//开发商项目查询
	private static final String STEP_STEP2="step2";//项目详细信息查询
	
	/*交易调用示例，trancode为具体交易代码
	TransEngine.getInstance().execute("trancode",mainContext);
	*/
	
	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		if(stepid.equals(STEP_STEP1)){
			return in_step1(type,mainContext);
		}
		if(stepid.equals(STEP_STEP2)){
			return in_step2(type,mainContext);
		}
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		if("CMD01".equals(task)){
			// 调用交易项目信息列表
			mainContext.getDataPool().put("flag","getxmlb");
			TransEngine.getInstance().execute("TranXMCX01",mainContext);
		}else if("CMD02".equals(task)){
			DataPool pool = mainContext.getDataPool();
			HashMap<String, Object> map = new HashMap<String, Object>();
			// 跳转至项目信息详细页面(列表当前页currentPage，项目协议号xmxyh，实例号slh)
			String url = IshExpression.encryptionUrl("WFXMCX01", "a=1,currentPage=" + pool.getString("currentPage")
					+ ",slh=" + pool.getString("_IS") + ",xmxyh=" + pool.getString("xmxyh"), 0);
			map.put("url", url);
			return JsonUtil.getJsonString(map);
		}
		return null;
	}
	
	private boolean in_step1(String type,MainContext mainContext){
		mainContext.getDataPool().put("flag","getxmlb");
		// 调用交易查询项目信息列表
		TransEngine.getInstance().execute("TranXMCX01",mainContext);
		return true;
	}

	private boolean in_step2(String type,MainContext mainContext){
		DataPool pool = mainContext.getDataPool();
		//要校验的数据
		pool.put("checkValue", pool.getString("xmxyh"));
		//对应的实体DP077的属性名
		pool.put("entityName", "unitaccnum1");
		//替换跳转后重新生成的实例号
		pool.put("_IS", pool.getString("slh"));
		//调用数据校验交易
		TransEngine.getInstance().execute("TranSJJY01", mainContext);
		mainContext.getDataPool().put("flag","getxmxx");
		//调用交易查询项目详细信息
		TransEngine.getInstance().execute("TranXMCX01",mainContext);
		return true;
	}
}
