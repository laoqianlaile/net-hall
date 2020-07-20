package com.yd.ish.print;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yd.basic.expression.IshExpression;
import com.yd.basic.service.IYDVoucher;
import com.yd.ish.common.util.FileUtil;
import com.yd.ish.util.MoneyUtil;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.excel.ExcelEngine;
import com.yd.svrplatform.pdf.PDFEngine;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.ReadProperty;
import com.yd.workflow.util.Constants;

/**
 * 
 * 名称：PdfTQHK01.java
 * <p>
 * 功能：提前还款打印 <br>
 * 
 * @author
 * @version 0.1 2018年7月6日 柏慧敏创建
 */
@Service
public class PdfTQHK01 implements IYDVoucher {

	@Autowired
	ParamConfigImp paramConfigImp;

	private static final Logger logger = LoggerFactory.getLogger(PdfTQHK01.class);
	/** 启用凭证系统标记 **/
	private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");
	/** 接口接受批量上下传文件本地临时路径 **/
	private static final String file_swap_path = ReadProperty.getString("file_swap_path");

	@Override
	public File generateVoucher(DataPool pool, int cs) {
		logger.info("[+]打印业务开始");
		File excelfile;
		// 不启用凭证系统
		if (!"1".equals(pzsystemFlag)) {
			String instance = pool.getString(Constants._WF_IS);
			// 初次打印时将凭证上数据都放到总线中，方便之后补打时从总线中获取初次打印时的业务数据
			if(cs == 1){
				String xingming = IshExpression.getRealUserExtInfo("xingming");
				String dwzh = IshExpression.getRealUserExtInfo("dwdjh");
				// 定义日期格式yyyy-mm-dd
				SimpleDateFormat sdf = new SimpleDateFormat(IshConstants.GG_DATE_FORMAT);
				Date date = new Date();
				pool.put("xingming",xingming);
				pool.put("transdate",sdf.format(date));
			}
			ExcelEngine excelEngine = new ExcelEngine("LN_TQHK_01", pool.getString("_OPERID"));
			// 输出单笔打印数据
			excelEngine.setData("loancontrnum", pool.getString("jkhtbh"));
			excelEngine.setData("loaneename", pool.getString("loaneename"));
			excelEngine.setData("acoper", pool.get("xingming"));
			excelEngine.setData("hostsernum", instance);
			excelEngine.setData("transdate", pool.get("transdate"));
			excelEngine.setData("agentbankcode", pool.getString("agentbankcode"));
			excelEngine.setData("loaneecertinum", pool.getString("loaneecertinum"));
			excelEngine.setData("repaytype", paramConfigImp.getVal("bsp.ln.repaytype." + pool.getString("hklx")));
			excelEngine.setData("settlemode", paramConfigImp.getVal("bsp.ln.jsfs." + pool.getString("jsfs")));
			excelEngine.setData("repaytolamt", MoneyUtil.addComma(pool.getString("tqhkzje")));
			excelEngine.setData("repaytolamtupper", MoneyUtil.numberToChineseRMB(pool.getString("tqhkzje")));
			excelEngine.setData("repayprin", MoneyUtil.addComma(pool.getString("repayprin")));
			excelEngine.setData("repayint", MoneyUtil.addComma(pool.getString("repayint")));
			excelEngine.setData("repaypun", MoneyUtil.addComma(pool.getString("repaypun")));

			// 生成打印文件
			excelfile = excelEngine.createExcel();
			try {
				// excel转成pdf
				PDFEngine engine = new PDFEngine();
				// excel凭证文件全路径
				String excelPath = excelfile.getAbsolutePath();
				// pdf凭证文件全路径
				String pdfPath = excelPath.substring(0, excelPath.lastIndexOf(".")) + ".pdf";
				// excel转成pdf
				engine.convertExcel2Pdf(excelPath, pdfPath);
				excelfile = new File(pdfPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else { // 启用凭证系统
			String pdfFilename = pool.get("filename") == null ? "" : pool.get("filename").toString();
			excelfile = new File(file_swap_path + "/" + pdfFilename); // 本地临时文件
			try {
				FileUtil fileUtil = new FileUtil();
				fileUtil.downloadFile(excelfile, pdfFilename);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		logger.info("[-]提前还款打印结束");

		return excelfile;
	}

}