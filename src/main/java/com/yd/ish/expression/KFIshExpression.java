package com.yd.ish.expression; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.spring.ApplicationContextHelper;


/**
 * @by  增加getMask方法提供给平台workflow中的配置需求
 * @version 1.0
 * @author  温顺全
 *
 */
public class KFIshExpression {
	private static final Logger logger = LoggerFactory.getLogger(KFIshExpression.class); 
	public String getMask(String name)
	{
		logger.debug("获取多级字典的掩码值");
		ParamConfigImp paramConfigImp = (ParamConfigImp)ApplicationContextHelper.getBean("paramConfigImp");
		return paramConfigImp.getMask(name);
	}  
	  
}
