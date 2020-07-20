package com.yd.ish.biz.comp.dp;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransOtherException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.GetDownFileMap;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.dataswap.file.FileSwap;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.comm_mdl.log.YDLogger;
import com.yd.svrplatform.util.ReadProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 名称：CompDWJCMX01.java
 * <p>功能：单位缴存明细查询 <br> 
 * @author 王赫
 * @version 0.1	2018年6月7日	王赫创建
 */
@Component("CompDWJCMX01")
public class CompDWJCMX01 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompDWJCMX01.class);

	/** 数据分割符 */
	private static final String file_separators = ReadProperty.getString("file_separators");
	
	/** 数据库类型 */
	private static final String DB_TYPE = ReadProperty.getString("dbType");

	/**核心返回文件编码格式*/
	private static final String encoding = ReadProperty.getString("readfile_encoding_bsp");
	@Autowired
	DP077Service dp077service;

	@Autowired
	GetDownFileMap getDownFileMap;

	@Override
	
	public int execute() {
		/*入口参数赋值开始*/
		int instance=getInt("_IS");//实例号
    	/*入口参数赋值结束*/
		//定义日期格式yyyy-mm-dd
		SimpleDateFormat sdf = new SimpleDateFormat (IshConstants.GG_DATE_FORMAT);
		
		//根据实例号删除当前数据
		dp077service.deleteBySlh(instance);
		logger.info("[+]调用接口单位汇补缴信息，存临时表开始");
		//调用接口单位汇补缴批量信息
		XmlResObj data = super.sendExternal("BSP_DP_DWJCXX_02");
		XmlResHead head = data.getHead();
		if( !XmlResHead.TR_SUCCESS.equals(head.getParticular_code()) ){
			throw new TransSingleException(head.getParticular_info()); 	
		}
		//调用综服接口从body中获取文件名
		Map<String,Object> body = data.getBody();
		String filename=body.get("file").toString();
//		String filename=head.getFile();
		if(!(filename==null)){
			File file=FileSwap.getFile(filename);
			if(file==null){
				logger.error("下传文件不存在："+filename);
				throw new TransOtherException("系统错误，请查看日志！");
			}else{
				//解析下传文件
				BufferedReader br=null;
				try {
					br = new BufferedReader(new InputStreamReader(new FileInputStream(file),encoding));
					//批量查表一次行数
					int count = 0;	
					//累计记录文件总行数
					int total = 0;	
					//是否为文件表头信息
					Boolean first = true; 
					//记录行文件
					String tmp=null;
					
					//实际返回的数据
					HashMap<String,Object> tmpdata=null;
					//文件中第一行列名
					String[] fileColNames=null;	
					
					//查询数据库临时表的最大序号
					List<DP077> list=new ArrayList<DP077>();
					int seqno=0;
					//如果文件信息不为null，开始解析文件
					while((tmp=br.readLine())!=null){	
						if( first ){
							fileColNames=tmp.toLowerCase().split(file_separators);
							first = false;
							continue;
						}else{
							tmpdata = getDownFileMap.query(fileColNames,tmp);
							total++;
							count++;
							
							//解析文件，将数据存入临时表中
							DP077 dp077 = new DP077();
							seqno++;
							dp077.setSeqno(seqno);//序号
							dp077.setDpbusitype("00");//业务类型
							dp077.setInstance(instance);//实例号
							if(tmpdata.get("slrq") != null && !"".equals(tmpdata.get("slrq").toString())){
								try {
									dp077.setBegdate(sdf.parse(tmpdata.get("slrq").toString()));//受理日期
								} catch (ParseException e1) {
									e1.printStackTrace();
								}
							}
							if(tmpdata.get("rzrq") != null && !"".equals(tmpdata.get("rzrq").toString())){
								try {
									dp077.setEnddate(sdf.parse(tmpdata.get("rzrq").toString()));//入账日期
								} catch (ParseException e1) {
									e1.printStackTrace();
								}
							}
							dp077.setUnitaccnum1(tmpdata.get("djh").toString());//登记号
							dp077.setUnitaccnum2(tmpdata.get("jclx").toString());//缴存类型
							dp077.setUnitaccname1(tmpdata.get("jkfs").toString());//缴款方式
							dp077.setBegym(tmpdata.get("ksny").toString());//开始年月
							dp077.setEndym(tmpdata.get("jzny").toString());//截至年月						
							dp077.setPeoplenum(Integer.valueOf(tmpdata.get("yjrs").toString()));//应缴人数
							dp077.setAmt1(new BigDecimal(tmpdata.get("yjje").toString()));//应缴金额
							dp077.setFreeuse4(Integer.valueOf(tmpdata.get("sjrs").toString()));//实缴人数
							dp077.setAmt2(new BigDecimal(tmpdata.get("sjje").toString()));//实缴金额	
							dp077.setPayvouamt(new BigDecimal(tmpdata.get("zchzcje").toString()));//暂存户转出金额
							dp077.setFreeuse2(new BigDecimal(tmpdata.get("zchzrje").toString()));//暂存户转入金额
							dp077.setFundsouflag(tmpdata.get("ywlx").toString());//登记撤销标志

							list.add(dp077);
							//累计循环一定条数批量提交入库一次
							if(count!=0&&count % IshConstants.GG_BATCH_COUNT == 0){
								//批量插入数据库之前临时关闭日志输出
								YDLogger.closeOut();
								if("db2".equals(DB_TYPE)){
									dp077service.db2batchInsert(list);
								}else if("oracle".equals(DB_TYPE)){
									dp077service.oraclebatchInsert(list);
								}
								list.clear();
								//批量插入数据库之后打开临时关闭日志输出
								YDLogger.openOut();
								count=0;
							}
						}
					}
					if(count>0){
						//批量插入数据库之前临时关闭日志输出
						YDLogger.closeOut();
						if("db2".equals(DB_TYPE)){
							dp077service.db2batchInsert(list);
						}else if("oracle".equals(DB_TYPE)){
							dp077service.oraclebatchInsert(list);
						}
						//批量插入数据库之后打开临时关闭日志输出
						YDLogger.openOut();
					}
					logger.info("共"+total+"条数据");
					logger.info("[-]调用调用接口单位汇补缴批量信息，存临时表结束-----");
				}catch(RuntimeException | IOException e){
					//批量插入数据库出错临时关闭日志输出
					YDLogger.openOut();
					e.printStackTrace();
					logger.error("插入数据库出错："+e.getMessage(),e);
					throw new TransOtherException("系统错误，请查看日志！");
				}finally{
					//打开临时关闭日志输出
					YDLogger.openOut();
					try {
						if(br!=null){
							br.close();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
						logger.error("关闭文件出错："+e1.getMessage(),e1);
						throw new TransOtherException("系统错误，请查看日志！");
					}
				}
			}
		}

    	/*出口参数赋值开始*/

    	/*出口参数赋值结束*/

    	return 0;
   }
}
