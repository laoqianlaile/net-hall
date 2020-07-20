package com.yd.ish.biz.comp.common.common;

import com.yd.biz.config.CompBean;
import com.yd.biz.exception.BatchExceptionBean;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.GetDownFileMap;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.org.util.CommonErrorCode;
import com.yd.svrplatform.comm_mdl.dataswap.file.FileSwap;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.ReadProperty;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 名称：CompBatchGetTransaction.java
 * <p>功能：贷款试算  batchget 贷款试算返回的错误信息是批量的，但是页面没有批量列表，所以单独处理，以单笔异常信息方式throw <br>
 *
 * @author 林楠
 * @version 0.1    2018年9月4日	林楠创建
 */
@Component("CompDkss")
public class CompDkss extends AbsCompBatchTransaction {
    /**
     * 读核心返回文件编码格式
     */
    private static final String ENCODING = ReadProperty.getString("readfile_encoding_bsp");
    @Autowired
    DP077Service dp077service;
    @Autowired
    GetDownFileMap getDownFileMap;
    /**
     * 取错误码
     */
    private static final String ERROR_PLYC = ReadProperty.getString("recode_batcherr");
    
    @Override
    public int execute() {
        /*入口参数赋值开始*/

        CompBean compBean = this.getCompBean();
        String apiId = getString(apiIdFiled());
        String apiName = getString(apiNameField(), apiId);
        String  fileField = getString(fileField());

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
        if (ERROR_PLYC.equals(head.getParticular_code())){
        	 // 从body中获取文件名
            Map<String, Object> body = data.getBody();
            String filename = super.getMainContext().getDataPool().getString("filename")+".err";
            if (body.containsKey(fileField)) {
                filename = String.valueOf(body.get(fileField));
            }
            if (!StringUtils.isEmpty(filename)) {
            	File file = FileSwap.getFile(filename);
            	String error_msg="";
                if (file != null&&file.exists()) {
                    List<String> fileList;
                    // 若批量错误文件存在，读取错误信息
                    LOGGER.info("[+]读取批量错误信息开始");
                    try {
                        fileList = FileUtils.readLines(file, ENCODING);
                        String strLine;
                        BatchExceptionBean bean;
                        if (fileList != null) {
                            for (int i = 0; i < fileList.size(); i++) {
                                strLine = fileList.get(i);
                                //错误文件返回的错误信息格式为  A~B~C 例如  370883198412083952~邢令彬~有未结清贷款不能再次贷款
                                error_msg+=strLine.split(FILE_SEPARATORS)[2]+",";
                            }
                        }
                        LOGGER.info("[-]读取批量错误信息结束");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        throw new TransSingleException(CommonErrorCode.ERROR_WJDQSB, "文件名：" + file.getPath());
                    }
                }else{
                    LOGGER.error("批量错误文件不存在，文件名：" + filename);
                    throw new TransSingleException("批量错误文件不存在，文件名：" + filename);
                }
                LOGGER.debug("error_msg----------"+error_msg);
                if (!com.alibaba.druid.util.StringUtils.isEmpty(error_msg)) {
//                    TransBatchException e = new TransBatchException(errlist, "0");
                	throw new TransSingleException(error_msg);
                }
            }
        }else if(!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())){
        	throw new TransSingleException(head.getParticular_info());
        } else {
            /*出口参数赋值开始*/
            setCompOutParams( data , compBean , apiId);
            /*出口参数赋值结束*/
        }
        return 0;
    }
}
