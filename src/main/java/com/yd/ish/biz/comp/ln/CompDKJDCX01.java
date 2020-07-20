package com.yd.ish.biz.comp.ln;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yd.svrplatform.comm_mdl.cache.YDMemcachedManager;
import com.yd.svrplatform.comm_mdl.session.SpyMemcachedManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransOtherException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.GetDownFileMap;
import com.yd.svrplatform.comm_mdl.dataswap.file.FileSwap;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.svrplatform.util.ReadProperty;

/**
 * 名称：CompDKJDCX01
 * <p>
 * 功能：个人贷款进度查询<br>
 * 
 * @brief 获取个人贷款进度信息
 * @author 柏慧敏
 * @version 0.1 2018年6月12日 柏慧敏创建
 * @note
 */
@Component("CompDKJDCX01")
public class CompDKJDCX01 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompDKJDCX01.class);
	/** 数据分割符 */
	private static final String separators = ReadProperty.getString("file_separators");
	/** 读核心返回文件编码格式 */
	private static final String encoding = ReadProperty.getString("readfile_encoding_bsp");

	@Autowired
	GetDownFileMap getDownFileMap;
	@Autowired
	ParamConfigImp paraConFigImp;
	@Override
	public int execute() {

		/* 入口参数赋值开始 */

		/* 入口参数赋值结束 */
		logger.info("[+]调用接口获取贷款审批信息开始");
		Map<String, Object> hashmap = null;
		XmlResObj data = super.sendExternal("BSP_LN_DKSPXX_01");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}
		// 调用综服接口从body中获取文件名
		Map<String,Object> body = data.getBody();
		String filename=body.get("file").toString();
		List<Map<String, Object>> temp1 = new ArrayList<Map<String, Object>>();
		if (!(filename == null)) {
			File file = FileSwap.getFile(filename);
			if (file == null) {
				logger.error("下传文件不存在：" + filename);
				throw new TransOtherException("系统错误，请查看日志！");
			} else {
				// 解析下传文件
				BufferedReader br = null;
				try {
					br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
					// 累计记录文件总行数
					int total = 0;
					// 是否为文件表头信息
					Boolean first = true;
					// 记录行文件
					String tmp = null;

					// 实际返回的数据
					HashMap<String, Object> tmpdata = null;
					// 文件中第一行列名
					String[] fileColNames = null;
					
					//返回审批信息是否包含流程可视化节点标志,false-不包括；true-包括
					Boolean bool1 = false;
					Boolean bool2 = false;
					Boolean bool3 = false;
					Boolean bool4 = false;
					Boolean bool5 = false;
					//当前审批节点id
					String firstid = "";
					//审批流程各节点
					String cs=paraConFigImp.getValByMask("bsp.ln.loancontrstate1", "06");
					String fs=paraConFigImp.getValByMask("bsp.ln.loancontrstate1", "07");
					String zs=paraConFigImp.getValByMask("bsp.ln.loancontrstate1", "08");
					String qdjkht=paraConFigImp.getValByMask("bsp.ln.loancontrstate1", "09");
					String dkfk=paraConFigImp.getValByMask("bsp.ln.loancontrstate1", "11");
					// 如果文件信息不为null，开始解析文件
					while ((tmp = br.readLine()) != null) {
						hashmap = new HashMap<String, Object>();
						if (first) {
							fileColNames = tmp.toLowerCase().split(separators);
							first = false;
							continue;
						} else {
							tmpdata = getDownFileMap.query(fileColNames, tmp);
							total++;
							//审批信息按日期倒序返回,当前审批节点id
							if(total==1){
								if(tmpdata.get("apprflagid")==null){
									logger.error("返回审批信息有误，审批id为空");
									throw new TransSingleException("返回审批信息有误，审批id为空");
								}else{
									firstid=tmpdata.get("apprflagid").toString();
									logger.info("当前审批节点为："+firstid);
									//将当前节点信息放入map中
									hashmap.put("bzbz", firstid);
									hashmap.put("date", tmpdata.get("apprflagdate").toString());
									//若当前节点为流程中最后一个节点，tgbz设为true
									if(dkfk.equals(firstid)){
										hashmap.put("tgbz", true);
										temp1.add(hashmap);
										bool5 = true;
									}else{
										////若当前节点不是流程中最后一个节点，tgbz设为false
										hashmap.put("tgbz", false);
										temp1.add(hashmap);
										//判断当前节点与各节点大小，对于当前节点后的节点，都不再将节点信息放入流程展示map中
										if(firstid.compareTo(qdjkht)<0){
											bool4 = true;
											if(firstid.compareTo(zs)<0){
												bool3 = true;
												if(firstid.compareTo(fs)<0){
													bool2 = true;
													if(firstid.compareTo(cs)<0){
														bool1 = true;
													}
												}
											}
										}
									}
								}
							}
							//判断返回信息中是否包含已配置的流程可视化节点，若包含，且节点id小于当前节点的id,将返回的节点信息放入流程展示map中
							if(tmpdata.get("apprflagid")!=null && cs.equals(tmpdata.get("apprflagid").toString()) && !cs.equals(firstid) && bool1 == false){
								hashmap.put("bzbz", cs);
								hashmap.put("date", tmpdata.get("apprflagdate").toString());
								hashmap.put("tgbz", true);
								temp1.add(hashmap);
								bool1 = true;
							}else if(tmpdata.get("apprflagid")!=null && fs.equals(tmpdata.get("apprflagid").toString()) && !fs.equals(firstid) && bool2 == false){
								hashmap.put("bzbz", fs);
								hashmap.put("date", tmpdata.get("apprflagdate").toString());
								hashmap.put("tgbz", true);
								temp1.add(hashmap);
								bool2 = true;
							}else if(tmpdata.get("apprflagid")!=null && zs.equals(tmpdata.get("apprflagid").toString()) && !zs.equals(firstid) && bool3 == false){
								hashmap.put("bzbz", zs);
								hashmap.put("date", tmpdata.get("apprflagdate").toString());
								hashmap.put("tgbz", true);
								temp1.add(hashmap);
								bool3 = true;
							}else if(tmpdata.get("apprflagid")!=null && qdjkht.equals(tmpdata.get("apprflagid").toString()) && !qdjkht.equals(firstid) && bool4 == false){
								hashmap.put("bzbz", qdjkht);
								hashmap.put("date", tmpdata.get("apprflagdate").toString());
								hashmap.put("tgbz", true);
								temp1.add(hashmap);
								bool4 = true;
							}
						}
					}
					//若返回审批信息中无下列节点信息，将节点的tgbz置为false，节点信息放入流程展示map中
					if(!bool1){
						hashmap.put("bzbz", cs);
						hashmap.put("tgbz", false);	
						temp1.add(hashmap);
					}
					if(!bool2){
						hashmap.put("bzbz", fs);
						hashmap.put("tgbz", false);	
						temp1.add(hashmap);
					}
					if(!bool3){
						hashmap.put("bzbz", zs);
						hashmap.put("tgbz", false);	
						temp1.add(hashmap);
					}
					if(!bool4){
						hashmap.put("bzbz", qdjkht);
						hashmap.put("tgbz", false);
						temp1.add(hashmap);
					}
					if(!bool5){
						hashmap.put("bzbz", dkfk);
						hashmap.put("tgbz", false);	
						temp1.add(hashmap);
					}
					logger.info("共" + total + "条数据");
					
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("读取文件出错：" + e.getMessage(), e);
					throw new TransOtherException("系统错误，请查看日志！");
				} finally {
					try {
						if (br != null) {
							br.close();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
						logger.error("关闭文件出错：" + e1.getMessage(), e1);
						throw new TransOtherException("系统错误，请查看日志！");
					}
				}
			}
		}
		// !3.9将设置后的审批流程转换成JSON格式返回数据总线中
		String dklc = JsonUtil.getJsonString(temp1);
		String dksqbh=body.get("dksqbh").toString();
		String swtyhmc=body.get("swtyhmc").toString();
		String htdkje=body.get("htdkje").toString();
		String dkqs=body.get("dkqs").toString();
		String fwzj=body.get("fwzj").toString();
		String fwzl=body.get("fwzl").toString();
		String jkhtbh=body.get("jkhtbh").toString();
		String dklx=body.get("dklx").toString();
		String fwxz=body.get("fwxz").toString();
		logger.info("[+]调用接口获取贷款审批信息结束");
		
		/* 出口参数赋值开始 */
		 setOutParam("dksqbh",dksqbh);//贷款申请编号
		 setOutParam("swtyhmc",swtyhmc);//受委托银行名称
		 setOutParam("htdkje",htdkje);//贷款金额
		 setOutParam("dkqs",dkqs);//贷款期限
		 setOutParam("fwzj",fwzj);//房屋总价
		 setOutParam("fwzl",fwzl);//房屋坐落
		 setOutParam("jkhtbh",jkhtbh);//借款合同编号
		 setOutParam("dklx",dklx);//贷款类型
		 setOutParam("fwxz",fwxz);//房屋性质
		 setOutParam("dklc",dklc);//贷款流程
		/* 出口参数赋值结束 */

		return 0;
	}

}
