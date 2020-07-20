package com.yd.ish.print;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yd.basic.service.IYDVoucher;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.FileUtil;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.ReadProperty;

/**
 * 
 * 名称：PdfSBCX01.java
 * <p>
 * 功能：申报查询凭证打印协议 <br>
 * 
 * @author
 * @version 0.1 2018年9月20日 柏慧敏创建
 */
@Service
public class PdfSBCX01 implements IYDVoucher {

	@Autowired
	ParamConfigImp paramConfigImp;

	private static final Logger logger = LoggerFactory.getLogger(PdfSBCX01.class);
	private static final String file_swap_path = ReadProperty.getString("file_swap_path");
	@Override
	public File generateVoucher(DataPool pool, int cs) {
		//ftp 服务器凭证文件名   去除yd.prooerties 里定义得 ftp_root部分
		String filenameFtp = pool.getString("filename"); 
		logger.info("[+]创建打印文件开始，文件名称："+ pool.getString("filename"));
		File file = new File(file_swap_path+filenameFtp);   //本地临时文件
		try {
			FileUtil fileUtil = new FileUtil();
			fileUtil.downloadFile(file, filenameFtp);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("创建文件出现异常请查看日志" + e.getMessage());
			throw new TransSingleException("创建文件出现异常请查看日志");
		}
		logger.info("[-]创建打印文件结束");
		return file;
	}
}