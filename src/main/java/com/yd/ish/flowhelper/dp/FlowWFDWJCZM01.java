package com.yd.ish.flowhelper.dp;

import com.yd.basic.util.YDVoucherUtil;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.workflow.FlowHelperI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * 名称：FlowWFDWJCZM01.java
 * <p>功能：单位住房公积金缴存情况的证明打印 <br>
 * @author 王赫
 * @version 0.1	2018年8月14日	王赫创建
 */
@Component
public class FlowWFDWJCZM01 implements FlowHelperI {

   private static final Logger logger = LoggerFactory.getLogger(FlowWFDWJCZM01.class);

   private static final String STEP_STEP1="step1";//个人住房公积金缴存证明打印

   @Autowired
   YDVoucherUtil yDVoucherUtil;

   /*交易调用示例，trancode为具体交易代码
   TransEngine.getInstance().execute("trancode",mainContext);
   */

   @Override
   public boolean in(String stepid, String type, MainContext mainContext) {
       if(stepid.equals(STEP_STEP1)){
           return in_step1(type,mainContext);
       }
       return true;
   }

   @Override
   public boolean out(String stepid, MainContext mainContext) {

       return true;
   }

   @Override
   public String cmd(String stepid, String task, MainContext mainContext) {

       return null;
   }


   /**
    * 打印凭证
    *
    * @param type
    * @param mainContext
    * @return
    */
   private boolean in_step1(String type, MainContext mainContext) {
       //获取单位缴存证明信息
       TransEngine.getInstance().execute("TranDWJCZM01",mainContext);
       // 获取打印模板
       String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "DWJCZM01");
       mainContext.getDataPool().put("pdfKey", poolkey);
       return true;
   }

}
