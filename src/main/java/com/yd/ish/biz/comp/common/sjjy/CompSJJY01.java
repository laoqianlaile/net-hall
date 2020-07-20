package com.yd.ish.biz.comp.common.sjjy;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransOtherException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.util.DataPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 名称：CompSJJY01
 * 功能：DP077表包含某个数据校验
 * 作者：许永峰
 * 修改记录：
 * 版本编号  修改人  修改日期  地点  原因
 */
@Component("CompSJJY01")
public class CompSJJY01 extends BaseComp {

    private static final Logger logger = LoggerFactory.getLogger(CompSJJY01.class);
    @Autowired
    DP077Service dp077service;

    @Override
    public int execute() {
        /* 入口参数赋值开始 */
        //实例号
        int instance = getInt("instance");
        //要校验的数据值
        String checkValue = getString("checkValue");
        //对应的实体属性
        String entityName = getString("entityName");
        /* 入口参数赋值结束 */
        //组装mybatis查询参数
        Map<String, Object> map = new HashMap<String, Object>();
        //实例号查询条件值
        map.put("instance", instance);
        //自定义属性查询条件
        map.put("field", entityName);
        //自定义查询条件值
        map.put("fieldValue", checkValue);
        List<DP077> list = dp077service.selectBySlhAndField(map);
        if (list.size() > 0) {
            return 0;
        } else {
            throw new TransSingleException("数据校验未通过！");
        }
    }
}
