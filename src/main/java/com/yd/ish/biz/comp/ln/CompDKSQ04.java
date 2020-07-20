package com.yd.ish.biz.comp.ln;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.basic.util.Constants;
import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransOtherException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.org.util.IshConstants;

 /**
 * 名称：CompDKSQ04
 * <p>功能：添加借款人<br>
 * @brief 添加借款人
 * @author 柏慧敏
 * @version 0.1 2018年7月17日 柏慧敏创建
 * @note
 */
@Component("CompDKSQ04")
public class CompDKSQ04 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDKSQ04.class);
	@Autowired
	DP077Service dp077service;
	@Override
	public int execute() {

		/*入口参数赋值开始*/
		int instance=getInt("instance");//实例号
		String jkrlx=getString("jkrlx");//借款人类型
		String cdgx=getString("cdgx");//参贷关系
		String jkrxm=getString("jkrxm");//借款人姓名
		String xingbie=getString("xingbie");//性别
		String jkrzjlx=getString("jkrzjlx");//借款人证件类型
		String jkrzjhm=getString("jkrzjhm");//借款人证件号码
		String csny=getString("csny");//出生年月
		String hyzk=getString("hyzk");//婚姻状况
		String zhiye=getString("zhiye");//职业
		String zhiwu=getString("zhiwu");//职务
		String zhichen=getString("zhichen");//职称
		String xueli=getString("xueli");//学历
		String sjhm=getString("sjhm");//手机号码
		String dwdh=getString("dwdh");//单位电话
		String jtzz=getString("jtzz");//家庭住址
		String dwmc=getString("dwmc");//单位名称
		String dwdz=getString("dwdz");//单位地址
		String grzh=getString("grzh");//个人账号
		String grzhzt=getString("grzhzt");//个人账户状态
		String khrq=getString("khrq");//开户日期
		String qshjny=getString("qshjny");//起始汇缴年月
		String jzny=getString("jzny");//缴至年月
		String grjcjs=getString("grjcjs");//个人缴存基数
		String yjce=getString("yjce");//月缴存额
		String grzhye=getString("grzhye");//个人账户余额
		int seqno=getInt("seqno");//序号
    	/*入口参数赋值结束*/
		
		//定义日期格式yyyy-mm-dd
		SimpleDateFormat sdf = new SimpleDateFormat (IshConstants.GG_DATE_FORMAT);
		logger.info("[+]新增信息插入数据库dp077开始");
		DP077 dp077=new DP077();
		dp077.setSeqno(seqno);
		dp077.setInstance(instance);
		dp077.setDpbusitype("10");
		//根据实例号和序号查询数据库中是否有当前信息
		List<DP077> jkrlist=dp077service.selectByCause(dp077);
		DP077 dp077repeat=new DP077();
		dp077repeat.setInstance(instance);
		dp077repeat.setAccname1(jkrxm);
		dp077repeat.setCertinum(jkrzjhm);
		dp077repeat.setCertitype(jkrzjlx);
		List<DP077> jkrlistrepeat=dp077service.selectByCause(dp077repeat);
		if((jkrlist == null || jkrlist.size() == 0) && (jkrlistrepeat != null && jkrlistrepeat.size() != 0)){
			throw new TransSingleException("当前借款人信息已存在，不能重复保存。");
		}
		//页面数据赋值给dp077中相应的字段
		dp077.setSex(xingbie);
		dp077.setAgentop(csny);
		dp077.setHyzk(hyzk);
		dp077.setJtzz(jtzz);
		dp077.setUnitaccname1(dwmc);
		dp077.setSjhm(sjhm);
		dp077.setUnitaccnum1(dwdh);
		dp077.setXmqp(dwdz);
		dp077.setAccnum1(grzh);
		dp077.setAccname1(jkrxm);
		dp077.setAccname2(zhiye);
		dp077.setBegym(qshjny);
		dp077.setEndym(jzny);
		dp077.setCertinum(jkrzjhm);
		dp077.setCertitype(jkrzjlx);
		dp077.setFundsouflag(jkrlx);//借款人类型：0-借款人；1-共同借款人
		dp077.setProptype(cdgx);//参贷关系：0-本人
		dp077.setPayvoubank(zhiwu);
		dp077.setInstance(instance);
		dp077.setZip(zhichen);
		dp077.setOnym(xueli);
		dp077.setAccnum2(grzhzt);
		dp077.setBasenum(grjcjs.equals("")?new BigDecimal("0"):new BigDecimal(grjcjs));
		dp077.setAmt1(yjce.equals("")?new BigDecimal("0"):new BigDecimal(yjce));
		dp077.setAmt2(grzhye.equals("")?new BigDecimal("0"):new BigDecimal(grzhye));
		try{
			//若数据库已有当前数据，将页面数据更新至数据库中
			if(jkrlist != null && jkrlist.size()>0){
				try {
					dp077.setEnddate(khrq==""?sdf.parse(Constants.GG_DATE_DEFAULT):sdf.parse(khrq));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				dp077service.updateBySeqnoInstance(dp077);
			}
			//若数据库没有当前数据，将页面数据插入至数据库中
			else{
				//根据实例号查询数据库序号，本次新增序号为数据库中已有最大序号加1
				DP077 dp077cxtj=new DP077();
				dp077cxtj.setDpbusitype("10");
				dp077cxtj.setInstance(instance);
				List<DP077> list=dp077service.selectByCause(dp077cxtj);
				int listlength = 0;
				if(list != null && list.size()>0){
					listlength=list.size();
					dp077.setSeqno(list.get(listlength-1).getSeqno()+1);
				}
				if(khrq != ""){
					try {
						dp077.setEnddate(sdf.parse(khrq));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				//将新增信息插入数据库中
				dp077service.insert(dp077);
			}
		}catch (RuntimeException e) {
			e.printStackTrace();
			logger.error("插入数据库出错：" + e.getMessage(), e);
			throw new TransOtherException("系统错误，请查看日志！");
		}
		logger.info("[-]新增信息插入数据库dp077结束");
		
    	/*出口参数赋值开始*/

    	/*出口参数赋值结束*/

    	return 0;
   }

}
