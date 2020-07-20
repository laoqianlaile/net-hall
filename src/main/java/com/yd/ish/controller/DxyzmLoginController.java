package com.yd.ish.controller;

import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.yd.basic.util.AesUtil;
import com.yd.basic.util.RSAUtils;
import com.yd.svrplatform.comm_mdl.cache.YDMemcachedManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yd.biz.engine.TransEngine;
import com.yd.org.util.EncryptionUtil;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.key_generator.SimpleKeyGenerator;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.util.DataPool;

/**
 * 名称：DxyzmLoginController.java
 * <p>
 * 功能：短信验证码登录 <br>
 *
 * @author 柏慧敏
 * @version 0.1 2019年08月05日 柏慧敏创建
 */
@Controller
public class DxyzmLoginController {

    private static final Logger logger = LoggerFactory.getLogger(DxyzmLoginController.class);
    private static final String COM_SY_KEY = "COM_RSA_SY_";

    @Autowired
    SimpleKeyGenerator simpleKeyGenerator;
    @Autowired
    ParamConfigImp paramConfigImp;

    /**
     * 发送短信验证码
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/login/getDxyzm")
    @ResponseBody
    public String getDxyzm(HttpServletRequest request) {
        MainContext mainContext = new MainContext(request);
        Map<String, String> dataMap = new HashMap<String, String>();
        DataPool pool = null;
        if (mainContext.getDataPool() != null) {
            pool = mainContext.getDataPool();
        } else {
            pool = new DataPool();
            mainContext.setDataPool(pool);
        }
        YDMemcachedManager manager = YDMemcachedManager.newInstance();
        //获取当前的sessionId
        String sessionId = request.getSession(false).getId();
        // 登录用户名
        String dlyhm = "";
        // 登录密码
        String dlmm = "";
        // 预留手机号
        String ylsjh = "";
        //登录页面加密后的aes的key
        String aeskey = "";
        String _aeskey = "";
        String jmvalue = "";
        RSAPrivateKey privateKey = null;
        if (request.getParameter("gg") != null && request.getParameter("A01001") != null && !"".equals(request.getParameter("A01001"))) {
            aeskey = request.getParameter("A01001");
            if (manager.get(COM_SY_KEY + sessionId) != null) {
                privateKey = (RSAPrivateKey) manager.get(COM_SY_KEY + sessionId);
            }
            try {
                _aeskey = RSAUtils.decryptByPrivateKey(aeskey, privateKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
            jmvalue = AesUtil.deCode(request.getParameter("gg"), _aeskey);
            org.json.JSONObject jsonObject = new org.json.JSONObject(jmvalue);
            // 从加密字段中获取登录用户名、登录密码、手机号
            dlyhm = jsonObject.getString("a01002");
            dlmm = jsonObject.getString("a01003");
            ylsjh = jsonObject.getString("a01006");
            //将页面输入信息放入总线中
            pool.put("dlyhm", dlyhm);
            pool.put("dlmm", EncryptionUtil.MD5Encode(dlmm));
            pool.put("ylsjh", ylsjh);
        }
        pool.put("flag", "sendyzm");
        //调用接口，发送短信验证码
        HashMap<String, Object> map = TransEngine.getInstance().execute("TranDLYZ501", mainContext);
        if (!XmlResHead.TR_SUCCESS.equals(map.get("returnCode"))) {
            dataMap.put("returnCode", "1");
            dataMap.put("message", map.get("msg") == null ? "调用接口发送验证码失败" : map.get("msg").toString());
            logger.error("调用接口失败，失败原因：" + map.get("msg"));
        } else {
            dataMap.put("returnCode", "0");
        }
        return JSONObject.toJSONString(dataMap);
    }

    /**
     * 获取预留手机号
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/login/getYlsjh")
    @ResponseBody
    public String getYlsjh(HttpServletRequest request) {
        MainContext mainContext = new MainContext(request);
        Map<String, String> dataMap = new HashMap<String, String>();
        DataPool pool = null;
        if (mainContext.getDataPool() != null) {
            pool = mainContext.getDataPool();
        } else {
            pool = new DataPool();
            mainContext.setDataPool(pool);
        }
        YDMemcachedManager manager = YDMemcachedManager.newInstance();
        //获取当前的sessionId
        String sessionId = request.getSession(false).getId();
        // 登录用户名
        String dlyhm = "";
        // 登录密码
        String dlmm = "";
        // 登录类型
        String logintype = "";
        //登录页面加密后的aes的key
        String aeskey = "";
        String _aeskey = "";
        String jmvalue = "";
        RSAPrivateKey privateKey = null;
        if (request.getParameter("gg") != null && request.getParameter("A01001") != null && !"".equals(request.getParameter("A01001"))) {
            aeskey = request.getParameter("A01001");
            if (manager.get(COM_SY_KEY + sessionId) != null) {
                privateKey = (RSAPrivateKey) manager.get(COM_SY_KEY + sessionId);
            }
            try {
                _aeskey = RSAUtils.decryptByPrivateKey(aeskey, privateKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
            jmvalue = AesUtil.deCode(request.getParameter("gg"), _aeskey);
            org.json.JSONObject jsonObject = new org.json.JSONObject(jmvalue);
            // 从加密字段中获取登录用户名和登录密码
            dlyhm = jsonObject.getString("a01002");
            dlmm = jsonObject.getString("a01003");
            logintype = jsonObject.getString("a01004");
            //将页面输入信息放入总线中
            pool.put("dlyhm", dlyhm);
            pool.put("dlmm", EncryptionUtil.MD5Encode(dlmm));
        }
        pool.put("flag", "getsjhm");
        //调用接口，获取预留手机号
        HashMap<String, Object> map = new HashMap<String, Object>();
        // 个人短信验证码登录
        if("persondxyzm".equals(logintype)){
            map = TransEngine.getInstance().execute("TranDLYZ701", mainContext);
        }
        // 单位短信验证码登录
        else if("orgdxyzm".equals(logintype)){
            map = TransEngine.getInstance().execute("TranDLYZ501", mainContext);
        }
        if (!XmlResHead.TR_SUCCESS.equals(map.get("returnCode"))) {
            dataMap.put("returnCode", "1");
            dataMap.put("message", map.get("msg") == null ? "调用接口获取预留手机号失败" : map.get("msg").toString());
            logger.error("调用接口失败，失败原因：" + map.get("msg"));
        } else {
            if (map.get("ylsjh") == null || "".equals(map.get("ylsjh").toString())) {
                dataMap.put("returnCode", "1");
                dataMap.put("message", "未获取到预留手机号");
            } else {
                dataMap.put("returnCode", "0");
                dataMap.put("ylsjh", map.get("ylsjh").toString());
            }
        }
        return JSONObject.toJSONString(dataMap);
    }
    @RequestMapping(value = "/login/getJbrSjhm")
    @ResponseBody
    public String getJbrSjhm(HttpServletRequest request) {
        MainContext mainContext = new MainContext(request);
        Map<String, String> dataMap = new HashMap<String, String>();
        DataPool pool = null;
        if (mainContext.getDataPool() != null) {
            pool = mainContext.getDataPool();
        } else {
            pool = new DataPool();
            mainContext.setDataPool(pool);
        }
        System.out.println(request.getParameterMap());
        YDMemcachedManager manager = YDMemcachedManager.newInstance();
        //获取当前的sessionId
        String sessionId = request.getSession(false).getId();
        // 登录用户名
        String dlyhm = "";
        // 登录密码
        String dlmm = "";
        // 登录类型
        String logintype = "";
        //登录页面加密后的aes的key
        String aeskey = "";
        String _aeskey = "";
        String jmvalue = "";
        RSAPrivateKey privateKey = null;
        if (request.getParameter("gg") != null && request.getParameter("A01001") != null && !"".equals(request.getParameter("A01001"))) {
            aeskey = request.getParameter("A01001");
            if (manager.get(COM_SY_KEY + sessionId) != null) {
                privateKey = (RSAPrivateKey) manager.get(COM_SY_KEY + sessionId);
            }
            try {
                _aeskey = RSAUtils.decryptByPrivateKey(aeskey, privateKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
            jmvalue = AesUtil.deCode(request.getParameter("gg"), _aeskey);
            org.json.JSONObject jsonObject = new org.json.JSONObject(jmvalue);
            // 从加密字段中获取登录用户名和登录密码
            dlyhm = jsonObject.getString("a01002");
            dlmm = jsonObject.getString("a01003");
            logintype = jsonObject.getString("a01004");
            //将页面输入信息放入总线中
            pool.put("dlyhm", dlyhm);
            pool.put("dlmm", EncryptionUtil.MD5Encode(dlmm));
        }
        pool.put("flag", "getsjhm");
        pool.put("loginId",dlyhm);
        pool.put("password",dlmm);
        //调用接口，获取预留手机号
        HashMap<String, Object> map = new HashMap<String, Object>();
        // 个人短信验证码登录
        Map<String,Object> dwInfo = TransEngine.getInstance().execute("TranDLYZ401",mainContext);
        String dwzh = (String)dwInfo.get("dwzh");
        System.out.println("单位账号：" + dwzh);
        pool.put("dwzh1",dwzh);
        map = TransEngine.getInstance().execute("TranDWJBXXCX4", mainContext);
        if (map.size() <= 0) {
            dataMap.put("returnCode", "1");
            dataMap.put("message", map.get("msg") == null ? "调用接口获取预留手机号失败" : map.get("msg").toString());
            logger.error("调用接口失败，失败原因：" + map.get("msg"));
        } else {
            if (map.get("jbrsjhm") == null || "".equals(map.get("jbrsjhm").toString())) {
                dataMap.put("returnCode", "1");
                dataMap.put("message", "未获取到预留的经办人手机号");
            } else {
                dataMap.put("returnCode", "0");
                dataMap.put("jbrsjhm", map.get("jbrsjhm").toString());
                String jbrsjhm = (String)map.get("jbrsjhm");
                pool.put("jbrsjhm",jbrsjhm);
                Map<String,Object> dxMap = TransEngine.getInstance().execute("TranDXYZM2",mainContext);
                dataMap.put("dxyzm",(String)dxMap.get("dxyzm"));
            }
        }
        return JSONObject.toJSONString(dataMap);
    }
}
