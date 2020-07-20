package com.yd.ish.service.common;

import cn.org.bjca.client.exceptions.ApplicationNotFoundException;
import cn.org.bjca.client.exceptions.InitException;
import cn.org.bjca.client.exceptions.SVSConnectException;
import cn.org.bjca.client.security.SecurityEngineDeal;
import com.yd.basic.service.ILoginCmdService;
import com.yd.basic.service.ILoginService;
import com.yd.biz.engine.TransEngine;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.HttpRequestUtil;
import com.yd.ish.common.util.TuoMinUtil;
import com.yd.ish.service.HomeData;
import com.yd.org.util.CommonErrorCode;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.context.UserContext;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.exception.PlatRuntimeException;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.ReadProperty;
import com.yd.ydpx.util.YdpxUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.applet.Main;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service("orgcert")
public class OrgCertLoginServletImpl implements ILoginService, ILoginCmdService {
    private static final Logger logger = LoggerFactory.getLogger(OrgCertLoginServletImpl.class);
    @Autowired
    ParamConfigImp paramConfigImp;
    @Override
    public String cmd(MainContext mainContext, Map<String, String> map) throws PlatRuntimeException {
        /* 单位认证经办人登录核验 开始 */
        DataPool datapool = new DataPool();
        datapool.put("dlyhm", map.get("dlyhm"));
        datapool.put("dlmm", map.get("dlmm"));
        mainContext.setDataPool(datapool);
        TransEngine.getInstance().execute_NoTran("UPLGG1001_02", mainContext);
        /* 单位认证经办人登录核验 结束 */
        datapool.put("ywmc", "单位经办人用户名登录");
        mainContext.setDataPool(datapool);
        TransEngine.getInstance().execute_NoTran("GGTrans_0005", mainContext);
        return datapool.getString("dtmmkey");
    }

    @Override
    public String getOperIDKey() {
        return "jbrdjh";
    }

    @Override
    public UserContext check(Map<String, String> loginmodel) throws PlatRuntimeException {
//        MainContext context = MainContext.currentMainContext();
//        String msg = "客户端证书验证失败";
//        int retValue = 0;
//        String uniqueId = "";
//        try{
//            InputStream in = OrgCertLoginServletImpl.class.getClassLoader().getResourceAsStream("webappName.properties");
//            Properties prop = new Properties();
//            prop.load(in);
//            SecurityEngineDeal.setProfilePath("C:\\Users\\Yondervision\\BJCAROOT");
//            SecurityEngineDeal sed = SecurityEngineDeal.getInstance(prop.getProperty("webappName"));
//            UserContext user = new UserContext();
//            String clientCert = loginmodel.get("UserCert");
//            String UserSignedData = loginmodel.get("UserSignedData");
//            logger.info("clientCert:" + clientCert);
//            logger.info("UserSignedData:" + UserSignedData);
//
//            String ranStr = (String)context.getAttribute("ToSign");
//            System.out.println("Random=============================>" + ranStr);
//            String ContainerName = loginmodel.get("ContainerName");
//
//            System.out.println("ranStr=" + ranStr);
//            System.out.println("clientCert=" + clientCert);
//            System.out.println("UserSignedData=" + UserSignedData);
//            System.out.println("ContainerName=" + ContainerName);
//            retValue = sed.validateCert(clientCert);
//
//            if(retValue != 1){
//                if(retValue == -1){
//                    msg = "登录证书的根不被信任";
//                }else if(retValue == -2){
//                    msg = "登录证书超过有效期";
//                }else if(retValue == -3){
//                    msg = "登录证书为作废证书";
//                }else if(retValue == -4){
//                    msg = "登录证书被临时冻结";
//                }else if(retValue == -5){
//                    msg = "登录证书未生效";
//                }
//                throw new TransSingleException(msg);
//            }
//            uniqueId = sed.getCertInfoByOid(clientCert,"1.2.156.112562.2.1.1.1");
//            if(uniqueId == null || uniqueId != null && uniqueId.equals("")){
//                uniqueId = sed.getCertInfoByOid(clientCert,"2.16.840.1.113732.2");
//            }
//            String username = sed.getCertInfo(uniqueId,15);
//            System.out.println("username=============================>" + username);
//        }catch(IOException e){
//            logger.error("读取webappName.properties文件异常" + e);
//        } catch (ApplicationNotFoundException e) {
//            logger.error("应用没有找到" + e);
//        } catch (SVSConnectException e) {
//            logger.error("服务器连接异常" + e);
//        } catch (InitException e) {
//            logger.error("初始化异常" + e);
//        } catch(Exception e){
//            throw new TransSingleException("客户端证书验证失败" + retValue);
//        }
//        System.out.println("uniqueId == " + uniqueId);
//
//        // 1. 增加前台参数的特殊字符校验
//        if (!HttpRequestUtil.checkRequest(loginmodel)) {
//            throw new TransSingleException("对不起，您输入的用户名不合法。");
//        }
//
////        String dlyhm = loginmodel.get("dlyhm");
//        String dlyhm = uniqueId;
//        String dlmm = loginmodel.get("dlmm");
//
//        MainContext mainContext = MainContext.currentMainContext();
//        DataPool datapool = new DataPool();
//
//        /*校验随机验证码	 开始*/
//        String yzm = loginmodel.get("yzm");
//        String yzmkey = loginmodel.get("yzmkey");
//        datapool.put("yzm", yzm);
//        datapool.put("yzmkey", yzmkey);
//        mainContext.setDataPool(datapool);
//        //随机验证码是否校验标识
//        String checkYzm = loginmodel.get("checkYzm");
//        //checkYzm为1时校验验证码
//        if("1".equals(checkYzm)){
//            TransEngine.getInstance().execute("GGTrans_0001",mainContext);
//            datapool = mainContext.getDataPool();
//        }else{
//            datapool = mainContext.getDataPool();
//            datapool.put("jg","0");
//        }
//        String jg = String.valueOf(datapool.get("jg"));//获取验证结果
//        //获取验证结果
//        if(!StringUtils.equals("0",jg)) {
//            throw new TransSingleException(CommonErrorCode.ERROR_INPUT,"验证码");
//        }
//        /*校验随机验证码	结束*/
//
//        /* 3. 单位认证经办人登录核验 开始 */
//        datapool.put("loginId", dlyhm);
//        if(StringUtils.isNotBlank(dlmm)){
////			datapool.put("password", EncryptionUtil.MD5Encode(dlmm));
//            datapool.put("password", dlmm);
//        }else{
//            datapool.put("password", "");
//        }
//        mainContext.setDataPool(datapool);
//        Map<String, Object> userinfo = TransEngine.getInstance().execute_NoTran("TranDLYZ401", mainContext);
//        /* 单位认证经办人登录核验 结束 */
//
//        /* 4. 获得单位经办人角色信息 开始 */
//        String rolecodes = userinfo.get("rolecodes").toString();
//        List<String> roleList = new ArrayList<String>();
//        for (String str : rolecodes.split(";")) {
//            roleList.add(str);
//        }
//        logger.debug("roleList===" + roleList);
//        /* 获得单位经办人角色信息 结束 */
//
//        /* 5. 组织 UserContext 等 开始 */
//        datapool = mainContext.getDataPool();
//
//        UserContext user = new UserContext();
//        user.setLoginTye("auth");
//        //将单位账号赋值给operid
//        user.setOperId(userinfo.get("dwdjh").toString());
//        user.setOperName(userinfo.get("jbrxm").toString());
//        user.setOrgId(userinfo.get("dwdjh").toString());
//        user.setOrgName(userinfo.get("dwmc").toString());
//        user.setRoles(roleList);
//        //客户标识
//        datapool.put("khbs", IshConstants.LOGIN_KHBS_JBR);
//        //登陆方式
//        datapool.put("dlfs", IshConstants.LOGIN_DLFS_DWYHM);
//        //登陆用户名
//        datapool.put("dlyhm", dlyhm);
//        //单位地址
//        datapool.put("dwdz", userinfo.get("dwdz").toString());
//        //单位设立日期
//        datapool.put("dwslrq", userinfo.get("dwslrq").toString());
//        //单位邮编
//        datapool.put("dwyb", userinfo.get("dwyb").toString());
//        //社会诚信代码
//        datapool.put("shcxdm", userinfo.get("shcxdm").toString());
//        user.setAttribute(ILoginService.EXTINFOKEY, datapool);
//        //脱敏功能使用
//        String tuomin_sfqy = ReadProperty.getString("tuomin_sfqy");
//        if("1".equals(tuomin_sfqy)){
//            TuoMinUtil.tMDataPool(datapool);
//        }
//        paramConfigImp.loadMaskData(new String[] { "ish.gg.func.systemtype" });
//
//        //子系统标识
//        String systemtype = paramConfigImp.getValByMask("ish.gg.func.systemtype", IshConstants.SYSTEM_WTDW);
//        user.setAttribute(IshConstants.LOGIN_SYSTEMTYPE,systemtype);
//        // 组装首页的用户信息
//        String homedata = HomeData.getOrgHomeData(datapool, user);
//        user.setAttribute("homedata", homedata);
//        /* 组织 UserContext 等 结束 */
//
//        user.setAttribute("taskurl", "WAITTASK_00.ydpx");
//        user.setAttribute("homeurl", "HOMEPAGE_"+ReadProperty.getString("template")+"_02.ydpx");
//
//        return user;
        if (!HttpRequestUtil.checkRequest(loginmodel)) {
            throw new TransSingleException("对不起，您输入的用户名不合法。");
        } else {
            String loginId = (String) loginmodel.get("dlyhm");
            String dlmm = (String) loginmodel.get("dlmm");
            String dllx = (String) loginmodel.get("logintype");
            MainContext mainContext = MainContext.currentMainContext();
            DataPool datapool = new DataPool();
            datapool.put("pubzh", loginId);
            datapool.put("password", dlmm);
            datapool.put("dllx", dllx);
            mainContext.setDataPool(datapool);
            Map<String, Object> userinfo = TransEngine.getInstance().execute_NoTran("TranDLYZ04", mainContext, new boolean[0]);
            String rolecodes = userinfo.get("rolecodes").toString();
            List<String> roleList = new ArrayList();
            String[] var10 = rolecodes.split(";");
            int var11 = var10.length;

            for (int var12 = 0; var12 < var11; ++var12) {
                String str = var10[var12];
                roleList.add(str);
            }

            logger.debug("roleList===" + roleList);
            datapool = mainContext.getDataPool();
            UserContext user = new UserContext();
            user.setLoginTye("auth");
            user.setOperId(userinfo.get("dwdjh").toString());
//            user.setOperName(userinfo.get("jbrxm").toString());
            user.setOperName(loginId);
            user.setOrgId(userinfo.get("dwdjh").toString());
            user.setOrgName(userinfo.get("dwmc").toString());
//            user.setOrgName(userinfo.get("jbrsjhm").toString());
            user.setRoles(roleList);
            datapool.put("khbs", "1");
            datapool.put("dlfs", "5");
            datapool.put("dwzh", loginId);
            //登陆用户名
            datapool.put("dlyhm", loginId);
            user.setAttribute("sjh", datapool.getString("sjh"));
            user.setAttribute("jbryx", datapool.getString("jbryx"));
            user.setAttribute("loginpool", datapool);
            this.paramConfigImp.loadMaskData(new String[]{"ish.gg.func.systemtype"});
            String systemtype = this.paramConfigImp.getValByMask("ish.gg.func.systemtype", "WTDW");
            user.setAttribute("systemtype", systemtype);
            datapool.put("dwdz", userinfo.get("dwdz"));
            datapool.put("yyzzhm", userinfo.get("yyzzhm"));
            datapool.put("dwclsj", userinfo.get("dwclsj"));
            String homedata = HomeData.getOrgHomeData(datapool, user);
            user.setAttribute("homedata", homedata);
            user.setAttribute("taskurl", "WAITTASK_00.ydpx");
            user.setAttribute("homeurl", "HOMEPAGE_"+ReadProperty.getString("template")+"_02.ydpx");
            return user;
        }
    }
}
