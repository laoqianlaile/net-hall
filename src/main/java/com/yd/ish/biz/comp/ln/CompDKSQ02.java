package com.yd.ish.biz.comp.ln;

import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.org.util.IshConstants;

 /**
 * 名称：CompDKSQ02
 * <p>功能：查询并展示当前所选行的信息<br>
 * @brief 查询并展示当前所选行的信息
 * @author 柏慧敏
 * @version 0.1 2018年7月16日 柏慧敏创建
 * @note
 */
@Component("CompDKSQ02")
public class CompDKSQ02 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDKSQ02.class);
	@Autowired
	DP077Service dp077service;
	
	@Override
	public int execute() {

		/*入口参数赋值开始*/
		int instance=getInt("instance");//实例号
		int seqno=getInt("seqno");//序号
		String dpbusitype=getString("dpbusitype");//类型 
    	/*入口参数赋值结束*/
		//定义日期格式yyyy-mm-dd
		SimpleDateFormat sdf = new SimpleDateFormat (IshConstants.GG_DATE_FORMAT);
		logger.info("[+]查询数据库中信息开始");
		DP077 dp077 = new DP077();
		//查询条件赋值
		dp077.setInstance(instance);
		dp077.setSeqno(seqno);
		dp077.setDpbusitype(dpbusitype);
		//从dp077中查询当前行信息
		List<DP077> list=dp077service.selectByCause(dp077);
		DP077 firstlist=list.get(0);
		String xingbie = firstlist.getSex();
		String hyzk = firstlist.getHyzk();
		String jtzz = firstlist.getJtzz();
		String csny = firstlist.getAgentop();
		String dwmc = firstlist.getUnitaccname1();
		String sjhm = firstlist.getSjhm();
		String dwdh = firstlist.getUnitaccnum1();
		String dwdz = firstlist.getXmqp();
		String grzh = firstlist.getAccnum1();
		String jkrxm = firstlist.getAccname1();
		String zhiye = firstlist.getAccname2();
		String khrq = firstlist.getEnddate()==null || firstlist.getEnddate().toString().equals("")?"":sdf.format(firstlist.getEnddate());
		String qshjny = firstlist.getBegym();
		String zhhjny = firstlist.getEndym();
		String grjcjs = firstlist.getBasenum()==null?"":firstlist.getBasenum().toString();
		String yjcehj = firstlist.getAmt1()==null?"":firstlist.getAmt1().toString();
		String grzhye = firstlist.getAmt2()==null?"":firstlist.getAmt2().toString();
		String jkrzjhm = firstlist.getCertinum();
		String jkrzjlx = firstlist.getCertitype();
		String zhiwu = firstlist.getPayvoubank();
		String zhichen = firstlist.getZip();
		String xueli = firstlist.getOnym();
		String grzhzt = firstlist.getAccnum2();
		String jkrlx = firstlist.getFundsouflag();
		String cdgx = firstlist.getProptype();
		logger.info("[-]查询数据库中信息结束");
    	/*出口参数赋值开始*/
		setOutParam("jkrxm",jkrxm);//借款人姓名
		setOutParam("xingbie",xingbie);//性别
		setOutParam("hyzk",hyzk);//婚姻状况
		setOutParam("jtzz",jtzz);//家庭住址
		setOutParam("csny",csny);//出生年月
		setOutParam("dwmc",dwmc);//单位名称
		setOutParam("sjhm",sjhm);//手机号码
		setOutParam("dwdh",dwdh);//单位电话
		setOutParam("dwdz",dwdz);//单位地址
		setOutParam("grzh",grzh);//个人账户
		setOutParam("zhiye",zhiye);//职业
		setOutParam("khrq",khrq);//开户日期
		setOutParam("qshjny",qshjny);//起始汇缴年月
		setOutParam("zhhjny",zhhjny);//终止汇缴年月
		setOutParam("grjcjs",grjcjs);//个人缴存基数
		setOutParam("yjcehj",yjcehj);//月缴存额
		setOutParam("grzhye",grzhye);//个人账户余额
		setOutParam("jkrzjhm",jkrzjhm);//借款人证件号码
		setOutParam("jkrzjlx",jkrzjlx);//借款人证件类型
		setOutParam("zhiwu",zhiwu);//职务
		setOutParam("zhichen",zhichen);//职称
		setOutParam("xueli",xueli);//学历
		setOutParam("grzhzt",grzhzt);//个人账户状态
		setOutParam("jkrlx",jkrlx);//借款人类型
		setOutParam("cdgx",cdgx);//参贷关系
    	/*出口参数赋值结束*/

    	return 0;
   }

}
