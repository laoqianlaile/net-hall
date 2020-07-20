package com.yd.ish.biz.comp.common.dlgl;

import com.alibaba.druid.support.json.JSONUtils;
import com.yd.biz.comp.BaseComp;
import com.yd.org.service.mybatis.LoginService;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * 名称：CompDLHQCD01.java
 * <p>功能：登录功能-通过角色获取菜单 <br> 
 * @author 张洪超
 * @version 0.1	2017年8月31日	张洪超创建
 *
 *
 */
@Component("CompDLHQCD01")
public class CompDLHQCD01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDLHQCD01.class);

	@Autowired
	LoginService loginService;
	
	@Override
	public int execute() {

		/*入口参数赋值开始*/
		String rolecodes=getString("rolecodes");//角色编号 多个用 ; 隔开
		String systemtype=getString("systemtype");//所属子系统
    	/*入口参数赋值结束*/
	
		DataPool dataPool = MainContext.currentMainContext().getDataPool();
		HashMap<String, List<HashMap<String, Object>>> map=loginService.getFuncByRolecode(systemtype, rolecodes, dataPool);
		String json=JSONUtils.toJSONString(map);
		logger.info("菜单："+json);

		/*出口参数赋值开始*/
		setOutParam("json",json);//json数据
    	/*出口参数赋值结束*/

    	return 0;
   }
	
}
