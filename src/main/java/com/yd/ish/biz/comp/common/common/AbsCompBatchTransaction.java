package com.yd.ish.biz.comp.common.common;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.config.CompBean;
import com.yd.biz.config.ParamBean;
import com.yd.biz.exception.TransSingleException;
import com.yd.org.util.CommonErrorCode;
import com.yd.org.util.IshConstants;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.ReadProperty;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 名称：AbsCompBatchTransaction.java
 * <p>功能：通用批量提交交易基类 <br>
 *
 * @author 林楠
 * @version 0.1    2018年9月4日	林楠创建
 */
public abstract class AbsCompBatchTransaction extends BaseComp {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    /**
     * 字段数据分割符
     */
    protected static final String FILE_FILED_SEPARATORS = ",";
    /**
     * key-value分割符
     */
    protected static final String FILE_KV_SEPARATORS = ":";
    /**
     * 数组总线取值分割符
     */
    protected static final String FILE_DATAPOOL_SEPARATORS = "=";
    /**
     * 数据分割符
     */
    protected static final String FILE_SEPARATORS = ReadProperty.getString("file_separators");
    /**
     * 编码格式
     */
    protected static final String READFILE_ENCODING_BSP = ReadProperty.getString("readfile_encoding_bsp");
    /**
     * 数据库类型
     **/
    protected static final String DB_TYPE = ReadProperty.getString("dbType");
    /**
     * 日期格式化
     */
    protected static final SimpleDateFormat SDF = new SimpleDateFormat(IshConstants.GG_DATE_FORMAT);

    /**
     * 时间戳格式化
     */
    protected static final SimpleDateFormat SDFSSS = new SimpleDateFormat(IshConstants.GG_DATE_FORMAT_SJC);
    /**
     * 文件名默认字段名
     */
    protected static final String DEFAULT_FILE_FIELD = "filename";


    /**
     * 出口默认赋值方式
     */
    protected static final String  DEFAULT_APISETOUTTYPE = "1";


    /**
     * 文件列定义缓存
     */
    private static Map<String, Map<String, String>> fileColDefsCache = new HashMap<String, Map<String, String>>();



    public static String getFilename(String filePath, String suffix, String... args) {
        if(!filePath.endsWith("/")) {
            filePath = filePath + "/";
        }

        String filename = "";

        for(int i = 0; i < args.length; ++i) {
            if(i != 0) {
                filename = filename + "_";
            }

            filename = filename + args[i];
        }

        filename = filename + suffix;
        return filePath + filename;
    }

    protected Map<String, String> getFileColDef() {
        String fileCols = getString(fileColsField());
        LOGGER.info("下传文件字段定义：{}", fileCols);
        if (StringUtils.isEmpty(fileCols)) {
            throw new TransSingleException(CommonErrorCode.ERROR_NOT_NULL, "下传文件字段定义");
        }

        Map<String, String> fileColMap = new LinkedHashMap<String, String>();
        if (fileColDefsCache.containsKey(fileCols)) {
            fileColMap = fileColDefsCache.get(fileCols);
        } else {
            String[] fileColArray = fileCols.split(FILE_FILED_SEPARATORS);
            for (String fileCol : fileColArray) {
                if (fileCol.contains(FILE_KV_SEPARATORS)) {
                    String[] kv = fileCol.split(FILE_KV_SEPARATORS);
                    fileColMap.put(kv[0], kv[1]);
                } else {
                    fileColMap.put(fileCol, fileCol);
                }
            }
            fileColDefsCache.put(fileCols, fileColMap);
        }
        return fileColMap;
    }

    /**
     * 从入参获取交易码使用的字段名,默认值:apiId
     *
     * @return 交易码字段名
     */
    public String apiIdFiled() {
        return "apiId";
    }

    /**
     * 从入参获取交易描述使用的字段名,默认值:apiName
     *
     * @return 交易描述字段名
     */
    public String apiNameField() {
        return "apiName";
    }

    /**
     * 从入参获取交易出口赋值方式,默认值:apiSetOutType
     *
     * @return 出口赋值方式字段名
     */
    public String apiSetOutType() {
        return "apiSetOutType";
    }

    /**
     * 从入参获取交易出口错误返回DATALIST名称
     *
     * @return
     */
    public String batchDataListId() {
        return "batchDataListId";
    }

    /**
     * 从入参获取批量文件字段定义使用的字段名,默认值:fileCols
     *
     * @return 批量文件字段定义字段名
     */
    public String fileColsField() {
        return "fileCols";
    }

    /**
     * 从入参获取下传文件字段名使用的字段名,默认值:fileField
     *
     * @return 下传文件字段名
     */
    public String fileField() {
        return "fileField";
    }

    /**
     *  初始化数据总线,暂不使用
     *
     * @return
     */

    public void initDatapool(HashMap<String, ParamBean> inParams) {
        for (ParamBean paramBean : inParams.values()) {
            String paramCode = paramBean.getParamCode();
            if (StringUtils.equals(paramCode, apiIdFiled()) || StringUtils.equals(paramCode, apiNameField()))
            {
                continue;
            }
            String paramType = paramBean.getParamType();
            if (StringUtils.isNotEmpty(paramType) && paramType.equals("constant")) {
                LOGGER.info("数据总线.{}={}", paramCode, paramBean.getParamValue());
                setValue(paramCode, paramBean.getParamValue());
            }
        }
    }

    public void setCompOutParams(XmlResObj data, CompBean compBean, String apiId){

        Map<String, Object> body = data.getBody();
        HashMap<String, ParamBean> outParams = compBean.getOutParams();

        for (Map.Entry<String, Object> entry : body.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue() == null ? "" : entry.getValue().toString();
            /**
             * 针对日期为 1899-12-31 的进行特殊处理 
             */
            if("1899-12-31".equals(value)){
           // 	setOutParam(key, "");
            	setOutParam(key, value);
            }else{
            	setOutParam(key, value);
            }
            LOGGER.info("出口参数 {}.{}={}", apiId, key, value);
        }

    }

}
