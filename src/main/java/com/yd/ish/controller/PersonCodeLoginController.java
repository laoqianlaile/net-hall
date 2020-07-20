package com.yd.ish.controller;

import com.alibaba.fastjson.JSONObject;
import com.yd.ish.common.util.QrcodeUtil;
import com.yd.ish.common.util.TuoMinUtil;
import com.yd.ish.dto.LoginMap;
import com.yd.ish.dto.QrCodeLoginInfoDTO;
import com.yd.ish.dto.QrCodeLoginStatusDTO;
import com.yd.ish.expression.BaseResponse;
import com.yd.ish.service.impl.tuomin.TuoMinServiceImpl;
import com.yd.ish.util.DESUtil;
import com.yd.svrplatform.comm_mdl.key_generator.SimpleKeyGenerator;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.util.JsonUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 名称：PersonCodeLoginController.java
 * <p>
 * 功能：个人客户扫码登录 <br>
 *
 * @author 张洪超
 * @version 0.1 2017年9月20日 张洪超创建
 *          0.2 2020年02月07日 柏慧敏 修改 根据新设计的扫码登录进行修改
 */
@Controller
public class PersonCodeLoginController {

    private static final Logger logger = LoggerFactory.getLogger(PersonCodeLoginController.class);

    /**
     * 秘钥
     */
    private static String KEY = "YDISH";

    @Autowired
    SimpleKeyGenerator simpleKeyGenerator;
    @Autowired
    ParamConfigImp paramConfigImp;

    public static String byte2Base64StringFun(byte[] b) {
        return Base64.encodeBase64String(b);
    }

    /**
     * 生成二维码
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/login/getQrCode")
    @ResponseBody
    public Map getQrCode(HttpServletRequest request) {
        logger.info("[+]【二维码扫码登录】生成二维码开始");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Map<String, String> dataMap = new HashMap<String, String>();
        try {
            // 获取跳转地址
            String url = paramConfigImp.getVal("ish.gg.other.qrCodeLoginUrl");
            // loginKey明文
			String loginKey = simpleKeyGenerator.generator();
            logger.info("【二维码扫码登录】明文loginKey = " + loginKey);
            // 对loginKey进行加密
            byte[] byteKey = DESUtil.encryption("{\"loginKey\":\"" + loginKey + "\"}", KEY);
            byte[] encKey = DESUtil.bcd_to_asc(byteKey);
            // loginKey密文
            String loginKey_mw = new String(encKey, "utf-8");
            logger.info("【二维码扫码登录】密文loginKey = " + loginKey_mw);
            // 生成二维码
            QrcodeUtil.encodeQrcode(url + "?qry=wsyytdl&params=" + loginKey_mw, 300, 300, byteArrayOutputStream);
            logger.info("【二维码扫码登录】生成二维码url=" + url + "?qry=wsyytdl&params=" + loginKey_mw);
            byte[] qrbytes = byteArrayOutputStream.toByteArray();
            String qrbase64 = byte2Base64StringFun(qrbytes);
            dataMap.put("qrcode", qrbase64);
            dataMap.put("loginKey", loginKey);
            dataMap.put("returnCode", "0");
        } catch (Exception e) {
            logger.error("生成二维码失败，失败原因：" + e.getMessage());
            e.printStackTrace();
            dataMap.put("returnCode", "1");
            dataMap.put("message", "生成二维码失败，请刷新重试！");
        } finally {
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        logger.info("[-]【二维码扫码登录】生成二维码结束");
        return dataMap;
    }

    /**
     * 获取扫描用户信息
     *
     * @param loginKey
     * @param request
     * @return
     */
    @RequestMapping(value = "/login/queryQrCodeLoginInfo")
    @ResponseBody
    public Map queryQrCodeLoginInfo(String loginKey, HttpServletRequest request) {
        logger.info("[+]【二维码扫码登录】获取扫描用户信息开始，loginKey=" + loginKey);
        Map<String, String> dataMap = new HashMap<String, String>();
        QrCodeLoginInfoDTO qrCodeLoginInfoDTO = LoginMap.getLoginInfoMap().get(loginKey + "_Info");
        // 若用户信息不为空
        if (qrCodeLoginInfoDTO != null) {
            // 身份证号 模糊处理
            String sfzh = qrCodeLoginInfoDTO.getSfzh();
            // 姓名模糊处理
            String xingming = qrCodeLoginInfoDTO.getXingming();
            // 将用户信息存入静态数据池登录用
            LoginMap.getLoginInfoMap().put(qrCodeLoginInfoDTO.getLoginKey(), qrCodeLoginInfoDTO);
            dataMap.put("xingming", TuoMinUtil.getTMValue(xingming,TuoMinServiceImpl.CHINESE_NAME));
            dataMap.put("sfzh", TuoMinUtil.getTMValue(sfzh,TuoMinServiceImpl.ID_CARD));
            dataMap.put("returnCode", "0");
            dataMap.put("loginKey", qrCodeLoginInfoDTO.getLoginKey());
            dataMap.put("fhbz", qrCodeLoginInfoDTO.getReturnCode());
            dataMap.put("message", qrCodeLoginInfoDTO.getMessage());
            logger.info("[-]【二维码扫码登录】获取扫描用户信息结束，loginKey=" + loginKey);
            // 将用户信息移除LoginMap
            LoginMap.getLoginInfoMap().remove(loginKey + "_Info");
        } else {
            dataMap.put("returnCode", "1");
            dataMap.put("message", "暂未获取到用户信息");
            logger.info("【二维码扫码登录】暂未获取到用户信息");
        }
        return dataMap;
    }

    /**
     * 获取扫描后登录结果
     *
     * @param loginKey
     * @param request
     * @return
     */
    @RequestMapping(value = "/login/queryQrCodeLoginStatus")
    @ResponseBody
    public Map queryQrCodeLoginStatus(String loginKey, HttpServletRequest request) {
        logger.info("[+]【二维码扫码登录】获取扫描后登录结果开始，loginKey=" + loginKey);
        Map<String, String> dataMap = new HashMap<String, String>();
        QrCodeLoginStatusDTO qrCodeLoginStatusDTO = LoginMap.getLoginStatusMap().get(loginKey + "_Status");
        // 若登录信息不为空
        if (qrCodeLoginStatusDTO != null) {
            dataMap.put("returnCode", "0");
            dataMap.put("loginKey", qrCodeLoginStatusDTO.getLoginKey());
            dataMap.put("loginStatus", qrCodeLoginStatusDTO.getLoginStatus());
            // 将登录状态移除LoginMap
            LoginMap.getLoginStatusMap().remove(loginKey + "_Status");
            logger.info("[-]【二维码扫码登录】获取扫描后登录结果结束，loginKey=" + loginKey + ",登录状态：" + qrCodeLoginStatusDTO.getLoginStatus());
        } else {
            // 若登录结果为失败
            dataMap.put("returnCode", "1");
            dataMap.put("message", "暂未获取到登录结果");
            logger.info("【二维码扫码登录】暂未获取到登录结果");
        }
        return dataMap;
    }

    /**
     * 删除用户信息
     *
     * @param loginKey
     * @param request
     * @return
     */
    @RequestMapping(value = "/login/deleteLoginInfoMap")
    @ResponseBody
    public String deleteLoginInfoMap(String loginKey, HttpServletRequest request) {
        logger.info("[+]【二维码扫码登录】清除用户信息开始，loginKey=" + loginKey);
        Map<String, String> dataMap = new HashMap<String, String>();
        // 将用户信息移除LoginMap
        LoginMap.getLoginInfoMap().remove(loginKey);
        // 成功
        dataMap.put("returnCode", "0");
        dataMap.put("message", "清除用户信息成功");
        logger.info("[-]【二维码扫码登录】清除用户信息成功，loginKey=" + loginKey);
        return JSONObject.toJSONString(dataMap);
    }

    /**
     * 接口-取验证的的扫描信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/login/person/qrCodeLoginInfo", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse qrCodeLoginInfo(HttpServletRequest request) {
        logger.info("[+]【二维码扫码登录】返回用户信息接口调用开始");
        BaseResponse response = new BaseResponse();
        // 请求中获取返回结果密文
		String params = request.getParameter("params");
		logger.info("【二维码扫码登录】返回用户信息密文 ：params = " + params);
		// 返回结果明文
		String loginKey_mw;
		// 对密文进行解密
		try {
			byte[] midbytes = params.getBytes("utf-8");
			int i = midbytes.length;
			byte[] d = DESUtil.asc_to_bcd(midbytes, i);
			loginKey_mw = DESUtil.decryption(d, KEY);
            logger.info("【二维码扫码登录】返回用户信息明文：" + loginKey_mw);
		} catch (UnsupportedEncodingException e) {
			logger.error("【二维码扫码登录】获取用户信息失败,失败原因："+e.getMessage());
			e.printStackTrace();
			response.setMessage("获取用户信息失败");
			return response;
		}
		HashMap<String, Object> map = JsonUtil.getJsonObject(loginKey_mw, HashMap.class);
        // 对返回结果中信息进行判断
		if (map.get("loginKey") == null) {
			logger.error("【二维码扫码登录】loginKey不允许为空");
			response.setMessage("loginKey不允许为空");
			return response;
		}
		if (map.get("sfzh") == null) {
			logger.error("【二维码扫码登录】sfzh不允许为空");
			response.setMessage("sfzh不允许为空");
			return response;
		}
		if (map.get("xingming") == null) {
			logger.error("【二维码扫码登录】xingming不允许为空");
			response.setMessage("xingming不允许为空");
			return response;
		}
        // 记录返回用户信息
        logger.info("【二维码扫码登录】记录返回的用户信息");
        QrCodeLoginInfoDTO qrCodeLoginInfoDTO = new QrCodeLoginInfoDTO();
		qrCodeLoginInfoDTO.setLoginKey(map.get("loginKey").toString());
		qrCodeLoginInfoDTO.setSfzh(map.get("sfzh").toString());
		qrCodeLoginInfoDTO.setXingming(map.get("xingming").toString());
		qrCodeLoginInfoDTO.setReturnCode(map.get("fhbz").toString());
		qrCodeLoginInfoDTO.setMessage(map.get("fhxx").toString());
        // 将返回的用户信息放入LoginMap(区分开返回用户信息_Info)
        LoginMap.getLoginInfoMap().put(qrCodeLoginInfoDTO.getLoginKey() + "_Info", qrCodeLoginInfoDTO);
        response.setReturnCode("0");
        response.setMessage("成功");
        logger.info("[-]【二维码扫码登录】返回用户信息接口调用结束");
        return response;
    }

    /**
     * 接口-取登录的结果存入LoginMap
     *
     * @param qrCodeLoginStatusDTO
     * @param request
     * @return
     */
    @RequestMapping(value = "/login/person/qrCodeLoginStatus", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse qrCodeLoginStatus(QrCodeLoginStatusDTO qrCodeLoginStatusDTO, HttpServletRequest request) {
        logger.info("[+]【二维码扫码登录】返回授权结果接口调用开始");
        BaseResponse response = new BaseResponse();
        // 校验请求中的参数是否为空
        if (StringUtils.isBlank(qrCodeLoginStatusDTO.getLoginKey())) {
            logger.info("【二维码扫码登录】loginKey不允许为空");
            response.setMessage("loginKey不允许为空");
            return response;
        }
        if (StringUtils.isBlank(qrCodeLoginStatusDTO.getLoginStatus())) {
            logger.info("【二维码扫码登录】loginStatus不允许为空");
            response.setMessage("loginStatus不允许为空");
            return response;
        }
        // 将返回的登录结果放入LoginMap(区分开返回用回信息_Status)
        logger.info("【二维码扫码登录】记录授权结果，key=" + qrCodeLoginStatusDTO.getLoginKey() + "_Status");
        LoginMap.getLoginStatusMap().put(qrCodeLoginStatusDTO.getLoginKey() + "_Status", qrCodeLoginStatusDTO);

        logger.info("[-]【二维码扫码登录】返回授权结果接口调用结束，登录结果 ：" + qrCodeLoginStatusDTO);
        response.setReturnCode("0");
        response.setMessage("成功");

        return response;
    }
}
