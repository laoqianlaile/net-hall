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
import com.yd.ish.util.MoneyUtil;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.excel.ExcelEngine;
import com.yd.svrplatform.pdf.PDFEngine;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.Datelet;
import com.yd.svrplatform.util.ReadProperty;

/**
 * 名称：PdfDWHJPZ01.java
 * <p>
 * 功能：单位汇缴受理回单打印 <br>
 *
 * @author 王赫
 * @version 0.1 2018年9月27日 王赫创建
 */
@Service
public class PdfDWHJPZ01 implements IYDVoucher {

	@Autowired
	ParamConfigImp paramConfigImp;

	@Autowired
	FlowAttachmentImp flowAttachmentImp;

	private static final Logger logger = LoggerFactory.getLogger(PdfDWHJPZ01.class);
	/** 启用凭证系统标记 **/
	private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");
	/** 接口接受批量上下传文件本地临时路径 **/
	private static final String file_swap_path = ReadProperty.getString("file_swap_path");

	@Override
	public File generateVoucher(DataPool pool, int cs) {
		logger.info("[+]单位汇缴受理回单打印开始");
		File excelfile;
		// 不启用凭证系统
		if (!"1".equals(pzsystemFlag)) {
			// 初次打印时将凭证上数据都放到总线中，方便之后补打时从总线中获取初次打印时的业务数据
			if(cs == 1){
				String dwmc = IshExpression.getRealUserExtInfo("dwmc");
				String dwzh = IshExpression.getRealUserExtInfo("dwdjh");
				pool.put("dwmc",dwmc);
				pool.put("dwzh",dwzh);
				pool.put("rq",Datelet.getCurrentDate());
			}
			ExcelEngine excelEngine = new ExcelEngine("DP_DWJCPZ_01", pool.getString("_OPERID"));
			// 输出打印数据
			// 日期
			excelEngine.setData("rq", pool.get("rq"));
			// 登记号
			excelEngine.setData("djh", pool.getString("djh"));
			// 单位名称
			excelEngine.setData("dwmc", pool.get("dwmc"));
			// 单位账号
			excelEngine.setData("dwzh", pool.get("dwzh"));
			// 开始年月
			excelEngine.setData("ksny", pool.getString("ksny"));
			// 终止年月
			excelEngine.setData("zzny", pool.getString("zzny"));
			// 收款账号
			excelEngine.setData("skzh", pool.getString("skyhzh"));
			// 收款银行
			excelEngine.setData("skyh", pool.getString("skyhmc"));
			// 应缴金额
			excelEngine.setData("yjje", MoneyUtil.addComma(pool.getString("yjje")));
			// 大写应缴金额
			excelEngine.setData("dxyjje", MoneyUtil.numberToChineseRMB(pool.getString("yjje").replaceAll(",", "")));
			// 汇缴人数
			excelEngine.setData("jcrs", pool.getString("hjrs"));
			// 实缴金额
			excelEngine.setData("sjje", MoneyUtil.addComma(pool.getString("sjje")));
			// 暂存户转出金额
			excelEngine.setData("zchzcje", MoneyUtil.addComma(pool.getString("zchzcje")));
			// 经办机构
			excelEngine.setData("jbjg", pool.getString("jbjg"));
			// 操作员
			excelEngine.setData("opername", pool.getString("_OPERNAME"));
			excelEngine.setData("hj", "√");
			excelEngine.setData("bj", "");

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
		logger.info("[-]单位汇缴受理回单打印结束");
		return excelfile;
	}
}