package com.yd.ish.biz.comp.ln;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;

 /**
 * 名称：CompZGEDNX01
 * <p>功能：获取最高贷款额度年限参数<br>
 * @brief 获取最高贷款额度年限参数
 * @author 柏慧敏
 * @version 0.1	2018年6月28日	柏慧敏创建
 */
@Component("CompZGEDNX01")
public class CompZGEDNX01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompZGEDNX01.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/

    	/*入口参数赋值结束*/
		logger.info("[+]调用接口获取最高贷款额度年限参数开始");
		XmlResObj data = super.sendExternal("BSP_LN_HQDKEDNX_01");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}
		Map<String,Object> body = data.getBody();
		String zgdkedfirst=body.get("zgdkedfirst")==null ? "":body.get("zgdkedfirst").toString();
		String zgdkedsecond=body.get("zgdkedsecond")==null ? "":body.get("zgdkedsecond").toString();
		String zgdknx=body.get("zgdknx")==null ? "":body.get("zgdknx").toString();
		logger.info("[-]调用接口获取最高贷款额度年限参数结束");
		
    	/*出口参数赋值开始*/
		setOutParam("zgdkedfirst",zgdkedfirst);//最高贷款额度(一手房)
		setOutParam("zgdkedsecond",zgdkedsecond);//最高贷款额度(二手房)
		setOutParam("zgdknx",zgdknx);//最高贷款年限
    	/*出口参数赋值结束*/

    	return 0;
   }

}
