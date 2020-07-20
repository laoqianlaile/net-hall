package com.yd.ish.biz.comp.common.ybmap;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.BatchExceptionBean;
import com.yd.biz.exception.TransBatchException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.biz.comp.common.plyc.CompPLYC02;
import com.yd.ish.common.util.StringUtils;
import com.yd.org.util.CommonErrorCode;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.external.EssFactory;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.svrplatform.util.ReadProperty;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 名称：CompYBMAP0201.java
 * <p>
 * 功能：综服2.0测试示例组件 <br>
 * 
 * @author 武丹
 * @version 0.1 2018年5月15日 武丹创建
 */
@Component("CompYBMAP0201")
public class CompYBMAP0201 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompYBMAP0201.class);

	@Override
	public int execute() {

		MainContext mainContext = MainContext.currentMainContext();
		XmlResObj data =  EssFactory.sendExternal("YBMAP2_TEST1",mainContext,false);
		XmlResHead head=data.getHead();
		if (XmlResHead.TR_SUCCESS.equals(head.getParticular_code())){
			logger.debug("接口调用成功");
		}else {
			logger.debug("接口调用失败");
		}

		/* 出口参数赋值开始 */
		/* 出口参数赋值结束 */

		return 0;
	}

}
