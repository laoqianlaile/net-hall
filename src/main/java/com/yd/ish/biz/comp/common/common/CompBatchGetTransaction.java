package com.yd.ish.biz.comp.common.common;

import com.yd.biz.config.CompBean;
import com.yd.biz.exception.TransOtherException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.GetDownFileMap;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.ish.util.GgUtil;
import com.yd.org.util.CommonErrorCode;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.dataswap.file.FileSwap;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.comm_mdl.log.YDLogger;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 名称：CompBatchGetTransaction.java
 * <p>功能：通用批量查询交易 <br>
 *
 * @author 林楠
 * @version 0.1    2018年9月4日	林楠创建
 */
@Component("CompBatchGetTransaction")
public class CompBatchGetTransaction extends AbsCompBatchTransaction {

    @Autowired
    DP077Service dp077service;
    @Autowired
    GetDownFileMap getDownFileMap;

    @Override
    public int execute() {
        /*入口参数赋值开始*/

        CompBean compBean = this.getCompBean();
        String apiId = getString(apiIdFiled());
        String apiName = getString(apiNameField(), apiId);
        String  fileField = getString(fileField());
        LOGGER.debug("prename================"+super.getMainContext().getDataPool().getString("prename"));
        /*入口参数结束*/

        //入口参数检查
        if (StringUtils.isEmpty(apiId)) {
            throw new TransSingleException(CommonErrorCode.ERROR_NOT_NULL, "交易码");
        }

        if ( StringUtils.isEmpty(fileField) ){
            throw new TransSingleException(CommonErrorCode.ERROR_NOT_NULL, "下传文件名");
        }

        //根据实例号清理临时表
        int instance = getInt("_IS");
        // 根据实例号删除当前数据
        LOGGER.info("[+]调用交易{}，删除临时表开始{}", apiName,instance);
        dp077service.deleteBySlh(instance);
        LOGGER.info("[+]调用交易{}，删除临时表结束", apiName);

        //调用接口
        XmlResObj data = this.sendExternal(apiId);
        XmlResHead head = data.getHead();
        if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
            throw new TransSingleException(head.getParticular_code(), head.getParticular_info());
        } else {

            // 从body中获取文件名
            Map<String, Object> body = data.getBody();
            String filename = "";
            if (body.containsKey(fileField)) {
                filename = String.valueOf(body.get(fileField));
            }
            if (StringUtils.isEmpty(filename)) {
                LOGGER.error("下传文件名为空");
                throw new TransSingleException(CommonErrorCode.ERROR_WJDQSB, "文件" + filename + "不存在!");
            }
            //内网下发开此段代码屏蔽下段代码
            File file = FileSwap.getFile(filename);
            if (file == null) {
                LOGGER.error("下传文件不存在：" + filename);
                throw new TransSingleException(CommonErrorCode.ERROR_WJDQSB, "文件" + filename + "不存在!");
            }
            // 解析下传文件
            try (FileInputStream fs = new FileInputStream(file);
//                 InputStreamReader isr = new InputStreamReader(fs, READFILE_ENCODING_BSP);
                 InputStreamReader isr = new InputStreamReader(fs,"GBK");
                 BufferedReader br = new BufferedReader(isr)) {
                // 批量查表一次行数
                int count = 0;
                // 累计记录文件总行数
                int total = 0;
                // 是否为文件表头信息
//                Boolean first = false;
                Boolean first = true;
                // 记录行文件
                String tmp = null;
                // 实际返回的数据
                HashMap<String, Object> tmpdata = null;
                // 文件中第一行列名
                String[] fileColNames = null;
                // 解析下传文件字段定义
                Map<String, String> fileColDef = getFileColDef();
                // 查询数据库临时表的最大序号
                List<DP077> list = new ArrayList<DP077>();
                int seqno = 0;
                // 如果文件信息不为null，开始解析文件
                while ((tmp = br.readLine()) != null) {
                    //判断是否存在表头
                    if (first) {
                        //存在表头，进行表头解析
//                        fileColNames = tmp.toLowerCase().split(FILE_SEPARATORS);
                        if(isTableHead(tmp)){//正则判断是否是表格头
                            first = false;
                            continue;
                        }
                        first = false;

                    }
                  //  fileColNames = tmp.toLowerCase().split(FILE_SEPARATORS);
                  //  tmpdata = getDownFileMap.query(fileColNames, tmp);
                    tmpdata = query(fileColDef, tmp);
                    total++;
                    count++;
                    // 解析文件，将数据存入临时表中
                    DP077 dp077 = new DP077();
                    // 序号
                    seqno++;
                    dp077.setSeqno(seqno);
                    // 实例号默认值
                    dp077.setInstance(instance);
                    //数据库不允许为空字段设置
                    dp077.setDpbusitype("0");
                    setDp077ByFile(fileColDef, dp077, tmpdata);
                    list.add(dp077);
                    // 累计循环一定条数批量提交入库一次
                    if (count % 100 == 0) {
                        //批量插入数据库之前临时关闭日志输出
                        YDLogger.closeOut();
                        batchInsert(list);
                        list.clear();
                        //批量插入数据库之后打开临时关闭日志输出
                        YDLogger.openOut();
                        count = 0;
                    }

                }
                if (count > 0) {
                    //批量插入数据库之前临时关闭日志输出
//                    YDLogger.closeOut();
                    batchInsert(list);
                    //批量插入数据库之后打开临时关闭日志输出
//                    YDLogger.openOut();
                }
                LOGGER.info("共" + total + "条数据");
                LOGGER.info("[+]调用交易{}，存临时表开始结束", apiName);
            } catch (RuntimeException | IOException e) {
                //批量插入数据库出错临时关闭日志输出
                YDLogger.openOut();
                LOGGER.error("插入数据库出错：" + e.getMessage(), e);
                throw new TransOtherException("系统错误，请查看日志！");
            } finally {
                // 打开临时关闭日志输出
                YDLogger.openOut();
            }

            /*出口参数赋值开始*/
            setCompOutParams( data , compBean , apiId);
            /*出口参数赋值结束*/
        }
        return 0;
    }

    private void batchInsert(List<DP077> list) {
        if ("db2".equals(DB_TYPE)) {
            dp077service.db2batchInsert(list);
        } else if ("oracle".equals(DB_TYPE)) {
            dp077service.oraclebatchInsert(list);
        }
    }

    /**
     * 文件内容解析为临时表实体
     *
     * @param dp077   临时表实体
     * @param tmpdata 文件内容
     */
    private void setDp077ByFile(Map<String, String> fileColDef, DP077 dp077, Map<String, Object> tmpdata) {
        Iterator it = fileColDef.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            try {
                setDp077(dp077, e.getKey().toString().toLowerCase(), e.getValue().toString(), tmpdata);
            } catch (ParseException ex) {
                LOGGER.error("转换日期格式出错[" + tmpdata.get(e.getValue().toString()) + "]", ex);
                throw new TransSingleException("转换日期格式出错，日期：" + tmpdata.get(e.getValue().toString()));
            } catch (NumberFormatException ex) {
                LOGGER.error("转换数字格式出错[" + tmpdata.get(e.getValue().toString()) + "]", ex);
                throw new TransSingleException("转换数字格式出错，数字：" + tmpdata.get(e.getValue().toString()));
            }
        }
    }

    /**
     * 根据文件列定义赋值临时表实体
     *
     * @param dp077   临时表实体
     * @param key     临时表实体字段名
     * @param value   文件列名
     * @param tmpdata 文件行
     * @throws NumberFormatException 数字格式异常
     * @throws ParseException        日期转换异常
     */
    private void setDp077(DP077 dp077, String key, String value, Map<String, Object> tmpdata) throws NumberFormatException, ParseException {
        // 标识了从总线取值的情况 将总线值放入map
        if (value.startsWith(FILE_DATAPOOL_SEPARATORS)) {
            value = value.replaceFirst(FILE_DATAPOOL_SEPARATORS, "");
            tmpdata.put(value, getString(value));
        }
        /**
         * 针对日期为 1899-12-31 的进行特殊处理 
         */
        String val=tmpdata.get(value).toString();
        if("1899-12-31".equals(val)){
        	val="";
        }
        switch (key) {
            case "seqno":
                dp077.setSeqno(Integer.valueOf(val));
                break;
            case "dpbusitype":
                dp077.setDpbusitype(val);
                break;
            case "transdate":
                dp077.setTransdate(GgUtil.strToDate(val, IshConstants.GG_DATE_FORMAT));
                break;
            case "unitaccnum1":
                dp077.setUnitaccnum1(val);
                break;
            case "unitaccnum2":
                dp077.setUnitaccnum2(val);
                break;
            case "unitaccname1":
                dp077.setUnitaccname1(val);
                break;
            case "unitaccname2":
                dp077.setUnitaccname2(val);
                break;
            case "accnum1":
                dp077.setAccnum1(val);
                break;
            case "accnum2":
                dp077.setAccnum2(val);
                break;
            case "accname1":
                dp077.setAccname1(val);
                break;
            case "accname2":
                dp077.setAccname2(val);
                break;
            case "sex":
                dp077.setSex(val);
                break;
            case "birthday":
                dp077.setBirthday(GgUtil.strToDate(val, IshConstants.GG_DATE_FORMAT));
                break;
            case "certitype":
                dp077.setCertitype(val);
                break;
            case "certinum":
                dp077.setCertinum(val);
                break;
            case "basenum":
                dp077.setBasenum(new BigDecimal(val));
                break;
            case "unitprop":
                dp077.setUnitprop(new BigDecimal(val));
                break;
            case "indiprop":
                dp077.setIndiprop(new BigDecimal(val));
                break;
            case "amt1":
                dp077.setAmt1(new BigDecimal(val));
                break;
            case "amt2":
                dp077.setAmt2(new BigDecimal(val));
                break;
            case "peoplenum":
                dp077.setPeoplenum(Integer.valueOf(val));
                break;
            case "begdate":
                dp077.setBegdate(GgUtil.strToDate(val, IshConstants.GG_DATE_FORMAT));
                break;
            case "enddate":
                dp077.setEnddate(GgUtil.strToDate(val, IshConstants.GG_DATE_FORMAT));
                break;
            case "reason":
                dp077.setReason(val);
                break;
            case "onym":
                dp077.setOnym(val);
                break;
            case "begym":
                dp077.setBegym(val);
                break;
            case "endym":
                dp077.setEndym(val);
                break;
            case "fundsouflag":
                dp077.setFundsouflag(val);
                break;
            case "proptype":
                dp077.setProptype(val);
                break;
            case "payvounum":
                dp077.setPayvounum(val);
                break;
            case "payvouamt":
                dp077.setPayvouamt(new BigDecimal(val));
                break;
            case "payvoubank":
                dp077.setPayvoubank(val);
                break;
            case "instance":
                dp077.setInstance(Integer.valueOf(val));
                break;
            case "agentinstcode":
                dp077.setAgentinstcode(val);
                break;
            case "agentop":
                dp077.setAgentop(val);
                break;
            case "xmqp":
                dp077.setXmqp(val);
                break;
            case "sjhm":
                dp077.setSjhm(val);
                break;
            case "zip":
                dp077.setZip(val);
                break;
            case "jtzz":
                dp077.setJtzz(val);
                break;
            case "jtysr":
                dp077.setJtysr(new BigDecimal(val));
                break;
            case "hyzk":
                dp077.setHyzk(val);
                break;
            case "freeuse1":
                dp077.setFreeuse1(val);
                break;
            case "freeuse2":
                dp077.setFreeuse2(new BigDecimal(val));
                break;
            case "freeuse3":
                dp077.setFreeuse3(GgUtil.strToDate(val, IshConstants.GG_DATE_FORMAT));
                break;
            case "freeuse4":
                dp077.setFreeuse4(Integer.valueOf(val));
                break;
        }
    }
    //无表头数据获取
    public HashMap<String, Object> query(Map<String ,String > map,String tmp) {
        String[] colValues = null;
        HashMap<String, Object> data = new HashMap();
        colValues = tmp.split("~");
        int indexTmp = 0;
        for(Map.Entry<String, String> vo : map.entrySet()){
            String key = vo.getKey();
            data.put(map.get(key).toString(), colValues.length > indexTmp ? colValues[indexTmp] : "");
            indexTmp++;
        }

        return data;
    }
    public static boolean isTableHead(String head) {
        String regex = "[a-zA-Z~]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(head);
        return matcher.matches();
    }

}
