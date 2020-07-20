package com.yd.ish.biz.comp.ln;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;

 /**
 * 名称：CompHKZHBG01
 * <p>功能：提交还款账号变更信息<br>
 * @brief 提交还款账号变更信息
 * @author 柏慧敏
 * @version 0.1 2018年6月29日 柏慧敏创建
 * @note
 */
@Component("CompHKZHBG01")
public class CompHKZHBG01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompHKZHBG01.class);

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
		// 若flag为true，调用接口提交变更信息
		if(flag) {
			logger.info("[+]调用接口提交变更信息开始");
			XmlResObj data = super.sendExternal("BSP_LN_HKZHBG_01");
			XmlResHead head = data.getHead();
			if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
				throw new TransSingleException(head.getParticular_info());
			}
			logger.info("[-]调用接口提交变更信息结束");
		}else{
			logger.error("获取到的借款合同编号："+ jkhtbh);
			throw new TransSingleException("提交失败，请确认后重新提交");
		}
    	/*出口参数赋值开始*/

    	/*出口参数赋值结束*/

    	return 0;
   }

}
