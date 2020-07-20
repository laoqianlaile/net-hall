package com.yd.ish.util;

import com.yd.svrplatform.spring.ApplicationContextHelper;
import com.yd.svrplatform.util.ReadProperty;
import com.yd.svrplatform.util.ftp.IFtp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class PdfFtpUtil {
	private static final Logger logger = LoggerFactory.getLogger(PdfFtpUtil.class);
	//ftp连接类型 支持ftp,tftp,sftp,ftps四种
	public static final String ftp_type=ReadProperty.getString("ftp_type");
	//ftp地址
	public static final String ftp_url=ReadProperty.getString("pdf_ftp_url");
	//端口号
	public static final String ftp_port=ReadProperty.getString("pdf_ftp_port");
	//用户名
	public static final String ftp_user=ReadProperty.getString("pdf_ftp_user");
	//密码 - 默认用平台的aes方式加密
	public static final String ftp_pass=ReadProperty.getString("pdf_ftp_pass");
	//ftp根目录
	public static final String ftp_root=ReadProperty.getString("pdf_ftp_root");
	
	/**
     * ftp上传文件-参数从配置文件中获取
     * @param filename 远程文件名
     * @param localFile 上传文件
     * @throws IOException 
     */
    public static void upload(String filename,File localFile) throws IOException{
    	if(ftp_url==null){
    		throw new IOException("未在配置文件中找到参数[ftp_url]");
    	}
    	logger.debug("[+]upload 1 filename={},localFile={}",filename,localFile);
    	upload(ftp_type, ftp_url, Integer.parseInt(ftp_port), ftp_user, ftp_pass, ftp_root, filename, localFile);
    	logger.debug("[-]upload 1");
    }
    /**
     * ftp上传文件-参数从配置文件中获取
     * @param filename 远程文件名
     * @param localFile 上传文件流
     * @throws IOException 
     */
    public static void upload(String filename,InputStream localFile) throws IOException{
    	if(ftp_url==null){
    		throw new IOException("未在配置文件中找到参数[ftp_url]");
    		
    	}
    	upload(ftp_type, ftp_url, Integer.parseInt(ftp_port), ftp_user, ftp_pass, ftp_root, filename, localFile);
    }
    /**
     * ftp下载文件-参数从配置文件中获取
     * @param filename 远程文件名
     * @param localFile 本地文件
	 * @param path 时间为节点的文件夹
     * @throws IOException 
     */
    public void download(String filename,File localFile, String path, String pdfpath) throws IOException{
    	logger.debug("[+] download ftp_url={} 1filename={}, localFile={}",ftp_url,filename,localFile);
    	if(ftp_url==null){
    		throw new IOException("未在配置文件中找到参数[ftp_url]");
    	}
		if(pdfpath==null){
			throw new IOException("未在配置文件中找到参数[pdfpath]");
		}
		download(ftp_type, ftp_url, Integer.parseInt(ftp_port), ftp_user, ftp_pass, pdfpath+"/"+path+"/", filename, localFile);
    	logger.debug("[-] download 1");
    }
    /**
     * ftp下载文件-参数从配置文件中获取
     * @param filename 远程文件名
     * @param localFile 本地文件流
     * @throws IOException 
     */
    public static void download(String filename,OutputStream localFile) throws IOException{
    	if(ftp_url==null){
    		throw new IOException("未在配置文件中找到参数[ftp_url]");
    	}
    	download(ftp_type, ftp_url, Integer.parseInt(ftp_port), ftp_user, ftp_pass, ftp_root, filename, localFile);
    }
	/**
     * ftp上传文件-默认ftp连接
     * @param url ftp地址
     * @param port ftp端口
     * @param user 用户名
     * @param pass 密码
     * @param ftproot ftp根路径
     * @param filename 远程文件名
     * @param localFile 上传文件
     * @throws IOException 
     */
    public static void upload(String url,int port,String user,String pass,String ftproot,String filename,File localFile) throws IOException{
    	upload("ftp", url, port, user, pass, ftproot, filename, localFile);
    }
    /**
     * ftp上传文件-默认ftp连接
     * @param url ftp地址
     * @param port ftp端口
     * @param user 用户名
     * @param pass 密码
     * @param ftproot ftp根路径
     * @param filename 远程文件名
     * @param localFile 上传文件
     * @param filetype ftp文件传输类型 支持0-ASCII，1-EBCDIC，2-BINARY，3-LOCAL
     * @throws IOException 
     */
    public static void upload(String url,int port,String user,String pass,String ftproot,String filename,File localFile,int filetype) throws IOException{
    	upload("ftp", url, port, user, pass, ftproot, filename, localFile, filetype);
    }
    /**
     * ftp上传文件-默认ftp连接
     * @param url ftp地址
     * @param port ftp端口
     * @param user 用户名
     * @param pass 密码
     * @param ftproot ftp根路径
     * @param filename 远程文件名
     * @param localFile 上传文件流
     * @throws IOException 
     */
    public static void upload(String url,int port,String user,String pass,String ftproot,String filename,InputStream localFile) throws IOException{
    	upload("ftp", url, port, user, pass, ftproot, filename, localFile);
    }
    /**
     * ftp上传文件-默认ftp连接
     * @param url ftp地址
     * @param port ftp端口
     * @param user 用户名
     * @param pass 密码
     * @param ftproot ftp根路径
     * @param filename 远程文件名
     * @param localFile 上传文件流
     * @param filetype ftp文件传输类型 支持0-ASCII，1-EBCDIC，2-BINARY，3-LOCAL
     * @throws IOException 
     */
    public static void upload(String url,int port,String user,String pass,String ftproot,String filename,InputStream localFile,int filetype) throws IOException{
    	upload("ftp", url, port, user, pass, ftproot, filename, localFile, filetype);
    }
    /**
     * ftp上传文件
     * @param ftptype ftp连接类型 支持ftp,tftp,sftp,ftps四种
     * @param url ftp地址
     * @param port ftp端口
     * @param user 用户名
     * @param pass 密码
     * @param ftproot ftp根路径
     * @param filename 远程文件名
     * @param localFile 上传文件
     * @throws IOException 
     */
    public static void upload(String ftptype,String url,int port,String user,String pass,String ftproot,String filename,File localFile) throws IOException{
    	//默认采用二进制传输
    	logger.debug("[+]upload 2 ftptype={},port={},user={},ftproot={}",ftptype, url, port, user, pass, ftproot, filename, localFile);
    	upload(ftptype, url, port, user, pass, ftproot, filename, localFile, 2);
    	logger.debug("[-]upload 2 filename={},localFile={}",filename,localFile);
    }
    /**
     * ftp上传文件
     * @param ftptype ftp连接类型 支持ftp,tftp,sftp,ftps四种
     * @param url ftp地址
     * @param port ftp端口
     * @param user 用户名
     * @param pass 密码
     * @param ftproot ftp根路径
     * @param filename 远程文件名
     * @param localFile 上传文件
     * @param filetype ftp文件传输类型 支持0-ASCII，1-EBCDIC，2-BINARY，3-LOCAL
     * @throws IOException 
     */
    public static void upload(String ftptype,String url,int port,String user,String pass,String ftproot,String filename,File localFile,int filetype) throws IOException{
    	upload(ftptype, url, port, user, pass, ftproot, filename, new FileInputStream(localFile), filetype);
    }
    /**
     * ftp上传文件
     * @param ftptype ftp连接类型 支持ftp,tftp,sftp,ftps四种
     * @param url ftp地址
     * @param port ftp端口
     * @param user 用户名
     * @param pass 密码
     * @param ftproot ftp根路径
     * @param filename 远程文件名
     * @param localFile 上传文件流
     * @throws IOException 
     */
    public static void upload(String ftptype,String url,int port,String user,String pass,String ftproot,String filename,InputStream localFile) throws IOException{
    	//默认采用二进制传输
    	upload(ftptype, url, port, user, pass, ftproot, filename, localFile, 2);
    }
    /**
     * ftp上传文件
     * @param ftptype ftp连接类型 支持ftp,tftp,sftp,ftps四种
     * @param url ftp地址
     * @param port ftp端口
     * @param user 用户名
     * @param pass 密码
     * @param ftproot ftp根路径
     * @param filename 远程文件名
     * @param localFile 上传文件流
     * @param filetype ftp文件传输类型 支持0-ASCII，1-EBCDIC，2-BINARY，3-LOCAL
     * @throws IOException 
     */
    public static void upload(String ftptype,String url,int port,String user,String pass,String ftproot,String filename,InputStream localFile,int filetype) throws IOException{
    	Object obj=ApplicationContextHelper.getBean("ftp."+ftptype);
    	if(obj==null){
    		throw new IOException("未实现的ftp类型["+ftptype+"]");
    	}
    	IFtp ftp=(IFtp) obj;
    	ftp.connect(url, port, user, getActualPassword(pass));
    	try{
	    	if(ftproot!=null&&!"".equals(ftproot)){
	    		ftp.changeWorkingDirectory(ftproot);
	    	}
	    	ftp.setFileType(filetype);
	    	ftp.upload(filename, localFile);
    	} finally{
    		ftp.disconnect();
    	}
    }
    /**
     * ftp下载文件-默认ftp连接
     * @param url ftp地址
     * @param port ftp端口
     * @param user 用户名
     * @param pass 密码
     * @param ftproot ftp根路径
     * @param filename 远程文件名
     * @param localFile 本地文件
     * @throws IOException 
     */
    public static void download(String url,int port,String user,String pass,String ftproot,String filename,File localFile) throws IOException{
    	download("ftp", url, port, user, pass, ftproot, filename, localFile);
    }
    /**
     * ftp下载文件-默认ftp连接
     * @param url ftp地址
     * @param port ftp端口
     * @param user 用户名
     * @param pass 密码
     * @param ftproot ftp根路径
     * @param filename 远程文件名
     * @param localFile 本地文件
     * @param filetype ftp文件传输类型 支持0-ASCII，1-EBCDIC，2-BINARY，3-LOCAL
     * @throws IOException 
     */
    public static void download(String url,int port,String user,String pass,String ftproot,String filename,File localFile,int filetype) throws IOException{
    	download("ftp", url, port, user, pass, ftproot, filename, localFile, filetype);
    }
    /**
     * ftp下载文件-默认ftp连接
     * @param url ftp地址
     * @param port ftp端口
     * @param user 用户名
     * @param pass 密码
     * @param ftproot ftp根路径
     * @param filename 远程文件名
     * @param localFile 本地文件流
     * @throws IOException 
     */
    public static void download(String url,int port,String user,String pass,String ftproot,String filename,OutputStream localFile) throws IOException{
    	download("ftp", url, port, user, pass, ftproot, filename, localFile);
    }
    /**
     * ftp下载文件-默认ftp连接
     * @param url ftp地址
     * @param port ftp端口
     * @param user 用户名
     * @param pass 密码
     * @param ftproot ftp根路径
     * @param filename 远程文件名
     * @param localFile 本地文件流
     * @param filetype ftp文件传输类型 支持0-ASCII，1-EBCDIC，2-BINARY，3-LOCAL
     * @throws IOException
     */
    public static void download(String url,int port,String user,String pass,String ftproot,String filename,OutputStream localFile,int filetype) throws IOException{
    	download("ftp", url, port, user, pass, ftproot, filename, localFile, filetype);
    }
    /**
     * ftp下载文件
     * @param ftptype ftp连接类型 支持ftp,tftp,sftp,ftps四种
     * @param url ftp地址
     * @param port ftp端口
     * @param user 用户名
     * @param pass 密码
     * @param ftproot ftp根路径
     * @param filename 远程文件名
     * @param localFile 本地文件
     * @throws IOException 
     */
    public static void download(String ftptype,String url,int port,String user,String pass,String ftproot,String filename,File localFile) throws IOException{
    	//默认采用二进制传输
    	logger.debug("[+] download 2 ftptype={}, url={}, port={}, user={}, pass={}, ftproot={}",ftptype, url, port, user, pass, ftproot);
    	download(ftptype, url, port, user, pass, ftproot, filename, localFile, 2);
    	logger.debug("[+] download 2filename={}, localFile={}",filename,localFile);
    }
    /**
     * ftp下载文件
     * @param ftptype ftp连接类型 支持ftp,tftp,sftp,ftps四种
     * @param url ftp地址
     * @param port ftp端口
     * @param user 用户名
     * @param pass 密码
     * @param ftproot ftp根路径
     * @param filename 远程文件名
     * @param localFile 本地文件
     * @param filetype ftp文件传输类型 支持0-ASCII，1-EBCDIC，2-BINARY，3-LOCAL
     * @throws IOException 
     */
    public static void download(String ftptype,String url,int port,String user,String pass,String ftproot,String filename,File localFile,int filetype) throws IOException{
    	if(!localFile.getParentFile().exists())
    		localFile.getParentFile().mkdirs();
    	logger.debug("[+]download filename={}",filename);
    	download(ftptype, url, port, user, pass, ftproot, filename, new FileOutputStream(localFile), filetype);
    	logger.debug("[-]download");
    }
    /**
     * ftp下载文件
     * @param ftptype ftp连接类型 支持ftp,tftp,sftp,ftps四种
     * @param url ftp地址
     * @param port ftp端口
     * @param user 用户名
     * @param pass 密码
     * @param ftproot ftp根路径
     * @param filename 远程文件名
     * @param localFile 本地文件流
     * @throws IOException 
     */
    public static void download(String ftptype,String url,int port,String user,String pass,String ftproot,String filename,OutputStream localFile) throws IOException{
    	//默认采用二进制传输
    	download(ftptype, url, port, user, pass, ftproot, filename, localFile, 2);
    }
    /**
     * ftp下载文件
     * @param ftptype ftp连接类型 支持ftp,tftp,sftp,ftps四种
     * @param url ftp地址
     * @param port ftp端口
     * @param user 用户名
     * @param pass 密码
     * @param ftproot ftp根路径
     * @param filename 远程文件名
     * @param localFile 本地文件流
     * @param filetype ftp文件传输类型 支持0-ASCII，1-EBCDIC，2-BINARY，3-LOCAL
     * @throws IOException
     */
    public static void download(String ftptype,String url,int port,String user,String pass,String ftproot,String filename,OutputStream localFile,int filetype) throws IOException{
    	Object obj=ApplicationContextHelper.getBean("ftp."+ftptype);
    	if(obj==null){
    		throw new IOException("未实现的ftp类型["+ftptype+"]");
    	}
    	IFtp ftp=(IFtp) obj;
    	ftp.connect(url, port, user, pass);
    	try{
	    	if(ftproot!=null&&!"".equals(ftproot)){
	    		ftp.changeWorkingDirectory(ftproot);
	    		logger.debug("cd {}",ftproot);
	    	}
	    	ftp.setFileType(filetype);
	    	if(filename.startsWith("/"))
	    		filename=filename.substring(1);
	    	logger.debug("download {}",filename);
	    	ftp.download(filename, localFile);
    	} finally{
    		ftp.disconnect();
    	}
    }
    /**
     * 得到实际的密码
     * @param pass
     * @return
     */
    public static String getActualPassword(String pass){
    	return pass;
    	/*if(pass==null||pass.length()==0){
    		return "";
    	}
    	return CryptoUtil.decrypt(pass,"aes");*/
    }
}
