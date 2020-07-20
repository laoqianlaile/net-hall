package com.yd.ish.service.impl.yy;

import com.yd.ish.common.interfaces.IYYManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.svrplatform.util.DataPool;

/**
 * 
 * 名称：YYWFYYSL01.java
 * <p>功能： 预约测试业务特殊校验<br> 
 * @author 张洪超
 * @version 0.1	2018年1月8日	张洪超创建
 */
@Component("YYWFYYSL01")
public class YYWFYYSL01 implements IYYManager {
	private static final Logger logger = LoggerFactory.getLogger(YYWFYYSL01.class);

	@Override
	public void checkYYData(DataPool paramDataPool) {
		logger.info("-----------------------");
		logger.info("----预约测试业务特殊校验");
	}

	@Override
	public void callbackYYYW(DataPool paramDataPool) {
		logger.info("-----------------------");
		logger.info("----预约测试业务确认后回调函数");
		
	}

}
