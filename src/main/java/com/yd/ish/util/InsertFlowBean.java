package com.yd.ish.util; 

 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yd.basic.expression.IshExpression;
import com.yd.biz.config.FlowBean;
import com.yd.biz.engine.TransHelper;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.jdbc.PersistentBiz;
 
@Service
public class InsertFlowBean{
	
	@Autowired
	PersistentBiz persistentBiz;
	
	@Autowired	
	ParamConfigImp paramConfigImp;
	private static final Logger logger = LoggerFactory.getLogger(InsertFlowBean.class);  
	
	// 参数：ywlx：业务类型（业务编码），rs：人数，bs：笔数，je：金额
	public int dwFlowBean(String ywlx, int rs, int bs, double je) {
		FlowBean flow=new FlowBean();
		flow.setJg(IshExpression.getUserExtInfo("_ORGID"));//设置机构信息
		flow.setYh(IshExpression.getUserExtInfo("_OPERID"));//设置用户信息
		flow.setZjlx(IshExpression.getUserExtInfo("jbrzjlx"));
		flow.setZjhm(IshExpression.getUserExtInfo("jbrzjhm"));
		flow.setQd(paramConfigImp.getValByMask("kf.ish.gg.jbqd", "DWWSJYPT"));//设置渠道
		flow.setJylx(ywlx);//设置交易类型
		if( rs > 0 ){
			flow.setRs(rs);
		}
		if( bs > 0 ){
			flow.setBs(bs);
		}
		if( je > 0 ){
			flow.setJe1(je);
		}
		//调用交易助手类，设置公共流水信息
		TransHelper.setFlowBean(flow);
	
		return 0;
	}
	
	// 参数：ywlx：业务类型（业务编码），rs：人数，bs：笔数，je：金额
	public int grFlowBean(String ywlx, int rs, int bs, double je) {
		logger.debug(ywlx);
		FlowBean flow=new FlowBean();
		flow.setJg(IshExpression.getUserExtInfo("_ORGID"));//设置机构信息
		flow.setYh(IshExpression.getUserExtInfo("_OPERID"));//设置用户信息
		flow.setQd(paramConfigImp.getValByMask("kf.ish.gg.jbqd", "GRWSJYPT"));//设置渠道
		flow.setZjlx(IshExpression.getUserExtInfo("zjlx"));
		flow.setZjhm(IshExpression.getRealUserExtInfo("zjhm"));
		flow.setJylx(ywlx);//设置交易类型
		if( rs > 0 ){
			flow.setRs(rs);
		}
		if( bs > 0 ){
			flow.setBs(bs);
		}
		if( je > 0 ){
			flow.setJe1(je);
		}
		//调用交易助手类，设置公共流水信息
		TransHelper.setFlowBean(flow);
	
		return 0;
	}
}
