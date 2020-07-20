package com.yd.ish.biz.comp.ln;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransOtherException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.GetDownFileMap;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
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
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 /**
 * 名称：CompDKHKJH01
 * <p>功能：公积金贷款还款计划<br>
 * @brief 获取公积金贷款还款计划信息
 * @author 柏慧敏
 * @version 0.1 2018年6月15日 柏慧敏创建
 * @note
 */
@Component("CompDKHKJH01")
public class CompDKHKJH01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDKHKJH01.class);
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
		int instance=getInt("instance");//实例号
		String jkhtbh = getString("jkhtbh"); // 借款合同编号
		String jkhtbhsz = getString("jkhtbhsz"); // 借款合同编号数组
		/* 入口参数赋值结束 */

		// 传递的参数是否正确
		Boolean flag = false;
		// 若借款合同编号数组不为空
		if(jkhtbhsz != null && !"".equals(jkhtbhsz)){
			String[] jkhtbharray = jkhtbhsz.split(",");
			// 循环数组
			for(int i=0;i<jkhtbharray.length;i++){
				// 判断页面选择的借款合同编号是否在数组中
				if(jkhtbh.equals(jkhtbharray[i])){
					// 若在数组中，标志赋值成true，退出循环
					flag = true;
					break;
				}
			}
		}
		if(flag) {
			//定义日期格式yyyy-mm-dd
			SimpleDateFormat sdf = new SimpleDateFormat(IshConstants.GG_DATE_FORMAT);
			// 根据实例号删除当前数据
			dp077service.deleteBySlh(instance);

			logger.info("[+]调用接口获取贷款还款计划信息，存临时表开始");
			// 调用接口获取贷款还款计划信息
			XmlResObj data = super.sendExternal("BSP_LN_GJJDKHKJH_01");
			XmlResHead head = data.getHead();
			if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
				throw new TransSingleException(head.getParticular_info());
			}
			// 调用综服接口从body中获取文件名
			Map<String, Object> body = data.getBody();
			String filename = body.get("file").toString();
			if (!(filename == null)) {
				File file = FileSwap.getFile(filename);
				if (file == null) {
					logger.error("下传文件不存在：" + filename);
					throw new TransOtherException("系统错误，请查看日志！");
				} else {
					// 解析下传文件
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

						// 如果文件信息不为null，开始解析文件
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
								dp077.setSeqno(total);// 序号
								dp077.setDpbusitype("00");
								dp077.setInstance(instance);// 实例号
								dp077.setAccnum1(tmpdata.get("dqqc") == null ? "" : tmpdata.get("dqqc").toString());//当前期次
								dp077.setAmt1(tmpdata.get("cqye") == null || tmpdata.get("cqye") == "" ? new BigDecimal("0") : new BigDecimal(tmpdata.get("cqye").toString()));//初期余额
								dp077.setAmt2(tmpdata.get("hkje") == null || tmpdata.get("hkje") == "" ? new BigDecimal("0") : new BigDecimal(tmpdata.get("hkje").toString()));//还款金额
								dp077.setBasenum(tmpdata.get("dqyhbj") == null || tmpdata.get("dqyhbj") == "" ? new BigDecimal("0") : new BigDecimal(tmpdata.get("dqyhbj").toString()));//当期应还本金
								dp077.setJtysr(tmpdata.get("dqyhlx") == null || tmpdata.get("dqyhlx") == "" ? new BigDecimal("0") : new BigDecimal(tmpdata.get("dqyhlx").toString()));//当期应还利息
								dp077.setPayvouamt(tmpdata.get("whbj") == null || tmpdata.get("whbj") == "" ? new BigDecimal("0") : new BigDecimal(tmpdata.get("whbj").toString()));//未还本金
								dp077.setAccnum2(tmpdata.get("scfxjxr") == null ? "" : tmpdata.get("scfxjxr").toString());//上次罚息计息日
								dp077.setAccname1(tmpdata.get("bqzt") == null ? "" : tmpdata.get("bqzt").toString());//本期状态
								dp077.setFreeuse2(new BigDecimal(tmpdata.get("dkll") == null ? "0" : tmpdata.get("dkll").toString()));//贷款利率
								if (tmpdata.get("jhhkrq") != null && !"".equals(tmpdata.get("jhhkrq").toString())) {
									try {
										dp077.setTransdate(sdf.parse(tmpdata.get("jhhkrq").toString()));//计划还款日期
									} catch (ParseException e) {
										e.printStackTrace();
									}
								}
								dp077.setAgentinstcode(tmpdata.get("whlx") == null ? "0.00" : tmpdata.get("whlx").toString());//未还利息
								dp077.setAgentop(tmpdata.get("whfx") == null ? "0.00" : tmpdata.get("whfx").toString());//未还罚息
								dp077.setCertinum(tmpdata.get("whhj") == null ? "0.00" : tmpdata.get("whhj").toString());//未还合计
								list.add(dp077);
								// 累计循环一定条数批量提交入库一次
								if (count != 0 && count % IshConstants.GG_BATCH_COUNT == 0) {
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
						logger.info("共" + total + "条数据");
						logger.info("[-]调用接口获取贷款还款计划信息，存临时表结束");
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
		}else{
			logger.error("获取到的借款合同编号："+ jkhtbh);
			throw new TransSingleException("获取贷款还款计划失败，请检查后重新查询");
		}

    	/*出口参数赋值开始*/

    	/*出口参数赋值结束*/

    	return 0;
   }

}
