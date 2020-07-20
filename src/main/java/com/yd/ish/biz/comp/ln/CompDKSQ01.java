package com.yd.ish.biz.comp.ln;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.basic.expression.IshExpression;
import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransOtherException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;


 /**
 * 名称：CompDKSQ01
 * <p>功能：获取登录人的个人信息<br>
 * @brief 获取登录人的个人信息
 * @author 柏慧敏
 * @version 0.1 2018年7月16日 柏慧敏创建
 * @note
 */
@Component("CompDKSQ01")
public class CompDKSQ01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDKSQ01.class);
	
	@Autowired
	DP077Service dp077service;
	@Autowired
	ParamConfigImp paraConfigImp;
	
	@Override
	public int execute() {

		/*入口参数赋值开始*/
		int instance=getInt("instance");//实例号
		
    	/*入口参数赋值结束*/
		//定义日期格式yyyy-mm-dd
		SimpleDateFormat sdf = new SimpleDateFormat (IshConstants.GG_DATE_FORMAT);
		logger.info("[+]调用接口获取登录人个人信息，开始");
		// 个人登录个人账号取user中的grzh
		if(IshConstants.LOGIN_KHBS_GR.equals(IshExpression.getUserExtInfo("khbs"))){
			super.setValue("grzh", IshExpression.getRealUserExtInfo("grzh"));
		}
		// 调用接口获取登录人个人信息
		XmlResObj grxxdata = super.sendExternal("BSP_DP_GETGRXX_01");
		XmlResHead grxxhead = grxxdata.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(grxxhead.getParticular_code())) {
			throw new TransSingleException(grxxhead.getParticular_info());
		}
		// 调用综服接口从body中获取个人信息
		Map<String,Object> body = grxxdata.getBody();
		String xingming=body.get("xingming")==null?"":body.get("xingming").toString();
		String zjlx=body.get("zjlx")==null?"":body.get("zjlx").toString();
		String zjhm=body.get("zjhm")==null?"":body.get("zjhm").toString();
		String xingbie=body.get("xingbie")==null?"":body.get("xingbie").toString();
		String csny=body.get("csny")==null?"":body.get("csny").toString();
		String hyzk=body.get("hyzk")==null?"":body.get("hyzk").toString();
		String zhiye=body.get("zhiye")==null?"":body.get("zhiye").toString();
		String zhiwu=body.get("zhiwu")==null?"":body.get("zhiwu").toString();
		String zhichen=body.get("zhichen")==null?"":body.get("zhichen").toString();
		String xueli=body.get("xueli")==null?"":body.get("xueli").toString();
		String sjhm=body.get("sjhm")==null?"":body.get("sjhm").toString();
		String dwdh=body.get("dwdh")==null?"":body.get("dwdh").toString();
		String dwmc=body.get("dwmc")==null?"":body.get("dwmc").toString();
		String jtzz=body.get("jtzz")==null?"":body.get("jtzz").toString();
		String dwdz=body.get("dwdz")==null?"":body.get("dwdz").toString();

		String grzhzt=body.get("grzhzt")==null?"":body.get("grzhzt").toString();
		String khrq=body.get("khrq")==null?"":body.get("khrq").toString();
		String qshjny=body.get("qshjny")==null?"":body.get("qshjny").toString();
		String jzny=body.get("jzny")==null?"":body.get("jzny").toString();
		String grjcjs=body.get("grjcjs")==null?"":body.get("grjcjs").toString();
		String yjce=body.get("yjce")==null?"":body.get("yjce").toString();
		String grzhye=body.get("grzhye")==null?"":body.get("grzhye").toString();
		logger.info("[-]调用接口获取登录人个人信息，结束");
		
		
		DP077 dp077=new DP077(); 
		dp077.setDpbusitype("10");
		dp077.setInstance(instance);
		//判断数据库是否已经有登录人的信息（暂存用）
		List<DP077> list=dp077service.selectByCause(dp077);
		//若第一次进入流程，将获取到的信息存入临时表dp077中，否则无操作
		if(list != null && list.size()==0){
			logger.info("[+]将公积金信息与个人信息存入临时表中，开始");
			dp077.setSex(xingbie);
			dp077.setAgentop(csny);
			dp077.setHyzk(hyzk);
			dp077.setJtzz(jtzz);
			dp077.setUnitaccname1(dwmc);
			dp077.setSjhm(sjhm);
			dp077.setUnitaccnum1(dwdh);
			dp077.setXmqp(dwdz);
			// 个人登录个人账号取user中的grzh
			if(IshConstants.LOGIN_KHBS_GR.equals(IshExpression.getUserExtInfo("khbs"))){
				dp077.setAccnum1(IshExpression.getRealUserExtInfo("grzh"));
				dp077.setAccname1(IshExpression.getRealUserExtInfo("xingming"));
			}else if(IshConstants.LOGIN_KHBS_KFS.equals(IshExpression.getUserExtInfo("khbs"))){
				dp077.setAccnum1(super.getMainContext().getDataPool().getString("grzh"));
				dp077.setAccname1(xingming);
			}
			dp077.setAccname2(zhiye);
			try {
				dp077.setEnddate(sdf.parse(khrq));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			dp077.setBegym(qshjny);
			dp077.setEndym(jzny);
			dp077.setBasenum(new BigDecimal(grjcjs));
			dp077.setAmt1(new BigDecimal(yjce));
			dp077.setAmt2(new BigDecimal(grzhye));
			dp077.setCertinum(zjhm);
			dp077.setCertitype(zjlx);
			dp077.setFundsouflag(paraConfigImp.getValByMask("bsp.ln.loaneetype", "0"));//借款人类型：0-借款人；1-共同借款人
			dp077.setProptype(paraConfigImp.getValByMask("bsp.ln.cdgx", "0"));//参贷关系：0-本人
			dp077.setPayvoubank(zhiwu);
			
			dp077.setZip(zhichen);
			dp077.setOnym(xueli);
			dp077.setAccnum2(grzhzt);
			dp077.setDpbusitype("10");//类型：借款申请人信息
			dp077.setSeqno(1);
			try{
				dp077service.insert(dp077);
				dp077.setDpbusitype("11");//类型：共有产权人信息
				dp077service.insert(dp077);
			}catch (RuntimeException e) {
				e.printStackTrace();
				logger.error("插入数据库出错：" + e.getMessage(), e);
				throw new TransOtherException("系统错误，请查看日志！");
			}
			logger.info("[-]将公积金信息与个人信息存入临时表中，结束");
		}
		
		
    	/*出口参数赋值开始*/

    	/*出口参数赋值结束*/

    	return 0;
   }

}
