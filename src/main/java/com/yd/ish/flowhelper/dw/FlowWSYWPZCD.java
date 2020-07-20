package com.yd.ish.flowhelper.dw;

import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.workflow.FlowHelperI;
import org.springframework.stereotype.Component;

/**
* 名称：FlowWFWSYWPZCD
* <p>功能：业务凭证补打流程助手<br>
* @author 柏慧敏
* @version 0.1	2018年5月25日	柏慧敏创建
* @note
*/
@Component
public class FlowWSYWPZCD implements FlowHelperI {

   /*交易调用示例，trancode为具体交易代码
   TransEngine.getInstance().execute("trancode",mainContext);
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

       return null;
   }
}
