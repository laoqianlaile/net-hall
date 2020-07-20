package com.yd.ish.biz.comp.ln;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.BatchExceptionBean;
import com.yd.biz.exception.TransBatchException;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;

/**
 * 名称：CompDKSQ11
 * <p>
 * 功能：抵押信息检查<br>
 * 
 * @brief 抵押信息检查
 * @author 柏慧敏
 * @version 0.1 2018年7月26日 柏慧敏创建
 * @note
 */
@Component("CompDKSQ11")
public class CompDKSQ11 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompDKSQ11.class);
	@Autowired
	DP077Service dp077service;

	@Override
	public int execute() {

		/* 入口参数赋值开始 */
		int instance = getInt("instance");// 实例号
		/* 入口参数赋值结束 */
		List<BatchExceptionBean> errlist = new ArrayList<BatchExceptionBean>();
		BatchExceptionBean bean;
		// 查询表中抵押人信息
		DP077 dp077 = new DP077();
		dp077.setInstance(instance);
		dp077.setDpbusitype("12");// 12-抵押人信息
		logger.info("[+]查询并检查抵押人信息开始");
		List<DP077> dyrlist = dp077service.selectRepeatRyxx(dp077);
		if (dyrlist != null && dyrlist.size() > 0) {

			int seqno;
			// 循环人员信息列表，记录重复人员信息批量错误
			for (int i = 0; i < dyrlist.size(); i++) {
				// 序号
				seqno = dyrlist.get(i).getSeqno();
				// 记录批量错误信息：人员信息重复
				bean = new BatchExceptionBean(Integer.parseInt("12"+seqno), "zjhm", "", "人员信息重复", "0", "seqno", "dyrlist", seqno + "", "",
						"", "", "");
				errlist.add(bean);
			}
		}
		logger.info("[-]查询并检查抵押人信息结束");
		dp077.setDpbusitype("13");// 12-抵押人信息
		logger.info("[+]查询并检查抵押物信息开始");
		List<DP077> dywlist = dp077service.selectRepeatDyw(dp077);
		if (dywlist != null && dywlist.size() > 0) {
			int seqno;
			// 循环人员信息列表，记录重复人员信息批量错误
			for (int i = 0; i < dywlist.size(); i++) {
				// 序号
				seqno = dywlist.get(i).getSeqno();
				// 记录批量错误信息：抵押物信息重复
				bean = new BatchExceptionBean(Integer.parseInt("13"+seqno), "dywqzh", "", "抵押物重复", "0", "seqno", "dywlist", seqno + "",
						"", "", "", "");
				errlist.add(bean);
			}

		}
		logger.info("[-]查询并检查抵押物信息结束");
		// 抛出批量错误信息
		if (errlist != null && errlist.size() > 0) {
			TransBatchException e = new TransBatchException(errlist, "0");
			e.commit();
			throw e;
		}
		/* 出口参数赋值开始 */

		/* 出口参数赋值结束 */

		return 0;
	}

}
