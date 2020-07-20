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
 * 名称：CompTQHK01
 * <p>
 * 功能：获取提前还款后信息<br>
 * 
 * @brief 获取提前还款后信息
 * @author 柏慧敏
 * @version 0.1 2018年7月2日 柏慧敏创建
 * @note
 */
@Component("CompTQHK01")
public class CompTQHK01 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompTQHK01.class);

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
		// 若flag为true，调用接口获取提前还款后信息
		if(flag) {
			// 调用接口获取提前还款后信息
			logger.info("[+]调用接口获取提前还款后信息开始");
			XmlResObj data = super.sendExternal("BSP_LN_TQHK_01");
			XmlResHead head = data.getHead();
			if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
				throw new TransSingleException(head.getParticular_info());
			}
			Map<String, Object> body = data.getBody();
			String shbj = body.get("shbj") == null ? "" : body.get("shbj").toString();
			String shfx = body.get("shfx") == null ? "" : body.get("shfx").toString();
			String shlx = body.get("shlx") == null ? "" : body.get("shlx").toString();

			logger.info("[-]调用接口获取提前还款后信息结束");

			/* 出口参数赋值开始 */
			setOutParam("shbj", shbj);// 实还本金
			setOutParam("shfx", shfx);// 实还罚息
			setOutParam("shlx", shlx);// 实还利息
			/* 出口参数赋值结束 */
		}else{
			logger.error("获取到的借款合同编号："+ jkhtbh);
			throw new TransSingleException("金额确认失败，请确认后重新录入");
		}
		return 0;
	}

}
