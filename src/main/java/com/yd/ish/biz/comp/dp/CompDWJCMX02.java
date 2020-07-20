package com.yd.ish.biz.comp.dp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransOtherException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.FileUtil;
import com.yd.ish.common.util.GetDownFileMap;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.svrplatform.comm_mdl.cache.YDMemcachedManager;
import com.yd.svrplatform.comm_mdl.dataswap.file.FileSwap;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.comm_mdl.key_generator.SimpleKeyGenerator;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.jdbc.PersistentBiz;
import com.yd.svrplatform.util.ReadProperty;
import com.yd.ydpx.exception.YdpxRuntimeException;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * 
 * 名称：CompDWJCMX02.java
 * <p>
 * 功能：批量查询单位下员工缴存信息 <br>
 * 
 * @author 王赫
 * @version 0.1 2018年6月20日 王赫创建
 *         V0.2 2018年9月27日 柏慧敏修改 批量数据写入excel
 */
@Component("CompDWJCMX02")
public class CompDWJCMX02 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompDWJCMX02.class);

	/** 上传文件本地路径 */
	private static final String file_export_path = ReadProperty.getString("file_export_path");
	/** 数据分割符 */
	private static final String separators = ReadProperty.getString("file_separators");	
	/** 数据分割符 */
	private static final String wjm = "缴存明细";
	/**核心返回文件编码格式*/
	private static final String encoding = ReadProperty.getString("readfile_encoding_bsp");
	
	@Autowired
	DP077Service dp077service;

	@Autowired
	SimpleKeyGenerator simpleKeyGenerator;

	@Autowired
	ParamConfigImp paramConfigImp;

	@Autowired
	PersistentBiz persistentBiz;
	
	@Autowired
	GetDownFileMap getDownFileMap;

	@Override

	public int execute() {
		/* 入口参数赋值开始 */
		String djh = getString("djh");
		/* 入口参数赋值结束 */
		
		// 定义生成文件名称
		String wjmc = wjm + djh;
		// 文件全路径及文件名
		String exporfilename = FileUtil.getFilename(file_export_path, ".xls", wjmc);
		File exportfile = new File(exporfilename);
		//若无父级文件夹，创建文件夹
		if (!exportfile.getParentFile().exists()) {
			exportfile.getParentFile().mkdirs();
		}
		logger.info("[+]调用接口查询批量单位员工缴存信息BSP_DP_DWJCXX_03开始");
		// 调用接口单位汇补缴批量信息
		XmlResObj data = super.sendExternal("BSP_DP_DWJCXX_03");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}
		// 调用综服接口从body中获取文件名
		Map<String, Object> body = data.getBody();
		String filename = body.get("file").toString();
		if (!(filename == null)) {
			File file = FileSwap.getFile(filename);
			if (file == null) {
				logger.error("下传文件不存在：" + filename);
				throw new TransOtherException("系统错误，请查看日志！");
			} else {
				// 解析下传文件
				BufferedReader br = null;
				try {
					br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
					//写入excel
					writeExcel(br, exportfile.getAbsolutePath(), new int[] {20, 20, 20, 20, 20, 20, 20 },
							new String[] {"个人账号", "姓   名", "缴存类型", "缴存金额","缴存起始月份", "缴存终止月份",
									"入账日期" });
					logger.info("[-]调用接口查询批量单位员工缴存信息BSP_DP_DWJCXX_03结束");
				} catch (IOException e) {
					e.printStackTrace();
					throw new TransOtherException("系统错误，请查看日志！");
				} finally {
					try {
						if (br != null) {
							br.close();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
						logger.error("关闭文件出错：" + e1.getMessage(), e1);
						throw new TransOtherException("系统错误，请查看日志！");
					}
				}
			}
			String url = genterFileHttpUrl(exportfile,30);
			
			/* 出口参数赋值开始 */
			setOutParam("url", url); // 跳转地址
			/* 出口参数赋值结束 */
		}
		return 0;
	}

	/**
	 * 写入EXCEL文件
	 * 
	 * @param br         准备写入的批量数据
	 * @param filePath   文件路径
	 * @param widths     长度（int数组)
	 * @param columnName 列名
	 */
	public void writeExcel(BufferedReader br, String filePath, int[] widths, String[] columnName) {
		WritableWorkbook workbook = null;
		WritableSheet sheet = null;
		// 从第一行开始写入
		int rowNum = 1; 
		boolean isHave = false;
		try {

			WritableCellFormat format = new WritableCellFormat();
			format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			format.setAlignment(jxl.format.Alignment.CENTRE);
			format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, jxl.format.Colour.DARK_BLUE);
			// 创建Excel文件
			workbook = Workbook.createWorkbook(new File(filePath)); 
			int sheetNo = 0;
			// 记录行文件
			String tmp = null;
			//是否为文件表头信息
			Boolean first = true; 
			// 将结果集写入
			while ((tmp = br.readLine()) != null) {
				isHave = true;
				//定义数组
				String[] col = new String[columnName.length];
				if(first){
					first=false;
					// 创建名为sheetName的工作簿
					sheet = workbook.createSheet("Sheet" + (sheetNo + 1), sheetNo); 
					// 首先将列名写入
					writeColName(sheet, widths, columnName, 0); 
				}else{
					//每行的信息存入数组
					col= tmp.split(separators);
					// 写入Excel
					writeCol(sheet, col, rowNum++, format);
					if (rowNum > 10000) {
						rowNum = 1;
						sheetNo++;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception", e);
			throw new YdpxRuntimeException("下载文件出错请查看日志");
		} finally {
			try {
				// 关闭
				if (isHave) {
					workbook.write();
				}

				workbook.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * EXCEL写入列名
	 * 
	 * @param sheet      sheet页
	 * @param widths     长度（int数组)
	 * @param col        列名
	 * @param rowNum     行数
	 */
	private static void writeColName(WritableSheet sheet, int[] widths, String[] col, int rowNum)
			throws RowsExceededException, WriteException {
		// 获取集合大小
		int size = col.length; 
		// 设置字体样式
		WritableCellFormat format = new WritableCellFormat();
		format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
		format.setAlignment(jxl.format.Alignment.CENTRE);
		format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, jxl.format.Colour.DARK_BLUE);
		format.setBackground(jxl.format.Colour.ICE_BLUE);
		// 写入每一列
		for (int i = 0; i < size; i++) { 
			Label label = new Label(i, rowNum, col[i] + "", format);
			sheet.addCell(label);
			sheet.setColumnView(i, widths[i]);
		}
	}
	
	/**
	 * EXCEL写入列
	 * 
	 * @param sheet      sheet页
	 * @param col        列名
	 * @param rowNum     行数
	 * @param format     字体样式
	 */
	private static void writeCol(WritableSheet sheet, String[] col, int rowNum, WritableCellFormat format) throws RowsExceededException, WriteException {
		 // 获取集合大小
		int size = col.length;
		// 写入每一列
		for (int i = 0; i < size; i++) { 
			Label label = null;
			label = new Label(i, rowNum, col[i] + "");
			sheet.addCell(label);
		}
	}
	
	public static String genterFileHttpUrl(File file,int expire){
		String key="file-"+UUID.randomUUID().toString();
		YDMemcachedManager sm = YDMemcachedManager.newInstance();
		sm.set(key, file.getAbsolutePath(), expire);
		return "/ishutil/downfileByKey/"+key;
	}
}
