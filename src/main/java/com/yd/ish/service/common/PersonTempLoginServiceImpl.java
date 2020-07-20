package com.yd.ish.service.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yd.ish.common.util.TuoMinUtil;
import com.yd.svrplatform.util.ReadProperty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yd.basic.service.ILoginService;
import com.yd.biz.engine.TransEngine;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.HttpRequestUtil;
import com.yd.ish.service.HomeData;
import com.yd.org.util.CommonErrorCode;
import com.yd.org.util.EncryptionUtil;
import com.yd.org.util.GetChannelCode;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.context.UserContext;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.exception.PlatRuntimeException;
import com.yd.svrplatform.util.DataPool;

/**
 * 名称：PersonLoginServiceImpl.java
 * <p>
 * 功能：个人临时客户登录管理实现类 <br>
 * 
 * @author 张洪超
 * @version 0.1 2017年9月14日 张洪超创建
 */
@Service("persontemp")
public class PersonTempLoginServiceImpl implements ILoginService {

	@Autowired
	ParamConfigImp paramConfigImp;

	private static final Logger logger = LoggerFactory.getLogger(PersonTempLoginServiceImpl.class);

	@Override
	public UserContext check(Map<String, String> loginmodel) throws PlatRuntimeException {

		// 增加前台参数的特殊字符校验
		if (!HttpRequestUtil.checkRequest(loginmodel)) {
			throw new TransSingleException("对不起，您输入的用户名不合法。");
		}

		String dlyhm = loginmodel.get("dlyhm");
		String dlmm = loginmodel.get("dlmm");
		MainContext mainContext = MainContext.currentMainContext();
		DataPool datapool = new DataPool();

		/* 校验随机验证码 开始 */
		String yzm = loginmodel.get("yzm");
		String yzmkey = loginmodel.get("yzmkey");
		datapool.put("yzm", yzm);
		datapool.put("yzmkey", yzmkey);
		mainContext.setDataPool(datapool);
		// 随机验证码校验
		TransEngine.getInstance().execute("GGTrans_0001", mainContext);
		datapool = mainContext.getDataPool();
		String jg = String.valueOf(datapool.get("jg"));// 获取验证结果
		// 获取验证结果
		if (!StringUtils.equals("0", jg)) {
			throw new TransSingleException(CommonErrorCode.ERROR_INPUT, "验证码");
		}
		/* 校验随机验证码 结束 */

		/* 核验个人客户信息 开始 */
		datapool.put("loginId", dlyhm);
		if (StringUtils.isNotBlank(dlmm)) {
			datapool.put("password", EncryptionUtil.MD5Encode(dlmm));
		} else {
			datapool.put("password", "");
		}
		datapool.put("dlqd", GetChannelCode.getNP());
		mainContext.setDataPool(datapool);
		// 调用登录管理交易 -返回登录用户信息
		String tranid = "wudan".equals(dlyhm) ? "TranDLYZ301" : "TranDLYZ302";
		Map<String, Object> userinfo = TransEngine.getInstance().execute(tranid, mainContext);
		/* 核验个人客户信息 结束 */

		/* 组织 UserContext 等 开始 */
		String rolecodes = userinfo.get("rolecodes").toString();
		List<String> roleList = new ArrayList<String>();
		for (String str : rolecodes.split(";")) {
			roleList.add(str);
		}
		logger.debug("roleList===" + roleList);
		datapool = mainContext.getDataPool();
		UserContext user = new UserContext();
		user.setLoginTye("person");
		user.setOperId(userinfo.get("grzh").toString());
		user.setOperName(userinfo.get("xingming").toString());
		user.setOrgId(userinfo.get("instcode").toString());
		user.setOrgName(userinfo.get("instname").toString());
		user.setRoles(roleList);
		datapool.put("khbs", IshConstants.LOGIN_KHBS_GR);
		datapool.put("dlfs", IshConstants.LOGIN_DLFS_GRRZYH);
		datapool.put("grzh", datapool.get("grzh").toString());
		datapool.put("xingming", userinfo.get("xingming").toString());
		datapool.put("ipdz", mainContext.getRemoteAddr());
		datapool.put("dlyhm", dlyhm);
		datapool.put("dwdjh", userinfo.get("instcode").toString());
		paramConfigImp.loadMaskData(new String[] { "ish.gg.func.systemtype" });
		String systemtype = paramConfigImp.getValByMask("ish.gg.func.systemtype", IshConstants.SYSTEM_WTGR);
		// 子系统标识
		user.setAttribute(IshConstants.LOGIN_SYSTEMTYPE, systemtype);
		user.setAttribute(ILoginService.EXTINFOKEY, datapool);
		//脱敏功能使用
		String tuomin_sfqy = ReadProperty.getString("tuomin_sfqy");
		if("1".equals(tuomin_sfqy)){
			TuoMinUtil.tMDataPool(datapool);
		}
		user.setAttribute("sjhm", userinfo.get("sjhm").toString());
		user.setAttribute("zjlx", userinfo.get("zjlx").toString());
		user.setAttribute("zjhm", userinfo.get("zjhm").toString());
		user.setAttribute("dwdjh", userinfo.get("instcode").toString());

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
		return "grdjh";
	}

}
