package com.yd.ish.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yd.basic.expression.IshExpression;
import com.yd.biz.exception.TransSingleException;
import com.yd.deplatform.util.PropertyUtil;
import com.yd.ish.common.dao.mybatis.CPLFlowViewMapper;
import com.yd.ish.common.util.StringUtils;
import com.yd.ish.dataanalysis.model.mybatis.FuncLog;
import com.yd.ish.dataanalysis.service.mybatis.FuncLogService;
import com.yd.ish.dataanalysis.service.mybatis.FuncRecommendService;
import com.yd.org.model.mybatis.CPLFunc;
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
import com.yd.svrplatform.util.ReadProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class UserViewISH04Util {

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
    CPLFlowViewMapper cplFlowViewMapper;

    public static final String BTNPRE = "btn_";
    private static final Logger logger = LoggerFactory.getLogger(UserViewISH04Util.class);
    //你可能要办理模块展示推荐业务的总数量
    private static final String dataanalysis_totalStr = ReadProperty.getString("dataanalysis_total");

    /**
     * 封装纯菜单模块方法（单位和个人公用）
     *
     * @param request         请求
     * @param moduleName_Chi  模块中文名
     * @param moduleName_pzwj 模块对应的配置文件属性
     * @return JSONObject
     */
    public JSONObject initFuncs(HttpServletRequest request, String moduleName_Chi, String moduleName_pzwj, String btnIdPre) {
        logger.info("[+]开始执行initFuncs方法，moduleName_Chi：" + moduleName_Chi + ",moduleName_pzwj" + moduleName_pzwj + ",btnIdPre:" + btnIdPre);
        //要返回的json  格式{'returnCode' : 0, 'message' : '', 'data' : {}}
        JSONObject funcInfosJson = new JSONObject();
        //json中data数据，格式{'ywsj':{},menu:[]}
        JSONObject data = new JSONObject();
        //data中的menu数据 格式menu : [{}，{}]
        JSONArray menu;
        String poolkey = request.getParameter("poolkey");
        if (StringUtils.isEmpty(poolkey)) {
            throw new TransSingleException(CommonErrorCode.ERROR_NOT_NULL, "poolkey不能为空");
        }
        MainContext mainContext = new MainContext(request);
        DataPool pool = PoolUtil.getPool(poolkey);
        mainContext.setDataPool(pool);
        PoolUtil.savePool(poolkey, pool);
        paramConfigImp.loadMaskData(new String[]{"ish.gg.other.sf", "ish.gg.func.systemtype"});
        //缓存中菜单信息
        JSONObject funcinfos;
        YDMemcachedManager manager = YDMemcachedManager.newInstance();
        if (manager.get("funcInfos") != null) {
            funcinfos = JSONObject.parseObject(manager.get("funcInfos").toString());
        } else {
            funcinfos = initAllFuncToMemcached.initAllFuncToMemcached();
        }
        menu = initMenuArray(funcinfos, moduleName_pzwj, btnIdPre, "");
        data.put("menu", menu);
        funcInfosJson.put("returnCode", 0);
        funcInfosJson.put("message", "获取" + moduleName_Chi + "数据成功");
        funcInfosJson.put("data", data);
        logger.info("[-]结束执行initFuncs方法");
        return funcInfosJson;
    }

    /**
     * 封装menu数组方法（单位和个人公用）
     *
     * @param funcinfos       缓存中存的菜单信息
     * @param moduleName_pzwj 模块对应的配置文件属性
     * @param btnIdPre        按钮ID前缀
     * @param iconDefault     菜单图标（如果传""则取菜单配置的icon，不空取传的值）
     * @return JSONArray
     */
    public JSONArray initMenuArray(JSONObject funcinfos, String moduleName_pzwj, String btnIdPre, String iconDefault) {
        logger.info("[+]开始执行initMenuArray方法,moduleName_pzwj:" + moduleName_pzwj + ",btnIdPre:" + btnIdPre + ",iconDefault:" + iconDefault);
        //配置文件中的功能信息
        String yhstPzdy = PropertyUtil.getString("yhst.properties", moduleName_pzwj);
        //data中的menu数据 格式menu : [{}，{}]
        JSONArray menu = new JSONArray();
        //menu的id
        String id;
        //menu的label
        String label;
        //menu 的url
        String url;
        //menu 的 icon
        String icon;
        //功能编码
        String funccode;
        CPLFunc cplFunc;
        //隐藏标识
        String funcexp;
        //停用标识
        String actflag;
        //功能名称
        String funcname;
        //单个功能数组
        String[] yhstFuncArr;
        //单条menu数据 格式	{"id":"","label":"","icon":"","url":"","children":[]}
        JSONObject menuJson;
        //缓存中单个菜单信息
        JSONObject funcinfo;
        paramConfigImp.loadMaskData(new String[]{"ish.gg.func.status", "ish.gg.other.sf", "ish.gg.func.systemtype"});
        //功能状态正常字典值
        String dictVal_ZC = paramConfigImp.getValByMask("ish.gg.func.status", "ZC");
        int count = 0;
        for (String yhstFuncTemp : yhstPzdy.split(",")) {
            id = btnIdPre + "_" + count++;
            menuJson = new JSONObject();
            yhstFuncArr = yhstFuncTemp.split(":");
            funccode = yhstFuncArr[0];
            String moduleName_Child = moduleName_pzwj + ".children." + funccode;
            String moduleName_Child_Pzdy = PropertyUtil.getString("yhst.properties", moduleName_Child);
            //配置文件中某个功能有children属性时，递归调用封装menujson
            if (StringUtils.isNotEmpty(moduleName_Child_Pzdy)) {
                JSONArray children;
                children = initMenuArray(funcinfos, moduleName_Child, id, iconDefault);
                menuJson.put("children", children);
            } else {
                menuJson.put("children", new JSONArray());
            }
            //配置文件中配置了功能名称就使用配置文件中的
            funcname = yhstFuncArr.length > 1 ? yhstFuncArr[1] : "";
            label = funcname;
            if (funcinfos != null && funcinfos.containsKey(funccode)) {//缓存中有则从缓存中取
                funcinfo = funcinfos.getJSONObject(funccode);
                if (StringUtils.isEmpty(label)) {
                    label = funcinfo.containsKey("funcname") ? funcinfo.getString("funcname") : "";
                }
                url = funcinfo.containsKey("href") ? funcinfo.getString("href") : "";
                //如果没传参数ICON则去菜单中的ICON
                if (StringUtils.isEmpty(iconDefault)) {
                    icon = funcinfo.containsKey("funcsign") ? funcinfo.getString("funcsign") : "";
                } else {
                    icon = iconDefault;
                }
                funcexp = funcinfo.containsKey("funcexp") ? funcinfo.getString("funcexp") : "";
                actflag = funcinfo.containsKey("actflag") ? funcinfo.getString("actflag") : "";
            } else {
                cplFunc = cplFuncService.getFuncByFunccode(funccode);
                if (StringUtils.isEmpty(label)) {
                    label = cplFunc == null ? "" : cplFunc.getFuncname();
                }
                url = cplFunc == null ? "" : cplFunc.getHref();
                //如果没传参数ICON则取菜单中的ICON
                if (StringUtils.isEmpty(iconDefault)) {
                    icon = cplFunc == null ? "" : cplFunc.getFuncsign();
                } else {
                    icon = iconDefault;
                }
                funcexp = cplFunc == null ? "" : cplFunc.getFuncexp();
                actflag = cplFunc == null ? "" : cplFunc.getActflag();
            }
            //功能状态不为正常时(包括菜单已被删除情况)或者隐藏表达式不为true时不添加
            if (!(dictVal_ZC).equals(actflag) || !("true").equals(funcexp)) {
                logger.info("配置文件" + moduleName_pzwj + "属性中功能编码为：" + funccode + "的功能状态已修改为停用、隐藏或者删除，请及时修改");
                continue;
            }
            menuJson.put("id", id);
            menuJson.put("label", label);
            menuJson.put("url", url);
            menuJson.put("icon", icon);
            menuJson.put("funccode", funccode);
            menu.add(menuJson);
        }
        logger.info("[-]结束执行initMenuArray方法");
        return menu;
    }

    /**
     * 获取"您可能要办理"模块方法（单位和个人公用）
     *
     * @param request        请求
     * @param systemtypeMask 系统标识MASK值
     * @return JSONObject
     */
    public JSONObject hqNknybl(HttpServletRequest request, String systemtypeMask) {
        logger.info("[+]开始执行hqNknybl方法，systemtypeMask：" + systemtypeMask);
        String poolkey = request.getParameter("poolkey");
        //要返回的json  格式{'returnCode' : 0, 'message' : '', 'data' : {}}
        JSONObject nknyblJson = new JSONObject();
        //json中的data数据
        JSONObject data = new JSONObject();
        //data中的menu数据 格式	{"id":"","label":"","icon":"","url":"","children":[]}
        JSONArray menu = new JSONArray();
        //单条menu数据
        JSONObject menuJson;
        if (StringUtils.isEmpty(poolkey)) {
            throw new TransSingleException(CommonErrorCode.ERROR_NOT_NULL, "poolkey不能为空");
        }
        MainContext mainContext = new MainContext(request);
        DataPool pool = PoolUtil.getPool(poolkey);
        mainContext.setDataPool(pool);
        PoolUtil.savePool(poolkey, pool);
        paramConfigImp.loadMaskData(new String[]{"ish.gg.func.systemtype", "ish.gg.func.status", "ish.gg.other.sf"});
        //个人子系统字典值
        String dictVal_systemtype = paramConfigImp.getValByMask("ish.gg.func.systemtype", systemtypeMask);


        //调用获取个人信息接口，封装accountStatus值
        String accountStatus = "";
        XmlResObj xmlResObj;
        if ("WTGR".equals(systemtypeMask)) {
            logger.info("[+]调用接口获取个人信息开始");
            xmlResObj = EssFactory.sendExternal("BSP_DP_GETGRXX_01", mainContext, false);
            XmlResHead head = xmlResObj.getHead();
            if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
                throw new TransSingleException(head.getParticular_info());
            }
            Map<String, Object> body = xmlResObj.getBody();
            String dkzt = body.get("dkzt") == null || StringUtils.isEmpty(body.get("dkzt").toString()) ? "0" : body.get("dkzt").toString();
            String sfyhkzdk = ("2").equals(dkzt) ? "1" : "0";
            String sfysqzdk = ("1").equals(dkzt) ? "1" : "0";
            String sfblgwtkh = body.get("sfblgwtkh") == null || StringUtils.isEmpty(body.get("sfblgwtkh").toString()) ? "0" : body.get("sfblgwtkh").toString();
            String sfmzltxtq = body.get("sfmzltxtq") == null || StringUtils.isEmpty(body.get("sfmzltxtq").toString()) ? "0" : body.get("sfmzltxtq").toString();
            accountStatus = StringUtils.rightPad(sfyhkzdk + sfysqzdk + sfblgwtkh + sfmzltxtq, 20, "0");
            logger.info("[+]调用接口获取个人信息结束,accountstatus=" + accountStatus);
        } else {
            //调用获取单位信息接口，封装accountStatus值
            logger.info("[+]调用接口获取单位信息开始");
            xmlResObj = EssFactory.sendExternal("BSP_DP_GETDWXX_01", mainContext, false);
            XmlResHead head = xmlResObj.getHead();
            if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
                throw new TransSingleException(head.getParticular_info());
            }
            Map<String, Object> body = xmlResObj.getBody();
            accountStatus = StringUtils.rightPad("", 20, "0");
            //TODO
            logger.info("[-]调用接口获取单位信息结束");
        }

        JSONObject funcInfosJson = funcRecommendService.calculateFuncRecommend(pool.getString("_OPERID"), dictVal_systemtype, accountStatus);
        //获取缓存所有中功能信息
        JSONObject funcinfos;
        YDMemcachedManager manager = YDMemcachedManager.newInstance();
        if (manager.get("funcInfos") != null) {
            funcinfos = JSONObject.parseObject(manager.get("funcInfos").toString());
        } else {
            funcinfos = initAllFuncToMemcached.initAllFuncToMemcached();
        }
        //缓存中单个菜单信息
        JSONObject funcinfo;
        //有数据说明是非首次登录
        //menu的id
        String id;
        //menu的label
        String label;
        //menu 的url
        String url;
        //menu 的 icon
        String icon;
        //menu 的funccode
        String funccode;
        //隐藏标识
        String funcexp;
        //停用标识
        String actflag;
        //拼menu的id
        int count = 1;
        Set<String> keySet = funcInfosJson.keySet();
        for (String key : keySet) {
            menuJson = new JSONObject();
            id = BTNPRE + "nknybl_" + (count++);
            funccode = key;
            if (funcinfos != null && funcinfos.containsKey(key)) {//缓存中有则从缓存中取
                funcinfo = funcinfos.getJSONObject(key);
                label = funcinfo.containsKey("funcname") ? funcinfo.getString("funcname") : "";
                url = funcinfo.containsKey("href") ? funcinfo.getString("href") : "";
                icon = funcinfo.containsKey("funcsign") ? funcinfo.getString("funcsign") : "";
                funcexp = funcinfo.containsKey("funcexp") ? funcinfo.getString("funcexp") : "";
                actflag = funcinfo.containsKey("actflag") ? funcinfo.getString("actflag") : "";
            } else {
                CPLFunc cplFunc = cplFuncService.getFuncByFunccode(key);
                label = cplFunc == null ? "" : cplFunc.getFuncname();
                url = cplFunc == null ? "" : cplFunc.getHref();
                icon = cplFunc == null ? "" : cplFunc.getFuncsign();
                funcexp = cplFunc == null ? "" : cplFunc.getFuncexp();
                actflag = cplFunc == null ? "" : cplFunc.getActflag();
            }
            menuJson.put("id", id);
            menuJson.put("label", label);
            menuJson.put("url", url);
            menuJson.put("icon", icon);
            menuJson.put("funccode", funccode);
            menuJson.put("children", new JSONArray());
            menu.add(menuJson);
        }

        data.put("menu", menu);
        nknyblJson.put("returnCode", 0);
        nknyblJson.put("message", "获取您可能要办理模块数据成功");
        nknyblJson.put("data", data);
        logger.info("[-]结束执行hqNknybl方法");
        return nknyblJson;
    }

    /**
     * 获取"我的申报"模块方法（单位和个人公用）
     *
     * @param request         请求
     * @param moduleName_Chi  模块中文名
     * @param moduleName_pzwj 模块对应的配置文件
     * @return JSONObject
     */
    public JSONObject hqWdsb(HttpServletRequest request, String moduleName_Chi, String moduleName_pzwj) {
        logger.info("[+]开始执行hqWdsb方法，moduleName_Chi：" + moduleName_Chi + ",moduleName_pzwj:" + moduleName_pzwj);
        //要返回的json  格式{'returnCode' : 0, 'message' : '', 'data' : {}}
        JSONObject funcInfosJson = new JSONObject();
        //json中的data数据
        JSONObject data = new JSONObject();
        //data中的ywsj数据
        JSONObject ywsj = new JSONObject();
        //data中的menu数据 格式	{"id":"","label":"","icon":"","url":"","children":[]}
        JSONArray menu;
        //缓存中的菜单信息
        JSONObject funcinfos;
        //模块名字 打日志使用
        String poolkey = request.getParameter("poolkey");
        if (StringUtils.isEmpty(poolkey)) {
            throw new TransSingleException(CommonErrorCode.ERROR_NOT_NULL, "poolkey不能为空");
        }
        MainContext mainContext = new MainContext(request);
        DataPool pool = PoolUtil.getPool(poolkey);
        pool.put("sbrzh", pool.getString("_OPERID"));
        pool.put("yhbs", IshExpression.getRealUserExtInfo("khbs"));
        mainContext.setDataPool(pool);
        PoolUtil.savePool(poolkey, pool);
        paramConfigImp.loadMaskData(new String[]{"ish.gg.other.sf", "ish.gg.func.systemtype", "ish.gg.wf.sbzt"});
        YDMemcachedManager manager = YDMemcachedManager.newInstance();
        if (manager.get("funcInfos") != null) {
            funcinfos = JSONObject.parseObject(manager.get("funcInfos").toString());
        } else {
            funcinfos = initAllFuncToMemcached.initAllFuncToMemcached();
        }
        //-----  封装页面展示字段区域 start -----
        XmlResObj data_sbcx = EssFactory.sendExternal("DECLARE_02", mainContext, false);
        XmlResHead head = data_sbcx.getHead();
        if (head != null && !XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
            String errMsg = "code=[" + head.getParticular_code() + "]，info=[" + head.getParticular_info() + "]。";
            logger.info("[-]调用查询申报信息接口DECLARE_02失败：" + errMsg);
            throw new TransSingleException(head.getParticular_code(),
                    CommonErrorCode.ERROR_FSP + head.getParticular_info());
        }
        BaseBean[] beans;
        if ("1".equals(ReadProperty.getString("isybmap_sbyw"))) {
            beans = data_sbcx.getBeans();
        } else {
            beans = data_sbcx.getPage().getBeans();
        }
        //业务数据中text
        String text;
        //业务数据中申报号
        String sbh;
        //配置文件中我的预约模块菜单配置属性
        BaseBean bean = beans.length >= 1 ? beans[0] : null;
        if (bean == null) {
            text = "";
            sbh = "";
        } else {
            //申报状态
            String sbzt = bean.get("sbzt") == null ? "" : bean.get("sbzt").toString();
            String sbzt_val = paramConfigImp.getVal("ish.gg.wf.sbzt." + sbzt);
            SimpleDateFormat sdf = new SimpleDateFormat(IshConstants.GG_DATETIME_FORMAT);
            SimpleDateFormat sdf_date = new SimpleDateFormat("yyyy年MM月dd日 HH:mm分");
            //申报日期
            Date dateTemp;
            //申报日期
            String cjsj = bean.get("cjsj") == null ? "" : bean.get("cjsj").toString();
            try {
                dateTemp = sdf.parse(cjsj);
            } catch (ParseException e) {
                e.printStackTrace();
                logger.error("日期格式转换出错" + e);
                throw new TransSingleException(CommonErrorCode.ERROR_LXZHCW, "日期格式转换出错");
            }
            text = "您在" + sdf_date.format(dateTemp) + "申报了 " + bean.get("wtlcmc") + "，申报状态现为" + sbzt_val
                    + "，如有问题请及时联系我们。";
            sbh = bean.get("sbh") == null ? "" : bean.get("sbh").toString();

        }
        ywsj.put("text", text);
        ywsj.put("sbh", sbh);
        //-----  封装页面展示字段区域 end -----

        //----- 封装页面按钮区域（不需修改代码，可修改配置文件） start-----
        menu = initMenuArray(funcinfos, moduleName_pzwj, BTNPRE + "sbxq", "");
        //----- 封装页面按钮区域（不需修改代码，可修改配置文件） end-----
        data.put("ywsj", ywsj);
        data.put("menu", menu);
        funcInfosJson.put("data", data);
        funcInfosJson.put("returnCode", 0);
        funcInfosJson.put("message", "获取" + moduleName_Chi + "数据成功");
        logger.info("[-]结束执行hqWdsb方法");
        return funcInfosJson;
    }

    /**
     * 获取最近操作记录模块接口（个人和单位用户公用）
     *
     * @param request 请求request
     * @return JSONObject
     */
    public JSONObject hqZjczjl(HttpServletRequest request, String moduleName_Chi, String moduleName_pzwj, String systemtypeMask) {
        logger.info("[+]开始执行hqZjczjl方法，moduleName_Chi：" + moduleName_Chi + ",moduleName_pzwj:" + moduleName_pzwj + ",systemtypeMask:" + systemtypeMask);
        //要返回的json  格式{'returnCode' : 0, 'message' : '', 'data' : {}}
        JSONObject funcInfosJson = new JSONObject();
        //json中的data数据
        JSONObject data = new JSONObject();
        //data中的ywsj数据
        JSONArray ywsj = new JSONArray();
        //单条业务数据
        JSONObject ywsjJson;
        //data中的menu数据 格式	{"id":"","label":"","icon":"","url":"","children":[]}
        JSONArray menu;
        //缓存中的菜单信息
        JSONObject funcinfos;
        String poolkey = request.getParameter("poolkey");
        if (StringUtils.isEmpty(poolkey)) {
            throw new TransSingleException(CommonErrorCode.ERROR_NOT_NULL, "poolkey不能为空");
        }
        MainContext mainContext = new MainContext(request);
        DataPool pool = PoolUtil.getPool(poolkey);
        mainContext.setDataPool(pool);
        PoolUtil.savePool(poolkey, pool);
        paramConfigImp.loadMaskData(new String[]{"ish.gg.func.systemtype", "ish.gg.other.sf"});
        String dictVal_systemtype = paramConfigImp.getValByMask("ish.gg.func.systemtype", systemtypeMask);
        String operId = pool.getString("_OPERID");
        FuncLog funcLog = new FuncLog();
        funcLog.setOperid(operId);
        funcLog.setSystemtype(dictVal_systemtype);
        List<FuncLog> funcLogList = funcLogService.selectByPrimaryKey(funcLog);
       YDMemcachedManager manager = YDMemcachedManager.newInstance();
        if (manager.get("funcInfos") != null) {
            funcinfos = JSONObject.parseObject(manager.get("funcInfos").toString());
        } else {
            funcinfos = initAllFuncToMemcached.initAllFuncToMemcached();
        }
        //日期
        String date;
        //时间
        String time;
        //内容
        String text;
        //日期
        Date dateTemp;

        SimpleDateFormat sdf = new SimpleDateFormat(IshConstants.GG_DATETIME_FORMAT);
        SimpleDateFormat sdf_date = new SimpleDateFormat(IshConstants.GG_DATE_FORMAT_CHN);
        SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");
        for (int i = 0; i < funcLogList.size(); i++) {
            //只封装6条
            if (i > 5) {
                break;
            }
            funcLog = funcLogList.get(i);
            try {
                dateTemp = sdf.parse(funcLog.getCjsj());
            } catch (ParseException e) {
                e.printStackTrace();
                logger.error("日期格式转换出错" + e);
                throw new TransSingleException(CommonErrorCode.ERROR_LXZHCW, "日期格式转换出错");
            }
            date = sdf_date.format(dateTemp);
            time = sdf_time.format(dateTemp);
            text = funcLog.getContents();
            ywsjJson = new JSONObject();
            ywsjJson.put("date", date);
            ywsjJson.put("time", time);
            ywsjJson.put("text", text);
            ywsj.add(ywsjJson);
        }
        String btnIdPre = BTNPRE + "zjczjl";
        menu = initMenuArray(funcinfos, moduleName_pzwj, btnIdPre, "");
        data.put("ywsj", ywsj);
        data.put("menu", menu);
        funcInfosJson.put("data", data);
        funcInfosJson.put("returnCode", 0);
        funcInfosJson.put("message", "获取" + moduleName_Chi + "数据成功");
        logger.info("[-]结束执行hqZjczjl方法");
        return funcInfosJson;
    }

}
