package com.yd.ish.service.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yd.ish.common.util.TuoMinUtil;
import com.yd.org.util.CommonErrorCode;
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
import com.yd.org.util.EncryptionUtil;
import com.yd.org.util.GetChannelCode;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.context.UserContext;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.exception.PlatRuntimeException;
import com.yd.svrplatform.util.DataPool;

/**
 * 名称：DeveloperLoginServiceImpl.java
 * <p>
 * 功能：开发商登录管理控制 <br>
 * 
 * @author 武丹
 * @version 0.1 2017年11月24日 创建
 */
@Service("developer")
public class DeveloperLoginServiceImpl implements ILoginService {

	private static final Logger logger = LoggerFactory.getLogger(DeveloperLoginServiceImpl.class);

	@Autowired
	ParamConfigImp paramConfigImp;

	/**
	 * 登录校验
	 * 
	 * @throws PlatRuntimeException
	 **/
	@Override
	public UserContext check(Map<String, String> loginmodel) throws PlatRuntimeException {

		// 增加前台参数的特殊字符校验
		if (!HttpRequestUtil.checkRequest(loginmodel)) {
			throw new TransSingleException("对不起，您输入的用户名不合法。");
		}
		/*校验随机验证码	 开始*/
		String yzm = loginmodel.get("yzm");
		String yzmkey = loginmodel.get("yzmkey");
		MainContext mainContext = MainContext.currentMainContext();
		DataPool datapool = new DataPool();

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

		/*核验开发商信息	 开始*/
		String dlyhm = loginmodel.get("dlyhm");
		String dlmm = loginmodel.get("dlmm");

		datapool.put("loginId", dlyhm);
        if(StringUtils.isNotBlank(dlmm)){
			datapool.put("password", EncryptionUtil.MD5Encode(dlmm));
		}else{
			datapool.put("password", "");
		}
        // 渠道
        datapool.put("dlqd", GetChannelCode.getNP()); 
        mainContext.setDataPool(datapool);
        
        //调用登录管理交易 -返回登录用户信息
        Map<String,Object> userinfo = TransEngine.getInstance().execute("TranDLYZ601",mainContext);
        /*核验开发商信息	结束*/
        
		/* 组织 UserContext 等 开始 */
        String rolecodes=userinfo.get("rolecodes").toString();
        List<String> roleList = new ArrayList<String>();
        for(String str:rolecodes.split(";")){
        	roleList.add(str);
        }
		datapool.put("rolecodes", rolecodes);
		mainContext.setDataPool(datapool);
		logger.debug("roleList===" + roleList);
		UserContext user = new UserContext();
		user.setLoginTye("developer");
		user.setOperId(userinfo.get("kfsdjh").toString());
		user.setOperName(userinfo.get("jbrxm").toString());
		user.setOrgId(userinfo.get("kfsdjh").toString());
		user.setOrgName(userinfo.get("kfsmc").toString());
		user.setRoles(roleList);
		// 开发商地址
		datapool.put("kfsdz", userinfo.get("kfsdz").toString()); 
		// 邮政编码
		datapool.put("yzbm", userinfo.get("yzbm").toString()); 
		// 开发商电话
		datapool.put("kfsdh", userinfo.get("kfsdh").toString()); 
		// 客户标识
		datapool.put("khbs", IshConstants.LOGIN_KHBS_KFS); 
		// 登录方式
		datapool.put("dlfs", IshConstants.LOGIN_DLFS_KFS); 
		// 登录用户名
		datapool.put("dlyhm", dlyhm);
		//脱敏功能使用
		String tuomin_sfqy = ReadProperty.getString("tuomin_sfqy");
		if("1".equals(tuomin_sfqy)){
			TuoMinUtil.tMDataPool(datapool);
		}
		// 组装首页的用户信息
		String homedata = HomeData.getKfsHomeData(datapool, user);
		user.setAttribute("homedata", homedata);
		paramConfigImp.loadMaskData(new String[] { "ish.gg.func.systemtype" });
		String systemtype = paramConfigImp.getValByMask("ish.gg.func.systemtype", IshConstants.SYSTEM_WTKFS);
		// 子系统标识
		user.setAttribute(IshConstants.LOGIN_SYSTEMTYPE, systemtype);
		// 首页公共页面
		user.setAttribute(ILoginService.EXTINFOKEY, datapool);

		user.setAttribute("taskurl", "WAITTASK_00.ydpx");
		//判断是否为ish04模板，如果是首页路径需修改
		String template = ReadProperty.getString("template");
		if("ish04".equals(template)){
			user.setAttribute("homeurl", "HOMEPAGE_"+template+"_04.ydpx");
		}else{
			user.setAttribute("homeurl", "HOMEPAGE_"+template+"_00.ydpx");
		}
		/* 组织 UserContext 等 结束 */
		return user;
	}

	@Override
	public String getOperIDKey() {
		return "yhdjh";
	}

}
