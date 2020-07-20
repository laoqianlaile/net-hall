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
			<input type="hidden" id="gg" name="gg" value="1"/>
			<input type="hidden" name="logintype" value="personQrCode" />
		</form>
	</div>
	<script src="<%=YdpxUtil.staticResource("jquery.js")%>"></script>
	<script src="../script/loginCommon.js"></script>
	<script type="text/javascript" src="<%= YdpxUtil.getContexPath() %>/common/script/app_qrcode.js"></script>

	<!--[if lt IE 9]>
<script src="<%=YdpxUtil.staticResource("respond.js")%>"></script>
<![endif]-->
	<script>
		// 登录唯一标示
		var loginKey;
		$("#formInfo").hide();

		var tips = {
			init: '请使用<b>【公积金APP】</b>扫描二维码安全登录',
			timeout: '二维码超时失效，请<a>刷新</a>重试!',
			error: '生成二维码失败，请<a>刷新</a>重试!',
		}
		 //点击刷新二维码
		$('.form-2code-img').on('click','a',function(){
			// 调用生成二维码方法
			generateQrCode();
		});
		// 生成二维码后的回调函数
		var generateCallBack = function(params, settings){
		    // 成功
			if(params.returnCode=="0"){
                $('.form-2code-tips').removeClass('refresh').html(tips.init);
                $('#face2code').show();
			} else {
                // 失败
                $('form>div').show();
                $('.form-2code-img').hide();
                console.log(params.errMsg);
                $('.form-2code-tips').removeClass('refresh').html(tips.error);
                alert('生成二维码失败，请刷新重试！');
			}
		};

		// 获取用户信息后的回调函数
		var UserInfoCallBack = function(params){
			console.log('I am UserInfoCallBack, executed...');
			console.log(params);
			// 成功返回了用户信息
			if('0' == params.fhbz){
				loginKey = params.loginKey;
				$('.form-result-info span.name').text(params.xingming);
				$('.form-result-info span.idcard').text(params.sfzh);
				$("#loginKey").val(params.loginKey);
				$("#formImg").hide();
				$("#formInfo").show();
			}
			// 未返回用户信息
			else if('0' == params.returnCode){
				$('#face2code').hide();
				$('.form-2code-tips').addClass('refresh').html(params.message+'，请<a>刷新</a>重试!');
			} else {
			    // 超时
				if(true === params.timeOutFlag){
					$('#face2code').hide();
					$('.form-2code-tips').addClass('refresh').html(tips.timeout);
				}
			}
		};

		// 获取授权登录信息回调函数
        var ResultCallBack = function(params){
            console.log('I am ResultCallBack, executed...');
            console.log(params);
            if('0' == params.returnCode){
                if(params.loginStatus == '0'){
                    login();
				}else{
                    $("#formInfo").hide();
                    generateQrCode();
				}
            } else {
                // 超时
                if(true === params.timeOutFlag){
                    $("#formInfo").hide();
                    generateQrCode();
                }
            }
        };
        // 点击切换账号
        $("#b_change").click(function (){
            $("#formInfo").hide();
            // 删除用户信息
            $.ajax({
                url: '<%= YdpxUtil.getContexPath() %>/login/deleteLoginInfoMap',
                type:'POST',
                data:{loginKey:loginKey},
                dataType:'json',
                cache:false,
                success:function(data) {
                    $("#formInfo").hide();
                    generateQrCode();
                }
            });
        });
		// 页面加载调用生成二维码方法
        generateQrCode();

        // 生成二维码方法
		function generateQrCode(){
			var geOpts = {
				context: '<%=YdpxUtil.getContexPath()%>',//必传,项目根路径，如果为空则传空字符串''
				imgId: 'face2code',//二维码图片元素(img)的id，非必传,如果传了则自动设置二维码图片
				width: 300,//二维码图片的宽,默认300
				height: 300,//二维码图片的高,默认300
				callBack1: generateCallBack,//必传,接收生成二维码结果的回调函数
				cycleQuery: true,//是否启用轮询查询认证结果
				callBack2: UserInfoCallBack,//获取用户信息后的回调函数
				callBack3: ResultCallBack,//获取登录结果后的回调函数
				alwaysExecute: false,//false:只在认证结果为成功或者轮询超时时才调用回调函数  true:总是调用回调函数，不论成功与否
				interval: 3000,//轮询的时间间隔，默认1000（单位：毫秒）
				timeOut: 120,//轮询超时时间，默认120（单位：秒）
			};
			$("#formImg").show();
			$("#face2code").show();
			$(".form-2code-tips").removeClass('refresh').html(tips.init);

			personCode.generateQrCode(geOpts);
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