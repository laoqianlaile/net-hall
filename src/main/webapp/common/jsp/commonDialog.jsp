<%@ page language="java" contentType="text/html; charset=utf-8" 
%><%@ page import="com.yd.ydpx.util.YdpxUtil"
%><%@ page import="org.json.*"
%><%@ page import="com.yd.svrplatform.comm_mdl.context.*"
%><%
MainContext mc = (MainContext)request.getAttribute("mainContext");
UserContext user = null;
if (mc != null) user = mc.getUserContext();
//读取当前用户设置的皮肤主题
JSONObject userConfig = new JSONObject(user != null ? user.getConfigs() : "{}");
String userConfigTheme = userConfig.has("theme") ? userConfig.getString("theme") : "style1";
String userConfigFontSize = userConfig.has("base_font_size") ? userConfig.getString("base_font_size") : "14";
%><!DOCTYPE html>
<html lang="zh">
<head>
<meta charset="utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta name="renderer" content="webkit" />
<meta name="contexPath" content="<%= YdpxUtil.getContexPath() %>" />
<meta http-equiv="Pragma" content="no-cache" />
<title>对话框</title>
<!--[if lt IE 9]>
  <script src="<%= YdpxUtil.staticResource("html5shiv.js") %>"></script>
  <script src="<%= YdpxUtil.staticResource("respond.js") %>"></script>
<![endif]-->
<link rel="stylesheet" type="text/css" href="<%= YdpxUtil.staticResource("bootstrap.css") %>" />
<style type="text/css">
body {margin: 0;}
#dialogBody {position: absolute; width: 100%; top: 0; bottom: 40px; overflow: scroll; overflow-x: hidden;}
#buttons {position: absolute; width: 100%; bottom: 0; text-align: center; padding: 10px 0;}
#buttons button {margin: 0 5px;}
</style>
<script src="<%= YdpxUtil.staticResource("jquery.js") %>"></script>
<script src="<%= YdpxUtil.staticResource("bootstrap.js") %>"></script>
<script src="<%= YdpxUtil.getContexPath() %>/common/lib/js/layer.js"></script>
<script src="<%= YdpxUtil.getContexPath() %>/common/lib/js/ydl.dialog.js"></script>
<script type="text/javascript">
$(function() {
	//ydl.ydpxInit();
	
	var options = ydl.dialog.arguments();
	document.title = options.title;
	$.each(options.buttons, function(key, clickFunc) {
		$('#buttons').append($('<button class="btn btn-default">' + key + '</button>').button().click(function() {
			clickFunc.call(window);
		}));
	});
	if (options.init) options.init.call(window, $('#dialogBody'));
});
</script>
</head>

<body>
<div id="dialogBody"></div>
<div id="buttons" class="dark-bg"></div>
</body>
</html>