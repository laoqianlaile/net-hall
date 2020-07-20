package com.yd.ish.service.impl.org;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yd.svrplatform.util.ReadProperty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yd.basic.service.ILoginService;
import com.yd.biz.engine.TransEngine;
import com.yd.biz.exception.TransSingleException;
import com.yd.org.util.CommonErrorCode;
import com.yd.org.util.GetChannelCode;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.context.UserContext;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.exception.PlatRuntimeException;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;

/**
 * 名称：ManagerLoginServiceImpl.java
 * <p>功能：后台管理录登录管理控制 <br> 
 * @author 张洪超
 * @version 0.1	2017年8月31日	张洪超创建
 */
@Service("manager")
public class ManagerLoginServiceImpl implements ILoginService {
	
	private static final Logger logger = LoggerFactory.getLogger(ManagerLoginServiceImpl.class);
	
	@Autowired
	ParamConfigImp paramConfigImp;
	
	/**
	 * 登录校验
	 * @param Map<String, String>
	 * @throws PlatRuntimeException
	 **/
	@Override
	public UserContext check(Map<String, String> loginmodel) throws PlatRuntimeException {

		String dlyhm=loginmodel.get("dlyhm");
		String dlmm=loginmodel.get("dlmm");
        
		MainContext mainContext = MainContext.currentMainContext();
		DataPool datapool = new DataPool();
		
		/*校验随机验证码	 开始*/
		String yzm=loginmodel.get("yzm");
		String yzmkey=loginmodel.get("yzmkey");
		
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
		
		/*核验个人客户信息	 开始*/
        datapool.put("loginId", dlyhm);
        datapool.put("password", dlmm);
        datapool.put("dlqd", GetChannelCode.getNP());
        mainContext.setDataPool(datapool);
        //调用登录管理交易 -返回登录用户信息
        Map<String,Object> userinfo =TransEngine.getInstance().execute("TranDLYZ101",mainContext);
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
        user.setLoginTye("manager");
        user.setOperId(userinfo.get("managerId").toString());
        user.setOperName(userinfo.get("managerName").toString());
        user.setOrgId(userinfo.get("instcode").toString());
        user.setOrgName(userinfo.get("instname").toString());
        user.setRoles(roleList);
        // 登录成功 放入公共参数
        datapool.put("yhdjh", userinfo.get("managerId").toString());
        datapool.put("yhxm", userinfo.get("managerName").toString());
        
        datapool.put("khbs", IshConstants.LOGIN_KHBS_GLY);
        datapool.put("dlfs", IshConstants.LOGIN_DLFS_GLY);
       
        paramConfigImp.loadMaskData(new String[] { "ish.gg.func.systemtype"});
        String systemtype=paramConfigImp.getValByMask("ish.gg.func.systemtype", IshConstants.SYSTEM_WTXTGL);
		//子系统标识
        user.setAttribute(IshConstants.LOGIN_SYSTEMTYPE,systemtype);
        // 组装首页的用户信息
        String homedata = getHomeData(datapool, user);
		user.setAttribute("homedata", homedata);
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

        /*组织 UserContext 等	结束*/
		return user;
	}
	
	/**
	 * 获得首页展示的个人信息
	 * 
	 * @param datapool
	 * @param user
	 * @return
	 */
	private String getHomeData(DataPool datapool, UserContext user) {
		
		List<Map<String,Object>> homeData = new ArrayList<Map<String,Object>>();
		Map<String,Object> m1=new HashMap<String,Object>();
		m1.put("label", "管理员证件号码");
		m1.put("value", "110******0041");
		m1.put("cols", 3);
		homeData.add(m1);
		Map<String,Object> m2=new HashMap<String,Object>();
		m2.put("label", "管理员登记号");
		m2.put("value", datapool.get("yhdjh").toString());
		m2.put("cols", 3);
		homeData.add(m2);
		Map<String,Object> m6=new HashMap<String,Object>();
		m6.put("label","单位登记号");
		m6.put("value", "20119743");
		m6.put("cols", 3);
		homeData.add(m6);
		Map<String,Object> m3=new HashMap<String,Object>();
		m3.put("label","单位名称");
		m3.put("value", "华信永道（北京）科技股份有限公司");
		m3.put("cols", 6);
		homeData.add(m3);
		Map<String,Object> m4=new HashMap<String,Object>();
		m4.put("label","公司地址");
		m4.put("value", "北京市朝阳区北苑路甲13号1号楼");
		m4.put("cols", 6);
		homeData.add(m4);
		Map<String,Object> m5=new HashMap<String,Object>();
		m5.put("name", user.getOperName());
		m5.put("info", homeData);
		
		return JsonUtil.getJsonString(m5);
	}
	
	@Override
	public String getOperIDKey() {
		return "yhdjh";
	}
	
}
