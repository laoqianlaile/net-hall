package com.yd.ish.biz.comp.ln;

import com.yd.biz.exception.BatchExceptionBean;
import com.yd.biz.exception.TransBatchException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;

import java.util.ArrayList;
import java.util.List;

/**
 * 名称：CompXMXQ03
 * <p>功能：校验楼栋信息<br>
 * @brief 校验楼栋信息
 * @author 柏慧敏
 * @version V0.1 柏慧敏 20190606 长春 新建
 * @note
 */
@Component("CompXMXQ03")
public class CompXMXQ03 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompXMXQ03.class);
	 @Autowired
	 DP077Service dp077service;
	@Override
	public int execute() {

		/*入口参数赋值开始*/
		int instance=getInt("instance");//实例号
		int lpzzs=getInt("lpzzs");//楼盘总幢数
    	/*入口参数赋值结束*/
		logger.info("[+]检查楼栋信息开始");
		DP077 dp077 = new DP077();
		dp077.setInstance(instance);
		dp077.setDpbusitype("03");// 楼栋信息
		// 查询楼栋信息与楼栋总幢数是否一致
		int ldcounts = dp077service.selectLdLpCounts(dp077);
		if(ldcounts != lpzzs){
			throw new TransSingleException("楼栋信息与楼栋总幢数不一致");
		}
		List<DP077> list = dp077service.selectRepeatLdxx(dp077);
		List<BatchExceptionBean> errlist = new ArrayList<BatchExceptionBean>();
		BatchExceptionBean bean;
		if(list != null && list.size()>0){
			int seqno;
			// 循环楼栋信息列表，记录重复楼栋信息批量错误
			for (int i = 0; i < list.size(); i++) {
				// 序号
				seqno = list.get(i).getSeqno();
				// 记录批量错误信息：楼栋信息重复
				bean = new BatchExceptionBean(Integer.parseInt("01"+seqno), "ldbh", "", "楼栋信息重复", "0", "seqno", "ldlist", seqno + "",
						"", "", "", "");
				errlist.add(bean);
			}
		}
		logger.info("[-]检查楼栋信息结束");
		// 抛出批量错误信息
		if (errlist != null && errlist.size() > 0) {
			TransBatchException e = new TransBatchException(errlist, "0");
			e.commit();
			throw e;
		}
    	/*出口参数赋值开始*/

    	/*出口参数赋值结束*/

    	return 0;
   }

}
