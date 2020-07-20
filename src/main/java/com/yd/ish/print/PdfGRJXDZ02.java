package com.yd.ish.print;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.yd.org.util.IshConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.yd.basic.service.IYDVoucher;
import com.yd.ish.common.util.FileUtil;
import com.yd.ish.util.MoneyUtil;
import com.yd.svrplatform.excel.ExcelEngine;
import com.yd.svrplatform.pdf.PDFEngine;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.Datelet;
import com.yd.svrplatform.util.ReadProperty;

/**
 * 
 * 名称：PdfGRJXDZ01.java
 * <p>
 * 功能：个人结息对账单打印 <br>
 * 
 * @author 柏慧敏
 * @version 0.1 2018年12月13日 柏慧敏 创建
 */
@Service
public class PdfGRJXDZ02 implements IYDVoucher {

	private static final Logger logger = LoggerFactory.getLogger(PdfGRJXDZ02.class);
	/** 启用凭证系统标记 **/
	private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");
	/** 接口接受批量上下传文件本地临时路径 **/
	private static final String file_swap_path = ReadProperty.getString("file_swap_path");

	@Override
	public File generateVoucher(DataPool pool, int cs) {
		logger.debug("[+]个人结息对账单打印开始");
		File excelfile;
		// 不启用凭证系统
		if (!"1".equals(pzsystemFlag)) {
			// 初次打印时将凭证上数据都放到总线中，方便之后补打时从总线中获取初次打印时的业务数据
			if(cs == 1) {
				pool.put("rq",Datelet.getCurrentDate());
			}
			ExcelEngine excelEngine = new ExcelEngine("DP_GRJXDZ_02", pool.getString("_OPERID"));
			// 输出单笔打印数据
			excelEngine.setData("rq", pool.get("rq"));
			// 姓名
			excelEngine.setData("xingming", pool.getString("_OPERNAME"));
			// 单位账号
			excelEngine.setData("grzh", pool.getString("_OPERID"));
			// 起息日期
			excelEngine.setData("qxrq", pool.getString("qxrq"));
			// 结息日期
			excelEngine.setData("jxrq", pool.getString("jxrq"));
			// 定期日利率
			excelEngine.setData("dqrll", pool.getString("dqrll"));
			// 活期日利率
			excelEngine.setData("hqrll", pool.getString("hqrll"));
			// 定期利息
			excelEngine.setData("dqlx", MoneyUtil.addComma(pool.getString("dqlx")));
			// 活期利息
			excelEngine.setData("hqlx", MoneyUtil.addComma(pool.getString("hqlx")));
			// 利息合计
			excelEngine.setData("zlx", MoneyUtil.addComma(pool.getString("zlx")));
			// 大写利息合计
			excelEngine.setData("dxzlx", MoneyUtil.numberToChineseRMB(pool.getString("zlx").replaceAll(",", "")));
						
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
			String pdfFilename = pool.get("pzfilename") == null ? "" : pool.get("pzfilename").toString();
			excelfile = new File(file_swap_path + "/" + pdfFilename); // 本地临时文件
			try{
				FileUtil fileUtil = new FileUtil();
				fileUtil.downloadFile(excelfile, pdfFilename);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		logger.debug("[-]个人结息对账单打印结束");

		return excelfile;
	}

}