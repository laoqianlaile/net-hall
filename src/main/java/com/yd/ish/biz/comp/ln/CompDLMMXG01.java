package com.yd.ish.biz.comp.ln;

import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;

 /**
 * 名称：CompDLMMXG01
 * <p>功能：登录密码修改<br>
 * @brief 登录密码修改
  * @author 柏慧敏
  * @version V0.1 柏慧敏 2019-06-20 长春 新建
 * @note
 */
@Component("CompDLMMXG01")
public class CompDLMMXG01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDLMMXG01.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/
    	/*入口参数赋值结束*/
		logger.info("[+]调用接口提交修改密码信息开始");
		XmlResObj data = super.sendExternal("BSP_LN_DLMMXG_01");
		XmlResHead head = data.getHead();
		if( !XmlResHead.TR_SUCCESS.equals(head.getParticular_code()) ){
			throw new TransSingleException(head.getParticular_info());
		}
		logger.debug("[-]调用接口提交修改密码信息结束");

    	/*出口参数赋值开始*/

    	/*出口参数赋值结束*/

    	return 0;
   }

}
