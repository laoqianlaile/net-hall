package com.yd.ish.biz.comp.ln;

import com.yd.biz.exception.BatchExceptionBean;
import com.yd.biz.exception.TransBatchException;
import com.yd.biz.exception.TransOtherException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.FileUtil;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.org.util.CommonErrorCode;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.dataswap.file.FileSwap;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.ReadProperty;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 名称：CompXMXQ05
 * <p>功能：提交开发商项目新签信息<br>
 *
 * @brief 提交开发商项目新签信息
 * @author 柏慧敏
 * @version V0.1 柏慧敏 20190606 长春 新建
 * @note
 */
@Component("CompXMXQ05")
public class CompXMXQ05 extends BaseComp {

    private static final Logger logger = LoggerFactory.getLogger(CompXMXQ05.class);
    /**
     * 上传文件本地路径
     */
    private static final String file_swap_path = ReadProperty.getString("file_swap_path");
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
     * 读核心返回文件编码格式
     */
    private static final String encoding = ReadProperty.getString("readfile_encoding_bsp");

    @Autowired
    DP077Service dp077service;

    @Override
    public int execute() {

        /*入口参数赋值开始*/
        int instance = getInt("_IS");// 实例号
        String operid = getString("_OPERID");// 柜员号
        /*入口参数赋值结束*/
        // 生成上传文件（楼栋列表文件，楼盘列表文件）
        String ldfile = creatLdFile(instance, operid);
        String lpfile = creatLpFile(instance, operid);
        super.setValue("filename", ldfile + "," + lpfile);
        String[] filenames = {ldfile, lpfile};
        // 提交开发商项目新签信息
        XmlResObj data = super.sendExternal("BSP_LN_TJXQXX_01");
        XmlResHead head = data.getHead();
        // 若返回码为批量错误码，获取批量错误文件
        if (ERROR_PLYC.equals(head.getParticular_code())) {
            String errfilename;
            File errfile;
            List<BatchExceptionBean> errlist = new ArrayList<BatchExceptionBean>();
            List<String> fileList = null;
            String strLine;
            // 正式文件名为上传文件名.err
            for (int i = 0; i < filenames.length; i++) {
                if (!"".equals(filenames[i])) {
                     errfilename = filenames[i] + ".err";
                    //本地测试使用下面代码
//                    errfilename = "errmessage" + i + ".txt.err";
                    errfile = FileSwap.getFile(errfilename);
                    // 若批量错误文件存在，读取错误信息
                    if (errfile != null && errfile.exists()) {
                        logger.info("[+]读取批量错误信息开始");
                        //若为楼栋信息的错误文件
                        if (ldfile.equals(filenames[i])) {
                            try {
                                fileList = FileUtils.readLines(errfile, encoding);
                                if (fileList != null) {
                                    BatchExceptionBean bean;
                                    for (int j = 0; j < fileList.size(); j++) {
                                        strLine = fileList.get(j);
                                        // 组装将要抛出的批量错误
                                        bean = new BatchExceptionBean(Integer.parseInt("01" + j + 1),
                                                strLine.split(file_separators)[0], strLine.split(file_separators)[1],
                                                strLine.split(file_separators)[2], "0",
                                                strLine.split(file_separators)[3].split("=")[0], "ldlist",
                                                strLine.split(file_separators)[3].split("=")[1], "", "", "", "");
                                        errlist.add(bean);
                                    }
                                }
                            } catch (IOException e) {
                                logger.error("文件读取失败，文件名：" + errfile.getPath());
                                e.printStackTrace();
                                throw new TransSingleException(CommonErrorCode.ERROR_WJDQSB, "");
                            }

                        }
                        //若为楼盘信息错误文件
                        if (lpfile.equals(filenames[i])) {
                            try {
                                fileList = FileUtils.readLines(errfile, encoding);
                                if (fileList != null) {
                                    BatchExceptionBean bean;
                                    for (int j = 0; j < fileList.size(); j++) {
                                        strLine = fileList.get(j);
                                        // 组装将要抛出的批量错误
                                        bean = new BatchExceptionBean(Integer.parseInt("02" + j + 1), strLine.split(file_separators)[0], strLine.split(file_separators)[1], strLine.split(file_separators)[2],
                                                "0", strLine.split(file_separators)[3].split("=")[0] +"," +strLine.split(file_separators)[4].split("=")[0] +"," + strLine.split(file_separators)[5].split("=")[0]
                                                +"," + strLine.split(file_separators)[6].split("=")[0], "lplist", strLine.split(file_separators)[3].split("=")[1],
                                                strLine.split(file_separators)[4].split("=")[1], strLine.split(file_separators)[5].split("=")[1], strLine.split(file_separators)[6].split("=")[1], "");
                                        errlist.add(bean);
                                    }
                                }
                                logger.info("[-]读取批量错误信息结束");
                            } catch (IOException e1) {
                                logger.error("文件读取失败，文件名：" + errfile.getPath());
                                e1.printStackTrace();
                                throw new TransSingleException(CommonErrorCode.ERROR_WJDQSB, "");
                            }
                        }
                    } else {
                        logger.error("批量错误文件不存在，文件名：" + errfilename);
                        throw new TransSingleException("批量错误文件不存在。");

                    }
                }
            }
            //若错误信息不为空，抛出批量错误信息
            if (errlist != null && errlist.size() > 0) {
                TransBatchException e = new TransBatchException(errlist, "0");
                e.commit();
                throw e;
            }
        } else if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
            throw new TransSingleException(head.getParticular_info());
        } else {
            // 调用接口成功，删除临时表中的数据
            dp077service.deleteBySlh(instance);
        }
        /*出口参数赋值开始*/

        /*出口参数赋值结束*/

        return 0;
    }

    /**
     * 生成楼栋文件
     *
     * @param instance 实例号
     * @param operid   操作员
     * @return 文件名（含路径）
     */
    private String creatLdFile(int instance, String operid) {
        FileWriterWithEncoding fw = null;
        File file = null;
        // 返回结果
        String returnresult;
        // 定义日期格式yyyyMMddHHmmss
        SimpleDateFormat sdf = new SimpleDateFormat(IshConstants.GG_DATETIME_FORMATWFG);
        // 定义日期格式yyyy-MM-dd
        SimpleDateFormat sdfnyr = new SimpleDateFormat(IshConstants.GG_DATE_FORMAT);
        // 定义文件名称
        String wjmc = instance + "01_" + operid + sdf.format(new Date());
        // 根据实例号和类型查临时表，获取当前的借款申请人信息
        DP077 dp077 = new DP077();
        dp077.setInstance(instance);
        dp077.setDpbusitype("03");// 楼栋信息
        List<DP077> list = dp077service.selectByCause(dp077);
        logger.info("[+]生成上传文件开始");
        // 文件全路径及文件名
        String filename = FileUtil.getFilename(file_swap_path, file_format, wjmc);

        try {
            // 查询结果不为空，将查询结果写入文件
            if (list != null && list.size() > 0) {
                file = new File(filename);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                fw = new FileWriterWithEncoding(file.getAbsolutePath(), encoding, false);
                String khrq = "";
                // 第一行写入列名
                fw.write("ldbh" + file_separators + "ysxkzh" + file_separators + "skyh" + file_separators + "yhzhmc"
                        + file_separators + "yhzh" + file_separators + "zcs");
                fw.write(System.lineSeparator());
                // 循环写入文件信息
                for (int i = 0; i < list.size(); i++) {
                    fw.write(list.get(i).getAccnum1() + file_separators + list.get(i).getAccnum2() + file_separators
                            + list.get(i).getAgentinstcode() + file_separators + list.get(i).getAccname1()
                            + file_separators + list.get(i).getAccname2() + file_separators
                            + list.get(i).getPeoplenum());
                    if (i < list.size() - 1) {
                        fw.write(System.lineSeparator());
                    }
                    if (i % 1000 == 0)
                        fw.flush();
                }
                fw.flush();
                returnresult = filename;
                logger.info("[-]生成上传文件结束");
            } else {
                logger.info("楼栋信息结果为空");
                returnresult = "";
            }
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            logger.error("生成上传文件出错,文件名：" + file.getPath()+"错误信息：" + e.getMessage(), e);
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
        return returnresult;
    }

    /**
     * 生成楼盘文件
     *
     * @param instance 实例号
     * @param operid   操作员
     * @return 文件名（含路径）
     */
    private String creatLpFile(int instance, String operid) {
        FileWriterWithEncoding fw = null;
        File file = null;
        // 返回结果
        String returnresult;
        // 定义日期格式yyyyMMddHHmmss
        SimpleDateFormat sdf = new SimpleDateFormat(IshConstants.GG_DATETIME_FORMATWFG);
        // 定义日期格式yyyy-MM-dd
        SimpleDateFormat sdfnyr = new SimpleDateFormat(IshConstants.GG_DATE_FORMAT);
        // 定义文件名称
        String wjmc = instance + "02_" + operid + sdf.format(new Date());
        // 根据实例号和类型查临时表，获取当前的借款申请人信息
        DP077 dp077 = new DP077();
        dp077.setInstance(instance);
        dp077.setDpbusitype("04");// 楼盘信息
        List<DP077> list = dp077service.selectByCause(dp077);
        logger.info("[+]生成上传文件开始");
        // 文件全路径及文件名
        String filename = FileUtil.getFilename(file_swap_path, file_format, wjmc);

        try {
            // 查询结果不为空，将查询结果写入文件
            if (list != null && list.size() > 0) {
                file = new File(filename);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                fw = new FileWriterWithEncoding(file.getAbsolutePath(), encoding, false);
                String khrq = "";
                // 第一行写入列名
                fw.write("ldbh" + file_separators + "dyh" + file_separators + "szcs" + file_separators + "mph"
                        + file_separators + "jzmj" + file_separators + "tnmj" + file_separators + "jzxsdj" + file_separators + "fwzj");
                fw.write(System.lineSeparator());
                // 循环写入文件信息
                for (int i = 0; i < list.size(); i++) {
                    fw.write(list.get(i).getAccnum1() + file_separators + list.get(i).getUnitaccnum1() + file_separators
                            + list.get(i).getUnitaccnum2() + file_separators + list.get(i).getUnitaccname1() + file_separators
                            + list.get(i).getUnitprop() + file_separators + list.get(i).getIndiprop() + file_separators
                            + list.get(i).getAmt1() + file_separators + list.get(i).getAmt2());
                    if (i < list.size() - 1) {
                        fw.write(System.lineSeparator());
                    }
                    if (i % 1000 == 0)
                        fw.flush();
                }
                fw.flush();
                returnresult = filename;
                logger.info("[-]生成上传文件结束");
            } else {
                logger.info("楼盘信息结果为空");
                returnresult = "";
            }
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            logger.error("生成上传文件出错,文件名：" + file.getPath()+"错误信息：" + e.getMessage(), e);
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
        return returnresult;
    }
}
