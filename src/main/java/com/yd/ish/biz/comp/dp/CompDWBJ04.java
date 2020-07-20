package com.yd.ish.biz.comp.dp;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.BatchExceptionBean;
import com.yd.biz.exception.TransBatchException;
import com.yd.biz.exception.TransOtherException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.FileUtil;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.org.util.CommonErrorCode;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.dataswap.file.FileSwap;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.ReadProperty;
import com.yd.workflow.util.Constants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 名称：CompDWBJ04.java
 * <p>功能：单位补缴-提交 <br>
 *
 * @author 王赫
 * @version 0.1    2018年9月17日	王赫创建
 * 0.2 2018年10月30日  柏慧敏修改  可以按照核心要求的编码格式写上传文件
 */
@Component("CompDWBJ04")
public class CompDWBJ04 extends BaseComp {

    private static final Logger logger = LoggerFactory.getLogger(CompDWBJ04.class);

    /**
     * 文件格式
     */
    private static final String file_format = ReadProperty.getString("file_format");
    /**
     * 数据分割符
     */
    private static final String file_separators = ReadProperty.getString("file_separators");
    /**
     * 取错误码
     */
    private static final String ERROR_PLYC = ReadProperty.getString("recode_batcherr");
    /**
     * 编码格式
     */
    private static final String READFILE_ENCODING_BSP = ReadProperty.getString("readfile_encoding_bsp");
    /**
     * 启用凭证系统标记
     **/
    private static final String pzsystemFlag = ReadProperty.getString("pzsystem.use");
    /**
     * 上传文件本地路径
     */
    private static final String file_swap_path = ReadProperty.getString("file_swap_path");
    @Autowired
    DP077Service dp077service;

    @Override
    public int execute() {
        /*入口参数赋值开始*/
        String zchkyye = getString("zchkyye");//暂存户可用余额
        String sjje = getString("sjje");//实缴金额
        String zchzcje = getString("zchzcje");//暂存户转出金额
        int instance = getInt("_IS");
        String operid = getString("_OPERID");
        /*入口参数赋值结束*/
        MainContext mainContext = MainContext.currentMainContext();
        DataPool dataPool = mainContext.getDataPool();
        dataPool.put("zchkyye", zchkyye.replaceAll(",", ""));
        dataPool.put("sjje", sjje.replaceAll(",", ""));
        dataPool.put("zchzcje", zchzcje.replaceAll(",", ""));

        // 查询动态列表临时数据
        FileWriterWithEncoding fw = null;
        File file = null;


        // 根据实例号查临时表，获取单位补缴人员信息
        List<DP077> list = dp077service.selectBySlh(instance);
        // 生成上传文件名称
        logger.info("[+]生成上传文件开始-------");
        // 定义日期格式yyyy-mm-dd
        SimpleDateFormat sdf = new SimpleDateFormat(IshConstants.GG_DATETIME_FORMATWFG);
        // 定义生成文件名称
        String wjmc = instance + "01_" + operid + sdf.format(new Date());
        // 文件全路径及文件名
        String filename = FileUtil.getFilename(file_swap_path, file_format, wjmc);
        try {
            if (list != null && list.size() > 0) {
                file = new File(filename);
                logger.info("生成文件名称:{}", filename);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                fw = new FileWriterWithEncoding(file.getAbsolutePath(), READFILE_ENCODING_BSP, false);
                // 第一行写入列名
                /*
                 * grzh~xingming~zjhm~ksny~jzny~bjyy~bjje
                 * */
                fw.write("grzh" + file_separators + "xingming" + file_separators + "zjhm" + file_separators + "ksny" + file_separators
                        + "jzny" + file_separators + "bjyy" + file_separators + "bjje");
                fw.write(System.lineSeparator());
                // 循环写入文件信息
                for (int i = 0; i < list.size(); i++) {
                    fw.write(list.get(i).getAccnum1() + file_separators + list.get(i).getAccname1() + file_separators
                            + list.get(i).getCertinum() + file_separators + list.get(i).getUnitaccnum1() + file_separators
                            + list.get(i).getUnitaccnum2() + file_separators + list.get(i).getReason() + file_separators
                            + list.get(i).getAmt1());
                    if (i < list.size() - 1) {
                        fw.write(System.lineSeparator());
                    }
                    if (i % 1000 == 0)
                        fw.flush();
                }
                fw.flush();
                // 将文件信息赋值给接口中设置的变量filename
                super.setValue("filename", filename);
                logger.info("[-]生成上传文件结束-------");
            }
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            logger.error("生成上传文件出错,文件名："+ file.getPath()+"错误信息：" + e.getMessage(), e);
            throw new TransSingleException(CommonErrorCode.ERROR_WJXRSB, "");
        } finally {
            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
                logger.error("关闭文件出错：" + e1.getMessage(), e1);
                throw new TransOtherException("系统错误，请查看日志！");
            }
        }

        //开始调用接口BSP_DP_DWBJTJ_01获取单位补缴信息
        logger.info("[+] 调用BSP_DP_DWBJTJ_01接口提交补缴信息开始!");
        XmlResObj data = super.sendExternal("BSP_DP_DWBJTJ_01");
        XmlResHead head = data.getHead();
        Map<String, Object> body = null;
        //收款银行账号
        String skyhzh = "";
        //收款银行名称
        String skyhmc = "";
        //经办机构
        String jbjg = "";
        //单位补缴金额
        String dwjcje = "";
        //个人补缴金额
        String grjcje = "";
        //入账流水号
        String rzlsh = "";
        //开始年月
        String ksny = "";
        //登记号
        String djh = "";
        //凭证文件名称
        String pzfilename = "";
        if (XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
            body = data.getBody();
            skyhzh = body.get("skyhzh") == null ? "" : body.get("skyhzh").toString();
            skyhmc = body.get("skyhmc") == null ? "" : body.get("skyhmc").toString();
            jbjg = body.get("jbjg") == null ? "" : body.get("jbjg").toString();
            dwjcje = body.get("dwjcje") == null ? "" : body.get("dwjcje").toString();
            grjcje = body.get("grjcje") == null ? "" : body.get("grjcje").toString();
            rzlsh = body.get("jbjg") == null ? "" : body.get("rzlsh").toString();
            ksny = body.get("ksny") == null ? "" : body.get("ksny").toString();
            djh = body.get("djh") == null ? "" : body.get("djh").toString();
            pzfilename = body.get("filename") == null ? "" : body.get("filename").toString();
            logger.debug("接口调用成功");
        } else if (ERROR_PLYC.equals(head.getParticular_code())) {
            // 本地测试自定义批量错误文件名，正式文件名为上传文件名.err
            String errfilename = filename + ".err";
//            String errfilename = "bjerrMessage" + ".err";
            File errfile = FileSwap.getFile(errfilename);
            List<BatchExceptionBean> errlist = new ArrayList<BatchExceptionBean>();
            List<String> fileList;
            //若批量错误文件存在，读取错误文件
            if (errfile.exists()) {
                logger.info("[+]读取批量错误文件开始-----");
                try {
                    fileList = FileUtils.readLines(errfile, READFILE_ENCODING_BSP);
                    if (fileList != null) {
                        for (int i = 0; i < fileList.size(); i++) {
                            String strLine = fileList.get(i);
                            String[] strArray = strLine.split(file_separators);
                            //组装交易异常
                            BatchExceptionBean bean = new BatchExceptionBean(i + 1, strArray[0], strArray[1], strArray[2], "1",
                                    strArray[3].split("=")[0] /*+ "," + strArray[4].split("=")[0]*/, "datalist1", strArray[3].split("=")[1],
                                    /*strArray[4].split("=")[1]*/"", "", "", "");
                            errlist.add(bean);
                        }
                    }
                    logger.debug("[-]读取批量错误信息结束------");
                } catch (IOException e) {
                    logger.error("读取文件失败，文件路径：" + errfile.getPath());
                    e.printStackTrace();
                    throw new TransSingleException(CommonErrorCode.ERROR_WJDQSB, "");
                }
            } else {
                logger.debug("批量错误文件不存在，文件名：" + errfilename);
                throw new TransSingleException("批量错误文件不存在");
            }
            if (errlist != null && errlist.size() > 0) {
                TransBatchException e = new TransBatchException(errlist, "0");
                e.commit();
                throw e;
            }

        } else if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
            logger.debug("普通接口异常：" + head.getParticular_info());
            throw new TransSingleException(head.getParticular_code(), head.getParticular_info());
        }
        logger.info("[-] 调用BSP_DP_DWBJTJ_01接口提交补缴信息结束!");
        /*出口参数赋值开始*/
        setOutParam("skyhzh", skyhzh);//收款银行账号
        setOutParam("skyhmc", skyhmc);//收款银行名称
        setOutParam("jbjg", jbjg);//经办机构
        setOutParam("dwjcje", dwjcje);//单位汇缴金额
        setOutParam("grjcje", grjcje);//个人汇缴金额
        setOutParam("rzlsh", rzlsh);//入账流水号
        setOutParam("ksny", ksny);//开始年月
        setOutParam("djh", djh);//登记号
        if ("1".equals(pzsystemFlag)) { //启用凭证系统时设置出口参数
            setOutParam("filename", pzfilename);//凭证文件名称
        }
        /*出口参数赋值结束*/

        return 0;
    }
}
