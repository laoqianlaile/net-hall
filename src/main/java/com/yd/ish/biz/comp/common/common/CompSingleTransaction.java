package com.yd.ish.biz.comp.common.common;

import com.yd.biz.config.CompBean;
import com.yd.biz.exception.TransSingleException;
import com.yd.org.util.CommonErrorCode;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.DataPool;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 名称：CompSingleTransaction.java
 * <p>功能：通用单笔交易 <br>
 *
 * @author 林楠
 * @version 0.1    2018年9月4日	林楠创建
 */
@Component("CompSingleTransaction")
public class CompSingleTransaction extends AbsCompBatchTransaction {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public int execute() {


        // 获取交易码及描述
        CompBean compBean = this.getCompBean();
        String apiId = getString(apiIdFiled());
        String apiName = getString(apiNameField(), apiId);
        DataPool pool=super.getMainContext().getDataPool();
        LOGGER.debug("jtzz================"+pool.getString("jtzz"));
        //调用单位基本信息查询时，给dwzh变量赋值 （登录也有调单位基本信息查询，所以不能在接口配置里写死_ORGID	）
        if(StringUtils.isEmpty(pool.getString("dwzh"))){
        	pool.put("dwzh", pool.getString("_ORGID"));
        }
//        String apiSetOutType = getString(apiSetOutType(), DEFAULT_APISETOUTTYPE);
        LOGGER.info("[+]调用查询{}{}开始", apiId, apiName);
        if (StringUtils.isEmpty(apiId)) {
            throw new TransSingleException(CommonErrorCode.ERROR_NOT_NULL, "交易码");
        }
        XmlResObj data = super.sendExternal(apiId);
        XmlResHead head = data.getHead();
        if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
            throw new TransSingleException(head.getParticular_code(), head.getParticular_info());
        } else {
            /*出口参数赋值开始*/
            LOGGER.info("[+]获取{}接口返回信息开始", apiId);

            setCompOutParams( data , compBean , apiId);
            
            LOGGER.info("[-] 获取{}接口返回的信息结束", apiId);

            /*出口参数赋值结束*/
            return 0;
        }
    }
}
