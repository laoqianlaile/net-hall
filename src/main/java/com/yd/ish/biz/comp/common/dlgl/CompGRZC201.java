package com.yd.ish.biz.comp.common.dlgl;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.org.util.CommonErrorCode;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.DataPool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *  个人注册
 * 名称：CompGRZC201.java
 * <p>功能：登录功能-个人用户登录验证返回用户信息 <br> 
 * @author 张洪超
 * @version 0.1	2017年8月31日	张洪超创建
 * 		   V0.2 2018年10月22日     柏慧敏修改    1.根据贯标信息项修改字段id 2.角色编码由detail标签中返回改为在body标签中返回
 *
 */
@Component("CompGRZC201")
public class CompGRZC201 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompGRZC201.class);

	private static final String INTERFACE_ID="getGRZC";//调用的接口

	@Override
	public int execute() {

		/*入口参数赋值开始*/
		String loginId = getString("zjhm");//登录id
		String zjhm = getString("zjhm");//登录id
		String sjhm = getString("sjhm");//登录id
		String zdyyhm = getString("zdyyhm");//登录id
		String xingming = getString("xingming");//登录id
//		String zjlx=getString("zjlx");//登录id
		String zjlx="01";//登录id
		//String dxyzm=getString("dxyzm");//登录id
		String pwd = getString("gjjmm");//登录id
		String password = getString("gjjmm");//密码
    	/*入口参数赋值结束*/
		/*if(StringUtils.isBlank(loginId)){
			throw new TransSingleException(CommonErrorCode.ERROR_NOT_NULL,"用户名");
		}
		if(StringUtils.isBlank(password)){
			throw new TransSingleException(CommonErrorCode.ERROR_NOT_NULL,"密码");
		}*/
		String grzh="";

		String instcode="";
		String instname="";
		String roleCodes="";
		//登录验证返回用户和角色信息
		logger.info("[+]调用核心用户验证返回用户信息接口"+INTERFACE_ID+"开始");
		DataPool dataPool = MainContext.currentMainContext().getDataPool();
		dataPool.put("zjhm", zjhm);
		dataPool.put("sjhm", sjhm);
		dataPool.put("zdyyhm", zdyyhm);
		dataPool.put("xingming", xingming);
		dataPool.put("zjlx", zjlx);
		//dataPool.put("dxyzm", dxyzm);
		dataPool.put("pwd", pwd);
		dataPool.put("password", password);
		dataPool.put("logintype","02");
		dataPool.put("loginId",zjhm);
		dataPool.put("flag","40");//渠道标识
		XmlResObj data =  super.sendExternal(INTERFACE_ID,false);
	    XmlResHead head=data.getHead();
	    if (XmlResHead.TR_SUCCESS.equals(head.getParticular_code())){


	    	logger.info("[-]调用核心用户验证返回用户信息接口"+INTERFACE_ID+"结束");
	    }else{
	    	logger.info("[-]调用核心用户验证返回用户信息接口"+INTERFACE_ID+"结束");
	        throw new TransSingleException(head.getParticular_code(),head.getParticular_info());
	    }


    	return 0;
   }

}
