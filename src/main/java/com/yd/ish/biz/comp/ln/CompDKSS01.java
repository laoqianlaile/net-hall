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
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
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
 * 名称：CompDKSS01
 * <p>
 * 功能：公积金贷款试算<br>
 * 
 * @brief 公积金贷款试算
 * @author 柏慧敏
 * @version 0.1 2018年6月21日 柏慧敏创建
 * @note
 */
@Component("CompDKSS01")
public class CompDKSS01 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompDKSS01.class);
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
		int instance=getInt("_IS"); // 实例号
		String jkhtbh = getString("jkhtbh"); // 借款合同编号
		String jkhtbhsz = getString("jkhtbhsz"); // 借款合同编号数组
		String sslx = getString("sslx"); // 所属类型
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
		// 若是贷前试算，不需要借款合同编号，标志直接赋值成true
		if(sslx.equals(paramConfigImp.getValByMask("bsp.ln.sslx","01"))){
			flag = true;
		}
		// 若flag为true，调用接口获取试算结果
		if(flag){
			//定义日期格式yyyy-mm-dd
			SimpleDateFormat sdf = new SimpleDateFormat (IshConstants.GG_DATE_FORMAT);

			// 根据实例号删除当前数据
			dp077service.deleteBySlh(instance);

			// 调用接口获取贷款详细信息
			logger.info("[+]调用接口进行贷款试算开始");
			XmlResObj data = super.sendExternal("BSP_LN_DKSS_01");
			XmlResHead head = data.getHead();
			if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
				throw new TransSingleException(head.getParticular_info());
			}
			Map<String, Object> body = data.getBody();
			String filename=body.get("file").toString();
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
								dp077.setPeoplenum(tmpdata.get("dqqc") == null ? 0:Integer.parseInt(tmpdata.get("dqqc").toString()));//当前期次
								dp077.setAmt1(tmpdata.get("qcye") == null || tmpdata.get("qcye") == "" ? new BigDecimal("0"):new BigDecimal(tmpdata.get("qcye").toString()));//期初余额
								dp077.setAmt2(tmpdata.get("dqyhje") == null || tmpdata.get("dqyhje") == "" ? new BigDecimal("0"):new BigDecimal(tmpdata.get("dqyhje").toString()));//当期应还金额
								dp077.setBasenum(tmpdata.get("dqyhbj") == null || tmpdata.get("dqyhbj") == "" ? new BigDecimal("0"):new BigDecimal(tmpdata.get("dqyhbj").toString()));//当期应还本金
								dp077.setJtysr(tmpdata.get("dqyhlx") == null || tmpdata.get("dqyhlx") == "" ? new BigDecimal("0"):new BigDecimal(tmpdata.get("dqyhlx").toString()));//当期应还利息
								if(tmpdata.get("hkrq") != null && !"".equals(tmpdata.get("hkrq").toString())){
									try {
										dp077.setTransdate(sdf.parse(tmpdata.get("hkrq").toString()));//还款日期
									} catch (ParseException e) {
										e.printStackTrace();
									}
								}
								list.add(dp077);
								// 累计循环一定条数批量提交入库一次
								if (count!=0&&count % IshConstants.GG_BATCH_COUNT == 0) {
									// 批量插入数据库之前临时关闭日志输出
									YDLogger.closeOut();
									if("db2".equals(DB_TYPE)){
										dp077service.db2batchInsert(list);
									}else if("oracle".equals(DB_TYPE)){
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
							if("db2".equals(DB_TYPE)){
								dp077service.db2batchInsert(list);
							}else if("oracle".equals(DB_TYPE)){
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
			String shbj = body.get("shbj") == null ? "" : body.get("shbj").toString();
			String shlx = body.get("shlx") == null ? "" : body.get("shlx").toString();
			String shfx = body.get("shfx") == null ? "" : body.get("shfx").toString();
			String yhke = body.get("yhke") == null ? "" : body.get("yhke").toString();
			String xsyqs = body.get("xsyqs") == null ? "" : body.get("xsyqs").toString();
			String tqhkbjze = body.get("tqhkbjze") == null ? "" : body.get("tqhkbjze").toString();
			String gjjyhke = body.get("gjjyhke") == null ? "" : body.get("gjjyhke").toString();
			String lxhj = body.get("lxhj") == null ? "" : body.get("lxhj").toString();
			String hkehj = body.get("hkehj") == null ? "" : body.get("hkehj").toString();
			String syyhke = body.get("syyhke") == null ? "" : body.get("syyhke").toString();
			String hkhj = body.get("hkhj") == null ? "" : body.get("hkhj").toString();

			logger.info("[-]调用接口进行贷款试算结束");

			/* 出口参数赋值开始 */
			setOutParam("shbj", shbj);// 实还本金
			setOutParam("shlx", shlx);// 实还利息
			setOutParam("shfx", shfx);// 实还罚息
			setOutParam("tqhkbjze", tqhkbjze);// 提前还款本金总额
			setOutParam("yhke", yhke);// 月还款额
			setOutParam("xsyqs", xsyqs);// 新剩余期数
			setOutParam("gjjyhke", gjjyhke);// 公积金月还款额
			setOutParam("lxhj", lxhj);// 利息合计
			setOutParam("hkehj", hkehj);// 还款额合计
			setOutParam("syyhke", syyhke);// 商贷月还款额
			setOutParam("hkhj", hkhj);// 还款合计
			/* 出口参数赋值结束 */
		}else{
			logger.error("获取到的借款合同编号："+ jkhtbh);
			throw new TransSingleException("试算失败，请确认信息后重新计算。");
		}


		return 0;
	}

}
