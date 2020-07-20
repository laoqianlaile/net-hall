package com.yd.ish.biz.comp.dp;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.ReadProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 名称：CompGRJCZM01
 * <p>
 * 功能：获取个人住房公积金缴存证明凭证<br>
 *
 * @author 许永峰
 * @version 0.1 2019年4月25日 柏慧敏创建
 * @brief 获取个人住房公积金缴存证明凭证
 * @note
 */
@Component("CompGRJCZM01")
public class CompGRJCZM01 extends BaseComp {

    private static final Logger logger = LoggerFactory.getLogger(CompGRJCZM01.class);

    @Override
    public int execute() {

        // 调用接口获取公积金账户基本信息
        logger.info("[+]调用接口获取个人公积金缴存证明凭证开始");
        XmlResObj data = super.sendExternal("BSP_DP_GRJCZM_01");
        XmlResHead head = data.getHead();
        if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
            throw new TransSingleException(head.getParticular_info());
        }
        Map<String, Object> body = data.getBody();

        String pzfilename = body.get("filename") == null ? "" : body.get("filename").toString();
        logger.info("[-]调用接口获取个人公积金缴存证明凭证结束");

        /* 出口参数赋值开始 */
        setOutParam("filename", pzfilename);// 凭证文件名称
        /* 出口参数赋值结束 */

        return 0;
    }

}
