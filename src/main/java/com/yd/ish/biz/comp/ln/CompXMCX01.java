package com.yd.ish.biz.comp.ln;

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

import com.yd.biz.comp.BaseComp;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 名称：CompXMCX01
 * <p>功能：获取项目、楼栋、楼盘信息<br>
 * @brief 获取项目、楼栋、楼盘信息
 * @author 柏慧敏
 * @version V0.1 柏慧敏 2019-06-12 长春 新建
 * @note
 */
@Component("CompXMCX01")
public class CompXMCX01 extends BaseComp {

    private static final Logger logger = LoggerFactory.getLogger(CompXMCX01.class);
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

        /*入口参数赋值开始*/
        int instance = getInt("instance");//实例号
        /*入口参数赋值结束*/

        // 根据实例号和业务类型删除临时表中的数据
        String[] dpbusitype = {"03","04"};// 01-开发商信息；02-项目信息；03-楼栋信息；04-楼盘信息
        dp077service.deleteByDpbusitype(dpbusitype,instance);
        // 调用接口查询项目、楼盘、楼栋信息
        XmlResObj data = super.sendExternal("BSP_LN_GETXMLPXX_01");
        XmlResHead head = data.getHead();
        if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
            throw new TransSingleException(head.getParticular_info());
        }
        Map<String, Object> body = data.getBody();
        String xmmc = body.get("xmmc") == null ? "" : body.get("xmmc").toString();
        String lpqymc = body.get("lpqymc") == null ? "" : body.get("lpqymc").toString();
        String xmwz = body.get("xmwz") == null ? "" : body.get("xmwz").toString();
        String xykssj = body.get("xykssj") == null ? "" : body.get("xykssj").toString();
        String xyjssj = body.get("xyjssj") == null ? "" : body.get("xyjssj").toString();
        String lpmj = body.get("lpmj") == null ? "" : body.get("lpmj").toString();
        String lpjj = body.get("lpjj") == null ? "" : body.get("lpjj").toString();
        String lpzts = body.get("lpzts") == null ? "" : body.get("lpzts").toString();
        String lpzzs = body.get("lpzzs") == null ? "" : body.get("lpzzs").toString();
        String yssj = body.get("yssj") == null ? "" : body.get("yssj").toString();
        String jgsj = body.get("jgsj") == null ? "" : body.get("jgsj").toString();
        String syqlx = body.get("syqlx") == null ? "" : body.get("syqlx").toString();
        String syqzzrq = body.get("syqzzrq") == null ? "" : body.get("syqzzrq").toString();
        String tdzfzjg = body.get("tdzfzjg") == null ? "" : body.get("tdzfzjg").toString();
        String jsydghydmj = body.get("jsydghydmj") == null ? "" : body.get("jsydghydmj").toString();
        String jsydghjsgm = body.get("jsydghjsgm") == null ? "" : body.get("jsydghjsgm").toString();
        String jsydghydxz = body.get("jsydghydxz") == null ? "" : body.get("jsydghydxz").toString();
        String tdzbh = body.get("tdzbh") == null ? "" : body.get("tdzbh").toString();
        String jsgcsgxkz = body.get("jsgcsgxkz") == null ? "" : body.get("jsgcsgxkz").toString();
        String jzgcghxkz = body.get("jzgcghxkz") == null ? "" : body.get("jzgcghxkz").toString();
        String jsydghxkz = body.get("jsydghxkz") == null ? "" : body.get("jsydghxkz").toString();
        String bzjkhyh = body.get("bzjkhyh") == null ? "" : body.get("bzjkhyh").toString();
        String bzjzhmc = body.get("bzjzhmc") == null ? "" : body.get("bzjzhmc").toString();
        String bzjzhhm = body.get("bzjzhhm") == null ? "" : body.get("bzjzhhm").toString();
        String filename = body.get("file") == null ? "" : body.get("file").toString();
        // 读取文件
        if (!(filename == null)) {
            for (int i = 0; i < filename.split(",").length; i++) {
                File file = FileSwap.getFile(filename.split(",")[i]);
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
                                DP077 dp077 = new DP077();
                                // 若为楼栋信息，业务类型赋值为03
                                if ("03".equals(tmpdata.get("ywlx").toString())) {
                                    dp077.setSeqno(total);// 序号
                                    dp077.setDpbusitype("03");// 业务类型，01-开发商信息；02-项目信息；03-楼栋信息；04-楼盘信息
                                    dp077.setInstance(instance);// 实例号
                                    dp077.setAccnum1(tmpdata.get("ldbh").toString());// 楼栋编号
                                    dp077.setAccnum2(tmpdata.get("ysxkzh").toString());// 预售许可证号
                                    dp077.setAgentinstcode(tmpdata.get("skyh").toString());// 收款银行
                                    dp077.setAccname1(tmpdata.get("yhzhmc").toString());// 银行账户名称
                                    dp077.setAccname2(tmpdata.get("yhzh").toString());// 银行账户
                                    dp077.setPeoplenum(Integer.parseInt(tmpdata.get("zcs").toString()));// 总层数
                                }
                                // 若为楼盘信息，业务类型赋值为04
                                else if ("04".equals(tmpdata.get("ywlx").toString())) {
                                    dp077.setSeqno(total);// 序号
                                    dp077.setDpbusitype("04");// 业务类型，01-开发商信息；02-项目信息；03-楼栋信息；04-楼盘信息
                                    dp077.setInstance(instance);// 实例号
                                    dp077.setAccnum1(tmpdata.get("ldbh").toString());// 楼栋编号
                                    dp077.setUnitaccnum1(tmpdata.get("dyh").toString());// 单元号
                                    dp077.setUnitaccnum2(tmpdata.get("szcs").toString());// 所在层数
                                    dp077.setUnitaccname1(tmpdata.get("mph").toString());// 门牌号
                                    dp077.setUnitprop(new BigDecimal(tmpdata.get("jzmj").toString()));// 建筑面积
                                    dp077.setIndiprop(new BigDecimal(tmpdata.get("tnmj").toString()));// 套内面积
                                    dp077.setAmt1(new BigDecimal(tmpdata.get("jzxsdj").toString()));// 建筑销售单价
                                    dp077.setAmt2(new BigDecimal(tmpdata.get("fwzj").toString()));// 房屋总价
                                }
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
                        logger.info("[-]调用接口获取开发商信息，存临时表结束");
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
        }
        /*出口参数赋值开始*/
        setOutParam("xmmc", xmmc);//项目名称
        setOutParam("lpqymc", lpqymc);//楼盘区域名称
        setOutParam("xmwz", xmwz);//项目位置
        setOutParam("xykssj", xykssj);//协议开始日期
        setOutParam("xyjssj", xyjssj);//协议结束日期
        setOutParam("lpmj", lpmj);//楼盘面积
        setOutParam("lpjj", lpjj);//楼盘均价
        setOutParam("lpzts", lpzts);//楼盘总套数
        setOutParam("lpzzs", lpzzs);//楼盘总幢数
        setOutParam("yssj", yssj);//预售时间
        setOutParam("jgsj", jgsj);//竣工时间
        setOutParam("syqlx", syqlx);//使用权类型
        setOutParam("syqzzrq", syqzzrq);//使用权终止日期
        setOutParam("tdzfzjg", tdzfzjg);//土地证发证机关
        setOutParam("jsydghydmj", jsydghydmj);//建设用地规划用地面积
        setOutParam("jsydghjsgm", jsydghjsgm);//保证金建设用地规划建设规模比例
        setOutParam("jsydghydxz", jsydghydxz);//建设用地规划用地性质
        setOutParam("tdzbh", tdzbh);//土地证编号
        setOutParam("jsgcsgxkz", jsgcsgxkz);//建筑工程施工许可证
        setOutParam("jzgcghxkz", jzgcghxkz);//建筑工程规划许可证
        setOutParam("jsydghxkz", jsydghxkz);//建设用地规划许可证
        setOutParam("bzjkhyh", bzjkhyh);//保证金开户银行
        setOutParam("bzjzhmc", bzjzhmc);//保证金账户名称
        setOutParam("bzjzhhm", bzjzhhm);//保证金账户号码
        setOutParam("filename", filename);//下传文件名
        /*出口参数赋值结束*/

        return 0;
    }

}
