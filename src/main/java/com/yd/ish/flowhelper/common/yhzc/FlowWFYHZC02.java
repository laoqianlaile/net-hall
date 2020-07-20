package com.yd.ish.flowhelper.common.yhzc;

import com.yd.basic.util.AesUtil;
import com.yd.biz.engine.TransEngine;
import com.yd.biz.exception.TransSingleException;
import com.yd.org.util.CommonErrorCode;
import com.yd.org.util.EncryptionUtil;
import com.yd.org.util.GetChannelCode;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.Datelet;
import com.yd.svrplatform.util.JsonUtil;
import com.yd.workflow.FlowHelperI;
import com.yd.workflow.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;

/**
 * 名称：FlowWFYHZC02.java
 * <p>功能：单位临时用户注册流程助手 <br>
 * @author 王赫
 * @version 0.1	2019年10月16日	王赫创建
 *
 */
@Component
public class FlowWFYHZC02 implements FlowHelperI {

	private static final String STEP_STEP1="step1";//个人信息录入

	
	private static final Logger logger = LoggerFactory.getLogger(FlowWFYHZC02.class);

	@Autowired
	ParamConfigImp paramConfigImp;
	
	/* 交易调用示例，trancode为具体交易代码
		TransEngine.getInstance().execute("trancode",mainContext);
	*/
	
	@Override
	public boolean in(String stepid, String type, MainContext mainContext) {
        DataPool dataPool = mainContext.getDataPool();
        dataPool.put("token",getRandomString(16));
        mainContext.setDataPool(dataPool);
		return true;
	}

	@Override
	public boolean out(String stepid, MainContext mainContext) {
        if(STEP_STEP1.equals(stepid)){
            return out_step01(mainContext);
        }
		return true;
	}

	@Override
	public String cmd(String stepid, String task, MainContext mainContext) {
	    if(STEP_STEP1.equals(stepid)&&"cmd001".equals(task)){
	        return cmd001(mainContext);
        }
		return null;
	}

    /**
     * 发送验证码
     * @param mainContext
     * @return
     */
    private String cmd001(MainContext mainContext){
        DataPool pool=mainContext.getDataPool();
        // 用户预留手机号
        pool.put("ylsjh",pool.getString("jbrsjhm"));
        //调用发送短信验证码交易 ,会调用KF00-API-COM-001接口
        Map<String,Object> map = TransEngine.getInstance().execute("GGTrans_0002",mainContext);
        logger.info("---发送短信验证码返回数据："+JsonUtil.getJsonString(map));
        return JsonUtil.getJsonString(map);
    }

    /**
     * 验证用户输入的验证码是否正确
     * @param mainContext
     * @return
     */
    private boolean out_step01(MainContext mainContext){
        DataPool datapool = mainContext.getDataPool();
        // 用户预留手机号
        datapool.put("ylsjh",datapool.getString("jbrsjhm"));
        String hdlmm = datapool.getString("hdlmm");
        String token = datapool.getString("token");
        String dlmm = AesUtil.deCode(hdlmm,token);
        if(StringUtils.isNotBlank(dlmm)){
            datapool.put("dlmm", EncryptionUtil.MD5Encode(dlmm));
        }
        //验证码
//        datapool.put("dxyzm",datapool.getString("dxyzm"));
        // 调用验证码录入是否正确校验
        TransEngine.getInstance().execute("GGTrans_0003",mainContext);
        TransEngine.getInstance().execute("TranLSYHZC01",mainContext);

        return true;
    }

    /**生成随机字符串
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }


}
