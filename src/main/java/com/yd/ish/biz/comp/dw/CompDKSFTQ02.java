package com.yd.ish.biz.comp.dw;

import com.yd.basic.expression.IshExpression;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;

 /**
 * 名称：CompDKSFTQ02
 * <p>功能：贷款首付提取提交<br>
  * @brief 贷款首付提取提交
  * @author 柏慧敏
  * @version 0.1 2019年10月14日 柏慧敏创建
 * @note
 */
@Component("CompDKSFTQ02")
public class CompDKSFTQ02 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDKSFTQ02.class);
	 @Autowired
	 ParamConfigImp paramConfigImp;
	@Override
	public int execute() {

		/*入口参数赋值开始*/
		String ygfrgx = getString("ygfrgx");
		String jkhtbh=getString("jkhtbh");//借款合同编号
		String jkhtbhsz=getString("jkhtbhsz");//借款合同编号数组
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
		// 若flag为true，调用接口获取贷款详细信息
		if(flag) {
			// 如果购房人为本人，将登录人的信息赋值给购房人
			if (ygfrgx.equals(paramConfigImp.getValByMask("bsp.dw.type", "1"))) {
				super.setValue("gfrxm", IshExpression.getRealUserExtInfo("xingming"));
				super.setValue("gfrzjhm", IshExpression.getRealUserExtInfo("zjhm"));
			}
			logger.info("[+]调用接口提交贷款首付提取信息开始");
			// 调用接口提交贷款首付提取信息
			XmlResObj data = super.sendExternal("BSP_DW_TJDKSFTQXX_01");
			XmlResHead head = data.getHead();
			if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
				throw new TransSingleException(head.getParticular_info());
			}

			logger.info("[-]调用接口提交贷款首付提取信息结束");
		}else{
			logger.error("提交贷款首付提取失败，获取到的借款合同编号："+ jkhtbh + ",不在可选范围内");
			throw new TransSingleException("提交贷款首付提取失败，请确认后重新提交。");
		}
    	/*出口参数赋值开始*/

    	/*出口参数赋值结束*/

    	return 0;
   }

}
