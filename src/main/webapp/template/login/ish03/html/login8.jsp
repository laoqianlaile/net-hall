<%@ page language="java" contentType="text/html; charset=utf-8" 
%><%@ page import="com.yd.ydpx.util.YdpxUtil"
%><%
	final String LOGIN_TYPE_NAME = "短信验证码登录（单位）";
	final String LOGIN_TYPE_CODE = "0";
%><%@ page import="com.yd.org.util.CommonErrorCode"%>
<%@ page import="com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp" %>
<%@ page import="com.yd.svrplatform.spring.ApplicationContextHelper" %>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>Document</title>
	<link rel="stylesheet" href="../css/frame.css">
</head>
<body class="frame-possword">
<div class="frame-main" >
    <div class="frame-header">
        <div class="frame-header-img"></div>
        <div class="frame-header-title"><span>短信验证码登录</span></div>
    </div>
    <form action="#" id="form8" method="post" target="_parent">
        <div class="form-id-layer">
            <img src="../image/icon-user.png" alt="">
            <input type="text" id="dlyhm" name="dlyhm" placeholder="请输入用户名(单位账号)" autocomplete="off" />
        </div>
        <div class="form-pass-layer">
            <img src="../image/icon-lock.png" alt="">
            <input type="password" id="dlmm" name="dlmm" placeholder="请输入密码" autocomplete="off" />
        <!-- 增加手机号和验证码 -->
        </div>
            <div class="form-id-layer">
            <img src="../image/icon_phone.png" alt="">
            <input type="text" id="ylsjh" name="ylsjh" placeholder="请输入手机号" autocomplete="off" />
        </div>
        <div class="form-dxyzm"  >
            <input type="text" id="yzm" name="yzm" placeholder="请输入验证码" autocomplete="off" />
            <button type="button" id="buSubmit1" >发送验证码</button>
        </div>
        <div class="frame-Prompt"></div>
        <div class="form-button"><button type="button" id="buSubmit">登录</button></div>
        <div class="form-href">
            <a href="#" target="view_window">忘记密码</a>
        </div>
        <!-- 登录时用 -->
        <input type="hidden" id="logintype" name="logintype" value="orgdxyzm" />
    </form>
</div>
<script src="<%= YdpxUtil.staticResource("jquery.js") %>"></script>
<script src="../script/loginCommon.js"></script>
<!--[if lt IE 9]>
<script src="<%= YdpxUtil.staticResource("respond.js") %>"></script>
<![endif]-->
<script>
<% ParamConfigImp paramConfigImp=(ParamConfigImp)ApplicationContextHelper.getBean("paramConfigImp"); %>
//发送短信时间间隔
var count =  <%= Integer.parseInt(paramConfigImp.getVal("ish.gg.other.dxyzm.yxsc")) %>
//发送验证码是否成功
var flag = true;
//点击发送验证码次数
var times = 0;
//发送验证码报错信息
var message;
$(function(){
  	 // 登录基础参数
     var options={
        url:"<%= YdpxUtil.getContexPath() %>/login" , // 地址
        before:function(){
        	if ($('#dlyhm').val() == '') {
        		$('.frame-Prompt').text("请输入用户名！"); 
        		return false;
        	}
        	if ($('#dlmm').val() == '') {
        		$('.frame-Prompt').text("请输入登录密码！"); 
        		return false;
        	}
        	if ($('#ylsjh').val() == '') {
        		$('.frame-Prompt').text("请输入用户预留手机号！"); 
        		return false;
        	}
        	if(!flag){
        		$('.frame-Prompt').text(message); 
        		return false;
        	}
        	if(times==0){
        		$('.frame-Prompt').text("请点击发送验证码按钮"); 
        		return false;
        	}
        	if ($('#yzm').val() == '') {
        		$('.frame-Prompt').text("请输入验证码！"); 
        		return false;
        	}
        	return true;
        },// 登录之前校验函数
        fail:function(code,reason){
        	$('.frame-Prompt').text(reason);
        }, // 失败回调函数
     } 
    // 回车提交事件
	$('form').on("keydown", function(event) {
		if (event.which == '13') {
			loginCommon.login(options,$('#form8').serialize());
		}
	});
  	 //获取短信验证码
    $('#buSubmit1').on("click", function() {
    	times++;
    	if ($('#dlyhm').val() == '') {
    		$('.frame-Prompt').text("请输入用户名！"); 
    		return false;
    	}
    	if ($('#dlmm').val() == '') {
    		$('.frame-Prompt').text("请输入登录密码！"); 
    		return false;
    	}
    	if ($('#ylsjh').val() == '') {
    		$('.frame-Prompt').text("请输入用户预留手机号！"); 
    		return false;
    	}
    	sendMessage();
    	var ylsjh = $('#ylsjh').val() ;
    	var dlyhm = $('#dlyhm').val() ;
    	var dlmm = $('#dlmm').val() ;
		interInfoTime = $.ajax({
			url : '<%= YdpxUtil.getContexPath() %>/login/getDxyzm',
			type : 'get',
			data : {ylsjh: ylsjh, dlmm: dlmm, dlyhm: dlyhm},
			dataType : 'json',
			cache : false,
			success : function(data) {
				if (data.returnCode != 0) {
					message=data.message;
					$('.frame-Prompt').text(message); 
					flag=false;
					times=0;
				}else{
					$('.frame-Prompt').text("");
					flag=true;
				}
			}
		});
		 //设置button效果，开始计时
	    function sendMessage() {
	        curCount = count;
	        $("#buSubmit1").attr("disabled", "true");//禁用按钮
	        $("#buSubmit1").text(curCount + "秒后重新发送");
	        InterValObj = window.setInterval(SetRemainTime, 1000); //启动计时器，1秒执行一次
	    }
	    //timer处理函数
	    function SetRemainTime() {
	        if (curCount == 0) {
	            window.clearInterval(InterValObj);//停止计时器
	            $("#buSubmit1").removeAttr("disabled");//启用按钮
	            $("#buSubmit1").text("重新发送验证码");
	        }
	        else {
	            curCount--;
	            $("#buSubmit1").text(curCount + "秒后重新发送");
	        }
	    }
 	});
  	 
  	 //点击登陆
	$('#buSubmit').on("click", function() {
		loginCommon.login(options,$('#form8').serialize());
	});
})


</script>
</body>
</html>