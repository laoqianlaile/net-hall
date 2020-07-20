package com.yd.ish.biz.comp.dp;

import com.yd.biz.comp.BaseComp;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 名称：CompDWHJ03.java
 * <p>功能：单位汇缴-计算暂存户转出金额 <br>
 *
 * @author 王赫
 * @version 0.1    2018年9月21日	王赫创建
 */
@Component("CompDWHJ03")
public class CompDWHJ03 extends BaseComp {

    private static final Logger logger = LoggerFactory.getLogger(CompDWHJ03.class);

    @Autowired
    ParamConfigImp paramConfigImp;

    @Override
    public int execute() {
        /*入口参数赋值开始*/
        String yjje = getString("yjje");//应缴金额
        String zchkyye = getString("zchkyye");//暂存户可用余额
    	/*入口参数赋值结束*/
        yjje = yjje.replaceAll(",","");
        zchkyye = zchkyye.replaceAll(",","");
        String jg = "0";

        //暂存户转出金额
        BigDecimal zchzcje = new BigDecimal(0.00);
        DecimalFormat format = new DecimalFormat("0.00");
        BigDecimal _yjje = new BigDecimal(yjje);
        BigDecimal _zchkyye = new BigDecimal(zchkyye);
        //如果暂存户转出金额>应缴金额
        if(_zchkyye.compareTo(_yjje)>=0) {
            //暂存户转出金额=暂存户转出金额-应缴金额
            zchzcje = _yjje;
        }else {
            jg = "1";
        }
    	/*出口参数赋值开始*/
        setOutParam("zchzcje", format.format(zchzcje));//暂存户转出金额
        setOutParam("jg", jg);//计算结果标识
    	/*出口参数赋值结束*/
        return 0;
    }
}
