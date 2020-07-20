package com.yd.ish.biz.comp.dw;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;

 /**
 * 名称：CompHQBDYH01
 * <p>功能：获取绑定银行信息<br>
 * @brief 获取绑定银行信息
 * @author 柏慧敏
 * @version 0.1 2018年8月10日 柏慧敏创建
 * @note
 */
@Component("CompHQBDYH01")
public class CompHQBDYH01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompHQBDYH01.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/

    	/*入口参数赋值结束*/
		
		logger.info("[+]调用接口获取绑定银行信息开始");
		// 调用接口获取获取绑定银行信息
		XmlResObj data = super.sendExternal("BSP_DW_HQBDYHK_01");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}
		Map<String,Object> body = data.getBody();
		String sfbd=body.get("sfbd")==null?"":body.get("sfbd").toString();
		String yhbh=body.get("yhbh")==null?"":body.get("yhbh").toString();
		String yhmc=body.get("yhmc")==null?"":body.get("yhmc").toString();
		String yhzh=body.get("yhzh")==null?"":body.get("yhzh").toString();
		
		logger.info("[-]调用接口获取绑定银行信息结束");
    	/*出口参数赋值开始*/
		setOutParam("sfbd",sfbd);//是否绑定 0-否；1-是
		setOutParam("yhbh",yhbh);//银行编号
		setOutParam("yhmc",yhmc);//银行名称
		setOutParam("yhzh",yhzh);//银行账号
    	/*出口参数赋值结束*/

    	return 0;
   }

}
