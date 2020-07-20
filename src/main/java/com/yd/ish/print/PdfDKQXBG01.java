package com.yd.ish.print;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.yd.basic.expression.IshExpression;
import com.yd.basic.serviceimp.FlowAttachmentImp;
import com.yd.ish.common.util.QrcodeUtil;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.svrplatform.util.QrCodeUtil;
import com.yd.svrplatform.word.WordPic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yd.basic.service.IYDVoucher;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.FileUtil;
import com.yd.ish.util.MoneyUtil;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.external.EssFactory;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.ReadProperty;
import com.yd.svrplatform.word.WordEngine;
import com.yd.svrplatform.word.WordEngine2;

/**
 * 
 * 名称：PdfDKQXBG01.java
 * <p>
 * 功能：打印贷款期限变更协议 <br>
 * 
 * @author
 * @version 0.1 2018年7月6日 柏慧敏创建
 */
@Service
public class PdfDKQXBG01 implements IYDVoucher {

	@Autowired
	ParamConfigImp paramConfigImp;

	private static final Logger logger = LoggerFactory.getLogger(PdfDKQXBG01.class);
	/** 启用凭证系统标记 **/
	private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");
	/** 接口接受批量上下传文件本地临时路径 **/
	private static final String file_swap_path = ReadProperty.getString("file_swap_path");
	@Autowired
	FlowAttachmentImp flowAttachmentImp;
	@Override
	public File generateVoucher(DataPool pool, int cs) {
		File wordfile;
		logger.info("[+]贷款期限变更协议打印");
		// 不启用凭证系统
		if (!"1".equals(pzsystemFlag)) {
			// 初次打印时将凭证上数据都放到总线中，方便之后补打时从总线中获取初次打印时的业务数据
			if(cs == 1){
				XmlResObj data = EssFactory.sendExternal("BSP_LN_HQDKLL_01", MainContext.currentMainContext());
				XmlResHead head = data.getHead();
				if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
					throw new TransSingleException(head.getParticular_info());
				}
				Map<String, Object> body = data.getBody();
				String downFiveRate = body.get("downFiveRate") == null ? "" : body.get("downFiveRate").toString();
				String upFiveRate = body.get("upFiveRate") == null ? "" : body.get("upFiveRate").toString();
				pool.put("downFiveRate",downFiveRate);
				pool.put("upFiveRate",upFiveRate);
				// 定义日期格式yyyy-mm-dd
				SimpleDateFormat sdf = new SimpleDateFormat(IshConstants.GG_DATE_FORMAT);
				Date date = new Date();
				pool.put("transdate",sdf.format(date));
				String grzh = IshExpression.getRealUserExtInfo("grzh");
				pool.put("grzh",grzh);
			}

			WordEngine wordEngine = new WordEngine("LN_DKQXBG_01", pool.getString("_OPERID"));
			// // 输出打印数据
			wordEngine.setData("loancontrnum", pool.getString("jkhtbh"));
			wordEngine.setData("accname1", pool.getString("accname1"));
			wordEngine.setData("loaneecertinum", pool.getString("loaneecertinum"));
			wordEngine.setData("loanamt", MoneyUtil.addComma(pool.getString("loanamt")));
			wordEngine.setData("loanbal", MoneyUtil.addComma(pool.getString("loanbal")));
			wordEngine.setData("beginintdate", pool.getString("beginintdate"));
			wordEngine.setData("attermdate", pool.getString("attermdate"));
			wordEngine.setData("date", pool.getString("date"));
			wordEngine.setData("downFiveRate", pool.getString("downFiveRate"));
			wordEngine.setData("upFiveRate", pool.getString("upFiveRate"));
			wordEngine.setData("transdate", pool.getString("transdate"));
			// 二维码中查询条件参数赋值
			HashMap<String,Object> map = new HashMap<String,Object>();
			// 实例号
			map.put("instanceid",pool.get("_IS"));
			// 实现IYDVoucher接口的实现类名称,例如：public class PdfGRKH01 implements IYDVoucher 中的GRKH01
			map.put("filename","DKQXBG01");
			// 打印文件的类型,例如：word、excel或pdf
			map.put("filetype","pdf");
			String cxtj = JsonUtil.getJsonString(map);
			// 生成凭证方位二维码中url
			String url = QrcodeUtil.generateUrl(cxtj,pool.getString("transdate"));
			logger.debug("生成的url验证地址：" + url);
			// 生成二维码
			File file = flowAttachmentImp.genFile("0001", "jpg", "二维码");
			QrCodeUtil.generatorPR(url, file);
			WordPic wordPic = null;
			// 生成二维码图片并定义二维码大小
			try {
				wordPic = new WordPic(120, 120, WordPic.PICTURE_TYPE_JPG,
						WordPic.inputStreamToByteArray(new FileInputStream(file), true));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			wordEngine.setData("ewm", wordPic);
			wordfile = wordEngine.generateWord();
			try {
				// word凭证文件全路径
				String wordPath = wordfile.getAbsolutePath();
				// pdf凭证文件全路径
				String pdfPath = wordPath.substring(0, wordPath.lastIndexOf(".")) + ".pdf";
				// word转成pdf
				WordEngine2 doc = new WordEngine2();
				wordfile = doc.doc2PDF(wordfile, pdfPath);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("创建文件出现异常请查看日志" + e.getMessage());
			}
		} else { // 启用凭证系统
			String pdfFilename = pool.get("filename") == null ? "" : pool.get("filename").toString();
			wordfile = new File(file_swap_path + "/" + pdfFilename); // 本地临时文件
			try {
				FileUtil fileUtil = new FileUtil();
				fileUtil.downloadFile(wordfile, pdfFilename);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("创建文件出现异常请查看日志" + e.getMessage());
			}
		}
		logger.info("[-]贷款期限变更协议打印");

		return wordfile;
	}
}