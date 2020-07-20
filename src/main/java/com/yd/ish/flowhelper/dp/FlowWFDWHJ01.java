package com.yd.ish.flowhelper.dp;

import com.alibaba.druid.support.json.JSONUtils;
import com.yd.basic.util.YDVoucherUtil;
import com.yd.biz.engine.TransEngine;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.util.DataPool;
import com.yd.workflow.FlowHelperI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 名称：FlowWFDWHJ01.java
 * <p>功能： 汇缴流程助手<br>
 *
 * @author wanghe
 * @version 0.1    2018年8月16日	wanghe创建
 */
@Component
public class FlowWFDWHJ01 implements FlowHelperI {


    private static final String STEP_STEP1 = "step1";//汇缴
    private static final String STEP_STEP2 = "step2";//打印凭证
    private static final String JKFS_ZCKZC = "zchzc";//缴款方式-暂存户转出
    private static final String JKFS_SSJS = "ssjs";//缴款方式-实时结算
    @Autowired
    YDVoucherUtil yDVoucherUtil;
    @Autowired
    ParamConfigImp paramconfigImp;

	/*交易调用示例，trancode为具体交易代码
    TransEngine.getInstance().execute("trancode",mainContext);
	*/

    @Override
    public boolean in(String stepid, String type, MainContext mainContext) {
        if(STEP_STEP1.equals(stepid)){
            return in_step1(mainContext,type);
        }
        if(STEP_STEP2.equals(stepid)){
            return in_step2(mainContext,type);
        }
        return true;
    }

    @Override
    public boolean out(String stepid, MainContext mainContext) {
        if (STEP_STEP1.equals(stepid)) {
            out_step1(mainContext);
        }
        return true;
    }

    @Override
    public String cmd(String stepid, String task, MainContext mainContext) {
        if ("HQDWHJXX".equals(task)) {
            return cmd_step1_02(mainContext);
        }else if("HQZCKZZJE".equals(task)){
            return cmd_step1_03(mainContext);
        }
        return null;
    }

    //提交汇缴信息
    private boolean out_step1(MainContext mainContext) {
        TransEngine.getInstance().execute("TranDWHJTJ01", mainContext);
        return true;
    }

    //获取单位汇缴信息
    private String cmd_step1_02(MainContext mainContext) {
        Map<String, Object> map = TransEngine.getInstance().execute("TranDWHJ02", mainContext);
        return JSONUtils.toJSONString(map);
    }

    //计算暂存户转出金额
    private String cmd_step1_03(MainContext mainContext) {
        Map<String, Object> map = TransEngine.getInstance().execute("TranDWHJ03", mainContext);
        return JSONUtils.toJSONString(map);
    }
    private boolean in_step1(MainContext mainContext,String type) {
        TransEngine.getInstance().execute("TranDWHJ01",mainContext);
        return true;
    }
    //打印凭证
    private boolean in_step2(MainContext mainContext,String type) {
        //暂存款和实时结算打印入账回单，其他的打印受理回单
        DataPool dataPool = mainContext.getDataPool();
        // 获取打印模板
        String jkfs = dataPool.getString("jkfs");
        String jkfsMask = paramconfigImp.getMask("bsp.dp.jkfs." + jkfs);
        if (JKFS_ZCKZC.equals(jkfsMask) || JKFS_SSJS.equals(jkfsMask)) {
            dataPool.put("_prfilename", "缴存入账回单");
            String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "DWHJPZ02");
            mainContext.getDataPool().put("pdfKey", poolkey);
        } else {
            dataPool.put("_prfilename", "汇(补)缴业务受理回单");
            String poolkey = yDVoucherUtil.savePdfVoucher(mainContext.getDataPool(), "DWHJPZ01");
            mainContext.getDataPool().put("pdfKey", poolkey);
        }
        return true;
    }

}
