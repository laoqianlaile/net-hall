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
 * 名称：CompXMXQ04
 * <p>功能：校验楼盘信息<br>
 * @brief 校验楼盘信息
 * @author 柏慧敏
 * @version V0.1 柏慧敏 20190606 长春 新建
 * @note
 */
@Component("CompXMXQ04")
public class CompXMXQ04 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompXMXQ04.class);

	@Autowired
	DP077Service dp077service;
	@Override
	public int execute() {

		/*入口参数赋值开始*/
		int instance = getInt("_IS");//实例号
		int lpzts = getInt("lpzts");//楼盘总套数
    	/*入口参数赋值结束*/
		logger.info("[+]检查楼盘信息开始");
		DP077 dp077 = new DP077();
		dp077.setInstance(instance);
		dp077.setDpbusitype("04");// 楼盘信息
		int lpcounts = dp077service.selectLdLpCounts(dp077);
		// 查询楼盘信息与楼盘总套数是否一致
		if(lpcounts != lpzts){
			throw new TransSingleException("楼盘信息与楼盘总套数不一致");
		}

		List<DP077> list = dp077service.selectRepeatLpxx(dp077);
		List<BatchExceptionBean> errlist = new ArrayList<BatchExceptionBean>();
		BatchExceptionBean bean;
		if(list != null && list.size()>0){
			int seqno;
			// 循环楼盘信息列表，记录楼盘信息批量错误
			for (int i = 0; i < list.size(); i++) {
				// 序号
				seqno = list.get(i).getSeqno();
				// 记录批量错误信息：楼盘信息重复
				bean = new BatchExceptionBean(Integer.parseInt("02"+seqno), "ldbh", "", "楼盘信息重复", "0", "seqno", "lplist", seqno + "",
						"", "", "", "");
				errlist.add(bean);
			}
		}
		logger.info("[-]检查楼盘信息结束");
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
