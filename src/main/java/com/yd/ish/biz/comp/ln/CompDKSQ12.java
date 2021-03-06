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
 * 名称：CompDKSQ12
 * <p>功能：保证信息检查<br>
 * 
 * @brief 保证信息检查
 * @author 柏慧敏
 * @version 0.1 2018年7月27日 柏慧敏创建
 * @note
 */
@Component("CompDKSQ12")
public class CompDKSQ12 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDKSQ12.class);
	
	@Autowired
	DP077Service dp077service;
	
	@Override
	public int execute() {

		/*入口参数赋值开始*/
		int instance=getInt("instance");//实例号
    	/*入口参数赋值结束*/
		
		// 查询表中保证信息
		DP077 dp077 = new DP077();
		dp077.setInstance(instance);
		dp077.setDpbusitype("14");// 14-保证信息
		logger.info("[+]查询并检查保证信息开始");
		List<DP077> dyrlist = dp077service.selectRepeatRyxx(dp077);
		if (dyrlist != null && dyrlist.size() > 0) {
			List<BatchExceptionBean> errlist = new ArrayList<BatchExceptionBean>();
			BatchExceptionBean bean;
			int seqno;
			// 循环人员信息列表，记录重复人员信息批量错误
			for (int i = 0; i < dyrlist.size(); i++) {
				// 序号
				seqno = dyrlist.get(i).getSeqno();
				// 记录批量错误信息：人员信息重复
				bean = new BatchExceptionBean(seqno, "zjhm", "", "人员信息重复", "0", "seqno", "bzlist", seqno + "", "",
						"", "", "");
				errlist.add(bean);
			}
			// 抛出批量错误信息
			if (errlist != null && errlist.size() > 0) {
				TransBatchException e = new TransBatchException(errlist, "0");
				e.commit();
				throw e;
			}
		}
		logger.info("[-]查询并检查保证信息结束");

    	/*出口参数赋值开始*/

    	/*出口参数赋值结束*/

    	return 0;
   }

}
