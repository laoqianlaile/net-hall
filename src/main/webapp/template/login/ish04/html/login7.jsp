<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="com.yd.org.util.CommonErrorCode"%>
<%
	final String LOGIN_TYPE_NAME = "密码登录（开发商）";
	final String LOGIN_TYPE_CODE = "0";
%><%@ page import="com.yd.ydpx.util.YdpxUtil"
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
    <form action="#" id="form7" method="post" target="_parent">
        <div class="form-id-layer">
            <img src="../image/icon-user.png" alt="">
            <input type="text" id="dlyhm" name="dlyhm" placeholder="请输入用户名" autocomplete="off"/>
        </div>
        <div class="form-pass-layer">
            <img src="../image/icon-lock.png" alt="">
            <input type="password" id="dlmm" name="dlmm" placeholder="请输入密码" autocomplete="off" />
        </div>
        <div class="form-verify" id="p_yzm">
            <input type="text" id="yzm" name="yzm" placeholder="请输入验证码" autocomplete="off"/>
        </div>
        <div class="frame-Prompt"></div>
<%--        <div class="form-verify" id="p_xyjmdzd" hidden="hidden">
            <input type="text" id="xyjmdzd" name="xyjmdzd" placeholder="xyjmdzd" autocomplete="off" />
        </div>--%>
        <div class="form-button"><button type="button" id="buSubmit">登录</button></div>
        <!-- <div class="form-button"><button type="submit" id="bSubmit">登录</button></div> -->
        <div class="form-href">
            <a href="#" target="_parent">用户注册</a>
            <a href="#" target="view_window">忘记密码</a>
        </div>
        <!-- 登录时用 -->
        <input type="hidden" id="yzmkey" name="yzmkey" />
        <input type="hidden" id="logintype" name="logintype" value="developer" />
        <input type="hidden" id="A01001" name="A01001" />
        <input type="hidden" id="gg" name="gg">
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
var src = '<%= YdpxUtil.getContexPath() %>/ranCode.jsp';

$(function(){
    console.log("加载验证码信息8");
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
            $('#dlyhm').removeAttr("disabled");
            $('#dlmm').removeAttr("disabled");
            $('#yzm').removeAttr("disabled");
            // 登录失败取消disabled属性，登录中改成登录
            $('#buSubmit').removeAttr("disabled");
            $('#buSubmit').text("登录");
        }, // 失败回调函数
     }
    // 回车提交事件
	$('form').on("keydown", function(event) {
		if (event.which == '13') {
//			loginCommon.login(options,$('#form7').serialize());
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
            $.ajax({
                type: 'post',
                url: "<%= YdpxUtil.getContexPath() %>/RSA/rsaKey",
                dataType: "json",
                cache:false,
                success: function (data) {
                    var b01001 = new RSAUtils.getKeyPair(data.exponent, "", data.modulus);
                    var a01001 = getRandomString(16);
                    var a01002 = $('#dlyhm').val().trim();
                    var a01003 = $('#dlmm').val().trim();
                    var a01004 = $('#yzm').val().trim();
                    var a01005 = $('#yzmkey').val().trim();
                    var json = {};
                    json.a01002=a01002;
                    json.a01003=a01003;
                    json.a01004=a01004;
                    json.a01005=a01005;
                    var gg = JSON.stringify(json);
                    var _gg = encrypt(gg,a01001);
                    var reversedkey = a01001.split("").reverse().join("");
                    var _a01001 = RSAUtils.encryptedString(b01001, reversedkey);
                    $('#A01001').val(_a01001);
                    $('#gg').val(_gg);
                    // $('#xyjmdzd').val("gg");
                    $('#dlyhm').attr('disabled','disabled');
                    $('#dlmm').attr('disabled','disabled');
                    $('#yzm').attr('disabled','disabled');
                    $('#yzmkey').attr('disabled','disabled');
                    loginCommon.login(options,$('#form7').serialize());
                }
            });
		}
	});
	$('#buSubmit').on("click", function() {
//      loginCommon.login(options,$('#form7').serialize());
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
        var username = $('#dlyhm').val();
        $.ajax({
            type: 'post',
            url: "<%= YdpxUtil.getContexPath() %>/RSA/rsaKey",
            dataType: "json",
            cache:false,
            success: function (data) {
                var b01001 = new RSAUtils.getKeyPair(data.exponent, "", data.modulus);
                var a01001 = getRandomString(16);
                var a01002 = $('#dlyhm').val().trim();
                var a01003 = $('#dlmm').val().trim();
                var a01004 = $('#yzm').val().trim();
                var a01005 = $('#yzmkey').val().trim();
                var json = {};
                json.a01002=a01002;
                json.a01003=a01003;
                json.a01004=a01004;
                json.a01005=a01005;
                var gg = JSON.stringify(json);
                var _gg = encrypt(gg,a01001);
                var reversedkey = a01001.split("").reverse().join("");
                var _a01001 = RSAUtils.encryptedString(b01001, reversedkey);
                $('#A01001').val(_a01001);
                $('#gg').val(_gg);
                // $('#xyjmdzd').val("gg");
                $('#dlyhm').attr('disabled','disabled');
                $('#dlmm').attr('disabled','disabled');
                $('#yzm').attr('disabled','disabled');
                $('#yzmkey').attr('disabled','disabled');
                loginCommon.login(options,$('#form7').serialize());
            }
        });
	});
})

</script>
</body>
</html>