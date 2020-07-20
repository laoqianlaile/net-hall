package com.yd.ish.biz.comp.common.dlgl;

import com.yd.biz.exception.TransSingleException;
import com.yd.org.util.CommonErrorCode;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;

import java.util.Map;

/**
 * 名称：CompDLYZ503
 * <p>功能：获取预留手机号<br>
 * @brief 获取预留手机号
 * @author 柏慧敏
 * @version V0.1 柏慧敏 2019-08-05 新增
 * @note
 */
@Component("CompDLYZ503")
public class CompDLYZ503 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDLYZ503.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/
		String dlyhm=getString("dlyhm");//用户名
		String dlmm=getString("dlmm");//登录密码
    	/*入口参数赋值结束*/
		if(StringUtils.isBlank(dlyhm)){
			throw new TransSingleException(CommonErrorCode.ERROR_NOT_NULL,"用户名");
		}
		if(StringUtils.isBlank(dlmm)){
			throw new TransSingleException(CommonErrorCode.ERROR_NOT_NULL,"密码");
		}
		logger.info("[+]调用接口获取预留手机号开始");
		XmlResObj data =  super.sendExternal("BSP-OAPI-600",false);
		XmlResHead head=data.getHead();
		String ylsjh = "";
		if (XmlResHead.TR_SUCCESS.equals(head.getParticular_code())){
			Map<String,Object> body=data.getBody();
			ylsjh = body.get("ylsjh") == null ? "" : body.get("ylsjh").toString();
		}
		logger.info("[-]调用接口获取预留手机号结束");
		/*出口参数赋值开始*/
		setOutParam("ylsjh",ylsjh);//预留手机号
		setOutParam("returnCode", head.getParticular_code());
		setOutParam("msg", head.getParticular_info());
    	/*出口参数赋值结束*/

    	return 0;
   }

}
