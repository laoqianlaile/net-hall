package com.yd.ish.print;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yd.basic.expression.IshExpression;
import com.yd.basic.service.IYDVoucher;
import com.yd.ish.common.util.FileUtil;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.ish.util.MoneyUtil;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.excel.ExcelEngine;
import com.yd.svrplatform.excel.ListExcelBatchData;
import com.yd.svrplatform.pdf.PDFEngine;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.ReadProperty;
import com.yd.workflow.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * 名称：PdfJCJSTZ01.java
 * <p>
 * 功能：打印 <br>
 * 
 * @author 许永峰
 * @version 0.1 2018年7月4日 许永峰 创建
 */
@Service
public class PdfJCJSTZ01 implements IYDVoucher {

	private static final Logger logger = LoggerFactory.getLogger(PdfJCJSTZ01.class);
	/** 启用凭证系统标记 **/
	private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");
	/** 接口接受批量上下传文件本地临时路径 **/
	private static final String file_swap_path = ReadProperty.getString("file_swap_path");
	@Autowired
	DP077Service dp077service;

	@Override
	public File generateVoucher(DataPool pool, int cs) {
		logger.debug("[+]缴存基数调整信息打印开始");
		File excelfile;
		String instance = pool.getString(Constants._WF_IS);
		// 不启用凭证系统
		if (!"1".equals(pzsystemFlag)) {
			// 初次打印时将凭证上数据都放到总线中，方便之后补打时从总线中获取初次打印时的业务数据
			if(cs == 1){
				String dwmc = IshExpression.getRealUserExtInfo("dwmc");
				String dwzh = IshExpression.getRealUserExtInfo("dwdjh");
				// 定义日期格式yyyy-mm-dd
				SimpleDateFormat sdf = new SimpleDateFormat(IshConstants.GG_DATE_FORMAT);
				Date date = new Date();
				pool.put("dwmc",dwmc);
				pool.put("dwzh",dwzh);
				pool.put("transdate",sdf.format(date));
			}
			ExcelEngine excelEngine = new ExcelEngine("DP_JCJSTZ_01", pool.getString("_OPERID"));
			// 输出单笔打印数据
			excelEngine.setData("unitaccname", pool.get("dwmc"));
			excelEngine.setData("unitaccnum", pool.get("dwzh"));
			excelEngine.setData("transdate", pool.get("transdate"));
			String printJsonData = "";
			// 若总线中有打印批量数据，获取总线中的批量数据（补打）
			if (pool.containsKey(IshConstants._ISH_PRINTJSONDATA) && !"".equals(IshConstants._ISH_PRINTJSONDATA)) {
				printJsonData = pool.getString(IshConstants._ISH_PRINTJSONDATA);
			} else {
				// 定义打印批量数据list
				List<Map<String, Object>> printList = new ArrayList<Map<String, Object>>();
				DP077 dp077tj = new DP077();
				dp077tj.setInstance(Integer.parseInt(instance));
				// 00-缴存基数调整批量信息
				dp077tj.setDpbusitype("00");
				// 根据实例号在临时表中查询个人缴存基数调整信息
				List<DP077> jstzlist = dp077service.selectByCause(dp077tj);
				// 定义查询结果长度
				int jstzlength = 0;

				if (jstzlist != null) {
					jstzlength = jstzlist.size();
				}
				// 循环查询结果，批量数据赋值
				for (int i = 0; i < jstzlength; i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					DP077 dp077 = jstzlist.get(i);
					map.put("seqno", i + 1 + "");
					map.put("grzh", dp077.getUnitaccnum1().toString());
					map.put("grxm", dp077.getAccname1().toString());
					map.put("bgqjcjs", MoneyUtil.addComma(dp077.getAmt1().toString()));
					map.put("bghjcjs", MoneyUtil.addComma(dp077.getBasenum().toString()));
					printList.add(map);
				}
				// 将打印批量数据放到总线中
				printJsonData = JSONUtils.toJSONString(printList);
				pool.put(IshConstants._ISH_PRINTJSONDATA, printJsonData);
			}
			JSONArray jsonArrayObject = JSONObject.parseArray(printJsonData);
			Iterator iterator = jsonArrayObject.iterator();
			List<String[]> resultList = new ArrayList<String[]>();
			// 循环批量数据，给准备打印的批量数据resultList赋值。
			while (iterator.hasNext()) {
				JSONObject object = (JSONObject) iterator.next();
				resultList.add(new String[] { object.getString("seqno"), object.getString("grzh"),
						object.getString("grxm"), object.getString("bgqjcjs"), object.getString("bghjcjs") });
			}
			// 打印批量数据赋值
			excelEngine.setBatchData(1, new ListExcelBatchData(resultList));
			// 生成打印文件
			excelfile = excelEngine.createExcel();
			try {
				// excel转成pdf
				PDFEngine engine = new PDFEngine();
				// excel凭证文件全路径
				String excelPath = excelfile.getAbsolutePath();
				// pdf凭证文件全路径
				String pdfPath = excelPath.substring(0, excelPath.lastIndexOf(".")) + ".pdf";
				// excel转成pdf
				engine.convertExcel2Pdf(excelPath, pdfPath);
				excelfile = new File(pdfPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else { // 启用凭证系统
			String pdfFilename = pool.get("filename") == null ? "" : pool.get("filename").toString();
			excelfile = new File(file_swap_path + "/" + pdfFilename); // 本地临时文件
			try {
				FileUtil fileUtil = new FileUtil();
				fileUtil.downloadFile(excelfile, pdfFilename);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 打印后删除临时表的数据
		dp077service.deleteBySlh(Integer.parseInt(instance));
		logger.debug("[-]缴存基数调整信息打印结束");

		return excelfile;
	}

}