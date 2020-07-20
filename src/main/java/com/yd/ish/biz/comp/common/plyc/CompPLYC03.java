package com.yd.ish.biz.comp.common.plyc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.BatchExceptionBean;
import com.yd.biz.exception.TransBatchException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.FileUtil;
import com.yd.org.util.CommonErrorCode;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.external.EssFactory;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.jdbc.BaseBean;
import com.yd.svrplatform.jdbc.PersistentBiz;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.ReadProperty;

/**
 * 名称：CompPLYC03.java
 * <p>
 * 功能：批量异常文件获取示例组件 <br>
 * 
 * @author 张洪超
 * @version 0.1 2018年2月6日 张洪超创建
 */
@Component("CompPLYC03")
public class CompPLYC03 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompPLYC03.class);
	/** 上传文件本地路径 */
	private static final String file_swap_path = ReadProperty.getString("file_swap_path");
	/** 文件格式 */
	private static final String file_format = ReadProperty.getString("file_format");
	/** 数据分割符 */
	private static final String file_separators = ReadProperty.getString("file_separators");

	@Autowired
	PersistentBiz persistentBiz;

	@Override
	public int execute() {

		/* 入口参数赋值开始 */
		String instanceid = getString("instanceid");// 示例号
		/* 入口参数赋值结束 */
		MainContext mainContext = MainContext.currentMainContext();
		DataPool dataPool = mainContext.getDataPool();
		// 查询动态列表插入数据库的临时数据,放入BaseBean数组，用来写文件用（也可以查询实体查询出list）
		String sql = "select w.xingming,w.zjlx,w.zjhm,w.instanceid " + "	from CPL_SHENBAO_TEMP w"
				+ " where w.instanceid =" + instanceid;
		BaseBean[] beans = persistentBiz.query(sql);
		// 获取文件名
		String filename = FileUtil.getFilename(file_swap_path, file_format, instanceid);
		// 第一行默认数据
		String firstCol = "xingming" + file_separators + "zjlx" + file_separators + "zjhm" 
					+ file_separators + "instanceid";
		// 文件路径
		String filepath = "";
		try {
			// 将数据写入文件,返回文件路径
			filepath = this.writeFile(beans, filename, firstCol);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("文件写入失败，文件名："+ filepath);
			throw new TransSingleException(CommonErrorCode.ERROR_WJXRSB, "");
		}
		dataPool.put(IshConstants._ISH_FILENAME, filepath);
		// 调用批量文件获取接口
		XmlResObj data = EssFactory.sendExternal("BSP_CHANNEL_PLYCWJ", mainContext, false);
		XmlResHead head = data.getHead();
		if (XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			logger.debug("接口调用成功");
			// 第一种获取批量错误文件情况：取报文头file的路径
			if ("7777".equals(data.getBody().get("returnCode"))) {
				logger.debug("批量异常");
				// 第二种获取批量错误文件情况：从FTP中取批量错误文件，文件名为上传文件名+.err
				String plycfilepath = filename + ".err";
				// 读文件,抛出批量异常
				this.readFile(plycfilepath);

			} else {
				// 从报文头获取的文件地址
				String plycfilepath = head.getFile();
				if (StringUtils.isNotBlank(plycfilepath)) {
					// 读文件，抛出批量异常
					this.readFile(plycfilepath);
				}
			}
		} else {
			logger.debug("普通接口异常：" + head.getParticular_info());
			throw new TransSingleException(head.getParticular_code(), head.getParticular_info());
		}
		/* 出口参数赋值开始 */
		/* 出口参数赋值结束 */

		return 0;
	}

	/**
	 * 写文件
	 * 
	 * @param beans
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public String writeFile(BaseBean[] beans, String filename, String firstCol) throws IOException {

		logger.info("[+]创建批量文件开始。filename：" + filename);
		// 创建文件
		File file = new File(filename);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		FileWriter fw = new FileWriter(file.getAbsolutePath(), false);
		// 先写入第一行数据
		fw.write(firstCol);
		// 换行符
		fw.write("\n");
		// 如果结果集不为空，将数据以~分割的形式写到文件里
		boolean first = true;
		for (int i = 0; i < beans.length; i++) {
			for (int j = 0; j < firstCol.split(file_separators).length; j++) {
				// 字段间添加分隔符
				if (!first) {
					fw.write(file_separators);
				}
				// 当字段为空时写入""
				String col = StringUtils.isBlank(String.valueOf(beans[i].get(j))) ? ""
						: String.valueOf(beans[i].get(j));
				fw.write(col);
				first = false;
			}
			// 换行符
			first = true;
			fw.write("\n");
		}
		fw.close();

		logger.info("[+]创建批量文件结束。getAbsolutePath：" + file.getAbsolutePath());
		logger.info("[+]创建批量文件结束。getPath：" + file.getPath());
		return file.getPath();
	}

	/**
	 * 读文件
	 * 
	 * @param beans
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public String readFile(String filename) {
		logger.info("[+]读取批量文件开始。filename：" + filename);
		// 取文件
		File file = new File(file_swap_path+filename);
		FileUtil fileUtil = new FileUtil();
		if (!file.exists() || !file.isFile() || file.length() <= 0) {
			// 对文件路径特殊处理 Windows特殊处理
			String f = filename;
			if (f.contains("\\")) {
				f = f.replaceAll("\\\\", "/");
			}
			// FTP文件下载
			fileUtil.downloadFile(file, f);
		}
		if (file.exists() && file.isFile() && file.length() > 0) {
			// 读取文件，将文件内数据存入数据库
			List<BatchExceptionBean> list = new ArrayList<BatchExceptionBean>();
			List<String> fileList;
			try {
				fileList = FileUtils.readLines(file, "utf-8");
				// 读取第一行
				String firstLine = fileList.get(0);
				String[] errArry = firstLine.split(file_separators);
				// 取各个元素的下标
				int count = 0;
				int item = 0;
				int content = 0;
				int reason = 0;
				for (int i = 0; i < errArry.length; i++) {
					if ("count".equals(errArry[i])) {
						count = i;
					}
					if ("item".equals(errArry[i])) {
						item = i;
					}
					if ("content".equals(errArry[i])) {
						content = i;
					}
					if ("reason".equals(errArry[i])) {
						reason = i;
					}
				}
				for (int i = 1; i < fileList.size(); i++) {
					String strLine = fileList.get(i);
					/**
					 * BatchExceptionBean bean = new BatchExceptionBean(seqno,
					 * item, content, reason, infotype, locationKey, key1, key2,
					 * key3, key4, key5);
					 * 
					 * seqno int 记录号 
					 * item String 错误字段名
					 * content String 错误值
					 * reason String 错误原因
					 * infotype String 信息类型 0-错误信息 1-提示信息
					 * locationKey String 定位键字，多个以半角逗号分隔 
					 * datalistId String 显示批量错误的动态列表ID，可省略
					 * 
					 *  key1 String 定位键字1值，与定位键字对应
					 *  key2 String 定位键字2值，与定位键字对应
					 *  key3 String 定位键字3值，与定位键字对应 
					 *  key4 String 定位键字4值，与定位键字对应
					 *  key5 String 定位键字5值，与定位键字对应
					 * 
					 *  item 实际查询动态列表中错误列对应的表字段名  
					 * 	locationKey
					 * 1、普通批量错误时，此项为出错字段的id
					 * 2、实际查询动态列表中可定位到一条记录的查询条件字段，最多5项，与定位键值对应。 例如：表查询sql为
					 * "select aa,bb,cc,dd from test",aa+bb可定位到查询的一条记录，
					 * locationKey的值就需要赋为 aa,bb，而key1的值是aa字段的值，key2的值为bb字段的值。
					 * 
					 */
					// 组装将要抛出的批量
					BatchExceptionBean bean = new BatchExceptionBean(
							Integer.parseInt(strLine.split(file_separators)[count]),
							strLine.split(file_separators)[item],
							strLine.split(file_separators)[content],
							strLine.split(file_separators)[reason],
							"1", "sllist", "", 
							new String[] {});
					list.add(bean);
					// 此处可以取到文件的数据进行业务处理
				}
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("文件写入失败，文件名："+ file.getPath());
				throw new TransSingleException(CommonErrorCode.ERROR_WJXRSB, "");
			}
			if (list != null && list.size() > 0) {
				// 为了异步提交能获取批量异常，按照以下方式抛出
				TransBatchException batchException = new TransBatchException(list);
				throw new TransBatchException(batchException.getJsonMessage());
			}
		}
		logger.info("[+]读取批量文件结束。getAbsolutePath：" + file.getAbsolutePath());
		logger.info("[+]读取批量文件结束。getPath：" + file.getPath());
		return file.getPath();
	}
}
