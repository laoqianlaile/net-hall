package com.yd.ish.flowhelper.dw;

import com.alibaba.fastjson.JSON;
import com.yd.basic.util.YDVoucherUtil;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import com.yd.workflow.FlowHelperI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
@Component
public class FlowZXBJ implements FlowHelperI {
    private static final Logger logger = LoggerFactory.getLogger(FlowZXBJ.class);

    private static final String STEP_STEP1 = "step1";// 偿还公积金贷款提取
    private static final String STEP_STEP2 = "step2";
    private static final String STEP_STEP3 = "step3";
    private static final String STEP_STEP4 = "step4";
    @Autowired
    YDVoucherUtil yDVoucherUtil;
    /*
     * 交易调用示例，trancode为具体交易代码
     * TransEngine.getInstance().execute("trancode",mainContext);
     */

    @Override
    public boolean in(String stepid, String type, MainContext mainContext) {
        if(stepid.equals(STEP_STEP1)){
            TransEngine.getInstance().execute("TranDWJBXXCX", mainContext);
        }
        //pdf打印
        if("step2".equals(stepid)){
            HashMap<String, Object> map = new HashMap<String, Object>();
            //获取打印模板
            String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "ZXBJDY");
            map.put("pdfKey", poolkey);
            DataPool pool = MainContext.currentMainContext().getDataPool();
            pool.put("pdfKey",poolkey);
            return true;
        }
        if(STEP_STEP3.equals(stepid)){
            HashMap<String, Object> map = new HashMap<String, Object>();
            //获取打印模板
            String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "ZXBJDY");
            map.put("pdfKey", poolkey);
            DataPool pool = MainContext.currentMainContext().getDataPool();
            pool.put("pdfKey",poolkey);
            return true;
        }
        if(STEP_STEP4.equals(stepid)){
            HashMap<String, Object> map = new HashMap<String, Object>();
            //获取打印模板
            String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "ZXBJDY");
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
            TransEngine.getInstance().execute("TranZXBJ1", mainContext);
        }
        if(stepid.equals(STEP_STEP2)){
            TransEngine.getInstance().execute("TranZXBJ2", mainContext);
        }
        if(stepid.equals(STEP_STEP3)){
            TransEngine.getInstance().execute("TranZXBJ3", mainContext);
        }
        return true;
    }

    @Override
    public String cmd(String stepid, String task, MainContext mainContext) {
        if(task.equals("cmdGRXXCX")){
            Map<String,Object> rtnMsg = TransEngine.getInstance().execute("TranGRXXCX", mainContext);
            return JSON.toJSONString(rtnMsg);
        }
        return null;

    }
}
