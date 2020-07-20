<%@ page language="java" contentType="text/html; charset=utf-8" 
%><%@ page import="com.yd.ydpx.util.YdpxUtil" 
%><%@ page import="com.yd.svrplatform.util.ReadProperty" 
%><%@ page import="java.io.*" 
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
<title>从文件导入数据</title>
<link rel="stylesheet" type="text/css" id="bootstrapStyle" href="<%= YdpxUtil.staticResource("bootstrap.css") %>" />
<style type="text/css">
.alert {padding:5px;}
#file_to_upload {padding:0px;}
body {overflow: auto; background-color: #fff !important;}
.form-group {overflow:hidden;}
#errorTable {overflow-x:auto !important; white-space: nowrap; word-break: break-all;margin-bottom: 20px;}
#errorTable th {line-height: 2em;}
.colbox {overflow:auto;}
.colbox table {width: 100%;}
.colbox tr.fixtable-head {height: auto;}
.colbox th {padding: 0px 10px;}
.colbox th span {line-height: 1em; min-height: 1em; margin-top: 5px; display: inline-block;}

.connect {margin-bottom: 0; border-bottom: 0;}
.connect+.box-container { padding-top: 0; border-top: 0;}
.buttons {text-align: center;}
.tip-box-wrap {border: 1px solid #ddd; border-radius: 4px; margin-bottom: 10px;}
.tip-box {position: relative;}
.glyphicon-info-sign {position: absolute;top: 6px;left: 4px;}
.tip-data {margin: 2px 22px; line-height: 1.8em;}
.fixtable-head th {padding: 5px 8px !important;}

</style>
<script src="<%= YdpxUtil.staticResource("jquery.js") %>"></script>
<script src="<%= YdpxUtil.staticResource("bootstrap.js") %>"></script>
<script src="<%= YdpxUtil.staticResource("jquery.form.js") %>"></script>
<script src="<%= YdpxUtil.getContexPath() %>/common/lib/js/layer.js"></script>
<script src="<%= YdpxUtil.getContexPath() %>/common/lib/js/ydl.dialog.js"></script>
<script type="text/javascript">
//<![CDATA[
/**
 * 通过js对象创建隐藏input
 * @param {Object} data 包含名值的对象数组：[{name: '', value: ''}]
 */
function createHiddenInput(data) {
	var $form = $('form');
	for (var key in data) {
		if (key == 'options') options = data[key];//导入列信息
		else if (key == 'template') template = data[key];//是否支持模板下载
		else $form.append('<input type="hidden" id="' + key + '" name="' + key + '" value="' + data[key] + '" />');
	}
}
var timeout = '';
var returnData = {};
var options = {};
var template = false;
var isupload = false;//是否开始上传文件

$(function() {
	
	//ydl.ydpxInit();

	$('#imuuid').val(new Date().getTime()); //导入唯一标识
	
	$('#tr_text_file_separator').hide();
	$('#tr_text_file_separator_custom').hide();
	createHiddenInput(ydl.dialog.arguments());
	
	//表头行信息
	var headerRows = '<%=ReadProperty.getString("import_header_rows")%>';
	if (headerRows == 'null') headerRows = 1;
	if ($('#dynamicTable_headerRows').val() != '') headerRows = $('#dynamicTable_headerRows').val();
	$('#headerRows').text(' '+headerRows+' ');
	$('#bodyerRows').text(' '+ (parseInt(headerRows)+1) + ' ');
	
	//数据类型字典
	var datatypeDic = {
		'validchar':'合法字符',
		'int':'整数',
		'float':'小数',
		'date':'日期(yyyy-mm-dd)',
		'time':'时间(hh:mm:ss)',
		'month':'月份(1~12)',
		'longmonth':'长月份(01~12)',
		'day':'日(1~31)',
		'longday':'长日(01~31)',
		'yyyymm':'年月(yyyymm)',
		'yyyy-mm':'年月(yyyy-mm)',
		'number':'数字',
		'money':'金额',
		'phone':'电话号码',
		'phones':'一个或多个电话号码',
		'email':'电子邮件地址',
		'zipcode':'邮政编码',
		'idcard':'身份证号码',
		'idcard15':'15位身份证号码',
		'idcard18':'18位身份证号码',
		'hanzi':'汉字',
		'ipv4':'IP地址',
		'orgcode':'组织机构代码'
	}
	//导入列信息 
	var importColHtml = $.map(options,function (ele,index){
		if (ele.importcol != undefined && (ele.importcol+'') != '') {
			var info = '';
			info += ele.required ? '必填项' : '非必填项';
			//if (ele.dataType != '') info += ' , 数据类型为【'+datatypeDic[ele.dataType]+'】';
			//if (ele.maxLength != '') info += ' , 最大长度为'+ele.maxLength+'个字符';
			//if (ele.decLen != undefined && (ele.decLen+'') != '') info += ' , 保留'+ele.decLen+'位小数';
			return '<tr><td>第'+(parseInt(ele.importcol)+1)+'列</td><td>'+ele.desc+'</td><td>'+info+'</td></tr>';
		}
	}).join('');
	$('#colsList').append(importColHtml);
	if (importColHtml=='') {
		$('#b_submit,.colbox').hide();
		$('#colsInfo').removeClass('hide').html('未设置导入列！');
	}
	
	/*暂注释--待确认方案
	//空白模板下载
	if (template) {
		var filename = $$('dynamicTable_page').value.replace('.ydpx','') + '_' + $$('dynamicTable_id').value;
		//$('#file_to_upload').after('<a style="margin-left:10px;" href="'+ydl.contexPath + path + '/'+filename+'.xls">点击此处下载空白模板</a>');
		$('#file_to_upload').after('<a style="margin-left:10px;" href="'+ydl.contexPath+'/ydpx/00000000/pb/pbpage7048.jsp?filename='+filename+'">点击此处下载空白模板</a>');
	}
	*/
	
	//如果文件扩展名是txt，选择字段分隔符
	$('#file_to_upload').change(function() {
		var dot = this.value.lastIndexOf('.');
		var ext = this.value.substring(dot + 1);
		if (dot > 0 && ext == 'txt') {
			$('#tr_text_file_separator').show();
			$('#text_file_separator').val('tab').focus();
		}
		else {
			$('#tr_text_file_separator').hide();
			$('#tr_text_file_separator_custom').hide();
		}
		//如果文件类型不支持，禁用提交按钮
		$('#b_submit').prop('disabled',dot > 0 && new RegExp('\\b' + ext + '\\b', 'i').test('xls xlsx txt csv') ? false : true);
	});
	//如果选择自定义分隔符，显示输入框
	$('#text_file_separator').change(function() {
		if (this.value == 'custom') {
			$('#tr_text_file_separator_custom').show();
			$('#text_file_separator_custom').val('').focus();
		}
		else $('#tr_text_file_separator_custom').hide();
	});
	//提交前检查
	$('form').submit(function() {
		//提交前校验
		var r = true;
		//测试--注释
		//if ($$('tr_text_file_separator_custom').style.display != 'none')  r = ydl.validator('text_file_separator_custom');
		//校验通过
		if (r) {
			$('form:first').hide();
			$('#progress').removeClass('hide');
			// 进度条初始化
			progressBarInit();
			
			//查询进度
			doLoad();
			// 提交表单
			$(this).ajaxSubmit({
				dataType:'json',async:true, 
				success: function(data) {
					if(data.message.indexOf("%")==0){
						data.message=decodeURI(data.message)
					}
					returnData = data;
					clearTimeout(timeout);
					$('#importResult').removeClass('hide');
					$('#importResult').show();
					$('#progress').addClass('hide');
					//提交成功
					if (data.returnCode == '0') {
						$('#b_export').hide();
						parent.$('.modal-header button.close').hide();
						$('#infoUpload').addClass('alert-info').html('<strong>上传成功</strong>' + (data.message ? '：<br />' + data.message : ''));
						$("#b_close").click();
					}
					//提交失败
					else { 
						$('#infoUpload').addClass('alert-danger').html('<strong>上传失败：</strong><br />' + data.message);
						//显示导入错误信息
						if (data.errorTable) {
							var tableHtml = '<table class="table container datalist row-selectable">'+ $.map(data.errorTable,function (ele,index){
								if (index == 0) {
									return '<tr class="info">'+$.map(ele,function(value,index){
										
										if(value && value.indexOf("%")==0){
												value=decodeURI(value)
										}
										return '<th>'+value+'</th>';
									}).join('')+'</tr>';
								}
								else {
									return '<tr>'+$.map(ele,function(value,index){
										if(value && value.data && value.message.indexOf("%")==0){
												value.message=decodeURI(value.message)
										}
										if(value && value.data && value.data.indexOf("%")==0){
												value.data=decodeURI(value.data)
										}
										if ($.isPlainObject(value)) return '<td class="ui-state-error" title="'+value.message+'">'+(value.data==null?"":value.data )+'</td>';
										else return '<td>'+value+'</td>';
									}).join('')+'</tr>';
								}
							}).join('')+'</table>';
							$('#errorTable').removeClass('hide').append(tableHtml);
							//$('#errorTable').removeClass('hide').append(data.errorTable);
							$('#errorTable tr:first').addClass('fixtable-head');
							$('#errorTable table').addClass('table-bordered');
							$('#errorTable td.ui-state-error').addClass('alert-danger');
						}
					}

				}
			});
		}
		//为了防止普通浏览器进行表单提交和产生页面导航（防止页面刷新？）返回false
		return false;
	});
	
	//取消
	$('#b_cancel').click(function() {
		ydl.dialog.close();
	});

	//关闭窗口
	$('#b_close').click(function() {
		ydl.dialog.close(returnData);
	});
	
	//导出
	$('#b_export').click(function() {
		ydl.listExport('errorTable');
	});
});

//发送请求查询
function doLoad() {
	setTimeout(function () {
		var url = 'progressbar.jsp';
		//var url = "<%= YdpxUtil.getContexPath() %>/ydpx/uploadprogress";
		$.ajax({
			data: "imuuid=" + $('#imuuid').val(),
			async: true,
			type: 'POST',
			//contentType: 'application/json; charset=utf-8',
			contentType: 'application/x-www-form-urlencoded; charset=utf-8',
			dataType: 'json',
			timeout: 1800000,
			cache: false,
			url: url,
			success: loadItemsDone,
			error: loadItemsFail
		});
	}, 400);
}
//执行成功
function loadItemsDone(data) {
	
	if (data.task == 'upload') {
		$('#progressInfo p').html(data.message + "：进度" + data.progress + "%");
		progressBarDisplay(data.progress);
		
	} else if(data.task == "import") {
		$('#progressInfo p').html("完成文件上传：进度100%</br>" + data.message + "：进度" + data.progress + "%");
		if(data.progress == 0){
			progressBarInit();
		}else{
			progressBarDisplay(data.progress);
		}
		
	} else if(data.task == "error" || data.task == "complete") {
		clearCache();
		return;
	}
	
	//执行下一项 发送请求查询
	doLoad();
}
//执行失败
function loadItemsFail(jqXHR, textStatus, errorThrown) {
	//发送请求进行查询
	doLoad();
}

//发ajax清后台缓存
function clearCache() {
	//ydl.ajax('progressbar.jsp', {imuuid: $('#imuuid').val(),task:'clear'}, function(data) {
	//},{async: false});
	$.ajax({
		url: 'progressbar.jsp',
		data: {imuuid: $('#imuuid').val(),task:'clear'},
		async: false,
		type: 'POST',
		dataType: 'json',
		success: function(data){
			
		},
		error: function(){
			
		}
	});
}

var progressBar = null;
// 进度条初始化
function progressBarInit() {
	// 判断浏览器版本
	if (judgeBrowser() == "IE8") {
		$("div#progressDiv").children().remove().end().removeClass("progress");
		progress_width = parseInt(($("#progress").css("width").replace("px", "")));
		$("#progressDiv").css("width", ((progress_width - 1) + "px"));
		progressBar = ydl.progressBar('progressDiv');
		progressBar.setRate(0);
	} else {
		$("#progress > .progress").html("").html("<div id=\"up_progressbar\" class=\"progress-bar\" role=\"progressbar\" aria-valuenow=\"0\" aria-valuemin=\"0\" aria-valuemax=\"100\" style=\"width: 0%;\">0%</div>");
	}
}

// 进度条展示
function progressBarDisplay(rate) {
	if (progressBar != null) {
		rate = rate / 100;
		progressBar.setRate(rate);
	} else {
		$('#up_progressbar').attr('aria-valuenow',parseFloat(rate)).width(parseFloat(rate)+'%').text(parseFloat(rate)+'%');
	}
}

// 判断浏览器版本
function judgeBrowser(){
    
	var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
    var isOpera = userAgent.indexOf("Opera") > -1; //判断是否Opera浏览器
    var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera; //判断是否IE浏览器
    var isFF = userAgent.indexOf("Firefox") > -1; //判断是否Firefox浏览器
    var isSafari = userAgent.indexOf("Safari") > -1; //判断是否Safari浏览器
    if (isIE) {
        var IE5 = IE55 = IE6 = IE7 = IE8 = false;
        var reIE = new RegExp("MSIE (\\d+\\.\\d+);");
        reIE.test(userAgent);
        var fIEVersion = parseFloat(RegExp["$1"]);
        IE55 = fIEVersion == 5.5;
        IE6 = fIEVersion == 6.0;
        IE7 = fIEVersion == 7.0;
        IE8 = fIEVersion == 8.0;
        if (IE55) {
            return "IE55";
        }
        if (IE6) {
            return "IE6";
        }
        if (IE7) {
            return "IE7";
        }
        if (IE8) {
            return "IE8";
        }
    }//isIE end
    if (isFF) {
        return "FF";
    }
    if (isOpera) {
        return "Opera";
    }
}

//]]>
</script>
</head>
<body class="listdialog">
<div id="content">
<form action="<%= YdpxUtil.getContexPath() %>/ydpx/upfile" enctype="multipart/form-data" method="post">
	 
<div class="panel panel-default ydpx-container table-container connect">
  <div class="panel-body">
    <div class="form-group">
      <label for="file_to_upload" class="col-xs-4 col-sm-4 col-md-3">请选择数据文件：</label>
      <div class="col-xs-8 col-sm-8 col-md-9">
      	<input class="form-control input-sm" type="file" id="file_to_upload" name="file_to_upload" />
      </div>
    </div>
   	<div class="form-group" id="tr_text_file_separator">
      <label for="text_file_separator" class="col-xs-4 col-sm-4 col-md-3">字段分隔符：</label>
      <div class="col-xs-8 col-sm-8 col-md-9">
      	<select id="text_file_separator" name="text_file_separator" class="form-control input-sm">
			<option value="tab">制表符（Tab）</option>
			<option value="space">空格（连续两个或以上）</option>
			<option value="comma">逗号（,）</option>
			<option value="semicolon">分号（;）</option>
			<option value="line">竖线（|）</option>
			<option value="custom">其他（请在下面输入）</option>
		</select>
      </div>
    </div>
	<div class="form-group" id="tr_text_file_separator_custom">
      <label for="text_file_separator_custom" class="col-xs-4 col-sm-4 col-md-3">自定义分隔符：</label>
      <div class="col-xs-8 col-sm-8 col-md-9">
      	<input class="form-control input-sm" type="text" id="text_file_separator_custom" name="text_file_separator_custom" />
      </div>
    </div>   
  </div>
</div>
<div class="panel panel-default ydpx-container box-container buttons">
	<div class="panel-body">
		<button type="submit" id="b_submit" disabled="disabled" class="btn btn-default">提交</button>
		<button type="button" id="b_cancel" class="btn btn-default">取消</button>
	</div>
</div>
<div class="tip-box-wrap bg-success">
    <div class="tip-box tip-warning">
        <span class="glyphicon glyphicon-info-sign text-primary"></span>
        <div class="tip-data">
            <b>导入文件说明：</b><br />
			1、支持导入xls、xlsx、txt、csv格式的数据文件；<br />
			2、文件的第<span id="headerRows"> 1 </span>行将被当作表头，从第<span id="bodyerRows"> 2 </span>行开始导入，表头不能为空白行；<br />
			3、应保证数据记录是连续的，导入过程中遇到空白行即停止，空白行之后的数据不会被导入。
        </div>
    </div>
</div>
<div class="colbox datalist-box">
    <table class="table table-striped table-bordered datalist-table-body ydpx-datalist">
        <thead>
            <tr class="fixtable-head info">
                <th><span><label>导入列</label></span></th>
                <th><span><label>字段描述</label></span></th>
                <th><span><label>导入列数据规范</label></span></th>
            </tr>
        </thead>
        <tbody id="colsList"></tbody>
    </table>
</div>

<div id="colsInfo" class="alert alert-info hide" role="alert"></div>
<input type="hidden" id="imuuid" name="imuuid" value="" />
</form>

<div id="progress" class="hide">
	<div id="progressInfo" >
		<p>开始上传文件</p>
	</div>
	<div id="progressDiv" class="progress">
		<div id="up_progressbar" class="progress-bar" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">0%</div>
	</div>
</div>

<div id="importResult" class="hide">
	<div id="infoUpload" class="alert" role="alert"></div>
	<div id="errorTable" class="colbox datalist-box hide"></div>
	<div class="ydpx-container box-container buttons">
		<button type="button" class="btn btn-default hide" id="b_export">导出</button>
		<button type="button" class="btn btn-default" id="b_close">确定</button>
	</div>
</div>

</div>
<!--[if lt IE 9]>
  <script src="<%= YdpxUtil.staticResource("html5shiv.js") %>"></script>
  <script src="<%= YdpxUtil.staticResource("respond.js") %>"></script>
<![endif]-->
</body>
</html>