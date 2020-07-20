<%@ page language="java" contentType="text/html; charset=utf-8"%><%@ page
	import="com.yd.svrplatform.util.ReadProperty"%><%@ page
	import="com.yd.ydpx.util.YdpxUtil"%>
<%
	final String LOGIN_TYPE_NAME = "扫码登录";
	final String LOGIN_TYPE_CODE = "1";
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Document</title>
<link rel="stylesheet" href="../css/frame.css">
</head>
<body class="frame-code">
	<div class="frame-main">
		<div class="frame-header">
			<div class="frame-header-img"></div>
			<div class="frame-header-title">
				<span>扫码登录</span>
			</div>
		</div>
		<form action="#" id="form6" method="post" target="_parent">
			<div class="form-2code-img" id="formImg">
				<img id="face2code" src="" alt="">
				<div class="form-2code-tips"></div>
			</div>
			
			<div class="form-info" id="formInfo">
				<div class="form-result-icon">
					<span></span>
					<label>扫码成功</label>
				</div>
				<div class="form-result-info">
					<span class="name"></span><span class="idcard"></span>
				</div>
				<div class="form-info-tip">
		            <label>请在手机上点击确认以登录</label>
		        </div>
		        <div class="form-button"><button type="button" id="b_change">切换账号</button></div>
	        </div>
			<!-- 登录时用 -->
	        <input type="hidden" id="loginKey" name="loginKey" />
			<input type="hidden" name="logintype" value="personQrCode" />
		</form>
	</div>
	<script src="<%=YdpxUtil.staticResource("jquery.js")%>"></script>
	<script src="../script/loginCommon.js"></script>
	<!--[if lt IE 9]>
<script src="<%=YdpxUtil.staticResource("respond.js")%>"></script>
<![endif]-->
	<script>
	var interInfoTime;
	var interStatusTime;
	var tips = {
        init: '请使用<b>【公积金APP】</b>扫描二维码安全登录',
        timeout: '二维码超时失效，请<a>刷新</a>重试!',
        error: '认证失败，请<a>刷新</a>重试!'
    }
	 //点击刷新二维码
    $('.form-2code-img').on('click','a',function(){
    	createQrcode();
    });
	var key;
	// 点击切换账号
	$("#b_change").click(function (){
		$.ajax({
               url: '<%= YdpxUtil.getContexPath() %>/login/person/qrCodeLoginStatusStop',
               type:'POST',
               data:{loginKey:key},
               dataType:'json',
               cache:false,
               success:function(data) {
               	createQrcode();
               }
		});
	});
	
	createQrcode();
	//生成二维码
	function createQrcode() {
		$("#formImg").show();
		$("#face2code").show();
		$(".form-2code-tips").removeClass('refresh').html(tips.init);		
		$("#formInfo").hide();
		//上传参数
		var paras = {};
		interInfoTime = $.ajax({
			url : '<%= YdpxUtil.getContexPath() %>/login/getQrCode',
			type : 'get',
			data : paras,
			dataType : 'json',
			cache : false,
			success : function(data) {
				//console.log('生成二维码返回数据',data);
				if (data.returnCode == 0) {
					$('#face2code').attr("src",'data:image/png;base64,' + data.qrcode);
					reQueryUserInfo(data.loginKey);
				} else {
					$('.form-2code-tips').addClass('refresh').html(data.message);
				}
			}
		});
	}
	//查询用户信息
	function reQueryUserInfo(loginKey) {
		//发查询结果请求
		queryUserInfo(0);

        function queryUserInfo(allTime){
        	var startTime = new Date().getTime();
            //发查询结果请求
           interInfoTime = $.ajax({
                url: '<%= YdpxUtil.getContexPath() %>/login/queryQrCodeLoginInfo',
                type:'POST',
                data:{loginKey:loginKey},
                dataType:'json',
                cache:false,
                success:function(data) {
                    //成功
                    if (data.returnCode == 0 && loginKey == data.loginKey) { 
                        $('.form-result-info span.name').text(data.xingming);
                        $('.form-result-info span.idcard').text(data.sfzh);
                        
                        $("#loginKey").val(data.loginKey);
                        $("#formImg").hide();
                        $("#formInfo").show();
                        key = loginKey;
                        reQueryResult(loginKey);
                    }
                    //失败
                    else if (data.returnCode == 1) {
                        $('#face2code').hide();
                        $('.form-2code-tips').addClass('refresh').html(data.message);
                    }
                },
                error : function() {    
                	$('#face2code').hide();
                       $('.form-2code-tips').addClass('refresh').html(tips.error); 
                }   
            });
        }  
	}
	//查询结果
	function reQueryResult(loginKey) {
		//发查询结果请求
		queryResult(0);

        function queryResult(allTime){
        	var startTime = new Date().getTime();
            //发查询结果请求
           interStatusTime = $.ajax({
                url: '<%= YdpxUtil.getContexPath() %>/login/queryQrCodeLoginStatus',
                type:'POST',
                data:{loginKey:loginKey},
                dataType:'json',
                cache:false,
                success:function(data) {
                    //成功
                    if (data.returnCode == 0 && loginKey == data.loginKey) { 
                        clearInterval(interStatusTime);
                        if("0" == data.loginStatus){
                        	// 登录
	                       login();
                        }else{
                        	// 取消
                        	createQrcode();
                        }
                    }
                    else if (data.returnCode == -1) {
	                   	// 退出等待
                    	console.info("退出等待结果");
                    }
                    //失败
                    else {
                        $('#formInfo').hide();
                        $("#formImg").show();
                        $('#face2code').hide();
                        $('.form-2code-tips').addClass('refresh').html(data.message);
                    }
                },
                error : function() {
                	$('#face2code').hide();
                       $('.form-2code-tips').addClass('refresh').html(tips.error); 
                }  
            });
        }  
	}
	function login(){
		// 登录基础参数
	    var options={
	       url:"<%= YdpxUtil.getContexPath() %>/login" , // 地址
	       fail:function(code,reason){
	             $('.form-2code-tips').addClass('refresh').html(reason);
	       }, // 失败回调函数
	    }
	    loginCommon.login(options,$('#form6').serialize());
	}
	</script>
</body>
</html>