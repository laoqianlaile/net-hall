package com.yd.ish.flowhelper.ln;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.workflow.FlowHelperI;
import com.yd.basic.expression.IshExpression;
import com.yd.basic.util.YDVoucherUtil;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;

/**
 * 名称：FlowWFTQHK01 功能：提前还款流程助手<br>
 *
 * @author 柏慧敏
 * @version 0.1 2018年7月2日 柏慧敏创建
 * @note
 */
@Component
public class FlowWFTQHK01 implements FlowHelperI {

	private static final String STEP_STEP1 = "step1";// 提前还款
	private static final String STEP_STEP2 = "step2";// 提前还款打印

	/*
	 * 交易调用示例，trancode为具体交易代码
	 * TransEngine.getInstance().execute("trancode",mainContext);
	 */
	@Autowired
	YDVoucherUtil yDVoucherUtil;

	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		if (stepid.equals(STEP_STEP1)) {
			return in_step1(type, mainContext);
		}
		if (stepid.equals(STEP_STEP2)) {
			return in_step2(type, mainContext);
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
		DataPool pool = mainContext.getDataPool();
		// 获取贷款账户信息
		if ("hqzhxx".equals(task)) {
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranHQDKXX01", mainContext);
			//获取公积金还款人信息
			pool.put("flag", "hqgjjhkr");
			TransEngine.getInstance().execute("TranTQHK01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		// 获取提前还款后的信息
		else if ("cktqhkxx".equals(task)) {
			pool.put("flag", "hqtqhkhxx");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranTQHK01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		// 生成跳转至贷款试算的url
		if ("CMD01".equals(task)) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			// 跳转至贷款试算页面流程，传递信息加密
			String url = IshExpression.encryptionUrl("WFDKSS01",
					"sslx=04,jkhtbhtz=" + pool.getString("jkhtbh") + ",tqhkzje=" + pool.getString("tqhkzje")
							+ ",dkbgbz=" + pool.getString("dkbgbz") + ",hklx=" + pool.getString("hklx") + ",jsfs="
							+ pool.getString("jsfs") + ",hygqs=" + pool.getString("hygqs"), 0);
			map.put("url", url);
			return JsonUtil.getJsonString(map);
		}
		// 根据月数获取提前还月供金额
		else if ("hqtqhygje".equals(task)) {
			pool.put("flag", "hqtqhygje");
			HashMap<String, Object> ret = TransEngine.getInstance().execute("TranTQHK01", mainContext);
			return JsonUtil.getJsonString(ret);
		}
		return null;
	}

	private boolean in_step1(String type, MainContext mainContext) {
		DataPool pool = mainContext.getDataPool();
		pool.put("dkzt", "2");// 0-已还清的贷款；1-申请中的贷款；2-已放款未还清的贷款；3-已放款
		// 获取借款合同编号
		TransEngine.getInstance().execute("TranJKHTBH01", mainContext);
		return true;
	}

	private boolean out_step1(MainContext mainContext) {
		DataPool pool = mainContext.getDataPool();
		// 提交提前还款信息
		pool.put("flag", "tjxx");
		TransEngine.getInstance().execute("TranTQHK01", mainContext);
		return true;
	}

    private boolean in_step2(String type, MainContext mainContext) {
        // 获取打印模板
        String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "TQHK01");
        mainContext.getDataPool().put("pdfKey", poolkey);
        return true;
    }
}
