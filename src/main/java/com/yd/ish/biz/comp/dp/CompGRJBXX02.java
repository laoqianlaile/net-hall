package com.yd.ish.biz.comp.dp;

import java.util.HashMap;
import java.util.Map;

import com.yd.basic.expression.IshExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.druid.support.json.JSONUtils;
import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;

/**
 * 名称：CompGRJBXX02.java
 * <p>
 * 功能：个人详细信息查询 <br>
 *
 * @author 王赫
 * @version 0.1 2018年6月28日 王赫创建
 */
@Component("CompGRJBXX02")
public class CompGRJBXX02 extends BaseComp {

    private static final Logger logger = LoggerFactory.getLogger(CompGRJBXX02.class);

    @Override
    public int execute() {

        /* 入口参数赋值开始 */
        String grzh = getString("grzh");
        /* 入口参数赋值结束 */
        logger.info("[+]开始调用查询个人详细信息接口BSP_DP_GETGRXX_01...");
        if(grzh == null || "".equals(grzh)){
            super.setValue("grzh",IshExpression.getRealUserExtInfo("grzh"));
        }
        XmlResObj data = super.sendExternal("BSP_DP_GETGRXX_01");
        XmlResHead head = data.getHead();
        if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
            String errMsg = "code=[" + head.getParticular_code() + "]，info=[" + head.getParticular_info() + "]。";
            logger.info("[-]调用查询个人详细信息接口BSP_DP_GETGRXX_01失败" + errMsg);
            throw new TransSingleException(head.getParticular_code(), head.getParticular_info());
        }
        Map<String, Object> body = data.getBody();
        String xingming = body.get("xingming") == null ? "" : body.get("xingming").toString();
        String zjlx = body.get("zjlx") == null ? "" : body.get("zjlx").toString();
        String zjhm = body.get("zjhm") == null ? "" : body.get("zjhm").toString();
        String jcbllx = body.get("jcbllx") == null ? "" : body.get("jcbllx").toString();
        String grjcjs = body.get("grjcjs") == null ? "" : body.get("grjcjs").toString();
        String dwyjce = body.get("dwyjce") == null ? "" : body.get("dwyjce").toString();
        String gryjce = body.get("gryjce") == null ? "" : body.get("gryjce").toString();
        String yjce = body.get("yjce") == null ? "" : body.get("yjce").toString();
        String sfsmrz = body.get("sfsmrz") == null ? "" : body.get("sfsmrz").toString();
        String xingbie = body.get("xingbie") == null ? "" : body.get("xingbie").toString();
        String hj = body.get("hj") == null ? "" : body.get("hj").toString();
        String gddhhm = body.get("gddhhm") == null ? "" : body.get("gddhhm").toString();
        String sjhm = body.get("sjhm") == null ? "" : body.get("sjhm").toString();
        String csny = body.get("csny") == null ? "" : body.get("csny").toString();
        String hyzk = body.get("hyzk") == null ? "" : body.get("hyzk").toString();
        String zhiye = body.get("zhiye") == null ? "" : body.get("zhiye").toString();
        String zhichen = body.get("zhichen") == null ? "" : body.get("zhichen").toString();
        String zhiwu = body.get("zhiwu") == null ? "" : body.get("zhiwu").toString();
        String xueli = body.get("xueli") == null ? "" : body.get("xueli").toString();
        String yzbm = body.get("yzbm") == null ? "" : body.get("yzbm").toString();
        String jtzz = body.get("jtzz") == null ? "" : body.get("jtzz").toString();
        String jtysr = body.get("jtysr") == null ? "" : body.get("jtysr").toString();
        String khyhmc = body.get("khyhmc") == null ? "" : body.get("khyhmc").toString();
        String khyhzh = body.get("khyhzh") == null ? "" : body.get("khyhzh").toString();
        String dwzh = body.get("dwzh") == null ? "" : body.get("dwzh").toString();
        String dwmc = body.get("dwmc") == null ? "" : body.get("dwmc").toString();
        String dwjcbl = body.get("dwjcbl") == null ? "" : body.get("dwjcbl").toString();
        String grjcbl = body.get("grjcbl") == null ? "" : body.get("grjcbl").toString();
        String grzhye = body.get("grzhye") == null ? "" : body.get("grzhye").toString();
        String qshjny = body.get("qshjny") == null ? "" : body.get("qshjny").toString();
        String jzny = body.get("jzny") == null ? "" : body.get("jzny").toString();
        String khrq = body.get("khrq") == null ? "" : body.get("khrq").toString();
        String grzhzt = body.get("grzhzt") == null ? "" : body.get("grzhzt").toString();
        String grdjbz = body.get("grdjbz") == null ? "" : body.get("grdjbz").toString();
        String djje = body.get("djje") == null ? "" : body.get("djje").toString();
        String kyye = body.get("kyye") == null ? "" : body.get("kyye").toString();
        String zhjg = body.get("zhjg") == null ? "" : body.get("zhjg").toString();
        String zjjcrq = body.get("zjjcrq") == null ? "" : body.get("zjjcrq").toString();
        String zjtqrq = body.get("zjtqrq") == null ? "" : body.get("zjtqrq").toString();
        String tqcs = body.get("tqcs") == null ? "" : body.get("tqcs").toString();
        String dkcs = body.get("dkcs") == null ? "" : body.get("dkcs").toString();
        String dwdh = body.get("dwdh") == null ? "" : body.get("dwdh").toString();
        String dwdz = body.get("dwdz") == null ? "" : body.get("dwdz").toString();
        String jcyh = body.get("jcyh") == null ? "" : body.get("jcyh").toString();
        String glyh = body.get("glyh") == null ? "" : body.get("glyh").toString();
        String glyhkh = body.get("glyhkh") == null ? "" : body.get("glyhkh").toString();

        /* 出口参数赋值开始 */
        setOutParam("xingming", xingming);// 姓名
        setOutParam("zjlx", zjlx);// 证件类型
        setOutParam("zjhm", zjhm);// 证件号码
        setOutParam("jcbllx", jcbllx);// 缴存比例类型
        setOutParam("grjcjs", grjcjs);// 个人缴存基数
        setOutParam("dwyjce", dwyjce);// 单位月缴存额
        setOutParam("gryjce", gryjce);// 个人月缴存额
        setOutParam("yjce", yjce);// 月缴存额
        setOutParam("sfsmrz", sfsmrz);// 是否实名认证
        setOutParam("xingbie", xingbie );// 性别
        setOutParam("hj", hj); // 户籍
        setOutParam("gddhhm", gddhhm);// 固定电话号码
        setOutParam("sjhm", sjhm);// 手机号码
        setOutParam("csny", csny);// 出生年月
        setOutParam("hyzk", hyzk);// 婚姻状况
        setOutParam("zhiye", zhiye ); // 职业
        setOutParam("zhichen", zhichen );// 职称
        setOutParam("zhiwu", zhiwu ); // 职务
        setOutParam("xueli", xueli ); // 学历
        setOutParam("yzbm", yzbm);// 邮政编码
        setOutParam("jtzz", jtzz);// 家庭住址
        setOutParam("jtysr", jtysr ); // 家庭月收入
        setOutParam("khyhmc", khyhmc);// 开户银行名称
        setOutParam("khyhzh", khyhzh);// 开户银行账号
        setOutParam("dwzh", dwzh);// 单位账号
        setOutParam("dwmc", dwmc);// 单位名称
        setOutParam("dwjcbl", dwjcbl);// 单位缴存比例
        setOutParam("grjcbl", grjcbl);// 个人缴存比例
        setOutParam("grzhye", grzhye);// 个人账户余额
        setOutParam("qshjny", qshjny);// 起始汇缴年月
        setOutParam("jzny", jzny);// 缴至年月
        setOutParam("khrq", khrq);// 开户日期
        setOutParam("grzhzt", grzhzt);// 个人账户状态
        setOutParam("grdjbz", grdjbz);// 个人冻结标志
        setOutParam("djje", djje);// 冻结金额
        setOutParam("kyye", kyye);// 可用余额
        setOutParam("zhjg", zhjg);// 账户机构
        setOutParam("zjjcrq", zjjcrq);// 最近缴存日期
        setOutParam("zjtqrq", zjtqrq);// 最近提取日期
        setOutParam("tqcs", tqcs);// 提取次数
        setOutParam("dkcs", dkcs);// 贷款次数
        setOutParam("dwdh", dwdh);// 单位电话
        setOutParam("dwdz", dwdz);// 单位地址
        setOutParam("jcyh", jcyh);// 缴存银行
        setOutParam("glyh", glyh);// 关联银行
        setOutParam("glyhkh", glyhkh);// 关联银行卡号
        /* 出口参数赋值结束 */

        return 0;
    }

}
