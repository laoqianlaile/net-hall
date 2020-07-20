package com.yd.ish.flowhelper.dp;

import com.yd.svrplatform.util.ReadProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.basic.util.YDVoucherUtil;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.workflow.FlowHelperI;

 /**
 * 名称：FlowWFGRJCZM01
 * <p>功能：个人住房公积金缴存证明打印<br>
 * @brief 功能简述 个人住房公积金缴存证明打印
 * @author 许永峰
 * @version 版本号	修改人	修改时间		地点	原因 
 * @note	0.1		许永峰	2018年7月30日  长春 创建
 */
@Component
public class FlowWFGRJCZM01 implements FlowHelperI {

	//private static final Logger logger = LoggerFactory.getLogger(FlowWFGRJCZM01.class);
	 /** 启用凭证系统标记 **/
	 private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");
	
	private static final String STEP_STEP1="step1";//个人住房公积金缴存证明打印
	
	@Autowired
	YDVoucherUtil yDVoucherUtil;
	
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
	

	/**
	 * 打印凭证
	 * 
	 * @param type
	 * @param mainContext
	 * @return
	 */
	private boolean in_step1(String type, MainContext mainContext) {
		//不启用凭证系统调用获取账户基本信息交易，启用凭证系统调用获取个人公积金缴存证明凭证交易
		String trancode = !"1".equals(pzsystemFlag)?"TranZHJBXX01":"TranGRJCZM01";
		TransEngine.getInstance().execute(trancode, mainContext);
		// 获取打印模板
		String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "GRJCZM01");
		mainContext.getDataPool().put("pdfKey", poolkey);
		return true;
	}

}
