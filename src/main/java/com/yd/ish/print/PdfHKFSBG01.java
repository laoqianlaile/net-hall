package com.yd.ish.print;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yd.basic.service.IYDVoucher;
import com.yd.basic.serviceimp.FlowAttachmentImp;
import com.yd.ish.common.util.FileUtil;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.ReadProperty;
import com.yd.svrplatform.word.WordEngine;
import com.yd.svrplatform.word.WordEngine2;

/**
 * 
 * 名称：PdfHKFSBG01.java
 * <p>
 * 功能：打印还款方式变更协议 <br>
 * 
 * @author
 * @version 0.1 2018年7月5日 柏慧敏创建
 */
@Service
public class PdfHKFSBG01 implements IYDVoucher {

	@Autowired
	ParamConfigImp paramConfigImp;
	@Autowired
	FlowAttachmentImp flowAttachmentImp;
	private static final Logger logger = LoggerFactory.getLogger(PdfHKFSBG01.class);
	/** 启用凭证系统标记 **/
	private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");
	/** 接口接受批量上下传文件本地临时路径 **/
	private static final String file_swap_path = ReadProperty.getString("file_swap_path");

	@Override
	public File generateVoucher(DataPool pool, int cs) {
		logger.info("[+]还款方式变更协议打印");
		File wordfile;
		// 不启用凭证系统
		if (!"1".equals(pzsystemFlag)) {
			WordEngine wordEngine = new WordEngine("LN_HKFSBG_01", pool.getString("_OPERID"));
			// // 输出打印数据
			wordEngine.setData("loanaccnum", pool.getString("jkhtbh"));
			wordEngine.setData("agencybank", pool.getString("agencybank"));
			wordEngine.setData("accname1", pool.getString("accname1"));
			wordEngine.setData("loaneecertinum", pool.getString("loaneecertinum"));
			wordEngine.setData("oldretloanmode",
					paramConfigImp.getVal("bsp.ln.repaymode." + pool.getString("oldretloanmode")));
			wordEngine.setData("retloanmode", paramConfigImp.getVal("bsp.ln.repaymode." + pool.getString("xdkhkfs")));

			wordfile = wordEngine.generateWord();
			try {
				// word凭证文件全路径
				String wordPath = wordfile.getAbsolutePath();
				// pdf凭证文件全路径
				String pdfPath = wordPath.substring(0, wordPath.lastIndexOf(".")) + ".pdf";
				// word转成pdf
				WordEngine2 doc = new WordEngine2();
				wordfile = doc.doc2PDF(wordfile, pdfPath);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("创建文件出现异常请查看日志" + e.getMessage());
			}
		} else { // 启用凭证系统
			String pdfFilename = pool.get("filename") == null ? "" : pool.get("filename").toString();
			wordfile = new File(file_swap_path + "/" + pdfFilename); // 本地临时文件
			try {
				FileUtil fileUtil = new FileUtil();
				fileUtil.downloadFile(wordfile, pdfFilename);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("创建文件出现异常请查看日志" + e.getMessage());
			}
		}
		logger.info("[-]还款方式变更协议打印");

		return wordfile;
	}
}