package com.yd.ish.biz.comp.dp;

import com.alibaba.druid.support.json.JSONUtils;
import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransOtherException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.GetDownFileMap;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.svrplatform.comm_mdl.dataswap.file.FileSwap;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.comm_mdl.log.YDLogger;
import com.yd.svrplatform.util.ReadProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 名称：CompDWHJ01.java
 * <p>功能：单位汇缴-单位账户查询 <br>
 *
 * @author 王赫
 * @version 0.1    2018年8月20日	王赫创建
 */
@Component("CompDWHJ01")
public class CompDWHJ01 extends BaseComp {

    private static final Logger logger = LoggerFactory.getLogger(CompDWHJ01.class);


    /**
     * 数据分割符
     */
    private static final String separators = ReadProperty.getString("file_separators");
    /**
     * 读核心返回文件编码格式
     */
    private static final String encoding = ReadProperty.getString("readfile_encoding_bsp");

    @Autowired
    DP077Service dp077service;

    @Autowired
    GetDownFileMap getDownFileMap;

    @Override

    public int execute() {
        /*入口参数赋值开始*/

        /*入口参数赋值结束*/
        //开始调用接口BSP-DP-HQHJZHXX-01获取单位汇缴账户信息
        logger.info("[-] 调用BSP_DP_HQHJZHXX_01接口开始");
        XmlResObj data = super.sendExternal("BSP_DP_HQHJZHXX_01");
        XmlResHead head = data.getHead();
        if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
            throw new TransSingleException(head.getParticular_code(), head.getParticular_info());
        }
        Map<String, Object> body = data.getBody();
        String jzny = body.get("jzny") == null ? "" : body.get("jzny").toString();
        String zchkyye = body.get("zchkyye") == null ? "" : body.get("zchkyye").toString();
        String fkzh = body.get("fkzh") == null ? "" : body.get("fkzh").toString();
        String fkzhm = body.get("fkzhm") == null ? "" : body.get("fkzhm").toString();
        String fkyh = body.get("fkyh") == null ? "" : body.get("fkyh").toString();
        logger.info("[-] 调用BSP_DP_HQHJZHXX_01接口结束");


        /*出口参数赋值开始*/
        setOutParam("jzny", jzny);//缴至年月
        setOutParam("zchkyye", zchkyye);//暂存户可用余额
        setOutParam("fkzh", fkzh);//付款账号
        setOutParam("fkzhm", fkzhm);//付款账户名
        setOutParam("fkyh", fkyh);//付款银行
        /*出口参数赋值结束*/
        return 0;
    }
}
