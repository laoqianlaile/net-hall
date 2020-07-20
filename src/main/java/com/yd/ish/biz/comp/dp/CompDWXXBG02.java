package com.yd.ish.biz.comp.dp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.yd.ish.common.util.FileUtil;
import com.yd.org.util.IshConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.BatchExceptionBean;
import com.yd.biz.exception.TransBatchException;
import com.yd.biz.exception.TransOtherException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.GetDownFileMap;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.org.util.CommonErrorCode;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.dataswap.file.FileSwap;
import com.yd.svrplatform.comm_mdl.external.EssFactory;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.jdbc.PersistentBiz;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.ReadProperty;

/**
 * 名称：CompWDXXBG02.java
 * <p>
 * 功能：提交单位信息 <br>
 *
 * @author 王赫
 * @version 0.1 2018年6月6日 王赫创建
 * 0.2 2018年10月30日  柏慧敏修改  可以按照核心要求的编码格式写上传文件
 */
@Component("CompDWXXBG02")
public class CompDWXXBG02 extends BaseComp {

    private static final Logger logger = LoggerFactory.getLogger(CompDWXXBG02.class);

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
     * 上传文件本地路径
     */
    private static final String file_swap_path = ReadProperty.getString("file_swap_path");

    @Autowired
    PersistentBiz persistentBiz;

    @Autowired
    DP077Service dp077Service;

    @Autowired
    GetDownFileMap getDownFileMap;

    @Override
    public int execute() {

        /* 入口参数赋值开始 */
        int instance = getInt("_IS");
        String operid = getString("_OPERID");
        String filename = "";
        /* 入口参数赋值结束 */

        FileWriterWithEncoding fw = null;
        File file = null;

        // 根据实例号查临时表，获取当前的个人封存信息
        List<DP077> list = dp077Service.selectBySlh(instance);

        if (list != null && list.size() > 0) {
            // 校验是否存在相同的个人证件号码
            String zjhm = "";
            for (int i = 0; i < list.size(); i++) {
                // 判断是否存在相同个人证件号码
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(i).getCertinum().toString().equals(list.get(j).getCertinum().toString()) && i != j) {
                        zjhm = list.get(i).getCertinum();
                        logger.error("存在相同的个人账号信息，请确认后重新提交。证件号码：" + zjhm);
                        throw new TransSingleException("存在相同的个证件号码:" + zjhm + "，请确认后重新提交。");
                    }
                }
            }
            try {
                // 生成上传文件名称
                logger.info("[+]生成上传文件开始-------");
                // 定义日期格式yyyy-mm-dd
                SimpleDateFormat sdf = new SimpleDateFormat(IshConstants.GG_DATETIME_FORMATWFG);
                // 定义生成文件名称
                String wjmc = instance + "01_" + operid + sdf.format(new Date());
                // 文件全路径及文件名
                filename = FileUtil.getFilename(file_swap_path, file_format, wjmc);
                file = new File(filename);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                fw = new FileWriterWithEncoding(file.getAbsolutePath(), READFILE_ENCODING_BSP, false);

                // 第一行写入列名
                // jbrxm~jbrzjlx~jbrzjhm~jbrsjhm~jbrgddhhm~jbrdzxx~jbrcz~jbbm~bgbz
                fw.write("jbrxm" + file_separators + "jbrzjlx" + file_separators + "jbrzjhm" + file_separators
                        + "jbrsjhm" + file_separators + "jbrgddhhm" + file_separators + "jbrdzxx" + file_separators
                        + "jbrcz" + file_separators + "jbbm" + file_separators + "bgbz");
                fw.write(System.lineSeparator());
                // 循环写入文件信息
                for (int i = 0; i < list.size(); i++) {
                    fw.write(list.get(i).getAgentop() + file_separators + list.get(i).getCertitype()
                            + file_separators + list.get(i).getCertinum() + file_separators + list.get(i).getSjhm()
                            + file_separators + list.get(i).getPayvounum() + file_separators
                            + list.get(i).getFreeuse1() + file_separators + list.get(i).getJtzz() + file_separators
                            + list.get(i).getAgentinstcode() + file_separators + list.get(i).getReason());
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
            } catch (RuntimeException | IOException e) {
                e.printStackTrace();
                logger.error("生成上传文件出错，文件名：" + file.getPath()+"，错误信息:" + e.getMessage(), e);
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
        }
        logger.info("[+]调用提交单位信息以及经办人信息接口BSP_DP_DWXXBG_01开始!");
        XmlResObj data = super.sendExternal("BSP_DP_DWXXBG_01");
        XmlResHead head = data.getHead();
        if (XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
            logger.info("[-]调用提交单位信息以及经办人信息接口BSP_DP_DWXXBG_01结束,调用成功!");

        } else if (ERROR_PLYC.equals(head.getParticular_code())) {
            // 本地测试自定义批量错误文件名，正式文件名为上传文件名.err
            // String errfilename = "dwxxbgErrMessage" + ".err";
            String errfilename = filename + ".err";
            File errfile = FileSwap.getFile(errfilename);
            List<BatchExceptionBean> errlist = new ArrayList<BatchExceptionBean>();
            List<String> fileList;
            // 若批量错误文件存在，读取错误信息
            if (errfile.exists()) {
                logger.info("[+]读取批量错误信息开始,错误文件：" + errfilename);
                try {
                    fileList = FileUtils.readLines(errfile, READFILE_ENCODING_BSP);
                    if (fileList != null) {
                        for (int i = 0; i < fileList.size(); i++) {
                            String strLine = fileList.get(i);
                            // 组装将要抛出的批量错误
                            BatchExceptionBean bean = new BatchExceptionBean(i + 1, strLine.split(file_separators)[0],
                                    strLine.split(file_separators)[1], strLine.split(file_separators)[2], "1",
                                    strLine.split(file_separators)[3].split("=")[0] + ","
                                            + strLine.split(file_separators)[4].split("=")[0],
                                    "datalist", strLine.split(file_separators)[3].split("=")[1],
                                    strLine.split(file_separators)[4].split("=")[1], "", "", "");
                            errlist.add(bean);
                        }
                    }
                    logger.info("[+]读取批量错误信息结束,错误文件：" + errfilename);
                } catch (IOException e1) {
                    logger.error("读取文件失败，文件路径：" + errfile.getPath());
                    e1.printStackTrace();
                    throw new TransSingleException(CommonErrorCode.ERROR_WJDQSB, "");
                }
            } else {
                logger.error("批量错误文件不存在，文件名：" + errfilename);
                throw new TransSingleException("批量错误文件不存在");
            }
            if (errlist != null && errlist.size() > 0) {
                TransBatchException e = new TransBatchException(errlist, "0");
                e.commit();
                throw e;
            }
        } else if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
            logger.error("普通接口异常：" + head.getParticular_info());
            throw new TransSingleException(head.getParticular_code(), head.getParticular_info());
        }

        /* 出口参数赋值开始 */

        /* 出口参数赋值结束 */

        return 0;
    }
}
