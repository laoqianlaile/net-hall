package com.yd.ish.biz.comp.ln;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;

 /**
 * 名称：CompDKSQ03
 * <p>功能：根据类型和序号删除相应信息<br>
 * @brief 根据类型和序号删除相应信息
 * @author 柏慧敏
 * @version 0.1 2018年7月17日 柏慧敏创建
 * @note
 */
@Component("CompDKSQ03")
public class CompDKSQ03 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDKSQ03.class);
	@Autowired
	DP077Service dp077service;
	@Override
	public int execute() {

		/*入口参数赋值开始*/
		int instance=getInt("instance");//实例号
		int seqno=getInt("seqno");//序号
		String dpbusitype=getString("dpbusitype");//类型
    	/*入口参数赋值结束*/
		
		logger.info("[+]根据类型和序号删除相应信息开始");
		DP077 dp077 = new DP077();
		//删除条件赋值
		dp077.setSeqno(seqno);
		dp077.setInstance(instance);
		dp077.setDpbusitype(dpbusitype);
		//删除当前信息
		dp077service.deleteByCause(dp077);
		logger.info("[-]根据类型和序号删除相应信息结束");
    	/*出口参数赋值开始*/

    	/*出口参数赋值结束*/

    	return 0;
   }

}
