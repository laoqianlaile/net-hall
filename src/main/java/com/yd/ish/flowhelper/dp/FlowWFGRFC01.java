package com.yd.ish.flowhelper.dp;

import java.util.HashMap;

import com.yd.basic.util.YDVoucherUtil;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.workflow.FlowHelperI;
/**
 *
 * 名称：FlowWFGRFC01.java
 * <p>功能： 封存流程助手<br>
 * @author wanghe
 * @version 0.1	2018年5月24日	wanghe创建
 */
@Component
public class FlowWFGRFC01 implements FlowHelperI {


	private static final String STEP_STEP1="step1";//封存
	private static final String STEP_STEP2="step2";//打印凭证

	@Autowired
	YDVoucherUtil yDVoucherUtil;

	@Autowired
	ParamConfigImp paramconfigImp;

	/*交易调用示例，trancode为具体交易代码
	TransEngine.getInstance().execute("trancode",mainContext);
	*/

	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		if(STEP_STEP2.equals(stepid)){
			return in_step2(mainContext,type);
		}
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
		if (stepid.equals(STEP_STEP1)) {
			return out_step1(mainContext);
		}
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {

		if("queryGRJCXX".equals(task)){
			return queryGRJCXX(mainContext);
		}

		return null;
	}
	/**
	 * 获取个人缴存信息
	 * @param mainContext
	 * @return
	 */
	public String queryGRJCXX(MainContext mainContext) {

		DataPool dataPool = mainContext.getDataPool();
		dataPool.put("taskFlag", "queryGRJCXX");
		//调用交易TranGRJCXX01
		HashMap<String, Object> map = TransEngine.getInstance().execute("TranGRJCXX01", mainContext);
		return JsonUtil.getJsonString(map);

	}
	//提交封存数据
	private boolean out_step1(MainContext mainContext) {
		// 调用批量异常获取接口
		TransEngine.getInstance().execute("TranTJFCXX01",mainContext);
		return true;
	}

    //打印凭证
    private boolean in_step2(MainContext mainContext, String type) {
        //查询封存人员信息
        DataPool dataPool = mainContext.getDataPool();
        dataPool.put("slh", dataPool.getString("_IS"));
        String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "GRFCPZ01");
        mainContext.getDataPool().put("pdfKey", poolkey);
        return true;
    }

}
