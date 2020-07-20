package com.yd.ish.flowhelper.dp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.workflow.FlowHelperI;
import com.yd.basic.util.YDVoucherUtil;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;

/**
 * 名称：FlowWFGRKH01
 * <p>功能：个人开户流程助手<br>
 * @author 柏慧敏
 * @version 0.1	2018年5月29日	柏慧敏创建
 * @note
 */
@Component
public class FlowWFGRKH01 implements FlowHelperI {


	private static final String STEP_STEP1="step1";//个人开户
	private static final String STEP_STEP2="step2";//个人开户
	/*交易调用示例，trancode为具体交易代码
	TransEngine.getInstance().execute("trancode",mainContext);
	*/
	@Autowired
	YDVoucherUtil yDVoucherUtil;
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
		if(stepid.equals(STEP_STEP1)){
			return out_step1(mainContext);
		}
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		//校验批量信息
		if("CMD01".equals(task)){
			DataPool datapool=mainContext.getDataPool();
			datapool.put("flag", "jyxx");
			TransEngine.getInstance().execute("TranGRKH01", mainContext);
		}
		return null;
	}

	private boolean in_step1(String type,MainContext mainContext){
		//获取缴存比例信息
		TransEngine.getInstance().execute("TranHQDWXX01",mainContext);
		return true;
	}

	private boolean out_step1(MainContext mainContext){
		//提交个人账户设立批量信息
		DataPool datapool=mainContext.getDataPool();
		datapool.put("flag", "tjxx");
		TransEngine.getInstance().execute("TranGRKH01",mainContext);
		return true;
	}

    private boolean in_step2(String type, MainContext mainContext) {
        // 获取打印模板
        String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "GRKH01");
        mainContext.getDataPool().put("pdfKey", poolkey);
        return true;
    }

}
