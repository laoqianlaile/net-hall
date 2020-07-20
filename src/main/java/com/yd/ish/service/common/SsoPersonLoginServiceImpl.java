package com.yd.ish.service.common;

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

import com.yd.basic.service.ILoginService;
import com.yd.biz.engine.TransEngine;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.HttpRequestUtil;
import com.yd.ish.service.HomeData;
import com.yd.org.util.GetChannelCode;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.context.UserContext;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.exception.PlatRuntimeException;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.expression.ParseExpression;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 名称：SsoPersonLoginServiceImpl.java
 * <p>功能：个人单点登录管理实现类 <br> 
 * @author 许永峰
 * @version 0.1	2018年12月26日	许永峰创建
 * @version 0.2 	2020年02月10日	许永峰修改  新增单点登录退出相关 #4761
 */
@Service("ssoperson")
public class SsoPersonLoginServiceImpl implements ILoginService,ILogoutService {
	
	@Autowired
	ParamConfigImp paramConfigImp;

	private static final Logger logger = LoggerFactory.getLogger(SsoPersonLoginServiceImpl.class);

	@Override
	public UserContext check(Map<String, String> loginmodel) throws PlatRuntimeException {
		
		// 增加前台参数的特殊字符校验
		if (!HttpRequestUtil.checkRequest(loginmodel)) {
			throw new TransSingleException("对不起，您输入的用户名不合法。");
		}
				
		String zdyyhm = loginmodel.get("dlyhm");
		MainContext mainContext = MainContext.currentMainContext();
		DataPool datapool = new DataPool();
		
        /*核验个人客户信息	 开始*/
        datapool.put("loginId", zdyyhm);
        datapool.put("dlqd", GetChannelCode.getNP()); 
        mainContext.setDataPool(datapool);
        
        //调用登录管理交易 -返回登录用户信息
        Map<String,Object> userinfo = TransEngine.getInstance().execute("TranDLYZ205",mainContext);
        /*核验个人客户信息	结束*/
       
        /*组织 UserContext 等	开始*/
        String rolecodes=userinfo.get("rolecodes").toString();
        List<String> roleList = new ArrayList<String>();
        for(String str:rolecodes.split(";")){
        	roleList.add(str);
        }
        logger.debug("roleList==="+roleList);
        datapool = mainContext.getDataPool();
        UserContext user=new UserContext();
        user.setLoginTye("ssoperson");
        user.setOperId(userinfo.get("grzh").toString());
        user.setOperName(userinfo.get("xingming").toString());
        user.setOrgId(userinfo.get("instcode").toString());
        user.setOrgName(userinfo.get("instname").toString());
        user.setRoles(roleList);
		// v0.2  新增 start  新增判断是否是统一认证登录属性，属性key为固定值，属性值为@Service注解中的值
		user.setAttribute("usesso","ssoperson");
		// v0.2  新增 end
        // 登录成功   放入公共参数
        // 用户登记号
        datapool.put("yhdjh", userinfo.get("grzh").toString());
        // 用户姓名
		datapool.put("yhxm", userinfo.get("xingming").toString());
		//单位编号
		datapool.put("dwbh", userinfo.get("instcode").toString());
		//单位名称
		datapool.put("dwmc", userinfo.get("instname").toString());
		//证件类型
		datapool.put("zjlx", userinfo.get("zjlx").toString());
		// 证件号码
		datapool.put("zjhm", userinfo.get("zjhm").toString());
		// IP地址
		datapool.put("ipdz", mainContext.getRemoteAddr());
		// 用户手机号
		datapool.put("sjhm",  userinfo.get("sjhm").toString());
		//客户标示
		datapool.put("khbs", IshConstants.LOGIN_KHBS_GR);
		//登录方式
		datapool.put("dlfs", IshConstants.LOGIN_DLFS_GRRZYH);
        
		user.setAttribute(ILoginService.EXTINFOKEY, datapool);
		//脱敏功能使用
		String tuomin_sfqy = ReadProperty.getString("tuomin_sfqy");
		if("1".equals(tuomin_sfqy)){
			TuoMinUtil.tMDataPool(datapool);
		}
        paramConfigImp.loadMaskData(new String[] { "ish.gg.func.systemtype"});
        String systemtype=paramConfigImp.getValByMask("ish.gg.func.systemtype", IshConstants.SYSTEM_WTGR);
		// 子系统标识
        user.setAttribute(IshConstants.LOGIN_SYSTEMTYPE,systemtype);
        // 组装首页的用户信息
        String homedata = HomeData.getPersonHomeData(datapool, user);
		user.setAttribute("homedata", homedata);
		
		user.setAttribute("taskurl", "WAITTASK_00.ydpx");
        user.setAttribute("homeurl", "HOMEPAGE_"+ReadProperty.getString("template")+"_00.ydpx");
        
		/* 组织 UserContext 等 结束 */
		return user;
	}

	@Override
	public String getOperIDKey() {
		return "yhdjh";
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
			//请求单点登录系统退出接口（一般为重定向或http请求）

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("请求统一登出接口失败："+e.getMessage());
			return false;
		}
		return true;

	}
	public static void main(String[] args) {
		DataPool pool = new DataPool();
		pool.put("grzfbtzh", "10002");
		ParseExpression.logicExp(pool, "grzfbtzh!=''");
	}
	
}
