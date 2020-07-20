package com.yd.ish.biz.comp.dp;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.dao.mybatis.DP077Mapper;
import com.yd.ish.model.mybatis.DP077;
import com.yd.org.util.CommonErrorCode;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * 名称：CompDWBJ03.java
 * <p>功能：单位补缴-计算实际补缴总金额 <br>
 *
 * @author 王赫
 * @version 0.1    2018年9月13日	王赫创建
 */
@Component("CompDWBJ03")
public class CompDWBJ03 extends BaseComp {

    private static final Logger logger = LoggerFactory.getLogger(CompDWBJ03.class);

    @Autowired
    ParamConfigImp paramConfigImp;

    @Autowired
    DP077Mapper dp077Mapper;

    @Override
    public int execute() {
        /*入口参数赋值开始*/
        String jkfs = getString("jkfs");//缴款方式
        String zchkyye = getString("zchkyye");//暂存户可用余额
        /*入口参数赋值结束*/

        //缴款方式
        paramConfigImp.loadMaskData(new String[]{"bsp.dp.jkfs"});
        //缴款方式--暂存户转出
        String zchzc = paramConfigImp.getValByMask("bsp.dp.jkfs", "zchzc");
        int index = 0;
        String jg = "0";
        //实缴金额
        BigDecimal sjje = new BigDecimal(0.00);
        //暂存户转出金额
        BigDecimal zchzcje = new BigDecimal(0.00);
        MainContext mainContext = MainContext.currentMainContext();
        int slh = Integer.parseInt(mainContext.getDataPool().getString("_IS"));
        List<DP077> list =  dp077Mapper.selectBySlh(slh);
        DecimalFormat format = new DecimalFormat("0.00");
        DP077 dp077 = new DP077();
        if(list!=null&& list.size()>0){
            for (int i=0;i<list.size();i++){
                dp077 = list.get(i);
                sjje = sjje.add(dp077.getAmt1());
                index++;
            }
        }

        //如果缴款方式为暂存户转出
        if(zchzc.equals(jkfs)){
            BigDecimal _zchkyye = new BigDecimal(zchkyye);
            if(_zchkyye.compareTo(sjje)>=0){
//                zchzcje = _zchkyye.subtract(sjje1);
                zchzcje = sjje;
            }else{
//                throw new TransSingleException("暂存户可用余额不足!");
                jg = "1";
            }
        }

        /*出口参数赋值开始*/
        setOutParam("sjje", format.format(sjje));//实缴金额
        setOutParam("bjrs", index);//补缴人数
        setOutParam("zchzcje", format.format(zchzcje));//暂存户转出金额
        setOutParam("jg", jg);//计算结果标识
        /*出口参数赋值结束*/

        return 0;
    }
}
