package com.yd.ish.flowhelper.ln;

import com.yd.basic.expression.IshExpression;
import com.yd.workflow.util.Constants;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;
import org.apache.log4j.DailyRollingFileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.workflow.FlowHelperI;
import com.yd.svrplatform.comm_mdl.context.MainContext;

import java.util.HashMap;

/**
 * 名称：FlowWFXMWH01
 * <p>功能： 开发商项目维护流程助手
  * @brief 开发商项目维护流程助手
  * @author 柏慧敏
  * @version 0.1	2019年6月17日	柏慧敏创建
 * @note
 */
@Component
public class FlowWFXMWH01 implements FlowHelperI {

	private static final Logger logger = LoggerFactory.getLogger(FlowWFXMWH01.class);
	
	private static final String STEP_STEP1="step1";//开发商项目查询
	private static final String STEP_STEP2="step2";//开发商项目维护
	
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
		if(stepid.equals(STEP_STEP2)){
			return out_step2(mainContext);
		}
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		DataPool pool = mainContext.getDataPool();
		// 检查楼栋信息
		if("CMD01".equals(task)){
			pool.put("flag", "checkldxx");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranXMXQ01",mainContext);
			return JsonUtil.getJsonString(ret);
		}
		// 检查楼盘信息
		else if("CMD02".equals(task)){
			pool.put("flag", "checklpxx");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranXMXQ01",mainContext);
			return JsonUtil.getJsonString(ret);
		}
		// 检查楼栋楼盘信息是否修改
		else if("CMD03".equals(task)){
			pool.put("flag", "checksfxg");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranXMWH01",mainContext);
			return JsonUtil.getJsonString(ret);
		}
		// 调用交易查询项目信息列表
		if("CMD04".equals(task)){
			TransEngine.getInstance().execute("TranXMWH01",mainContext);
		}
		// 返回跳转地址
		if("CMD05".equals(task)){
			HashMap<String, Object> map = new HashMap<String, Object>();
			// 跳转至项目信息详细页面(列表当前页currentPage，项目协议号xmxyh，实例号slh)
			String url = IshExpression.encryptionUrl("WFXMWH01", "a=1,currentPage=" + pool.getString("currentPage")
					+ ",slh=" + pool.getString("_IS") + ",xmxyh=" + pool.getString("xmxyh"), 0);
			map.put("url", url);
			return JsonUtil.getJsonString(map);
		}
		return null;
	}
	
	private boolean in_step1(String type,MainContext mainContext){
		mainContext.getDataPool().put("flag","instep1");
		// 调用交易查询项目信息列表
		TransEngine.getInstance().execute("TranXMWH01",mainContext);
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
		mainContext.getDataPool().put("flag","instep2");
		//调用交易查询项目详细信息
		TransEngine.getInstance().execute("TranXMWH01",mainContext);
		return true;
	}

	private boolean out_step2(MainContext mainContext){
		DataPool pool = mainContext.getDataPool();
		// 点提交调用交易
		if (pool.getString(Constants._WF_APPLY).equals(Constants.APP_YES)) {
			pool.put("flag","outstep2");
			//调用交易提交项目维护信息
			TransEngine.getInstance().execute("TranXMWH01",mainContext);
		}
		return true;
	}
}
