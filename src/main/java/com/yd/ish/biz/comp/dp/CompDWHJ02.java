package com.yd.ish.biz.comp.dp;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.ReadProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * 名称：CompDWHJ01.java
 * <p>功能：单位汇缴-单位汇缴信息查询 <br>
 *
 * @author 王赫
 * @version 0.1    2018年8月21日	王赫创建
 */
@Component("CompDWHJ02")
public class CompDWHJ02 extends BaseComp {

    private static final Logger logger = LoggerFactory.getLogger(CompDWHJ02.class);

    /**
     * 数据分割符
     */
    private static final String separators = ReadProperty.getString("file_separators");
    /**
     * 读核心返回文件编码格式
     */
    private static final String encoding = ReadProperty.getString("readfile_encoding_bsp");
    /**
     * 数据库类型
     */
    private static final String DB_TYPE = ReadProperty.getString("dbType");

    @Autowired
    DP077Service dp077service;

    @Override
    public int execute() {
        /*入口参数赋值开始*/

        /*入口参数赋值结束*/
        logger.info("[+]获取单位汇缴信息接口BSP_DP_HQDWHJXX_01开始...");
        XmlResObj data = super.sendExternal("BSP_DP_HQDWHJXX_01");
        XmlResHead head = data.getHead();
        if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
            throw new TransSingleException(head.getParticular_code(), head.getParticular_info());
        }

        Map<String, Object> body = data.getBody();
        String yjje = body.get("yjje") == null ? "" : body.get("yjje").toString();
        String hjrs = body.get("hjrs") == null ? "" : body.get("hjrs").toString();
        String hjys = body.get("hjys") == null ? "" : body.get("hjys").toString();
        String jkfs = body.get("jkfs") == null ? "" : body.get("jkfs").toString();
        logger.info("[-] 获取单位汇缴信息接口BSP_DP_HQDWHJXX_01结束...");

        /*出口参数赋值开始*/
        setOutParam("yjje", yjje);//应缴金额
        setOutParam("hjrs", hjrs);//汇缴人数
        setOutParam("hjys", hjys);//汇缴月数
        setOutParam("jkfs", jkfs);//缴款方式
        /*出口参数赋值结束*/

        return 0;
    }
}
