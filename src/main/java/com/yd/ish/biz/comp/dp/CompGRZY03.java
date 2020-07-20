package com.yd.ish.biz.comp.dp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.yd.basic.expression.IshExpression;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.BatchExceptionBean;
import com.yd.biz.exception.TransBatchException;
import com.yd.biz.exception.TransOtherException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.FileUtil;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.org.util.CommonErrorCode;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.dataswap.file.FileSwap;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.ReadProperty;

/**
 * 名称：CompGRZY03
 * <p> 功能：提交转移信息<br>
 * @brief 批量数据写入文件，提交至核心，返回结果
 * @author 柏慧敏
 * @version 0.1 2018年8月21日 柏慧敏创建
 * 			0.2 2018年10月30日  柏慧敏修改  可以按照核心要求的编码格式写上传文件
 * @note
 */
@Component("CompGRZY03")
public class CompGRZY03 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompGRZY03.class);
	/** 上传文件本地路径 */
	private static final String file_swap_path = ReadProperty.getString("file_swap_path");
	/** 文件格式 */
	private static final String file_format = ReadProperty.getString("file_format");
	/** 数据分割符 */
	private static final String file_separators = ReadProperty.getString("file_separators");
	/** 取错误码 */
	private static final String ERROR_PLYC = ReadProperty.getString("recode_batcherr");
	/** 读核心返回文件编码格式 */
	private static final String encoding = ReadProperty.getString("readfile_encoding_bsp");

	@Autowired
	DP077Service dp077service;
	@Autowired
	ParamConfigImp paramConfigImp;
	@Override
	public int execute() {

		/* 入口参数赋值开始 */
		int instance = getInt("instance");// 实例号
		String operid = getString("_OPERID");
		String zylx = getString("zylx");// 转移类型
		String zyjylx = getString("zyjylx");// 转移交易类型
		/* 入口参数赋值结束 */
		FileWriterWithEncoding fw = null;
		File file = null;

		// 根据实例号查临时表，获取当前的个人转移信息
		List<DP077> list = dp077service.selectBySlh(instance);
		logger.info("[+]生成上传文件开始");
		// 定义日期格式yyyy-mm-dd
		SimpleDateFormat sdf = new SimpleDateFormat(IshConstants.GG_DATETIME_FORMATWFG);
		// 定义生成文件名称
		String wjmc = instance + "01_" + operid + sdf.format(new Date());
		// 文件全路径及文件名
		String filename = FileUtil.getFilename(file_swap_path, file_format, wjmc);
		try {
			// 查询结果不为空，将查询结果写入文件
			if (list != null && list.size() > 0) {
				file = new File(filename);
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				fw = new FileWriterWithEncoding(file.getAbsolutePath(), encoding, false);

				// 第一行写入列名
				fw.write("grzh"+ file_separators + "xingming" + file_separators + "zjlx" + file_separators + "zjhm");
				fw.write(System.lineSeparator());
				// 循环写入文件信息
				for (int i = 0; i < list.size(); i++) {
					fw.write(list.get(i).getAccnum1() + file_separators + list.get(i).getAccname1() + file_separators
							+ list.get(i).getCertitype() + file_separators + list.get(i).getCertinum());
					if (i < list.size() - 1) {
						fw.write(System.lineSeparator());
					}
					if (i % 1000 == 0)
						fw.flush();
				}
				fw.flush();
				// 将文件信息赋值给接口中设置的变量filename
				super.setValue("filename", filename);
				
				logger.info("[-]生成上传文件结束"+file.getAbsolutePath());
			}
		} catch (RuntimeException | IOException e) {
			e.printStackTrace();
			logger.error("生成上传文件出错,文件名："+ file.getPath()+"错误信息："+ e.getMessage(), e);
			throw new TransSingleException(CommonErrorCode.ERROR_WJXRSB, "");
		} finally {
			try {
				if (fw != null) {
					fw.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
				logger.error("关闭文件出错：" + e1.getMessage(), e1);
				throw new TransOtherException("系统错误，请查看日志！");
			}
		}
		if(zylx.equals(paramConfigImp.getValByMask("bsp.dp.trstype","0")) || zyjylx.equals(paramConfigImp.getValByMask("bsp.dp.zyjylx","1"))){
			super.setValue("zrdwzh",IshExpression.getRealUserExtInfo("dwdjh"));
		}else if(zylx.equals(paramConfigImp.getValByMask("bsp.dp.trstype","1")) || zyjylx.equals(paramConfigImp.getValByMask("bsp.dp.zyjylx","2"))){
			super.setValue("zcdwzh",IshExpression.getRealUserExtInfo("dwdjh"));
		}
		logger.info("[+]调用接口提交个人转移信息开始");

		// 调用接口提交个人转移信息
		XmlResObj data = super.sendExternal("BSP_DP_TJZYXX_01");
		XmlResHead head = data.getHead();

		// 若返回码为批量错误码，获取批量错误文件
		if (ERROR_PLYC.equals(head.getParticular_code())) {
			// 正式文件名为上传文件名.err
			// String errfilename = filename + ".err";
			String errfilename = "grzymessage" + ".err";// 本地测试自定义批量错误文件名 TODO
			File errfile = FileSwap.getFile(errfilename);
			List<BatchExceptionBean> errlist = new ArrayList<BatchExceptionBean>();
			List<String> fileList;
			// 若批量错误文件存在，读取错误信息
			if (errfile.exists()) {
				logger.info("[+]读取批量错误信息开始");
				try {
					fileList = FileUtils.readLines(errfile, encoding);
					String strLine;
					BatchExceptionBean bean;
					if (fileList != null) {
						for (int i = 0; i < fileList.size(); i++) {
							strLine = fileList.get(i);
							// 组装将要抛出的批量错误
							bean = new BatchExceptionBean(i + 1, strLine.split(file_separators)[0],
									strLine.split(file_separators)[1], strLine.split(file_separators)[2], "0",
									strLine.split(file_separators)[3].split("=")[0],
									"datalist", strLine.split(file_separators)[3].split("=")[1], "", "" , "", "");
							errlist.add(bean);
						}
					}
					logger.info("[-]读取批量错误信息结束");
				} catch (IOException e1) {
					logger.error("文件读取失败，文件名：" + errfile.getPath());
					e1.printStackTrace();
					throw new TransSingleException(CommonErrorCode.ERROR_WJDQSB, "");
				}
			} else {
				logger.error("批量错误文件不存在，文件名：" + errfilename);
				throw new TransSingleException("批量错误文件不存在。");
			}
			if (errlist != null && errlist.size() > 0) {
				TransBatchException e = new TransBatchException(errlist, "0");
				e.commit();
				throw e;
			}
		} else if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			logger.error("普通接口异常：" + head.getParticular_info());
			throw new TransSingleException(head.getParticular_code(), head.getParticular_info());
		}else{
			// 调用接口成功，删除临时表中的数据
			dp077service.deleteBySlh(instance);
		}
		logger.info("[-]调用接口提交个人转移信息结束");

		/* 出口参数赋值开始 */

		/* 出口参数赋值结束 */

		return 0;
	}

}
