package com.yd.ish.flowhelper.dw;

import com.yd.basic.util.YDVoucherUtil;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.workflow.FlowHelperI;
import dm.jdbc.filter.stat.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 个人开户
 */
@Component
public class FlowGRKH implements FlowHelperI {
    private static final Logger logger = LoggerFactory.getLogger(FlowGRKH.class);

    private static final String STEP_STEP1 = "step1";// 偿还公积金贷款提取
    @Autowired
    YDVoucherUtil yDVoucherUtil;
    /*
     * 交易调用示例，trancode为具体交易代码
     * TransEngine.getInstance().execute("trancode",mainContext);
     */

    @Override
    public boolean in(String stepid, String type, MainContext mainContext) {
        //获取单位基本信息
        if(stepid.equals(STEP_STEP1)){
            TransEngine.getInstance().execute("TranDWJBXXCX", mainContext);
        }
        //pdf打印
        if("step2".equals(stepid)){
            HashMap<String, Object> map = new HashMap<String, Object>();
            //获取打印模板
            String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "GRKHDY");
            map.put("pdfKey", poolkey);
            DataPool pool = MainContext.currentMainContext().getDataPool();
            pool.put("pdfKey",poolkey);
            return true;
        }
        if("step3".equals(stepid)){
            HashMap<String, Object> map = new HashMap<String, Object>();
            //获取打印模板
            String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "GRKHDY");
            map.put("pdfKey", poolkey);
            DataPool pool = MainContext.currentMainContext().getDataPool();
            pool.put("pdfKey",poolkey);
            return true;
        }
        return true;
    }

    @Override
    public boolean out(String stepid, MainContext mainContext) {
        if(stepid.equals(STEP_STEP1)){
            TransEngine.getInstance().execute("TranGRKH1", mainContext);
        }
        if(stepid.equals("step2")){
            TransEngine.getInstance().execute("TranGRKH2", mainContext);
        }
        return true;
    }

    @Override
    public String cmd(String stepid, String task, MainContext mainContext) {

        return null;

    }
}
