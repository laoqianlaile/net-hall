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
import com.yd.svrplatform.util.Datelet;
import com.yd.svrplatform.util.ReadProperty;

/**
 * 名称：PdfDWHJPZ02.java
 * <p>
 * 功能：单位汇缴入账回单打印 <br>
 *
 * @author 王赫
 * @version 0.1 2018年9月27日 王赫创建
 */
@Service
public class PdfDWHJPZ02 implements IYDVoucher {

	@Autowired
	ParamConfigImp paramConfigImp;

	@Autowired
	FlowAttachmentImp flowAttachmentImp;

	private static final Logger logger = LoggerFactory.getLogger(PdfDWHJPZ02.class);
	/** 启用凭证系统标记 **/
	private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");
	/** 接口接受批量上下传文件本地临时路径 **/
	private static final String file_swap_path = ReadProperty.getString("file_swap_path");

	@Override
	public File generateVoucher(DataPool pool, int cs) {
		logger.info("[+]单位汇缴入账回单打印开始");
		File excelfile;
		// 不启用凭证系统
		if (!"1".equals(pzsystemFlag)) {
			// 初次打印时将凭证上数据都放到总线中，方便之后补打时从总线中获取初次打印时的业务数据
			if(cs == 1){
				pool.put("rzrq",Datelet.getCurrentDate());
			}
			ExcelEngine excelEngine = new ExcelEngine("DP_DWJCPZ_02", pool.getString("_OPERID"));
			// 输出打印数据
			// 登记号
			excelEngine.setData("djh", pool.getString("djh"));
			// 入账日期
			excelEngine.setData("rzrq", pool.getString("rzrq"));
			// 单位账号
			excelEngine.setData("dwzh", pool.getString("dwzh"));
			// 入账银行
			excelEngine.setData("rzyh", pool.getString("skyhmc"));
			// 单位名称
			excelEngine.setData("dwmc", pool.getString("dwmc"));
			// 开始年月
			excelEngine.setData("ksny", pool.getString("ksny"));
			// 结束年月
			excelEngine.setData("jsny", pool.getString("zzny"));
			// 缴存人数
			excelEngine.setData("jcrs", pool.getString("hjrs"));
			// 单位汇缴金额
			excelEngine.setData("dwjcje", MoneyUtil.addComma(pool.getString("dwjcje")));
			// 大写单位汇交金额
			excelEngine.setData("dwjcjedx", MoneyUtil.numberToChineseRMB(pool.getString("dwjcje").replaceAll(",", "")));
			// 个人汇缴金额
			excelEngine.setData("grjcje", MoneyUtil.addComma(pool.getString("grjcje")));
			// 大写个人汇交金额
			excelEngine.setData("grjcjedx", MoneyUtil.numberToChineseRMB(pool.getString("grjcje").replaceAll(",", "")));
			// 入账金额
			excelEngine.setData("rzje", MoneyUtil.addComma(pool.getString("yjje").replaceAll(",", "")));
			// 大写入账金额
			excelEngine.setData("rzjedx", MoneyUtil.numberToChineseRMB(pool.getString("yjje").replaceAll(",", "")));
			String jclx = pool.getString("jclx");
			String jkfs = pool.getString("jkfs");
			String jclxval = paramConfigImp.getVal("bsp.dp.dptype." + jclx);
			String jkfsval = paramConfigImp.getVal("bsp.dp.jkfs." + jkfs);
			// 缴存类型
			excelEngine.setData("jclx", jclxval);
			// 缴存方式
			excelEngine.setData("jkfs", jkfsval);
			// 入账流水号
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
		logger.info("[-]单位汇缴入账回单打印结束");
		return excelfile;
	}
}