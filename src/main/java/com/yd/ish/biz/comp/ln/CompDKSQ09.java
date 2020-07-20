package com.yd.ish.biz.comp.ln;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransOtherException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.svrplatform.comm_mdl.log.YDLogger;
import com.yd.svrplatform.util.ReadProperty;

 /**
 * 名称：CompDKSQ09
 * <p>功能：复制借款人<br>
 * @brief 复制借款人
 * @author 柏慧敏
 * @version 0.1 2018年7月19日 柏慧敏创建
 * @note
 */
@Component("CompDKSQ09")
public class CompDKSQ09 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompDKSQ09.class);
	/** 数据库类型 */
	private static final String DB_TYPE = ReadProperty.getString("dbType");
	
	@Autowired
	DP077Service dp077service;
	
	@Override
	public int execute() {

		/*入口参数赋值开始*/
		int instance=getInt("instance");//实例号
		String dpbusitype=getString("dpbusitype");//类型
    	/*入口参数赋值结束*/

		DP077 dp077=new DP077();
		dp077.setInstance(instance);
		dp077.setDpbusitype("10");//10-借款申请人
		//查询借款人信息
		List<DP077> list=dp077service.selectByCause(dp077);
		
		
		if(list != null && list.size()>0){
			// 总条数
			int total = 0;
			List<DP077> arraylist = new ArrayList<DP077>();
			//根据实例号和类型查询数据库序号，本次新增序号为数据库中已有最大序号加1
			dp077.setDpbusitype(dpbusitype);
			List<DP077> listseqno=dp077service.selectByCause(dp077);
			//序号查询结果长度
			int listlength = 0;
			//序号
			int seqno=1;
			//若查询的结果不为空
			if(listseqno != null && listseqno.size()>0){
				//序号赋值为查询出的序号加1
				listlength=listseqno.size();
				seqno = listseqno.get(listlength-1).getSeqno()+1;
			}
			//申请人查询结果若不为空

			for(int i=0;i<list.size();i++){
				//姓名、证件类新、证件号码赋值
				dp077.setAccname1(list.get(i).getAccname1());
				dp077.setCertinum(list.get(i).getCertinum());
				dp077.setCertitype(list.get(i).getCertitype());
				//查询同一类型中是否已有相同的人员信息
				List<DP077> ifsame=dp077service.selectByCause(dp077);
				//若无同一人员信息将其他信息进行赋值
				if(ifsame==null || ifsame.size()<=0){
					DP077 dp077insert = new DP077();
					dp077insert.setInstance(instance);
					dp077insert.setDpbusitype(dpbusitype);
					dp077insert.setAccname1(list.get(i).getAccname1());
					dp077insert.setCertinum(list.get(i).getCertinum());
					dp077insert.setCertitype(list.get(i).getCertitype());
					dp077insert.setSex(list.get(i).getSex());
					dp077insert.setSeqno(seqno++);
					//抵押人信息
					if("12".equals(dpbusitype)){
						dp077insert.setUnitaccname1(list.get(i).getUnitaccname1());
						dp077insert.setAccnum1(list.get(i).getSjhm());
					}
					//共有权人信息
					else if("11".equals(dpbusitype)){
						dp077insert.setFundsouflag(list.get(i).getFundsouflag());//借款人类型：0-借款人；1-共同借款人
						dp077insert.setProptype(list.get(i).getProptype());//参贷关系：0-本人
						dp077insert.setSjhm(list.get(i).getSjhm());
					}
					//总数加一
					total++;
					arraylist.add(dp077insert);
				}
			}
			//若总数量大于0，将list中数据存入数据库
			if(total>0){
				try{
					// 批量插入数据库之前临时关闭日志输出
//					YDLogger.closeOut();
					if("db2".equals(DB_TYPE)){
						dp077service.db2batchInsert(arraylist);
					}else if("oracle".equals(DB_TYPE)){
						dp077service.oraclebatchInsert(arraylist);
					}
//					YDLogger.openOut();
				}catch (RuntimeException e) {
					e.printStackTrace();
					logger.error("插入数据库出错：" + e.getMessage(), e);
					throw new TransOtherException("系统错误，请查看日志！");
				}
			}
		}else{
			throw new TransSingleException("借款人信息为空，请确认。");
		}
    	/*出口参数赋值开始*/

    	/*出口参数赋值结束*/

    	return 0;
   }

}
