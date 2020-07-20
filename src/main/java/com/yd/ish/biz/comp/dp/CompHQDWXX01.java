package com.yd.ish.biz.comp.dp;

import com.alibaba.druid.support.json.JSONUtils;
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
import com.yd.svrplatform.jdbc.PersistentBiz;
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
 * 名称：CompHQDWXX01.java
 * <p>
 * 功能：获取单位信息 <br>
 *
 * @author 王赫
 * @version 0.1 2018年6月4日 王赫创建
 * v0.2  20191127  许永峰 修改增加返回经办人json
 */
@Component("CompHQDWXX01")
public class CompHQDWXX01 extends BaseComp {

    private static final Logger logger = LoggerFactory.getLogger(CompHQDWXX01.class);

    /**
     * 数据分割符
     */
    private static final String file_separators = ReadProperty.getString("file_separators");
    /**
     * 编码格式
     */
    private static final String READFILE_ENCODING_BSP = ReadProperty.getString("readfile_encoding_bsp");
    /**
     * 数据库类型
     **/
    private static final String DB_TYPE = ReadProperty.getString("dbType");

    @Autowired
    PersistentBiz persistentBiz;

    @Autowired
    DP077Service dp077Service;

    @Autowired
    GetDownFileMap getDownFileMap;

    @Override
    public int execute() {

        /* 入口参数赋值开始 */
        int instance = getInt("_IS");// 实例号
        /* 入口参数赋值结束 */
        logger.info("[+]调用查询单位信息接口BSP_DP_GETDWXX_01开始");
        XmlResObj data = super.sendExternal("BSP_DP_GETDWXX_01", false);
        XmlResHead head = data.getHead();
        if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
            throw new TransSingleException(head.getParticular_info());
        }
        Map<String, Object> body = data.getBody();
        String dwjcbl = returnString(body.get("dwjcbl"));
        String grjcbl = returnString(body.get("grjcbl"));
        String dwfcrs = returnString(body.get("dwfcrs"));
        String dwmc = returnString(body.get("dwmc"));
        String tyshxydm = returnString(body.get("tyshxydm"));
        String zzjgdm = returnString(body.get("zzjgdm"));
        String dwdz = returnString(body.get("dwdz"));
        String dwfrdbxm = returnString(body.get("dwfrdbxm"));
        String dwfrdbzjlx = returnString(body.get("dwfrdbzjlx"));
        String dwfrdbzjhm = returnString(body.get("dwfrdbzjhm"));
        String dwfrlxdh = returnString(body.get("dwfrlxdh"));
        String dwsbzh = returnString(body.get("dwsbzh"));
        String gszch = returnString(body.get("gszch"));
        String dwsshy = returnString(body.get("dwsshy"));
        String dwxz = returnString(body.get("dwxz"));
        String sbjczrs = returnString(body.get("sbjczrs"));
        String sbjczjs = returnString(body.get("sbjczjs"));
        String dwjb = returnString(body.get("dwjb"));
        String sjdwmc = returnString(body.get("sjdwmc"));
        String sjdwzh = returnString(body.get("sjdwzh"));
        String dwlsgx = returnString(body.get("dwlsgx"));
        String dwjjlx = returnString(body.get("dwjjlx"));
        String dwyb = returnString(body.get("dwyb"));
        String dwdzxx = returnString(body.get("dwdzxx"));
        String dwslrq = returnString(body.get("dwslrq"));
        String dwfxr = returnString(body.get("dwfxr"));
        String zgbm = returnString(body.get("zgbm"));
        String sftsba = returnString(body.get("sftsba"));
        String dwxxhdnf = returnString(body.get("dwxxhdnf"));
        String szqy = returnString(body.get("szqy"));
        String szxz = returnString(body.get("szxz"));
        String yhmc = returnString(body.get("yhmc"));
        String wdmc = returnString(body.get("wdmc"));
        String jjr = returnString(body.get("jjr"));
        String jcqsny = returnString(body.get("jcqsny"));
        String jzny = returnString(body.get("jzny"));
        String dwzhzt = returnString(body.get("dwzhzt"));
        String dwhjzt = returnString(body.get("dwhjzt"));
        String dwqjzt = returnString(body.get("dwqjzt"));
        String yjrs = returnString(body.get("yjrs"));
        String yjje = returnString(body.get("yjje"));
        String zckye = returnString(body.get("zckye"));
        //调用综服接口从body中获取文件名解析单位经办人信息
        String filename = returnString(body.get("file"));
        //v0.2 新增  start
         //经办人信息
        List<HashMap<String, Object>> jbrxx = new ArrayList<HashMap<String, Object>>();
        //v0.2 新增  end
        if (!"".equals(filename)) {
            File file = FileSwap.getFile(filename);
            if (file == null) {
                logger.error("下传文件不存在：" + filename);
                throw new TransOtherException("系统错误，请查看日志！");
            } else {
                //解析下传文件
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(file), READFILE_ENCODING_BSP));
                    //批量查表一次行数
                    int count = 0;
                    //累计记录文件总行数
                    int total = 0;
                    //是否为文件表头信息
                    Boolean first = true;
                    //记录行文件
                    String tmp = null;

                    //实际返回的数据
                    HashMap<String, Object> tmpdata = null;
                    //文件中第一行列名
                    String[] fileColNames = null;

                    //查询数据库临时表的最大序号
                    List<DP077> list = new ArrayList<DP077>();
                    int seqno = 0;

                    //如果文件信息不为null，开始解析文件
                    while ((tmp = br.readLine()) != null) {
                        if (first) {
                            fileColNames = tmp.toLowerCase().split(file_separators);
                            first = false;
                            continue;
                        } else {
                            tmpdata = getDownFileMap.query(fileColNames, tmp);
                            //v0.2 新增  start
                            jbrxx.add(tmpdata);
                            //v0.2 新增  end
                            total++;
                            count++;

                            //解析文件，将数据存入临时表中
//								jbrxm~jbrzjlx~jbrzjhm~jbrsjhm~jbrgddhhm~dwdzxx~jbrcz~jbbm~bgbz
//	seqno,dpbusitype,instance,agentop,certitype,certinum,sjhm,payvounum,freeuse1,jtzz,agentinstcode,reason";
                            DP077 dp077 = new DP077();
                            seqno++;
                            dp077.setSeqno(seqno);//序号
                            dp077.setDpbusitype("0");//业务类型
                            dp077.setInstance(instance);//实例号
                            dp077.setAgentop(tmpdata.get("jbrxm").toString());//经办人姓名
                            dp077.setCertitype(tmpdata.get("jbrzjlx").toString());//经办人证件类型
                            dp077.setCertinum(tmpdata.get("jbrzjhm").toString());//经办人证件号码
                            dp077.setSjhm(tmpdata.get("jbrsjhm").toString());//经办人手机号码
                            dp077.setPayvounum(tmpdata.get("jbrgddhhm").toString());//经办人固定电话
                            dp077.setFreeuse1(tmpdata.get("jbrdzxx").toString());//经办人电子信箱
                            dp077.setJtzz(tmpdata.get("jbrcz").toString());//经办人传真
                            dp077.setAgentinstcode(tmpdata.get("jbbm").toString());//经办部门
                            dp077.setReason(tmpdata.get("bgbz").toString());//变更备注

                            list.add(dp077);
                            //累计循环一定条数批量提交入库一次
                            if (count != 0 && count % IshConstants.GG_BATCH_COUNT == 0) {
                                //批量插入数据库之前临时关闭日志输出
                                YDLogger.closeOut();
                                if ("db2".equals(DB_TYPE)) {
                                    dp077Service.db2batchInsert(list);
                                } else if ("oracle".equals(DB_TYPE)) {
                                    dp077Service.oraclebatchInsert(list);
                                }
                                list.clear();
                                //批量插入数据库之后打开临时关闭日志输出
                                YDLogger.openOut();
                                count = 0;
                            }
                        }
                    }
                    if (count > 0) {
                        //批量插入数据库之前临时关闭日志输出
                        YDLogger.closeOut();
                        if ("db2".equals(DB_TYPE)) {
                            dp077Service.db2batchInsert(list);
                        } else if ("oracle".equals(DB_TYPE)) {
                            dp077Service.oraclebatchInsert(list);
                        }
                        //批量插入数据库之后打开临时关闭日志输出
                        YDLogger.openOut();
                    }
                    logger.debug("共" + total + "条数据");
                    logger.debug("[-]调用接口获取经办人批量信息，存临时表结束-----");
                } catch (RuntimeException | IOException e) {
                    //批量插入数据库出错临时关闭日志输出
                    YDLogger.openOut();
                    e.printStackTrace();
                    logger.error("插入数据库出错：" + e.getMessage(), e);
                    throw new TransOtherException("系统错误，请查看日志！");
                } finally {
                    //打开临时关闭日志输出
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
        logger.info("[-]调用查询单位信息接口BSP_DP_GETDWXX_01成功");

        /* 出口参数赋值开始 */
        setOutParam("dwjcbl", dwjcbl);
        setOutParam("grjcbl", grjcbl);
        setOutParam("dwfcrs", dwfcrs);
        setOutParam("dwmc", dwmc);
        setOutParam("tyshxydm", tyshxydm);
        setOutParam("zzjgdm", zzjgdm);
        setOutParam("dwdz", dwdz);
        setOutParam("dwfrdbxm", dwfrdbxm);
        setOutParam("dwfrdbzjlx", dwfrdbzjlx);
        setOutParam("dwfrdbzjhm", dwfrdbzjhm);
        setOutParam("dwfrlxdh", dwfrlxdh);
        setOutParam("dwsbzh", dwsbzh);
        setOutParam("gszch", gszch);
        setOutParam("dwsshy", dwsshy);
        setOutParam("dwxz", dwxz);
        setOutParam("sbjczrs", sbjczrs);
        setOutParam("sbjczjs", sbjczjs);
        setOutParam("dwjb", dwjb);
        setOutParam("sjdwmc", sjdwmc);
        setOutParam("sjdwzh", sjdwzh);
        setOutParam("dwlsgx", dwlsgx);
        setOutParam("dwjjlx", dwjjlx);
        setOutParam("dwyb", dwyb);
        setOutParam("dwdzxx", dwdzxx);
        setOutParam("dwslrq", dwslrq);
        setOutParam("dwfxr", dwfxr);
        setOutParam("zgbm", zgbm);
        setOutParam("sftsba", sftsba);
        setOutParam("dwxxhdnf", dwxxhdnf);
        setOutParam("szqy", szqy);
        setOutParam("szxz", szxz);
        setOutParam("yhmc", yhmc);
        setOutParam("wdmc", wdmc);
        setOutParam("jjr", jjr);
        setOutParam("jcqsny", jcqsny);
        setOutParam("jzny", jzny);
        setOutParam("dwzhzt", dwzhzt);
        setOutParam("dwhjzt", dwhjzt);
        setOutParam("dwqjzt", dwqjzt);
        setOutParam("yjrs", yjrs);
        setOutParam("yjje", yjje);
        setOutParam("zckye", zckye);
        setOutParam("filename", filename);
        //v0.2 新增  start
        setOutParam("jbrxxjson", JSONUtils.toJSONString(jbrxx));
        //v0.2 新增  end
        /* 出口参数赋值结束 */

        return 0;
    }

    public String returnString(Object str) {
        return str == null || str.toString().isEmpty() ? "" : str.toString();
    }
}
