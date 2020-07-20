package com.yd.ish.biz.comp.ln;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransOtherException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.GetDownFileMap;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.dataswap.file.FileSwap;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.comm_mdl.log.YDLogger;
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
 * 名称：CompFYLPXXCX03
 * <p>
 * 功能：房源楼盘信息查询<br>
 *
 * @author 柏慧敏
 * @version 0.1    2018年6月5日	柏慧敏创建
 * @brief 获取房源楼盘信息
 * @note
 */
@Component("CompFYLPXX03")
public class CompFYLPXX03 extends BaseComp {

    private static final Logger logger = LoggerFactory.getLogger(CompFYLPXX03.class);
    /**
     * 数据分割符
     */
    private static final String separators = ReadProperty.getString("file_separators");
    /**
     * 读核心返回文件编码格式
     */
    private static final String encoding = ReadProperty.getString("readfile_encoding_bsp");
    /**
     * 数据库类型
     */
    private static final String DB_TYPE = ReadProperty.getString("dbType");

    @Autowired
    GetDownFileMap getDownFileMap;

    @Autowired
    DP077Service dp077service;

    @Override
    public int execute() {

        /* 入口参数赋值开始 */
        int instance = getInt("instance");// 实例号
        /* 入口参数赋值结束 */

        DP077 dp077 = new DP077();
        // 根据实例号删除当前数据
        dp077.setInstance(instance);
        dp077.setDpbusitype("03");
        dp077service.deleteByCause(dp077);
        logger.info("[+]调用接口获取房源楼盘信息，存临时表开始");
        // 调用接口获取房源楼盘信息
        XmlResObj data = super.sendExternal("BSP_LN_FYLPXXCX_01");
        XmlResHead head = data.getHead();
        if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
            throw new TransSingleException(head.getParticular_info());
        }
        // 调用综服接口从body中获取文件名
        Map<String, Object> body = data.getBody();
        String filename = body.get("file").toString();
        //String filename = head.getFile(); //webservive从head中获取文件名 TODO
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
                    // 批量查表一次行数
                    int count = 0;
                    // 累计记录文件总行数
                    int total = 0;
                    // 是否为文件表头信息
                    Boolean first = true;
                    // 记录行文件
                    String tmp = null;

                    // 实际返回的数据
                    HashMap<String, Object> tmpdata = null;
                    // 文件中第一行列名
                    String[] fileColNames = null;

                    List<DP077> list = new ArrayList<DP077>();

                    // 如果文件信息不为null，开始解析文件
                    while ((tmp = br.readLine()) != null) {
                        if (first) {
                            fileColNames = tmp.toLowerCase().split(separators);
                            first = false;
                            continue;
                        } else {
                            tmpdata = getDownFileMap.query(fileColNames, tmp);
                            total++;
                            count++;

                            // 解析文件，将数据存入临时表中
                            dp077 = new DP077();
                            dp077.setSeqno(total);// 序号
                            dp077.setDpbusitype("03");// 业务类型，01-开发商信息；02-楼盘信息；03-房源楼盘信息
                            dp077.setInstance(instance);// 实例号
                            dp077.setUnitaccnum2(tmpdata.get("hzlpbh").toString());// 合作楼盘编号
                            dp077.setUnitaccname2(tmpdata.get("lpmc").toString());// 楼盘名称
                            dp077.setUnitaccname1(tmpdata.get("kfsmc").toString());// 开发商名称
                            list.add(dp077);
                            // 累计循环一定条数批量提交入库一次
                            if (count != 0 && count % IshConstants.GG_BATCH_COUNT == 0) {
                                // 批量插入数据库之前临时关闭日志输出
                                YDLogger.closeOut();
                                if ("db2".equals(DB_TYPE)) {
                                    dp077service.db2batchInsert(list);
                                } else if ("oracle".equals(DB_TYPE)) {
                                    dp077service.oraclebatchInsert(list);
                                }
                                // 批量插入数据库之后打开临时关闭日志输出
                                YDLogger.openOut();
                                list.clear();
                                count = 0;
                            }
                        }
                    }
                    if (count > 0) {
                        // 批量插入数据库之前临时关闭日志输出
                        YDLogger.closeOut();
                        if ("db2".equals(DB_TYPE)) {
                            dp077service.db2batchInsert(list);
                        } else if ("oracle".equals(DB_TYPE)) {
                            dp077service.oraclebatchInsert(list);
                        }
                        // 批量插入数据库之后打开临时关闭日志输出
                        YDLogger.openOut();
                    }
                    logger.info("共" + total + "条数据");
                    logger.info("[-]调用接口获取房源楼盘信息，存临时表结束");
                } catch (RuntimeException | IOException e) {
                    // 批量插入数据库出错临时关闭日志输出
                    YDLogger.openOut();
                    e.printStackTrace();
                    logger.error("插入数据库出错：" + e.getMessage(), e);
                    throw new TransOtherException("系统错误，请查看日志！");
                } finally {
                    // 打开临时关闭日志输出
                    YDLogger.openOut();
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
        /* 出口参数赋值开始 */

        /* 出口参数赋值结束 */

        return 0;
    }

}
