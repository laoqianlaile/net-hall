package com.yd.ish.biz.comp.dp;

import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransOtherException;
import com.yd.biz.exception.TransSingleException;
import com.yd.ish.common.util.GetDownFileMap;
import com.yd.ish.model.mybatis.DP077;
import com.yd.ish.service.mybatis.DP077Service;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.dataswap.file.FileSwap;
import com.yd.svrplatform.comm_mdl.log.YDLogger;
import com.yd.svrplatform.util.DataPool;
import com.yd.svrplatform.util.ReadProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 名称：CompDWXXBG03.java
 * <p>
 * 功能：校验经办人信息 <br>
 *
 * @author 王赫
 * @version 0.1 2018年10月15日 王赫创建
 *                0.2 2019年7月16日  王赫修改 不能校验经办人信息动态列表数据是否变化的问题
 */
@Component("CompDWXXBG03")
public class CompDWXXBG03 extends BaseComp {

    private static final Logger logger = LoggerFactory.getLogger(CompDWXXBG03.class);

    /**
     * 数据分割符
     */
    private static final String file_separators = ReadProperty.getString("file_separators");
    /**
     * 编码格式
     */
    private static final String READFILE_ENCODING_BSP = ReadProperty.getString("readfile_encoding_bsp");

    @Autowired
    DP077Service dp077Service;

    @Autowired
    GetDownFileMap getDownFileMap;

    @Override
    public int execute() {

		/* 入口参数赋值开始 */
        String filename = getString("filename");// 经办人信息下传文件名称
        String changeflag = getString("changeflag");// 对比列表修改标识
        /* 入口参数赋值结束 */
        //校验标识 1-有变化，0-没有变化
        String jyjg = "0";//校验结果
        //获取下传文件中的经办人信息
        List<HashMap<String, Object>> jbrlist = new ArrayList();
        MainContext mainContext = MainContext.currentMainContext();
        DataPool dataPool = mainContext.getDataPool();

        // 获取实例号
        int instance = Integer.valueOf(dataPool.getString("_IS"));
        // 查询动态列表临时数据 提交前经办人信息
        List<DP077> newlist = new ArrayList<DP077>();
        newlist = dp077Service.selectBySlh(Integer.valueOf(instance));
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

                    //如果文件信息不为null，开始解析文件
                    while ((tmp = br.readLine()) != null) {
                        if (first) {
                            fileColNames = tmp.toLowerCase().split(file_separators);
                            first = false;
                            continue;
                        } else {
                            tmpdata = getDownFileMap.query(fileColNames, tmp);
                            jbrlist.add(tmpdata);
                        }
                    }
                } catch (RuntimeException | IOException e) {
                    YDLogger.openOut();
                    e.printStackTrace();
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
        } else {
            logger.error("读取单位经办人信息文件失败!");
        }
        //start 校验经办人信息
        //0.2 start
        int count = 0;
        if (jbrlist.size() == newlist.size()) {
            if (jbrlist.size() == 0 && newlist.size() == 0) {
                if("false".equals(changeflag)){
                    throw new TransSingleException("数据没有变化，不能提交。");
                }
            }else {
                //1.把临时表中的数据转成map的形式
                for (int i = 0; i < newlist.size(); i++) {
                    HashMap<String, Object> newmap = new HashMap<String, Object>();
                    newmap.put("jbrxm", newlist.get(i).getAgentop());
                    newmap.put("jbrzjlx", newlist.get(i).getCertitype());
                    newmap.put("jbrzjhm", newlist.get(i).getCertinum());
                    newmap.put("jbrsjhm", newlist.get(i).getSjhm());
                    newmap.put("jbrgddhhm", newlist.get(i).getPayvounum());
                    newmap.put("jbrdzxx", newlist.get(i).getFreeuse1());
                    newmap.put("jbrcz", newlist.get(i).getJtzz());
                    newmap.put("jbbm", newlist.get(i).getAgentinstcode());
                    newmap.put("bgbz", newlist.get(i).getReason());
                    //如果newmap中的值为null替换成""
                    for (Map.Entry<String, Object> entry : newmap.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        if(value==null){
                            newmap.put(key,"");
                        }
                    }
                        for (int j = 0; j < jbrlist.size(); j++) {
                        HashMap<String, Object> jbrMap = jbrlist.get(j);
                        if(jbrMap.equals(newmap)){
                            count++;
                        }
            /*            HashMap<String, Object> jbrMap = jbrlist.get(j);
                        if (jbrlist.get(j).get("jbrzjhm").equals(newmap.get("jbrzjhm"))) {
                            if (!jbrMap.equals(newmap)) {
                                jyjg = "1";
                                break;
                            }
                        }*/
                    }
                   /* if("1".equals(jyjg)){
                        break;
                    }*/
                }
                if(count!=jbrlist.size()){
                    jyjg = "1";
                }

                //0.2 end
                if("false".equals(changeflag)&&"0".equals(jyjg)){
                    throw new TransSingleException("单位信息数据没有变化，不能提交!");
                }

            }
        }

		/* 出口参数赋值开始 */
		/* 出口参数赋值结束 */

        return 0;
    }
}
