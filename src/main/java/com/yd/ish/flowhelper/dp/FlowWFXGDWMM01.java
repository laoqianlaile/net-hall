package com.yd.ish.flowhelper.dp;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.yd.workflow.FlowHelperI;
import com.yd.biz.engine.TransEngine;
import com.yd.org.util.EncryptionUtil;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;

 /**
 * 名称：FlowWFXGDWMM01
 * <p>功能： 修改单位密码流程助手<br>
 * @author 柏慧敏
 * @version 0.1	2018年5月24日	柏慧敏创建
 * @note
 */
@Component
public class FlowWFXGDWMM01 implements FlowHelperI {
	
	private static final String STEP_STEP1="step1";//单位密码修改
	
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
		//提交密码修改信息
		DataPool dataPool=MainContext.currentMainContext().getDataPool();
		String ydlmm=dataPool.getString("ydlmm");
		String xdlmm=dataPool.getString("xdlmm");
		String qrxdlmm=dataPool.getString("qrxdlmm");
		//对密码进行加密
		if(StringUtils.isNotBlank(ydlmm)){
			dataPool.put("ydlmm", EncryptionUtil.MD5Encode(ydlmm));
		}
		if(StringUtils.isNotBlank(xdlmm)){
			dataPool.put("xdlmm", EncryptionUtil.MD5Encode(xdlmm));
		}
		if(StringUtils.isNotBlank(qrxdlmm)){
			dataPool.put("qrxdlmm", EncryptionUtil.MD5Encode(qrxdlmm));
		}
		TransEngine.getInstance().execute("TranXGDWMM01",mainContext);
		return true;
	}

}
