<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="com.yd.ydpx.util.YdpxUtil"%>
<%@ page import="com.yd.org.util.CommonErrorCode"%>
<%
	final String LOGIN_TYPE_NAME = "密码登录（临时个人）";
	final String LOGIN_TYPE_CODE = "0";
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>Document</title>
	<link rel="stylesheet" href="../css/frame.css">
</head>
<body class="frame-possword">
<div class="frame-main">
    <div class="frame-header">
        <div class="frame-header-img"></div>
        <div class="frame-header-title"><span>密码登录</span></div>
    </div>
    <form action="#" id="form2" method="post" target="_parent">
        <div class="form-id-layer">
            <img src="../image/icon-user.png" alt="">
            <input type="text" id="dlyhm" name="dlyhm" placeholder="请输入用户名" autocomplete="off"/>
        </div>
        <div class="form-pass-layer">
            <img src="../image/icon-lock.png" alt="">
            <input type="password" id="dlmm" name="dlmm" placeholder="请输入密码" autocomplete="off" />
        </div>
        <div class="form-verify" id="p_yzm">
            <input type="text" id="yzm" name="yzm" placeholder="请输入验证码" autocomplete="off" />
        </div>
        <div class="frame-Prompt"></div>
        <div class="form-button"><button type="button" id="buSubmit">登录</button></div>
        <div class="form-href">
            <a href="#" target="view_window">用户注册</a><a href="#" target="view_window">忘记密码</a>
        </div>
        <!-- 登录时用 -->
        <input type="hidden" id="logintype" name="logintype" value="persontemp" />
        <input type="hidden" id="yzmkey" name="yzmkey" />
    </form>
</div>
<script src="<%= YdpxUtil.staticResource("jquery.js") %>"></script>
<script src="../script/loginCommon.js"></script>
<!--[if lt IE 9]>
<script src="<%= YdpxUtil.staticResource("respond.js") %>"></script>
<![endif]-->
<script>
var src = '<%= YdpxUtil.getContexPath() %>/ranCode.jsp';

$(function(){
    console.log("加载验证码信息13");
	 $('#p_yzm').append($('<img id="t_yzm" src="' + src + '" style="cursor:pointer;" /><a href="javascript:void(0)" onclick="loginCommon.refresh(src)">换一张</a>'));
     $('#t_yzm').click(function () {
    	 loginCommon.refresh(src);
     });
     loginCommon.refresh(src);
     // 登录基础参数
     var options={
        url:"<%= YdpxUtil.getContexPath() %>/login" , // 地址
        before:function(){
        	if ($('#dlyhm').val() == '') {
        		$('.frame-Prompt').text("请输入用户名！"); 
        		return false;
        	}
        	if ($('#dlmm').val() == '') {
        		$('.frame-Prompt').text("请输入密码！"); 
        		return false;
        	}
        	if ($('#yzm').val() == '') {
        		$('.frame-Prompt').text("请输入验证码！"); 
        		return false;
        	}
        	if ($('#yzmkey').val() == '') {
        		$('.frame-Prompt').text("验证码加载失败，请刷新！"); 
        		return false;
        	}
        	return true;
        },// 登录之前校验函数
        fail:function(code,reason){
        	$('.frame-Prompt').text(reason);
        	//如果用户已被锁定不刷新验证码
    		if(code!= "<%= CommonErrorCode.ERROR_YBSD %>"){
    			loginCommon.refresh(src);
    		}
        }, // 失败回调函数
     }
    // 回车提交事件
	$('form').on("keydown", function(event) {
		if (event.which == '13') {
			loginCommon.login(options,$('#form2').serialize());
		}
	});
	$('#buSubmit').on("click", function() {
		loginCommon.login(options,$('#form2').serialize());
	});
})
</script>
</body>
</html>