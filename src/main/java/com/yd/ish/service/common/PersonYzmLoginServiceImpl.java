package com.yd.ish.service.common;

import com.yd.basic.service.ILoginService;
import com.yd.biz.engine.TransEngine;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.HttpRequestUtil;
import com.yd.ish.common.util.TuoMinUtil;
import com.yd.ish.service.HomeData;
import com.yd.org.util.EncryptionUtil;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.context.UserContext;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.exception.PlatRuntimeException;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.ReadProperty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 个人短信验证码登录
 * 
 * @author 柏慧敏
 *
 */
@Service("persondxyzm")
public class PersonYzmLoginServiceImpl implements ILoginService {

	private static final Logger logger = LoggerFactory.getLogger(PersonYzmLoginServiceImpl.class);

	@Autowired
	ParamConfigImp paramConfigImp;

	@Override
	public UserContext check(Map<String, String> loginmodel) throws PlatRuntimeException {

		// 1. 增加前台参数的特殊字符校验
		if (!HttpRequestUtil.checkRequest(loginmodel)) {
			throw new TransSingleException("对不起，您输入的用户名不合法。");
		}

		String dlyhm = loginmodel.get("dlyhm");
		String dlmm = loginmodel.get("dlmm");
		String ylsjh = loginmodel.get("ylsjh");
		String yzm = loginmodel.get("yzm");
		MainContext mainContext = MainContext.currentMainContext();
		DataPool datapool = new DataPool();

		/* 2. 个人登录核验 开始 */
		datapool.put("loginId", dlyhm);
		if (StringUtils.isNotBlank(dlmm)) {
			datapool.put("password", EncryptionUtil.MD5Encode(dlmm));
		} else {
			datapool.put("password", "");
		}
		datapool.put("yzm", yzm);
		datapool.put("sjhm", ylsjh);
		datapool.put("flag", "getyhxx");
		mainContext.setDataPool(datapool);
		Map<String, Object> userinfo = TransEngine.getInstance().execute_NoTran("TranDLYZ701", mainContext);
		/* 个人登录核验 结束 */

		/* 3. 获得个人角色信息 开始 */
		String rolecodes = userinfo.get("rolecodes").toString();
		List<String> roleList = new ArrayList<String>();
		for (String str : rolecodes.split(";")) {
			roleList.add(str);
		}
		logger.debug("roleList===" + roleList);
		/* 获得个人角色信息 结束 */

		/* 4. 组织 UserContext 等 开始 */
		datapool = mainContext.getDataPool();

		UserContext user = new UserContext();
		user.setLoginTye("person");
		user.setOperId(userinfo.get("grzh").toString());
		user.setOperName(userinfo.get("xingming").toString());
		user.setOrgId(userinfo.get("instcode").toString());
		user.setOrgName(userinfo.get("instname").toString());
		user.setRoles(roleList);
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
		datapool.put("dlfs", IshConstants.LOGIN_DLFS_GRDXYZM);
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
		return "jbrdjh";
	}
}
