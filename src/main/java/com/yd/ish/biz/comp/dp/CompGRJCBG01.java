package com.yd.ish.biz.comp.dp;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransOtherException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.GetDownFileMap;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.org.util.CommonErrorCode;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 名称：CompGRJCBG01.java
 * <p>功能： 单位下个人缴存变更明细查询<br> 
 * @author 王赫
 * @version 0.1	2018年7月18日	王赫创建
 */
@Component("CompGRJCBG01")
public class CompGRJCBG01 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompGRJCBG01.class);
	/** 数据分割符 */
	private static final String file_separators = ReadProperty.getString("file_separators");
	/** 数据库类型 */
	private static final String DB_TYPE = ReadProperty.getString("dbType");

	@Autowired
	DP077Service dp077service;

	@Autowired
	GetDownFileMap getDownFileMap;
	@Override
	public int execute() {

		/* 入口参数赋值开始 */
		String instance = getString("slh");
		//删除已存在的记录
		dp077service.deleteBySlh(Integer.valueOf(instance));
		/* 入口参数赋值结束 */
		logger.info("[+]调用单位下个人缴存变更明细查询接口BSP_DP_GRJCBG_01开始");
		XmlResObj data = super.sendExternal("BSP_DP_GRJCBG_01", false);
		XmlResHead head = data.getHead();
		Map<String, Object> map = new HashMap<String, Object>();
		if (XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			logger.info("[-]调用单位下个人缴存变更明细查询接口BSP_DP_GRJCBG_01成功");
			map = data.getBody();
			String filename = map.get("file")==null?"":map.get("file").toString();
			if("".equals(filename)){
				logger.error("文件名不能为空。");
				throw new TransSingleException(CommonErrorCode.ERROR_WJDQSB,"");
			}
			File file=FileSwap.getFile(filename);
			if(file==null){
				logger.error("下传文件不存在："+filename);
				throw new TransSingleException(CommonErrorCode.ERROR_WJDQSB,"");
			}else{
				//BufferedReader提供了按行读取文本文件的方法readLine()
				//readLine()返回行有效数据，不包含换行符，未读取到数据返回null
				FileReader fr = null;
				BufferedReader br = null;
				//批量查表一次行数
				int count = 0;	
				try {
					fr = new FileReader(file);	
					br = new BufferedReader(fr);
					//是否为文件表头信息
					Boolean first = true; 
					//记录行文件
					String line = "";
					//实际返回的数据
					HashMap<String,Object> tmpdata=null;
					//文件中第一行列名
					String[] fileColNames=null;
					int seqno=0;
					List<DP077> list=new ArrayList<DP077>();
					while((line = br.readLine())!=null){
						if(first){
							fileColNames=line.toLowerCase().split(file_separators);
							first = false;
							continue;
						}else{
							tmpdata = getDownFileMap.query(fileColNames,line);
							//解析文件，将数据存入临时表中
							seqno++;
							count++;
//							bglx~bgrq~bgrs~bgqjcjs~bghjcjs~bgqdwjcbl~bghdwjcbl~bgqgrjcbl~bghgrjcbl~bgqyyjce~bghyyjce
							DP077 dp077 = new DP077();
							dp077.setSeqno(seqno);
							dp077.setInstance(Integer.valueOf(instance));
							//业务类型
							dp077.setDpbusitype("00");
							//变更类型
							dp077.setAccname1(tmpdata.get("bglx").toString());
							//变更日期
							try {
								dp077.setTransdate(new SimpleDateFormat("yyyy-MM-dd").parse(tmpdata.get("bgrq").toString()));
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								throw new TransSingleException(CommonErrorCode.ERROR_LXZHCW,"变更日期【bgrq】字段,时间类型转换错误!");
							}
							//变更人数
							dp077.setPeoplenum(Integer.valueOf(tmpdata.get("bgrs").toString()));
							//变更前缴存基数
							dp077.setAmt1(new BigDecimal(tmpdata.get("bgqjcjs").toString()));
							//变更后缴存基数
							dp077.setAmt2(new BigDecimal(tmpdata.get("bghjcjs").toString()));
							//变更前单位缴存比例
							dp077.setUnitaccname1(tmpdata.get("bgqdwjcbl").toString());
							//变更后单位缴存比例
							dp077.setUnitaccname2(tmpdata.get("bghdwjcbl").toString());
							//变更前个人缴存比例
							dp077.setPayvoubank(tmpdata.get("bgqgrjcbl").toString());
							//变更后个人缴存比例
							dp077.setAgentinstcode(tmpdata.get("bghgrjcbl").toString());
							//变更前月应缴存额
							dp077.setPayvouamt(new BigDecimal(tmpdata.get("bgqyyjce").toString()));
							//变更后月应缴存额
							dp077.setFreeuse2(new BigDecimal(tmpdata.get("bghyyjce").toString()));
							list.add(dp077);
						}
					}
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
					logger.info("共"+seqno+"条数据");
					logger.info("[-]调用单位下个人缴存变更明细查询接口BSP_DP-GRJCBG_01，存临时表结束-----");
				} catch (RuntimeException | IOException e) {
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
						if(fr!=null){
							fr.close();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
						logger.error("关闭文件出错："+e1.getMessage(),e1);
						throw new TransOtherException("系统错误，请查看日志！");
					}
				
				}
			}
		} else {
			throw new TransSingleException(head.getParticular_code(), head.getParticular_info());
		}
		/* 出口参数赋值开始 */
		/* 出口参数赋值结束 */

		return 0;
	}
}
