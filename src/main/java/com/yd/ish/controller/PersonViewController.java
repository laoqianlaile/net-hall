package com.yd.ish.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yd.basic.expression.IshExpression;
import com.yd.biz.engine.TransEngine;
import com.yd.biz.exception.TransSingleException;
import com.yd.deplatform.util.PropertyUtil;
import com.yd.ish.common.dao.mybatis.CPLFlowViewMapper;
import com.yd.ish.common.flowevent.YDFlowEvent;
import com.yd.ish.common.model.mybatis.CPLFlowView;
import com.yd.ish.common.util.StringUtils;
import com.yd.ish.dataanalysis.service.mybatis.FuncLogService;
import com.yd.ish.dataanalysis.service.mybatis.FuncRecommendService;
import com.yd.ish.dataanalysis.util.DoubleUtil;
import com.yd.ish.util.MoneyUtil;
import com.yd.ish.util.UserViewISH04Util;
import com.yd.org.service.InitMemcached.InitAllFuncToMemcached;
import com.yd.org.service.mybatis.CPLFuncService;
import com.yd.org.util.CommonErrorCode;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.cache.YDMemcachedManager;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.external.EssFactory;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.jdbc.BaseBean;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 名称：PersonViewController.java
 * <p>功能："个人视图"组装接口(包括个人首页和我的账户视图) <br>
 *
 * @author 许永峰
 * v0.1	2019年11月05日 许永峰创建
 */
@Controller
@RequestMapping("/template/ish04/grst")
public class PersonViewController {
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
    @Autowired
    YDFlowEvent ydFlowEvent;
    @Autowired
    CPLFlowViewMapper cplFlowViewMapper;

    private static final Logger logger = LoggerFactory.getLogger(PersonViewController.class);
    private static final String icon = PropertyUtil.getString("yhst.properties", "yhst.gg.ywsj.icon");
    @Autowired
    UserViewISH04Util userViewISH04Util;


    /**
     * 获取个人视图-个人首页-您可能要办理模块接口
     * 不需修改
     *
     * @param request 请求
     * @return JSONObject
     */
    @RequestMapping(value = "/grsy/hqnknybl",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject hqNknybl(HttpServletRequest request) {
        long start = System.currentTimeMillis();
        String moduleName_Chi = "个人视图您可能要办理模块";
        logger.info("[+]调用组装" + moduleName_Chi + "接口开始");
        //要返回的json  格式{'returnCode' : 0, 'message' : '', 'data' : {}}
        JSONObject nknyblJson = new JSONObject();
        try {
            String systemTypeMask = "WTGR";
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

    /**
     * 获取个人视图-个人首页-凭证打印模块接口
     * 不需要修改，只需修改yhst.properties配置文件中grst.grsy.pzdy.menu值
     *
     * @param request 请求request
     * @return JSONObject
     */
    @RequestMapping(value = "/grsy/hqpzdy",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject hqPzdy(HttpServletRequest request) {
        long start = System.currentTimeMillis();
        // 模块名字 打印日志时用
        String moduleName_Chi = "个人视图凭证打印模块";
        //配置文件中模块属性
        String moduleName_pzwj = "grst.grsy.pzdy.menu";
        //按钮ID前缀
        String btnIdPre = userViewISH04Util.BTNPRE + "pzdy";
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
     * 获取个人视图-个人首页-公共业务模块接口
     * 不需要修改，只需修改yhst.properties配置文件中grst.grsy.ggyw.menu值
     *
     * @param request 请求request
     * @return JSONObject
     */
    @RequestMapping(value = "/grsy/hqggyw",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject hqGgyw(HttpServletRequest request) {
        long start = System.currentTimeMillis();
        // 模块名字 打印日志时用
        String moduleName_Chi = "个人视图公共业务模块";
        //配置文件中模块属性
        String moduleName_pzwj = "grst.grsy.ggyw.menu";
        //按钮ID前缀
        String btnIdPre = userViewISH04Util.BTNPRE + "ggyw";
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
            logger.error("获取" + moduleName_Chi + "数据失败" + e);
            e.printStackTrace();
        }
        logger.info("[-]调用组装" + moduleName_Chi + "接口结束" + (System.currentTimeMillis() - start) + "毫秒");
        return funcInfosJson;

    }

    /**
     * 获取个人视图-个人首页-我的贷款模块接口
     * 不需要修改，只需修改yhst.properties配置文件中grst.grsy.wddk.menu值
     *
     * @param request 请求request
     * @return JSONObject
     */
    @RequestMapping(value = "/grsy/hqwddk",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject hqWddk(HttpServletRequest request) {
        long start = System.currentTimeMillis();
        // 模块名字 打印日志时用
        String moduleName_Chi = "个人视图我的贷款模块";
        //配置文件中模块属性
        String moduleName_pzwj = "grst.grsy.wddk.menu";
        //按钮ID前缀
        String btnIdPre = userViewISH04Util.BTNPRE + "wddk";
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
     * 获取个人视图-个人首页-个人账户基本信息模块接口
     *
     * @param request 请求request
     * @return JSONObject
     */
    @RequestMapping(value = "/grsy/hqgrzhjbxx",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject hqGrzhjbxx1(HttpServletRequest request) {
        long start = System.currentTimeMillis();
        //要返回的json  格式{'returnCode' : 0, 'message' : '', 'data' : {}}
        JSONObject funcInfosJson = new JSONObject();
        //json中的data数据
        JSONObject data = new JSONObject();
        //data中的ywsj数据
        JSONObject ywsj = new JSONObject();
        //data中的menu数据 格式	{"id":"","label":"","icon":"","url":"","children":[]}
        JSONArray menu;
        JSONObject funcinfos;
        //模块名字，打印日志时使用
        String moduleName_Chi = "个人视图个人首页个人账户基本信息模块";
        logger.info("[+]调用组装" + moduleName_Chi + "接口开始");
        try {
            String poolkey = request.getParameter("poolkey");
            if (StringUtils.isEmpty(poolkey)) {
                throw new TransSingleException(CommonErrorCode.ERROR_NOT_NULL, "poolkey不能为空");
            }
            MainContext mainContext = new MainContext(request);
            DataPool pool = PoolUtil.getPool(poolkey);
            mainContext.setDataPool(pool);
            PoolUtil.savePool(poolkey, pool);
            //----- 封装模块展示的业务数据信息 根据实际情况设置修改展示的字段名和值  start-----
            //调用接口获取账户基本信息
            TransEngine.getInstance().execute("TranZHJBXX01", mainContext);
            //个人账户余额信息
            JSONObject zhye = new JSONObject();
            zhye.put("label", "账户余额");
            zhye.put("value", MoneyUtil.addComma(pool.getString("grzhye")));
            ywsj.put("zhye", zhye);
            //个人账户状态信息
            JSONObject zhzt = new JSONObject();
            zhzt.put("label", "账户状态");
            String zhtz = pool.getString("grzhzt");
            zhzt.put("value", zhtz);
            paramConfigImp.loadMaskData(new String[]{"bsp.dp.grzhzt", "ish.gg.func.status", "ish.gg.other.sf", "ish.gg.func.systemtype"});
            String text = paramConfigImp.getVal("bsp.dp.grzhzt." + zhtz);
            //text 长度只能为2，如果超长在此处重新赋值或者处理(目前字典项中只有销户状态超长，统一使用最后两个字销户)
            text = text.length() > 2 ? text.substring(text.length() - 2) : text;
            zhzt.put("text", text);
            ywsj.put("zhzt", zhzt);
            //缴至年月信息
            JSONObject jzny = new JSONObject();
            jzny.put("label", "缴至年月");
            jzny.put("value", pool.getString("jzny"));
            ywsj.put("jzny", jzny);
            data.put("ywsj", ywsj);
            //-----  封装模块展示的业务数据信息 根据实际情况设置修改展示的字段名和值  end -----

            //-----  封装menu数据我要提取和账户明细（不需修改代码，可修改配置文件） start -----
            YDMemcachedManager manager = YDMemcachedManager.newInstance();
            if (manager.get("funcInfos") != null) {
                funcinfos = JSONObject.parseObject(manager.get("funcInfos").toString());
            } else {
                funcinfos = initAllFuncToMemcached.initAllFuncToMemcached();
            }
            //配置文件中模块属性
            String moduleName_pzwj = "grst.grsy.grzhjbxx.menu";
            //按钮id前缀
            String btnIdPre = userViewISH04Util.BTNPRE + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".", moduleName_pzwj.lastIndexOf(".") - 1) + 1, moduleName_pzwj.lastIndexOf("."));
            menu = userViewISH04Util.initMenuArray(funcinfos, moduleName_pzwj, btnIdPre, "");
            data.put("menu", menu);
            //-----  封装menu数据我要提取和账户明细（不需修改代码，可修改配置文件） end -----

            //-----  封装安全设置功能信息（不需修改代码，可修改配置文件） start -----
            //配置文件中安全设置菜单属性
            moduleName_pzwj = "grst.grsy.grzhjbxx.aqsz.menu";
            btnIdPre = userViewISH04Util.BTNPRE + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".", moduleName_pzwj.lastIndexOf(".") - 1) + 1, moduleName_pzwj.lastIndexOf("."));
            JSONArray aqszArray = userViewISH04Util.initMenuArray(funcinfos, moduleName_pzwj, btnIdPre, "");
            data.put("aqsz", aqszArray);
            //-----  封装安全设置功能信息（不需修改代码，可修改配置文件） end -----
            funcInfosJson.put("returnCode", 0);
            funcInfosJson.put("message", "获取个人账户基本信息成功");
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
     * 获取个人视图-个人首页-我的申报模块接口
     *
     * @param request 请求request
     * @return JSONObject
     */
    @RequestMapping(value = "/grsy/hqwdsb",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject hqWdsb(HttpServletRequest request) {
        long start = System.currentTimeMillis();
        //要返回的json  格式{'returnCode' : 0, 'message' : '', 'data' : {}}
        JSONObject funcInfosJson = new JSONObject();
        //模块名字 打日志使用
        String moduleName_Chi = "个人视图我的申报模块";
        String moduleName_pzwj = "grst.grsy.wdsb.sbxq.menu";
        logger.info("[+]调用组装" + moduleName_Chi + "接口开始");
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
     * 获取个人视图-个人首页-我的预约模块接口
     * 使用网厅预约功能的不需修改（只需修改配置文件中的grst.grsy.wdyy.yysq.menu和grst.grsy.wdyy.yyxq.menu）
     * 调用综服接口的自行修改调用综服接口
     *
     * @param request 请求request
     * @return JSONObject
     */
    @RequestMapping(value = "/grsy/hqwdyy",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject hqWdyy(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String ip2 = request.getLocalAddr();
        long start = System.currentTimeMillis();
        //要返回的json  格式{'returnCode' : 0, 'message' : '', 'data' : {}}
        JSONObject funcInfosJson = new JSONObject();
        //json中的data数据
        JSONObject data = new JSONObject();
        //data中的ywsj数据
        JSONObject ywsj = new JSONObject();
        //data中的menu数据 格式	{"id":"","label":"","icon":"","url":"","children":[]}
        JSONArray menu;
        JSONObject funcinfos;
        String moduleName_Chi = "个人视图我的预约模块";
        logger.info("[+]调用组装" + moduleName_Chi + "接口开始");
        try {
            String poolkey = request.getParameter("poolkey");
            if (StringUtils.isEmpty(poolkey)) {
                throw new TransSingleException(CommonErrorCode.ERROR_NOT_NULL, "poolkey不能为空");
            }
            MainContext mainContext = new MainContext(request);
            DataPool pool = PoolUtil.getPool(poolkey);
            pool.put("khbs", IshExpression.getRealUserExtInfo("khbs"));
            pool.put("khdjh", IshExpression.getRealUserExtInfo("yhdjh"));
            pool.put("order", "w.yyrq ,c.qssj desc");
            pool.put("isPage", "0");
            mainContext.setDataPool(pool);
            paramConfigImp.loadMaskData(new String[]{"ish.gg.yy.yysqzt", "ish.gg.wf.sbzt", "ish.gg.func.systemtype", "ish.gg.other.sf"});
            String yyczzt_dsl_val = paramConfigImp.getValByMask("ish.gg.yy.yysqzt", "DSL");
            pool.put("yyzt", yyczzt_dsl_val);
            PoolUtil.savePool(poolkey, pool);
            //-----  封装页面展示字段区域 start -----
            YDMemcachedManager manager = YDMemcachedManager.newInstance();
            if (manager.get("funcInfos") != null) {
                funcinfos = JSONObject.parseObject(manager.get("funcInfos").toString());
            } else {
                funcinfos = initAllFuncToMemcached.initAllFuncToMemcached();
            }
            Map<String, Object> map = TransEngine.getInstance().execute("TranYYSQJK05", mainContext);
            BaseBean[] beans = (BaseBean[]) map.get("details");
            BaseBean bean = beans.length >= 1 ? beans[0] : null;
            //业务数据
            String text;
            //预约号
            String yyh;
            //配置文件中我的预约模块菜单配置属性
            String moduleName_pzwj;

            if (bean == null) {
                text = "";
                yyh = "";
                moduleName_pzwj = "grst.grsy.wdyy.yysq.menu";
                //----- 封装页面按钮区域（不需修改代码，可修改配置文件） start-----
                menu = userViewISH04Util.initMenuArray(funcinfos, moduleName_pzwj, userViewISH04Util.BTNPRE + "yysq", "");
                //----- 封装页面按钮区域（不需修改代码，可修改配置文件） end-----
            } else {
                //预约日期
                String yyrq = bean.get("yyrq") == null ? "" : bean.get("yyrq").toString();
                SimpleDateFormat sdf = new SimpleDateFormat(IshConstants.GG_DATE_FORMAT);
                SimpleDateFormat sdf_date = new SimpleDateFormat("yyyy年MM月dd日");
                //预约日期
                Date dateTemp;
                try {
                    dateTemp = sdf.parse(yyrq);
                } catch (ParseException e) {
                    e.printStackTrace();
                    logger.error("日期格式转换出错" + e);
                    throw new TransSingleException(CommonErrorCode.ERROR_LXZHCW, "日期格式转换出错");
                }
                text = "您预约了" + sdf_date.format(dateTemp) + " " + bean.get("sjd") + "在" + bean.get("wdmc") + "办理"
                        + bean.get("ywmc") + "！如不能及时到达办理请提前取消。";
                yyh = bean.get("yyh") == null ? "" : bean.get("yyh").toString();
                moduleName_pzwj = "grst.grsy.wdyy.yyxq.menu";
                //----- 封装页面按钮区域（不需修改代码，可修改配置文件） start-----
                menu = userViewISH04Util.initMenuArray(funcinfos, moduleName_pzwj, userViewISH04Util.BTNPRE + "yyxq", "");
                //----- 封装页面按钮区域（不需修改代码，可修改配置文件） end-----
            }
            //-----  封装页面展示字段区域 end -----
            ywsj.put("text", text);
            ywsj.put("yyh", yyh);
            data.put("ywsj", ywsj);
            data.put("menu", menu);
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
     * 获取个人视图-个人首页-最近操作记录模块接口（无需修改）
     *
     * @param request 请求request
     * @return JSONObject
     */
    @RequestMapping(value = "/grsy/hqzjczjl",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject hqZjczjl(HttpServletRequest request) {
        long start = System.currentTimeMillis();
        //要返回的json  格式{'returnCode' : 0, 'message' : '', 'data' : {}}
        JSONObject funcInfosJson = new JSONObject();
        // 模块名字 打印日志时用
        String moduleName_Chi = "个人视图最近操作记录模块";
        //配置文件中模块属性
        String moduleName_pzwj = "grst.grsy.zjczjl.menu";
        logger.info("[+]调用组装" + moduleName_Chi + "接口开始");
        String systemTypeMask = "WTGR";
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
     * 获取个人视图-我的账户-个人基本信息模块接口
     *
     * @param request 请求request
     * @return JSONObject
     */
    @RequestMapping(value = "/wdzh/hqgrjbxx",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject hqGrjbxx(HttpServletRequest request) {
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
        String moduleName_Chi = "个人视图个人基本信息模块";
        logger.info("[+]调用组装" + moduleName_Chi + "接口开始");
        try {
            String poolkey = request.getParameter("poolkey");
            if (StringUtils.isEmpty(poolkey)) {
                throw new TransSingleException(CommonErrorCode.ERROR_NOT_NULL, "poolkey不能为空");
            }
            MainContext mainContext = new MainContext(request);
            DataPool pool = PoolUtil.getPool(poolkey);
            pool.put("grzh", pool.getString("_OPERID"));
            pool.put("flag", "hqgrxx");
            mainContext.setDataPool(pool);
            PoolUtil.savePool(poolkey, pool);
            paramConfigImp.loadMaskData(new String[]{"ish.gg.func.systemtype", "ish.gg.other.sf", "bsp.pb.certitype", "bsp.pb.marstatus", "bsp.pb.xingbie", "bsp.pb.hj", "bsp.pb.occupation"});
            TransEngine.getInstance().execute("TranGRXXBG01", mainContext);
            //资料完整度统计已完善的信息数量
            int count = 0;
            //资料完整度统计的信息总数
            int countTotal = 0;
            //ywsj中jcxx和jtzyxx中的字段
            //字段id
            String id;
            //字段名
            String label;
            //字段值
            String value;
            //字段级别（1时收起和展开都展示，0时只有展开时展示）
            String level;
            JSONObject jsonTemp;
            //----- 封装基础信息字段区域（根据实际情况修改 最多6个字段）start -----
            //业务数据中中的jcxx数据
            JSONArray jcxx = new JSONArray();

            //证件类型信息
            jsonTemp = new JSONObject();
            id = "zjlx";
            label = "证件类型";
            //证件类型字典值
            String dictVal = IshExpression.getUserExtInfo("zjlx");
            value = paramConfigImp.getVal("bsp.pb.certitype." + dictVal);
            level = "1";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //证件号码信息
            jsonTemp = new JSONObject();
            id = "zjhm";
            label = "证件号码";
            value = IshExpression.getUserExtInfo("zjhm");
            countTotal++;
            level = "1";
            if (StringUtils.isNotEmpty(value)) count++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //手机号码信息
            jsonTemp = new JSONObject();
            id = "sjhm";
            label = "手机号码";
            value = IshExpression.getUserExtInfo("sjhm");
            level = "1";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            //配置文件属性
            String moduleName_pzwj = "grst.wdzh.grjbxx.sjhm.button";
            //按钮id前缀
            String btnIdPre = userViewISH04Util.BTNPRE + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".", moduleName_pzwj.lastIndexOf(".") - 1) + 1, moduleName_pzwj.lastIndexOf("."));
            button = userViewISH04Util.initMenuArray(funcinfos, moduleName_pzwj, btnIdPre, icon);
            jsonTemp.put("button", button);
            jcxx.add(jsonTemp);

            //出生日期信息
            jsonTemp = new JSONObject();
            id = "csrq";
            label = "出生日期";
            value = pool.getString("csny");
            level = "1";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //婚姻状况信息
            jsonTemp = new JSONObject();
            id = "hyzk";
            label = "婚姻状况";
            dictVal = pool.getString("hyzk");
            value = paramConfigImp.getVal("bsp.pb.marstatus." + dictVal);
            level = "1";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jcxx.add(jsonTemp);

            //性别信息
            jsonTemp = new JSONObject();
            id = "xb";
            label = "性别";
            dictVal = pool.getString("xingbie");
            value = paramConfigImp.getVal("bsp.pb.xingbie." + dictVal);
            level = "1";
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

            //----- 封装家庭职业信息字段区域（根据实际情况修改 最多5个字段）start -----
            JSONArray jtzyxx = new JSONArray();
            //户籍信息
            jsonTemp = new JSONObject();
            id = "hj";
            label = "户籍";
            dictVal = pool.getString("hj");
            value = paramConfigImp.getVal("bsp.pb.hj." + dictVal);
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jtzyxx.add(jsonTemp);

            //家庭住址信息
            jsonTemp = new JSONObject();
            id = "jtzz";
            label = "家庭住址";
            value = pool.getString("jtzz");
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jtzyxx.add(jsonTemp);

            //家庭住址信息
            jsonTemp = new JSONObject();
            id = "yzbm";
            label = "邮政编码";
            value = pool.getString("yzbm");
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jtzyxx.add(jsonTemp);

            //固定电话信息
            jsonTemp = new JSONObject();
            id = "gddh";
            label = "固定电话";
            value = pool.getString("gddhhm");
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jtzyxx.add(jsonTemp);

            //职业信息
            jsonTemp = new JSONObject();
            id = "zy";
            label = "职业";
            dictVal = pool.getString("zhiye");
            value = paramConfigImp.getVal("bsp.pb.occupation." + dictVal);
            level = "0";
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            jtzyxx.add(jsonTemp);
            ywsj.put("jtzyxx", jtzyxx);
            data.put("ywsj", ywsj);
            //----- 封装家庭职业信息字段区域end -----

            //----- 统计资料完整度信息使用 不展示在页面上（根据实际情况修改） start -----
            //姓名
            value = IshExpression.getUserExtInfo("xingming");
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            //职称
            value = pool.getString("zhichen");
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            //职务
            value = pool.getString("zhiwu");
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            //学历
            value = pool.getString("xueli");
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            //个人缴存基数
            value = pool.getString("grjcjs");
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;
            //家庭月收入
            value = pool.getString("jtysr");
            if (StringUtils.isNotEmpty(value)) count++;
            countTotal++;

            String zlwzd = countTotal == 0 ? "100" : new DecimalFormat("0").format(DoubleUtil.div_down(count * 100, countTotal, 0));
            data.put("zlwzd", zlwzd);
            //----- 统计资料完整度信息使用 不展示在页面上 end -----

            //----- 封装更新资料按钮（无需修改代码，可修改配置文件） start -----
            //配置文件属性
            moduleName_pzwj = "grst.wdzh.grjbxx.gxzl.menu";
            //按钮id前缀
            btnIdPre = userViewISH04Util.BTNPRE + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".", moduleName_pzwj.lastIndexOf(".") - 1) + 1, moduleName_pzwj.lastIndexOf("."));
            menu = userViewISH04Util.initMenuArray(funcinfos, moduleName_pzwj, btnIdPre, icon);
            data.put("button", menu);
            //----- 封装更新资料按钮（无需修改代码，可修改配置文件） end -----
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
     * 获取个人视图-我的账户-个人账户基本信息模块接口
     *
     * @param request 请求request
     * @return JSONObject
     */
    @RequestMapping(value = "/wdzh/hqgrzhjbxx",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject hqGrzhjbxx2(HttpServletRequest request) {
        long start = System.currentTimeMillis();
        //要返回的json  格式{'returnCode' : 0, 'message' : '', 'data' : {}}
        JSONObject funcInfosJson = new JSONObject();
        //json中的data数据
        JSONObject data = new JSONObject();
        //data中的ywsj数据
        JSONArray ywsj = new JSONArray();
        //data中的menu数据 格式	{"id":"","label":"","icon":"","url":"","children":[]}
        JSONArray menu;

        //data中的button数据 格式	{"id":"","label":"","icon":"","url":"","children":[]}
        JSONArray button;
        //缓存中的菜单信息
        JSONObject funcinfos;
        String moduleName_Chi = "个人视图我的账户个人账户基本信息模块";
        logger.info("[+]调用组装" + moduleName_Chi + "接口开始");
        try {
            String poolkey = request.getParameter("poolkey");
            if (StringUtils.isEmpty(poolkey)) {
                throw new TransSingleException(CommonErrorCode.ERROR_NOT_NULL, "poolkey不能为空");
            }
            MainContext mainContext = new MainContext(request);
            DataPool pool = PoolUtil.getPool(poolkey);
            pool.put("grzh", pool.getString("_OPERID"));
            mainContext.setDataPool(pool);
            PoolUtil.savePool(poolkey, pool);
            paramConfigImp.loadMaskData(new String[]{"ish.gg.func.systemtype", "ish.gg.other.sf"});
            //调用交易获取个人账户信息
            TransEngine.getInstance().execute("TranZHJBXX01", mainContext);
            // ------ 封装页面展示账户余额和账户状态数据（根据实际情况修改） start -----
            JSONObject jsonTemp = new JSONObject();
            //个人账户余额信息
            jsonTemp.put("label", "账户余额");
            jsonTemp.put("value", MoneyUtil.addComma(pool.getString("grzhye")));
            data.put("zhye", jsonTemp);
            //个人账户状态信息
            jsonTemp = new JSONObject();
            jsonTemp.put("label", "账户状态");
            String zhtz = pool.getString("grzhzt");
            jsonTemp.put("value", zhtz);
            paramConfigImp.loadMaskData(new String[]{"bsp.dp.grzhzt", "ish.gg.func.status", "ish.gg.other.sf", "ish.gg.func.systemtype"});
            String text = paramConfigImp.getVal("bsp.dp.grzhzt." + zhtz);
            //text 长度只能为2，如果超长在此处重新赋值或者处理(目前字典项中只有销户状态超长，统一使用最后两个字销户)
            text = text.length() > 2 ? text.substring(text.length() - 2) : text;
            jsonTemp.put("text", text);
            data.put("zhzt", jsonTemp);
            // ------ 封装页面展示账户余额和账户状态数据（根据实际情况修改） end -----

            // ------ 封装页面展示我要提取和账户明细按钮数据（无需修改代码，可修改配置文件） start -----
            YDMemcachedManager manager = YDMemcachedManager.newInstance();
            if (manager.get("funcInfos") != null) {
                funcinfos = JSONObject.parseObject(manager.get("funcInfos").toString());
            } else {
                funcinfos = initAllFuncToMemcached.initAllFuncToMemcached();
            }
            //配置文件中模块属性
            String moduleName_pzwj = "grst.wdzh.grzhjbxx.button";
            //按钮id前缀
            String btnIdPre = userViewISH04Util.BTNPRE + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".", moduleName_pzwj.lastIndexOf(".") - 1) + 1, moduleName_pzwj.lastIndexOf("."));
            button = userViewISH04Util.initMenuArray(funcinfos, moduleName_pzwj, btnIdPre, "");
            data.put("button", button);
            // ------ 封装页面展示我要提取和账户明细按钮数据（无需修改代码，可修改配置文件） end -----

            // ------ 封装页面展示业务字段数据（根据实际情况修改） start -----
            //ywsj中的字段
            //字段id
            String id;
            //字段名
            String label;
            //字段值
            String value;
            //字段级别（1时收起和展开都展示最多6个是1的，0时只有展开时展示）
            String level;

            //个人账号信息
            jsonTemp = new JSONObject();
            id = "grzh";
            label = "个人账号";
            value = pool.getString("_OPERID");
            level = "1";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            ywsj.add(jsonTemp);

            //单位账号信息
            jsonTemp = new JSONObject();
            id = "dwzh";
            label = "单位账号";
            value = pool.getString("_ORGID");
            level = "1";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            ywsj.add(jsonTemp);

            //单位名称信息
            jsonTemp = new JSONObject();
            id = "dwmc";
            label = "单位名称";
            value = pool.getString("_ORGNAME");
            level = "1";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            ywsj.add(jsonTemp);

            //缴至年月信息
            jsonTemp = new JSONObject();
            id = "jzny";
            label = "缴至年月";
            value = pool.getString("jzny");
            level = "1";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            ywsj.add(jsonTemp);

            //缴存基数信息
            jsonTemp = new JSONObject();
            id = "grjcjs";
            label = "缴存基数";
            value = MoneyUtil.addComma(pool.getString("grjcjs")) + "元";
            level = "1";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            ywsj.add(jsonTemp);

            //月应缴存额信息
            jsonTemp = new JSONObject();
            id = "yjce";
            label = "月应缴存额";
            value = MoneyUtil.addComma(pool.getString("yjce")) + "元";
            level = "1";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            ywsj.add(jsonTemp);

            //个人缴存比例信息
            jsonTemp = new JSONObject();
            id = "grjcbl";
            label = "个人缴存比例";
            value = pool.getString("grjcbl") + "%";
            level = "0";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            ywsj.add(jsonTemp);

            //单位比例信息
            jsonTemp = new JSONObject();
            id = "dwjcbl";
            label = "单位缴存比例";
            value = pool.getString("dwjcbl") + "%";
            level = "0";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            ywsj.add(jsonTemp);

            //开户日期信息
            jsonTemp = new JSONObject();
            id = "khrq";
            label = "开户日期";
            value = pool.getString("khrq");
            level = "0";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            ywsj.add(jsonTemp);

            //缴存银行信息
            jsonTemp = new JSONObject();
            id = "jcyh";
            label = "缴存银行";
            value = pool.getString("jcyh");
            level = "0";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            ywsj.add(jsonTemp);

            //关联银行信息
            jsonTemp = new JSONObject();
            id = "glyh";
            label = "关联银行";
            value = pool.getString("glyh");
            level = "0";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            ywsj.add(jsonTemp);

            //关联银行卡号信息
            jsonTemp = new JSONObject();
            id = "glyhkh";
            label = "关联银行卡号";
            value = pool.getString("glyhkh");
            level = "0";
            jsonTemp.put("id", id);
            jsonTemp.put("label", label);
            jsonTemp.put("value", value);
            jsonTemp.put("level", level);
            jsonTemp.put("button", new JSONArray());
            ywsj.add(jsonTemp);
            data.put("ywsj", ywsj);
            // ------ 封装页面展示业务字段数据（根据实际情况修改） end -----

            // ------ 封装页面底侧菜单数据（无需修改代码，可修改配置文件） start -----
            //配置文件中模块属性
            moduleName_pzwj = "grst.wdzh.grzhjbxx.menu";
            //按钮id前缀  防止与其他id冲突最后又拼一节
            btnIdPre = userViewISH04Util.BTNPRE + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".", moduleName_pzwj.lastIndexOf(".") - 1) + 1, moduleName_pzwj.lastIndexOf(".")) + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".") + 1);
            menu = userViewISH04Util.initMenuArray(funcinfos, moduleName_pzwj, btnIdPre, "");
            data.put("menu", menu);
            // ------ 封装页面底侧菜单数据（无需修改代码，可修改配置文件） end -----
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
     * 获取个人视图-我的账户-公积金贷款账户信息模块接口
     *
     * @param request 请求request
     * @return JSONObject
     */
    @RequestMapping(value = "/wdzh/hqgjjdkzhxx",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject hqGjjdkzhxx(HttpServletRequest request) {
        long start = System.currentTimeMillis();
        //要返回的json  格式{'returnCode' : 0, 'message' : '', 'data' : {}}
        JSONObject funcInfosJson = new JSONObject();
        //json中的data数据
        JSONObject data = new JSONObject();
        //data中的ywsj数据
        JSONArray ywsj = new JSONArray();
        //data中的menu数据 格式	{"id":"","label":"","icon":"","url":"","children":[]}
        JSONArray menu;
        //data中的dkjd数据 格式
        JSONArray dkjd = new JSONArray();

        //data中的button数据 格式	{"id":"","label":"","icon":"","url":"","children":[]}
        JSONArray button;
        //缓存中的菜单信息
        JSONObject funcinfos = new JSONObject();
        String moduleName_Chi = "个人视图公积金贷款账户信息模块";
        logger.info("[+]调用组装" + moduleName_Chi + "接口开始");
        try {
            String poolkey = request.getParameter("poolkey");
            if (StringUtils.isEmpty(poolkey)) {
                throw new TransSingleException(CommonErrorCode.ERROR_NOT_NULL, "poolkey不能为空");
            }
            MainContext mainContext = new MainContext(request);
            DataPool pool = PoolUtil.getPool(poolkey);
            pool.put("grzh", pool.getString("_OPERID"));
            String instance = pool.get("_IS") == null ? "" : pool.getString("_IS");
            if (StringUtils.isEmpty(instance)) {
                instance = ydFlowEvent.getInstanceId();
                pool.put("_IS", instance);
            }
            mainContext.setDataPool(pool);
            PoolUtil.savePool(poolkey, pool);
            paramConfigImp.loadMaskData(new String[]{"ish.gg.func.systemtype", "ish.gg.other.sf", "bsp.ln.loancontrstate1"});
            //调用获取个人信息接口
            XmlResObj xmlResObj = EssFactory.sendExternal("BSP_DP_GETGRXX_01", mainContext, false);
            XmlResHead head = xmlResObj.getHead();
            if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
                throw new TransSingleException(head.getParticular_info());
            }
            Map<String, Object> body = xmlResObj.getBody();
            String dkzt = body.get("dkzt") == null || StringUtils.isEmpty(body.get("dkzt").toString()) ? "0" : body.get("dkzt").toString();
            String jkhtbh = body.get("jkhtbh") == null || StringUtils.isEmpty(body.get("jkhtbh").toString()) ? "0" : body.get("jkhtbh").toString();
            pool.put("jkhtbh", jkhtbh);
            pool.put("jkhtbhsz", jkhtbh);
            JSONObject chart = new JSONObject();
            JSONObject jsonTemp;
            //字段id
            String id;
            //字段名
            String label;
            //字段值
            String value;
            //字段级别（1时收起和展开都展示,还款状态最多4个1,贷中状态最多6个1,无贷款状态最多6个1;0时只有展开时展示）
            String level;
            //配置文件模块属性
            String moduleName_pzwj;
            //按钮id前缀
            String btnIdPre;
            if ("2".equals(dkzt)) {//还款中状态
                TransEngine.getInstance().execute("TranHQDKXX01", mainContext);
                // ----- 封装echart 图表部分数据 （根据实际情况修改） start -----
                //贷款状态
                data.put("dkzt", dkzt);
                //贷款已还本金信息
                jsonTemp = new JSONObject();
                label = "贷款已还本金";
                value = MoneyUtil.addComma(pool.getString("hsbjze"));
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                chart.put("dkyh", jsonTemp);

                //贷款余额信息
                jsonTemp = new JSONObject();
                label = "贷款余额";
                value = MoneyUtil.addComma(pool.getString("dkye"));
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                chart.put("dkye", jsonTemp);

                //贷款总额
                jsonTemp = new JSONObject();
                label = "贷款总额";
                value = MoneyUtil.addComma(pool.getString("htdkje"));
                jsonTemp.put("label", label);
                jsonTemp.put("value",value);
                chart.put("dkze", jsonTemp);

                data.put("chart", chart);
                // ----- 封装echart 图标部分数据 end -----

                // ----- 封装贷款账户状态图标部分（根据实际情况修改） start -----
                //逾期情况（0未逾期，1逾期）
                value = pool.getString("sfyq");
                data.put("dkzhzt", value);
                // ----- 封装贷款账户状态图标部分 end -----

                // ----- 封装提前还款和还款明细 button按钮部分(无需修改代码，可修改配置文件) start -----
                //配置文件中模块属性
                moduleName_pzwj = "grst.wdzh.gjjdkzh_hkzt.button";
                //按钮id前缀
                btnIdPre = userViewISH04Util.BTNPRE + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".", moduleName_pzwj.lastIndexOf(".") - 1) + 1, moduleName_pzwj.lastIndexOf("."));

                button = userViewISH04Util.initMenuArray(funcinfos, moduleName_pzwj, btnIdPre, "");
                data.put("button", button);
                // ----- 封装提前还款和还款明细 button按钮部分(无需修改代码，可修改配置文件) end -----

                // ------ 封装页面展示业务字段数据（根据实际情况修改） start -----
                //借款合同编号信息
                jsonTemp = new JSONObject();
                id = "jkhtbh";
                label = "借款合同编号";
                value = jkhtbh;
                level = "1";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                jsonTemp.put("button", new JSONArray());
                ywsj.add(jsonTemp);

                //剩余期数信息
                jsonTemp = new JSONObject();
                id = "syqs";
                label = "剩余期数";
                value = pool.getString("syqs") + "期";
                level = "1";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                moduleName_pzwj = "grst.wdzh.gjjdkzh.syqs.button";
                btnIdPre = userViewISH04Util.BTNPRE + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".", moduleName_pzwj.lastIndexOf(".") - 1) + 1, moduleName_pzwj.lastIndexOf("."));
                button = userViewISH04Util.initMenuArray(funcinfos, moduleName_pzwj, btnIdPre, icon);
                jsonTemp.put("button", button);
                ywsj.add(jsonTemp);

                //逾期总金额信息
                jsonTemp = new JSONObject();
                id = "yqzje";
                label = "逾期总金额";
                value = MoneyUtil.addComma(pool.getString("yqzje")) + "元";
                level = "1";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                moduleName_pzwj = "grst.wdzh.gjjdkzh.yqzje.button";
                btnIdPre = userViewISH04Util.BTNPRE + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".", moduleName_pzwj.lastIndexOf(".") - 1) + 1, moduleName_pzwj.lastIndexOf("."));
                button = userViewISH04Util.initMenuArray(funcinfos, moduleName_pzwj, btnIdPre, icon);
                jsonTemp.put("button", button);
                ywsj.add(jsonTemp);

                //逾期本金总额信息
                jsonTemp = new JSONObject();
                id = "yqbjze";
                label = "逾期本金总额";
                value = MoneyUtil.addComma(pool.getString("yqbjze")) + "元";
                level = "0";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                jsonTemp.put("button", new JSONArray());
                ywsj.add(jsonTemp);

                //逾期利息总额信息
                jsonTemp = new JSONObject();
                id = "yqlxze";
                label = "逾期利息总额";
                value = MoneyUtil.addComma(pool.getString("yqlxze")) + "元";
                level = "0";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                jsonTemp.put("button", new JSONArray());
                ywsj.add(jsonTemp);

                //罚息总额信息
                jsonTemp = new JSONObject();
                id = "fxze";
                label = "罚息总额";
                value = MoneyUtil.addComma(pool.getString("yqlxze")) + "元";
                level = "0";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                jsonTemp.put("button", new JSONArray());
                ywsj.add(jsonTemp);

                //贷款发放额信息
                jsonTemp = new JSONObject();
                id = "dkffe";
                label = "贷款发放额";
                value = MoneyUtil.addComma(pool.getString("dkffe")) + "元";
                level = "0";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                jsonTemp.put("button", new JSONArray());
                ywsj.add(jsonTemp);

                //贷款期数信息
                jsonTemp = new JSONObject();
                id = "dkqs";
                label = "贷款期数";
                value = pool.getString("dkqs") + "期";
                level = "0";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                jsonTemp.put("button", new JSONArray());
                ywsj.add(jsonTemp);

                //贷款发放日期信息
                jsonTemp = new JSONObject();
                id = "dkffrq";
                label = "贷款发放日期";
                value = pool.getString("dkffrq");
                level = "0";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                jsonTemp.put("button", new JSONArray());
                ywsj.add(jsonTemp);

                //月还款额信息
                jsonTemp = new JSONObject();
                id = "yhke";
                label = "月还款额";
                value = MoneyUtil.addComma(pool.getString("yhke")) + "元";
                level = "0";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                ywsj.add(jsonTemp);

                //贷款利率信息
                jsonTemp = new JSONObject();
                id = "dkll";
                label = "贷款利率";
                value = pool.getString("dkll") + "%";
                level = "0";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                jsonTemp.put("button", new JSONArray());
                ywsj.add(jsonTemp);

                //约定还款日信息
                jsonTemp = new JSONObject();
                id = "ydhkr";
                label = "约定还款日";
                value = pool.getString("ydhkr") + "日";
                level = "1";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                moduleName_pzwj = "grst.wdzh.gjjdkzh.ydhkr.button";
                btnIdPre = userViewISH04Util.BTNPRE + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".", moduleName_pzwj.lastIndexOf(".") - 1) + 1, moduleName_pzwj.lastIndexOf("."));
                button = userViewISH04Util.initMenuArray(funcinfos, moduleName_pzwj, btnIdPre, icon);
                jsonTemp.put("button", button);
                ywsj.add(jsonTemp);

                //贷款还款方式信息
                jsonTemp = new JSONObject();
                id = "dkhkfs";
                label = "贷款还款方式";
                String dict_value = pool.getString("dkhkfs");
                value = paramConfigImp.getVal("bsp.ln.repaymode." + dict_value);
                level = "0";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                moduleName_pzwj = "grst.wdzh.gjjdkzh.dkhkfs.button";
                btnIdPre = userViewISH04Util.BTNPRE + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".", moduleName_pzwj.lastIndexOf(".") - 1) + 1, moduleName_pzwj.lastIndexOf("."));
                button = userViewISH04Util.initMenuArray(funcinfos, moduleName_pzwj, btnIdPre, icon);
                jsonTemp.put("button", button);
                ywsj.add(jsonTemp);

                //还款账号信息
                jsonTemp = new JSONObject();
                id = "hkzh";
                label = "还款账号";
                value = pool.getString("hkzh");
                level = "0";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                moduleName_pzwj = "grst.wdzh.gjjdkzh.hkzh.button";
                btnIdPre = userViewISH04Util.BTNPRE + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".", moduleName_pzwj.lastIndexOf(".") - 1) + 1, moduleName_pzwj.lastIndexOf("."));
                button = userViewISH04Util.initMenuArray(funcinfos, moduleName_pzwj, btnIdPre, icon);
                jsonTemp.put("button", button);
                ywsj.add(jsonTemp);

                //受委托银行名称信息
                jsonTemp = new JSONObject();
                id = "swtyhmc";
                label = "受委托银行名称";
                value = pool.getString("swtyhmc");
                level = "0";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                jsonTemp.put("button", new JSONArray());
                ywsj.add(jsonTemp);
                // ------ 封装页面底侧菜单数据（无需修改代码，可修改配置文件） start -----
                //配置文件中模块属性
                moduleName_pzwj = "grst.wdzh.gjjdkzh_hkzt.menu";
                //按钮id前缀  防止与其他id冲突最后又拼一节
                btnIdPre = userViewISH04Util.BTNPRE + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".", moduleName_pzwj.lastIndexOf(".") - 1) + 1, moduleName_pzwj.lastIndexOf(".")) + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".") + 1);
                menu = userViewISH04Util.initMenuArray(funcinfos, moduleName_pzwj, btnIdPre, "");
                // ------ 封装页面底侧菜单数据（无需修改代码，可修改配置文件） end -----
            } else if ("1".equals(dkzt)) {//贷中状态
                TransEngine.getInstance().execute("TranDKJDCX01", mainContext);
                data.put("dkzt", dkzt);
                //个人贷款进度流程可视化配置的名称 固定值
                String kshlcmc = "个人贷款进度查询";
                //核心返回的贷款流程json
                String dklcStr = pool.getString("dklc");
                JSONArray dklc = JSONArray.parseArray(dklcStr);
                //获取数据库中配置的流程可视化信息
                List<CPLFlowView> steplist = cplFlowViewMapper.selectByKshlcmc(kshlcmc);
                //节点状态
                String jdzt;
                //节点名称
                String jdmc;
                //节点日期
                String jdrq;
                jsonTemp = new JSONObject();
                //设置初始节点
                jdzt = "1";
                jdmc = "开始";
                jdrq = "";
                jsonTemp.put("jdzt", jdzt);
                jsonTemp.put("jdmc", jdmc);
                jsonTemp.put("jdrq", jdrq);
                dkjd.add(jsonTemp);
                //封装核心返回的贷款进度信息
                //核心返回的dklc中的每个节点
                JSONObject json;
                //步骤标志
                String bzbz = "";
                //通过标志
                String tgbz = "";


                for (int i = 0; i < steplist.size(); i++) {
                    jsonTemp = new JSONObject();
                    jdzt = "0";
                    jdmc = steplist.get(i).getBzms();
                    jdrq = "";
                    //遍历核心返回的dklc
                    for (Object object : dklc) {
                        json = JSONObject.parseObject(object.toString());
                        if (json != null && json.containsKey("bzbz")) {
                            bzbz = json.getString("bzbz");
                        }
                        if ((steplist.get(i).getBzbz()).equals(bzbz)) {
                            if (json != null && json.containsKey("tgbz")) {
                                tgbz = json.getString("tgbz");
                                if ("true".equals(tgbz)) {
                                    jdzt = "1";
                                }
                            }
                            if (json != null && json.containsKey("date")) {
                                jdrq = json.getString("date");
                            }
                            //节点有日期，通过标志为false时表示为当前节点进行中
                            if (StringUtils.isNotEmpty(jdrq) && "false".equals(tgbz)) {
                                jdzt = "2";
                            }
                            break;
                        }
                    }
                    jsonTemp.put("jdzt", jdzt);
                    jsonTemp.put("jdmc", jdmc);
                    jsonTemp.put("jdrq", jdrq);
                    dkjd.add(jsonTemp);
                }
                //封装结束节点信息
                jsonTemp = new JSONObject();
                jdmc = "结束";
                jdrq = "";
                //结束节点状态与结束上一个节点的状态一直
                jsonTemp.put("jdzt", jdzt);
                jsonTemp.put("jdmc", jdmc);
                jsonTemp.put("jdrq", jdrq);
                dkjd.add(jsonTemp);
                data.put("dkjd", dkjd);

                // ------ 封装页面展示业务字段数据（根据实际情况修改） start -----
                //贷款申请编号信息
                jsonTemp = new JSONObject();
                id = "dksqbh";
                label = "贷款申请编号";
                value = pool.getString("dksqbh");
                level = "1";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                jsonTemp.put("button", new JSONArray());
                ywsj.add(jsonTemp);

                //借款合同编号信息
                jsonTemp = new JSONObject();
                id = "jkhtbh";
                label = "借款合同编号";
                value = pool.getString("jkhtbh");
                level = "1";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                jsonTemp.put("button", new JSONArray());
                ywsj.add(jsonTemp);

                //合同贷款金额信息
                jsonTemp = new JSONObject();
                id = "htdkje";
                label = "合同贷款金额";
                value = MoneyUtil.addComma(pool.getString("htdkje")) + "元";
                level = "1";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                jsonTemp.put("button", new JSONArray());
                ywsj.add(jsonTemp);

                //贷款期数信息
                jsonTemp = new JSONObject();
                id = "dkqs";
                label = "贷款期数";
                value = pool.getString("dkqs") + "期";
                level = "1";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                jsonTemp.put("button", new JSONArray());
                ywsj.add(jsonTemp);

                //贷款类型信息
                jsonTemp = new JSONObject();
                id = "dklx";
                label = "贷款类型";
                String dictVal = pool.getString("dklx");
                value = paramConfigImp.getVal("bsp.ln.dklx." + dictVal);
                level = "1";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                jsonTemp.put("button", new JSONArray());
                ywsj.add(jsonTemp);

                //受委托银行名称信息
                jsonTemp = new JSONObject();
                id = "swtyhmc";
                label = "受委托银行名称";
                value = pool.getString("swtyhmc");
                level = "1";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                jsonTemp.put("button", new JSONArray());
                ywsj.add(jsonTemp);

                //房屋性质信息
                jsonTemp = new JSONObject();
                id = "fwxz";
                label = "房屋性质";
                value = pool.getString("fwxz");
                level = "0";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                jsonTemp.put("button", new JSONArray());
                ywsj.add(jsonTemp);

                //房屋总价信息
                jsonTemp = new JSONObject();
                id = "fwzj";
                label = "房屋总价";
                value = MoneyUtil.addComma(pool.getString("fwzj")) + "元";
                level = "0";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                jsonTemp.put("button", new JSONArray());
                ywsj.add(jsonTemp);

                //房屋坐落信息
                jsonTemp = new JSONObject();
                id = "fwzl";
                label = "房屋坐落";
                value = pool.getString("fwzl");
                level = "0";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                jsonTemp.put("button", new JSONArray());
                ywsj.add(jsonTemp);
                data.put("ywsj", ywsj);
                // ------ 封装页面展示业务字段数据（根据实际情况修改） end -----

                // ------ 封装页面底侧菜单数据（无需修改代码，可修改配置文件） start -----
                //配置文件中模块属性
                moduleName_pzwj = "grst.wdzh.gjjdkzh_sqzt.menu";
                //按钮id前缀  防止与其他id冲突最后又拼一节
                btnIdPre = userViewISH04Util.BTNPRE + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".", moduleName_pzwj.lastIndexOf(".") - 1) + 1, moduleName_pzwj.lastIndexOf(".")) + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".") + 1);
                menu = userViewISH04Util.initMenuArray(funcinfos, moduleName_pzwj, btnIdPre, "");
                // ------ 封装页面底侧菜单数据（无需修改代码，可修改配置文件） end -----

            } else {//无未还清贷款
                TransEngine.getInstance().execute("TranHQDKZC01", mainContext);
                data.put("dkzt", dkzt);

                // ----- 封装贷款申请 button按钮部分(无需修改代码，可修改配置文件) start -----
                //配置文件中模块属性
                moduleName_pzwj = "grst.wdzh.gjjdkzh_wdkzt.button";
                //按钮id前缀
                btnIdPre = userViewISH04Util.BTNPRE + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".", moduleName_pzwj.lastIndexOf(".") - 1) + 1, moduleName_pzwj.lastIndexOf("."));
                button = userViewISH04Util.initMenuArray(funcinfos, moduleName_pzwj, btnIdPre, "");
                data.put("button", button);
                // ----- 封装贷款申请 button按钮部分(无需修改代码，可修改配置文件) end -----

                // ------ 封装页面展示业务字段数据（根据实际情况修改） start -----
                //个人住房贷款最高额度信息
                jsonTemp = new JSONObject();
                id = "grzfdkzged";
                label = "个人住房贷款最高额度";
                value = MoneyUtil.addComma(pool.getString("grzfdkzged")) + "元";
                level = "1";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                jsonTemp.put("button", new JSONArray());
                ywsj.add(jsonTemp);

                //夫妻住房贷款最高额度信息
                jsonTemp = new JSONObject();
                id = "fqzfdkzged";
                label = "夫妻住房贷款最高额度";
                value = MoneyUtil.addComma(pool.getString("fqzfdkzged")) + "元";
                level = "1";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                jsonTemp.put("button", new JSONArray());
                ywsj.add(jsonTemp);

                //个人住房贷款最长年限信息
                jsonTemp = new JSONObject();
                id = "grzfdkzcnx";
                label = "个人住房贷款最长年限";
                value = pool.getString("grzfdkzcnx") + "年";
                level = "1";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                jsonTemp.put("button", new JSONArray());
                ywsj.add(jsonTemp);

                //夫妻住房贷款最长年限信息
                jsonTemp = new JSONObject();
                id = "fqzfdkzcnx";
                label = "夫妻住房贷款最长年限";
                value = pool.getString("fqzfdkzcnx") + "年";
                level = "1";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                jsonTemp.put("button", new JSONArray());
                ywsj.add(jsonTemp);

                //住房公积金贷款利率五年以内信息
                jsonTemp = new JSONObject();
                id = "zfgjjdklvwnyn";
                label = "住房公积金贷款利率（五年以内）";
                value = pool.getString("zfgjjdklvwnyn");
                level = "1";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                jsonTemp.put("button", new JSONArray());
                ywsj.add(jsonTemp);

                //住房公积金贷款利率五年以上信息
                jsonTemp = new JSONObject();
                id = "zfgjjdklvwnys";
                label = "住房公积金贷款利率（五年以上）";
                value = pool.getString("zfgjjdklvwnys");
                level = "1";
                jsonTemp.put("id", id);
                jsonTemp.put("label", label);
                jsonTemp.put("value", value);
                jsonTemp.put("level", level);
                jsonTemp.put("button", new JSONArray());
                ywsj.add(jsonTemp);

                // ------ 封装页面展示业务字段数据（根据实际情况修改） start -----
                // ------ 封装页面底侧菜单数据（无需修改代码，可修改配置文件） start -----
                //配置文件中模块属性
                moduleName_pzwj = "grst.wdzh.gjjdkzh_wdkzt.menu";
                //按钮id前缀  防止与其他id冲突最后又拼一节
                btnIdPre = userViewISH04Util.BTNPRE + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".", moduleName_pzwj.lastIndexOf(".") - 1) + 1, moduleName_pzwj.lastIndexOf(".")) + moduleName_pzwj.substring(moduleName_pzwj.lastIndexOf(".") + 1);
                menu = userViewISH04Util.initMenuArray(funcinfos, moduleName_pzwj, btnIdPre, "");
                // ------ 封装页面底侧菜单数据（无需修改代码，可修改配置文件） end -----
            }
            data.put("ywsj", ywsj);
            data.put("menu", menu);

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
     * 获取用户视图-右侧悬浮框展示控制数据接口
     *
     * @param request 请求request
     * @return JSONObject
     */
    @RequestMapping(value = "/gg/hqycxfk",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject hqYcxfk(HttpServletRequest request) {
        long start = System.currentTimeMillis();
        //要返回的json  格式{'returnCode' : 0, 'message' : '', 'data' : {}}
        JSONObject funcInfosJson = new JSONObject();
        //json中的data数据
        JSONObject data = new JSONObject();
        //data中的ywsj数据
        JSONObject ywsj = new JSONObject();
        // 模块名字 打印日志时用
        String moduleName_Chi = "用户视图右侧悬浮框展示数据";
        logger.info("[+]调用组装" + moduleName_Chi + "接口开始");
        //配置文件中模块属性
        String moduleName_pzwj;
        try {
            String poolkey = request.getParameter("poolkey");
            if (StringUtils.isEmpty(poolkey)) {
                throw new TransSingleException(CommonErrorCode.ERROR_NOT_NULL, "poolkey不能为空");
            }
            MainContext mainContext = new MainContext(request);
            DataPool pool = PoolUtil.getPool(poolkey);
            mainContext.setDataPool(pool);
            PoolUtil.savePool(poolkey, pool);
            String loginType = pool.getString("_LONGINTYPE");
            String wdzhUrl = "";
            //个人用户登录
            if ("person".equals(loginType)) {
                moduleName_Chi = "个人视图右侧悬浮框展示数据";
                moduleName_pzwj = "grst.gg.ycxfk";
                wdzhUrl = "/flow/menu/WFWDZH01";
            } else if ("orgauth".equals(loginType)) {
                //单位用户登录
                moduleName_Chi = "单位视图右侧悬浮框展示数据";
                moduleName_pzwj = "dwst.gg.ycxfk";
                wdzhUrl = "/flow/menu/WFWDZH02";
            } else {
                //除个人和单位的其他用户登录
                moduleName_Chi = "其他视图右侧悬浮框展示数据";
                moduleName_pzwj = "qtst.gg.ycxfk";
                wdzhUrl = "";
            }
            String yhstPz = PropertyUtil.getString("yhst.properties", moduleName_pzwj);
            String[] ycxfkArr;
            //封装除在线客服和帮助 以外的悬浮框中的按钮是否展示(在线客服和帮助沿用以前逻辑，不需要在此处控制)
            for (String grstFuncTemp : yhstPz.split(",")) {
                ycxfkArr = grstFuncTemp.split(":");
                ywsj.put(ycxfkArr[0], StringUtils.isEmpty(ycxfkArr[1]) ? "0" : ycxfkArr[1]);
            }
            data.put("ywsj", ywsj);
            data.put("wdzhUrl", wdzhUrl);
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


}
