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
 * 名称：CompDKQXBG01
 * <p>功能：获取变更后信息<br>
 * @brief 获取变更后信息
 * @author 柏慧敏
 * @version 0.1 2018年6月19日 柏慧敏创建
 * @note
 */
@Component("CompDKQXBG01")
public class CompDKQXBG01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDKQXBG01.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/
		String jkhtbh=getString("jkhtbh");//借款合同编号
		String jkhtbhsz = getString("jkhtbhsz"); // 借款合同编号数组
    	/*入口参数赋值结束*/

		// 传递的参数是否正确
		Boolean flag = false;
		// 若借款合同编号数组不为空
		if(jkhtbhsz != null && !"".equals(jkhtbhsz)){
			String[] jkhtbharray = jkhtbhsz.split(",");
			// 循环数组
			for(int i=0;i<jkhtbharray.length;i++){
				// 判断页面选择的借款合同编号是否在数组中
				if(jkhtbh.equals(jkhtbharray[i])){
					// 若在数组中，标志赋值成true，退出循环
					flag = true;
					break;
				}
			}
		}
		// 若flag为true，调用接口获取贷款期限变更后贷款信息
		if(flag){
			// 调用接口贷款期限变更后贷款信息
			logger.info("[+]调用接口贷款期限变更后贷款信息开始");
			XmlResObj data = super.sendExternal("BSP_LN_DKQXBG_01");
			XmlResHead head = data.getHead();
			if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
				throw new TransSingleException(head.getParticular_info());
			}
			Map<String,Object> body = data.getBody();
			String tzhyhke=body.get("tzhyhke")==null?"":body.get("tzhyhke").toString();
			String tzhdkzqs=body.get("tzhdkzqs")==null?"":body.get("tzhdkzqs").toString();
			String tzhdqrq=body.get("tzhdqrq")==null?"":body.get("tzhdqrq").toString();
			logger.info("[-]调用接口贷款期限变更后贷款信息结束");
			/*出口参数赋值开始*/
			setOutParam("tzhyhke",tzhyhke);//调整后月还款额
			setOutParam("tzhdkzqs",tzhdkzqs);//调整后贷款总期数
			setOutParam("tzhdqrq",tzhdqrq);//调整后到期日期
			/*出口参数赋值结束*/
		}else{
			logger.error("获取到的借款合同编号："+ jkhtbh);
			throw new TransSingleException("查看调整后贷款情况失败，请确认后重新录入");
		}
    	return 0;
   }

}
