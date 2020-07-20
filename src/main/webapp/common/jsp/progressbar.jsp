<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.yd.ydpx.controller.UpFileController"%>
<%@page import="com.yd.ydpx.controller.UpFileController.UploadProgress"%>
<%@page import="org.json.JSONObject"%>

<%
    
    String task = request.getParameter("task");
	String uuid = request.getParameter("imuuid");
	UploadProgress uploadProgress = UpFileController.PROGRESS.get(uuid);
	
	JSONObject json = new JSONObject();
	
	// 清空服务器进度信息
	if ("clear".equals(task)) {
	    // 增加对象为空的判断，防止空指针报错。 20180711
		if (uploadProgress != null) {
			uploadProgress.setTask("clear");
			json.put("task", "clear");
			json.put("message", uploadProgress.getMessage());
			UpFileController.PROGRESS.remove(uuid);
		}
	} else {
		if (uploadProgress != null) {
			
			// 上传或导入数据展示进度
			if("upload".equals(uploadProgress.getTask())) {
				json.put("progress", uploadProgress.getUploadRate());
			} else if("import".equals(uploadProgress.getTask())) {
				json.put("progress", uploadProgress.getCompletedRate());
			} else if("complete".equals(uploadProgress.getTask())) {
				json.put("progress", uploadProgress.getCompletedRate());
			} else if("error".equals(uploadProgress.getTask())) {
				UpFileController.PROGRESS.remove(uuid);
			} else { // 刚进入Controller task 为空
				json.put("progress", 0);
			}
			
			json.put("task", uploadProgress.getTask());
			json.put("message", uploadProgress.getMessage());
			
		} else {
			json.put("task", "");
			json.put("progress", 0);
		}
		
	}
	
	response.setHeader("Content-type", "application/json;charset=UTF-8");
	response.setContentType("application/json;charset=UTF-8");
	response.setCharacterEncoding("UTF-8");
	
    response.setHeader("Pragma", "No-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.setHeader("Cache-Control", "no-store");
    response.setDateHeader("Expires", 0);
	
	out.println(json.toString());
%>