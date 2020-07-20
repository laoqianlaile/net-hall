package com.yd.ish.biz.comp.dw;

import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;

import java.util.Map;

/**
 * 名称：CompDKSFTQ03
 * <p>功能：获取贷款首付信息<br>
 * @brief 获取贷款首付信息
 * @author 柏慧敏
 * @version 0.1 2019年10月14日 柏慧敏创建
 * @note
 */
@Component("CompDKSFTQ03")
public class CompDKSFTQ03 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDKSFTQ03.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/
		String jkhtbh=getString("jkhtbh");//借款合同编号
		String jkhtbhsz=getString("jkhtbhsz");//借款合同编号数组
    	/*入口参数赋值结束*/
		// 传递的参数是否正确
		Boolean flag = false;
		// 若借款合同编号数组不为空WFWTKHQY01
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
			logger.info("[+]调用接口获取贷款首付信息开始");
			// 调用接口计算提取金额
			XmlResObj data = super.sendExternal("BSP_DW_HQDKSFXX_01");
			XmlResHead head = data.getHead();
			if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
				throw new TransSingleException(head.getParticular_info());
			}
			Map<String, Object> body = data.getBody();
			String fkrq = body.get("fkrq") == null ? "" : body.get("fkrq").toString();
			String fwzj = body.get("fwzj") == null ? "" : body.get("fwzj").toString();
			String fwzl = body.get("fwzl") == null ? "" : body.get("fwzl").toString();
			String gfsfk = body.get("gfsfk") == null ? "" : body.get("gfsfk").toString();
			String sfkytqje = body.get("sfkytqje") == null ? "" : body.get("sfkytqje").toString();
			logger.info("[-]调用接口获取贷款首付信息结束");
			/*出口参数赋值开始*/
			setOutParam("fkrq",fkrq);//放款日期
			setOutParam("fwzj",fwzj);//房屋总价
			setOutParam("fwzl",fwzl);//房屋坐落
			setOutParam("gfsfk",gfsfk);//购房首付款
			setOutParam("sfkytqje",sfkytqje);//首付款已提取额
			/*出口参数赋值结束*/
		}else{
			logger.error("获取贷款信息失败，获取到的借款合同编号："+ jkhtbh + ",不在可选范围内");
			throw new TransSingleException("获取贷款信息失败，请确认后重新输入。");
		}
    	return 0;
   }

}
