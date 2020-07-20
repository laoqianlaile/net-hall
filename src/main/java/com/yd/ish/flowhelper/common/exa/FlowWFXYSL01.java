package com.yd.ish.flowhelper.common.exa;

import com.yd.biz.engine.TransEngine;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.service.file.FileInteractiveXywj;
import com.yd.org.util.CommonErrorCode;
import com.yd.org.util.GetChannelCode;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.jdbc.BaseBean;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.Datelet;
import com.yd.svrplatform.util.ReadProperty;
import com.yd.workflow.FlowHelperI;
import com.yd.workflow.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * 名称：FlowWFXYSL01.java
 * <p>功能：协议功能示例流程助手 <br> 
 * @author 张洪超
 * @version 0.1	2017年9月27日	张洪超创建
 * 		   V0.2 2018-9-26       王赫   修改获取协议报错bug
 *         V0.3 2018-9-26       王赫   调用协议签订接口的方式改为掉用交易TranXYQD01的方式实现
 */
@Component
public class FlowWFXYSL01 implements FlowHelperI {

	private static final String STEP_STEP1="step1";//信息录入
	private static final String STEP_STEP2="step2";//签订协议
	private static final String STEP_STEP3="step3";//成功
	
	@Autowired
	ParamConfigImp paramConfigImp;

	@Autowired
	FileInteractiveXywj fileInteractiveXywj;

	/**
	 * 协议文件FTP地址
	 */
	private static final String FILE_FTP_PATH = ReadProperty.getString("file_ftp_xygl_path");
	
	/*交易调用示例，trancode为具体交易代码
		TransEngine.getInstance().execute("trancode",mainContext);
	*/
	
	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		if(stepid.equals(STEP_STEP2) && "submit".equals(type)){
			return in_step2(type,mainContext);
		}
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
		if(stepid.equals(STEP_STEP1)){
			return out_step1(mainContext);
		}
		if(stepid.equals(STEP_STEP3)){
			return out_step3(mainContext);
		}
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		
		return null;
	}
	
	private boolean in_step2(String type,MainContext mainContext){
		
		//获取示例的协议
		DataPool pool=mainContext.getDataPool();
		String systemtype=paramConfigImp.getValByMask("ish.gg.func.systemtype", IshConstants.SYSTEM_WTXTGL);
		pool.put("systemtype", systemtype);
		pool.put("flowid", pool.get(Constants._WF_FLOWID));
		pool.put("stepid", pool.get(Constants._WF_STEPID));
		pool.put("isPage", "0");
//		TransEngine.getInstance().execute("TranXYHQ01",mainContext);
		// V0.2 start
		HashMap map = TransEngine.getInstance().execute("TranXYBD03",mainContext);
		BaseBean[] beans = (BaseBean[]) pool.get("details");

		String xyid="";
		String xymc="";
		String bbh="";
		String fileid="";
		if (beans!=null&&beans.length>0){

			BaseBean b=beans[0];
			String sybz=b.getString("sybz");
			String sxrq=b.getString("sxrq");
			String zfrq=b.getString("zfrq");
			//校验协议状态
			paramConfigImp.loadMaskData(new String[]{"ish.gg.func.status"});
			if(paramConfigImp.getValByMask("ish.gg.func.status", "TY").equals(sybz)){
				throw new TransSingleException(CommonErrorCode.ERROR_XYHQSB,"协议已停用");
			}
			//校验日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date sxrq1=null;
			Date zfrq1=null;
			try {
				sxrq1=sdf.parse(sxrq);
				zfrq1=sdf.parse(zfrq);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Date now=new Date();
			if(now.before(sxrq1)){
				throw new TransSingleException(CommonErrorCode.ERROR_XYHQSB,"协议未生效");
			}
			if(now.after(zfrq1)){
				throw new TransSingleException(CommonErrorCode.ERROR_XYHQSB,"协议已失效");
			}
			xyid=b.getString("xyid");
			xymc=b.getString("xymc");
			bbh=b.getString("bbh");
			fileid=b.getString("fileid");
		}else{
			throw new TransSingleException("协议业务绑定查询失败!");
		}

		String fileLocalPath = fileInteractiveXywj.getFileRootPath();
		pool.put("xyid",xyid);
		pool.put("xymc",xymc);
		pool.put("bbh",bbh);
		pool.put("fileid",fileid);
		pool.put("fileLocalPath",fileLocalPath);
		pool.put("fileFTPPath",FILE_FTP_PATH);
		// V0.2 end
		return true;
	}

	private boolean out_step1(MainContext mainContext){
		//随机验证码校验
		TransEngine.getInstance().execute("GGTrans_0001",mainContext); 
		DataPool datapool = mainContext.getDataPool();
        String jg = String.valueOf(datapool.get("jg"));//获取验证结果
        //获取验证结果
        if(!StringUtils.equals("0",jg)) {
        	throw new TransSingleException(CommonErrorCode.ERROR_INPUT,"验证码");
        }
		return true;
	}

	private boolean out_step3(MainContext mainContext){
		
		//  TODO 成功处理
		
		// 最后提交确认，调用协议签约接口
		DataPool dataPool = mainContext.getDataPool();
		//以下5个字段为必输字段 （参数可以是界面上送也可以在数据总线获取）
        dataPool.put("khdjh",mainContext.getUserContext().getOperId());//客户登记号
        dataPool.put("xm",mainContext.getUserContext().getOperName());//姓名
        dataPool.put("khbj","0");//客户标识	0-个人，1-经办人
        dataPool.put("zjlx",paramConfigImp.getValByMask("ish.gg.user.zjlx", "SFZ"));//证件类型
        dataPool.put("zjhm","100100100100100");//证件号码
		//V0.3 start
		dataPool.put("ywbh",dataPool.getString("_WF"));//业务编号
		dataPool.put("ywmc",dataPool.getString("_FLOWNAME"));//业务名称
		dataPool.put("yhipdz",mainContext.getRemoteAddr());//用户IP地址
		dataPool.put("slh",dataPool.getString("_IS"));//实例号
		dataPool.put("czqd", GetChannelCode.getNP());//操作渠道
		dataPool.put("czrq", Datelet.getCurrentDate());//操作日期
		dataPool.put("xm", mainContext.getUserContext().getOperName());//姓名
		//用户注册协议同意，调用用户签订协议接口记录用户签订的协议
//		TransEngine.getInstance().execute("TranXYQY01",mainContext);
		TransEngine.getInstance().execute("TranXYQD01",mainContext);
		//V0.3 end
		return true;
	}

}
