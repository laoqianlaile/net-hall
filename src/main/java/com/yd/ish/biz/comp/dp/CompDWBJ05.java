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
 * 名称：CompDWBJ05.java
 * <p>功能：单位补缴-获取单位最近一笔补缴信息 <br>
 *
 * @author 许永峰
 * @version 0.1    2019年11月26日	许永峰创建
 */
@Component("CompDWBJ05")
public class CompDWBJ05 extends BaseComp {

    private static final Logger logger = LoggerFactory.getLogger(CompDWBJ05.class);

    @Override

    public int execute() {
        /*入口参数赋值开始*/

        /*入口参数赋值结束*/
        //开始调用接口BSP_DP_HQZJBJXX_01获取单位最近补缴信息
        logger.info("[-] 调用BSP_DP_HQZJBJXX_01接口开始");
        XmlResObj data = super.sendExternal("BSP_DP_HQZJBJXX_01");
        XmlResHead head = data.getHead();
        if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
            throw new TransSingleException(head.getParticular_code(), head.getParticular_info());
        }
        Map<String, Object> body = data.getBody();
        String djh = body.get("djh") == null ? "" : body.get("djh").toString();
        String ksny = body.get("ksny") == null ? "" : body.get("ksny").toString();
        String jzny = body.get("jzny") == null ? "" : body.get("jzny").toString();
        String sjje = body.get("sjje") == null ? "" : body.get("sjje").toString();
        String yjje = body.get("yjje") == null ? "" : body.get("yjje").toString();
        String sjrs = body.get("sjrs") == null ? "" : body.get("sjrs").toString();
        String yjrs = body.get("yjrs") == null ? "" : body.get("yjrs").toString();
        String jkfs = body.get("jkfs") == null ? "" : body.get("jkfs").toString();
        String jclx = body.get("jclx") == null ? "" : body.get("jclx").toString();
        String zchzrje = body.get("zchzrje") == null ? "" : body.get("zchzrje").toString();
        String zchzcje = body.get("zchzcje") == null ? "" : body.get("zchzcje").toString();
        String slrq = body.get("slrq") == null ? "" : body.get("slrq").toString();
        String rzrq = body.get("rzrq") == null ? "" : body.get("rzrq").toString();
        String ywzt = body.get("ywzt") == null ? "" : body.get("ywzt").toString();
        logger.info("[-] 调用BSP_DP_HQZJBJXX_01接口结束");


        /*出口参数赋值开始*/
        setOutParam("djh", djh);//登记号
        setOutParam("ksny", ksny);//开始年月
        setOutParam("jzny", jzny);//缴至年月
        setOutParam("sjje", sjje);//实缴金额
        setOutParam("yjje", yjje);//应缴金额
        setOutParam("sjrs", sjrs);//实缴人数
        setOutParam("yjrs", yjrs);//应缴人数
        setOutParam("jkfs", jkfs);//缴款方式
        setOutParam("jclx", jclx);//缴存类型
        setOutParam("zchzrje", zchzrje);//暂存户转入金额
        setOutParam("zchzcje", zchzcje);//暂存款转出金额
        setOutParam("slrq", slrq);//受理日期
        setOutParam("rzrq", rzrq);//入账日期
        setOutParam("ywzt", ywzt);//业务状态
        /*出口参数赋值结束*/
        return 0;
    }
}
