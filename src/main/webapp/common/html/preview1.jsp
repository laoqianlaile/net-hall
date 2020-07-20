<%@ page language="java" contentType="text/html; charset=utf-8" 
%><%@ page import="com.yd.ydpx.util.YdpxUtil"
%><!DOCTYPE html>
<html lang="zh">
<head>
<meta charset="utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta name="renderer" content="webkit" />
<meta name="contexPath" content="<%= YdpxUtil.getContexPath() %>" />
<title>协议预览</title>
<!--[if lt IE 9]>
  <script src="<%= YdpxUtil.staticResource("html5shiv.js") %>"></script>
  <script src="<%= YdpxUtil.staticResource("respond.js") %>"></script>
<![endif]-->
<link rel="stylesheet" type="text/css" href="<%= YdpxUtil.staticResource("bootstrap.css") %>" />
<link rel="stylesheet" type="text/css" href="<%= YdpxUtil.staticResource("ydl.layout.css") %>" />
<link rel="stylesheet" type="text/css" href="<%= YdpxUtil.staticResource("ydl.themes.css") %>" />
<style type="text/css">
#result {width: 100%; height: 50px; border: 0;}
.form-group {overflow:hidden;margin-bottom:0px;}
.form-group>label {font-weight:normal; text-align: right; padding-top: 4px; padding-left: 10px; padding-right: 0; line-height: 1em; display: inline-block; max-height: 45px; overflow:hidden;}
.radio {margin-top:0px;}
</style>
<style media=print type="text/css"> 
.noprint{visibility:hidden} 
</style>
<script src="<%= YdpxUtil.staticResource("jquery.js") %>"></script>
<script src="<%= YdpxUtil.staticResource("bootstrap.js") %>"></script>
<script src="<%= YdpxUtil.staticResource("ydl.base.js") %>"></script>
<script src="<%= YdpxUtil.staticResource("ydl.ydpx.js") %>"></script>
<script type="text/javascript">//<![CDATA[

	var poolkey = '';
	var currentPreview = '';	// 当前位置 从0开始
	var totalPreview = '';		// 总记录数
	var suffix = '';			// 后缀名
	var currSuffix = '';		// 当前文件的后缀名
	var result = '';			// 截取后缀名
	var resfid = '';			// 截取fid
	var fid = '';				// fileid值
	var currentfid = '';		// 当前fileid值
	var reqtype = '';			// type值
	var zipName = '';			// 批量下载的文件名
	var realName = '';			// 下载的真实文件名
	var resName = '';			// 截取真实文件名

$(function() {
	
	//处理传入的页面数据
	var args = ydl.dialog.arguments();
	
	$.each(args, function(key, value) {
		if(key=='poolkey'){
			poolkey = value;
		}
		if(key=='currentPreview'){
			currentPreview = value;
		}
		if(key=='totalPreview'){
			totalPreview = value;
		}
		if(key=='suffix'){
			suffix = value;
		}
		if(key=='fid'){
			fid = value;
		}
		if(key=='reqtype'){
			reqtype = value;
		}
		if(key=='zipname'){
			zipName = value;
		}
		if(key=='realName'){
			realName = value;
		}
		
	});

	result = (suffix+'').split(",");
	resfid = (fid+'').split(",");
	resName = (realName+'').split(",");
	look();

});

// 预览展示
function look(){

	currSuffix = result[currentPreview];
	// 初始化按钮的状态
	$('#b_last').attr('disabled',false);
	$('#b_next').attr('disabled',false);
	
	if(parseInt(currentPreview)==0){
		$('#b_last').attr('disabled',true);
	}
	if(parseInt(currentPreview)==(parseInt(totalPreview)-1)){
		$('#b_next').attr('disabled',true);
	}

	//发请求解析文件
	if(currSuffix=='xls'||currSuffix=='xlsx'){
		$('#mess').html('共预览'+ totalPreview +'份文件，当前第'+ (parseInt(currentPreview)+1) +'份文件：'+resName[currentPreview]);
		ydl.ajax(ydl.contexPath + '/excelPrint/excel/' + currentPreview, {_POOLKEY: poolkey,fileTypeFlag:0}, function (data) {
			$('#previewArea').html(data[0].html);
		}, {method: 'GET'}).fail(function(data){
			$('#previewArea').html('该文件已被删除或失效，请重新确认！');
		});
		return;
	}
	
	if(currSuffix=='docx'||currSuffix=='doc'){
		$('#mess').html('共预览'+ totalPreview +'份文件，当前第'+ (parseInt(currentPreview)+1) +'份文件：'+resName[currentPreview]);
		ydl.ajax(ydl.contexPath + '/wordPrint/word/' + currentPreview, {_POOLKEY: poolkey,fileTypeFlag:0}, function (data) {
			$('#previewArea').html(data[0].html);
		}, {method: 'GET'}).fail(function(data){
			$('#previewArea').html('该文件已被删除或失效，请重新确认！');
		});
		return;
	}	
	
	if(currSuffix=='jpg'||currSuffix=='jpeg'||currSuffix=='png'||currSuffix=='gif'||currSuffix=='txt'){
		$('#mess').html('共预览'+ totalPreview +'份文件，当前第'+ (parseInt(currentPreview)+1) +'份文件：'+resName[currentPreview]);
		if(currSuffix=='txt'){
			$('#previewArea').html('<iframe src="'+ydl.contexPath + '/fileInteractivePreview/previewImg/Img/' + poolkey + '/Byt' + currentPreview + '" marginheight="0" id="iframepage" marginwidth="0" frameborder="0" scrolling="no" width="765" height=100% onLoad="iFrameHeight()"></iframe>');
			return;
		}
		$('#previewArea').html('<img src="'+ydl.contexPath + '/fileInteractivePreview/previewImg/Img/' + poolkey + '/Byt' + currentPreview + '" />');
		return;
	}
	
	$('#mess').html('共预览'+ totalPreview +'份文件，当前第'+ (parseInt(currentPreview)+1) +'份文件：'+resName[currentPreview]);
	$('#previewArea').html('<sapn>暂不支持此种格式文件预览！！</span>');
}

// iframe 自适应宽度和高度
function iFrameHeight() {

        var ifm= document.getElementById("iframepage");

        var subWeb = document.frames ? document.frames["iframepage"].document :

ifm.contentDocument;

            if(ifm != null && subWeb != null) {

            ifm.height = subWeb.body.scrollHeight;

            }

    }



// 下一篇
function next(){
	// 不是最后一个文件
	if(parseInt(currentPreview)<(parseInt(totalPreview)-1)){
		// 当前位置+1
		currentPreview = parseInt(currentPreview)+1;
		look();
		return;
	}
	if(parseInt(currentPreview)==(parseInt(totalPreview)-1)){
		// 已经是最后一个文件
		alert('已经是最后一个了，没有下一个了哦！');
		return;
	}
}

// 上一篇
function last(){
	// 不是第一个文件
	if(parseInt(currentPreview)>0){
		// 当前位置-1
		currentPreview = parseInt(currentPreview)-1;
		look();
		return;
	}
	if(parseInt(currentPreview)==0){
		// 已经是第一个文件
		alert('已经是第一个了，没有前一个了哦！');
		return;
	}
}

// 下载
function download(){
	  var $up_btn=$(this); 
	  var type=reqtype;
	  var zipname=resName[currentPreview];
	  var fileids=resfid[currentPreview];
	  var url=ydl.contexPath + '/fileInteractiveDownLoad/jsondata/' +type;  
	  $.ajax(url,{ 
	               method: 'POST',
			       dataType: 'json',
			       data:{
			          _r:(new Date())+"",
			          zipname:zipname,
			          fileids:fileids
			       },
		           success:function(jsonData){  
		                 if(jsonData.returnCode=="00000000"){ 
	                        var subPage = {close: function () {
		                            $dialog.dialog('close');
	                        }};
	                        var $dialog = $('<div class="subpage-container"></div>')
	                          .append("<a href='"+ydl.contexPath 
		                              + '/fileInteractiveDownLoad/download/' +jsonData.poolkey
		                              +"'>点这里下载</a>")
		                       .appendTo($('body')).dialog({
		                           title: "下载窗口",
		                           close: true,
		                           size: 'lg',
		                           hidden: function () {
			                               $dialog.remove();
		                                  }
	                          }); 
	                        $dialog.dialog('open');
		                 }else{
		                   alert(jsonData.message)			                 
		                 } 
		           }//end  success
		       }
	); //end ajax 
}

// 批量下载
function downloadAll(){
	  var $up_btn=$(this); 
	  var type=reqtype;
	  var zipname=zipName;
	  var fileids=fid;
	  var url=ydl.contexPath + '/fileInteractiveDownLoad/jsondata/' +type;  
	  $.ajax(url,{ 
	               method: 'POST',
			       dataType: 'json',
			       data:{
			          _r:(new Date())+"",
			          zipname:zipname,
			          fileids:fileids
			       },
		           success:function(jsonData){  
		                 if(jsonData.returnCode=="00000000"){ 
	                        var subPage = {close: function () {
		                            $dialog.dialog('close');
	                        }};
	                        var $dialog = $('<div class="subpage-container"></div>')
	                          .append("<a href='"+ydl.contexPath 
		                              + '/fileInteractiveDownLoad/download/' +jsonData.poolkey
		                              +"'>点这里下载</a>")
		                       .appendTo($('body')).dialog({
		                           title: "下载窗口",
		                           close: true,
		                           size: 'lg',
		                           hidden: function () {
			                               $dialog.remove();
		                                  }
	                          }); 
	                        $dialog.dialog('open');
		                 }else{
		                   alert(jsonData.message)			                 
		                 } 
		           }//end  success
		       }
	); //end ajax 
}

// 打印
function printPreview(){
	window.print();
}
//]]>
</script>
</head>
<body style="overflow-x:auto; width:auto; height:500px;">
	
	<span id="mess" class="noprint" style="font-size:18px"></span>
	<br/><br/>
    <div id="previewArea" style="overflow-x:auto; width:auto; height:390px;">
    
    </div>

	<br/>
	<center>
	<%--<div class="butbox mar-bottom noprint">--%>
		<%--<button type="button" class="btn btn-danger btn-lg" onclick="printPreview();" title="打印预览">--%>
		  <%--<span class="glyphicon glyphicon-print" aria-hidden="true"></span>--%>
		<%--</button>--%>
		<%--<button type="button" id="b_last" class="btn btn-warning btn-lg" onclick="last();" title="上一篇">--%>
		  <%--<span class="glyphicon glyphicon-menu-left" aria-hidden="true"></span>--%>
		<%--</button>--%>
		<%--<button type="button" id="b_next" class="btn btn-success btn-lg" onclick="next();" title="下一篇">--%>
		  <%--<span class="glyphicon glyphicon-menu-right" aria-hidden="true"></span>--%>
		<%--</button>--%>
		<%--<button type="button" class="btn btn-primary btn-lg" onclick="download();" title="下载当前">--%>
		  <%--<span class="glyphicon glyphicon-circle-arrow-down" aria-hidden="true"></span>--%>
		<%--</button>--%>
		<%--<button type="button" class="btn btn-info btn-lg" onclick="downloadAll();" title="下载全部">--%>
		  <%--<span class="glyphicon glyphicon-save" aria-hidden="true"></span>--%>
		<%--</button>--%>
	<%--</div>--%>
	</center>
</body>
</html>