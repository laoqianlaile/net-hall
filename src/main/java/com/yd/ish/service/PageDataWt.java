package com.yd.ish.service;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.context.UserContext;
import com.yd.svrplatform.comm_mdl.services.IFlowInfoServices;
import com.yd.svrplatform.comm_mdl.services.IMessageInfoServices;
import com.yd.svrplatform.comm_mdl.services.IPortalNameServices;
import com.yd.svrplatform.jdbc.BaseBean;
import com.yd.svrplatform.spring.ApplicationContextHelper;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.Datelet;
import com.yd.svrplatform.util.FlowUtil;
import com.yd.svrplatform.util.PoolUtil;
import com.yd.svrplatform.util.ReadProperty;
import com.yd.ydpx.services.IncludeJsPublic;
import com.yd.ydpx.util.IPageData;
import com.yd.ydpx.util.YdpxUtil;

@Service("ydpx.pageData")
public class PageDataWt implements IPageData {
	
	private static final Logger logger = LoggerFactory.getLogger(PageDataWt.class);
	
	public PageDataWt() {
		
	}
	
	
	public JSONObject getData(String templateId, HttpServletRequest request) {
		try {
			switch (templateId) {
			case "0401":
			case "0402":
				return getTplData(request, true);
			case "0403":
			case "0409":
				return getTplData(request, false);
			case "frameset":
				return getFramesetData();
			default: 
				return getErrorData("YDP7001", "未指定模板ID");
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			String message = "获取" + templateId + "YDPX模板数据时出错" + ex.getMessage();
			logger.error(message, ex);
			return getErrorData("error", message);
		}
	}
	
	private JSONObject getFramesetData() {
		JSONObject json = new JSONObject();
		json.put("system_message_enable", ReadProperty.getString("system_message_enable"));
		json.put("home", YdpxUtil.getContexPath() + "/home");
		return json;
	}
	
	private JSONObject getTplData(HttpServletRequest request, boolean hasLogin) throws Exception {
		JSONObject json = new JSONObject();
		MainContext mc = MainContext.currentMainContext();
		if (mc == null) mc = new MainContext(request);
		//数据总线
		DataPool pool = mc.getDataPool();
		if (pool == null) pool = new DataPool();
		json.put("poolSelect", JSONObject.parse(PoolUtil.toJson(pool)));
		//流程配置信息
		String pageConfigString = (String)request.getAttribute("pageConfig");
		JSONObject pageConfig = (JSONObject) JSONObject.parse(pageConfigString);
		json.put("pageConfig", pageConfig);
		//序列化的原始数据总线
		json.put("dataPool", FlowUtil.transObject(pool).replaceAll("\r\n", ""));
		//当前系统时间
		json.put("serverTime", Datelet.getCurrentDateTime());
		//上下文根
		json.put("contexPath", request.getContextPath());
		
		//是否显示客服图标  0-不显示
		json.put("customer-service-button", ReadProperty.getString("customer-service-button") == null ? "0" : ReadProperty.getString("customer-service-button"));
		json.put("customer-service-url", ReadProperty.getString("customer-service-url") == null ? "" : ReadProperty.getString("customer-service-url"));
		//是否显示【导出批量错误数据】功能（0-不显示，1-显示；默认显示）
		json.put("datalist_batch_export", ReadProperty.getString("datalist_batch_export") == null ? "1" : ReadProperty.getString("datalist_batch_export"));
		if (hasLogin) {
			//登录用户信息
			UserContext user = mc.getUserContext();
			json.put("userConfig", JSONObject.parse(user.getConfigs()));
			//审批意见
			if (!"menu".equals(pool.getString("_TYPE")) && (pageConfig != null && pageConfig.getIntValue("apply") == 1)) {
				json.put("applyInfo", getApplyInfo(mc, pool));
			}
			//首页用户信息
			json.put("userContent", user.getAttribute("homedata"));
			//json.put("menuData", user.getAttribute("menuTree"));
			//一级菜单下 带链接的二级菜单组名称
			json.put("group2Name", ReadProperty.getString("level2_menu_group_name") == null ? "" : ReadProperty.getString("level2_menu_group_name"));
			//
			json.put("strCertId", user.getAttribute("strCertId"));
			//消息配置
			IMessageInfoServices smis = (IMessageInfoServices)ApplicationContextHelper.getBean("systemMessageInfoServicesImp");
			if (smis != null) json.put("message", smis.getInfoList());
			//公告查询
			IMessageInfoServices nmis = (IMessageInfoServices)ApplicationContextHelper.getBean("noticeMessageInfoServicesImp");
			if (nmis != null) json.put("noticeInfo", nmis.getInfoList());
			
			json.put("_OPERNAME", user.getOperName());
		}
		return json;
	}

	//查历史审批意见
	private JSONArray getApplyInfo(MainContext mc, DataPool pool) {
		JSONArray ret = new JSONArray();
		IPortalNameServices pns = (IPortalNameServices)ApplicationContextHelper.getBean("portalNameServicesImp");
		IFlowInfoServices fis = (IFlowInfoServices)ApplicationContextHelper.getBean("flowInfoServicesImp");
		if (fis != null) {
			BaseBean[] beans = fis.getApplyList(pool.get("_IS").toString());
			for (int i = 0; i < beans.length; i++) {
				JSONObject rec = new JSONObject();
				String operId = beans[i].get("operid").toString();
				rec.put("operId", operId);
				if (pns != null) rec.put("operName", pns.getOperName(operId));
            	rec.put("apply", beans[i].get("apply"));
            	rec.put("time", beans[i].get("createtime"));
            	rec.put("memo", beans[i].get("applymemo"));
            	ret.add(rec);
			}
		}
		return ret;
	}

	private JSONObject getErrorData(String errCode, String message) {
		JSONObject json = new JSONObject();
		json.put("returnCode", errCode);
		json.put("message", message);
		return json;
	}


	@Override
	public HashMap<String, String> htmlVariable(String templateId, HttpServletRequest request) {
		HashMap<String, String> map = new HashMap<String, String>();
		//SCRIPT
		String strHtml = "";
		IncludeJsPublic ijp=(IncludeJsPublic)ApplicationContextHelper.getBean("includeJsPublicImp");
		if(ijp != null){
			MainContext mc = (MainContext)request.getAttribute("mainContext");
			if (mc != null) {
				String jsJb = ijp.getScript1(mc);
				if(jsJb != null){
					strHtml += "<script>"+jsJb+"</script>";
				}
				String[] urls = ijp.getScripts(mc);
				for(int i=0; i<urls.length; i++){
					strHtml += "<script src=\"" +YdpxUtil.getContexPath()+ urls[i] + "\"></script>";
				}
				jsJb = ijp.getScript2(mc);
				if(jsJb != null){
					strHtml += "<script>"+jsJb+"</script>";
				}
			}
		}
		map.put("SCRIPT", strHtml);
		return map;
	}

	
}
