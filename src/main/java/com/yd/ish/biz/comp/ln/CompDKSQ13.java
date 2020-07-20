package com.yd.ish.biz.comp.ln;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.util.ReadProperty;

/**
 * 名称：CompDKSQ13
 * <p>
 * 功能：提交贷款申请<br>
 * 
 * @brief 提交贷款申请
 * @author 柏慧敏
 * @version 0.1 2018年7月27日 柏慧敏创建
 * @note
 */
@Component("CompDKSQ13")
public class CompDKSQ13 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompDKSQ13.class);
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
		int instance = getInt("_IS");
		String operid = getString("_OPERID");
		String dkdblx = getString("dkdblx");
		/* 入口参数赋值结束 */
		logger.info("[+]生成上传文件开始");
		//定义借款人、共有权人、抵押人、抵押物、保证文件信息
		String jkrfile = creatJkrFile(instance, operid);
		String gyqrfile = creatGyqrFile(instance, operid);
		String dyrfile = "";
		String dywfile = "";
		String bzfile = "";
		//贷款担保类型mask码
		String dkdblxmask = paramConfigImp.getMask("bsp.ln.dkdblx." + dkdblx);
		//贷款担保类型包含抵押信息
		if ("01".equals(dkdblxmask) || "71".equals(dkdblxmask)) {
			//生成抵押信息相关文件
			dyrfile = creatDyrFile(instance, operid);
			dywfile = creatDywFile(instance, operid);
		} 
		//贷款担保类型包含保证信息
		else if ("03".equals(dkdblxmask) || "73".equals(dkdblxmask)) {
			//生成保证信息相关文件
			bzfile = creatBzFile(instance, operid);
		} 
		//贷款担保类型包含抵押和保证信息
		else if ("70".equals(dkdblxmask) || "72".equals(dkdblxmask)) {
			//生成抵押信息和保证信息相关文件
			dyrfile = creatDyrFile(instance, operid);
			dywfile = creatDywFile(instance, operid);
			bzfile = creatBzFile(instance, operid);
		}
		//定义文件名数组
		String[] filenames = { jkrfile, gyqrfile, dyrfile, dywfile, bzfile };
		StringBuffer filename = new StringBuffer();
		//循环文件名数组
		for (int i = 0; i < filenames.length; i++) {
			//文件名不为空，增加至最终接口传递信息的filename中
			if (!"".equals(filenames[i])) {
				if(filename != null && filename.length()>0){
					filename.append(",");
				}
				filename.append(filenames[i]);
			}
		}
		// 将文件信息赋值给接口中设置的变量filename
		super.setValue("filename", filename.toString());
		logger.info("[-]生成上传文件结束");

		logger.info("[+]调用接口提交贷款申请信息开始");
		// 调用接口根据借款合同编号获取还款人信息
		XmlResObj data = super.sendExternal("BSP_LN_DKSQ_01");
		XmlResHead head = data.getHead();

		// 若返回码为批量错误码，获取批量错误文件
		if (ERROR_PLYC.equals(head.getParticular_code())) {
			String errfilename;
			File errfile;
			List<BatchExceptionBean> errlist = new ArrayList<BatchExceptionBean>();
			List<String> fileList = null;
			String strLine;
			// 正式文件名为上传文件名.err
			for (int i = 0; i < filenames.length; i++) {
				if (!"".equals(filenames[i])) {
					// errfilename = filename + ".err";
					errfilename = "errmesasge" + i + ".err";
					errfile = FileSwap.getFile(errfilename);
					// 若批量错误文件存在，读取错误信息
					if (errfile != null && errfile.exists()) {
						logger.info("[+]读取批量错误信息开始");
						//若为借款人信息的错误文件
						if (jkrfile.equals(filenames[i])) {
							try {
								fileList = FileUtils.readLines(errfile, encoding);
								if (fileList != null) {
									StringBuffer cwxx = new StringBuffer();
									for (int j = 0; j < fileList.size(); j++) {
										strLine = fileList.get(j);
										cwxx.append("[" + strLine.split(file_separators)[3] + ","
												+ strLine.split(file_separators)[4] + ","
												+ strLine.split(file_separators)[5] + ","
												+ strLine.split(file_separators)[2] + "]");
									}
									//抛出借款人错误信息
									throw new TransSingleException("借款申请人列表中信息有误：" + cwxx.toString());
								}
							} catch (IOException e) {
								logger.error("文件读取失败，文件名：" + errfile.getPath());
								e.printStackTrace();
								throw new TransSingleException(CommonErrorCode.ERROR_WJDQSB, "");
							}

						}
						//若为保证信息错误文件
						if (bzfile.equals(filenames[i])) {
							errlist = createBatchException(errfile, fileList, errlist, "bzlist", "14");
						}
						//若为抵押人信息错误文件
						if (dyrfile.equals(filenames[i])) {
							errlist = createBatchException(errfile, fileList, errlist, "dyrlist", "12");
						}
						//若为共有权人信息错误文件
						if (gyqrfile.equals(filenames[i])) {
							errlist = createBatchException(errfile, fileList, errlist, "gycqrlist", "11");
						}
						//若为抵押物信息错误文件
						if (dywfile.equals(filenames[i])) {
							try {
								fileList = FileUtils.readLines(errfile, encoding);
								if (fileList != null) {
									BatchExceptionBean bean;
									for (int j = 0; j < fileList.size(); j++) {
										strLine = fileList.get(j);
										// 组装将要抛出的批量错误
										bean = new BatchExceptionBean(Integer.parseInt("13" + j + 1),
												strLine.split(file_separators)[0], strLine.split(file_separators)[1],
												strLine.split(file_separators)[2], "0",
												strLine.split(file_separators)[3].split("=")[0], "dywlist",
												strLine.split(file_separators)[3].split("=")[1], "", "", "", "");
										errlist.add(bean);
									}
								}
								logger.info("[-]读取批量错误信息结束");
							} catch (IOException e1) {
								logger.error("文件读取失败，文件名：" + errfile.getPath());
								e1.printStackTrace();
								throw new TransSingleException(CommonErrorCode.ERROR_WJDQSB,"");
							}
						}
					} else {
						logger.error("批量错误文件不存在，文件名：" + errfilename);
						throw new TransSingleException("批量错误文件不存在。");
					}
				}

			}
			//若错误信息不为空，抛出批量错误信息
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
		/* 出口参数赋值开始 */

		/* 出口参数赋值结束 */

		return 0;
	}

	/**
	 * 生成借款人文件
	 * 
	 * @param instance
	 *            实例号
	 * @param operid
	 *            操作员
	 * @return 文件名（含路径）
	 */
	private String creatJkrFile(int instance, String operid) {
		FileWriterWithEncoding fw = null;
		File file = null;
		// 返回结果
		String returnresult;
		// 定义日期格式yyyyMMddHHmmss
		SimpleDateFormat sdf = new SimpleDateFormat(IshConstants.GG_DATETIME_FORMATWFG);
		// 定义日期格式yyyy-MM-dd
		SimpleDateFormat sdfnyr = new SimpleDateFormat(IshConstants.GG_DATE_FORMAT);
		// 定义文件名称
		String wjmc = instance + "01_" + operid + sdf.format(new Date());
		// 根据实例号和类型查临时表，获取当前的借款申请人信息
		DP077 dp077 = new DP077();
		dp077.setInstance(instance);
		dp077.setDpbusitype("10");//借款申请人
		List<DP077> list = dp077service.selectByCause(dp077);
		logger.info("[+]生成上传文件开始");
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
				String khrq="";
				// 第一行写入列名
				fw.write("xingming" + file_separators + "zjlx" + file_separators + "zjhm" + file_separators + "jkrlx"
						+ file_separators + "cdgx" + file_separators + "xingbie" + file_separators + "sjhm"
						+ file_separators + "csny" + file_separators + "hyzk" + file_separators + "zhiye"
						+ file_separators + "zhichen" + file_separators + "zhiwu" + file_separators + "xueli"
						+ file_separators + "dwdh" + file_separators + "jtzz" + file_separators + "dwmc"
						+ file_separators + "dwdz" + file_separators + "grzh" + file_separators + "grzhzt"
						+ file_separators + "khrq" + file_separators + "qshjny" + file_separators + "jzny"
						+ file_separators + "grjcjs" + file_separators + "yjce" + file_separators + "grzhye");
				fw.write(System.lineSeparator());
				// 循环写入文件信息
				for (int i = 0; i < list.size(); i++) {
					if(list.get(i).getEnddate()!=null){
						khrq=sdfnyr.format(list.get(i).getEnddate());
					}else{
						khrq="";
					}
					fw.write(
							list.get(i).getAccname1() + file_separators + list.get(i).getCertitype() + file_separators
									+ list.get(i).getCertinum() + file_separators + list.get(i).getFundsouflag()
									+ file_separators + list.get(i).getProptype() + file_separators
									+ list.get(i).getSex() + file_separators + list.get(i).getSjhm() + file_separators
									+ list.get(i).getAgentop()
									+ file_separators + list.get(i).getHyzk() + file_separators + list.get(i)
											.getAccname2()
									+ file_separators + list.get(i).getZip() + file_separators
									+ list.get(i).getPayvoubank() + file_separators + list.get(i).getOnym()
									+ file_separators + list.get(i).getUnitaccnum1() + file_separators
									+ list.get(i).getJtzz() + file_separators + list.get(i).getUnitaccname1()
									+ file_separators + list.get(i).getXmqp() + file_separators
									+ list.get(i).getAccnum1() + file_separators + list.get(i).getAccnum2()
									+ file_separators + khrq + file_separators
									+ list.get(i).getBegym() + file_separators + list.get(i).getEndym()
									+ file_separators + list.get(i).getBasenum() + file_separators
									+ list.get(i).getAmt1() + file_separators + list.get(i).getAmt2());
					if (i < list.size() - 1) {
						fw.write(System.lineSeparator());
					}
					if (i % 1000 == 0)
						fw.flush();
				}
				fw.flush();
				returnresult = filename;
				logger.info("[-]生成上传文件结束");
			} else {
				logger.info("借款人信息查询结果为空");
				returnresult = "";
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
		return returnresult;
	}
	/**
	 * 生成共有权人文件
	 * 
	 * @param instance
	 *            实例号
	 * @param operid
	 *            操作员
	 * @return 文件名（含路径）
	 */
	private String creatGyqrFile(int instance, String operid) {
		FileWriterWithEncoding fw = null;
		File file = null;
		// 返回结果
		String returnresult;
		// 定义日期格式yyyy-mm-dd
		SimpleDateFormat sdf = new SimpleDateFormat(IshConstants.GG_DATETIME_FORMATWFG);
		// 定义文件名称
		String wjmc = instance + "02_" + operid + sdf.format(new Date());
		// 根据实例号和类型查临时表，获取共有权人信息
		DP077 dp077 = new DP077();
		dp077.setInstance(instance);
		dp077.setDpbusitype("11");//共有权人
		List<DP077> list = dp077service.selectByCause(dp077);
		logger.info("[+]生成上传文件开始");
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
				fw.write("xingming" + file_separators + "zjlx" + file_separators + "zjhm" + file_separators + "gyqrlx"
						+ file_separators + "yjkrgx" + file_separators + "xingbie" + file_separators + "sjhm");
				fw.write(System.lineSeparator());
				// 循环写入文件信息
				for (int i = 0; i < list.size(); i++) {
					fw.write(list.get(i).getAccname1() + file_separators + list.get(i).getCertitype() + file_separators
							+ list.get(i).getCertinum() + file_separators + list.get(i).getFundsouflag()
							+ file_separators + list.get(i).getProptype() + file_separators + list.get(i).getSex()
							+ file_separators + list.get(i).getSjhm());
					if (i < list.size() - 1) {
						fw.write(System.lineSeparator());
					}
					if (i % 1000 == 0)
						fw.flush();
				}
				fw.flush();
				returnresult = filename;
				logger.info("[-]生成上传文件结束");
			} else {
				logger.info("共有产权人信息查询结果为空");
				returnresult = "";
			}
		} catch (RuntimeException | IOException e) {
			e.printStackTrace();
			logger.error("生成上传文件出错,文件名：" + file.getPath() + "错误信息：" + e.getMessage(), e);
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
		return returnresult;
	}
	/**
	 * 生成抵押人文件
	 * 
	 * @param instance
	 *            实例号
	 * @param operid
	 *            操作员
	 * @return 文件名（含路径）
	 */
	private String creatDyrFile(int instance, String operid) {
		FileWriterWithEncoding fw = null;
		File file = null;
		// 返回结果
		String returnresult;
		// 定义日期格式yyyy-mm-dd
		SimpleDateFormat sdf = new SimpleDateFormat(IshConstants.GG_DATETIME_FORMATWFG);
		// 定义文件名称
		String wjmc = instance + "03_" + operid + sdf.format(new Date());
		// 根据实例号和类型查临时表，获取当前的抵押人信息
		DP077 dp077 = new DP077();
		dp077.setInstance(instance);
		dp077.setDpbusitype("12");//抵押人信息
		List<DP077> list = dp077service.selectByCause(dp077);
		logger.info("[+]生成上传文件开始");
		// 文件全路径及文件名
		String filename = FileUtil.getFilename(file_swap_path, file_format, wjmc);
		try {
			// 查询结果不为空，2将查询结果写入文件
			if (list != null && list.size() > 0) {
				file = new File(filename);
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				fw = new FileWriterWithEncoding(file.getAbsolutePath(), encoding, false);

				// 第一行写入列名
				fw.write("xingming" + file_separators + "zjlx" + file_separators + "zjhm" + file_separators + "xingbie"
						+ file_separators + "dwmc" + file_separators + "lxdh");
				fw.write(System.lineSeparator());
				// 循环写入文件信息
				for (int i = 0; i < list.size(); i++) {
					fw.write(list.get(i).getAccname1() + file_separators + list.get(i).getCertitype() + file_separators
							+ list.get(i).getCertinum() + file_separators + list.get(i).getSex() + file_separators
							+ list.get(i).getUnitaccname1() + file_separators + list.get(i).getAccnum1());
					if (i < list.size() - 1) {
						fw.write(System.lineSeparator());
					}
					if (i % 1000 == 0)
						fw.flush();
				}
				fw.flush();
				returnresult = filename;
				logger.info("returnresult" + returnresult);
				logger.info("[-]生成上传文件结束");
			} else {
				logger.info("抵押人信息查询结果为空");
				returnresult = "";
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
		return returnresult;
	}
	/**
	 * 生成抵押物文件
	 * 
	 * @param instance
	 *            实例号
	 * @param operid
	 *            操作员
	 * @return 文件名（含路径）
	 */
	private String creatDywFile(int instance, String operid) {
		FileWriterWithEncoding fw = null;
		File file = null;
		// 返回结果
		String returnresult;
		// 定义日期格式yyyy-mm-dd
		SimpleDateFormat sdfnyr = new SimpleDateFormat(IshConstants.GG_DATE_FORMAT);
		// 定义日期格式yyyyMMddHHmmss
		SimpleDateFormat sdf = new SimpleDateFormat(IshConstants.GG_DATETIME_FORMATWFG);
		// 定义文件名称
		String wjmc = instance + "04_" + operid + sdf.format(new Date());
		// 根据实例号和类型查临时表，获取当前的抵押物信息
		DP077 dp077 = new DP077();
		dp077.setInstance(instance);
		dp077.setDpbusitype("13");//抵押物信息
		List<DP077> list = dp077service.selectByCause(dp077);
		logger.info("[+]生成上传文件开始");
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
				fw.write("dylx" + file_separators + "dywlx" + file_separators + "dywpgjz" + file_separators + "dywmj"
						+ file_separators + "dywjgrq" + file_separators + "dywqzh" + file_separators + "dywfwzl");
				fw.write(System.lineSeparator());
				// 循环写入文件信息
				for (int i = 0; i < list.size(); i++) {
					fw.write(list.get(i).getFundsouflag() + file_separators + list.get(i).getProptype()
							+ file_separators + list.get(i).getAmt1() + file_separators + list.get(i).getAmt2()
							+ file_separators + sdfnyr.format(list.get(i).getEnddate()) + file_separators
							+ list.get(i).getUnitaccnum1() + file_separators + list.get(i).getJtzz());
					if (i < list.size() - 1) {
						fw.write(System.lineSeparator());
					}
					if (i % 1000 == 0)
						fw.flush();
				}
				fw.flush();
				returnresult = filename;
				logger.info("[-]生成上传文件结束");
			} else {
				logger.info("抵押物信息查询结果为空");
				returnresult = "";
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
		return returnresult;
	}
	
	/**
	 * 生成保证信息文件
	 * 
	 * @param instance
	 *            实例号
	 * @param operid
	 *            操作员
	 * @return 文件名（含路径）
	 */
	private String creatBzFile(int instance, String operid) {
		FileWriterWithEncoding fw = null;
		File file = null;
		// 返回结果
		String returnresult;
		// 定义日期格式yyyy-mm-dd
		SimpleDateFormat sdf = new SimpleDateFormat(IshConstants.GG_DATETIME_FORMATWFG);
		// 定义文件名称
		String wjmc = instance + "05_" + operid + sdf.format(new Date());
		// 根据实例号和类型查临时表，获取当前的保证信息
		DP077 dp077 = new DP077();
		dp077.setInstance(instance);
		dp077.setDpbusitype("14");//保证信息
		List<DP077> list = dp077service.selectByCause(dp077);
		logger.info("[+]生成上传文件开始");
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
				fw.write("xingming" + file_separators + "zjlx" + file_separators + "zjhm" + file_separators + "bzrlx"
						+ file_separators + "grzh" + file_separators + "sjhm" + file_separators + "grzhye");
				fw.write(System.lineSeparator());
				// 循环写入文件信息
				for (int i = 0; i < list.size(); i++) {
					fw.write(list.get(i).getAccname1() + file_separators + list.get(i).getCertitype() + file_separators
							+ list.get(i).getCertinum() + file_separators + list.get(i).getProptype() + file_separators
							+ list.get(i).getAccnum1() + file_separators + list.get(i).getSjhm() + file_separators
							+ list.get(i).getAmt1());
					if (i < list.size() - 1) {
						fw.write(System.lineSeparator());
					}
					if (i % 1000 == 0)
						fw.flush();
				}
				fw.flush();
				returnresult = filename;
				logger.info("[-]生成上传文件结束");
			} else {
				logger.info("保证信息查询结果为空");
				returnresult = "";
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
		return returnresult;
	}
	
	/**
	 * 获取批量错误信息（共有权人，抵押人，保证信息）
	 * 
	 * @param errfile 错误文件
	 * @param fileList          
	 * @param errlist 错误信息list
	 * @param datalist  动态列表id
	 * @param bz  查询类型
	 * @return 错误信息list
	 */
	private List<BatchExceptionBean> createBatchException(File errfile, List<String> fileList, List<BatchExceptionBean> errlist,
			String datalist, String bz) {
		try {
			BatchExceptionBean bean;
			fileList = FileUtils.readLines(errfile, encoding);
			if (fileList != null) {
				for (int j = 0; j < fileList.size(); j++) {
					String strLine = fileList.get(j);
					// 组装将要抛出的批量错误
					bean = new BatchExceptionBean(Integer.parseInt(bz + j + 1),
							strLine.split(file_separators)[0], strLine.split(file_separators)[1],
							strLine.split(file_separators)[2], "0",
							strLine.split(file_separators)[3].split("=")[0] + ","
									+ strLine.split(file_separators)[4].split("=")[0] + ","
									+ strLine.split(file_separators)[5].split("=")[0],
							datalist, strLine.split(file_separators)[3].split("=")[1],
							strLine.split(file_separators)[4].split("=")[1],
							strLine.split(file_separators)[5].split("=")[1], "", "");
					errlist.add(bean);
				}
			}
			logger.info("[-]读取批量错误信息结束");
		} catch (IOException e1) {
			logger.error("文件读取失败，文件名：" + errfile.getPath());
			e1.printStackTrace();
			throw new TransSingleException(CommonErrorCode.ERROR_WJDQSB, "");
		}
		return errlist;
	}
}
