package com.yd.ish.print;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.impl.xb.xmlschema.SpaceAttribute;
import org.json.JSONObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFldChar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHdrFtr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yd.basic.service.IYDVoucher;
import com.yd.basic.serviceimp.FlowAttachmentImp;
import com.yd.ish.common.util.YyywUtil;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.QrCodeUtil;
import com.yd.svrplatform.word.IWordEvent;
import com.yd.svrplatform.word.WordEngine;
import com.yd.svrplatform.word.WordPic;

/**
 * 
 * 名称：WordYBTJSL01.java
 * <p>
 * 功能：异步提交示例打印 <br>
 * 
 * @author 张洪超
 * @version 0.1 2018年1月5日 张洪超创建
 */
@Service
public class WordYBTJSL01 implements IYDVoucher {

	@Autowired
	ParamConfigImp paramConfigImp;
	@Autowired
	FlowAttachmentImp flowAttachmentImp;
	private static final Logger logger = LoggerFactory.getLogger(WordYBTJSL01.class);

	@Override
	public File generateVoucher(DataPool pool, int cs) {
		logger.debug("[+]-----异步提交示例打印");
		WordEngine wordEngine = new WordEngine("YBTJ_PZSL_01", pool.getString("_OPERID"));
		// 输出打印数据
		wordEngine.setData("pz1", pool.getString("pz1"));
		wordEngine.setData("pz2", pool.getString("pz2"));
		wordEngine.setData("qssj", pool.getString("qssj"));
		wordEngine.setData("jzsj", pool.getString("jzsj"));
		wordEngine.setData("pzsj", YyywUtil.getDate());
		// 设置二维码图片
		File file = null;
		try {
			JSONObject json = new JSONObject();
			json.put("xh", "123");
			file = flowAttachmentImp.genFile("0001", "jpg", "二维码"); 
			QrCodeUtil.generatorPR(json.toString(), file);
			WordPic wordPic = new WordPic(300, 300, WordPic.PICTURE_TYPE_JPG, 
					WordPic.inputStreamToByteArray(new FileInputStream(file), true));
			wordEngine.setData("ewm", wordPic);
		} catch (Exception e) {
			logger.info("生成二维码失败：" + e.getMessage());
    		e.printStackTrace();
		}
		wordEngine.setWordEvent(new IWordEvent() {
			public void run(XWPFDocument document) {
				try {
					// 设置字体	
					// simpleNumberFooter(document);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		File wordfile = wordEngine.generateWord();
		logger.debug("[-]-----异步提交示例打印");

		return wordfile;
	}

	public String getYdpxPath() {
		return "common/wordVoucher.ydpx";
	}

	public static void simpleNumberFooter(XWPFDocument document) throws Exception {
		CTP ctp = CTP.Factory.newInstance();
		XWPFParagraph codePara = new XWPFParagraph(ctp, document);
		XWPFRun r1 = codePara.createRun();
		r1.setText("第");
		r1.setFontSize(11);
		CTRPr rpr = r1.getCTR().isSetRPr() ? r1.getCTR().getRPr() : r1.getCTR().addNewRPr();
		CTFonts fonts = rpr.isSetRFonts() ? rpr.getRFonts() : rpr.addNewRFonts();
		fonts.setAscii("宋体");
		fonts.setEastAsia("宋体");
		fonts.setHAnsi("宋体");

		r1 = codePara.createRun();
		CTFldChar fldChar = r1.getCTR().addNewFldChar();
		fldChar.setFldCharType(STFldCharType.Enum.forString("begin"));

		r1 = codePara.createRun();
		CTText ctText = r1.getCTR().addNewInstrText();
		ctText.setStringValue("PAGE \\* MERGEFORMAT");
		ctText.setSpace(SpaceAttribute.Space.Enum.forString("preserve"));
		r1.setFontSize(11);
		rpr = r1.getCTR().isSetRPr() ? r1.getCTR().getRPr() : r1.getCTR().addNewRPr();
		fonts = rpr.isSetRFonts() ? rpr.getRFonts() : rpr.addNewRFonts();
		fonts.setAscii("宋体");
		fonts.setEastAsia("宋体");
		fonts.setHAnsi("宋体");

		fldChar = r1.getCTR().addNewFldChar();
		fldChar.setFldCharType(STFldCharType.Enum.forString("end"));

		r1 = codePara.createRun();
		r1.setText("页 总共");
		r1.setFontSize(11);
		rpr = r1.getCTR().isSetRPr() ? r1.getCTR().getRPr() : r1.getCTR().addNewRPr();
		fonts = rpr.isSetRFonts() ? rpr.getRFonts() : rpr.addNewRFonts();
		fonts.setAscii("宋体");
		fonts.setEastAsia("宋体");
		fonts.setHAnsi("宋体");

		r1 = codePara.createRun();
		fldChar = r1.getCTR().addNewFldChar();
		fldChar.setFldCharType(STFldCharType.Enum.forString("begin"));

		r1 = codePara.createRun();
		ctText = r1.getCTR().addNewInstrText();
		ctText.setStringValue("NUMPAGES \\* MERGEFORMAT ");
		ctText.setSpace(SpaceAttribute.Space.Enum.forString("preserve"));
		r1.setFontSize(11);
		rpr = r1.getCTR().isSetRPr() ? r1.getCTR().getRPr() : r1.getCTR().addNewRPr();
		fonts = rpr.isSetRFonts() ? rpr.getRFonts() : rpr.addNewRFonts();
		fonts.setAscii("宋体");
		fonts.setEastAsia("宋体");
		fonts.setHAnsi("宋体");

		fldChar = r1.getCTR().addNewFldChar();
		fldChar.setFldCharType(STFldCharType.Enum.forString("end"));

		r1 = codePara.createRun();
		r1.setText("页");
		r1.setFontSize(11);
		rpr = r1.getCTR().isSetRPr() ? r1.getCTR().getRPr() : r1.getCTR().addNewRPr();
		fonts = rpr.isSetRFonts() ? rpr.getRFonts() : rpr.addNewRFonts();
		fonts.setAscii("宋体");
		fonts.setEastAsia("宋体");
		fonts.setHAnsi("宋体");

		codePara.setAlignment(ParagraphAlignment.CENTER);
		codePara.setVerticalAlignment(TextAlignment.CENTER);
		codePara.setBorderTop(Borders.THICK);
		XWPFParagraph[] newparagraphs = new XWPFParagraph[1];
		newparagraphs[0] = codePara;
		CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
		XWPFHeaderFooterPolicy headerFooterPolicy = new XWPFHeaderFooterPolicy(document, sectPr);
		headerFooterPolicy.createFooter(STHdrFtr.DEFAULT, newparagraphs);
	}
}