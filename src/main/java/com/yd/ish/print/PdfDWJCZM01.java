package com.yd.ish.print;

import java.io.File;

import com.yd.basic.expression.IshExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yd.basic.service.IYDVoucher;
import com.yd.basic.serviceimp.FlowAttachmentImp;
import com.yd.ish.common.util.FileUtil;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.ish.util.MoneyUtil;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.excel.ExcelEngine;
import com.yd.svrplatform.pdf.PDFEngine;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.ReadProperty;

/**
 *
 * 名称：PdfDWJCZM01.java
 * <p>
 * 功能：单位住房公积金缴存情况的证明凭证打印 <br>
 * 
 * @author 王赫
 * @version 0.1 2018年8月14日 王赫创建
 */
@Service
public class PdfDWJCZM01 implements IYDVoucher {

	@Autowired
	ParamConfigImp paramconfigImp;
	@Autowired
	FlowAttachmentImp flowAttachmentImp;
	private static final Logger logger = LoggerFactory.getLogger(PdfDWJCZM01.class);
	/** 启用凭证系统标记 **/
	private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");
	/** 接口接受批量上下传文件本地临时路径 **/
	private static final String file_swap_path = ReadProperty.getString("file_swap_path");
	@Autowired
	DP077Service dp077service;

	@Override
	public File generateVoucher(DataPool pool, int cs) {
		logger.debug("[+]单位住房公积金缴存证明信息打印开始");
		File excelfile;
		// 不启用凭证系统
		if (!"1".equals(pzsystemFlag)) {
			// 初次打印时将凭证上数据都放到总线中，方便之后补打时从总线中获取初次打印时的业务数据
			if(cs == 1){
				String dwmc = IshExpression.getRealUserExtInfo("dwmc");
				String dwzh = IshExpression.getRealUserExtInfo("dwdjh");
				pool.put("dwmc",dwmc);
				pool.put("dwzh",dwzh);
			}
			ExcelEngine excelEngine = new ExcelEngine("DP_DWJCZM_01", pool.getString("_OPERID"));
			// 输出单笔打印数据
			// 单位账号
			excelEngine.setData("dwzh", pool.getString("dwzh"));
			// 单位名称
			excelEngine.setData("dwmc", pool.getString("dwmc"));
			String khrq = pool.getString("khrq");
			String YYYY = khrq.substring(0, 4);
			String MM = khrq.substring(4, 6);
			String DD = khrq.substring(6, 8);
			// 开户日期
			excelEngine.setData("khrq", YYYY + "年" + MM + "月" + DD + "日");
			// 单位账户状态
			excelEngine.setData("dwzhzt", pool.getString("dwzhzt"));
			// 账户机构
			excelEngine.setData("zhjg", pool.getString("zhjg"));
			// 单位缴存比例
			excelEngine.setData("dwjcbl", pool.getString("dwjcbl") + "%");
			// 个人缴存比例
			excelEngine.setData("grjcbl", pool.getString("grjcbl") + "%");
			// 缴存起始年月
			excelEngine.setData("jcqsny", pool.getString("jcqsny"));
			// 缴至年月
			excelEngine.setData("jzny", pool.getString("jzny"));
			String zjly = pool.getString("zjly");
			paramconfigImp.getVal("bsp.dp.fundsouflag." + zjly);
			// 资金来源
			excelEngine.setData("zjly", paramconfigImp.getVal("bsp.dp.fundsouflag." + zjly));
			// 账户余额大写
			excelEngine.setData("zhyedx", MoneyUtil.numberToChineseRMB(pool.getString("zhye")));
			// 账户余额
			excelEngine.setData("zhye", MoneyUtil.addComma(pool.getString("zhye")));
			// 封存人数
			excelEngine.setData("fcrs", pool.getString("fcrs"));
			// 正常人数
			excelEngine.setData("zcrs", pool.getString("zcrs"));
			// 生成打印文件
			excelfile = excelEngine.createExcel();
			try{
				// excel转成pdf
				PDFEngine engine = new PDFEngine();
				// excel凭证文件全路径
				String excelPath = excelfile.getAbsolutePath();
				// pdf凭证文件全路径
				String pdfPath = excelPath.substring(0, excelPath.lastIndexOf(".")) + ".pdf";
				// excel转成pdf
				engine.convertExcel2Pdf(excelPath, pdfPath);
				excelfile = new File(pdfPath);
			}catch(Exception e){
				e.printStackTrace();
			}
		} else { // 启用凭证系统
			String pdfFilename = pool.get("filename") == null ? "" : pool.get("filename").toString();
			excelfile = new File(file_swap_path + "/" + pdfFilename); // 本地临时文件
			try{
				FileUtil fileUtil = new FileUtil();
				fileUtil.downloadFile(excelfile, pdfFilename);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		logger.debug("[-]个人住房公积金缴存证明信息打印结束");

		return excelfile;
	}

}