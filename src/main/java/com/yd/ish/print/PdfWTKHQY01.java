package com.yd.ish.print;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yd.basic.expression.IshExpression;
import com.yd.basic.service.IYDVoucher;
import com.yd.basic.serviceimp.FlowAttachmentImp;
import com.yd.ish.common.util.FileUtil;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.ish.util.MoneyUtil;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.excel.ExcelEngine;
import com.yd.svrplatform.excel.ListExcelBatchData;
import com.yd.svrplatform.pdf.PDFEngine;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.ReadProperty;
import com.yd.workflow.util.Constants;

/**
 * 
 * 名称：PdfWTKHQY01.java
 * <p>
 * 功能：委托扣划签约打印 <br>
 * 
 * @author
 * @version 0.1 2018年8月31日 柏慧敏创建
 * @version 0.2 2018年11月9日 柏慧敏修改 首次打印后查临时表获取批量数据，然后将批量数据放到总线中，补打时从总线中获取批量数据
 */
@Service
public class PdfWTKHQY01 implements IYDVoucher {

	@Autowired
	ParamConfigImp paramConfigImp;
	@Autowired
	FlowAttachmentImp flowAttachmentImp;
	@Autowired
	DP077Service dp077service;

	private static final Logger logger = LoggerFactory.getLogger(PdfWTKHQY01.class);
	/** 启用凭证系统标记 **/
	private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");
	/** 接口接受批量上下传文件本地临时路径 **/
	private static final String file_swap_path = ReadProperty.getString("file_swap_path");

	@Override
	public File generateVoucher(DataPool pool, int cs) {
		logger.debug("[+]-----委托扣划签约打印开始");
		File excelfile;
		String instance = pool.getString(Constants._WF_IS);
		// 不启用凭证系统
		if (!"1".equals(pzsystemFlag)) {
			// 初次打印时将凭证上数据都放到总线中，方便之后补打时从总线中获取初次打印时的业务数据
			if(cs == 1){
				String xingming = IshExpression.getRealUserExtInfo("xingming");
				pool.put("xingming",xingming);
				// 定义日期格式yyyy-mm-dd
				SimpleDateFormat sdf = new SimpleDateFormat(IshConstants.GG_DATE_FORMAT);
				Date date = new Date();
				pool.put("jbrq",sdf.format(date));
			}
			ExcelEngine excelEngine = new ExcelEngine("LN_WTKHQY_01", pool.getString("_OPERID"));
			// // 输出单笔打印数据
			excelEngine.setData("jkhtbh", pool.get("jkhtbh"));
			excelEngine.setData("op", pool.get("xingming"));
			excelEngine.setData("jbrq", pool.get("jbrq"));

			// 若打印凭证中包含批量数据，需要对批量数据进行赋值 V0.2 start
			// 定义打印批量数据json格式
			String printJsonData = "";
			// 若总线中有打印批量数据，获取总线中的批量数据（补打）
			if (pool.containsKey(IshConstants._ISH_PRINTJSONDATA) && !"".equals(IshConstants._ISH_PRINTJSONDATA)) {
				printJsonData = pool.getString(IshConstants._ISH_PRINTJSONDATA);
			}
			// 若总线中没有打印需要的批量数据，取临时表数据，放入总线中（首次打印）
			else {
				// 定义打印批量数据list
				List<Map<String, Object>> printList = new ArrayList<Map<String, Object>>();
				// 根据实例号在临时表中查询个人账户设立信息
				List<DP077> wtkhlist = dp077service.selectBySlh(Integer.parseInt(instance));
				// 定义查询结果长度
				int wtkhlength = 0;
				if (wtkhlist != null) {
					wtkhlength = wtkhlist.size();
				}
				// 循环查询结果，批量数据赋值
				for (int i = 0; i < wtkhlength; i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					DP077 dp077 = wtkhlist.get(i);
					map.put("xingming", dp077.getAccname1());
					map.put("zjlx", paramConfigImp.getVal("bsp.pb.zjlx." + dp077.getCertitype().toString()));
					map.put("zjhm", dp077.getCertinum());
					map.put("qylx", paramConfigImp.getVal("bsp.ln.qylx." + dp077.getSex().toString()));
					map.put("grzhye", MoneyUtil.addComma((dp077.getAmt1().toString())));
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
				resultList.add(new String[] { object.getString("xingming"), object.getString("zjlx"),
						object.getString("zjhm"), object.getString("qylx"), object.getString("grzhye") });
			}
			// 打印批量数据赋值
			excelEngine.setBatchData(1, new ListExcelBatchData(resultList));
			// 若打印凭证中包含批量数据，需要对批量数据进行赋值 V0.2 end
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
		logger.debug("[-]-----委托扣划签约打印结束");

		return excelfile;
	}
}