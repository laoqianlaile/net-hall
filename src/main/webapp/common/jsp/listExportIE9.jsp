<%@ page language="java" contentType="text/html; charset=utf-8" session="false"
%><%@ page import="com.yd.ydpx.util.YdpxUtil"
%><%@ page import="org.json.*"
%><%@ page import="com.yd.svrplatform.comm_mdl.context.*"
%><%@ page import="com.yd.svrplatform.util.ReadProperty"
%><%@ page import="com.yd.svrplatform.comm_mdl.context.UserContextFactory"
%><%
UserContext user = UserContextFactory.getInstance();
//读取当前用户设置的皮肤主题
JSONObject userConfig = new JSONObject(user != null ? user.getConfigs() : "{}");
String userConfigTheme = userConfig.has("theme") ? userConfig.getString("theme") : "normal";
String userConfigFontSize = userConfig.has("base_font_size") ? userConfig.getString("base_font_size") : "12";
String printTag=request.getParameter("print")==null?"0":request.getParameter("print");
//当前使用模板
String template = ReadProperty.getString("template");
%><!DOCTYPE html>
<html lang="zh">
<head>
<meta charset="utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta name="renderer" content="webkit" />
<meta name="contexPath" content="<%= YdpxUtil.getContexPath() %>" />
<meta http-equiv="Pragma" content="no-cache" />
<title>数据导出</title>
<!--[if lt IE 9]>
  <script src="<%= YdpxUtil.staticResource("html5shiv.js") %>"></script>
  <script src="<%= YdpxUtil.staticResource("respond.js") %>"></script>
<![endif]-->
<link rel="stylesheet" type="text/css" href="<%= YdpxUtil.staticResource("bootstrap.css") %>" />
<style type="text/css">
body {overflow: auto; background-color: #fff !important;}
#result {width: 100%; height: 0px; border: 0;}
.form-group {overflow:hidden; margin-bottom:0px;}
.form-group>label {padding-top: 4px; padding-right: 0; line-height: 1em;}
.radio {margin-top:0px;}
.butbox {text-align: right;}
#export_range input {width: 6em;}
#export_range .input-middle {width: 30px; text-align: center; line-height: 30px;}
fieldset.multivalue {padding: 0px;background-color: transparent;border-width: 0px;}
</style>
<script src="<%= YdpxUtil.getContexPath() %>/template/<%= template %>/js/theme-manifest.js"></script>
<script type="text/javascript">document.write('<link rel="stylesheet" id="themeStyle" href="<%=YdpxUtil.getContexPath()%>/template/<%=template%>/css/'+addThemeFile('dialog', '<%=userConfigTheme%>')+'"/>')</script>
<script src="<%= YdpxUtil.staticResource("jquery.js") %>"></script>
<script src="<%= YdpxUtil.staticResource("jquery.form.js") %>"></script>
<script src="<%= YdpxUtil.staticResource("bootstrap.js") %>"></script>
<script src="<%= YdpxUtil.staticResource("ydl.base.js") %>"></script>
<script src="<%= YdpxUtil.getContexPath() %>/common/lib/js/layer.js"></script>
<script src="<%= YdpxUtil.getContexPath() %>/common/lib/js/ydl.dialog.js"></script>
<script type="text/javascript">//<![CDATA[
$(function() { 
	//处理传入的页面数据
	var args = dialogArguments; 
	$.each(args, function(key, value) { 
		$('#listExport_form').append('<input type="hidden" id="' + key + '" name="' + key + '" value="' + value + '" />');
	}); 
	if(args.export_pageCount == '1') {
		$('.radio:gt(0)').hide();
	}
	
	//设置下载起始页码范围
	$('#export_range_start').val(args.export_currentPage);
	$('#export_range_end').val(args.export_pageCount);
	
	//由导出类型控制页码范围是否显示
	$('#export_range').hide();
	$('[name=export_style]').change(function() {
		if (this.value == 'range') {
			$('#export_range').show();
			$('#export_range_start').focus();
		}
		else $('#export_range').hide();
	});
	
	//更新页码范围
	$('#export_range_start,#export_range_end').change(function (){
		//setHref(args);
	});
	
	//关闭
	$('#b_close').click(function() {
		ydl.dialog.close();
	});
	
	$('#b_submit').click(function() {
		var $btn=$(this);
		$btn.attr("disabled",true); 
		 //------------------- 
		$('#listExport_form').ajaxSubmit({
			dataType: 'json',
			async: true,
			success: function (ret) {
				$btn.attr("disabled",false);
				 
				if(ret.returnCode=='0'){
					window.returnValue="<%= YdpxUtil.getContexPath() %>"+ret.url;
					window.close();
				}else{
					//ydl.alert(ret.message);
					ydl.alert({message: ret.message,width: 500,height: 160, shade: 0.1});
				}
			}
		});
		 //---------------------
	});
	//更改按钮样式
	var metaObjArr = parent.document.getElementsByTagName('meta');
	for(var i=0;i<metaObjArr.length; i++){
		var metaName = metaObjArr[i].getAttribute('name');
		var con =  metaObjArr[i].getAttribute('content');
		if( metaName == 'cssTemplate'&& con == 'ish03'){
			$('style').append('.butbox {text-align: left;} .butbox button { width: 104px; padding: 10px 0; border: none; border-radius: 0; background: #3e97df; color: #fff; }');
		}
	}
});

//]]>
</script>
</head>
<body class="listdialog font-size-<%= userConfigFontSize %>">
<div id="content">
<form id="listExport_form" target="result" action="<%= YdpxUtil.getContexPath() %>/ydpx/export/excel" method="post">
<%=printTag.equals("1")?"<input type='hidden' name='print' value='1'>":""%>
<div class="panel panel-default ydpx-container">
  <div class="panel-body">
    <div class="form-group">
      <label for="" class="col-xs-4 col-sm-4 col-md-3"><%=printTag.equals("1")?"打印":"导出"%>类型：</label>
      <div class="col-xs-8 col-sm-8 col-md-9">
      	<fieldset class="multivalue">
	      	<div class="radio"><label><input type="radio" name="export_style" id="export_style_all" value="all" checked="checked" /><%=printTag.equals("1")?"打印":"导出"%>全部</label></div>
			<div class="radio"><label><input type="radio" name="export_style" id="export_style_currentPage" value="currentPage" /><%=printTag.equals("1")?"打印":"导出"%>当前页</label></div>
			<div class="radio"><label><input type="radio" name="export_style" id="export_style_range" value="range" /><%=printTag.equals("1")?"打印":"导出"%>选定页码范围</label></div>
		</fieldset>
      </div>
    </div>
    <div id="export_range" class="form-group">
      <label for="" class="col-xs-4 col-sm-4 col-md-3">页码范围：</label>
      <div class="col-xs-8 col-sm-8 col-md-9">
      	<div class="input-group input-group-sm" style="display:flex;">
        	<input class="form-control input-sm" type="text" id="export_range_start" name="export_beginPage" size="6" maxlength="6" />
        	<div class="input-middle">～</div>
        	<input class="form-control input-sm" type="text" id="export_range_end" name="export_endPage" size="6" maxlength="6" />
        </div>
      </div>
    </div>
    <div class="form-group hidden">
      <div class="col-sm-12 col-md-12">
        <iframe src="about:blank" id="result" name="result" frameborder="0"></iframe>
      </div>
    </div>
  </div>
</div>
<div class="butbox">
	<button type="button" id="b_submit" class="btn btn-default">提交</button>
	<button type="button" id="b_close" class="btn btn-default">关闭</button>
</div>
</form>
</div>
</body>
</html>