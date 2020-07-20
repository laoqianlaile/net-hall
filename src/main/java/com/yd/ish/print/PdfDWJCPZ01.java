package com.yd.ish.print;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yd.basic.service.IYDVoucher;
import com.yd.basic.serviceimp.FlowAttachmentImp;
import com.yd.ish.common.util.FileUtil;
import com.yd.ish.util.MoneyUtil;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.excel.ExcelEngine;
import com.yd.svrplatform.pdf.PDFEngine;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.ReadProperty;

/**
 * 
 * 名称：PdfDWJCPZ01.java
 * <p>
 * 功能：单位汇缴书打印 <br>
 * 
 * @author 柏慧敏
 * @version 0.1 2018年9月26日 柏慧敏创建
 */
@Service
public class PdfDWJCPZ01 implements IYDVoucher {

	@Autowired
	ParamConfigImp paramConfigImp;
	@Autowired
	FlowAttachmentImp flowAttachmentImp;
	private static final Logger logger = LoggerFactory.getLogger(PdfDWJCPZ01.class);
	/** 启用凭证系统标记 **/
	private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");
	/** 接口接受批量上下传文件本地临时路径 **/
	private static final String file_swap_path = ReadProperty.getString("file_swap_path");

	@Override
	public File generateVoucher(DataPool pool, int cs) {
		logger.info("[+]单位汇缴书打印开始");
		File excelfile;
		// 不启用凭证系统
		if (!"1".equals(pzsystemFlag)) {
			ExcelEngine excelEngine = new ExcelEngine("DP_DWJCPZ_02", pool.getString("_OPERID"));
			// 输出打印数据
			excelEngine.setData("dwmc", pool.getString("dwmc"));
			excelEngine.setData("dwzh", pool.getString("dwzh"));
			excelEngine.setData("djh", pool.getString("djh"));
			excelEngine.setData("rzrq", pool.getString("rzrq"));
			excelEngine.setData("rzyh", pool.getString("rzyh"));
			excelEngine.setData("jcrs", pool.getString("jcrs"));
			excelEngine.setData("ksny", pool.getString("ksny"));
			excelEngine.setData("jsny", pool.getString("jsny"));
			excelEngine.setData("dwjcje", pool.getString("dwjcje") == null || "".equals(pool.getString("dwjcje")) ? ""
					: MoneyUtil.addComma(pool.getString("dwjcje")));
			excelEngine.setData("grjcje", pool.getString("grjcje") == null || "".equals(pool.getString("grjcje")) ? ""
					: MoneyUtil.addComma(pool.getString("grjcje")));
			excelEngine.setData("rzje", pool.getString("rzje") == null || "".equals(pool.getString("rzje")) ? ""
					: MoneyUtil.addComma(pool.getString("rzje")));
			excelEngine.setData("jclx", paramConfigImp.getVal("bsp.dp.dptype." + pool.getString("jclx")));
			excelEngine.setData("dwjcjedx", pool.getString("dwjcje") == null || "".equals(pool.getString("dwjcje")) ? ""
					: MoneyUtil.numberToChineseRMB(pool.getString("dwjcje")));
			excelEngine.setData("grjcjedx", pool.getString("grjcje") == null || "".equals(pool.getString("grjcje")) ? ""
					: MoneyUtil.numberToChineseRMB(pool.getString("grjcje")));
			excelEngine.setData("rzjedx", pool.getString("rzje") == null || "".equals(pool.getString("rzje")) ? ""
					: MoneyUtil.numberToChineseRMB(pool.getString("rzje")));
			excelEngine.setData("jkfs", paramConfigImp.getVal("bsp.dp.paymode." + pool.getString("jkfs")));
			excelEngine.setData("rzlsh", pool.getString("rzlsh"));
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
		logger.info("[-]单位汇缴书打印结束");
		return excelfile;
	}
}