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
 * 名称：CompJCJSTZ02
 * <p>
 * 功能：校验提交信息<br>
 * 
 * @brief 功能简述 校验提交的缴存基数调整信息是否有重复的个人账号
 * @author 许永峰
 * @version 版本号 修改人 修改时间 地点 原因
 * @note 0.1 许永峰 2018年7月2日 长春 创建
 */
@Component("CompJCJSTZ02")
public class CompJCJSTZ02 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompJCJSTZ02.class);
	@Autowired
	DP077Service dp077service;
	
	@Autowired	
	ParamConfigImp paramConfigImp; 


	/*
	 * @Autowired ParamConfigImp paramconfigImp;
	 */
	@Override
	public int execute() {

		/* 入口参数赋值开始 */
		int instance = getInt("instance");// 实例号
		/* 入口参数赋值结束 */
		// 根据实例号查临时表，获取重复的人员信息列表
		logger.info("[+]缴存基数调整批量信息校验开始");
		List<BatchExceptionBean> errlist = new ArrayList<BatchExceptionBean>();
		BatchExceptionBean bean;
		DP077 dp077 = new DP077();
		dp077.setInstance(instance);
		// 00-缴存基数调整批量信息
		dp077.setDpbusitype("00");
		List<DP077> ryxxlist = dp077service.selectRepeatGRZH(dp077);

		// 根据实例号查临时表，获取缴存基数调整信息列表
		List<DP077> jcjstzlist = dp077service.selectByCause(dp077);

		int mark = 0;
		// 校验账号重复错误
		for (DP077 entity : ryxxlist) {
			int seqno = entity.getSeqno();
			String grzh = entity.getUnitaccnum1();
			logger.info("个人账号" + grzh + "信息重复");
			bean = new BatchExceptionBean(mark, "grzh", "", "个人账号信息重复", "0", "seqno", "grjcjsList", seqno + "", "", "",
					"", "");
			mark++;
			errlist.add(bean);
		}
		// 当调低标志为"0"时 ，校验调整后的缴存基数是否大于调整前的
		String tdflagMask = paramConfigImp.getMask("ish.gg.other.sf."+getString("tdflag"));
		for (DP077 entity : jcjstzlist) {
			if ("NO".equals(tdflagMask)) {
				if (entity.getAmt1().compareTo(entity.getBasenum()) > 0) {
					logger.info("个人账号" + entity.getUnitaccname1() + "调整后的缴存基数：" + entity.getBasenum() + "低于调整前的缴存基数："
							+ entity.getAmt1());
					int seqno = entity.getSeqno();
					bean = new BatchExceptionBean(mark, "tzhgrjcjs", "", "不能低于调整前缴存基数,", "0", "seqno", "grjcjsList",
							seqno + "", "", "", "", "");
					mark++;
					errlist.add(bean);

				}
			}
			//校验调整后缴存基数不能等于调整前的
			if (entity.getAmt1().compareTo(entity.getBasenum()) == 0) {
				logger.info("个人账号" + entity.getUnitaccname1() + "调整后的缴存基数：" + entity.getBasenum() + "等于调整前的缴存基数："
						+ entity.getAmt1());
				int seqno = entity.getSeqno();
				bean = new BatchExceptionBean(mark, "tzhgrjcjs", "", "缴存基数没有变化,", "0", "seqno", "grjcjsList",
						seqno + "", "", "", "", "");
				mark++;
				errlist.add(bean);

			}
		}
		logger.info("[-]缴存基数调整批量信息校验结束");
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
