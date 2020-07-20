package com.yd.ish.biz.comp.dp;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
* 名称：CompXGDWMM01
* <p>功能：临时单位用户密码修改<br>
* @brief 提交密码修改信息
* @author 王赫
* @version 0.1	2019年10月31日	王赫创建
* @note
*/
@Component("CompXGDWMM02")
public class CompXGDWMM02 extends BaseComp{

   private static final Logger logger = LoggerFactory.getLogger(CompXGDWMM02.class);
   private static final String INTERFACE_ID="BSP_DP_XGDWMM_02";

   @Override
   public int execute() {

       /*入口参数赋值开始*/
       String ydlmm=getString("ydlmm");//原登录密码
       String xdlmm=getString("xdlmm");//新登录密码
       String qrxdlmm=getString("qrxdlmm");//确认登录密码
       /*入口参数赋值结束*/
       logger.info("[+]调用接口提交修改密码信息开始");
       XmlResObj data = super.sendExternal(INTERFACE_ID);
       XmlResHead head = data.getHead();
       if( !XmlResHead.TR_SUCCESS.equals(head.getParticular_code()) ){
           throw new TransSingleException(head.getParticular_info());
       }
       logger.debug("[-]调用接口提交修改密码信息结束");
       /*出口参数赋值开始*/

       /*出口参数赋值结束*/

       return 0;
  }

}
