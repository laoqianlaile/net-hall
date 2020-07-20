package com.yd.ish.biz.comp.dp;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.ReadProperty;

 /**
 * 名称：CompJCBLTZ01
 * <p>功能：缴存比例调整<br>
 * @brief 功能简述 调整缴存比例
 * @author 许永峰
 * @version 版本号	修改人	修改时间			地点		原因	
 * @note	0.1		许永峰	2018年6月28日		长春		创建
 */
@Component("CompJCBLTZ01")
public class CompJCBLTZ01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompJCBLTZ01.class);
	/** 启用凭证系统标记 **/
	private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");
	@Override
	public int execute() {

		/*入口参数赋值开始*/
		/*String dwjcbl=getString("dwjcbl");//变更后单位缴存比例
		String grjcbl=getString("grjcbl");//变更后个人缴存比例
*/    	/*入口参数赋值结束*/
		logger.info("[+]调用接口提交缴存比例调整信息开始");
		XmlResObj data = super.sendExternal("BSP_DP_JCBLTZ_01");
		XmlResHead head = data.getHead();
		if( !XmlResHead.TR_SUCCESS.equals(head.getParticular_code()) ){
			throw new TransSingleException(head.getParticular_info()); 	
		}else if("1".equals(pzsystemFlag)){ //启用凭证系统时设置出口参数
			Map<String,Object> body = data.getBody();
			String pzfilename = body.get("filename")==null ? "":body.get("filename").toString();
	    	/*出口参数赋值开始*/
			setOutParam("filename",pzfilename);//凭证文件名称
	    	/*出口参数赋值结束*/
		}
		logger.debug("[-]调用接口提交缴存比例调整信息结束");


    	return 0;
   }

}
