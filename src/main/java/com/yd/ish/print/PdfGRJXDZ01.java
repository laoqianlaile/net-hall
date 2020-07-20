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
 * 名称：PdfGRJXDZ01.java
 * <p>
 * 功能：打印 <br>
 * 
 * @author 许永峰
 * @version 0.1 2018年7月4日 许永峰 创建
 */
@Service
public class PdfGRJXDZ01 implements IYDVoucher {

	@Autowired
	ParamConfigImp paramConfigImp;
	@Autowired
	FlowAttachmentImp flowAttachmentImp;
	private static final Logger logger = LoggerFactory.getLogger(PdfGRJXDZ01.class);
	/** 启用凭证系统标记 **/
	private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");
	/** 接口接受批量上下传文件本地临时路径 **/
	private static final String file_swap_path = ReadProperty.getString("file_swap_path");
	@Autowired
	DP077Service dp077service;

	@Override
	public File generateVoucher(DataPool pool, int cs) {
		logger.debug("[+]缴存单位下个人结息对账单打印开始");
		File excelfile;
		String instance = pool.getString(Constants._WF_IS);
		// 不启用凭证系统
		if (!"1".equals(pzsystemFlag)) {
			// 初次打印时将凭证上数据都放到总线中，方便之后补打时从总线中获取初次打印时的业务数据
			if(cs == 1) {
				// 定义日期格式yyyy-mm-dd
				SimpleDateFormat sdf = new SimpleDateFormat(IshConstants.GG_DATE_FORMAT);
				// 交易日期
				String TranDate = sdf.format(new Date());
				pool.put("TranDate",TranDate);
			}
			ExcelEngine excelEngine = new ExcelEngine("DP_GRJXDZ_01", pool.getString("_OPERID"));
			// 输出单笔打印数据
			// 单位名称
			excelEngine.setData("dwzh", pool.getString("_ORGID"));
			// 单位账号
			excelEngine.setData("dwmc", pool.getString("_ORGNAME"));
			// 打印日期
			excelEngine.setData("transdate", pool.get("TranDate"));
			// 起息日期
			excelEngine.setData("qxrq", pool.getString("qxrq"));
			// 结息日期
			excelEngine.setData("jxrq", pool.getString("jxrq"));
			// 小计人数
			excelEngine.setData("xjrs", pool.getString("xjrs"));
			// 小计定期本金
			excelEngine.setData("xjdqbj", MoneyUtil.addComma(pool.getString("xjdqbj")));
			// 小计活期本金
			excelEngine.setData("xjhqbj", MoneyUtil.addComma(pool.getString("xjhqbj")));
			// 小计总本金
			excelEngine.setData("xjzbj", MoneyUtil.addComma(pool.getString("xjzbj")));
			// 小计定期利息
			excelEngine.setData("xjdqlx", MoneyUtil.addComma(pool.getString("xjdqlx")));
			// 小计活期利息
			excelEngine.setData("xjhqlx", MoneyUtil.addComma(pool.getString("xjhqlx")));
			// 小计总利息
			excelEngine.setData("xjzlx", MoneyUtil.addComma(pool.getString("xjzlx")));
			// 小计余额
			excelEngine.setData("xjye", MoneyUtil.addComma(pool.getString("xjye")));
			String printJsonData = "";
			// 若总线中有打印批量数据，获取总线中的批量数据（补打）
			if (pool.containsKey(IshConstants._ISH_PRINTJSONDATA) && !"".equals(IshConstants._ISH_PRINTJSONDATA)) {
				printJsonData = pool.getString(IshConstants._ISH_PRINTJSONDATA);
			} else {
				// 定义打印批量数据list
				List<Map<String, Object>> printList = new ArrayList<Map<String, Object>>();
				// 根据实例号在临时表中查询个人账户设立信息
				List<DP077> grjxdzlist = dp077service.selectBySlh(Integer.parseInt(instance));
				// 定义查询结果长度
				int grjxdzlength = 0;

				if (grjxdzlist != null) {
					grjxdzlength = grjxdzlist.size();
				}
				// 循环查询结果，批量数据赋值
				for (int i = 0; i < grjxdzlength; i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					DP077 dp077 = grjxdzlist.get(i);
					// map.put("seqno", i + 1 + "");
					map.put("grzh", dp077.getAccname2().toString());
					map.put("xm", dp077.getAccname1().toString());
					map.put("dqbj", MoneyUtil.addComma(dp077.getAmt1().toString()));
					map.put("hqbj", MoneyUtil.addComma(dp077.getAmt2().toString()));
					map.put("zbj", MoneyUtil.addComma(dp077.getBasenum().toString()));
					map.put("dqlx", MoneyUtil.addComma(dp077.getPayvouamt().toString()));
					map.put("hqlx", MoneyUtil.addComma(dp077.getJtysr().toString()));
					map.put("zlx", MoneyUtil.addComma(dp077.getFreeuse2().toString()));
					map.put("ye", MoneyUtil.addComma(dp077.getUnitprop().toString()));

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
				resultList
						.add(new String[] { object.getString("grzh"), object.getString("xm"), object.getString("dqbj"),
								object.getString("hqbj"), object.getString("zbj"), object.getString("dqlx"),
								object.getString("hqlx"), object.getString("zlx"), object.getString("ye") });
			}
			// 打印批量数据赋值
			excelEngine.setBatchData(1, new ListExcelBatchData(resultList));
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
			String pdfFilename = pool.get("pzfilename") == null ? "" : pool.get("pzfilename").toString();
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
		logger.debug("[-]缴存单位下个人结息对账单打印结束");

		return excelfile;
	}

}