package com.yd.ish.flowhelper.dp;

import com.yd.basic.util.YDVoucherUtil;
import com.yd.biz.engine.TransEngine;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.workflow.FlowHelperI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 名称：FlowWFDWJCMX01.java
 * <p>功能： 单位汇补缴信息查询<br>
 *
 * @author 王赫
 * @version 0.1    2018年6月7日	王赫创建
 */
@Component
public class FlowWFDWJCMX01 implements FlowHelperI {

    @Autowired
    YDVoucherUtil yDVoucherUtil;
    @Autowired
    DP077Service dp077service;

    private static final String STEP_STEP1 = "step1";
    private static final String STEP_SUBPAGE01 = "subpage01";

    @Override
    public boolean in(String stepid, String type, MainContext mainContext) {
        if (STEP_SUBPAGE01.equals(stepid)) {
            return in_subpage01(mainContext);
        }
        return true;
    }

    @Override
    public boolean out(String stepid, MainContext mainContext) {

        return true;
    }

    @Override
    public String cmd(String stepid, String task, MainContext mainContext) {
        if (STEP_STEP1.equals(stepid) && "DWJCMXCX".equals(task)) {
            return queryDWJCMX(mainContext);
        }
        if ("CMD01".equals(task)) {
            DataPool pool = mainContext.getDataPool();
            //要校验的数据
            pool.put("checkValue", pool.getString("djh"));
            //对应的实体DP077的属性名
            pool.put("entityName", "unitaccnum1");
            //调用数据校验交易
            TransEngine.getInstance().execute("TranSJJY01", mainContext);
            HashMap<String, Object> map = TransEngine.getInstance().execute("TranDWJCMX02", mainContext);
            return JsonUtil.getJsonString(map);
        }
        return null;
    }

    /**
     * 查询单位汇补缴信息
     *
     * @param mainContext
     * @return
     */
    private String queryDWJCMX(MainContext mainContext) {

        TransEngine.getInstance().execute("TranDWJCMX01", mainContext);

        return null;
    }

    /**
     * 获取打印模板
     *
     * @param mainContext
     * @return
     */
    private boolean in_subpage01(MainContext mainContext) {
        DataPool pool = mainContext.getDataPool();
        //要校验的数据
        pool.put("checkValue", pool.getString("djh"));
        //对应的实体DP077的属性名
        pool.put("entityName", "unitaccnum1");
        //调用数据校验交易
        TransEngine.getInstance().execute("TranSJJY01", mainContext);
        TransEngine.getInstance().execute("TranDWJCMX03", mainContext);
        // 获取打印模板
        String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "DWJCPZ01");
        mainContext.getDataPool().put("pdfKey", poolkey);
        return true;
    }


}
