package com.yd.ish.print;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.yd.ish.common.util.QrcodeUtil;
import com.yd.svrplatform.excel.ExcelPic;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.svrplatform.util.QrCodeUtil;
import com.yd.svrplatform.word.WordPic;
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
 * 名称：PdfGRKH01.java
 * <p>
 * 功能：个人账户设立打印 <br>
 * 
 * @author
 * @version 0.1 2018年1月5日 张洪超创建 version 0.2 2018年11月8日 柏慧敏修改
 *          首次打印后查临时表获取批量数据，然后将批量数据放到总线中，补打时从总线中获取批量数据
 */
@Service
public class PdfGRKH01 implements IYDVoucher {

	@Autowired
	ParamConfigImp paramConfigImp;
	@Autowired
	FlowAttachmentImp flowAttachmentImp;
	private static final Logger logger = LoggerFactory.getLogger(PdfGRKH01.class);
	/** 启用凭证系统标记 **/
	private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");
	/** 接口接受批量上下传文件本地临时路径 **/
	private static final String file_swap_path = ReadProperty.getString("file_swap_path");
	@Autowired
	DP077Service dp077service;

	@Override
	public File generateVoucher(DataPool pool, int cs) {
		logger.info("[+]个人账户设立打印开始");
		File excelfile;
		// 实例号
		String instance = pool.getString(Constants._WF_IS);
		// 不启用凭证系统
		if (!"1".equals(pzsystemFlag)) {
			// 初次打印时将凭证上数据都放到总线中，方便之后补打时从总线中获取初次打印时的业务数据
			if(cs == 1){
				String dwmc = IshExpression.getRealUserExtInfo("dwmc");
				String dwdjh = IshExpression.getRealUserExtInfo("dwdjh");
				String jbrxm = IshExpression.getRealUserExtInfo("jbrxm");
				pool.put("dwmc",dwmc);
				pool.put("dwdjh",dwdjh);
				pool.put("jbrxm",jbrxm);
				// 定义日期格式yyyy-mm-dd
				SimpleDateFormat sdf = new SimpleDateFormat(IshConstants.GG_DATE_FORMAT);
				Date date = new Date();
				pool.put("transdate",sdf.format(date));
			}
			ExcelEngine excelEngine = new ExcelEngine("DP_GRKH_01", pool.getString("_OPERID"));
			// 输出单笔打印数据
			excelEngine.setData("unitaccname", pool.get("dwmc"));
			excelEngine.setData("unitaccnum", pool.get("dwdjh"));
			excelEngine.setData("op", pool.get("jbrxm"));
			excelEngine.setData("TranSeq", instance);
			excelEngine.setData("transdate", pool.get("transdate"));

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
				DP077 dp077tj = new DP077();
				dp077tj.setInstance(Integer.parseInt(instance));
				// 58-个人开户
				dp077tj.setDpbusitype("58");
				// 根据实例号在临时表中查询个人账户设立信息
				List<DP077> grkhlist = dp077service.selectByCause(dp077tj);
				// 定义查询结果长度
				int grkhlength = 0;
				if (grkhlist != null) {
					grkhlength = grkhlist.size();
				}
				// 循环查询结果，打印批量数据list赋值
				for (int i = 0; i < grkhlength; i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					DP077 dp077 = grkhlist.get(i);
					map.put("seqno", i + 1 + "");
					map.put("xingming", dp077.getAccname1());
					map.put("zjhm", dp077.getCertinum());
					map.put("jcjs", MoneyUtil.addComma(dp077.getBasenum()==null?"":dp077.getBasenum().toString()));
					map.put("dwjcbl", pool.getString("dwjcbl"));
					map.put("grjcbl", pool.getString("grjcbl"));
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
				resultList.add(new String[] { object.getString("seqno"), object.getString("xingming"),
						object.getString("zjhm"), object.getString("jcjs"), object.getString("dwjcbl"),
						object.getString("grjcbl") });
			}
			// 打印批量数据赋值
			excelEngine.setBatchData(1, new ListExcelBatchData(resultList));
			// 若打印凭证中包含批量数据，需要对批量数据进行赋值 V0.2 end
			// 二维码中查询条件参数赋值
			HashMap<String,Object> map = new HashMap<String,Object>();
			// 实例号
			map.put("instanceid",instance);
			// 实现IYDVoucher接口的实现类名称,例如：public class PdfGRKH01 implements IYDVoucher 中的GRKH01
			map.put("filename","GRKH01");
			// 打印文件的类型,例如：word、excel或pdf
			map.put("filetype","pdf");
			String cxtj = JsonUtil.getJsonString(map);
			// 生成凭证方位二维码中url
			String url = QrcodeUtil.generateUrl(cxtj,pool.getString("transdate"));
			logger.info("生成的url验证地址：" + url);
			// 生成二维码
			File file = flowAttachmentImp.genFile("0001", "jpg", "二维码");
			QrCodeUtil.generatorPR(url, file);
			ExcelPic excelPic = null;
			// 生成二维码图片并定义二维码在凭证中位置
			try {
				excelPic = new ExcelPic(0,0,100,100,(short)0,0,(short)1, 1, ExcelPic.PICTURE_TYPE_JPEG,
						ExcelPic.inputStreamToByteArray(new FileInputStream(file), true));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			excelEngine.setData("ewm", excelPic);
			// 生成打印文件
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
		logger.info("[-]个人账户设立打印结束");
		return excelfile;
	}
}