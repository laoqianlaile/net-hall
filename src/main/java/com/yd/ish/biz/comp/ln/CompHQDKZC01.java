package com.yd.ish.biz.comp.ln;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
* 名称：CompHQDKZC01
* <p>功能：获取贷款政策信息<br>
* @brief 获取贷款政策信息
* @author 许永峰
* @version 0.1 2019年11月21日 柏慧敏创建
* @note
*/
@Component("CompHQDKZC01")
public class CompHQDKZC01 extends BaseComp{

   private static final Logger logger = LoggerFactory.getLogger(CompHQDKZC01.class);

   @Autowired
    DP077Service dp077Service;

   @Override
   public int execute() {

       /*入口参数赋值开始*/
       /*入口参数赋值结束*/
           logger.info("[+]调用接口 获取贷款政策信息开始");
           XmlResObj data = super.sendExternal("BSP_LN_GETDKZC_01");
           XmlResHead head = data.getHead();
           if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
               throw new TransSingleException(head.getParticular_info());
           }
           Map<String,Object> body = data.getBody();
           String grzfdkzged=body.get("grzfdkzged")==null?"":body.get("grzfdkzged").toString();
           String fqzfdkzged=body.get("fqzfdkzged")==null?"":body.get("fqzfdkzged").toString();
           String grzfdkzcnx=body.get("grzfdkzcnx")==null?"":body.get("grzfdkzcnx").toString();
           String fqzfdkzcnx=body.get("fqzfdkzcnx")==null?"":body.get("fqzfdkzcnx").toString();
           String zfgjjdklvwnyn=body.get("zfgjjdklvwnyn")==null?"":body.get("zfgjjdklvwnyn").toString();
           String zfgjjdklvwnys=body.get("zfgjjdklvwnys")==null?"":body.get("zfgjjdklvwnys").toString();

           logger.info("[-]调用接口 获取贷款政策信息结束");

           /*出口参数赋值开始*/
           setOutParam("grzfdkzged",grzfdkzged);//个人住房贷款最高额度
           setOutParam("fqzfdkzged",fqzfdkzged);//夫妻住房贷款最高额度
           setOutParam("grzfdkzcnx",grzfdkzcnx);//个人住房贷款最长年限
           setOutParam("fqzfdkzcnx",fqzfdkzcnx);//夫妻住房贷款最长年限
           setOutParam("zfgjjdklvwnyn",zfgjjdklvwnyn);//住房公积金贷款利率五年以内
            setOutParam("zfgjjdklvwnys",zfgjjdklvwnys);//住房公积金贷款利率五年以上
           /*出口参数赋值结束*/
       return 0;
  }

}
