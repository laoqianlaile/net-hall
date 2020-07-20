<%@page import="java.util.*,java.io.FileInputStream"%>
<%@page language="java" import="cn.org.bjca.client.security.*" %>
<%
	request.setCharacterEncoding("UTF-8");
	Properties properties = new Properties();
	properties.load(new FileInputStream(application.getRealPath("webappName.properties")));
	
//	SecurityEngineDeal.setProfilePath("C:\\Users\\Yondervision\\BJCAROOT");
	SecurityEngineDeal.setProfilePath("/var/workflow/tomcat/");
    SecurityEngineDeal sed = null;
    sed = SecurityEngineDeal.getInstance(properties.getProperty("webappName"));
    String function = properties.getProperty("function");
%>