package com.yd.ish.biz.comp.dw;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 * 名称：CompDwInfo.java
 * <p>
 * 功能：单位信息查询 <br>
 *
 * @author 王赫
 * @version 0.1 2018年8月10日 王赫创建
 */
@Component("CompDwInfo")
public class CompDwInfo extends BaseComp{
    private static final Logger logger = LoggerFactory.getLogger(CompDwInfo.class);


   @Override
   public int execute() {

       /*入口参数赋值开始*/
       /*入口参数赋值结束*/

       logger.info("[+]调用单位基本信息接口");
       // 调用接口计算可提取金额
       XmlResObj data = super.sendExternal("getDWJBXXCX2");
       XmlResHead head = data.getHead();
       if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
           throw new TransSingleException(head.getParticular_info());
       }
       Map<String,Object> body = data.getBody();
       String ktqje=body.get("ktqje")==null?"":body.get("ktqje").toString();

       logger.info("[-]调用接口计算可提取金额结束");
       /*出口参数赋值开始*/
       setOutParam("ktqje",ktqje);//可提取金额
       /*出口参数赋值结束*/

       return 0;

  }



}
