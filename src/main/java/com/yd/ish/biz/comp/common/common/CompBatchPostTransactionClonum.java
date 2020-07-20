package com.yd.ish.biz.comp.common.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.biz.config.CompBean;
import com.yd.biz.exception.BatchExceptionBean;
import com.yd.biz.exception.TransBatchException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.ish.util.GgUtil;
import com.yd.org.util.CommonErrorCode;
import com.yd.svrplatform.comm_mdl.dataswap.file.FileSwap;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.ReadProperty;

/**
 * 名称：CompBatchPostTransaction.java
 * <p>功能：批量文件待表格头 <br>
 *
 * @author 林楠
 * @version 0.1    2018年9月4日	林楠创建
 */
@Component("CompBatchPostTransactionClonum")
public class CompBatchPostTransactionClonum extends AbsCompBatchTransaction {

    @Autowired
    DP077Service dp077service;

    /**
     * 上传文件本地路径
     */
    private static final String FILE_SWAP_PATH = ReadProperty.getString("file_swap_path");
    /**
     * 文件格式
     */
    private static final String FILE_FORMAT = ReadProperty.getString("file_format");
    /**
     * 数据分割符
     */
    private static final String FILE_SEPARATORS = ReadProperty.getString("file_separators");
    /**
     * 取错误码
     */
    private static final String ERROR_PLYC = ReadProperty.getString("recode_batcherr");
    /**
     * 读核心返回文件编码格式
     */
    private static final String ENCODING = ReadProperty.getString("readfile_encoding_bsp");

    @Override
    public int execute() {
        /* 入口参数赋值开始 */
        // 获取查询交易码
        CompBean compBean = this.getCompBean();
        String apiId = getString(apiIdFiled());
        String apiName = getString(apiNameField(), apiId);
        if (StringUtils.isEmpty(apiId)) {
            throw new TransSingleException(CommonErrorCode.ERROR_NOT_NULL, "交易码");
        }
        //CompBean compBean = this.getCompBean();
        //HashMap<String, ParamBean> inParams = compBean.getInParams();
        // 上传文件名取默认值
        String fileField = DEFAULT_FILE_FIELD;
        //// 组件上传参数规定了上传文件字段名的情况 从上传参数中获取
        //if (inParams != null && inParams.containsKey(fileField())) {
        fileField = getString(fileField());
       // }
        // 实例号
        int instance = getInt("_IS");
        String operid = getString("_OPERID");
        //initDatapool(inParams);
        /* 入口参数赋值结束 */

        // 根据实例号查临时表，获取批量信息
        List<DP077> list = dp077service.selectBySlh(instance);
        LOGGER.info("[+]生成上传文件开始");
        // 定义生成文件名称
        String wjmc = operid + "01_" + instance + new Date().getTime();
        // 文件全路径及文件名
        String filename = getFilename(FILE_SWAP_PATH, "", wjmc);
        String filenameBak = filename ;
        // 解析上传文件字段定义
        Map<String, String> fileColDef = getFileColDef();
        // 查询结果不为空，将查询结果写入文件
        if (list != null && list.size() > 0) {
            File file = new File(filename);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
//            	FileWriter fw = new FileWriter(file.getAbsolutePath(), false);
            	PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsolutePath()),"GBk")));
                // 第一行写入列名
                writeFileHead1(fileColDef, out);
                out.write(System.lineSeparator());
                // 循环写入文件信息
                for (int i = 0; i < list.size(); i++) {
                	writeFileByDp0771(fileColDef, out, list.get(i));
                    if (i < list.size() - 1) {
                        out.write(System.lineSeparator());
                    }
                    if (i % 1000 == 0)
                    	out.flush();
                }
                out.flush();
                // 将文件信息赋值给接口中设置的变量filename
                this.setValue(fileField, wjmc);
                LOGGER.info("[-]生成上传文件结束" + file.getAbsolutePath());
            } catch (RuntimeException | IOException e) {
                LOGGER.error("生成上传文件出错：" + e.getMessage(), e);
                throw new TransSingleException(CommonErrorCode.ERROR_WJXRSB, "文件名：" + file.getPath());
            }
        }

        LOGGER.info("[+]调用交易{}开始", apiName);

        // 调用接口提交批量信息
        XmlResObj data = this.sendExternal(apiId);
        XmlResHead head = data.getHead();

        // 若返回码为批量错误码，获取批量错误文件
        if (ERROR_PLYC.equals(head.getParticular_code())) {
            // 正式文件名为上传文件名.err
            String errfilename  = filenameBak + ".err";
            String batchDataListId = getString(batchDataListId(),"dataList");

            File errfile = FileSwap.getFile(errfilename);
            List<BatchExceptionBean> errlist = new ArrayList<BatchExceptionBean>();
            List<String> fileList;
            // 若批量错误文件存在，读取错误信息
            if (errfile.exists()) {
                LOGGER.info("[+]读取批量错误信息开始");
                try {
                    fileList = FileUtils.readLines(errfile, ENCODING);
                    String strLine;
                    BatchExceptionBean bean;
                    if (fileList != null) {
                        for (int i = 0; i < fileList.size(); i++) {
                            strLine = fileList.get(i);
                            System.out.println(strLine);
                            // 组装将要抛出的批量错误
                            bean = new BatchExceptionBean(
                                    i + 1,
                                    strLine.split(FILE_SEPARATORS)[0],
                                    strLine.split(FILE_SEPARATORS)[1],
                                    strLine.split(FILE_SEPARATORS)[2],
                                    "0",
                                    strLine.split(FILE_SEPARATORS)[3].split("=")[0],
                                    batchDataListId,
                                    strLine.split(FILE_SEPARATORS)[3].split("=")[1],

                                    "",
                                    "",
                                    "",
                                    "");
                            errlist.add(bean);
                        }
                    }
                    LOGGER.info("[-]读取批量错误信息结束");
                } catch (IOException e1) {
                    e1.printStackTrace();
                    throw new TransSingleException(CommonErrorCode.ERROR_WJDQSB, "文件名：" + errfile.getPath());
                }
            } else {
                LOGGER.error("批量错误文件不存在，文件名：" + errfilename);
                throw new TransSingleException("批量错误文件不存在，文件名：" + errfilename);
            }
            if (errlist != null && errlist.size() > 0) {
                TransBatchException e = new TransBatchException(errlist, "0");
                e.commit();
                throw e;
            }
        } else if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
            LOGGER.error("普通接口异常：" + head.getParticular_info());
            throw new TransSingleException(head.getParticular_code(), head.getParticular_info());
        }
        LOGGER.info("[-]调用交易{}结束", apiName);

        /* 出口参数赋值开始 */
        /*出口参数赋值开始*/
        setCompOutParams( data , compBean , apiId);
            /*出口参数赋值结束*/
        /* 出口参数赋值结束 */

        return 0;
    }

    /**
     * 写入文件标题
     *
     * @param fileColDef 文件列定义
     * @param fw         文件内容
     */
    private void writeFileHead(Map<String, String> fileColDef, FileWriter fw) throws IOException {
        fw.write(StringUtils.join(fileColDef.keySet().iterator(), FILE_SEPARATORS));
    }
    /**
     * 写入文件标题
     *
     * @param fileColDef 文件列定义
     * @param pw         文件内容
     */
    private void writeFileHead1(Map<String, String> fileColDef, PrintWriter pw) throws IOException {
        pw.write(StringUtils.join(fileColDef.keySet().iterator(), FILE_SEPARATORS));
    }
    /**
     * 临时表实体写入文件
     *
     * @param fileColDef 文件列定义
     * @param fw         文件内容
     * @param dp077      临时表实体 
     */
    private void writeFileByDp077(Map<String, String> fileColDef, FileWriter fw, DP077 dp077) throws IOException {
        Iterator it = fileColDef.entrySet().iterator();
        List<String> fileCols = new ArrayList<String>();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            fileCols.add(getfileCol(dp077, e.getValue()+""));
        }
        fw.write(StringUtils.join(fileCols.iterator(), FILE_SEPARATORS));
    }
    private void writeFileByDp0771(Map<String, String> fileColDef, PrintWriter pw, DP077 dp077) throws IOException {
        Iterator it = fileColDef.entrySet().iterator();
        List<String> fileCols = new ArrayList<String>();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            fileCols.add(getfileCol(dp077, e.getValue()+""));
        }
        pw.write(StringUtils.join(fileCols.iterator(), FILE_SEPARATORS));
    }
    /**
     * 根据文件列定义获取临时表实体字段值
     *
     * @param dp077 临时表实体
     * @param key   临时表实体字段名
     * @return
     */
    private String getfileCol(DP077 dp077, String key) {
        String fileCol = "";
        switch (key) {
            case "seqno":
                fileCol = dp077.getSeqno()+"";
                break;
            case "dpbusitype":
                fileCol = dp077.getDpbusitype();
                break;
            case "transdate":
                fileCol = GgUtil.dateToStr(dp077.getTransdate(), "yyyy-MM-dd");
                break;
            case "unitaccnum1":
                fileCol = dp077.getUnitaccnum1();
                break;
            case "unitaccnum2":
                fileCol = dp077.getUnitaccnum2();
                break;
            case "unitaccname1":
                fileCol = dp077.getUnitaccname1();
                break;
            case "unitaccname2":
                fileCol = dp077.getUnitaccname2();
                break;
            case "accnum1":
                fileCol = dp077.getAccnum1();
                break;
            case "accnum2":
                fileCol = dp077.getAccnum2();
                break;
            case "accname1":
                fileCol = dp077.getAccname1();
                break;
            case "accname2":
                fileCol = dp077.getAccname2();
                break;
            case "sex":
                fileCol = dp077.getSex();
                break;
            case "birthday":
                fileCol = GgUtil.dateToStr(dp077.getBirthday(), "yyyy-MM-dd");
                break;
            case "certitype":
                fileCol = dp077.getCertitype();
                break;
            case "certinum":
                fileCol = dp077.getCertinum();
                break;
            case "basenum":
                fileCol = dp077.getBasenum()+"";
                break;
            case "unitprop":
                fileCol = dp077.getUnitprop()+"";
                break;
            case "indiprop":
                fileCol = dp077.getIndiprop()+"";
                break;
            case "amt1":
                fileCol = dp077.getAmt1()+"";
                break;
            case "amt2":
                fileCol = dp077.getAmt2()+"";
                break;
            case "peoplenum":
                fileCol = dp077.getPeoplenum()+"";
                break;
            case "begdate":
                fileCol =  GgUtil.dateToStr(dp077.getBegdate(), "yyyy-MM-dd");
                break;
            case "enddate":
                fileCol =  GgUtil.dateToStr(dp077.getEnddate(), "yyyy-MM-dd");
                break;
            case "reason":
                fileCol = dp077.getReason();
                break;
            case "onym":
                fileCol = dp077.getOnym();
                break;
            case "begym":
                fileCol = dp077.getBegym();
                break;
            case "endym":
                fileCol = dp077.getEndym();
                break;
            case "fundsouflag":
                fileCol = dp077.getFundsouflag();
                break;
            case "proptype":
                fileCol = dp077.getProptype();
                break;
            case "payvounum":
                fileCol = dp077.getPayvounum();
                break;
            case "payvouamt":
                fileCol = dp077.getPayvouamt()+"";
                break;
            case "payvoubank":
                fileCol = dp077.getPayvoubank();
                break;
            case "instance":
                fileCol = dp077.getInstance()+"";
                break;
            case "agentinstcode":
                fileCol = dp077.getAgentinstcode();
                break;
            case "agentop":
                fileCol = dp077.getAgentop();
                break;
            case "xmqp":
                fileCol = dp077.getXmqp();
                break;
            case "sjhm":
                fileCol = dp077.getSjhm();
                break;
            case "zip":
                fileCol = dp077.getZip();
                break;
            case "jtzz":
                fileCol = dp077.getJtzz();
                break;
            case "jtysr":
                fileCol = dp077.getJtysr()+"";
                break;
            case "hyzk":
                fileCol = dp077.getHyzk();
                break;
            case "freeuse1":
                fileCol = dp077.getFreeuse1()+"";
                break;
            case "freeuse2":
                fileCol = dp077.getFreeuse2()+"";
                break;
            case "freeuse3":
                fileCol = GgUtil.dateToStr(dp077.getFreeuse3(), "yyyy-MM-dd");
                break;
            case "freeuse4":
                fileCol = dp077.getFreeuse4()+"";
                break;
        }
        return fileCol;
    }
}
