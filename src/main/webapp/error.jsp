<%@ page language="java" contentType="text/html; charset=utf-8" 
session="false"
%><%@ page import="java.util.*,java.text.SimpleDateFormat"
%><%@ page import="org.json.*"
%><%@ page import="com.yd.ydpx.util.*"
%><%@ page import="com.yd.svrplatform.util.*"
%><%@ page import="com.yd.svrplatform.comm_mdl.context.*"
%><%@ page import="com.yd.svrplatform.jdbc.*"
%><%@ page import="com.yd.svrplatform.spring.ApplicationContextHelper"
%><%@ page import="com.yd.svrplatform.comm_mdl.services.*"
%><%@ page import="com.yd.svrplatform.util.ReadProperty"
%><%
/**
 * YDPX错误页面（北京）
 */
//获取Ydpx解析结果
final DataPool pool = (DataPool)request.getAttribute("datapool");
MainContext mc = (MainContext)request.getAttribute("mainContext"); 
UserContext user = request.getSession(false)==null?null: (UserContext)request.getSession(false).getAttribute("user");
if(user==null){ 
    String url="/template/"+ ReadProperty.getString("template") +"/html/errorNoSession.jsp";
    request.getRequestDispatcher(url).forward(request, response); 
    return;
}

response.setHeader("Cache-Control", "no-cache, no-store");	//HTTP 1.1
response.setHeader("Pragma", "no-cache");	//HTTP 1.0 

IPortalNameServices pns = (IPortalNameServices)ApplicationContextHelper.getBean("portalNameServicesImp");
IFlowInfoServices fis = (IFlowInfoServices)ApplicationContextHelper.getBean("flowInfoServicesImp");
IMessageInfoServices smis = (IMessageInfoServices)ApplicationContextHelper.getBean("systemMessageInfoServicesImp");
IMessageInfoServices nmis = (IMessageInfoServices)ApplicationContextHelper.getBean("noticeMessageInfoServicesImp");

//读取当前用户设置的皮肤主题
String theme = user == null ? null : user.getConfig("theme");
if (theme == null) theme = "style1";
%><!DOCTYPE html>
<html lang="zh">
<head>
<meta charset="utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta name="renderer" content="webkit" />
<meta name="contexPath" content="<%= YdpxUtil.getContexPath() %>" />
<meta name="ydpxTemplate" content="0401" />
<meta name="cssTemplate" content="<%= YdpxUtil.getTemplate() %>" />
<title>错误页面</title>
<link rel="stylesheet" type="text/css" id="bootstrapStyle" href="<%= YdpxUtil.staticResource("bootstrap.css") %>" />
<link rel="stylesheet" type="text/css" href="<%= YdpxUtil.staticResource("datepicker.css") %>" />
<link rel="stylesheet" type="text/css" href="<%= YdpxUtil.staticResource("ydl.global.css") %>" />
<link rel="stylesheet" type="text/css" href="<%= YdpxUtil.staticResource("ydl.layout.css") %>" />
<link rel="stylesheet" type="text/css" id="themeStyle" href="<%= YdpxUtil.staticResource("ydl.themes.css", theme) %>" />
<link rel="stylesheet" type="text/css" href="<%= YdpxUtil.getTemplatePath() %>/css/result-page.css" />
<link rel="stylesheet" type="text/css" href="<%= YdpxUtil.staticResource("ydl.printer.css") %>" />
<link rel="stylesheet" type="text/css" id="customStyle" href="<%= YdpxUtil.staticResource("custom.css", theme) %>" />
</head>
<body>
<!-- 页头 -->
<div class="header">
	<div class="pull-left">
		<div class="home-logo">
			<img class="home-logo-img1" src="<%= YdpxUtil.getTemplatePath() %>/image/logo-name-296x38.png" alt="">
			<div class="home-logo0"></div>
		</div>
	</div>
	<div class="pull-right header-nav">
		<div class="pull-left header-input">
			<input type="text">
			<span></span>
		</div>
		<ul id="head-nav" class="pull-left">
			<li class="link-first"><a href="<%= YdpxUtil.getContexPath() %>/home"><span class="move0"></span>首页</a></li>
			<li>
				<a class="frame-link" href="<%= YdpxUtil.getContexPath() %>/home_mess" title="系统信息">
					<span  class="move1"><i class="message spanicon"></i></span>消息
				</a>
			</li>
			<li class="link-last"><a class="frame-link" href="<%= YdpxUtil.getContexPath() %>/home_task" title="待办任务信息">
				<span  class="move2"><i class="task"></i></span>任务
			</a></li>
		</ul>
		<div class="pull-left header-user">
			<img src="<%= YdpxUtil.getTemplatePath() %>/image/icon-user-home.png" alt="">
			<span class="user-name"><%= user == null ? "未登录" : user.getOperName() %></span>
			<i></i>
			<dl>
				<dd><a href=""><span class="user-set"></span>设置</a></dd>
				<dd>
					<a target="_top" href="<%= YdpxUtil.getContexPath() %>/logout/<%= user == null ? "nologin" : user.getOperId() %>">
					<span class="user-exit"></span>退出</a>
				</dd>
			</dl>
		</div>
	</div>
</div>

<!-- 导航栏 -->
<div class="neck">
	<div class="pull-left neck-menu-btn">
		<span class="glyphicon glyphicon-th-list"></span>
		<span>系统菜单</span>
		<span class="neck-icon"></span>
	</div>
	<div class="pull-left neck-hint">当前位置 : <a class="neck-place"> 首页</a></div>
	<div class="pull-right" id="page_nav_notice">
		<img src="<%= YdpxUtil.getTemplatePath() %>/image/sound-24x16.png" alt="">
		<div>
			<ul>
<% 
if (user!=null && nmis != null) {
	JSONObject jsNotice = new JSONObject(nmis.getInfoList());
	if ("00000000".equals(jsNotice.getString("returnCode"))) {
		JSONArray jsNoticeInfo = jsNotice.getJSONArray("data");
		JSONObject jsInfo;
		String url;
		for (int i = 0; i < jsNoticeInfo.length(); i++) {
			jsInfo = jsNoticeInfo.getJSONObject(i);
			url = jsInfo.has("url") ? jsInfo.getString("url") : "";
			if (url.isEmpty()) out.println("<li>" + jsInfo.getString("title") + "</li>");
			else out.println("<li><a href=\"" + url + "\">" + jsInfo.getString("title") + "</a></li>");
		}
	}
	else out.println("<li>公告查询失败</li>");
}
else System.err.println("Get NoticeMessageInfoServicesImp Failed!");
%>
      </ul>
		</div>
  </div>

	<div class="neck-menu">
		<!-- 左边二级菜单开始 -->
		<div class="neck-menu-main"><ul></ul></div>
	</div>
</div>

<!-- 中间框架 -->
<div id="page_wrap" class="container-fluid">
  <div class="page-main-content"> 
  
  <!-- 交易页面 -->
  <div id="page_main" class="">
    <form method="post">
      <div class="wrap">
        <div id="page_tabs"> 
		  <div class="tab-content">
<%

String message = (String)request.getAttribute("message");
String title = message.replaceAll("<br\\s*/?>", "\n").replace("&", "&amp;").replace("<", "&lt;").replace("\n", "<br />");
String returnCode = (String)request.getAttribute("returnCode");
String messageForCopy = "错误码：" + returnCode + "\n错误信息：" + title + 
		"\n操作人：" + (user == null ? "未登录" : user.getOperId() + " - " + user.getOperName()) + 
		"\n操作日期：" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
%>
        <div id="resultInfo">
          <div class="result-top">
            <div class="result-type type_2"></div>
            <div class="result-right">
              <div class="result-title">您没有权限访问该功能!</div>
              <div class="result-content"><p><b>错误代码：</b>000001</p></div>
              <div class="result-buttons">
              <% if ("TIMEOUT".equals(returnCode)) { %>
                <a href="<%= YdpxUtil.getContexPath() %>" class="btn btn-info btn-sm">重新登录</a>
              <% } else { %>
                <a href="javascript:void(0)" id="bCopyMessage" class="btn btn-default btn-sm result-copy" data-clipboard-text="<%= messageForCopy %>">复制错误信息</a>
                <a href="<%= YdpxUtil.getContexPath() %>/home" class="btn btn-info btn-sm result-home">返回首页</a>
              <% } %>
              </div>
            </div>
          </div>
        </div>

		  </div><!-- #page_tabs .tab-content end -->
        </div><!-- #page_tabs end -->
      </div><!-- #page_main .wrap end -->
    </form>
  </div><!-- #page_main end -->
  </div><!-- .page-main-content end -->
</div><!-- #page_wrap end -->

<!-- 页脚 -->
<div class="footer">
	<ul>
		<li>住房公积金管理中心 版权所有 All Rights Reserved.</li>
		<li>技术支持：华信永道（北京）科技股份有限公司</li>
		<li>支持电话 : 4008-12329-0</li>
	</ul>
</div>

<textarea id="form_datapool"><%= FlowUtil.transObject(pool) %></textarea>
<script src="<%= YdpxUtil.staticResource("jquery.js") %>"></script>
<script src="<%= YdpxUtil.staticResource("bootstrap.js") %>"></script>
<script>
	if(!/MSIE 8/.test(navigator.userAgent)){
		$.getScript("<%= YdpxUtil.getContexPath()+"/common/script/clipboard.min.js" %>");
	}
</script>
<script src="<%= YdpxUtil.staticResource("plugins.js") %>"></script>
<script src="<%= YdpxUtil.staticResource("ydl.base.js") %>"></script>
<script src="<%= YdpxUtil.staticResource("ydl.ydpx.js") %>"></script>
<script src="<%= YdpxUtil.staticResource("ydl.datalist.js") %>"></script>
<script src="<%= YdpxUtil.staticResource("ydl.validator.js") %>"></script>
<script src="<%= YdpxUtil.staticResource("template.js", "0401") %>"></script>
<script src="<%= YdpxUtil.staticResource("custom.js") %>"></script>
<script type="text/javascript">//<![CDATA[
'use strict';

//数据总线
var poolSelect = <%= PoolUtil.toJson(pool) %>;

//菜单数据
var menuData = <%= user != null ? user.getAttribute("menuTree") : null %> || [];


//页面组件初始化
$(function() {

	ydl.ydpxInit();

	//模板初始化
	tpl.init(menuData);
	
	//初始化定时消息提醒
	tpl.promptDialog(<%= smis == null ? "null" : smis.getInfoList() %>, $('#theme_switcher'));
	
	//初始化复制信息到剪贴板按钮
	//如果不是ie8则加载插件
	if(!/MSIE 8/.test(navigator.userAgent)){
		//获取剪贴板插件js文件
		$.getScript(ydl.contexPath +'/common/script/clipboard.min.js',function(){
			//初始化复制信息到剪贴板按钮
			new Clipboard('#bCopyMessage').on('success', function(e) {
				$(e.trigger).text('已复制到剪贴板');
			});
		});
	}else{
	//如果是ie8则单独处理
		$('#bCopyMessage').click(function () {
			alert($(this).data('clipboard-text') + '\n\n请按Ctrl+C复制');
		});
	}

});

//]]></script>
<!--[if lt IE 9]>
  <script src="<%= YdpxUtil.staticResource("html5shiv.js") %>"></script>
  <script src="<%= YdpxUtil.staticResource("respond.js") %>"></script>
  <script src="<%= YdpxUtil.staticResource("json.js") %>"></script>
<![endif]-->
</body>
</html>
