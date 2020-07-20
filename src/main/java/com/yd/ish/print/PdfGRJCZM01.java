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
import com.yd.ish.util.MoneyUtil;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.excel.ExcelEngine;
import com.yd.svrplatform.pdf.PDFEngine;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.ReadProperty;

/**
 * 
 * 名称：PdfGRJCZM01.java
 * <p>
 * 功能：打印 <br>
 * 
 * @author 许永峰
 * @version 0.1 2018年7月30日 许永峰 创建
 */
@Service
public class PdfGRJCZM01 implements IYDVoucher {

	@Autowired
	ParamConfigImp paramConfigImp;
	@Autowired
	FlowAttachmentImp flowAttachmentImp;
	private static final Logger logger = LoggerFactory.getLogger(PdfGRJCZM01.class);
	/** 启用凭证系统标记 **/
	private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");
	/** 接口接受批量上下传文件本地临时路径 **/
	private static final String file_swap_path = ReadProperty.getString("file_swap_path");
	@Autowired
	DP077Service dp077service;

	@Override
	public File generateVoucher(DataPool pool, int cs) {
		logger.debug("[+]个人住房公积金缴存证明信息打印开始");
		File excelfile;
		// 不启用凭证系统
		if (!"1".equals(pzsystemFlag)) {
			// 初次打印时将凭证上数据都放到总线中，方便之后补打时从总线中获取初次打印时的业务数据
			if(cs == 1){
				// 定义日期格式yyyy-mm-dd
				SimpleDateFormat sdf = new SimpleDateFormat(IshConstants.GG_DATE_FORMAT);
				// 交易日期
				String TranDate = sdf.format(new Date());
				// 姓名
				String xingming = IshExpression.getRealUserExtInfo("xingming");
				// 证件类型
				String zjlx = IshExpression.getUserExtInfo("zjlx");
				// 证件号码
				String zjhm = IshExpression.getRealUserExtInfo("zjhm");
				// 用户账号
				String yhdjh = IshExpression.getRealUserExtInfo("yhdjh");
				// 单位名称
				String instname = IshExpression.getRealUserExtInfo("instname");
				// 单位账号
				String instcode = IshExpression.getRealUserExtInfo("instcode");
				pool.put("TranDate",TranDate);
				pool.put("xingming",xingming);
				pool.put("zjlx",zjlx);
				pool.put("zjhm",zjhm);
				pool.put("yhdjh",yhdjh);
				pool.put("instname",instname);
				pool.put("instcode",instcode);
			}
			ExcelEngine excelEngine = new ExcelEngine("DP_GRJCZM_01", pool.getString("_OPERID"));
			// 输出单笔打印数据
			// 姓名
			excelEngine.setData("accname", pool.getString("xingming"));
			// 缴至年月
			excelEngine.setData("jzny", pool.getString("jzny"));
			// 证件类型

			excelEngine.setData("certitype",
					IshExpression.getMulData("bsp.pb.certitype", pool.getString("zjlx")));
			// 证件号
			excelEngine.setData("certinum", pool.getString("zjhm"));
			// 个人账户状态
			excelEngine.setData("indiaccstate", IshExpression.getMulData("bsp.dp.grzhzt", pool.getString("grzhzt")));
			// 个人账号
			excelEngine.setData("accnum", pool.getString("yhdjh"));
			// 开户日期
			excelEngine.setData("opnaccdate", pool.getString("khrq"));
			// 余额
			excelEngine.setData("bal", MoneyUtil.addComma(pool.getString("grzhye")));
			// 单位名称
			excelEngine.setData("unitaccname", pool.getString("instname"));
			// 单位账号
			excelEngine.setData("unitaccnum", pool.getString("instcode"));
			// 缴存基数
			excelEngine.setData("basenum", MoneyUtil.addComma(pool.getString("grjcjs")));
			// 单位缴存比例
			excelEngine.setData("uprop", pool.getString("dwjcbl"));
			// 个人缴存比例
			excelEngine.setData("iprop", pool.getString("grjcbl"));
			// 月缴存额
			excelEngine.setData("payamt", MoneyUtil.addComma(pool.getString("yjce")));
			excelEngine.setData("TranDate", pool.getString("TranDate"));
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