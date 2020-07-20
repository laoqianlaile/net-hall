package com.yd.ish.biz.comp.common.plyc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.BatchExceptionBean;
import com.yd.biz.exception.TransBatchException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.FileUtil;
import com.yd.ish.common.util.StringUtils;
import com.yd.org.util.CommonErrorCode;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.svrplatform.util.ReadProperty;

/**
 * 名称：CompPLYC01.java
 * <p>
 * 功能：批量异常接口 <br>
 * 
 * @author 张洪超
 * @version 0.1 2018年2月6日 张洪超创建
 */
@Component("CompPLYC01")
public class CompPLYC01 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompPLYC01.class);
	/** 接口文件路径 **/
	private static final String file_swap_path = ReadProperty.getString("file_swap_path");
	/** 数据分割符 */
	private static final String file_separators = ReadProperty.getString("file_separators");
	@Override
	public int execute() {

		/* 入口参数赋值开始 */
		String details = getString("details");// 业务明细
		/* 入口参数赋值结束 */
		
		MainContext content = MainContext.currentMainContext();
		DataPool pool = content.getDataPool();

		// 获取接口接收的文件名(IN_FILE 为接口服务端接收的文件名，多个用,分割)
		String files = pool.getString("IN_FILE");// 文件明细
		/**
		 * 该接口为示例接口，支持接收上送批量明细信息details，还支持批量文件
		 * 1、如果为批量明细信息details则直接解析数据，处理过程省略了，直接返回了批量的错误（支持details数据量不大的情况）
		 * 2、如果为批量文件，则示例为：接收文件，解析文件，处理过程省略，将批量的错误信息组装成文件上传至FTP，
		 * 	   返回特定的错误码让客户端读取文件，文件名固定定义为上传的文件名+.err。如/ish/file/-123123.txt.err
		 */
		List<BatchExceptionBean> beans = new ArrayList<BatchExceptionBean>();
		int count = 1;
		// 1、业务明细不为空
		if (StringUtils.isNotBlank(details) && !"[]".equals(details)) {
			List<Map> detail = JsonUtil.getJsonList(details, Map.class);
			for (Map map : detail) {
				/**
				 * BatchExceptionBean bean = new BatchExceptionBean(seqno, item, content, reason, 
				 * 				infotype, locationKey, key1, key2, key3, key4, key5);
				 * 
				 * seqno	int	记录号
				 * item	String	错误字段名
				 * content	String	错误值
				 * reason	String	错误原因
				 * infotype	String	信息类型 0-错误信息 1-提示信息
				 * locationKey	String	定位键字，多个以半角逗号分隔
				 * datalistId	String	显示批量错误的动态列表ID，可省略
				 * key1	String	定位键字1值，与定位键字对应
				 * key2	String	定位键字2值，与定位键字对应
				 * key3	String	定位键字3值，与定位键字对应
				 * key4	String	定位键字4值，与定位键字对应
				 * key5	String	定位键字5值，与定位键字对应
				 * 
				 * 	item 实际查询动态列表中错误列对应的表字段名
				 * 	locationKey
				 * 1、普通批量错误时，此项为出错字段的id
				 * 2、实际查询动态列表中可定位到一条记录的查询条件字段，最多5项，与定位键值对应。
				 * 例如：表查询sql为"select aa,bb,cc,dd from test",aa+bb可定位到查询的一条记录，
				 * locationKey的值就需要赋为 aa,bb，而key1的值是aa字段的值，key2的值为bb字段的值。
				 * 
				 */
				BatchExceptionBean bean = new BatchExceptionBean(count, "xingming", map.get("xingming")+"", "原因：不存在此姓名", "1",
						"xh","sllist",map.get("xh")+"", "","","","");
				beans.add(bean);
				count++;
			}
			throw new TransBatchException(beans);
		}
		///2、文件明细不为空
		String returnCode = "";
		String message = "";
		// 返回的失败文件名，默认为空
		String refilepath = "";
		if (StringUtils.isNotBlank(files)) {
			try {
				String[] fileArry = files.split(",");
				for(String fileName:fileArry){
					// 取从FTP取下的文件
					String rePath = file_swap_path;
					if (!rePath.endsWith("/")) {
						rePath += "/";
					}
					// 取文件
					File file = new File(rePath+fileName);
					// 文件不存在报错
					if (!file.exists() || !file.isFile() || file.length() <= 0) {
						logger.info("[-]文件下载失败。文件名：" + file.getName() );
						throw new TransSingleException(CommonErrorCode.ERROR_WJXZSB, "");
					}
					if (file.exists() && file.isFile() && file.length() > 0) {
						// 读取文件，处理文件数据
						List<String> fileList = FileUtils.readLines(file, "utf-8");
						
						// TODO 此处可以取到文件的数据进行业务处理
						
						// 示例：将错误的信息再写入文件
						// 批量错误文件默认为原文件名+.err
						File refile = new File(file.getPath()+".err");
						FileWriter fw = new FileWriter(refile.getAbsolutePath(), false);
						// 写入返回错误文件的第一行
						fw.write("count"+file_separators+"item"+file_separators+"content"+file_separators+"reason");
						// 换行符
						fw.write("\n");
						// 示例内容默认将读取的文件内容取第一列,xingming返回错误写入文件(也可以示例读入文件内容)
						String firstLine = fileList.get(0);
						System.out.println("第一行数据:"+firstLine);
						for (int i = 1; i < fileList.size(); i++) {
							String strLine = fileList.get(i);
							//此处可以取到文件的数据进行业务处理
							String errContent = strLine.split(file_separators)[0];
							fw.write(i+file_separators+"姓名"+file_separators+"姓名错误"+file_separators+"姓名["+errContent+"]不存在");
							// 换行符
							fw.write("\n");
						}
						fw.close();
						//1、 此处可以调用FTP上传文件
//						FileUtil fileUtil = new FileUtil();
//						String relativePath = refile.getAbsolutePath().replace((new File(file_swap_path)).getAbsolutePath() + "/", "")
//								.replace((new File(file_swap_path)).getAbsolutePath() + "\\", "").replace('\\', '/');
//						fileUtil.uploadFile(refile, relativePath);
//						returnCode = "7777";
//						message = "批量错误";
						//2、 接口直接通过平台返回文件，文件路径放入报文头file(多个问津用，号隔开)
						refilepath = refile.getAbsolutePath();
					} else {
						logger.error("读取文件失败，文件名：" + file.getName());
						throw new TransSingleException(CommonErrorCode.ERROR_WJDQSB, "");
					}
					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/* 出口参数赋值开始 */
		setOutParam("file", refilepath);// 返回文件
		setOutParam("returnCode", returnCode);//接口返回错误码，批量错误文件错误码默认定义为7777（此处实际开发中应该和服务方协定），这样在返回后客户端默认读取文件
		setOutParam("message", message);//接口返回错误信息
		/* 出口参数赋值结束 */

		return 0;
	}

}
