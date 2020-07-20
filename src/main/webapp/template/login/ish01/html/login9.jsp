<%@ page language="java" contentType="text/html; charset=utf-8"
%><%@ page import="com.yd.ydpx.util.YdpxUtil"
%><%
	final String LOGIN_TYPE_NAME = "短信验证码登录（个人）";
	final String LOGIN_TYPE_CODE = "0";
%><%@ page import="com.yd.org.util.CommonErrorCode"%>
<%@ page import="com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp" %>
<%@ page import="com.yd.svrplatform.spring.ApplicationContextHelper" %>
<%@ page import="com.yd.ydpx.util.YdpxUtil"
%>
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
	<form action="#" id="form9" method="post" target="_parent">
		<div class="form-id-layer">
			<img src="../image/icon-user.png" alt="">
			<input type="text" id="dlyhm" name="dlyhm" placeholder="请输入用户名(个人账号)" autocomplete="off" />
		</div>
		<div class="form-pass-layer">
			<img src="../image/icon-lock.png" alt="">
			<input type="password" id="dlmm" name="dlmm" placeholder="请输入密码" autocomplete="off" />
			<!-- 增加手机号和验证码 -->
		</div>
		<div class="form-id-layer">
			<img src="../image/icon_phone.png" alt="">
			<input type="text" id="ylsjh" name="ylsjh" placeholder="" autocomplete="off" readonly="true" />
		</div>
		<div class="form-dxyzm"  >
			<input type="text" id="yzm" name="yzm" placeholder="请输入验证码" autocomplete="off" />
			<button type="button" id="buSubmit1" >发送验证码</button>
		</div>
		<div class="frame-Prompt"></div>
		<div class="form-button"><button type="button" id="buSubmit">登录</button></div>
		<div class="form-href">
			<a href="<%= YdpxUtil.getContexPath() %>/flow/menu/WFWJMA01" target="_parent">忘记密码</a>
		</div>
		<!-- 登录时用 -->
		<input type="hidden" id="logintype" name="logintype" value="persondxyzm" />
		<input type="hidden" id="gg" name="gg"/>
		<input type="hidden" id="A01001" name="A01001" />
	</form>
</div>
<script src="<%= YdpxUtil.staticResource("jquery.js") %>"></script>
<script src="../script/loginCommon.js"></script>
<script src="<%= YdpxUtil.getContexPath() %>/common/script/security.js"></script>
<script src="<%= YdpxUtil.getContexPath() %>/common/script/aes.js"></script>
<script src="<%= YdpxUtil.getContexPath() %>/common/script/my-aes-crypto.js"></script>
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
    // 公钥
    var b01001 ;
    // 随机数
    var a01001;
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
                $('#dlyhm').removeAttr("disabled");
                $('#dlmm').removeAttr("disabled");
                $('#yzm').removeAttr("disabled");
                // 登录失败取消disabled属性，登录中改成登录
                $('#buSubmit').removeAttr("disabled");
                $('#buSubmit').text("登录");
            }, // 失败回调函数
        }
        // 登录用户名、登录密码chang事件
        $('#dlyhm,#dlmm').on("change", function() {
            $('#ylsjh').val("");
            // 若登录用户名和登录密码都不为空
            if($('#dlyhm').val() != "" && $('#dlmm').val() != ""){
                var dlyhm = $('#dlyhm').val() ;
                var dlmm = $('#dlmm').val() ;
                $('#dlmm').val() ;
                // 对用户名和密码加密
                $.ajax({
                    type: 'post',
                    url: "<%= YdpxUtil.getContexPath() %>/RSA/rsaKey",
                    dataType: "json",
                    cache:false,
                    success: function (data) {
                        b01001 = new RSAUtils.getKeyPair(data.exponent, "", data.modulus);
                        a01001 = getRandomString(16);
                        var a01002 = $('#dlyhm').val().trim();
                        var a01003 = $('#dlmm').val().trim();
                        var a01004 = $('#logintype').val().trim();
                        var json = {};
                        json.a01002=a01002;
                        json.a01003=a01003;
                        json.a01004=a01004;
                        var gg = JSON.stringify(json);
                        var _gg = encrypt(gg,a01001);
                        var reversedkey = a01001.split("").reverse().join("");
                        var _a01001 = RSAUtils.encryptedString(b01001, reversedkey);
                        $('#A01001').val(_a01001);
                        $('#gg').val(_gg);
                        // 根据用户名密码获取手机号（加密传递）
                        $.ajax({
                            url : '<%= YdpxUtil.getContexPath() %>/login/getYlsjh',
                            data : {gg: $('#gg').val(), A01001: $('#A01001').val()},
                            dataType : 'json',
                            type : 'get',
                            cache:false,
                            success : function(data) {
                                if (data.returnCode != 0) {
                                    message=data.message;
                                    $('.frame-Prompt').text(message);
                                    $('#ylsjh').val("");
                                }else{
                                    $('.frame-Prompt').text("");
                                    $('#ylsjh').val(data.ylsjh);
                                }
                            }
                        });
                    }
                });
            }
        })

        // 回车提交事件
        $('form').on("keydown", function(event) {
            if (event.which == '13') {
                // 登录不可点，登录改成登录中
                $('#buSubmit').attr('disabled','disabled');
                $('#buSubmit').text("登录中...");
                // 请求前校验处理
                if(options.before &&  typeof options.before == "function") {
                    var flag = options.before.call();
                    if(!flag){
                        // 登录失败取消disabled属性，登录中改成登录
                        $('#buSubmit').removeAttr("disabled");
                        $('#buSubmit').text("登录");
                        return false;
                    }
                }
                // 传递参数进行加密,重新获取秘钥，防止秘钥超时或点登陆后缓存中的秘钥被删除
                $.ajax({
                    type: 'post',
                    url: "<%= YdpxUtil.getContexPath() %>/RSA/rsaKey",
                    dataType: "json",
                    cache: false,
                    success: function (data) {
                        b01001 = new RSAUtils.getKeyPair(data.exponent, "", data.modulus);
                        a01001 = getRandomString(16);
                        var a01002 = $('#dlyhm').val().trim();
                        var a01003 = $('#dlmm').val().trim();
                        var a01004 = $('#yzm').val().trim();
                        var a01006 = $('#ylsjh').val().trim();
                        var json = {};
                        json.a01002 = a01002;
                        json.a01003 = a01003;
                        json.a01004 = a01004;
                        json.a01006 = a01006;
                        var gg = JSON.stringify(json);
                        var _gg = encrypt(gg, a01001);
                        var reversedkey = a01001.split("").reverse().join("");
                        var _a01001 = RSAUtils.encryptedString(b01001, reversedkey);
                        $('#A01001').val(_a01001);
                        $('#gg').val(_gg);
                        $('#dlyhm').attr('disabled', 'disabled');
                        $('#dlmm').attr('disabled', 'disabled');
                        $('#yzm').attr('disabled', 'disabled');
                        $('#ylsjh').attr('disabled', 'disabled');
                        loginCommon.login(options, $('#form9').serialize());
                    }
                })
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
                $('.frame-Prompt').text(message);
                return false;
            }
            sendMessage();
            // 传递参数进行加密,重新获取秘钥，防止秘钥超时或点登陆后缓存中的秘钥被删除
            $.ajax({
                type: 'post',
                url: "<%= YdpxUtil.getContexPath() %>/RSA/rsaKey",
                dataType: "json",
                cache: false,
                success: function (data) {
                    b01001 = new RSAUtils.getKeyPair(data.exponent, "", data.modulus);
                    a01001 = getRandomString(16);
                    var a01002 = $('#dlyhm').val().trim();
                    var a01003 = $('#dlmm').val().trim();
                    var a01006 = $('#ylsjh').val().trim();
                    var json = {};
                    json.a01002 = a01002;
                    json.a01003 = a01003;
                    json.a01006 = a01006;
                    var gg = JSON.stringify(json);
                    var _gg = encrypt(gg, a01001);
                    var reversedkey = a01001.split("").reverse().join("");
                    var _a01001 = RSAUtils.encryptedString(b01001, reversedkey);
                    $('#A01001').val(_a01001);
                    $('#gg').val(_gg);
                    interInfoTime = $.ajax({
                        url: '<%= YdpxUtil.getContexPath() %>/login/getDxyzm',
                        type: 'get',
                        data: {gg: $('#gg').val(), A01001: $('#A01001').val()},
                        dataType: 'json',
                        cache: false,
                        success: function (data) {
                            if (data.returnCode != 0) {
                                message = data.message;
                                $('.frame-Prompt').text(message);
                                flag = false;
                                times = 0;
                            } else {
                                $('.frame-Prompt').text("");
                                flag = true;
                            }
                        }
                    });
                }
            })
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
            // 登录不可点，登录改成登录中
            $('#buSubmit').attr('disabled','disabled');
            $('#buSubmit').text("登录中...");
            // 请求前校验处理
            if(options.before &&  typeof options.before == "function") {
                var flag = options.before.call();
                if(!flag){
                    // 登录失败取消disabled属性，登录中改成登录
                    $('#buSubmit').removeAttr("disabled");
                    $('#buSubmit').text("登录");
                    return false;
                }
            }
            // 传递参数进行加密,重新获取秘钥，防止秘钥超时或点登陆后缓存中的秘钥被删除
            $.ajax({
                type: 'post',
                url: "<%= YdpxUtil.getContexPath() %>/RSA/rsaKey",
                dataType: "json",
                cache: false,
                success: function (data) {
                    b01001 = new RSAUtils.getKeyPair(data.exponent, "", data.modulus);
                    a01001 = getRandomString(16);
                    var a01002 = $('#dlyhm').val().trim();
                    var a01003 = $('#dlmm').val().trim();
                    var a01004 = $('#yzm').val().trim();
                    var a01006 = $('#ylsjh').val().trim();
                    var json = {};
                    json.a01002 = a01002;
                    json.a01003 = a01003;
                    json.a01004 = a01004;
                    json.a01006 = a01006;
                    var gg = JSON.stringify(json);
                    var _gg = encrypt(gg, a01001);
                    var reversedkey = a01001.split("").reverse().join("");
                    var _a01001 = RSAUtils.encryptedString(b01001, reversedkey);
                    $('#A01001').val(_a01001);
                    $('#gg').val(_gg);
                    $('#dlyhm').attr('disabled', 'disabled');
                    $('#dlmm').attr('disabled', 'disabled');
                    $('#yzm').attr('disabled', 'disabled');
                    $('#ylsjh').attr('disabled', 'disabled');
                    loginCommon.login(options, $('#form9').serialize());
                }
            })
        });
    })


</script>
</body>
</html>