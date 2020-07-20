package com.yd.ish.biz.comp.common.common;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 名称：AbsCompBatchTransaction.java
 * <p>功能：-无贷款则抛出异常 <br>
 *
 * @author 林楠
 * @version 0.1    2018年9月4日	林楠创建
 */
@Component("CompGrxxcx")
public class CompGrxxcx extends BaseComp {

	@Autowired
    ParamConfigImp paramConfigImp;
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Override
	public int execute() {
		//查询个人信息
		String accnum=getString("accnum");//个人账号
		super.getMainContext().getDataPool().put("accnum", accnum);
		 XmlResObj data = super.sendExternal("BSP-DP-GJJZHJBXX-01");
	     XmlResHead head = data.getHead();
	     if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
	         throw new TransSingleException(head.getParticular_code(), head.getParticular_info());
	     } 
	     Map<String, Object> body=data.getBody();
	     String isloanflag=body.get("isloanflag").toString();
	     if(paramConfigImp.getValByMask("bsp.ln.isloanflag", "0").equals(isloanflag)){
	    	 throw new TransSingleException("未查询到贷款信息！");
	     }
		return 0;
	}

}
