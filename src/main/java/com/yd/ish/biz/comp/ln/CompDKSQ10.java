package com.yd.ish.biz.comp.ln;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;

 /**
 * 名称：CompDKSQ10
 * <p>功能：提交前对信息进行校验<br>
 * @brief 提交前对信息进行校验
 * @author 柏慧敏
 * @version 0.1 2018年7月20日 柏慧敏创建
 * @note
 */
@Component("CompDKSQ10")
public class CompDKSQ10 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDKSQ10.class);
	@Autowired
	DP077Service dp077service;
	@Autowired
	ParamConfigImp paraConfigImp;
	@Override
	public int execute() {

		/*入口参数赋值开始*/
		int instance=getInt("instance");//实例号
    	/*入口参数赋值结束*/

		DP077 dp077=new DP077();
		dp077.setInstance(instance);
		dp077.setDpbusitype("10");//10-贷款申请人信息
		//查询表中申请人信息
		List<DP077> sqrlist=dp077service.selectRepeatRyxx(dp077);
		if(sqrlist != null && sqrlist.size()>0){
			//证件类型
			String zjlx="";
			//证件号码
			String zjhm="";
			//证件类型汉字
			String zjlxhz="";
			String erromessage="";
			//循环对比查询结果中证件类型、证件号码信息
			for(int i=0;i<sqrlist.size();i++){
				zjlx=sqrlist.get(i).getCertitype();
				zjhm=sqrlist.get(i).getCertinum();
				zjlxhz=paraConfigImp.getVal("bsp.pb.zjlx."+zjlx);
				logger.error("证件类型："+zjlxhz+"证件号码:"+zjhm+"人员信息重复");
				erromessage="["+zjlxhz+"]["+zjhm+"]"+erromessage;
			}
			throw new TransSingleException("申请人列表中存在相同的人员信息"+erromessage+"请确认后重新提交");
		}
    	/*出口参数赋值开始*/

    	/*出口参数赋值结束*/

    	return 0;
   }

}
