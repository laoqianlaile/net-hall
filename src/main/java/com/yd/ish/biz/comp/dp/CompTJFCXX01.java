package com.yd.ish.biz.comp.dp;

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
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.dataswap.file.FileSwap;
import com.yd.svrplatform.comm_mdl.external.EssFactory;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.ReadProperty;
import com.yd.workflow.util.Constants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 
 * 名称：CompTJFCXX01.java
 * <p>
 * 功能： 提交封存信息<br>
 * 
 * @author wanghe
 * @version 0.1 2018年5月28日 wanghe创建
 * 			0.2 2018年10月30日  柏慧敏修改  可以按照核心要求的编码格式写上传文件
 */
@Component("CompTJFCXX01")
public class CompTJFCXX01 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompTJFCXX01.class);

	/** 文件格式 */
	private static final String file_format = ReadProperty.getString("file_format");
	/** 数据分割符 */
	private static final String file_separators = ReadProperty.getString("file_separators");
	/** 取错误码 */
	private static final String ERROR_PLYC = ReadProperty.getString("recode_batcherr");
	/** 编码格式 */
	private static final String READFILE_ENCODING_BSP = ReadProperty.getString("readfile_encoding_bsp");
	/** 读核心返回文件编码格式 */
	private static final String encoding = ReadProperty.getString("readfile_encoding_bsp");
	/** 启用凭证系统标记 **/
	private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");
	/** 上传文件本地路径 */
	private static final String file_swap_path = ReadProperty.getString("file_swap_path");
	@Autowired
	DP077Service dp077service;

	@Override
	public int execute() {

		/* 入口参数赋值开始 */
		int instance = getInt("_IS");
		String operid = getString("_OPERID");
		/* 入口参数赋值结束 */
		MainContext mainContext = MainContext.currentMainContext();
		// 查询动态列表临时数据
		FileWriterWithEncoding fw = null;
		File file = null;

		// 根据实例号查临时表，获取当前的个人封存信息
		List<DP077> list = dp077service.selectBySlh(instance);

		// 校验是否存在相同的个人账号
		for (int i = 0; i < list.size(); i++) {
			// 判断是否存在相同个人账号
			for (int j = 0; j < list.size(); j++) {
				if (list.get(i).getAccnum1().toString().equals(list.get(j).getAccnum1().toString()) && i != j) {
					String grzh = list.get(i).getAccnum1();
					logger.error("存在相同的个人账号信息，请确认后重新提交。个人账号：" + grzh);
					throw new TransSingleException("存在相同的个人账号信息，请确认后重新提交。");
				}
			}
		}
		try {
			// 查询结构不为空，将查询结果写入文件
			if (list != null && list.size() > 0) {
				// 生成上传文件名称
				logger.info("[+]生成上传文件开始-------");
				// 定义日期格式yyyy-mm-dd
				SimpleDateFormat sdf = new SimpleDateFormat(IshConstants.GG_DATETIME_FORMATWFG);
				// 定义生成文件名称
				String wjmc = instance + "01_" + operid + sdf.format(new Date());
				// 文件全路径及文件名
				String filename = FileUtil.getFilename(file_swap_path, file_format, wjmc);
				logger.info("生成的文件名称：" + filename);
				file = new File(filename);
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				fw = new FileWriterWithEncoding(file.getAbsolutePath(), READFILE_ENCODING_BSP, false);

				// 第一行写入列名

				/*
				 * 上传文件具体内容 grzh 个人账号 xingming 姓名 zjhm 证件号码 grjcjs 个人缴存基数
				 * yjce月缴存额（单位加个人） grzh~xingming~zjlx~zjhm~grjcjs~yjce
				 */
				fw.write("grzh" + file_separators + "xingming" + file_separators + "zjlx" + file_separators + "zjhm" + file_separators + "grjcjs"
						+ file_separators + "yjce");
				fw.write(System.lineSeparator());
				// 循环写入文件信息
				for (int i = 0; i < list.size(); i++) {
					fw.write(list.get(i).getAccnum1() + file_separators + list.get(i).getAccname1() + file_separators
							+list.get(i).getCertitype()+ file_separators + list.get(i).getCertinum() + file_separators
							+ list.get(i).getBasenum() + file_separators + list.get(i).getAmt1());
					if (i < list.size() - 1) {
						fw.write(System.lineSeparator());
					}
					if (i % 1000 == 0)
						fw.flush();
				}
				fw.flush();
				// 将文件信息赋值给接口中设置的变量filename
				super.setValue("filename", filename);
				logger.info("[-]生成上传文件结束-------");
			}
		} catch (RuntimeException | IOException e) {
			e.printStackTrace();
			logger.error("生成上传文件出错,文件名："+ file.getPath()+"错误信息：" + e.getMessage(), e);
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

		XmlResObj data = super.sendExternal("BSP_DP_TJGRFC_01");
		XmlResHead head = data.getHead();
		Map<String,Object> body = null;
		//流水号
		String lsh = "";
		//经办机构
		String jbjg = "";
		//凭证文件名称
		String pzfilename = "";
		if (XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			body = data.getBody();
			//流水号
			lsh = body.get("lsh")==null?"":body.get("lsh").toString();
			//经办机构
			jbjg = body.get("jbjg")==null?"":body.get("jbjg").toString();
			//凭证文件名称
			pzfilename = body.get("filename")==null ? "":body.get("filename").toString();

			logger.debug("接口调用成功");

		} else if (ERROR_PLYC.equals(head.getParticular_code())) {
			// 本地测试自定义批量错误文件名，正式文件名为上传文件名.err
			// String errfilename = file.getName() + ".err";
			String errfilename = "fcerrMessage" + ".err";
			File errfile = FileSwap.getFile(errfilename);
			List<BatchExceptionBean> errlist = new ArrayList<BatchExceptionBean>();
			List<String> fileList;
			// 若批量错误文件存在，读取错误信息
			if (errfile.exists()) {
				logger.info("[+]读取批量错误信息开始------");
				try {
					fileList = FileUtils.readLines(errfile, READFILE_ENCODING_BSP);
					if (fileList != null) {
						for (int i = 0; i < fileList.size(); i++) {
							String strLine = fileList.get(i);
							// 组装将要抛出的批量错误
							BatchExceptionBean bean = new BatchExceptionBean(i + 1, strLine.split(file_separators)[0],
									strLine.split(file_separators)[1], strLine.split(file_separators)[2], "1",
									strLine.split(file_separators)[3].split("=")[0] + ",",
									"datalist1", strLine.split(file_separators)[3].split("=")[1],
									"", "", "", "");
							errlist.add(bean);
						}
					}
					logger.debug("[-]读取批量错误信息结束------");
				} catch (IOException e1) {
					logger.error("文件读取失败，文件名：" + errfile.getPath());
					e1.printStackTrace();
					throw new TransSingleException(CommonErrorCode.ERROR_WJDQSB, "");
				}
			} else {
				logger.debug("批量错误文件不存在，文件名：" + errfilename);
				throw new TransSingleException("批量错误文件不存在。");
			}
			if (errlist != null && errlist.size() > 0) {
				TransBatchException e = new TransBatchException(errlist, "0");
				e.commit();
				throw e;
			}
		} else if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			logger.debug("普通接口异常：" + head.getParticular_info());
			throw new TransSingleException(head.getParticular_code(), head.getParticular_info());
		}

		/* 出口参数赋值开始 */
		setOutParam("lsh",lsh);//流水号
		setOutParam("jbjg",jbjg);//经办机构
		if("1".equals(pzsystemFlag)){ //启用凭证系统时设置出口参数
			setOutParam("filename",pzfilename);//凭证文件名称
		}
		/* 出口参数赋值结束 */

		return 0;
	}
}
