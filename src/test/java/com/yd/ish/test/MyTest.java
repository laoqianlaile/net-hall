package com.yd.ish.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.yd.basic.service.ILoginService;
import com.yd.biz.engine.TransTestEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.context.UserContext;
import com.yd.svrplatform.comm_mdl.log.YDLogger;
import com.yd.svrplatform.util.DataPool;

@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试  
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath*:spring/spring-mybatis.xml","classpath*:org/spring-mybatis.xml"}) //加载配置文件
public class MyTest {

	@Test
	public void test(){
		
		//设置入口参数变量
		UserContext user=new UserContext();
    	user.setOperId("0000");
    	MainContext mainContext=new MainContext(user);
		DataPool pool=new DataPool();
		pool.put("sfzh", "11308761234");
		pool.put("accname1", "王地不上");
		pool.put("age", "12");
		pool.put("salary", "4000");
		pool.put("_IS", "100");
		pool.put("_TRANSDATE", "2015-12-10");
		pool.put("funccode", "010002");
		user.setAttribute(ILoginService.EXTINFOKEY, pool);
		mainContext.setDataPool(pool);
		try{
			//调用交易测试引擎类
			new TransTestEngine().test("TranYWXXZSCX01","CompYWXXZSCX01",mainContext);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			//提交日志
			YDLogger.commitAllLog();
		}
	}
}
