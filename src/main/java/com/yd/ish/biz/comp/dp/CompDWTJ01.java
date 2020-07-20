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
 * 名称：CompDWTJ01
 * <p>
 * 功能：校验退缴信息<br>
 * 
 * @brief 功能简述 校验退缴信息
 * @author 柏慧敏
 * @version 0.1 柏慧敏 2018年9月7日 长春 新建
 * @note 
 */
@Component("CompDWTJ01")
public class CompDWTJ01 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompDWTJ01.class);
	@Autowired
	DP077Service dp077service;
	@Autowired
	ParamConfigImp paramconfigImp;

	@Override
	public int execute() {

		/* 入口参数赋值开始 */
		int instance = getInt("instance");// 实例号
		/* 入口参数赋值结束 */
		logger.info("[+]退缴批量信息校验开始");
		List<BatchExceptionBean> errlist = new ArrayList<BatchExceptionBean>();
		BatchExceptionBean bean;
		DP077 dp077tj = new DP077();
		dp077tj.setInstance(instance);
		// 30-退缴批量信息
		dp077tj.setDpbusitype("30");
		List<DP077> tjxxlist = dp077service.selectRepeatGRZH(dp077tj);
		// 重复的人员信息查询结果长度定义
		int tjxxlength = 0;
		if (tjxxlist != null) {
			tjxxlength = tjxxlist.size();
		}
		int seqno;
		int mark=0;
		for(int i = 0;i<tjxxlength;i++){
			seqno=tjxxlist.get(i).getSeqno();
			bean = new BatchExceptionBean(mark, "grzh", "", "不能低于调整前缴存基数", "0", "seqno", "datalist1", seqno + "", "",
					"", "", "");
			errlist.add(bean);
			mark++;
		}
		DP077 dp077 = new DP077();
		dp077.setInstance(instance);
		dp077.setDpbusitype("30");
		tjxxlist=dp077service.selectByCause(dp077);
		if (tjxxlist != null) {
			tjxxlength = tjxxlist.size();
		}
		for(int i=0;i<tjxxlength;i++){
			if(tjxxlist.get(i).getAmt1().compareTo(tjxxlist.get(i).getAmt2())==-1){
				seqno=tjxxlist.get(i).getSeqno();
				bean = new BatchExceptionBean(mark, "tjje", "", "退缴金额应不能大于个人账户余额", "0", "seqno", "datalist1", seqno + "", "",
						"", "", "");
				errlist.add(bean);
				mark++;
			}
		}
		logger.info("[-]退缴批量信息校验结束");
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
