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
 * 名称：CompDWJCMX03
 * <p>
 * 功能：查询汇缴书信息<br>
 * 
 * @brief 查询汇缴书信息
 * @author 柏慧敏
 * @version 0.1 2018年9月27日 柏慧敏创建
 * @note
 */
@Component("CompDWJCMX03")
public class CompDWJCMX03 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompDWJCMX03.class);
	/** 读核心返回文件编码格式 */
	private static final String encoding = ReadProperty.getString("readfile_encoding_bsp");
	/** 启用凭证系统标记 **/
	private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");

	@Override
	public int execute() {

		/* 入口参数赋值开始 */

		/* 入口参数赋值结束 */
		logger.info("[+]调用接口查询汇缴书信息BSP_DP_DWJCXX_04开始");
		// 调用接口单位汇补缴批量信息
		XmlResObj data = super.sendExternal("BSP_DP_DWJCXX_04");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}
		// 调用综服接口从body中获取文件名
		Map<String, Object> body = data.getBody();
		String rzrq = body.get("rzrq") == null ? "" : body.get("rzrq").toString();
		String dwmc = body.get("dwmc") == null ? "" : body.get("dwmc").toString();
		String dwzh = body.get("dwzh") == null ? "" : body.get("dwzh").toString();
		String ksny = body.get("ksny") == null ? "" : body.get("ksny").toString();
		String jsny = body.get("jsny") == null ? "" : body.get("jsny").toString();
		String dwjcje = body.get("dwjcje") == null ? "" : body.get("dwjcje").toString();
		String grjcje = body.get("grjcje") == null ? "" : body.get("grjcje").toString();
		String rzje = body.get("rzje") == null ? "" : body.get("rzje").toString();
		String jclx = body.get("jclx") == null ? "" : body.get("jclx").toString();
		String rzyh = body.get("rzyh") == null ? "" : body.get("rzyh").toString();
		String jcrs = body.get("jcrs") == null ? "" : body.get("jcrs").toString();
		String jkfs = body.get("jkfs") == null ? "" : body.get("jkfs").toString();
		String rzlsh = body.get("rzlsh") == null ? "" : body.get("rzlsh").toString();
		String pzfilename = body.get("filename") == null ? "" : body.get("filename").toString();
		logger.info("[-]调用接口查询汇缴书信息BSP_DP_DWJCXX_04结束");
		/* 出口参数赋值开始 */
		setOutParam("rzrq", rzrq);// 入账日期
		setOutParam("dwzh", dwzh);// 单位账号
		setOutParam("dwmc", dwmc);// 单位名称
		setOutParam("ksny", ksny);// 开始年月
		setOutParam("jsny", jsny);// 结束年月
		setOutParam("dwjcje", dwjcje);// 单位汇缴金额
		setOutParam("grjcje", grjcje);// 个人汇缴金额
		setOutParam("rzje", rzje);// 入账金额
		setOutParam("jclx", jclx);// 缴存类型
		setOutParam("rzyh", rzyh);// 入账银行
		setOutParam("jcrs", jcrs);// 缴存人数
		setOutParam("jkfs", jkfs);// 缴款方式
		setOutParam("rzlsh", rzlsh);// 入账流水号
		if ("1".equals(pzsystemFlag)) { // 启用凭证系统时设置出口参数
			setOutParam("filename", pzfilename);// 凭证文件名称
		}
		/* 出口参数赋值结束 */

		return 0;
	}

}
