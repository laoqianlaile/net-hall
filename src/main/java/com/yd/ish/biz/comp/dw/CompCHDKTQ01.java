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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 名称：CompCHDKTQ01.java
 * <p>
 * 功能：偿还公积金贷款提交 <br>
 *
 * @author 王赫
 * @version 0.1 2018年8月10日 王赫创建
 */
@Component("CompCHDKTQ01")
public class CompCHDKTQ01 extends BaseComp{
    private static final Logger logger = LoggerFactory.getLogger(CompCHDKTQ01.class);

    @Autowired
    ParamConfigImp paramConfigImp;

   @Override
   public int execute() {

       /*入口参数赋值开始*/
       String tqyy = getString("tqyy");//提取原因
       String jkhtbh = getString("jkhtbh");//借款合同编号
       String jkhtbhsz = getString("jkhtbhsz");//借款合同编号数组
       /*入口参数赋值结束*/

       // 传递的参数是否正确
       Boolean flag = false;
       // 若借款合同编号数组不为空
       if("tqyy".equals(paramConfigImp.getValByMask("bsp.dw.chdktqyy","CHGJJDKTQ"))){
           String[] jkhtbharray = jkhtbhsz.split(",");
           // 循环数组
           for(int i=0;i<jkhtbharray.length;i++){
               // 判断页面选择的借款合同编号是否在数组中
               if(jkhtbh.equals(jkhtbharray[i])){
                   // 若在数组中，标志赋值成true，退出循环
                   flag = true;
                   break;
               }
           }
       }else{
           flag = true;
       }
       if(flag) {
           //调用偿还公积金贷款提取接口BSP_DW_HQHKXX_01
           XmlResObj data = super.sendExternal("BSP_DW_CHDKTQ_01", false);
           XmlResHead head = data.getHead();
           //如果接口调用成功
           if (XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
               logger.info("调用偿还公积金贷款提取提交接口成功!：" + head.getParticular_info());
           } else {
               logger.error("调用偿还公积金贷款提取提交接口异常：" + head.getParticular_info());
               throw new TransSingleException(head.getParticular_code(), head.getParticular_info());
           }
       }else{
           logger.error("获取到的借款合同编号："+ jkhtbh+ ",不在借款合同编号数组中");
           throw new TransSingleException("提交提取信息失败。");
       }
       /*出口参数赋值开始*/

       /*出口参数赋值结束*/

       return 0;
  }



}
