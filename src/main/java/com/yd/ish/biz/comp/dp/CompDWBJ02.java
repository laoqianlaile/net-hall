package com.yd.ish.biz.comp.dp;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 名称：CompDWBJ02.java
 * <p>功能：单位补缴-获取个人补缴信息 <br>
 *
 * @author 王赫
 * @version 0.1    2018年9月12日	王赫创建
 */
@Component("CompDWBJ02")
public class CompDWBJ02 extends BaseComp {

    private static final Logger logger = LoggerFactory.getLogger(CompDWBJ02.class);

    @Override
    public int execute() {
        /*入口参数赋值开始*/
        /*入口参数赋值结束*/
        //开始调用接口BSP_DP_DWBJXX_02获取个人补缴信息
        logger.info("[+] 调用BSP_DP_DWBJXX_02接口获取个人补缴信息开始!");
        XmlResObj data = super.sendExternal("BSP_DP_DWBJXX_02");
        XmlResHead head = data.getHead();
        if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
            throw new TransSingleException(head.getParticular_code(), head.getParticular_info());
        }
        Map<String, Object> body = data.getBody();
        String xingming = body.get("xingming") == null ? "" : body.get("xingming").toString();
        String zjhm = body.get("zjhm") == null ? "" : body.get("zjhm").toString();
        String bjje = body.get("bjje") == null ? "" : body.get("bjje").toString();
        logger.info("[-] 调用BSP_DP_DWBJXX_02接口获取个人补缴信息结束!");
        /*出口参数赋值开始*/
        setOutParam("xingming", xingming);//姓名
        setOutParam("zjhm", zjhm);//证件号码
        setOutParam("bjje", bjje);//补缴金额
        /*出口参数赋值结束*/

        return 0;
    }
}
