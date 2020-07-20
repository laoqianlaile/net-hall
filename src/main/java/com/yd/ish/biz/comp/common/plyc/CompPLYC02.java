package com.yd.ish.biz.comp.common.plyc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.BatchExceptionBean;
import com.yd.biz.exception.TransBatchException;
import com.yd.biz.exception.TransSingleException;
import com.yd.org.util.CommonErrorCode;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.external.EssFactory;
import com.yd.svrplatform.comm_mdl.external.parse.ExternalUtil;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.jdbc.BaseBean;
import com.yd.svrplatform.jdbc.PersistentBiz;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.workflow.util.Constants;

/**
 * 名称：CompPLYC02.java
 * <p>
 * 功能：批量异常获取示例组件 <br>
 * 
 * @author 张洪超
 * @version 0.1 2018年2月6日 张洪超创建
 */
@Component("CompPLYC02")
public class CompPLYC02 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompPLYC02.class);

	@Autowired
	PersistentBiz persistentBiz;
	
	@Override
	public int execute() {

		/* 入口参数赋值开始 */
		String flag = getString("flag");// 1同步2异步
		/* 入口参数赋值结束 */
		MainContext mainContext = MainContext.currentMainContext();
		DataPool dataPool = mainContext.getDataPool();
		String instanceid = dataPool.getString(Constants._WF_IS);
		// 查询动态列表临时数据
		String sql = "select w.xh,w.xingming,w.zjlx,w.zjhm,w.instanceid "
				+ "	from CPL_SHENBAO_TEMP w"
				+ " where w.instanceid =" + instanceid;
		BaseBean[] beans = persistentBiz.query(sql);
		// 将要上送的批量数据，组装到details
		dataPool.put("details", ExternalUtil.transBeansToString(beans));
		// 调用批量异常获取接口
		XmlResObj data =  EssFactory.sendExternal("BSP_CHANNEL_PLYC",mainContext,false);
	    XmlResHead head=data.getHead();
	    if (XmlResHead.TR_SUCCESS.equals(head.getParticular_code())){
	    	logger.debug("接口调用成功");
	    }else if (CommonErrorCode.ERROR_PLYC.equals(head.getParticular_code())){
	    	// 批量异常码为接口批量异常返回的固定码值
	    	logger.debug("批量接口异常："+head.getParticular_info());
	    	if("1".equals(flag)){
	    		// 同步批量异常
	    		List<BatchExceptionBean> list = new ArrayList<BatchExceptionBean>();
	    		// 插入列表id字段
	    		Map<String, Object> map= JsonUtil.getJsonObject(head.getParticular_info(), Map.class);
	    		Map<String, Object> batchData = (Map<String, Object>)map.get("batchData");
	    		List<Map<String, Object>> l = (List<Map<String, Object>>)batchData.get("data");
	    		for(Map<String, Object> m :l){
	    			// 原：未插入列表id字段写法
	    			BatchExceptionBean bean = new BatchExceptionBean(
	    					Integer.parseInt(m.get("seqno")+""), 
	    					m.get("item")+"", 
	    					m.get("content")+"", 
	    					m.get("reason")+"", 
	    					m.get("infotype")+"", 
	    					m.get("locationKey")+"", 
	    					m.get("datalistId")+"", 
	    					m.get("key1")+"",
	    					m.get("key2")+"",
	    					m.get("key3")+"",
	    					m.get("key4")+"",
	    					m.get("key5")+"");
	    			list.add(bean);
	    		}
	    		if (list.size() > 0) {
	    			TransBatchException e = new TransBatchException(list, "0");
	    			e.commit();
	    			throw e;
	    		}
	    	}
	    	if("2".equals(flag)){
	    		// 异步批量异常
		    	throw new TransBatchException(head.getParticular_info());
	    	}
	    }else{
	    	logger.debug("普通接口异常："+head.getParticular_info());
	    	throw new TransSingleException(head.getParticular_code(),head.getParticular_info());
	    }
		/* 出口参数赋值开始 */
		/* 出口参数赋值结束 */

		return 0;
	}

}
