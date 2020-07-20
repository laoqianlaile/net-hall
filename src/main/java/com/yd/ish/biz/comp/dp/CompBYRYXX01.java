package com.yd.ish.biz.comp.dp;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 名称：CompBYRYXX01.java
 * <p>功能：单位下本月人员信息 <br>
 *
 * @author 许永峰
 * @version 0.1    2019年11月26日	许永峰创建
 */
@Component("CompBYRYXX01")
public class CompBYRYXX01 extends BaseComp {
    private static final Logger logger = LoggerFactory.getLogger(CompBYRYXX01.class);


    @Override

    public int execute() {
        /*入口参数赋值开始*/

        /*入口参数赋值结束*/
        logger.info("[-] 调用BSP_DP_HQBYRYXX_01接口开始");
        XmlResObj data = super.sendExternal("BSP_DP_HQBYRYXX_01");
        XmlResHead head = data.getHead();
        if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
            throw new TransSingleException(head.getParticular_code(), head.getParticular_info());
        }
        Map<String, Object> body = data.getBody();
        String zrs = body.get("zrs") == null ? "" : body.get("zrs").toString();
        String zrrs = body.get("zrrs") == null ? "" : body.get("zrrs").toString();
        String zcrs = body.get("zcrs") == null ? "" : body.get("zcrs").toString();
        String fcrs = body.get("fcrs") == null ? "" : body.get("fcrs").toString();
        String qfrs = body.get("qfrs") == null ? "" : body.get("qfrs").toString();
        String khrs = body.get("khrs") == null ? "" : body.get("khrs").toString();
        logger.info("[-] 调用BSP_DP_HQBYRYXX_01接口结束");


        /*出口参数赋值开始*/
        setOutParam("zrs", zrs);//总人数
        setOutParam("zrrs", zrrs);//转入人数
        setOutParam("zcrs", zcrs);//转出人数
        setOutParam("fcrs", fcrs);//封存人数
        setOutParam("qfrs", qfrs);//启封人数
        setOutParam("khrs", khrs);//开户人数
        /*出口参数赋值结束*/
        return 0;
    }
}
