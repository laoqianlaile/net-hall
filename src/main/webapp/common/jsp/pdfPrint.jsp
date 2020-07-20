<%@ page language="java" contentType="text/html; charset=utf-8" session="false"
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
<title>打印数据</title>
<!--[if lt IE 9]>
  <script src="<%= YdpxUtil.staticResource("html5shiv.js") %>"></script>
  <script src="<%= YdpxUtil.staticResource("respond.js") %>"></script>
<![endif]-->
<link rel="stylesheet" type="text/css" href="<%= YdpxUtil.staticResource("bootstrap.css") %>" />
<style type="text/css">
#result {width: 100%; height: 500px; border: 0;}
.form-group {overflow:hidden;margin-bottom:0px;}
.form-group>label {font-weight:normal; text-align: right; padding-top: 4px; padding-left: 10px; padding-right: 0; line-height: 1em; display: inline-block; max-height: 45px; overflow:hidden;}
.radio {margin-top:0px;}
</style>
<script src="<%= YdpxUtil.staticResource("jquery.js") %>"></script>
<script src="<%= YdpxUtil.staticResource("bootstrap.js") %>"></script>
<script src="<%= YdpxUtil.staticResource("ydl.base.js") %>"></script>
<script src="<%= YdpxUtil.getContexPath() %>/common/lib/js/layer.js"></script>
<script src="<%= YdpxUtil.getContexPath() %>/common/lib/js/ydl.dialog.js"></script>
<script type="text/javascript">//<![CDATA[
$(function() {
	//处理传入的页面数据
	var args = ydl.dialog.arguments();
	$.each(args, function(key, value) {
		//alert(key+"===="+value)
	});
	ydl.contexPath = '<%= YdpxUtil.getContexPath() %>';
	var url = ydl.contexPath + args.url;
	$("#result").attr("src", url+"?printflag=print");
	//$("#result").contents().find(":button [id='secondaryPrint']").click();

});

//]]>
</script>
</head>
<body class="listdialog">
<div id="content">

<div class="panel panel-default">
  <div class="panel-body">
	<div class="form-group">
      <div class="col-sm-12 col-md-12">
        <iframe src="about:blank" id="result" name="result" frameborder="1"></iframe>
      </div>
    </div>
  </div>
</div>
</div>
</body>
</html>