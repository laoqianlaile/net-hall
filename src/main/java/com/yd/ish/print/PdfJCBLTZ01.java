package com.yd.ish.print;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.yd.svrplatform.util.Datelet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yd.basic.expression.IshExpression;
import com.yd.basic.service.IYDVoucher;
import com.yd.basic.serviceimp.FlowAttachmentImp;
import com.yd.ish.common.util.FileUtil;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.excel.ExcelEngine;
import com.yd.svrplatform.pdf.PDFEngine;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.ReadProperty;

/**
 * 
 * 名称：PdfJCBLTZ01.java
 * <p>
 * 功能：打印 <br>
 * 
 * @author 许永峰
 * @version 0.1 2018年7月4日 许永峰 创建
 */
@Service
public class PdfJCBLTZ01 implements IYDVoucher {

	@Autowired
	ParamConfigImp paramConfigImp;
	@Autowired
	FlowAttachmentImp flowAttachmentImp;
	private static final Logger logger = LoggerFactory.getLogger(PdfJCBLTZ01.class);
	/** 启用凭证系统标记 **/
	private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");
	/** 接口接受批量上下传文件本地临时路径 **/
	private static final String file_swap_path = ReadProperty.getString("file_swap_path");
	@Autowired
	DP077Service dp077service;

	@Override
	public File generateVoucher(DataPool pool, int cs) {
		logger.debug("[+]缴存比例调整信息打印开始");
		File excelfile;
		// 不启用凭证系统
		if (!"1".equals(pzsystemFlag)) {
			// 初次打印时将凭证上数据都放到总线中，方便之后补打时从总线中获取初次打印时的业务数据
			if(cs == 1){
				String dwmc = IshExpression.getRealUserExtInfo("dwmc");
				String dwzh = IshExpression.getRealUserExtInfo("dwdjh");
				// 定义日期格式yyyy-mm-dd
				SimpleDateFormat sdf = new SimpleDateFormat(IshConstants.GG_DATE_FORMAT);
				Date date = new Date();
				pool.put("dwmc",dwmc);
				pool.put("dwzh",dwzh);
				pool.put("transdate",sdf.format(date));
			}
			ExcelEngine excelEngine = new ExcelEngine("DP_JCBLTZ_01", pool.getString("_OPERID"));
			// 输出单笔打印数据
			excelEngine.setData("unitaccname", pool.get("dwmc"));
			excelEngine.setData("unitaccnum", pool.get("dwzh"));
			excelEngine.setData("transdate", pool.get("transdate"));
			excelEngine.setData("bfchgunitprop", pool.getString("o_dwjcbl"));
			excelEngine.setData("afchgunitprop", pool.getString("dwjcbl"));
			excelEngine.setData("bfchgindiprop", pool.getString("o_grjcbl"));
			excelEngine.setData("afchgindiprop", pool.getString("grjcbl"));
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
			}catch (Exception e) {
				e.printStackTrace();
				logger.error("创建文件出现异常请查看日志" + e.getMessage());
			}
		} else { // 启用凭证系统
			String pdfFilename = pool.get("filename") == null ? "" : pool.get("filename").toString();
			excelfile = new File(file_swap_path + "/" + pdfFilename); // 本地临时文件
			try {
				FileUtil fileUtil = new FileUtil();
				fileUtil.downloadFile(excelfile, pdfFilename);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("创建文件出现异常请查看日志" + e.getMessage());
			}
		}
		logger.debug("[-]缴存基数调整信息打印结束");

		return excelfile;
	}

}