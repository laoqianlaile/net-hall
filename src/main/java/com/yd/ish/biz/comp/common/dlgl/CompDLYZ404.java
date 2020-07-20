package com.yd.ish.biz.comp.common.dlgl;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.org.util.CommonErrorCode;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.DataPool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 名称：CompDLYZ403.java
 * <p>功能：登录功能-单位临时用户登录验证返回用户信息 <br>
 * @author 王赫
 * @version 0.1	2019年10月23日	王赫创建
 */
@Component("CompDLYZ404")
public class CompDLYZ404 extends BaseComp{
	private static final Logger logger = LoggerFactory.getLogger(CompDLYZ404.class);
	private static final String INTERFACE_ID = "getCALogin";

	public CompDLYZ404() {
	}

	public int execute() {
		String pubzh = this.getString("pubzh");
		String password = this.getString("password");
		String dllx = this.getString("dllx");
		if (StringUtils.isBlank(pubzh)) {
			throw new TransSingleException("ICM99701", new Object[]{"用户名"});
		} else {
			String jbrzjhm = "";
			String jbrxm = "";
			String dwdjh = "";
			String dwmc = "";
			String dwdz = "";
			String jbrsjhm = "";
			String dwslrq = "";
			String dwzhzt = "";
			String zzjgdm = "";
			String jzny = "";
			String wtinstcode = "";
			String BrcCode = "";
			logger.info("[+]调用核心用户验证返回用户信息接口getCALogin开始");
			DataPool dataPool = MainContext.currentMainContext().getDataPool();
			dataPool.put("pubzh", pubzh);
			dataPool.put("dlmm", password);
			dataPool.put("dlfs", "1");
			dataPool.put("type", "01");
			dataPool.put("loginId", pubzh);
			dataPool.put("logintype", dllx);
			XmlResObj data = super.sendExternal(INTERFACE_ID, false);
			XmlResHead head = data.getHead();
			if (XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
				Map<String, Object> body = data.getBody();
				dwdjh = body.get("dwzh").toString();
				dwdz = body.get("dwdz").toString();
				dwmc = body.get("dwmc").toString();
				dwslrq = body.get("dwslrq").toString();
				wtinstcode = body.get("jbjg").toString();
				BrcCode = body.get("jbjg").toString();
//				jbrsjhm = body.get("jbrsjhm").toString();
				jbrxm = body.get("jbrxm").toString();
//				jbrzjhm = body.get("jbrzjhm").toString();
//				dwzhzt = body.get("dwzhzt").toString();
//				zzjgdm = body.get("zzjgdm").toString();
//				jzny = body.get("jzny").toString();
				logger.info("[-]调用核心用户验证返回用户信息接口getCALogin结束");
//				dataPool.put("jbrzjhm", jbrzjhm);
				dataPool.put("dwzh", dwdjh);
//				dataPool.put("dwzhzt", dwzhzt);
//				dataPool.put("jbrxm", jbrxm);
				this.setOutParam("jbrxm", jbrxm);
//				this.setOutParam("jbrsjhm", jbrsjhm);
//				this.setOutParam("jbrdjh", jbrzjhm);
				this.setOutParam("dwdjh", dwdjh);
				this.setOutParam("dwmc", dwmc);
				this.setOutParam("instcode", dwdjh);
				this.setOutParam("instname", dwmc);
				this.setOutParam("dwdz", dwdz);
				this.setOutParam("yyzzhm", "11111");
				this.setOutParam("wtinstcode", wtinstcode);
				this.setOutParam("BrcCode", BrcCode);
//				this.setOutParam("dwclsj", dwslrq);
//				this.setOutParam("dwzhzt", dwzhzt);
//				this.setOutParam("zzjgdm", zzjgdm);
//				this.setOutParam("jzny", jzny);
//				this.setOutParam("rolecodes", "ORG01004");
				this.setOutParam("rolecodes", "ORG01001");
				return 0;
			} else {
				logger.info("[-]调用核心用户验证返回用户信息接口getCALogin结束");
				throw new TransSingleException(head.getParticular_code(), new Object[]{head.getParticular_info()});
			}
		}
	}
}