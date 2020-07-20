package com.yd.ish.flowhelper.dw;

import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.workflow.FlowHelperI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FlowGRZYCX implements FlowHelperI {
    private static final Logger logger = LoggerFactory.getLogger(FlowGRZYCX.class);

    private static final String STEP_STEP1 = "step1";// 偿还公积金贷款提取

    /*
     * 交易调用示例，trancode为具体交易代码
     * TransEngine.getInstance().execute("trancode",mainContext);
     */

    @Override
    public boolean in(String stepid, String type, MainContext mainContext) {
        if(stepid.equals(STEP_STEP1)){
            TransEngine.getInstance().execute("TranDWJBXXCX", mainContext);
        }

        return true;
    }

    @Override
    public boolean out(String stepid, MainContext mainContext) {

        return true;
    }

    @Override
    public String cmd(String stepid, String task, MainContext mainContext) {
        if(task.equals("cmdGRZYCX")){
            TransEngine.getInstance().execute("TranGRZYCX", mainContext);
        }
        return null;

    }
}
