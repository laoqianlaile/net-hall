package com.yd.ish.flowhelper.dp;

import org.springframework.stereotype.Component;

import com.yd.biz.engine.TransEngine;
import com.yd.org.util.EncryptionUtil;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.workflow.FlowHelperI;

 /**
 * 名称：FlowWFZFMMBG01
 * <p>功能： 支付密码变更流程助手<br>
 * @brief 支付密码变更
 * @author 
 * @version 版本号	修改人	修改时间			地点	原因 
 * @note	0.1		许永峰	2018年06月27日	长春	创建
 */
@Component
public class FlowWFZFMMBG01 implements FlowHelperI {

	
	private static final String STEP_STEP1="step1";//支付密码变更
	
	/*交易调用示例，trancode为具体交易代码
	TransEngine.getInstance().execute("trancode",mainContext);
	*/
	
	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {

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
		
		return null;
	}
	
	private boolean out_step1(MainContext mainContext){
		DataPool datapool = mainContext.getDataPool();
		datapool.put("yzfmm",  EncryptionUtil.MD5Encode(datapool.getString("yzfmm")));
		datapool.put("xzfmm",  EncryptionUtil.MD5Encode(datapool.getString("xzfmm")));
		datapool.put("qrxzfmm",  EncryptionUtil.MD5Encode(datapool.getString("qrxzfmm")));
		
		TransEngine.getInstance().execute("TranZFMMZH01",mainContext);
		return true;
	}

}
