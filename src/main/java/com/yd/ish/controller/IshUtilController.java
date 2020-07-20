package com.yd.ish.controller;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yd.svrplatform.comm_mdl.cache.YDMemcachedManager;
import com.yd.svrplatform.util.ReadProperty;

/**
 * 名称：IshUtilController.java
 * <p>
 * 功能：网厅公共功能 <br>
 * 
 * @author 柏慧敏
 * @version 0.1 2018年9月28日 柏慧敏新增
 */
@Controller
@RequestMapping("/ishutil")
public class IshUtilController {
	private static final Logger logger = LoggerFactory.getLogger(IshUtilController.class);
	/**
	 * 导出文件
	 * 
	 * @param request
	 * @param response
	 * @param poolkey
	 * @return
	 */
	@RequestMapping("/downfileByKey/{poolkey}")
	public String downfileByKey(HttpServletRequest request
			,HttpServletResponse response,
			@PathVariable(value="poolkey") String poolkey
			) {
		YDMemcachedManager sm = YDMemcachedManager.newInstance();
	    if(sm.get(poolkey)==null){
	    	request.setAttribute("returnCode", "00000001");
			request.setAttribute("message", "下载文件失败!");
	    	return "/template/" + ReadProperty.getString("template") + "/html/error";
	    }
	    String filePath=(String)sm.get(poolkey);
	    File file=new File(filePath);
	    if(!file.exists()){
	    	request.setAttribute("returnCode", "00000002");
			request.setAttribute("message", "下载文件失败!");	
			logger.error(file.getAbsolutePath()+" 文件不存在");
			return "/template/" + ReadProperty.getString("template") + "/html/error";
	    }
	    
		  String useAgent=request.getHeader("user-agent")==null?"":request.getHeader("user-agent"); 
		  
		  try{
			  String downloadName=file.getName();
			  if(useAgent.indexOf("Mozilla")==-1)
				  downloadName=java.net.URLEncoder.encode(downloadName, "utf-8");	 
			  else
				downloadName=new String(downloadName.getBytes("GB2312"), "ISO_8859_1"); 
			  response.setHeader("Content-Disposition", "attachment;filename="+downloadName); 
			  InputStream bis=new FileInputStream(file);
			  BufferedOutputStream bos=new BufferedOutputStream(response.getOutputStream());
			  long fl=file.length();
			  logger.debug("文件长度   {}",fl);
		  	  response.setHeader("Content-Length", fl+"");
		  	  byte data[]=new byte[40960];
		  	  long size=0; 
		  	  while (size<fl){
		  		   int tmpsize=bis.read(data);
		  		   if(tmpsize>=0){
		  			 size+=tmpsize;        			 
		  		   }else{
		  			 break;	
		  		   } 
		  		   bos.write(data,0,tmpsize);   
			  }
		  	  bis.close();
		  	  bos.close();
			  request.setAttribute("returnCode", "00000000");
			  request.setAttribute("message", "succ");	
		  }catch(Exception e){
			  request.setAttribute("returnCode", "00000003");
			  request.setAttribute("message", "下载文件失败!");	
		      logger.error(file.getAbsolutePath(),e);
			  return "/template/" + ReadProperty.getString("template") + "/html/error";
		  }
	    
		return null;
	}
	
 	
}
