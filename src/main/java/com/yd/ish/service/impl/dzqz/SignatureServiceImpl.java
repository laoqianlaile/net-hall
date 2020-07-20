package com.yd.ish.service.impl.dzqz;

import com.yd.ydpx.export.ISignatureService;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 *
 * 名称：SignatureServiceImpl.java
 * <p>功能： 列表打印添加电子签章功能<br>
 * @author 王赫
 * @version 0.1	2019年06月06日	王赫创建
 */
@Component("SignatureServiceImpl")
public class SignatureServiceImpl implements ISignatureService {
    /**
     *
     * @param filePath 文件路径
     * @throws Exception
     */
    @Override
    public void addSignature(String filePath) throws Exception {
        File file  = new File(filePath);
        if(file.exists()){
            //实现对文件添加电子签章的代码
        }
    }
}
