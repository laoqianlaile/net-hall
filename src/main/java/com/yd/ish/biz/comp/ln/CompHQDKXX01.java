package com.yd.ish.biz.comp.ln;

import java.util.List;
import java.util.Map;

import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;

 /**
 * 名称：CompHQDKXX01
 * <p>功能：获取贷款信息<br>
 * @brief 获取贷款信息
 * @author 柏慧敏
 * @version 0.1 2018年6月15日 柏慧敏创建
  * 		0.2 2019年11月20日 许永峰修改  新增回收贷款回收本金总额
 * @note
 */
@Component("CompHQDKXX01")
public class CompHQDKXX01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompHQDKXX01.class);

	@Autowired
	 DP077Service dp077Service;

	@Override
	public int execute() {

		/*入口参数赋值开始*/
		int instance = getInt("_IS");// 实例号
		String jkhtbh=getString("jkhtbh");//借款合同编号
		String jkhtbhsz=getString("jkhtbhsz");//借款合同编号数组
    	/*入口参数赋值结束*/
		// 传递的参数是否正确
		Boolean flag = false;
		// 若借款合同编号数组不为空
		if(jkhtbhsz != null && !"".equals(jkhtbhsz)){
			String[] jkhtbharray = jkhtbhsz.split(",");
			// 循环数组
			for(int i=0;i<jkhtbharray.length;i++){
				// 判断页面选择的借款合同编号是否在数组中
				if(jkhtbh.equals(jkhtbharray[i])){
					// 若在数组中，标志赋值成true，退出循环
					flag = true;
					break;
				}
			}
		}else{
			List<DP077> list = dp077Service.selectBySlh(instance);
			if(list != null && list.size() > 0){
				for(int i=0; i<list.size(); i++){
					if(jkhtbh.equals(list.get(i).getAccnum1())){
						flag = true;
						break;
					}
				}
			}
		}
		// 若flag为true，调用接口获取贷款详细信息
		if(flag){
			super.setValue("jkhtbh", jkhtbh);
			// 调用接口获取贷款详细信息
			logger.info("[+]调用接口 获取贷款详细信息开始");
			XmlResObj data = super.sendExternal("BSP_LN_GETDKXX_01");
			XmlResHead head = data.getHead();
			if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
				throw new TransSingleException(head.getParticular_info());
			}
			Map<String,Object> body = data.getBody();
			String jgmc=body.get("jgmc")==null?"":body.get("jgmc").toString();
			String swtyhmc=body.get("swtyhmc")==null?"":body.get("swtyhmc").toString();
			String jkrxm=body.get("jkrxm")==null?"":body.get("jkrxm").toString();
			String jkrzjhm=body.get("jkrzjhm")==null?"":body.get("jkrzjhm").toString();
			String dkffrq=body.get("dkffrq")==null?"":body.get("dkffrq").toString();
			String dkffe=body.get("dkffe")==null?"":body.get("dkffe").toString();
			String dkqs=body.get("dkqs")==null?"":body.get("dkqs").toString();
			String ydhkr=body.get("ydhkr")==null?"":body.get("ydhkr").toString();
			String dkhkfs=body.get("dkhkfs")==null?"":body.get("dkhkfs").toString();
			String yhke=body.get("yhke")==null?"":body.get("yhke").toString();
			String hkzh=body.get("hkzh")==null?"":body.get("hkzh").toString();
			String htzt=body.get("htzt")==null?"":body.get("htzt").toString();
			String dkye=body.get("dkye")==null?"":body.get("dkye").toString();
			String dkll=body.get("dkll")==null?"":body.get("dkll").toString();
			String sfyq=body.get("sfyq")==null?"":body.get("sfyq").toString();
			String yqbjze=body.get("yqbjze")==null?"":body.get("yqbjze").toString();
			String yqlxze=body.get("yqlxze")==null?"":body.get("yqlxze").toString();
			String fxze=body.get("fxze")==null?"":body.get("fxze").toString();
			String tqhj=body.get("tqhj")==null?"":body.get("tqhj").toString();
			String yddqrq=body.get("yddqrq")==null?"":body.get("yddqrq").toString();
			String dqqc=body.get("dqqc")==null?"":body.get("dqqc").toString();
			String syqs=body.get("syqs")==null?"":body.get("syqs").toString();
			String xdqrq=body.get("xdqrq")==null?"":body.get("xdqrq").toString();
			String tqqbhkje=body.get("tqqbhkje")==null?"":body.get("tqqbhkje").toString();
			String kkrlx=body.get("kkrlx")==null?"":body.get("kkrlx").toString();
			String kkrxm=body.get("kkrxm")==null?"":body.get("kkrxm").toString();
			String kkrzjhm=body.get("kkrzjhm")==null?"":body.get("kkrzjhm").toString();
			String gtjkrxm=body.get("gtjkrxm")==null?"":body.get("gtjkrxm").toString();
			String zdhke=body.get("zdhke")==null?"":body.get("zdhke").toString();
			String dkzxll=body.get("dkzxll")==null?"":body.get("dkzxll").toString();
			String htdkje=body.get("htdkje")==null?"":body.get("htdkje").toString();
			String yqbj=body.get("yqbj")==null?"":body.get("yqbj").toString();
			String yqlx=body.get("yqlx")==null?"":body.get("yqlx").toString();
			String whfx=body.get("whfx")==null?"":body.get("whfx").toString();
			String yqzje=body.get("yqzje")==null?"":body.get("yqzje").toString();
			String xcslx=body.get("xcslx")==null?"":body.get("xcslx").toString();
			String wdqbj=body.get("wdqbj")==null?"":body.get("wdqbj").toString();
			// v0.2 新增 start
			String hsbjze=body.get("hsbjze")==null?"":body.get("hsbjze").toString();
			// v0.2 新增 end

			logger.info("[-]调用接口 获取贷款详细信息结束");

			/*出口参数赋值开始*/
			setOutParam("jgmc",jgmc);//机构名称
			setOutParam("swtyhmc",swtyhmc);//受委托银行名称
			setOutParam("jkrxm",jkrxm);//借款人姓名
			setOutParam("jkrzjhm",jkrzjhm);//借款人证件号码
			setOutParam("dkffrq",dkffrq);//贷款发放日期
			setOutParam("dkffe",dkffe);//贷款发放额
			setOutParam("dkqs",dkqs);//贷款期数
			setOutParam("ydhkr",ydhkr);//约定还款日
			setOutParam("dkhkfs",dkhkfs);//贷款还款方式
			setOutParam("yhke",yhke);//月还款额
			setOutParam("hkzh",hkzh);//还款账号
			setOutParam("htzt",htzt);//合同状态
			setOutParam("dkye",dkye);//贷款余额
			setOutParam("dkll",dkll);//贷款利率
			setOutParam("sfyq",sfyq);//是否逾期
			setOutParam("yqbjze",yqbjze);//逾期本金总额
			setOutParam("yqlxze",yqlxze);//逾期利息总额
			setOutParam("fxze",fxze);//罚息总额
			setOutParam("tqhj",tqhj);//拖欠合计
			setOutParam("dqqc",dqqc);//当前期次
			setOutParam("yddqrq",yddqrq);//约定到期日期
			setOutParam("syqs",syqs);//剩余期数
			setOutParam("xdqrq",xdqrq);//现到期日期
			setOutParam("tqqbhkje",tqqbhkje);//提前全部还款金额
			setOutParam("kkrlx",kkrlx);//扣款人类型
			setOutParam("kkrxm",kkrxm);//扣款人姓名
			setOutParam("kkrzjhm",kkrzjhm);//扣款人证件号码
			setOutParam("gtjkrxm",gtjkrxm);//共同借款人姓名
			setOutParam("zdhke",zdhke);//最低还款额
			setOutParam("dkzxll",dkzxll);//贷款执行利率
			setOutParam("htdkje",htdkje);//合同贷款金额
			setOutParam("yqbj",yqbj);//逾期本金
			setOutParam("yqlx",yqlx);//逾期利息
			setOutParam("whfx",whfx);//未还罚息
			setOutParam("yqzje",yqzje);//逾期总金额
			setOutParam("xcslx",xcslx);//新产生利息
			setOutParam("wdqbj",wdqbj);//未到期本金

			// v0.2 新增 start
			setOutParam("hsbjze",hsbjze);//回收本金总额
			// v0.2 新增 end
			/*出口参数赋值结束*/
		}else{
			logger.error("获取到的借款合同编号："+ jkhtbh);
			throw new TransSingleException("获取贷款信息失败，请确认后重新输入。");
		}
    	return 0;
   }

}
