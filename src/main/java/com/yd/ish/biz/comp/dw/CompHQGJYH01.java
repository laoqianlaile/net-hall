package com.yd.ish.biz.comp.dw;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransOtherException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.GetDownFileMap;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.org.util.CommonErrorCode;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.dataswap.file.FileSwap;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.comm_mdl.log.YDLogger;
import com.yd.svrplatform.util.ReadProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 /**
 * 名称：CompHQGJYH01
 * <p>功能：获取归集银行信息<br>
 * @brief 功能简述 TODO
 * @author 
 * @version 版本号	修改人	修改时间	地点	原因	TODO
 * @note
 */
@Component("CompHQGJYH01")
public class CompHQGJYH01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompHQGJYH01.class);
	/** 数据分割符 */
	private static final String separators = ReadProperty.getString("file_separators");
	/** 读核心返回文件编码格式 */
	private static final String encoding = ReadProperty.getString("readfile_encoding_bsp");
	/** 数据库类型 */
	private static final String DB_TYPE = ReadProperty.getString("dbType");

	@Autowired
	GetDownFileMap getDownFileMap;

	@Autowired
	DP077Service dp077service;
	
	@Override
	public int execute() {

		/*入口参数赋值开始*/
		int slh=getInt("slh");//实例号
    	/*入口参数赋值结束*/
		XmlResObj data = super.sendExternal("BSP_DW_HQGJYH_01", false);
		XmlResHead head = data.getHead();
		Map<String, Object> map = new HashMap<String, Object>();
		if (XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			map = data.getBody();
			String filename = map.get("file") == null ? "" : map.get("file").toString();
			if ("".equals(filename)) {
				logger.error("文件名不能为空。");
				throw new TransSingleException(CommonErrorCode.ERROR_WJDQSB,"");
			}
			File file=FileSwap.getFile(filename);
			if(file==null){
				logger.error("下传文件不存在："+filename);
				throw new TransSingleException(CommonErrorCode.ERROR_WJDQSB,"");
			} else {
				// BufferedReader提供了按行读取文本文件的方法readLine()
				// readLine()返回行有效数据，不包含换行符，未读取到数据返回null
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(file);
				} catch (FileNotFoundException e2) {
					e2.printStackTrace();
				}
				BufferedReader br = null;
				try {
					br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
					// 批量查表一次行数
					int count = 0;
					// 累计记录文件总行数
					int total = 0;
					// 是否为文件表头信息
					Boolean first = true;
					// 记录行文件
					String tmp = null;
					// 实际返回的数据
					HashMap<String, Object> tmpdata = null;
					// 文件中第一行列名
					String[] fileColNames = null;

					List<DP077> list = new ArrayList<DP077>();
					while ((tmp = br.readLine()) != null) {
						if (first) {
							fileColNames = tmp.toLowerCase().split(separators);
							first = false;
							continue;
						} else {
							tmpdata = getDownFileMap.query(fileColNames, tmp);
							total++;
							count++;
							// 解析文件，将数据存入临时表中
							DP077 dp077 = new DP077();
							dp077.setSeqno(total);
							dp077.setUnitaccname1(tmpdata.get("yhmc")==null?"":tmpdata.get("yhmc").toString());
							dp077.setUnitaccnum1(tmpdata.get("xh")==null?"":tmpdata.get("xh").toString());
							dp077.setDpbusitype("00");
							dp077.setInstance(slh);// 实例号
							list.add(dp077);
							// 累计循环一定条数批量提交入库一次
							if (count!=0&&count % IshConstants.GG_BATCH_COUNT == 0) {
								// 批量插入数据库之前临时关闭日志输出
								YDLogger.closeOut();
								if ("db2".equals(DB_TYPE)) {
									dp077service.db2batchInsert(list);
								} else if ("oracle".equals(DB_TYPE)) {
									dp077service.oraclebatchInsert(list);
								}
								// 批量插入数据库之后打开临时关闭日志输出
								YDLogger.openOut();
								list.clear();
								count = 0;
							}

						}
					}
					if (count > 0) {
						// 批量插入数据库之前临时关闭日志输出
						YDLogger.closeOut();
						if ("db2".equals(DB_TYPE)) {
							dp077service.db2batchInsert(list);
						} else if ("oracle".equals(DB_TYPE)) {
							dp077service.oraclebatchInsert(list);
						}
						// 批量插入数据库之后打开临时关闭日志输出
						YDLogger.openOut();
					}
					logger.debug("共" + total + "条数据");
					logger.debug("[-]调用接口获取单位明细账批量信息，存临时表结束");

				} catch (RuntimeException | IOException e) {
					// 批量插入数据库出错临时关闭日志输出
					YDLogger.openOut();
					e.printStackTrace();
					logger.error("插入数据库出错：" + e.getMessage(), e);
					throw new TransOtherException("系统错误，请查看日志！");
				} finally {
					// 打开临时关闭日志输出
					YDLogger.openOut();
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

		}


    	/*出口参数赋值开始*/

    	/*出口参数赋值结束*/

    	return 0;
   }

}
