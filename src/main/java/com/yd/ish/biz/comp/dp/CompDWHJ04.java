package com.yd.ish.biz.comp.dp;

import com.alibaba.druid.support.json.JSONUtils;
import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransOtherException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.GetDownFileMap;
import com.yd.svrplatform.comm_mdl.dataswap.file.FileSwap;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.ReadProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 名称：CompDWHJ04.java
 * <p>功能：单位汇缴-获取单位欠缴信息(缴至年月至当前时点) <br>
 *
 * @author 许永峰
 * @version 0.1    2019年11月26日	许永峰创建
 */
@Component("CompDWHJ04")
public class CompDWHJ04 extends BaseComp {

    private static final Logger logger = LoggerFactory.getLogger(CompDWHJ04.class);
    /**
     * 数据分割符
     */
    private static final String separators = ReadProperty.getString("file_separators");
    /**
     * 读核心返回文件编码格式
     */
    private static final String encoding = ReadProperty.getString("readfile_encoding_bsp");
    @Autowired
    GetDownFileMap getDownFileMap;

    @Override
    public int execute() {
        /*入口参数赋值开始*/
        /*入口参数赋值结束*/
        logger.debug("[+]调用接口获取单位欠缴批量信息开始");
        // 调用接口获取单位欠缴批量信息
        XmlResObj data = super.sendExternal("BSP_DP_HQDWQJXX_01");
        XmlResHead head = data.getHead();
        if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
            throw new TransSingleException(head.getParticular_code(), head.getParticular_info());
        }

        Map<String, Object> body = data.getBody();
        String filename = body.get("file").toString();
        List<HashMap<String, Object>> dwqjxx = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> tmpdata = null;
        if (!(filename == null)) {
            File file = FileSwap.getFile(filename);
            if (file == null) {
                logger.error("下传文件不存在：" + filename);
                throw new TransOtherException("系统错误，请查看日志！");
            } else {
                // 解析下传文件
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
                    // 是否为文件表头信息
                    Boolean first = true;
                    // 记录行文件
                    String tmp = null;
                    // 实际返回的数据

                    // 文件中第一行列名
                    String[] fileColNames = null;
                    // 如果文件信息不为null，开始解析文件
                    while ((tmp = br.readLine()) != null) {
                        if (first) {
                            fileColNames = tmp.toLowerCase().split(separators);
                            first = false;
                            continue;
                        } else {
                            tmpdata = getDownFileMap.query(fileColNames, tmp);
                            dwqjxx.add(tmpdata);
                        }
                    }
                    logger.debug("[-]调用接口获取单位欠缴批量信息结束");
                } catch (IOException e1) {
                    e1.printStackTrace();
                    logger.error("读取文件出错：" + e1.getMessage(), e1);
                    throw new TransOtherException("系统错误，请查看日志！");
                } finally {
                    try {
                        if (br != null) {
                            br.close();
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        logger.error("关闭文件出错：" + e1.getMessage(), e1);
                        throw new TransOtherException("系统错误，请查看日志！");
                    }
                }
            }
        }
        /*出口参数赋值开始*/
        setOutParam("json", JSONUtils.toJSONString(dwqjxx));
        /*出口参数赋值结束*/
        return 0;
    }
}
