package com.yd.ish.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//import net.sf.json.JSONObject;

import com.alibaba.fastjson.JSONObject;
import com.yd.basic.exp.YDLoginRuntimeException;
import com.yd.basic.serviceimp.MenuInfoService;
import com.yd.ish.service.common.SsoOrgAuthLoginServiceImpl;
import com.yd.ish.service.common.SsoPersonLoginServiceImpl;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.context.UserContext;
import com.yd.svrplatform.comm_mdl.context.UserContextFactory;
import com.yd.svrplatform.comm_mdl.session.MemSesTimoutManager;
import com.yd.svrplatform.exception.PlatRuntimeException;
import com.yd.svrplatform.exception.YDBaseRuntimeException;
import com.yd.svrplatform.spring.ApplicationContextHelper;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.PoolUtil;
import com.yd.svrplatform.util.ReadProperty;
import com.yd.workflow.util.Constants;

/**
 * 名称：SsoLoginController.java
 * <p>
 * 功能： 单点登录
 * 
 * @author 许永峰
 * @version 0.1 2019年01月03日 创建
 */
@Controller
public class SsoLoginController {

	private static final Logger logger = LoggerFactory.getLogger(SsoLoginController.class);

	@Autowired
	MenuInfoService menuInfoService;
	/**
	 * 单点登录登录 第一步：获取token 第二步：请求省平台方法获取用户信息，重新组装用户信息 第三部：通过返回参数logintype调用登录实现类
	 * 
	 * @param request
	 * @param model
	 */
	@RequestMapping(value = "/ssoLogin", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String ssoLogin(HttpServletRequest request, HttpServletResponse response, Model model) {
		logger.info("[+]通过token获取用户信息登录开始");
		// TODO 第一步 根据省平台实际情况获取token start
		String token = request.getParameter("token");
		//第一步 根据省平台实际情况获取token end
		try {
			// TODO 第二步 根据token去省平台获取用户信息 start（省平台提供jar包）
			// 省平台单点登录获取用户信息方法地址
			String url = "http://平台IP/方法  ";
			// 调用省平台提供方法获取用户信息
			String userInfo = "调用省平台方法";
			//第二步 根据token去省平台获取用户信息 end
			logger.info("userInfo："+userInfo);
			// TODO 将返回的String类型的用户信息转成json格式 start （如果省平台返回的是json格式忽略这步）
			JSONObject userinfoJson;
			if (userInfo != null && !"".equals(userInfo)) {
				userinfoJson = JSONObject.parseObject(userInfo);
			} else {
				request.setAttribute("returnCode", "TOK12346");
				request.setAttribute("message", "无法获得用户信息");
				return "forward:/template/" + ReadProperty.getString("template") + "/html/error.jsp";
			}
			// 将返回的String类型的用户信息转成json格式 end
			logger.info("[-]通过token获取用户信息登录结束");
			// TODO 第三步 通过返回用户信息组装参数，跳转到对应登录实现类 start
			// 省平台返回的登录类型 type
			String type = userinfoJson.getString("type");
			// 登录用户名
			String dlyhm = userinfoJson.getString("dlyhm").trim();
			Map<String, String> map = new HashMap<String, String>();
			MainContext mainContext = new MainContext(request);
			UserContext user;
			String logintype ;
			// 判断返回值type值 0 个人登录 1单位用户登录
			if ("1".equals(type)) {
				// 根据logintype值跳转到对应的登录实现类
				logintype = "ssoorgauth";
				map.put("dlyhm", dlyhm);
				map.put("logintype", logintype);
				SsoOrgAuthLoginServiceImpl ssoOrgAuth = (SsoOrgAuthLoginServiceImpl) ApplicationContextHelper
						.getBean(logintype);
				user = ssoOrgAuth.check(map);
			} else {
				// 根据logintype值跳转到对应的登录实现类
				logintype = "ssoperson";
				map.put("dlyhm", dlyhm);
				map.put("logintype", "ssoperson");
				SsoPersonLoginServiceImpl ssoPerson = (SsoPersonLoginServiceImpl) ApplicationContextHelper
						.getBean(logintype);
				user = ssoPerson.check(map);
			}
			// 第三步 通过返回用户信息组装参数，跳转到对应登录实现类 end
			
			if (StringUtils.isBlank(logintype)) {
				request.setAttribute("returnCode", "BAS32401");
				request.setAttribute("message", "缺少参数logintype");
				throw new com.yd.svrplatform.exception.PlatRuntimeException("BAS32401", "缺少参数type");
			}
			if (ApplicationContextHelper.getBean(logintype) == null) {
				logger.debug("logintype=({})", logintype);
				request.setAttribute("returnCode", "BAS34003");
				request.setAttribute("message", "没有找到处理bean");
				throw new com.yd.svrplatform.exception.PlatRuntimeException("BAS34003", "没有找到处理bean");
			}
			if (user == null) {
				request.setAttribute("returnCode", "BAS31005");
				request.setAttribute("message", "check不能返回空对象");
				throw new PlatRuntimeException("BAS31005", "check不能返回空对象");
			}
			if (user.getOperId() == null) {
				request.setAttribute("returnCode", "BAS31006");
				request.setAttribute("message", "user.getOperId() 不能为空");
				throw new PlatRuntimeException("BAS31006", "user.getOperId() 不能为空");
			}

			user.setAttribute("menuflag", "0");
			user.setLoginTye(map.get("logintype").toString());
			user.loadConfig();
			UserContextFactory.open(user);
			mainContext.setUserContext(user);
			// 获取当前用户的权限树，用于权限校验
			user.setAttribute("menuTree", menuInfoService.getMenuTree());
			user.setAttribute("_IPDZ", request.getRemoteHost());
			if (request.getSession(false) != null) {
				UserContext olduser = (UserContext) request.getSession(false).getAttribute("user");
				if (olduser != null) {
					if (!user.getOperId().equals(olduser.getOperId())
							|| !user.getLoginTye().equals(olduser.getLoginTye())) {
						request.getSession(false).invalidate();
					}
				}
			}
			request.getSession().setAttribute("user", user);
			String use = ReadProperty.getString("memcached.use");
			if (use == null)
				use = "1";
			if (use.equals("1")) {
				MemSesTimoutManager.addSession(request.getSession().getId(), user.getOperId());
			}
			DataPool pool = null;
			if (mainContext.getDataPool() != null) {
				pool = mainContext.getDataPool();
			} else {
				pool = new DataPool();
				mainContext.setDataPool(pool);
			}
			pool.put(Constants._WF_OPERID, user.getOperId());
			pool.put(Constants._WF_ORGID, user.getOrgId());
			logger.debug("----------11");
			logger.debug("成功登陆     " + mainContext.getUserContext());
			if (mainContext.getUserContext().getAttribute("taskurl") == null) {
				pool.put("$page", "WAITTASK_01.ydpx");
			} else {
				pool.put("$page", mainContext.getUserContext().getAttribute("taskurl"));
			}
			String poolkey = PoolUtil.genPoolKey();
			pool.put(Constants._WF_POOLKEY, poolkey);
			PoolUtil.savePool(poolkey, pool);
			try {
				// 登录用户拦截器
				com.yd.svrplatform.comm_mdl.session.IUserContextListenerService ius = null;
				ius = (com.yd.svrplatform.comm_mdl.session.IUserContextListenerService) ApplicationContextHelper
						.getBean("ydUserContextListenerImp");
				if (ius != null)
					ius.userCreate(user,
							request.getSession(false).getId() == null ? null : request.getSession(false).getId());
				ius = (com.yd.svrplatform.comm_mdl.session.IUserContextListenerService) ApplicationContextHelper
						.getBean("ydUserContextListenerSys");
				if (ius != null)
					ius.userCreate(user,
							request.getSession(false).getId() == null ? null : request.getSession(false).getId());
			} catch (Exception e) {
				e.printStackTrace();
				request.setAttribute("returnCode", "BAS34005");
				request.setAttribute("message", "类型转换出错");
				throw new com.yd.svrplatform.exception.PlatRuntimeException("BAS34005", "类型转换出错");
			}

			String frameset = ReadProperty.getString("frameset");
			logger.debug("frameset={}", frameset);
			if (frameset != null && frameset.equals("1"))
				response.sendRedirect(request.getContextPath() + "/frameset");
			else {
				if (request.getParameter("func") == null) {
					response.sendRedirect(request.getContextPath() + "/home");
				} else {
					response.sendRedirect(request.getContextPath() + "/" + request.getParameter("func"));
				}
			}
			logger.debug("----------13");
			request.setAttribute("returnCode", "00000000");
			request.setAttribute("message", "succ");
			return null;
		} catch (YDLoginRuntimeException e) {
			response.setStatus(555);
			logger.error(e.getMessage());
			request.setAttribute("returnCode", "YDLoginRuntimeException");
			request.setAttribute("message", e.getMessage());
			if (e.getUrl() == null) {
				return null;
			} else {
				logger.debug("forward:" + e.getUrl());
				return null;
			}
		} catch (YDBaseRuntimeException e) {
			response.setStatus(200);
			System.err.println("YDBaseRuntimeException    " + e.getJsonMessage());
			logger.error(e.getMessage());
			request.setAttribute("returnCode", "YDBaseRuntimeException");
			request.setAttribute("message", e.getMessage());
			return e.getJsonMessage();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("登录时发生错误", e);
			request.setAttribute("returnCode", "Exception");
			request.setAttribute("message", "登录时发生错误:" + e.getMessage());
			response.setStatus(555);
			return "";
		}
	}
}
