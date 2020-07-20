package com.yd.ish.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yd.basic.expression.IshExpression;
import com.yd.biz.engine.TransEngine;
import com.yd.biz.exception.TransSingleException;
import com.yd.deplatform.util.PropertyUtil;
import com.yd.ish.common.flowevent.YDFlowEvent;
import com.yd.ish.common.util.StringUtils;
import com.yd.ish.dataanalysis.service.mybatis.FuncLogService;
import com.yd.ish.dataanalysis.service.mybatis.FuncRecommendService;
import com.yd.ish.dataanalysis.util.DoubleUtil;
import com.yd.ish.util.MoneyUtil;
import com.yd.ish.util.UserViewISH04Util;
import com.yd.org.service.InitMemcached.InitAllFuncToMemcached;
import com.yd.org.service.mybatis.CPLFuncService;
import com.yd.org.util.CommonErrorCode;
import com.yd.svrplatform.comm_mdl.cache.YDMemcachedManager;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.PoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;


/**
 * 名称：OrgViewController.java
 * <p>功能："单位视图"组装接口(包括单位首页和我的账户视图) <br>
 *
 * @author 许永峰
 * v0.1	2019年11月05日 许永峰创建
 */
@Controller
@RequestMapping("/template/ish04/dwst")
public class OrgViewController {
    @Autowired
    FuncRecommendService funcRecommendService;
    @Autowired
    ParamConfigImp paramConfigImp;
    @Autowired
    InitAllFuncToMemcached initAllFuncToMemcached;
    @Autowired
    CPLFuncService cplFuncService;
    @Autowired
    FuncLogService funcLogService;

    private static final Logger logger = LoggerFactory.getLogger(OrgViewController.class);
    private static final String icon = PropertyUtil.getString("yhst.properties", "yhst.gg.ywsj.icon");
    @Autowired
    UserViewISH04Util userViewISH04Util;
    @Autowired
    YDFlowEvent ydFlowEvent;

    /**
     * 获取单位视图视图-单位首页-您可能要办理模块接口
     * 不需修改
     *
     * @param request 请求
     * @return JSONObject
     */
    @RequestMapping(value = "/dwsy/hqnknybl",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject hqNknybl(HttpServletRequest request) {
        long start = System.currentTimeMillis();
        String moduleName_Chi = "单位视图您可能要办理模块";
        logger.info("[+]调用组装" + moduleName_Chi + "接口开始");
        JSONObject nknyblJson = new JSONObject();
        try {
            String systemTypeMask = "WTDW";
            nknyblJson = userViewISH04Util.hqNknybl(request, systemTypeMask);
        } catch (Exception e) {
            nknyblJson.put("returnCode", 1);
            nknyblJson.put("message", "获取" + moduleName_Chi + "数据失败");
            nknyblJson.put("data", new JSONObject());
            logger.error("获取" + moduleName_Chi + "数据失败:" + e);
            e.printStackTrace();

        }
        logger.info("[-]调用组装" + moduleName_Chi + "接口结束" + (System.currentTimeMillis() - start) + "毫秒");
        return nknyblJson;
    }

    @RequestMapping(value = "/dwsy/hqwdsb",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject hqWdsb(HttpServletRequest request) {
        long start = System.currentTimeMillis();
        String moduleName_Chi = "单位视图我的申报模块";
        String moduleName_pzwj = "dwst.dwsy.wdsb.sbxq.menu";
        logger.info("[+]调用组装" + moduleName_Chi + "接口开始");
        JSONObject funcInfosJson = new JSONObject();
        try {
            funcInfosJson = userViewISH04Util.hqWdsb(request, moduleName_Chi, moduleName_pzwj);
        } catch (Exception e) {
            funcInfosJson.put("returnCode", 1);
            funcInfosJson.put("message", "获取" + moduleName_Chi + "数据失败");
            funcInfosJson.put("data", new JSONObject());
            logger.error("获取" + moduleName_Chi + "数据失败:" + e);
            e.printStackTrace();
        }
        logger.info("[-]调用组装" + moduleName_Chi + "接口结束" + (System.currentTimeMillis() - start) + "毫秒");
        return funcInfosJson;
    }

    /**
     * 获取单位视图-单位首页-归集业务模块接口
     * 不需要修改，只需修改yhst.properties配置文件中dwst.dwsy.gjyw.menu值
     *
     * @param request 请求
     * @return JSONObject
     */
    @RequestMapping(value = "/dwsy/hqgjyw",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject hqGjyw(HttpServletRequest request) {
        long start = System.currentTimeMillis();
        // 模块名字 打印日志时用
        String moduleName_Chi = "单位视图归集业务模块";
        //配置文件中模块属性
        String moduleName_pzwj = "dwst.dwsy.gjyw.menu";
        //按钮ID前缀
        String btnIdPre = userViewISH04Util.BTNPRE + "gjyw";
        logger.info("[+]调用组装" + moduleName_Chi + "接口开始");
        //要返回的json  格式{‘returnCode’ : 0, ’message’ : ‘‘, ’data’ : {}}
        JSONObject funcInfosJson = new JSONObject();
        try {
            funcInfosJson = userViewISH04Util.initFuncs(request, moduleName_Chi, moduleName_pzwj, btnIdPre);
            logger.info("获取" + moduleName_Chi + "数据成功，data=" + funcInfosJson.get("data"));
        } catch (Exception e) {
            funcInfosJson.put("returnCode", 1);
            funcInfosJson.put("message", "获取" + moduleName_Chi + "数据失败");
            funcInfosJson.put("data", new JSONObject());
            logger.error("获取" + moduleName_Chi + "数据失败:" + e);
            e.printStackTrace();
        }
        logger.info("[-]调用组装" + moduleName_Chi + "接口结束" + (System.currentTimeMillis() - start) + "毫秒");
        return funcInfosJson;

    }

    /**
     * 获取单位视图-单位首页-我要查询模块接口
     * 不需要修改，只需修改yhst.properties配置文件中grst.grsy.pzdy.menu值
     *
     * @param request 请求
     * @return JSONObject
     */
    @RequestMapping(value = "/dwsy/hqwycx",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject hqWycx(HttpServletRequest request) {
        long start = System.currentTimeMillis();
        // 模块名字 打印日志时用
        String moduleName_Chi = "单位视图我要查询模块";
        //配置文件中模块属性
        String moduleName_pzwj = "dwst.dwsy.wycx.menu";
        //按钮ID前缀
        String btnIdPre = userViewISH04Util.BTNPRE + "wycx";
        logger.info("[+]调用组装" + moduleName_Chi + "接口开始");
        //要返回的json  格式{‘returnCode’ : 0, ’message’ : ‘‘, ’data’ : {}}
        JSONObject funcInfosJson = new JSONObject();
        try {
            funcInfosJson = userViewISH04Util.initFuncs(request, moduleName_Chi, moduleName_pzwj, btnIdPre);
            logger.info("获取" + moduleName_Chi + "数据成功，data=" + funcInfosJson.get("data"));
        } catch (Exception e) {
            funcInfosJson.put("returnCode", 1);
            funcInfosJson.put("message", "获取" + moduleName_Chi + "数据失败");
            funcInfosJson.put("data", new JSONObject());
            logger.error("获取" + moduleName_Chi + "数据失败:" + e);
            e.printStackTrace();
        }
        logger.info("[-]调用组装" + moduleName_Chi + "接口结束" + (System.currentTimeMillis() - start) + "毫秒");
        return funcInfosJson;

    }

    /**
     * 获取单位视图-单位首页-最近操作记录模块接口（无需修改）
     *
     * @param request 请求request
     * @return JSONObject
     */
    @RequestMapping(value = "/dwsy/hqzjczjl",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject hqZjczjl(HttpServletRequest request) {
        long start = System.currentTimeMillis();
        // 模块名字 打印日志时用
        String moduleName_Chi = "单位视图最近操作记录模块";
        logger.info("[+]调用组装" + moduleName_Chi + "接口开始");
        //配置文件中模块属性
        String moduleName_pzwj = "dwst.dwsy.zjczjl.menu";
        //按钮ID前缀
        String btnIdPre = userViewISH04Util.BTNPRE + "zjczjl";
        String systemTypeMask = "WTDW";
        JSONObject funcInfosJson = new JSONObject();
        try {
            funcInfosJson = userViewISH04Util.hqZjczjl(request, moduleName_Chi, moduleName_pzwj, systemTypeMask);
        } catch (Exception e) {
            funcInfosJson.put("returnCode", 1);
            funcInfosJson.put("message", "获取" + moduleName_Chi + "数据失败");
            funcInfosJson.put("data", new JSONObject());
            logger.error("获取" + moduleName_Chi + "数据失败:" + e);
            e.printStackTrace();
        }
        logger.info("[-]调用组装" + moduleName_Chi + "接口结束" + (System.currentTimeMillis() - start) + "毫秒");
        return funcInfosJson;
    }

    /**
     * 获取单位视图-单位首页-单位视图汇缴信息模块接口（无需修改）
     *
     * @param request 请求request
     * @return JSONObject
     */
    @RequestMapping(value = "/dwsy/hqhjxx",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject hqHjxx(HttpServletRequest request) {
        long start = System.currentTimeMillis();
        // 模块名字 打印日志时用
        String moduleName_Chi = "单位视图汇缴信息模块";
        logger.info("[+]调用组装" + moduleName_Chi + "接口开始");
        //配置文件中模块属性
        String moduleName_pzwj = "dwst.dwsy.hjxx.menu";
        //按钮ID前缀
        String btnIdPre = userViewISH04Util.BTNPRE + "hjxx";
        //要返回的json  格式{'returnCode' : 0, 'message' : '', 'data' : {}}
        JSONObject funcInfosJson = new JSONObject();
        //json中的data数据
        JSONObject data = new JSONObject();
        //data中的ywsj数据[[],[]]
        JSONArray ywsj = new JSONArray();
        //ywsj数据的每一个数组，格式[]
        JSONArray ywsjPer;
        //ywsjPer 中的每一条数据
        JSONObject temp;
        //data中的menu数据 格式	{"id":"","label":"","icon":"","url":"","children":[]}
        JSONArray menu;
        try {
            //缓存中菜单信息
            JSONObject funcinfos;
            String poolkey = request.getParameter("poolkey");
            if (StringUtils.isEmpty(poolkey)) {
                throw new TransSingleException("poolkey不能为空");
            }
            MainContext mainContext = new MainContext(request);
            DataPool pool = PoolUtil.getPool(poolkey);
            mainContext.setDataPool(pool);
            PoolUtil.savePool(poolkey, pool);
            paramConfigImp.loadMaskData(new String[]{"ish.gg.other.sf", "ish.gg.func.systemtype"});
            YDMemcachedManager manager = YDMemcachedManager.newInstance();
            if (manager.get("funcInfos") != null) {
                funcinfos = JSONObject.parseObject(manager.get("funcInfos").toString());
            } else {
                funcinfos = initAllFuncToMemcached.initAllFuncToMemcached();
            }
            //----- 封装页面按钮区域（不需修改代码，可修改配置文件） start-----
            menu = userViewISH04Util.initMenuArray(funcinfos, moduleName_pzwj, btnIdPre, "");
            data.put("menu", menu);
            //----- 封装页面按钮区域（不需修改代码，可修改配置文件） end-----

            //----- 封装基础信息字段区域（根据实际情况修改）start -----
            //调用接口获取单位欠缴信息
            TransEngine.getInstance().execute("TranDWHJ04", mainContext);
            //单位欠缴json字符串
            String dwqjxxStr = pool.getString("json");

            //单位有欠缴信息时
            //字段名称
            String label;
            //字段值
            String value;
            //单位欠缴数组
            JSONArray dwqjxxArr = JSONArray.parseArray(dwqjxxStr);
            if (dwqjxxArr.size() > 0) {
                //临时json
                JSONObject dwqjTemp;
                for (Object obj : dwqjxxArr) {
                    ywsjPer = new JSONArray();
                    dwqjTemp = JSONObject.parseObject(obj.toString());
                    //预计汇缴年月信息
                    label = "预计汇缴年月";
                    temp = new JSONObject();
                    //开始年月
                    String ksny = "";
                    //缴至年月
                    String jzny = "";

                    if (dwqjTemp != null && dwqjTemp.containsKey("ksny")) {
                        ksny = dwqjTemp.getString("ksny");
                    }
                    if (dwqjTemp != null && dwqjTemp.containsKey("jzny")) {
                        jzny = dwqjTemp.getString("jzny");
                    }
                    //组装汇缴年月展示信息
                    value = ksny.equals(jzny) ? ksny : ksny + " 至 " + jzny;
                    temp.put("label", label);
                    temp.put("value", value);
                    ywsjPer.add(temp);
                    //预计汇缴金额信息
                    label = "预计汇缴金额（元）";
                    temp = new JSONObject();
                    if (dwqjTemp != null && dwqjTemp.containsKey("yjje")) {
                        value = MoneyUtil.addComma(dwqjTemp.getString("yjje"));
                        temp.put("label", label);
                        temp.put("value", value);
                        ywsjPer.add(temp);
                    }
                    //预计汇缴人数信息
                    label = "预计汇缴人数";
                    temp = new JSONObject();
                    if (dwqjTemp != null && dwqjTemp.containsKey("yjrs")) {
                        value = dwqjTemp.getString("yjrs");
                        temp.put("label", label);
                        temp.put("value", value);
                        ywsjPer.add(temp);
                    }
                    ywsj.add(ywsjPer);
                }
            } else {//没有欠缴信息时且本月已做汇缴，取最近一笔汇缴信息展示
                //调用接口获取单位最近汇缴信息
                TransEngine.getInstance().execute("TranDWHJ05", mainContext);
                ywsjPer = new JSONArray();
                temp = new JSONObject();
                //预计汇缴年月信息
                label = "最近汇缴年月";
                String ksny = pool.getString("ksny");
                String jzny = pool.getString("jzny");
                temp = new JSONObject();
                value = ksny + " 至 " + jzny;
                temp.put("label", label);
                temp.put("value", value);
                ywsjPer.add(temp);

                //汇缴金额信息
                label = "汇缴金额（元）";
                temp = new JSONObject();
                value = MoneyUtil.addComma(pool.getString("sjje"));
                temp.put("label", label);
                temp.put("value", value);
                ywsjPer.add(temp);

                //汇缴人数信息
                label = "汇缴人数";
                temp = new JSONObject();
                value = pool.getString("sjrs");
                temp.put("label", label);
                temp.put("value", value);
                ywsjPer.add(temp);
                ywsj.add(ywsjPer);
            }
            data.put("ywsj", ywsj);
            //----- 封装基础信息字段区域（根据实际情况修改）end -----
            funcInfosJson.put("returnCode", 0);
            funcInfosJson.put("message", "获取" + moduleName_Chi + "数据成功");
            funcInfosJson.put("data", data);

        } catch (Exception e) {
            funcInfosJson.put("returnCode", 1);
            funcInfosJson.put("message", "获取" + moduleName_Chi + "数据失败");
            funcInfosJson.put("data", new JSONObject());
            logger.error("获取" + moduleName_Chi + "数据失败:" + e);
            e.printStackTrace();
        }
        logger.info("[-]调用组装" + moduleName_Chi + "接口结束" + (System.currentTimeMillis() - start) + "毫秒");
        return funcInfosJson;
    }

    /**
     * 获取单位视图-我的账户-单位基本信息模块接口
     *
     * @param request 请求request
     * @return JSONObject
     */
    @RequestMapping(value = "/wdzh/hqdwjbxx",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject hqDwjbxx(HttpServletRequest request) {
        long start = System.currentTimeMillis();
        //要返回的json  格式{'returnCode' : 0, 'message' : '', 'data' : {}}
        JSONObject funcInfosJson = new JSONObject();
        //json中的data数据
        JSONObject data = new JSONObject();
        //data中的ywsj数据
        JSONObject ywsj = new JSONObject();
        //data中的menu数据 格式	{"id":"","label":"","icon":"","url":"","children":[]}
        JSONArray menu;

        //具体业务字段的button数据 格式	{"id":"","label":"","icon":"","url":"","children":[]}
        JSONArray button;
        //缓存中的菜单信息
        JSONObject funcinfos = new JSONObject();
        String moduleName_Chi = "单位视图单位基本信息模块";
        logger.info("[+]调用组装" + moduleName_Chi + "接口开始");
        try {
            String poolkey = request.getParameter("poolkey");
            if (StringUtils.isEmpty(poolkey)) {
                throw new TransSingleException("poolkey不能为空");
            }
            MainContext mainContext = new MainContext(request);
            DataPool pool = PoolUtil.getPool(poolkey);
            String instance = pool.get("_IS") == null ? "" : pool.getString("_IS");
            if (StringUtils.isEmpty(instance)) {
                instance = ydFlowEvent.getInstanceId();
                pool.put("_IS", instance);
            }
            mainContext.setDataPool(pool);
            PoolUtil.savePool(poolkey, pool);
            paramConfigImp.loadMaskData(new String[]{"ish.gg.func.systemtype", "ish.gg.other.sf", "bsp.cm.tradetype", "bsp.cm.supsubrelation", "bsp.cm.dwjjlx", "bsp.pb.certitype", "bsp.dp.dwzhzt"});
            TransEngine.getInstance().execute("TranHQDWXX01", mainContext);
            //资料完整度统计已完善的信息数量
            int count = 0;
            //资料完整度统计的信息总数
            int countTotal = 0;
            //ywsj中jcxx中的字段
            //字段id
            String id;
            //字段名
            String label;
            //字段值
            String value;
            //字段级别（1时收起和展开都展示，0时只有展开时展示）
            String level;
            JSONObject jsonTemp;
            //配置文件属性
            String moduleName_pzwj;
            //----- 封装基础信息字段区域（根据实际情况修改  level最多设置5个1）start -----
            //业务数据中中的jcxx数据
            JSONArray jcxx = new JSONArray();
            String dictVal;
            //单位账号信息
            jsonTemp = new JSONObject();
            id = "dwzh";
            label = "单位账号";
            value = IshExpression.getUserExtInfo("dwdjh");
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            level = "1";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //统一社会信用代码信息
            jsonTemp = new JSONObject();
            id = "tyshxydm";
            label = "统一社会信用代码";
            value = pool.getString("tyshxydm");
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //社保单位编号信息
            jsonTemp = new JSONObject();
            id = "dwsbzh";
            label = "社保单位编号";
            value = pool.getString("dwsbzh");
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //组织机构代码信息
            jsonTemp = new JSONObject();
            id = "zzjgdm";
            label = "组织机构代码";
            value = pool.getString("zzjgdm");
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //单位地址信息
            jsonTemp = new JSONObject();
            id = "dwdz";
            label = "单位地址";
            value = pool.getString("dwdz");
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //单位所属行业信息
            jsonTemp = new JSONObject();
            id = "dwsshy";
            label = "单位所属行业";
            dictVal = pool.getString("dwsshy");
            value = paramConfigImp.getVal("bsp.cm.tradetype." + dictVal);
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //单位隶属关系信息
            jsonTemp = new JSONObject();
            id = "dwlsgx";
            label = "单位隶属关系";
            dictVal = pool.getString("dwlsgx");
            value = paramConfigImp.getVal("bsp.cm.supsubrelation." + dictVal);
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);


            //单位经济类型信息
            jsonTemp = new JSONObject();
            id = "dwjjlx";
            label = "单位经济类型";
            dictVal = pool.getString("dwjjlx");
            value = paramConfigImp.getVal("bsp.cm.dwjjlx." + dictVal);
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //单位法人姓名信息
            jsonTemp = new JSONObject();
            id = "dwfrdbxm";
            label = "单位法人姓名";
            value = pool.getString("dwfrdbxm");
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //单位法人代表证件类型信息
            jsonTemp = new JSONObject();
            id = "dwfrdbzjlx";
            label = "单位法人代表证件类型";
            dictVal = pool.getString("dwfrdbzjlx");
            value = paramConfigImp.getVal("bsp.pb.certitype." + dictVal);
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //单位法人证件号码信息
            jsonTemp = new JSONObject();
            id = "dwfrdbzjhm";
            label = "单位法人证件号码";
            value = pool.getString("dwfrdbzjhm");
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);


            //单位法人电话号码信息
            jsonTemp = new JSONObject();
            id = "dwfrlxdh";
            label = "单位法人电话号码";
            value = pool.getString("dwfrlxdh");
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //单位邮编信息
            jsonTemp = new JSONObject();
            id = "dwyb";
            label = "单位邮编";
            value = pool.getString("dwyb");
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //单位电子信箱信息
            jsonTemp = new JSONObject();
            id = "dwdzxx";
            label = "单位电子信箱";
            value = pool.getString("dwdzxx");
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //单位设立日期信息
            jsonTemp = new JSONObject();
            id = "dwslrq";
            label = "单位设立日期";
            value = pool.getString("dwslrq");
            level = "1";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //单位发薪日信息
            jsonTemp = new JSONObject();
            id = "dwfxr";
            label = "单位发薪日";
            value = pool.getString("dwfxr") + "日";
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //是否托收备案信息
            jsonTemp = new JSONObject();
            id = "sftsba";
            label = "是否托收备案";
            dictVal = pool.getString("sftsba");
            value = paramConfigImp.getVal("ish.gg.other.sf." + dictVal);
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //所在区域信息
            jsonTemp = new JSONObject();
            id = "szqy";
            label = "所在区域";
            value = pool.getString("szqy");
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //单位缴存比例信息
            jsonTemp = new JSONObject();
            id = "dwjcbl";
            label = "单位缴存比例";
            value = pool.getString("dwjcbl") + "%";
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //个人缴存比例信息
            jsonTemp = new JSONObject();
            id = "grjcbl";
            label = "个人缴存比例";
            value = pool.getString("grjcbl") + "%";
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);


            //账户状态信息
            jsonTemp = new JSONObject();
            id = "dwzhzt";
            label = "账户状态";
            dictVal = pool.getString("dwzhzt");
            value = paramConfigImp.getVal("bsp.dp.dwzhzt." + dictVal);
            level = "1";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //暂存款余额信息
            jsonTemp = new JSONObject();
            id = "zckye";
            label = "暂存款余额";
            value = MoneyUtil.addComma(pool.getString("zckye")) + "元";
            level = "0";
             if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //银行名称信息
            jsonTemp = new JSONObject();
            id = "yhmc";
            label = "银行名称";
            value = pool.getString("yhmc");
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //网点名称信息
            jsonTemp = new JSONObject();
            id = "wdmc";
            label = "网点名称";
            value = pool.getString("wdmc");
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //缴至年月信息
            jsonTemp = new JSONObject();
            id = "jzny";
            label = "缴至年月";
            value = pool.getString("jzny");
            level = "1";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //缴交日信息
            jsonTemp = new JSONObject();
            id = "jjr";
            label = "缴交日";
            value = pool.getString("jjr") + "日";
            level = "1";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //缴存起始年月信息
            jsonTemp = new JSONObject();
            id = "jcqsny";
            label = "缴存起始年月";
            value = pool.getString("jcqsny");
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);
            ywsj.put("jcxx", jcxx);
            //----- 封装基础信息字段区域end -----

            //----- 统计资料完整度信息使用 不展示在页面上（根据实际情况修改） start -----
            String zlwzd = countTotal == 0 ? "100" : new DecimalFormat("0").format(DoubleUtil.div_down(count * 100, countTotal, 0));
            data.put("zlwzd", zlwzd);
            //----- 统计资料完整度信息使用 不展示在页面上 end -----


            //----- 封装更新资料按钮  start -----
            //配置文件属性
            moduleName_pzwj = "dwst.wdzh.dwjbxx.gxzl.menu";
            //按钮id前缀
            String btnIdPre = userViewISH04Util.BTNPRE + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".", moduleName_pzwj.lastIndexOf(".") - 1) + 1, moduleName_pzwj.lastIndexOf("."));
            menu = userViewISH04Util.initMenuArray(funcinfos, moduleName_pzwj, btnIdPre, icon);
            data.put("button", menu);
            //----- 封装更新资料按钮  end -----

            //----- 封装更多单位基本信息查询按钮  start -----
            //配置文件属性
            moduleName_pzwj = "dwst.wdzh.dwjbxx.gddwjbxx.menu";
            //按钮id前缀
            btnIdPre = userViewISH04Util.BTNPRE + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".", moduleName_pzwj.lastIndexOf(".") - 1) + 1, moduleName_pzwj.lastIndexOf("."));
            menu = userViewISH04Util.initMenuArray(funcinfos, moduleName_pzwj, btnIdPre, icon);
            ywsj.put("menu", menu);
            //----- 封装更多单位基本信息查询按钮  end -----

            //----- 封装经办人信息区域（根据实际情况修改 最多展示4个）start -----
            JSONArray jbrxx = new JSONArray();
            //经办人信息字符串
            String jbrxxStr = pool.getString("jbrxxjson");
            //经办人信息数组
            JSONArray jbrxxArr = JSONArray.parseArray(jbrxxStr);
            if (jbrxxArr.size() > 0) {
                //单条经办人信息JSON
                JSONObject jbrxxPer;
                for (int i = 0; i < jbrxxArr.size(); i++) {
                    jbrxxPer = jbrxxArr.getJSONObject(i);
                    //最多设置4个
                    if (i > 3) {
                        break;
                    }
                    //经办人信息信息
                    label = "经办人";
                    id = "jbrxx" + i;
                    value = "";
                    jsonTemp = new JSONObject();
                    if (jbrxxPer != null && jbrxxPer.containsKey("jbrxm")) {
                        label = label + jbrxxPer.getString("jbrxm");
                    }
                    if (jbrxxPer != null && jbrxxPer.containsKey("jbrsjhm")) {
                        value = jbrxxPer.getString("jbrsjhm");
                    }
                    //组装汇缴年月展示信息
                    jsonTemp.put("label", label);
                    jsonTemp.put("value", value);
                    jsonTemp.put("id", id);
                    jsonTemp.put("button", new JSONArray());
                    jbrxx.add(jsonTemp);
                }
                ywsj.put("jbrxx", jbrxx);
            }
            data.put("ywsj", ywsj);
            //----- 封装经办人信息区域（根据实际情况修改 最多展示4个）end -----
            funcInfosJson.put("data", data);
            funcInfosJson.put("returnCode", 0);
            funcInfosJson.put("message", "获取" + moduleName_Chi + "数据成功");
        } catch (Exception e) {
            funcInfosJson.put("returnCode", 1);
            funcInfosJson.put("message", "获取" + moduleName_Chi + "数据失败");
            funcInfosJson.put("data", new JSONObject());
            logger.error("获取" + moduleName_Chi + "数据失败:" + e);
            e.printStackTrace();
        }
        logger.info("[-]调用组装" + moduleName_Chi + "接口结束" + (System.currentTimeMillis() - start) + "毫秒");
        return funcInfosJson;
    }

    /**
     * 获取单位视图-我的账户-最近汇/补缴信息信息模块接口
     *
     * @param request 请求request
     * @return JSONObject
     */
    @RequestMapping(value = "/wdzh/hqzjhbjxx",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject hqZjhbjxx(HttpServletRequest request) {
        long start = System.currentTimeMillis();
        //要返回的json  格式{'returnCode' : 0, 'message' : '', 'data' : {}}
        JSONObject funcInfosJson = new JSONObject();
        //json中的data数据
        JSONObject data = new JSONObject();
        //data中的ywsj数据
        JSONObject ywsj = new JSONObject();
        //data中的menu数据 格式	{"id":"","label":"","icon":"","url":"","children":[]}
        JSONArray menu;

        //具体业务字段的button数据 格式	{"id":"","label":"","icon":"","url":"","children":[]}
        JSONArray button;
        //缓存中的菜单信息
        JSONObject funcinfos = new JSONObject();
        String moduleName_Chi = "单位视图最近汇补缴信息模块";
        logger.info("[+]调用组装" + moduleName_Chi + "接口开始");
        try {
            String poolkey = request.getParameter("poolkey");
            if (StringUtils.isEmpty(poolkey)) {
                throw new TransSingleException(CommonErrorCode.ERROR_NOT_NULL,"poolkey不能为空");
            }
            MainContext mainContext = new MainContext(request);
            DataPool pool = PoolUtil.getPool(poolkey);
            mainContext.setDataPool(pool);
            PoolUtil.savePool(poolkey, pool);
            paramConfigImp.loadMaskData(new String[]{"ish.gg.func.systemtype", "ish.gg.other.sf", "bsp.dp.dptype", "bsp.dp.paymode", "bsp.dp.jcywzt"});
            //ywsj中zjhjxx和zjbjxx中的字段
            //字段id
            String id;
            //字段名
            String label;
            //字段值
            String value;
            //字段级别（1时收起和展开都展示，0时只有展开时展示）
            String level;
            //字典值
            String dictVal;
            JSONObject jsonTemp;
            //配置文件属性
            String moduleName_pzwj;
            //----- 封装基础信息字段最近汇缴信息区域（根据实际情况修改  level最多设置6个1）start -----
            TransEngine.getInstance().execute("TranDWHJ05", mainContext);
            //业务数据中的zjhjxx数据
            JSONArray zjhjxx = new JSONArray();

            //登记号信息
            jsonTemp = new JSONObject();
            id = "zjhj_djh";
            label = "登记号";
            value = pool.getString("djh");
            level = "1";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjhjxx.add(jsonTemp);

            //开始年月信息
            jsonTemp = new JSONObject();
            id = "zjhj_ksny";
            label = "开始年月";
            value = pool.getString("ksny");
            level = "1";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjhjxx.add(jsonTemp);

            //缴至年月信息
            jsonTemp = new JSONObject();
            id = "zjhj_jzny";
            label = "缴至年月";
            value = pool.getString("jzny");
            level = "1";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjhjxx.add(jsonTemp);

            //实缴人数信息
            jsonTemp = new JSONObject();
            id = "zjhj_sjrs";
            label = "实缴人数";
            value = pool.getString("sjrs");
            level = "1";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjhjxx.add(jsonTemp);

            //应缴人数信息
            jsonTemp = new JSONObject();
            id = "zjhj_yjrs";
            label = "应缴人数";
            value = pool.getString("yjrs");
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjhjxx.add(jsonTemp);

            //实缴金额信息
            jsonTemp = new JSONObject();
            id = "zjhj_sjje";
            label = "实缴金额";
            value = MoneyUtil.addComma(pool.getString("sjje")) + "元";
            level = "1";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjhjxx.add(jsonTemp);

            //应缴金额信息
            jsonTemp = new JSONObject();
            id = "zjhj_yjje";
            label = "应缴金额";
            value = MoneyUtil.addComma(pool.getString("yjje")) + "元";
            level = "0";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjhjxx.add(jsonTemp);

            //缴存类型信息
            jsonTemp = new JSONObject();
            id = "zjhj_jclx";
            label = "缴存类型";
            dictVal = pool.getString("jclx");
            value = paramConfigImp.getVal("bsp.dp.dptype." + dictVal);
            level = "1";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjhjxx.add(jsonTemp);

            //缴款方式信息
            jsonTemp = new JSONObject();
            id = "zjhj_jkfs";
            label = "缴款方式";
            dictVal = pool.getString("jkfs");
            value = paramConfigImp.getVal("bsp.dp.paymode." + dictVal);
            level = "0";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjhjxx.add(jsonTemp);

            //暂存款转入金额信息
            jsonTemp = new JSONObject();
            id = "zjhj_zchzrje";
            label = "暂存款转入金额";
            value = MoneyUtil.addComma(pool.getString("zchzrje")) + "元";
            level = "0";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjhjxx.add(jsonTemp);

            //暂存款转出金额信息
            jsonTemp = new JSONObject();
            id = "zjhj_zchzcje";
            label = "暂存款转出金额";
            value = MoneyUtil.addComma(pool.getString("zchzcje")) + "元";
            level = "0";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjhjxx.add(jsonTemp);

            //受理日期信息
            jsonTemp = new JSONObject();
            id = "zjhj_slrq";
            label = "受理日期";
            value = pool.getString("slrq");
            level = "0";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjhjxx.add(jsonTemp);

            //入账日期信息
            jsonTemp = new JSONObject();
            id = "zjhj_rzrq";
            label = "入账日期";
            value = pool.getString("rzrq");
            level = "0";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjhjxx.add(jsonTemp);

            //业务状态信息
            jsonTemp = new JSONObject();
            id = "zjhj_ywzt";
            label = "业务状态";
            dictVal = pool.getString("ywzt");
            value = paramConfigImp.getVal("bsp.dp.jcywzt." + dictVal);
            level = "0";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjhjxx.add(jsonTemp);
            ywsj.put("zjhjxx", zjhjxx);
            //----- 封装基础信息字段最近汇缴信息区域区域end -----

            //----- 封装基础信息字段最近补缴信息区域（根据实际情况修改  level最多设置6个1）start -----
            TransEngine.getInstance().execute("TranDWBJ05", mainContext);
            //业务数据中的zjhjxx数据
            JSONArray zjbjxx = new JSONArray();
            //登记号信息
            jsonTemp = new JSONObject();
            id = "zjbj_djh";
            label = "登记号";
            value = pool.getString("djh");
            level = "1";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjbjxx.add(jsonTemp);

            //开始年月信息
            jsonTemp = new JSONObject();
            id = "zjbj_ksny";
            label = "开始年月";
            value = pool.getString("ksny");
            level = "1";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjbjxx.add(jsonTemp);

            //缴至年月信息
            jsonTemp = new JSONObject();
            id = "zjbj_jzny";
            label = "缴至年月";
            value = pool.getString("jzny");
            level = "1";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjbjxx.add(jsonTemp);

            //实缴人数信息
            jsonTemp = new JSONObject();
            id = "zjbj_sjrs";
            label = "实缴人数";
            value = pool.getString("sjrs");
            level = "1";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjbjxx.add(jsonTemp);

            //应缴人数信息
            jsonTemp = new JSONObject();
            id = "zjbj_yjrs";
            label = "应缴人数";
            value = pool.getString("yjrs");

            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjbjxx.add(jsonTemp);

            //实缴金额信息
            jsonTemp = new JSONObject();
            id = "zjbj_sjje";
            label = "实缴金额";
            value = MoneyUtil.addComma(pool.getString("sjje")) + "元";
            level = "1";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjbjxx.add(jsonTemp);

            //应缴金额信息
            jsonTemp = new JSONObject();
            id = "zjbj_yjje";
            label = "应缴金额";
            value = MoneyUtil.addComma(pool.getString("yjje")) + "元";
            level = "0";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjbjxx.add(jsonTemp);


            //缴存类型信息
            jsonTemp = new JSONObject();
            id = "zjbj_jclx";
            label = "缴存类型";
            dictVal = pool.getString("jclx");
            value = paramConfigImp.getVal("bsp.dp.dptype." + dictVal);
            level = "1";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjbjxx.add(jsonTemp);

            //缴款方式信息
            jsonTemp = new JSONObject();
            id = "zjbj_jkfs";
            label = "缴款方式";
            dictVal = pool.getString("jkfs");
            value = paramConfigImp.getVal("bsp.dp.paymode." + dictVal);
            level = "0";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjbjxx.add(jsonTemp);

            //暂存款转入金额信息
            jsonTemp = new JSONObject();
            id = "zjbj_zchzrje";
            label = "暂存款转入金额";
            value = MoneyUtil.addComma(pool.getString("zchzrje")) + "元";
            level = "0";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjbjxx.add(jsonTemp);

            //暂存款转出金额信息
            jsonTemp = new JSONObject();
            id = "zjbj_zchzcje";
            label = "暂存款转出金额";
            value = MoneyUtil.addComma(pool.getString("zchzcje")) + "元";
            level = "0";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjbjxx.add(jsonTemp);

            //受理日期信息
            jsonTemp = new JSONObject();
            id = "zjbj_slrq";
            label = "受理日期";
            value = pool.getString("slrq");
            level = "0";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjbjxx.add(jsonTemp);

            //入账日期信息
            jsonTemp = new JSONObject();
            id = "zjbj_rzrq";
            label = "入账日期";
            value = pool.getString("rzrq");
            level = "0";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjbjxx.add(jsonTemp);

            //业务状态信息
            jsonTemp = new JSONObject();
            id = "zjbj_ywzt";
            label = "业务状态";
            dictVal = pool.getString("ywzt");
            value = paramConfigImp.getVal("bsp.dp.jcywzt." + dictVal);
            level = "0";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            zjbjxx.add(jsonTemp);
            ywsj.put("zjbjxx", zjbjxx);
            data.put("ywsj", ywsj);
            //----- 封装基础信息字段最近补缴信息区域 end -----

            //----- 封装底侧菜单区域  start -----
            //配置文件属性
            moduleName_pzwj = "dwst.wdzh.zjhbjxx.menu";
            //按钮id前缀
            String btnIdPre = userViewISH04Util.BTNPRE + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".", moduleName_pzwj.lastIndexOf(".") - 1) + 1, moduleName_pzwj.lastIndexOf("."));
            menu = userViewISH04Util.initMenuArray(funcinfos, moduleName_pzwj, btnIdPre, "");
            data.put("menu", menu);
            //----- 封装底侧菜单区域  end -----

            funcInfosJson.put("data", data);
            funcInfosJson.put("returnCode", 0);
            funcInfosJson.put("message", "获取" + moduleName_Chi + "数据成功");
        } catch (Exception e) {
            funcInfosJson.put("returnCode", 1);
            funcInfosJson.put("message", "获取" + moduleName_Chi + "数据失败");
            funcInfosJson.put("data", new JSONObject());
            logger.error("获取" + moduleName_Chi + "数据失败:" + e);
            e.printStackTrace();
        }
        logger.info("[-]调用组装" + moduleName_Chi + "接口结束" + (System.currentTimeMillis() - start) + "毫秒");
        return funcInfosJson;
    }

    /**
     * 获取单位视图-我的账户-单位下个人信息模块接口
     *
     * @param request 请求request
     * @return JSONObject
     */
    @RequestMapping(value = "/wdzh/hqdwxgrxx",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject hqDwxgrxx(HttpServletRequest request) {
        long start = System.currentTimeMillis();
        //要返回的json  格式{'returnCode' : 0, 'message' : '', 'data' : {}}
        JSONObject funcInfosJson = new JSONObject();
        //json中的data数据
        JSONObject data = new JSONObject();
        //data中的ywsj数据
        JSONArray ywsj = new JSONArray();
        //data中的menu数据 格式	{"id":"","label":"","icon":"","url":"","children":[]}
        JSONArray menu;

        //具体业务字段的button数据 格式	{"id":"","label":"","icon":"","url":"","children":[]}
        JSONArray button;
        //缓存中的菜单信息
        JSONObject funcinfos = new JSONObject();
        String moduleName_Chi = "单位视图单位下个人信息模块";
        logger.info("[+]调用组装" + moduleName_Chi + "接口开始");
        try {
            String poolkey = request.getParameter("poolkey");
            if (StringUtils.isEmpty(poolkey)) {
                throw new TransSingleException(CommonErrorCode.ERROR_NOT_NULL,"poolkey不能为空");
            }
            MainContext mainContext = new MainContext(request);
            DataPool pool = PoolUtil.getPool(poolkey);
            mainContext.setDataPool(pool);
            PoolUtil.savePool(poolkey, pool);
            paramConfigImp.loadMaskData(new String[]{"ish.gg.func.systemtype", "ish.gg.other.sf"});
            //ywsj中的字段
            //字段id
            String id;
            //字段名
            String label;
            //字段值
            String value;
            JSONObject jsonTemp;
            //配置文件属性
            String moduleName_pzwj;
            //----- 封装基础信息字段区域（根据实际情况修改）start -----
            TransEngine.getInstance().execute("TranBYRYXX01", mainContext);
            //总人数信息
            jsonTemp = new JSONObject();
            id = "zrs";
            label = "总人数";
            value = pool.getString("zrs");
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("button", new JSONArray());
            ywsj.add(jsonTemp);

            //转入人数信息
            jsonTemp = new JSONObject();
            id = "zrrs";
            label = "转入人数";
            value = pool.getString("zrrs");
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("button", new JSONArray());
            ywsj.add(jsonTemp);

            //转出人数信息
            jsonTemp = new JSONObject();
            id = "zcrs";
            label = "转出人数";
            value = pool.getString("zcrs");
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("button", new JSONArray());
            ywsj.add(jsonTemp);

            //启封人数信息
            jsonTemp = new JSONObject();
            id = "qfrs";
            label = "启封人数";
            value = pool.getString("qfrs");
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("button", new JSONArray());
            ywsj.add(jsonTemp);

            //封存人数信息
            jsonTemp = new JSONObject();
            id = "fcrs";
            label = "封存人数";
            value = pool.getString("fcrs");
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("button", new JSONArray());
            ywsj.add(jsonTemp);

            //开户人数信息
            jsonTemp = new JSONObject();
            id = "khrs";
            label = "开户人数";
            value = pool.getString("khrs");
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("button", new JSONArray());
            ywsj.add(jsonTemp);
            data.put("ywsj", ywsj);
            //----- 封装基础信息字段区域区域end -----


            //----- 封装底侧菜单区域  start -----
            //配置文件属性
            moduleName_pzwj = "dwst.wdzh.dwxgrxx.menu";
            //按钮id前缀
            String btnIdPre = userViewISH04Util.BTNPRE + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".", moduleName_pzwj.lastIndexOf(".") - 1) + 1, moduleName_pzwj.lastIndexOf("."));
            menu = userViewISH04Util.initMenuArray(funcinfos, moduleName_pzwj, btnIdPre, "");
            data.put("menu", menu);
            //----- 封装底侧菜单区域  end -----

            funcInfosJson.put("data", data);
            funcInfosJson.put("returnCode", 0);
            funcInfosJson.put("message", "获取" + moduleName_Chi + "数据成功");
        } catch (Exception e) {
            funcInfosJson.put("returnCode", 1);
            funcInfosJson.put("message", "获取" + moduleName_Chi + "数据失败");
            funcInfosJson.put("data", new JSONObject());
            logger.error("获取" + moduleName_Chi + "数据失败:" + e);
            e.printStackTrace();
        }
        logger.info("[-]调用组装" + moduleName_Chi + "接口结束" + (System.currentTimeMillis() - start) + "毫秒");
        return funcInfosJson;
    }

    /**
     * 获取单位视图-我的账户-单位明细账信息模块接口
     *
     * @param request 请求request
     * @return JSONObject
     */
    @RequestMapping(value = "/wdzh/hqdwmxz",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject hqDwmxz(HttpServletRequest request) {
        long start = System.currentTimeMillis();
        //要返回的json  格式{'returnCode' : 0, 'message' : '', 'data' : {}}
        JSONObject funcInfosJson = new JSONObject();
        //json中的data数据
        JSONObject data = new JSONObject();
        //data中的ywsj数据
        JSONObject ywsj = new JSONObject();
        //data中的menu数据 格式	{"id":"","label":"","icon":"","url":"","children":[]}
        JSONArray menu;

        //具体业务字段的button数据 格式	{"id":"","label":"","icon":"","url":"","children":[]}
        JSONArray button;
        //缓存中的菜单信息
        JSONObject funcinfos = new JSONObject();
        String moduleName_Chi = "单位视图单位明细账模块";
        logger.info("[+]调用组装" + moduleName_Chi + "接口开始");
        try {
            String poolkey = request.getParameter("poolkey");
            if (StringUtils.isEmpty(poolkey)) {
                throw new TransSingleException(CommonErrorCode.ERROR_NOT_NULL,"poolkey不能为空");
            }
            MainContext mainContext = new MainContext(request);
            DataPool pool = PoolUtil.getPool(poolkey);
            mainContext.setDataPool(pool);
            PoolUtil.savePool(poolkey, pool);
            paramConfigImp.loadMaskData(new String[]{"ish.gg.func.systemtype", "ish.gg.other.sf", "bsp.pb.ywmxlx"});
            //配置文件属性
            String moduleName_pzwj;
            //----- 封装基础信息字段区域（根据实际情况修改 最多展示3条）start -----
            TransEngine.getInstance().execute("TranDWMXXX01", mainContext);
            //列表表头
            String[] head = {"交易日期", "业务类型", "发生人数", "发生额", "余额"};
            ywsj.put("head", head);
            //列表表体
            JSONArray body = new JSONArray();
            //每一个标体数据数组
            JSONArray dwmxxxPerArr;

            //----- 封装单位明细账信息区域（根据实际情况修改 ）start -----
            //经办人信息字符串
            String dwmxxxStr = pool.getString("json");
            //经办人信息数组
            JSONArray dwmxxxArr = JSONArray.parseArray(dwmxxxStr);
            if (dwmxxxArr.size() > 0) {
                //单条单位明细账信息JSON
                JSONObject dwmxxxPerJson;
                //交易日期
                String jyrq;
                //业务类型
                String ywlx;
                //发生人数
                String fsrs;
                //发生额
                String fse;
                //余额
                String ye;
                for (int i = 0; i < dwmxxxArr.size(); i++) {
                    //最多设置3个
                    if (i > 2) {
                        break;
                    }
                    dwmxxxPerArr = new JSONArray();
                    dwmxxxPerJson = dwmxxxArr.getJSONObject(i);
                    jyrq = dwmxxxPerJson.getString("jyrq");
                    String dictValue = dwmxxxPerJson.getString("ywlx");
                    ywlx = paramConfigImp.getVal("bsp.pb.ywmxlx." + dictValue);
                    fsrs = dwmxxxPerJson.getString("fsrs");
                    fse = MoneyUtil.addComma(dwmxxxPerJson.getString("fse"));
                    ye = MoneyUtil.addComma(dwmxxxPerJson.getString("ye"));
                    dwmxxxPerArr.add(jyrq);
                    dwmxxxPerArr.add(ywlx);
                    dwmxxxPerArr.add(fsrs);
                    dwmxxxPerArr.add(fse);
                    dwmxxxPerArr.add(ye);
                    body.add(dwmxxxPerArr);
                }
                ywsj.put("body", body);
            }
            data.put("ywsj", ywsj);
            //----- 封装单位明细账信息区域（根据实际情况修改 ） end -----


            //----- 封装底侧菜单区域  start -----
            //配置文件属性
            moduleName_pzwj = "dwst.wdzh.dwmxz.menu";
            //按钮id前缀
            String btnIdPre = userViewISH04Util.BTNPRE + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".", moduleName_pzwj.lastIndexOf(".") - 1) + 1, moduleName_pzwj.lastIndexOf("."));
            menu = userViewISH04Util.initMenuArray(funcinfos, moduleName_pzwj, btnIdPre, "");
            data.put("menu", menu);
            //----- 封装底侧菜单区域  end -----

            funcInfosJson.put("data", data);
            funcInfosJson.put("returnCode", 0);
            funcInfosJson.put("message", "获取" + moduleName_Chi + "数据成功");
        } catch (Exception e) {
            funcInfosJson.put("returnCode", 1);
            funcInfosJson.put("message", "获取" + moduleName_Chi + "数据失败");
            funcInfosJson.put("data", new JSONObject());
            logger.error("获取" + moduleName_Chi + "数据失败:" + e);
            e.printStackTrace();
        }
        logger.info("[-]调用组装" + moduleName_Chi + "接口结束" + (System.currentTimeMillis() - start) + "毫秒");
        return funcInfosJson;
    }
}
