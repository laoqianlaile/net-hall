package com.yd.ish.biz.comp.dp;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 名称：CompGRJXDZ01
 * <p>
 * 功能：单位下个人结息对账单查询<br>
 * 
 * @brief 功能简述 单位下个人结息对账单查询
 * @author
 * @version 版本号 修改人 修改时间 地点 原因
 * @note 0.1 许永峰 2018年11月27日 长春 创建
 */
@Component("CompGRJXDZ01")
public class CompGRJXDZ01 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompGRJXDZ01.class);
	/** 数据分割符 */
	private static final String separators = ReadProperty.getString("file_separators");
	/** 读核心返回文件编码格式 */
	private static final String encoding = ReadProperty.getString("readfile_encoding_bsp");
	/** 数据库类型 */
	private static final String DB_TYPE = ReadProperty.getString("dbType");
	/** 启用凭证系统标记 **/
	private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");
	@Autowired
	GetDownFileMap getDownFileMap;

	@Autowired
	DP077Service dp077service;
	@Override
	public int execute() {

		/* 入口参数赋值开始 */
		int instance = getInt("instance");// 实例号
		/* 入口参数赋值结束 */
		// 定义日期格式yyyy-mm-dd
		SimpleDateFormat sdf = new SimpleDateFormat(IshConstants.GG_DATE_FORMAT);
		// 根据实例号删除当前数据
		dp077service.deleteBySlh(instance);
		logger.debug("[+]调用接口获取单位下个人结息对账单批量信息，存临时表DP077开始[+]");
		// 调用接口获取单位挂账批量信息
		XmlResObj data = super.sendExternal("BSP_DP_GRJXDZ_01");
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
							dp077.setSeqno(total);

							dp077.setInstance(instance);// 实例号
							dp077.setAccname1(returnString(tmpdata.get("xm")));// 姓名
							dp077.setAccname2(returnString(tmpdata.get("grzh")));// 个人账号
							dp077.setAmt1(new BigDecimal(tmpdata.get("dqbj").toString()));// 定期本金
							dp077.setAmt2(new BigDecimal(tmpdata.get("hqbj").toString()));//活期本金
							dp077.setBasenum(new BigDecimal(tmpdata.get("zbj").toString()));//总本金
							dp077.setPayvouamt(new BigDecimal(tmpdata.get("dqlx").toString()));//定期利息
							dp077.setJtysr(new BigDecimal(tmpdata.get("hqlx").toString()));//活期利息
							dp077.setFreeuse2(new BigDecimal(tmpdata.get("zlx").toString()));//总利息
							dp077.setUnitprop(new BigDecimal(tmpdata.get("ye").toString()));//余额
							dp077.setDpbusitype("21");
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
					logger.debug("[-]调用接口获取单位下个人结息对账单批量信息，存临时表结束");

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

		/* 出口参数赋值开始 */
		String qxrq = returnString(body.get("qxrq"));
		String jxrq = returnString(body.get("jxrq"));
		String dqrll = returnString(body.get("dqrll"));
		String hqrll = returnString(body.get("hqrll"));
		String xjrs = returnString(body.get("xjrs"));
		String xjdqbj = returnString(body.get("xjdqbj"));
		String xjhqbj = returnString(body.get("xjhqbj"));
		String xjzbj = returnString(body.get("xjzbj"));
		String xjdqlx = returnString(body.get("xjdqlx"));
		String xjhqlx = returnString(body.get("xjhqlx"));
		String xjzlx = returnString(body.get("xjzlx"));
		String xjye = returnString(body.get("xjye"));
		String pzfilename = body.get("pzfilename") == null ? "" : body.get("pzfilename").toString();
		setOutParam("qxrq", qxrq);// 起息日期
		setOutParam("jxrq", jxrq);// 结息日期
		setOutParam("dqrll", dqrll);// 定期日利率
		setOutParam("hqrll", hqrll);// 活期日利率
		setOutParam("xjrs", xjrs);// 小计人数
		setOutParam("xjdqbj", xjdqbj);// 小计定期本金
		setOutParam("xjhqbj", xjhqbj);// 小计活期本金
		setOutParam("xjzbj", xjzbj);// 小计总本金
		setOutParam("xjdqlx", xjdqlx);// 小计定期利息
		setOutParam("xjhqlx", xjhqlx);// 小计活期利息
		setOutParam("xjzlx", xjzlx);// 小计总利息
		setOutParam("xjye", xjye);// 小计余额
		if ("1".equals(pzsystemFlag)) { // 启用凭证系统时设置出口参数
			setOutParam("pzfilename", pzfilename);// 凭证文件名称
		}
		/* 出口参数赋值结束 */
		return 0;
	}
	private String returnString(Object obj){
		return obj ==null ? "" : obj.toString();
	}
}
