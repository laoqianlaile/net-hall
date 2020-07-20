package com.yd.ish.flowhelper.dp;

import com.yd.basic.util.YDVoucherUtil;
import com.yd.biz.engine.TransEngine;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.ApplyUtil;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.workflow.FlowHelperI;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 名称：FlowWFDWKH01
 * <p>功能：单位开户流程助手<br>
 * @author 王赫
 * @version 0.1	2019年10月25日	王赫创建
 * @note
 */
@Component
public class FlowWFDWKH01 implements FlowHelperI {


	private static final String STEP_STEP1="step1";//单位开户
	/*交易调用示例，trancode为具体交易代码
	TransEngine.getInstance().execute("trancode",mainContext);
	*/
	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
//		String sbrzh = mainContext.getDataPool().getString("jbrzjhm");// 需要校验在途业务的申报人账号
//		String sbzt = "0,1"; // 未处理和处理中的申报状态
//		String wtlcbh = "WFDWKH01"; // 有冲突的网厅流程编号
//		JSONObject json = ApplyUtil.isTreated(sbrzh, sbzt, wtlcbh);
//		if(("true").equals(json.get("ztywFlag").toString())){
//			return true;
//		}else{
//			throw new TransSingleException("您有未完成的冲突申报业务，请耐心等待。","申报信息"+json);
//		}
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		return null;
	}

}
