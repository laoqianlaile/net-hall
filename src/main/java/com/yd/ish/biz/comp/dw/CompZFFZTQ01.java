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
 * 名称：CompZFFZTQ01
 * <p>功能：获取公租房信息<br>
 * @brief 获取公租房信息
 * @author 柏慧敏
 * @version 0.1 2018年8月14日 柏慧敏创建
 * @note
 */
@Component("CompZFFZTQ01")
public class CompZFFZTQ01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompZFFZTQ01.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/
		
    	/*入口参数赋值结束*/

		logger.info("[+]调用接口获取公租房信息开始");
		// 调用接口获取获取公租房信息
		XmlResObj data = super.sendExternal("BSP_DW_HQGZFXX_01");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}
		Map<String,Object> body = data.getBody();
		String zlfwdz=body.get("zlfwdz")==null?"":body.get("zlfwdz").toString();
		String fwjzmj=body.get("fwjzmj")==null?"":body.get("fwjzmj").toString();
		String zfyzj=body.get("zfyzj")==null?"":body.get("zfyzj").toString();
		
		logger.info("[-]调用接口获取公租房信息结束");
    	/*出口参数赋值开始*/
		setOutParam("zlfwdz",zlfwdz);//租赁房屋地址
		setOutParam("fwjzmj",fwjzmj);//房屋建筑面积
		setOutParam("zfyzj",zfyzj);//支付月租金
    	/*出口参数赋值结束*/

    	return 0;
   }

}
