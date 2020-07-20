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

import com.yd.basic.service.ILoginCmdService;
import com.yd.basic.service.ILoginService;
import com.yd.biz.engine.TransEngine;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.HttpRequestUtil;
import com.yd.ish.service.HomeData;
import com.yd.org.util.CommonErrorCode;
import com.yd.org.util.EncryptionUtil;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.context.UserContext;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.exception.PlatRuntimeException;
import com.yd.svrplatform.util.DataPool;

/**
 * 单位用户名密码登录
 * 
 * @author
 * 修改：v0.2 20181016 单位登录时，将operid由经办人登记号改成单位账号（大部分单位没有经办人登记号字段）  by柏慧敏
 *
 */
@Service("orgauth")
public class OrgAuthLoginServiceImpl implements ILoginService, ILoginCmdService {

	private static final Logger logger = LoggerFactory.getLogger(OrgAuthLoginServiceImpl.class);

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
		//随机验证码是否校验标识
		String checkYzm = loginmodel.get("checkYzm");
		//checkYzm为1时校验验证码
		if("1".equals(checkYzm)){
			TransEngine.getInstance().execute("GGTrans_0001",mainContext); 
			datapool = mainContext.getDataPool();
		}else{
			datapool = mainContext.getDataPool();
			datapool.put("jg","0");
		}
		String jbrsjhm = loginmodel.get("jbrsjhm");
		String dxyzm = loginmodel.get("dxyzm");
		datapool.put("jbrsjhm",jbrsjhm);
		datapool.put("dxyzm",dxyzm);
		//调用短信验证码交易
		Map<String,Object> dxMap = TransEngine.getInstance().execute("TranDXYZM3",mainContext);
		if(!"000000".equals(dxMap.get("recode"))){
			throw new TransSingleException(CommonErrorCode.ERROR_INPUT,"短信验证码");
		}
        String jg = String.valueOf(datapool.get("jg"));//获取验证结果
        //获取验证结果
        if(!StringUtils.equals("0",jg)) {
        	throw new TransSingleException(CommonErrorCode.ERROR_INPUT,"验证码");
        }
		/*校验随机验证码	结束*/

		/* 3. 单位认证经办人登录核验 开始 */
		datapool.put("loginId", dlyhm);
		if(StringUtils.isNotBlank(dlmm)){
//			datapool.put("password", EncryptionUtil.MD5Encode(dlmm));
			datapool.put("password", dlmm);
		}else{
			datapool.put("password", "");
		}
		mainContext.setDataPool(datapool);
		Map<String, Object> userinfo = TransEngine.getInstance().execute_NoTran("TranDLYZ401", mainContext);
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
		user.setAttribute(ILoginService.EXTINFOKEY, datapool);
		//单位邮编
		datapool.put("dwyb", userinfo.get("dwyb").toString());
		//社会诚信代码
		datapool.put("shcxdm", userinfo.get("shcxdm").toString());
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
