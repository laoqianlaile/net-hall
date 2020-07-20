package com.yd.ish.biz.comp.dw;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 * 名称：CompHQHKXX02.java
 * <p>
 * 功能：获取商贷还款信息 <br>
 *
 * @author 王赫
 * @version 0.1 2018年8月09日 王赫创建
 */
@Component("CompHQHKXX02")
public class CompHQHKXX02 extends BaseComp{

   private static final Logger logger = LoggerFactory.getLogger(CompHQHKXX02.class);

   @Override
   public int execute() {

       /*入口参数赋值开始*/
       String sddkhth = getString("sddkhth");//商贷贷款合同号
       /*入口参数赋值结束*/
       //调用获取商贷还款信息接口BSP-DW-HQHKXX-02
       XmlResObj data = super.sendExternal("BSP_DW_HQHKXX_02", false);
       XmlResHead head = data.getHead();
       //如果接口调用成功
       if(!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())){
           throw new TransSingleException(head.getParticular_info());
       }
       Map<String,Object> body = data.getBody();
       String dkyh = body.get("dkyh") == null ? "" : body.get("dkyh").toString();
       String dkye = body.get("dkye") == null ? "" : body.get("dkye").toString();
       String yhmc = body.get("yhmc") == null ? "" : body.get("yhmc").toString();

       /*出口参数赋值开始*/
       setOutParam("dkyh",dkyh);//贷款银行
       setOutParam("dkye",dkye);//贷款余额
       setOutParam("yhmc",yhmc);//银行名称
       /*出口参数赋值结束*/

       return 0;
  }

}
