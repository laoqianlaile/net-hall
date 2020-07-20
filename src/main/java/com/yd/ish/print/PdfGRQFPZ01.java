package com.yd.ish.print;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.yd.basic.expression.IshExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.yd.svrplatform.util.Datelet;
import com.yd.svrplatform.util.ReadProperty;
import com.yd.workflow.util.Constants;

/**
 * 
 * 名称：PdfGRQFPZ01.java
 * <p>
 * 功能：个人启封凭证打印 <br>
 * 
 * @author 王赫
 * @version 0.1 2018年9月28日 王赫创建
 */
@Service
public class PdfGRQFPZ01 implements IYDVoucher {

	@Autowired
	ParamConfigImp paramConfigImp;
	@Autowired
	FlowAttachmentImp flowAttachmentImp;
	private static final Logger logger = LoggerFactory.getLogger(PdfGRQFPZ01.class);
	/** 启用凭证系统标记 **/
	private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");
	/** 接口接受批量上下传文件本地临时路径 **/
	private static final String file_swap_path = ReadProperty.getString("file_swap_path");
	@Autowired
	DP077Service dp077service;

	@Override
	public File generateVoucher(DataPool pool, int cs) {
		logger.info("[+]个人封存凭证打印开始");
		File excelfile;
		// 实例号
		String instance = pool.getString(Constants._WF_IS);
		// 不启用凭证系统
		if (!"1".equals(pzsystemFlag)) {
			// 初次打印时将凭证上数据都放到总线中，方便之后补打时从总线中获取初次打印时的业务数据
			if(cs == 1){
				String dwmc = IshExpression.getRealUserExtInfo("dwmc");
				String dwzh = IshExpression.getRealUserExtInfo("dwdjh");
				pool.put("dwmc",dwmc);
				pool.put("dwzh",dwzh);
				pool.put("transdate",Datelet.getCurrentDateString());
			}
			ExcelEngine excelEngine = new ExcelEngine("DP_GRZHBG_01", pool.getString("_OPERID"));
			// 输出打印数据
			excelEngine.setData("lsh", pool.getString("lsh"));
			excelEngine.setData("transdate", pool.get("transdate"));
			excelEngine.setData("dwmc", pool.get("dwmc"));
			excelEngine.setData("dwzh", pool.get("dwzh"));
			// excelEngine.setData("bgrs", pool.getString("total"));
			excelEngine.setData("jbjg", pool.getString("jbjg"));
			List<String[]> list = new ArrayList<String[]>();

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
				DP077 dp077tj = new DP077();
				dp077tj.setInstance(Integer.parseInt(instance));
				// 83-启封
				dp077tj.setDpbusitype("83");
				// 根据实例号在临时表中查询个人启封信息
				List<DP077> grqflist = dp077service.selectByCause(dp077tj);
				// 循环查询结果，打印批量数据list赋值
				if (grqflist != null && grqflist.size() > 0) {
					for (int i = 0; i < grqflist.size(); i++) {
						HashMap<String, Object> map = new HashMap<String, Object>();
						DP077 dp077 = grqflist.get(i);
						map.put("grzh", dp077.getAccnum1());
						map.put("xingming", dp077.getAccname1());
						map.put("zjlx", dp077.getCertitype());
						map.put("zjhm", dp077.getCertinum());
						map.put("grjcjs", dp077.getBasenum());
						printList.add(map);
					}
				}
				// 将打印批量数据放到总线中
				printJsonData = JSONUtils.toJSONString(printList);
				pool.put(IshConstants._ISH_PRINTJSONDATA, printJsonData);
			}

			JSONArray jsonArrayObject = JSONObject.parseArray(printJsonData);
			Iterator iterator = jsonArrayObject.iterator();
			Integer index = 0;
			while (iterator.hasNext()) {
				index++;
				JSONObject object = (JSONObject) iterator.next();
				String zjlx = object.getString("zjlx");
				String _zjlx = paramConfigImp.getVal("bsp.pb.certitype." + zjlx);
				list.add(
						new String[] { index.toString(), "个人启封", object.getString("grzh"), object.getString("xingming"),
								_zjlx, object.getString("zjhm"), MoneyUtil.addComma(object.getString("grjcjs")) });
			}
			excelEngine.setData("bgrs", index);
			excelEngine.setBatchData(1, new ListExcelBatchData(list));
			excelfile = excelEngine.createExcel();
			try{
				// excel转成pdf
				PDFEngine engine = new PDFEngine();
				// excel凭证文件全路径
				String excelPath = excelfile.getAbsolutePath();
				// pdf凭证文件全路径
				String pdfPath = excelPath.substring(0, excelPath.lastIndexOf(".")) + ".pdf";
				// excel转成pdf
				engine.convertExcel2Pdf(excelPath, pdfPath);
				excelfile = new File(pdfPath);
			}catch(Exception e){
				e.printStackTrace();
			}
		} else { // 启用凭证系统
			String pdfFilename = pool.get("filename") == null ? "" : pool.get("filename").toString();
			excelfile = new File(file_swap_path + "/" + pdfFilename); // 本地临时文件
			try{
				FileUtil fileUtil = new FileUtil();
				fileUtil.downloadFile(excelfile, pdfFilename);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		// 打印后删除临时表的数据
		dp077service.deleteBySlh(Integer.parseInt(instance));
		logger.info("[-]个人启封凭证打印结束");
		return excelfile;
	}
}