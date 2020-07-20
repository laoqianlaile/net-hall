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
public class FlowGRZY implements FlowHelperI {
    private static final Logger logger = LoggerFactory.getLogger(FlowGRZY.class);

    private static final String STEP_STEP1 = "step1";
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
            String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "GRZYDY");
            map.put("pdfKey", poolkey);
            DataPool pool = MainContext.currentMainContext().getDataPool();
            pool.put("pdfKey",poolkey);
            return true;
        }
        if("step3".equals(stepid)){
            HashMap<String, Object> map = new HashMap<String, Object>();
            //获取打印模板
            String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "GRZYDY");
            map.put("pdfKey", poolkey);
            DataPool pool = MainContext.currentMainContext().getDataPool();
            pool.put("pdfKey",poolkey);
            return true;
        }
        if("step4".equals(stepid)){
            HashMap<String, Object> map = new HashMap<String, Object>();
            //获取打印模板
            String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "GRZYDY");
            map.put("pdfKey", poolkey);
            DataPool pool = MainContext.currentMainContext().getDataPool();
            pool.put("pdfKey",poolkey);
            return true;
        }
        if("step5".equals(stepid)){
            HashMap<String, Object> map = new HashMap<String, Object>();
            //获取打印模板
            String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "GRZYDY");
            map.put("pdfKey", poolkey);
            DataPool pool = MainContext.currentMainContext().getDataPool();
            pool.put("pdfKey",poolkey);
            return true;
        }
        if("step6".equals(stepid)){
            HashMap<String, Object> map = new HashMap<String, Object>();
            //获取打印模板
            String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "GRZYDY");
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
            TransEngine.getInstance().execute("TranGRZY1", mainContext);
        }
        if(stepid.equals("step2")){
            TransEngine.getInstance().execute("TranGRZY2", mainContext);
        }
        if(stepid.equals("step3")){
            TransEngine.getInstance().execute("TranGRZY3", mainContext);
        }
        if(stepid.equals("step4")){
            TransEngine.getInstance().execute("TranGRZY4", mainContext);
        }
        if(stepid.equals("step5")){
            TransEngine.getInstance().execute("TranGRZY5", mainContext);
        }
        return true;
    }

    @Override
    public String cmd(String stepid, String task, MainContext mainContext) {
        if(task.equals("cmdDWJBXXCX2")){
            Map<String,Object> rtnMap1 = TransEngine.getInstance().execute("TranDWJBXXCX2", mainContext);
            return JSON.toJSONString(rtnMap1);
        }
        if(task.equals("cmdGRXXCX")){
            Map<String,Object> rtnMap2 = TransEngine.getInstance().execute("TranGRXXCX", mainContext);
            return JSON.toJSONString(rtnMap2);
        }
        return null;

    }
}
