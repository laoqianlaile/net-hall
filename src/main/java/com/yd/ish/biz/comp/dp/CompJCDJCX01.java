package com.yd.ish.biz.comp.dp;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;

 /**
 * 名称：CompJCDJCX01
 * <p>功能：获取缴存登记信息<br>
 * @brief 获取缴存登记信息
 * @author 柏慧敏
 * @version 0.1 2018年9月28日  柏慧敏创建
 * @note
 */
@Component("CompJCDJCX01")
public class CompJCDJCX01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompJCDJCX01.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/
		
    	/*入口参数赋值结束*/
		
		logger.info("[+]调用接口查询缴存登记信息开始");
		// 调用接口查询缴存登记信息
		XmlResObj data = super.sendExternal("BSP_DP_JCDJXX_01");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}
		// 调用综服接口从body中获取文件名
		Map<String, Object> body = data.getBody();
		String djh=body.get("djh") == null ? "" : body.get("djh").toString();
		String djrq=body.get("djrq") == null ? "" : body.get("djrq").toString();
		String jclx=body.get("jclx") == null ? "" : body.get("jclx").toString();
		String fkyh=body.get("fkyh") == null ? "" : body.get("fkyh").toString();
		String jkfs=body.get("jkfs") == null ? "" : body.get("jkfs").toString();
		String jkje=body.get("jkje") == null ? "" : body.get("jkje").toString();
		logger.info("[-]调用接口查询缴存登记信息结束");
    	/*出口参数赋值开始*/
		setOutParam("djh",djh);//登记号
		setOutParam("djrq",djrq);//登记日期
		setOutParam("jclx",jclx);//缴存类型
		setOutParam("fkyh",fkyh);//付款银行
		setOutParam("jkfs",jkfs);//缴款方式
		setOutParam("jkje",jkje);//缴款金额
    	/*出口参数赋值结束*/

    	return 0;
   }

}
