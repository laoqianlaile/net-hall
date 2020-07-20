package com.yd.ish.biz.comp.ln;

import com.yd.biz.exception.TransOtherException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.FileUtil;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.org.util.CommonErrorCode;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.ReadProperty;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 名称：CompDKSQ14
 * <p>功能：计算最高贷款额度年限<br>
 * @brief 计算最高贷款额度年限
 * @author 柏慧敏
 * @version 0.1 2019年6月25日 柏慧敏创建
 * @note
 */
@Component("CompDKSQ14")
public class CompDKSQ14 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDKSQ14.class);
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
	DP077Service dp077Service;

	@Override
	public int execute() {

		/*入口参数赋值开始*/
		int instance=getInt("instance");//实例号
		String operid=getString("_OPERID");//柜员号
    	/*入口参数赋值结束*/


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
		List<DP077> list = dp077Service.selectByCause(dp077);
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
						+ file_separators + "cdgx" + file_separators + "grzh" + file_separators + "grzhzt"
						+ file_separators + "grjcjs" + file_separators + "yjce" + file_separators + "grzhye");
				fw.write(System.lineSeparator());
				// 循环写入文件信息
				for (int i = 0; i < list.size(); i++) {
					fw.write(
							list.get(i).getAccname1() + file_separators + list.get(i).getCertitype() + file_separators
									+ list.get(i).getCertinum() + file_separators + list.get(i).getFundsouflag()
									+ file_separators + list.get(i).getProptype() + file_separators
									+ list.get(i).getAccnum1() + file_separators + list.get(i).getAccnum2()
									+ file_separators + list.get(i).getBasenum() + file_separators
									+ list.get(i).getAmt1() + file_separators + list.get(i).getAmt2());
					if (i < list.size() - 1) {
						fw.write(System.lineSeparator());
					}
					if (i % 1000 == 0)
						fw.flush();
				}
				fw.flush();
				logger.info("[-]生成上传文件结束");
			} else {
				logger.info("借款人信息查询结果为空");
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
		super.setValue("filename",filename);
		// 调用接口获取贷款详细信息
		logger.info("[+]调用接口计算最高贷款额度年限开始");
		XmlResObj data = super.sendExternal("BSP_LN_JSDKJENX_01");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}
		Map<String, Object> body = data.getBody();
		String zgdked = body.get("zgdked") == null ? "" : body.get("zgdked").toString();
		String zgdknx = body.get("zgdknx") == null ? "" : body.get("zgdknx").toString();

		logger.info("[-]调用接口计算最高贷款额度年限结束");

    	/*出口参数赋值开始*/
		setOutParam("zgdked",zgdked);//最高贷款额度
		setOutParam("zgdknx",zgdknx);//最高贷款年限
    	/*出口参数赋值结束*/

    	return 0;
   }

}
