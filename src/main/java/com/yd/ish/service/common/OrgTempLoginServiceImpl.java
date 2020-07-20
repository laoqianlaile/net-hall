package com.yd.ish.service.common;

import com.yd.basic.service.ILoginCmdService;
import com.yd.basic.service.ILoginService;
import com.yd.biz.engine.TransEngine;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.HttpRequestUtil;
import com.yd.ish.common.util.TuoMinUtil;
import com.yd.ish.service.HomeData;
import com.yd.org.util.CommonErrorCode;
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
 * 名称：OrgTempLoginServiceImpl.java
 * <p>功能：单位l临时用户名录实现类 <br>
 * @author 王赫
 * @version 0.1	2019年10月16日	王赫创建
 */
@Service("orgtemp")
public class OrgTempLoginServiceImpl implements ILoginService {

	private static final Logger logger = LoggerFactory.getLogger(OrgTempLoginServiceImpl.class);

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

		MainContext mainContext = MainContext.currentMainContext();
		DataPool datapool = new DataPool();

		/*校验随机验证码	 开始*/
		String yzm = loginmodel.get("yzm");
		String yzmkey = loginmodel.get("yzmkey");
		datapool.put("yzm", yzm);
		datapool.put("yzmkey", yzmkey);
		mainContext.setDataPool(datapool);
		//随机验证码校验
		TransEngine.getInstance().execute("GGTrans_0001",mainContext);
		datapool = mainContext.getDataPool();
        String jg = String.valueOf(datapool.get("jg"));//获取验证结果
        //获取验证结果
        if(!StringUtils.equals("0",jg)) {
        	throw new TransSingleException(CommonErrorCode.ERROR_INPUT,"验证码");
        }
		/*校验随机验证码	结束*/

		/* 3. 单位认证经办人登录核验 开始 */
		datapool.put("loginId", dlyhm);
		if(StringUtils.isNotBlank(dlmm)){
			datapool.put("password", EncryptionUtil.MD5Encode(dlmm));
		}else{
			datapool.put("password", "");
		}
		mainContext.setDataPool(datapool);
		Map<String, Object> userinfo = TransEngine.getInstance().execute_NoTran("TranDLYZ403", mainContext);
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
		user.setLoginTye("temp");
		//将单位账号赋值给operid
		user.setOperId(userinfo.get("jbrzjhm").toString());
		user.setOperName(userinfo.get("jbrxm").toString());
		user.setOrgId(userinfo.get("jbrzjhm").toString());
//		user.setOrgName(userinfo.get("dwmc").toString());
		user.setRoles(roleList);
		//客户标识
		datapool.put("khbs", IshConstants.LOGIN_KHBS_DWLS);
		//登陆方式
		datapool.put("dlfs", IshConstants.LOGIN_DLFS_DWLSYH);
		//登陆用户名
		datapool.put("dlyhm", dlyhm);
		//单位名称
//		datapool.put("dwmc",userinfo.get("dwmc").toString());
		datapool.put("jbrxm",userinfo.get("jbrxm").toString());
		datapool.put("jbrzjlx",userinfo.get("jbrzjlx").toString());
		datapool.put("jbrzjhm",userinfo.get("jbrzjhm").toString());
		datapool.put("jbrsjhm",userinfo.get("jbrsjhm").toString());
        user.setAttribute(ILoginService.EXTINFOKEY, datapool);
        //脱敏功能使用
		String tuomin_sfqy = ReadProperty.getString("tuomin_sfqy");
		if("1".equals(tuomin_sfqy)){
			TuoMinUtil.tMDataPool(datapool);
		}
		paramConfigImp.loadMaskData(new String[] { "ish.gg.func.systemtype" });

		//子系统标识  根据此标识获取菜单
		String systemtype = paramConfigImp.getValByMask("ish.gg.func.systemtype", IshConstants.SYSTEM_WTDWLSYH);
        user.setAttribute(IshConstants.LOGIN_SYSTEMTYPE,systemtype);
        // 组装首页的用户信息
        String homedata = HomeData.getOrgTempHomeData(datapool, user);
		user.setAttribute("homedata", homedata);
		/* 组织 UserContext 等 结束 */

		user.setAttribute("taskurl", "WAITTASK_00.ydpx");
		//判断是否为ish04模板，如果是首页路径需修改
		String template = ReadProperty.getString("template");
		if("ish04".equals(template)){
			user.setAttribute("homeurl", "HOMEPAGE_"+template+"_06.ydpx");
		}else{
			user.setAttribute("homeurl", "HOMEPAGE_"+template+"_02.ydpx");
		}

		return user;
	}

	@Override
	public String getOperIDKey() {
//		return "jbrdjh";
		return "jbrzjhm";
	}

}
