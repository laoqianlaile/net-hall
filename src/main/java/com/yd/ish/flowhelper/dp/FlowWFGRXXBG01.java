package com.yd.ish.flowhelper.dp;

import com.yd.ish.common.util.TuoMinUtil;
import com.yd.svrplatform.util.ReadProperty;
import org.springframework.stereotype.Component;

import com.yd.basic.expression.IshExpression;
import com.yd.basic.service.ILoginService;
import com.yd.biz.engine.TransEngine;
import com.yd.ish.service.HomeData;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.context.UserContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.workflow.FlowHelperI;

 /**
 * 名称：FlowWFGRXXBG01
 * <p>功能： 个人基本信息变更<br>
 * @brief 功能简述 个人基本信息变更
 * @author 许永峰
 * @version 版本号	修改人	修改时间			地点		原因 
 * @note	0.1		许永峰	2018年7月31日		长春		创建
 */
@Component
public class FlowWFGRXXBG01 implements FlowHelperI {

	//private static final Logger logger = LoggerFactory.getLogger(FlowWFGRXXBG01.class);
	
	private static final String STEP_STEP1="step1";//个人基本信息变更
	
	/*交易调用示例，trancode为具体交易代码
	TransEngine.getInstance().execute("trancode",mainContext);
	*/
	
	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		if(stepid.equals(STEP_STEP1)){
			return in_step1(type,mainContext);
		}
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
		if(stepid.equals(STEP_STEP1)){
			return out_step1(mainContext);
		}
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		
		return null;
	}
	/**
	 * 获取个人基本信息
	 * @param type
	 * @param mainContext
	 * @return
	 */
	private boolean in_step1(String type,MainContext mainContext){
		DataPool pool = mainContext.getDataPool();
		pool.put("flag", "hqgrxx");
		pool.put("grzh",pool.getString("_OPERID"));
		TransEngine.getInstance().execute("TranGRXXBG01",mainContext);
		return true;
	}
	/**
	 * 提交修改后的信息
	 * @param mainContext
	 * @return
	 */
	private boolean out_step1(MainContext mainContext){
		DataPool pool = mainContext.getDataPool();
		pool.put("flag", "tjgrxx");
		TransEngine.getInstance().execute("TranGRXXBG01",mainContext);
		
		//若修改了首页展示的信息项，改变首页展示信息项的值
		UserContext user = MainContext.currentMainContext().getUserContext();
		// 获取登录时的user中的总线
		DataPool loginpool = (DataPool) user.getAttribute(ILoginService.EXTINFOKEY);
		// 业务中修改的字段id若与首页展示字段id相同，或与登录时user中赋值字段id相同的，需要重新进行赋值
		// loginpool.put("gddhhm", pool.getString("gddhhm"));
		//脱敏功能使用
		String tuomin_sfqy = ReadProperty.getString("tuomin_sfqy");
		if("1".equals(tuomin_sfqy)){
			TuoMinUtil.tMDataPool(loginpool);
		}
		user.setAttribute(ILoginService.EXTINFOKEY, loginpool);
		String homedata = HomeData.getPersonHomeData(loginpool, user);
		user.setAttribute("homedata", homedata);
		MainContext.currentMainContext().setUserContext(user);
		return true;
	}

}
