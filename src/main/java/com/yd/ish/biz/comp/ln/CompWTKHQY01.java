package com.yd.ish.biz.comp.ln;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.util.ReadProperty;

/**
 * 名称：CompWTKHQY01
 * <p>
 * 功能：获取借款人信息<br>
 * 
 * @brief 获取借款人信息
 * @author 柏慧敏
 * @version 0.1 2018年8月28日 柏慧敏创建
 */
@Component("CompWTKHQY01")
public class CompWTKHQY01 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompWTKHQY01.class);
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
	
	@Autowired
	ParamConfigImp paramConfigImp;

	@Override
	public int execute() {

		/* 入口参数赋值开始 */
		int instance = getInt("_IS");// 实例号
		String jkhtbh=getString("jkhtbh");//借款合同编号
		String jkhtbhsz = getString("jkhtbhsz"); // 借款合同编号数组
		/*入口参数赋值结束*/
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
		// 若flag为true，调用接口获取借款人信息
		if(flag) {
			XmlResObj data = super.sendExternal("BSP_LN_HQJKRXX_01");
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
								// 解析文件，将数据存入临时表中
								DP077 dp077 = new DP077();
								dp077.setInstance(instance);// 实例号
								dp077.setAccname1(tmpdata.get("xingming") == null ? "" : tmpdata.get("xingming").toString());//姓名
								dp077.setCertinum(tmpdata.get("zjhm") == null ? "" : tmpdata.get("zjhm").toString());// 证件号码
								dp077.setCertitype(tmpdata.get("zjlx") == null ? "" : tmpdata.get("zjlx").toString());// 证件类型
								List<DP077> list1 = dp077service.selectByCause(dp077);
								dp077.setHyzk(tmpdata.get("grzhzt") == null ? "" : tmpdata.get("grzhzt").toString());// 个人账户状态
								dp077.setAmt1(tmpdata.get("grzhye") == null ? new BigDecimal("0") : new BigDecimal(tmpdata.get("grzhye").toString()));// 个人账户余额
								dp077.setFundsouflag(tmpdata.get("kkrlx") == null ? "" : tmpdata.get("kkrlx").toString());//扣款人类型
								if (tmpdata.get("qylx") != null && !"".equals(tmpdata.get("qylx").toString())) {
									dp077.setSex(tmpdata.get("qylx").toString());//签约类型
								}
								if (tmpdata.get("khsx") != null && !"".equals(tmpdata.get("khsx").toString())) {
									dp077.setPeoplenum(Integer.parseInt(tmpdata.get("khsx").toString()));//扣划顺序
								}

								dp077.setDpbusitype("30");// 业务类型
								if (list1 != null && list1.size() > 0) {
									dp077.setSeqno(list1.get(0).getSeqno());
									dp077service.updateBySeqnoInstance(dp077);
								} else {
									List<DP077> list2 = dp077service.selectBySlh(instance);
									if (list2 != null && list2.size() > 0) {
										total++;
										count++;
										dp077.setSeqno(list2.get(list2.size() - 1).getSeqno() + 1);
										list.add(dp077);
									} else {
										total++;
										count++;
										dp077.setSeqno(total);
										list.add(dp077);
									}
								}

								// 累计循环一定条数批量提交入库一次
								if (count % IshConstants.GG_BATCH_COUNT == 0 && count > 0) {
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
						logger.debug("[-]调用接口获取借款人批量信息，存临时表结束");

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
			throw new TransSingleException("获取借款人信息失败，请确认后重新录入");
		}
		/* 出口参数赋值开始 */

		/* 出口参数赋值结束 */

		return 0;
	}

}
