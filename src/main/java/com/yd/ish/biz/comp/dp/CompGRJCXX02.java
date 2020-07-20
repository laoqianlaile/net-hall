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

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 名称：CompGRJCXX02.java
 * <p>功能：单位下个人汇补缴信息查询 <br> 
 * @author 王赫
 * @version 0.1	2018年7月12日	王赫创建
 */
@Component("CompGRJCXX02")
public class CompGRJCXX02 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompGRJCXX02.class);
	/** 数据分割符 */
	private static final String file_separators = ReadProperty.getString("file_separators");
	/** 数据库类型 */
	private static final String DB_TYPE = ReadProperty.getString("dbType");
	/** 读核心返回文件编码格式 */
	private static final String encoding = ReadProperty.getString("readfile_encoding_bsp");

	@Autowired
	DP077Service dp077service;

	@Autowired
	GetDownFileMap getDownFileMap;
	@Override
	public int execute() {

		/* 入口参数赋值开始 */
		int instance = getInt("_IS");
		/* 入口参数赋值结束 */
		//删除已存在的记录
		dp077service.deleteBySlh(instance);
		logger.info("[+]调用单位下个人汇补缴信息查询接口BSP_DP_GRJCXX_02开始");
		XmlResObj data = super.sendExternal("BSP_DP_GRJCXX_02", false);
		XmlResHead head = data.getHead();
		Map<String, Object> map = new HashMap<String, Object>();
		if (XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			logger.info("[-]调用单位下个人汇补缴信息查询接口BSP_DP_GRJCXX_02成功");
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
				BufferedReader br = null;
				//批量查表一次行数
				int count = 0;	
				try {
					br = new BufferedReader(new InputStreamReader(new FileInputStream(file),encoding));
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
//							grzh~xingming~jcsbh~jclx~jce~slrq~ksny~jzny~ywzt
							DP077 dp077 = new DP077();
							dp077.setSeqno(seqno);
							dp077.setInstance(Integer.valueOf(instance));
							dp077.setDpbusitype("00");//业务类型
							//个人账号
							dp077.setAccnum1(tmpdata.get("grzh").toString());
							dp077.setAccname1(tmpdata.get("xingming").toString());
							dp077.setJtzz(tmpdata.get("jcsbh").toString());
							dp077.setUnitaccname1(tmpdata.get("jclx").toString());
							dp077.setAccname2(tmpdata.get("jce").toString());
							try {
								dp077.setBegdate(new SimpleDateFormat("yyyy-MM-dd").parse(tmpdata.get("slrq").toString()));
							} catch (ParseException e) {
								e.printStackTrace();
								throw new TransSingleException(CommonErrorCode.ERROR_LXZHCW, "时间类型转换错误");
							}
							dp077.setBegym(tmpdata.get("ksny").toString());
							dp077.setEndym(tmpdata.get("jzny").toString());
							dp077.setUnitaccnum2(tmpdata.get("ywzt").toString());
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
					logger.info("[-]调用单位下个人汇补缴信息查询接口BSP_DP_GRJCXX_02，存临时表结束-----");
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
/*						if(fr!=null){
							fr.close();
						}*/
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
