<%@ page language="java" contentType="text/html; charset=utf-8" 
%><%@ page import="com.yd.org.util.CommonErrorCode"
%><%
	final String LOGIN_TYPE_NAME = "密码登录（单位）";
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
    <form action="#" id="form3" method="post" target="_parent">
        <div class="form-id-layer">
            <img src="../image/icon-user.png" alt="">
            <input type="text" id="dlyhm" name="dlyhm" placeholder="请输入用户名" autocomplete="off" />
        </div>
        <div class="form-pass-layer">
            <img src="../image/icon-lock.png" alt="">
            <input type="password" id="dlmm" name="dlmm" placeholder="请输入密码" autocomplete="off"/>
        </div>
        <div>
            <input type="text" id="dxyzm" name="dxyzm" style="width:130px;background-color: white;color:black;" placeholder="请输入短信验证码" autocomplete="off"/>
            <button type="button" id="getDxyzm" style="width:115px;height:33px;background-color:#007ed6;color:white;font-size:12px;border:none;cursor:pointer;">获取验证码</button>
        </div>
        <div class="form-verify" id="p_yzm">
            <input type="text" id="yzm" name="yzm" placeholder="请输入验证码" autocomplete="off" />
        </div>
        <div class="frame-Prompt"></div>
<%--        <div class="form-verify" id="p_xyjmdzd" hidden="hidden">
            <input type="text" id="xyjmdzd" name="xyjmdzd" placeholder="xyjmdzd" autocomplete="off" />
        </div>--%>
        <div class="form-button"><button type="button" id="buSubmit">登录</button></div>
        <!-- <div class="form-button"><button type="submit" id="bSubmit">登录</button></div> -->
        <div class="form-href">
            <%--<a href="<%= YdpxUtil.getContexPath() %>/flow/menu/WFDWMMZH01" target="view_window">忘记密码</a>--%>
            <a href="#" id="forgetPwd">忘记密码</a>
        </div>
        <!-- 登录时用 -->
        <input type="hidden" id="logintype" name="logintype" value="orgauth" />
        <input type="hidden" id="yzmkey" name="yzmkey" />
        <input type="hidden" id="checkYzm" name="checkYzm" />
        <input type="hidden" id="A01001" name="A01001" />
        <input type="hidden" id="jbrsjhm" name="jbrsjhm">
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
//是否显示验证码标识，单位用户第一次登陆时默认不显示，密码输入错误后才显示
var checkYzmFlag = false;
//校验是否显示验证码,
 function ifCheckYzm(){
	 if (checkYzmFlag) {
			$('#checkYzm').val("1");
			$('#p_yzm').show();
		}else{
			$('#checkYzm').val("0");
			$('#p_yzm').hide();
		}
 }
$(function(){
     $('#p_yzm').hide();
	 if(checkYzmFlag){
		  $('#p_yzm').show();
	 }
    console.log("加载验证码信息22");
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
            if ($('#dxyzm').val() == '') {
                $('.frame-Prompt').text("请输入短信验证码！");
                return false;
            }
        	if (checkYzmFlag) {
        		if ($('#yzm').val() == '') {
        			$('.frame-Prompt').text("请输入验证码！"); 
        			return false;
        		}
        		if ($('#yzmkey').val() == '') {
        			$('.frame-Prompt').text("验证码加载失败，请刷新！"); 
        			return false;
        		}
        		$('#checkYzm').val("1");
        	}else{
        		$('#checkYzm').val("0");
        	}
        	return true;
        },// 登录之前校验函数
        fail:function(code,reason){
        	$('.frame-Prompt').text(reason);
        	//如果用户已被锁定不刷新验证码
    		if(code!= "<%= CommonErrorCode.ERROR_YBSD %>"){
    			loginCommon.refresh(src);
    		}
            // 登录失败取消disabled属性，登录中改成登录 start
            $('#buSubmit').removeAttr("disabled");
            $('#buSubmit').text("登录");
            // end
            $('#dlyhm').removeAttr("disabled");
            $('#dlmm').removeAttr("disabled");
            $('#yzm').removeAttr("disabled");
            checkYzmFlag = true;
            ifCheckYzm();
            loginCommon.refresh(src);
        }, // 失败回调函数
     }
    // 回车提交事件
	$('form').on("keydown", function(event) {
		if (event.which == '13') {
            //			loginCommon.login(options,$('#form0').serialize());
            // 登录不可点，登录改成登录中
            $('#buSubmit').attr('disabled','disabled');
            $('#buSubmit').text("登录中...");
            // 请求前校验处理
            if(options.before &&  typeof options.before == "function") {
                var flag = options.before.call();
                if(!flag){
                    // 登录失败取消disabled属性，登录中改成登录 start
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
                    var a01006 = $("#jbrsjhm").val().trim();
                    var a01007 = $("#dxyzm").val().trim();
                    var json = {};
                    json.a01002=a01002;
                    json.a01003=a01003;
                    json.a01004=a01004;
                    json.a01005=a01005;
                    json.a01006 = a01006;
                    json.a01007 = a01007;
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
                    loginCommon.login(options,$('#form3').serialize());
                }
            });
		}
	});
	$('#buSubmit').on("click", function() {
        //      loginCommon.login(options,$('#form0').serialize());
        // 登录不可点，登录改成登录中
        $('#buSubmit').attr('disabled','disabled');
        $('#buSubmit').text("登录中...");
        // 请求前校验处理
        if(options.before &&  typeof options.before == "function") {
            var flag = options.before.call();
            if(!flag){
                // 登录失败取消disabled属性，登录中改成登录 start
                $('#buSubmit').removeAttr("disabled");
                $('#buSubmit').text("登录");
                return false;
            }
        }
        // var username = $('#dlyhm').val();
        $.ajax({
            type: 'post',
            url: "<%= YdpxUtil.getContexPath() %>/RSA/rsaKey/",
            dataType: "json",
            cache:false,
            success: function (data) {
                console.log("返回的数据",data);
                var b01001 = new RSAUtils.getKeyPair(data.exponent, "", data.modulus);
                var a01001 = getRandomString(16);
                var a01002 = $('#dlyhm').val().trim();
                var a01003 = $('#dlmm').val().trim();
                var a01004 = $('#yzm').val().trim();
                var a01005 = $('#yzmkey').val().trim();
                var a01006 = $("#jbrsjhm").val().trim();
                var a01007 = $("#dxyzm").val().trim();
                var json = {};
                json.a01002=a01002;
                json.a01003=a01003;
                json.a01004=a01004;
                json.a01005=a01005;
                json.a01006 = a01006;
                json.a01007 = a01007;
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
                loginCommon.login(options,$('#form3').serialize());
            }
        });
	});
	$("#getDxyzm").click(function(){
        var dlyhm = $("#dlyhm").val().trim();
        var dlmm = $("#dlmm").val().trim();
        if(dlyhm == "" || dlmm == ""){
            alert("用户名、密码不能为空！");
            return false;
        }
        $('#getDxyzm').prop('disabled',true);
        //启动定时器
        createClock();
	    $.ajax({
            type: 'post',
            url: "<%= YdpxUtil.getContexPath() %>/RSA/rsaKey/",
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
                $.ajax({
                    type: "POST",
                    dataType: "json",
                    url: "<%= YdpxUtil.getContexPath() %>/login/getJbrSjhm",
                    data: $('#form3').serialize(),
                    success: function (result) {
                        //console.log("result",result);
                        if(result.returnCode == "1"){
                            alert(result.message);
                        }else{
                            $("#jbrsjhm").val(result.jbrsjhm);
                            //alert("您本次的短信验证码为：" + result.dxyzm);

                        }
                        $("#dlyhm").prop("disabled",false);
                        $("#dlmm").prop("disabled",false);
                        $("#yzm").prop("disabled",false);
                    },
                    error : function(XMLHttpRequest, textStatus, errorThrown) {
                        // 状态码
                        console.log(XMLHttpRequest.status);
                        // 状态
                        console.log(XMLHttpRequest.readyState);
                        // 错误信息
                        console.log(textStatus);
                        options.fail.call(XMLHttpRequest,9999,"专办员不存在或者密码错误");
                        //解除按钮禁用状态
                        //loginCommon.buttonAble(loginButtonId, 'able');
                    }
                });
            }
        });
    });
    $("#forgetPwd").click(function(){
       alert("请到柜台重置密码！");
    });
})
var clockid = 0;//定时器id
//创建定时器
function createClock(){
    dropClock();
    timmer = 60;
    clockid = setInterval(autoRefreshDo, 1000);
}
//删除定时器
function dropClock(){
    $('#getDxyzm').text("获取验证码");
    if(clockid != 0){
        clearInterval(clockid);
        clockid = 0;
    }
}
//全局自动刷新定时器
var timmer = 0 ;
function autoRefreshDo(){
    timmer --;
    var $getButton = $('#getDxyzm');
    if(timmer == 0){
        $getButton.text( "重新获取验证码");
    }else{
        $getButton.text( "重新获取验证码" + timmer + "");
    }
    if( timmer == 59 ){
        $('#getDxyzm').prop('disabled',true);
    }
    if( timmer  == 0 ) {
        dropClock();
        $('#getDxyzm').prop('disabled',false);
    }
}
</script>
</body>
</html>