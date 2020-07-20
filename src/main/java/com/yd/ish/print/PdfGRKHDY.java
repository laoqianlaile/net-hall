package com.yd.ish.print;

import com.yd.basic.service.IYDVoucher;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.util.PdfFtpUtil;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.ReadProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 *
 * 个人开户打印
 *
 */
@Service
public class PdfGRKHDY implements IYDVoucher {

	@Autowired
	ParamConfigImp paramConfigImp;

	private static final Logger logger = LoggerFactory.getLogger(PdfSBCX01.class);
	private static final String file_swap_path = ReadProperty.getString("file_swap_path");
	@Override
	public File generateVoucher(DataPool pool, int cs) {
		//ftp 服务器凭证文件名   去除yd.prooerties 里定义得 ftp_root部分
		String filenameFtp1 = pool.getString("filename");
		String[] filename = filenameFtp1.split("/");
		String path = filename[0];
		String filenameFtp = filename[1];
		//ftp根目录
		String pdfpath = ReadProperty.getString("pdf_ftp_root");;
		logger.info("[+]创建打印文件开始，文件名称："+ pool.getString("filename"));
		File file = new File(file_swap_path+"/"+path+"/"+filenameFtp);   //本地临时文件
		try {
			//Office2PDF pdf = Office2PDF.newInstance();

			//pdf.printPrepare(file);
			//现在凭证系统文件
			PdfFtpUtil fileUtil = new PdfFtpUtil();
			fileUtil.download(filenameFtp,file, String.valueOf(path),pdfpath);
		} catch (Exception e) {
			logger.error("创建文件出现异常请查看日志" + e.getMessage());
			throw new TransSingleException("创建文件出现异常请查看日志");
		}
		logger.info("[-]创建打印文件结束");
		return file;
	}
}