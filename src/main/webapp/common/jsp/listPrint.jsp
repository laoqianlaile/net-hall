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
<script src="<%= YdpxUtil.staticResource("jquery.js") %>"></script>
<script src="<%= YdpxUtil.staticResource("bootstrap.js") %>"></script>
<script src="<%= YdpxUtil.staticResource("ydl.base.js") %>"></script>
<script src="<%= YdpxUtil.getContexPath() %>/common/lib/js/layer.js"></script>
<script src="<%= YdpxUtil.getContexPath() %>/common/lib/js/ydl.dialog.js"></script>
<script type="text/javascript">//<![CDATA[
$(function() {
	
	//处理传入的页面数据
	var postData = [];
	var args = ydl.dialog.arguments();
	$.each(args, function(key, value) {
		$('form').append('<input type="hidden" id="' + key + '" name="' + key + '" value="' + value + '" />');
		postData.push({name: key, value: value});
	});
	
	if(args.export_pageCount == '1') {
		$('.radio:gt(0)').hide();
	}
	
	//设置下载起始页码范围
	$('#export_range_start').val(args.export_currentPage);
	$('#export_range_end').val(args.export_pageCount);
	
	//由打印类型控制页码范围是否显示
	$('#export_range').hide();
	$('[name=export_style]').change(function() {
		if (this.value == 'range') {
			$('#export_range').show();
			$$('export_range_start').focus();
		}
		else $('#export_range').hide();
	});
	
	//更新页码范围
	$('#export_range_start,#export_range_end').change(function (){
		//setHref(args);
	});
	
	//关闭
 	$('#b_submit').click(function() {
		$('#content').find('input[name=export_style]:checked,input[type=text]').each(function () {
			var id = $(this).attr("name");//获取name属性
	        var value = $(this).val();//获取value属性
	        postData.push({name: id, value: value});
		})
		//$.ajax({ url:ydl.contexPath + '/ydpx/print/list', data:postData})
		ydl.ajax(ydl.contexPath + '/ydpx/print/list', postData, function(data) {
			if(data.returnCode==0){
				ydl.dialog.close(data);
			}
			
		})
	}); 
	
	//关闭
	$('#b_close').click(function() {
		ydl.dialog.close();
	});
	//更改按钮样式
	var metaObjArr = parent.document.getElementsByTagName('meta');
	for(var i=0;i<metaObjArr.length; i++){
		var metaName = metaObjArr[i].getAttribute('name');
		var con =  metaObjArr[i].getAttribute('content');
		if( metaName == 'cssTemplate'&& con == 'ish03'){
			$('style').append('.butbox {text-align: left;}'+
			'.butbox button { width: 104px; padding: 10px 0; border: none; border-radius: 0; background: #3e97df; color: #fff; }'+
			'.btn-default:hover,'+
			'.btn-default:active,'+
			'.btn-default:focus,'+
			'.btn-default.active,'+
			'.btn-default.active.focus, '+
			'.btn-default.active:focus, '+
			'.btn-default.active:hover, '+
			'.btn-default:active.focus, '+
			'.btn-default:active:focus, '+
			'.btn-default:active:hover, .btn-default.disabled.focus, .btn-default.disabled:focus, .btn-default.disabled:hover, .btn-default[disabled].focus, .btn-default[disabled]:focus, .btn-default[disabled]:hover, '+
			'.open > .dropdown-toggle.btn-default.focus, '+
			'.open > .dropdown-toggle.btn-default:focus, '+
			'.open > .dropdown-toggle.btn-default:hover { background: #204d74; border-color: #204d74; color: #fff; }');
		}
	}
});

//]]>
</script>
</head>
<body class="listdialog">
<div id="content">
<!--  
	<form  action="<%= YdpxUtil.getContexPath() %>/ydpx/print/list" method="post">
-->
<div class="panel panel-default ydpx-container">
  <div class="panel-body">
    <div class="form-group">
      <label for="" class="col-xs-4 col-sm-4 col-md-3">打印类型：</label>
      <div class="col-xs-8 col-sm-8 col-md-9">
		<fieldset class="multivalue">
	      	<div class="radio"><label><input type="radio" name="export_style" id="export_style_all" value="all" checked="checked" />打印全部</label></div>
			<div class="radio"><label><input type="radio" name="export_style" id="export_style_currentPage" value="currentPage" />打印当前页</label></div>
			<div class="radio"><label><input type="radio" name="export_style" id="export_style_range" value="range" />打印选定页码范围</label></div>
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
        <iframe src="about:blank" id="result" name="result" frameborder="1"></iframe>
      </div>
    </div>
  </div>
</div>
<div class="butbox">
	<button type="button" id="b_submit" class="btn btn-default">提交</button>
	<button type="button" id="b_close" class="btn btn-default">关闭</button>
</div>
<!-- 
</form>
 -->
</div>
</body>
</html>