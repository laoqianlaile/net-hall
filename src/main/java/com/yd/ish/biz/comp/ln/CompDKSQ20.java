package com.yd.ish.biz.comp.ln;

import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;


/**
 * 名称：CompDKSQ20
 * <p>功能：校验贷款资质<br>
 * @brief 校验贷款资质
 * @author 柏慧敏
 * @version V0.1 柏慧敏 2019-06-24 新建
 * @note
 */
@Component("CompDKSQ20")
public class CompDKSQ20 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDKSQ20.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/
    	/*入口参数赋值结束*/
		// 调用接口校验主借款人的贷款资质
		XmlResObj grxxdata = super.sendExternal("BSP_LN_DKZZJY_01");
		XmlResHead grxxhead = grxxdata.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(grxxhead.getParticular_code())) {
			throw new TransSingleException(grxxhead.getParticular_info());
		}
    	/*出口参数赋值开始*/

    	/*出口参数赋值结束*/

    	return 0;
   }

}
