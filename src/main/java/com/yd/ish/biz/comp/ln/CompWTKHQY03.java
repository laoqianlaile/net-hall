package com.yd.ish.biz.comp.ln;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.BatchExceptionBean;
import com.yd.biz.exception.TransBatchException;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;

 /**
 * 名称：CompWTKHQY03
 * <p>功能：检查签约人列表数据<br>
 * 
 * @brief 检查签约人列表数据
 * @author 柏慧敏
 * @version 0.1 2018年8月30日 柏慧敏创建
 * @note
 */
@Component("CompWTKHQY03")
public class CompWTKHQY03 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompWTKHQY03.class);
	@Autowired
	DP077Service dp077service;
	@Autowired
	ParamConfigImp paramconfigImp;
	@Override
	public int execute() {

		/*入口参数赋值开始*/
		int instance=getInt("instance");//实例号
    	/*入口参数赋值结束*/
		logger.info("[+]批量信息校验开始");
		// 根据实例号查临时表，获取签约信息列表
		List<DP077> qylist=dp077service.selectOrderByKhsx(instance);
		int qylistlength = 0;
		//定义查询结果长度
		if(qylist != null){
			qylistlength=qylist.size();
		}
		List<BatchExceptionBean> errlist = new ArrayList<BatchExceptionBean>();
		BatchExceptionBean bean;
		//序号
		int seqno;
		//批量错误信息数量
		int count=0;
		//签订数量
		int total=0;
		List<Integer> list=new ArrayList<>();
		//循环查询结果
		for(int i=0;i<qylistlength;i++){
			//若签约类型不为空
			if(!"".equals(qylist.get(i).getSex()) && qylist.get(i).getSex() != null){
				//交易密码为空
				if("".equals(qylist.get(i).getAgentinstcode()) || qylist.get(i).getAgentinstcode()==null){
					count++;
					//获取错误信息的序号
					seqno = qylist.get(i).getSeqno();
					//添加错误信息
					bean = new BatchExceptionBean(count, "jymm", "", "签约类型不为空时，交易密码不能为空", "0", "seqno", "datalist1",
							seqno + "", "", "", "", "");
					errlist.add(bean);
				}
				//若签约类型为签订
				if("1".equals(paramconfigImp.getMask("bsp.ln.qylx."+qylist.get(i).getSex()))){
					//扣划顺序若为空，添加错误信息
					if("".equals(qylist.get(i).getPeoplenum()+"") || qylist.get(i).getPeoplenum()==null){
						count++;
						seqno = qylist.get(i).getSeqno();
						bean = new BatchExceptionBean(count, "khsx", "", "签约类型为签订时，扣划顺序不能为空", "0", "seqno", "datalist1",
								seqno + "", "", "", "", "");
						errlist.add(bean);
					}
					//获取不为空的扣划顺序，总记录加1
					else{
						list.add(qylist.get(i).getPeoplenum());
						total++;
					}
					
				}
			}
		}
		//若签约类型为签订且扣划顺序不为空的数量大于1 
		if(total>0){
			//判断扣划顺序是否从1开始，若不是，则添加错误信息
			if(list.get(0) != 1){
				count++;
				seqno = qylist.get(0).getSeqno();
				bean = new BatchExceptionBean(count, "khsx", "", "扣划顺序应从1开始依次递增1", "0", "seqno", "datalist1",
						seqno + "", "", "", "", "");
				errlist.add(bean);
			}
			//判断最后一个扣划顺序是否为总数量，若不是则添加错误信息
			if(list.get(total-1) != total){
				count++;
				seqno = qylist.get(total-1).getSeqno();
				bean = new BatchExceptionBean(count, "khsx", "", "扣划顺序应从1开始依次递增1", "0", "seqno", "datalist1",
						seqno + "", "", "", "", "");
				errlist.add(bean);
			}
			//判断扣划顺序是否依次递增1，若不是则添加错误信息
			for(int i=0;i<list.size();i++){
				for(int j=i+1;j<list.size();j++){
					if(list.get(j)-list.get(i) != 1){
						count++;
						seqno = qylist.get(j).getSeqno();
						bean = new BatchExceptionBean(count, "khsx", "", "扣划顺序应从1开始依次递增1", "0", "seqno", "datalist1",
								seqno + "", "", "", "", "");
						errlist.add(bean);
					}
				}
			}
		}
		logger.info("[-]批量信息校验结束");
		// 抛出批量错误信息
		if (errlist != null && errlist.size() > 0) {
			TransBatchException e = new TransBatchException(errlist, "0");
			e.commit();
			throw e;
		}
    	/*出口参数赋值开始*/

    	/*出口参数赋值结束*/

    	return 0;
   }

}
