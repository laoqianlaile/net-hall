package com.yd.ish.biz.comp.dp;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.ReadProperty;

 /**
 * 名称：CompGRJXDZ02
 * <p>功能：个人结息对账单查询<br>
 * @brief 个人结息对账单查询<br>
 * @author 柏慧敏
 * @version 0.1	2018年12月13日	 柏慧敏创建
 * @note
 */
@Component("CompGRJXDZ02")
public class CompGRJXDZ02 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompGRJXDZ02.class);
	/** 启用凭证系统标记 **/
	private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");
	
	@Override
	public int execute() {

		/*入口参数赋值开始*/

		/*入口参数赋值结束*/

		logger.info("[+]调用接口获取个人结息对账单信息开始");
		XmlResObj data = super.sendExternal("BSP_DP_GRJXDZ_02");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}
		Map<String, Object> body = data.getBody();
		String qxrq = returnString(body.get("qxrq"));
		String jxrq = returnString(body.get("jxrq"));
		String hqrll = returnString(body.get("hqrll"));
		String dqrll = returnString(body.get("dqrll"));
		String hqbj = returnString(body.get("hqbj"));
		String dqbj = returnString(body.get("dqbj"));
		String hqlx = returnString(body.get("hqlx"));
		String dqlx = returnString(body.get("dqlx"));
		String zlx = returnString(body.get("zlx"));
		String zbj = returnString(body.get("zbj"));
		String grzhye = returnString(body.get("grzhye"));
		String pzfilename = body.get("filename") == null ? "" : body.get("filename").toString();
    	/*出口参数赋值开始*/
		setOutParam("qxrq",qxrq);//起息日期
		setOutParam("jxrq",jxrq);//结息日期
		setOutParam("hqrll",hqrll);//活期日利率
		setOutParam("dqrll",dqrll);//定期日利率
		setOutParam("dqbj",dqbj);//定期本金
		setOutParam("hqbj",hqbj);//活期本金
		setOutParam("zbj",zbj);//总本金
		setOutParam("dqlx",dqlx);//定期利息
		setOutParam("hqlx'",hqlx);//活期利息
		setOutParam("zlx",zlx);//总利息
		setOutParam("grzhye",grzhye);//个人账户余额
		if ("1".equals(pzsystemFlag)) { // 启用凭证系统时设置出口参数
			setOutParam("filename", pzfilename);// 凭证文件名称
		}
    	/*出口参数赋值结束*/

    	return 0;
   }
	public String returnString(Object str) {
		return str == null ? "" : str.toString();
	}
}
