package com.yd.ish.flowhelper.common.exa;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.ish.common.util.YyywUtil;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.workflow.FlowHelperI;

/**
 * 名称：FlowWFYYSL01.java
 * <p>功能：预约跳转示例流程助手 <br> 
 * @author 张洪超
 * @version 0.1	2017年10月18日	张洪超创建
 */
@Component
public class FlowWFYYSL01 implements FlowHelperI {

	
	/*交易调用示例，trancode为具体交易代码
	TransEngine.getInstance().execute("trancode",mainContext);
	*/
	@Autowired
	YyywUtil yyUtil;
	
	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {

		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {

		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		if (StringUtils.equals("getYyUrl", task)) {
			// 获得预约跳转获取跳转到预约页面地址
			return yyUtil.getYyUrl(mainContext);
		}
		return null;
	}
	

}
