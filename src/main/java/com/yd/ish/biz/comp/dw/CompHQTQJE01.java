package com.yd.ish.biz.comp.dw;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.GetDownFileMap;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.DataPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 名称：CompHQTQJE01.java
 * <p>
 * 功能：获取提取金额 <br>
 * 
 * @author 王赫
 * @version 0.1 2018年8月07日 王赫创建
 */
@Component("CompHQTQJE01")
public class CompHQTQJE01 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompHQTQJE01.class);

	@Autowired
	GetDownFileMap getDownFileMap;

	@Override
	public int execute() {

		/* 入口参数赋值开始 */
		String jkhtbh = getString("jkhtbh");//借款合同编号
		String jkhtbhsz=getString("jkhtbhsz");//借款合同编号数组
		/* 入口参数赋值结束 */
		// 传递的参数是否正确
		Boolean flag = false;
		// 若借款合同编号数组不为空
		if(jkhtbh != null && !"".equals(jkhtbh)){
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
		}else{
			// 计算商贷还款情况
			flag = true;
		}
		if(flag){
			XmlResObj data = super.sendExternal("BSP_DW_HQTQJE_01", false);
			XmlResHead head = data.getHead();
			if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
				logger.error("获取提取金额接口：BSP_DW_HQTQJE_01异常：" + head.getParticular_info());
				throw new TransSingleException(head.getParticular_code(), head.getParticular_info());
			}
			Map<String, Object> body = data.getBody();
			String tqje = body.get("tqje") == null ? "" : body.get("tqje").toString();
			/* 出口参数赋值开始 */
			setOutParam("tqje", tqje);// 提取金额


			/* 出口参数赋值结束 */
		}else{
			logger.error("获取到的借款合同编号："+ jkhtbh+ ",不在借款合同编号数组中");
			throw new TransSingleException("计算提取金额失败，请确认后重新输入");
		}
		return 0;
	}

}
