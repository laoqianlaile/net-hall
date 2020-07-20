package com.yd.ish.biz.comp.ln;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 名称：CompDKSS02
 * <p>
 * 功能：获取调整后的到期日期<br>
 * 
 * @brief 获取调整后的到期日期
 * @author 柏慧敏
 * @version 0.1 2019年4月15日 柏慧敏创建
 * @note
 */
@Component("CompDKSS03")
public class CompDKSS03 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompDKSS03.class);

	@Override
	public int execute() {

		/* 入口参数赋值开始 */
		String jkhtbh = getString("jkhtbh"); // 借款合同编号
		String jkhtbhsz = getString("jkhtbhsz"); // 借款合同编号数组
		/* 入口参数赋值结束 */
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
		// 若flag为true，调用接口获取试算结果
		if(flag) {
			// 调用接口获取贷款详细信息
			logger.info("[+]调用接口获取调整后的到期日期开始");
			XmlResObj data = super.sendExternal("BSP_LN_DKQXBG_01");
			XmlResHead head = data.getHead();
			if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
				throw new TransSingleException(head.getParticular_info());
			}
			Map<String, Object> body = data.getBody();
			String tzhdqrq = body.get("tzhdqrq") == null ? "" : body.get("tzhdqrq").toString();

			logger.info("[-]调用接口获取调整后的到期日期结束");

			/* 出口参数赋值开始 */
			setOutParam("tzhdqrq", tzhdqrq);// 调整后到期日期
			/* 出口参数赋值结束 */
		}else{
			logger.error("获取到的借款合同编号："+ jkhtbh);
			throw new TransSingleException("计算失败，请确认信息后重新计算。");
		}
		return 0;
	}

}
