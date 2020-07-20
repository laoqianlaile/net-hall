package com.yd.ish.flowhelper.common.sbcx;

import java.io.File;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jdom.Element;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.basic.util.YDVoucherUtil;
import com.yd.biz.engine.TransEngine;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.FileUtil;
import com.yd.org.util.CommonErrorCode;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.svrplatform.util.ReadProperty;
import com.yd.workflow.FlowEngineApi;
import com.yd.workflow.FlowHelperI;
import com.yd.workflow.init.FlowCache;
import com.yd.workflow.util.Constants;

/**
 * 名称：FlowWFSBCX01.java
 * <p>
 * 功能：申报信息查询流程助手 <br>
 * 
 * @author 张洪超
 * @version 0.1 2017年9月27日 张洪超创建 0.2 2018年11月14日 柏慧敏修改 总线中增加核心实例号字段
 *			0.3 2019年06月03日 许永峰修改 删除掉申报批量文件相关代码，迁移到CompSBCX0301中
 */
@Component
public class FlowWFSBCX01 implements FlowHelperI {

	/**
	 * 调用申报接口业务系统版本
	 */
	private static final String SBJK_TYPE = ReadProperty.getString("sbjk_type").toLowerCase();
	/**
	 * 申报文件地址
	 */
	private static final String file_app_path = ReadProperty.getString("file_app_path");

	@Autowired
	ParamConfigImp paramConfigImp;

	@Autowired
	YDVoucherUtil yDVoucherUtil;
	/*
	 * 交易调用示例，trancode为具体交易代码
	 * TransEngine.getInstance().execute("trancode",mainContext);
	 */

	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
		if (StringUtils.equals("getYwlb", task)) {
			// 获得流程列表
			return cmdGetYwlb(mainContext);
		} else if (StringUtils.equals("cmdQX", task)) {
			// 申报取消
			return cmdQX(mainContext);
		} else if (StringUtils.equals("cmdCK", task)) {
			// 申报查看
			if (IshConstants.GG_SBJK_JAVA.equals(SBJK_TYPE)) {
				// Java版申报查看处理
				return cmdCKForJava(mainContext);
			} else {
				return cmdCK(mainContext);
			}
		} else if (StringUtils.equals("cmdPrt", task)) {
			// 打印凭证
			return cmdPrt(mainContext);
		}
		return null;
	}

	/**
	 * 获得业务流程列表
	 * 
	 * @param mainContext
	 * @return
	 */
	private String cmdGetYwlb(MainContext mainContext) {

		mainContext.getDataPool().put("systemtype",
				mainContext.getUserContext().getAttribute(IshConstants.LOGIN_SYSTEMTYPE));
		// 末级节点
		mainContext.getDataPool().put("levelid", "1");
		// 条件查询所有功能菜单
		HashMap<String, Object> map = TransEngine.getInstance().execute("TranCDGL05", mainContext);
		// 设置返回值，业务编码：业务名称
		List<?> list = JsonUtil.getJsonList(map.get("json").toString(), HashMap.class);
		HashMap<?, ?> temp;
		map = new HashMap<String, Object>();
		String[] flowid;
		for (int i = 0; i < list.size(); i++) {
			temp = (HashMap<?, ?>) list.get(i);
			if (temp.get("href") != null && !"".equals(temp.get("href"))) {
				flowid = temp.get("href").toString().split("/");
				map.put(flowid[flowid.length - 1], flowid[flowid.length - 1] + "-" + temp.get("funcname").toString());
			}
		}

		return JsonUtil.getJsonString(map);
	}

	/**
	 * 申报取消
	 * 
	 * @param mainContext
	 * @return
	 */
	private String cmdQX(MainContext mainContext) {

		// 调用具体组件 默认为PKG版接口
		String taskFlag = "pkg";
		if (IshConstants.GG_SBJK_JAVA.equals(SBJK_TYPE)) {
			taskFlag = "java";
			String instanceid = mainContext.getDataPool().getString(Constants._WF_IS);
			mainContext.getDataPool().put("slh", instanceid);
		}
		mainContext.getDataPool().put("taskFlag", taskFlag);
		// 是否存在电子档案，是否需要删除默认不删除（需要具体业务特殊判断）
		mainContext.getDataPool().put("scdzda", IshConstants.GG_NO);
		// 调用申报取消接口
		HashMap<String, Object> map = TransEngine.getInstance().execute("TranSBCX02", mainContext);
		return JsonUtil.getJsonString(map);

	}

	/**
	 * 申报查看
	 * 
	 * @param mainContext
	 * @return
	 */
	private String cmdCK(MainContext mainContext) {
		//申报查询列表页总线
		DataPool poolOld = mainContext.getDataPool();
		//点申报号跳转到新页面总线
		DataPool pool = new DataPool();
		poolOld.put("taskFlag", "pkg");
		// 调用申报数据查询接口
		HashMap<String, Object> map = TransEngine.getInstance().execute("TranSBCX03", mainContext);
		pool.put(IshConstants._ISH_APPLYID, poolOld.getString("sbh"));
		// 0.2 start
		pool.put(IshConstants._ISH_IS_HX, poolOld.getString("hxslh"));
		// 0.2 end
		// 将业务数据的json信息解析放入pool
		JSONObject jsonObject = new JSONObject(map.get("data").toString());
		Iterator<?> iterator = jsonObject.keys();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			String value = jsonObject.getString(key);
			// 特殊数据处理
			pool.put(key, value);
		}
		pool.put("_rw", "r");
		//提交按钮控制 0不展示 1展示
		pool.put("_TjFlag", "0");
		//返回按钮控制 0不展示 1展示
		pool.put("fhFlag", "1");
		// 申报状态为退回时，展示页面重新编辑按钮 。0 不展示，1展示
		String sbzt = poolOld.getString("sbzt");
		String paravalTH = paramConfigImp.getValByMask("ish.gg.wf.sbzt", "TH");
		String paravalQX = paramConfigImp.getValByMask("ish.gg.wf.sbzt", "QX");
		if (sbzt.equals(paravalTH) || sbzt.equals(paravalQX)) {
			pool.put("_CxbjFlag", "1");
		}
		// v0.3  删除  start
		/*String applyId = pool.getString(IshConstants._ISH_APPLYID);
		String fileNames = pool.getString(IshConstants._ISH_FILENAME);
		// 判断是否为申报业务 是否需要取文件
		if (StringUtils.isNotBlank(applyId) && StringUtils.isNotBlank(fileNames)) {
			FileUtil fileUtil = new FileUtil();
			for (String fileName : fileNames.split(",")) {
				// 取文件
				File file = new File(fileName);
				if (!file.exists() || !file.isFile() || file.length() <= 0) {
					// 对文件路径特殊处理 Windows特殊处理
					String f = fileName;
					if (f.contains("\\")) {
						f = f.replaceAll("\\\\", "/");
					}
					f = StringUtils.substringAfter(f, file_app_path);
					// FTP文件下载
					fileUtil.downloadFile(file, f);
				}
				if (file.exists() && file.isFile() && file.length() > 0) {
					// 读取文件，将文件内数据存入数据库
					// 许永锋修改 start 2018/08/21
					fileUtil.saveFileToTable(file, "GBK");
					// 许永锋修改 end 2018/08/21
					// 删除文件
					// fileUtil.deleteFile(fileName);
				} else {
					throw new TransSingleException(CommonErrorCode.ERROR_WJDQSB, "文件名：" + file.getName());
				}
			}
		}*/
		//v0.3  删除  end
		String flowid = poolOld.getString("flowid");
		Element curNode = FlowCache.getFirstNode(flowid);
		String stepid = curNode.getAttributeValue("id");
		// 获取要跳转的URL
		FlowEngineApi flowEngineApi = new FlowEngineApi();
		String url = flowEngineApi.generateFlowUrl(flowid, stepid, pool);
		map.put("url", url);
		return JsonUtil.getJsonString(map);
	}

	/**
	 * 申报查看-java版
	 * 
	 * @param mainContext
	 * @return
	 */
	private String cmdCKForJava(MainContext mainContext) {
		DataPool pool = mainContext.getDataPool();
		pool.put("taskFlag", "java");
		// 调用申报数据查询接口
		HashMap<String, Object> map = TransEngine.getInstance().execute("TranSBCX03", mainContext);
		pool.put(IshConstants._ISH_APPLYID, pool.getString("sbh"));
		// 0.2 start
		pool.put(IshConstants._ISH_IS_HX, pool.getString("hxslh"));
		// 0.2 end
		// 将业务数据的json信息解析放入pool
		JSONObject jsonObject = new JSONObject(map.get("data").toString());
		Iterator<?> iterator = jsonObject.keys();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			String value = jsonObject.getString(key);
			// 特殊数据处理
			pool.put(key, value);
		}
		pool.put("rw", "r");
		String applyId = pool.getString(IshConstants._ISH_APPLYID);
		String fileNames = pool.getString(IshConstants._ISH_FILENAME);
		// 判断是否为申报业务 是否需要取文件
		if (StringUtils.isNotBlank(applyId) && StringUtils.isNotBlank(fileNames)) {
			// 文件特殊处理
			TransEngine.getInstance().execute("TranSBCX05", mainContext);
		}
		String flowid = pool.getString("flowid");
		Element curNode = FlowCache.getFirstNode(flowid);
		String stepid = curNode.getAttributeValue("id");
		// 获取要跳转的URL
		FlowEngineApi flowEngineApi = new FlowEngineApi();
		String url = flowEngineApi.generateFlowUrl(flowid, stepid, pool);
		map.put("url", url);
		return JsonUtil.getJsonString(map);
	}

	private String cmdPrt(MainContext mainContext) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		// 获取打印模板
		String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "SBCX01");
		map.put("pdfKey", poolkey);
		return JsonUtil.getJsonString(map);
	}
}
