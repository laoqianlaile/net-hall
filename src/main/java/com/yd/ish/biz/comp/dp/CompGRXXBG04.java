package com.yd.ish.biz.comp.dp;

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
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;

/**
 * 名称：CompGRXXBG04
 * <p>
 * 功能：校验批量信息<br>
 * 
 * @brief 校验批量信息
 * @author 柏慧敏
 * @version 0.1 2018年10月10日 柏慧敏创建
 * @note
 */
@Component("CompGRXXBG04")
public class CompGRXXBG04 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompGRXXBG04.class);

	@Autowired
	DP077Service dp077service;
	@Autowired
	ParamConfigImp paramconfigImp;

	@Override
	public int execute() {

		/* 入口参数赋值开始 */
		int instance = getInt("instance");// 实例号
		/* 入口参数赋值结束 */
		logger.info("[+]单位下个人信息变更批量信息校验开始");
		List<BatchExceptionBean> errlist = new ArrayList<BatchExceptionBean>();
		BatchExceptionBean bean;
		DP077 dp077 = new DP077();
		dp077.setInstance(instance);
		// 30-退缴批量信息
		dp077.setDpbusitype("30");
		// 查询个人账号重复的信息
		List<DP077> repeatlist = dp077service.selectRepeatGRZH(dp077);
		// 定义重复信息的数量
		int repeatlistsize = 0;
		if (repeatlist != null) {
			repeatlistsize = repeatlist.size();
		}
		// 错误信息总数
		int total = 0;
		// 报错信息的序号
		int seqno = 0;
		// 个人账号
		String grzh = "";
		// 循环重复的人员信息
		for (int i = 0; i < repeatlistsize; i++) {
			// 错误信息数量加1
			total++;
			seqno = repeatlist.get(i).getSeqno();
			grzh = repeatlist.get(i).getUnitaccnum1();
			logger.info("个人账号为" + grzh + "的人员信息重复");
			// 增加批量错误信息
			bean = new BatchExceptionBean(total, "grzh", "", "个人账号重复", "0", "seqno", "datalist1", seqno + "", "", "",
					"", "");
			errlist.add(bean);

		}
		// 查询批量变更信息
		List<DP077> list = dp077service.selectBySlh(instance);
		// 定义批量信息数量
		int listsize = 0;
		if (list != null) {
			listsize = list.size();
		}
		for (int i = 0; i < listsize; i++) {
			if ("".equals(list.get(i).getUnitaccname2()) && "".equals(list.get(i).getUnitaccnum2())
					&& "".equals(list.get(i).getBegym()) && "".equals(list.get(i).getZip())
					&& "".equals(list.get(i).getAccnum2()) && "".equals(list.get(i).getXmqp())) {
				// 错误信息数量加1
				total++;
				seqno = list.get(i).getSeqno();
				grzh = list.get(i).getUnitaccnum1();
				logger.info("个人账号为" + grzh + "的人员信息未变化");
				// 增加批量错误信息
				bean = new BatchExceptionBean(total, "bghjtzz", "", "家庭住址、手机号码、职业、职称、职务、学历信息均未变化", "0", "seqno", "datalist1", seqno + "", "",
						"", "", "");
				errlist.add(bean);
			}

		}
		logger.info("[-]单位下个人信息变更批量信息校验结束");
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
