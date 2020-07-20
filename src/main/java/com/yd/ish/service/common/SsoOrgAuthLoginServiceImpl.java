package com.yd.ish.service.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yd.basic.service.ILogoutService;
import com.yd.ish.common.util.Httpclient;
import com.yd.ish.common.util.TuoMinUtil;
import com.yd.svrplatform.util.ReadProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yd.basic.service.ILoginCmdService;
import com.yd.basic.service.ILoginService;
import com.yd.biz.engine.TransEngine;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.HttpRequestUtil;
import com.yd.ish.service.HomeData;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.context.UserContext;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.exception.PlatRuntimeException;
import com.yd.svrplatform.util.DataPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 名称：SsoPersonLoginServiceImpl.java
 * <p>功能：单位用户单点登录管理实现类 <br> 
 * @author 许永峰
 * @version 0.1	2019年01月03日	许永峰创建
 *@version 0.2 	2020年02月10日	许永峰修改  新增单点登录退出相关 #4761
 */
@Service("ssoorgauth")
public class SsoOrgAuthLoginServiceImpl implements ILoginService, ILoginCmdService ,ILogoutService {

	private static final Logger logger = LoggerFactory.getLogger(SsoOrgAuthLoginServiceImpl.class);

	@Autowired
	ParamConfigImp paramConfigImp;

	@Override
	public UserContext check(Map<String, String> loginmodel) throws PlatRuntimeException {

		// 1. 增加前台参数的特殊字符校验
		if (!HttpRequestUtil.checkRequest(loginmodel)) {
			throw new TransSingleException("对不起，您输入的用户名不合法。");
		}

		String dlyhm = loginmodel.get("dlyhm");

		MainContext mainContext = MainContext.currentMainContext();
		DataPool datapool = new DataPool();
		/* 3. 单位认证经办人登录核验 开始 */
		datapool.put("loginId", dlyhm);
		mainContext.setDataPool(datapool);
		Map<String, Object> userinfo = TransEngine.getInstance().execute_NoTran("TranDLYZ402", mainContext);
		/* 单位认证经办人登录核验 结束 */

		/* 4. 获得单位经办人角色信息 开始 */
		String rolecodes = userinfo.get("rolecodes").toString();
		List<String> roleList = new ArrayList<String>();
		for (String str : rolecodes.split(";")) {
			roleList.add(str);
		}
		logger.debug("roleList===" + roleList);
		/* 获得单位经办人角色信息 结束 */

		/* 5. 组织 UserContext 等 开始 */
		datapool = mainContext.getDataPool();

		UserContext user = new UserContext();
		user.setLoginTye("auth");
		//将单位账号赋值给operid
		user.setOperId(userinfo.get("dwdjh").toString());
		user.setOperName(userinfo.get("jbrxm").toString());
		user.setOrgId(userinfo.get("dwdjh").toString());
		user.setOrgName(userinfo.get("dwmc").toString());
		user.setRoles(roleList);
		// v0.2  新增 start  新增判断是否是统一认证登录属性，属性key为固定值，属性值为@Service注解中的值
		user.setAttribute("usesso","ssoorgauth");
		// v0.2  新增 end
		//客户标识
		datapool.put("khbs", IshConstants.LOGIN_KHBS_JBR);
		//登陆方式
		datapool.put("dlfs", IshConstants.LOGIN_DLFS_DWYHM);
		//登陆用户名
		datapool.put("dlyhm", dlyhm);
		//单位地址
		datapool.put("dwdz", userinfo.get("dwdz").toString());
		//单位设立日期
		datapool.put("dwslrq", userinfo.get("dwslrq").toString());
		//单位邮编
		datapool.put("dwyb", userinfo.get("dwyb").toString());
		//社会诚信代码
		datapool.put("shcxdm", userinfo.get("shcxdm").toString());
		user.setAttribute(ILoginService.EXTINFOKEY, datapool);
		//脱敏功能使用
		String tuomin_sfqy = ReadProperty.getString("tuomin_sfqy");
		if("1".equals(tuomin_sfqy)){
			TuoMinUtil.tMDataPool(datapool);
		}
		paramConfigImp.loadMaskData(new String[] { "ish.gg.func.systemtype" });
	
		//子系统标识
		String systemtype = paramConfigImp.getValByMask("ish.gg.func.systemtype", IshConstants.SYSTEM_WTDW);
        user.setAttribute(IshConstants.LOGIN_SYSTEMTYPE,systemtype);
        // 组装首页的用户信息
        String homedata = HomeData.getOrgHomeData(datapool, user);
		user.setAttribute("homedata", homedata);
		/* 组织 UserContext 等 结束 */

		user.setAttribute("taskurl", "WAITTASK_00.ydpx");
		user.setAttribute("homeurl", "HOMEPAGE_"+ReadProperty.getString("template")+"_02.ydpx");

		return user;
	}

	@Override
	public String getOperIDKey() {
		return "jbrdjh";
	}

	/**
	 * 统一登出方法
	 * @param mainContext
	 * @param request
	 * @param response
	 * @return   返回结果
	 */
	@Override
	public boolean logoutExt(MainContext mainContext, HttpServletRequest request,HttpServletResponse response){
		try {
			//请求单点登录系统退出接口

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("请求统一登出接口失败："+e.getMessage());
			return false;
		}
		return true;

	}


	@Override
	public String cmd(MainContext mainContext, Map<String, String> map) throws PlatRuntimeException {
		/* 单位认证经办人登录核验 开始 */
		DataPool datapool = new DataPool();
		datapool.put("dlyhm", map.get("dlyhm"));
		datapool.put("dlmm", map.get("dlmm"));
		mainContext.setDataPool(datapool);
		TransEngine.getInstance().execute_NoTran("UPLGG1001_02", mainContext);
		/* 单位认证经办人登录核验 结束 */
		datapool.put("ywmc", "单位经办人用户名登录");
		mainContext.setDataPool(datapool);
		TransEngine.getInstance().execute_NoTran("GGTrans_0005", mainContext);
		return datapool.getString("dtmmkey");
	}

}
