<%@ page language="java" contentType="text/html; charset=utf-8" 
%><%@ page import="com.yd.ydpx.util.YdpxUtil"
%><%
	final String LOGIN_TYPE_NAME = "刷脸登录";
	final String LOGIN_TYPE_CODE = "1";
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>Document</title>
	<link rel="stylesheet" href="../css/frame.css">
</head>
<body class="frame-face">
<div class="frame-main" style="height: 355px">
    <div class="frame-header">
        <div class="frame-header-img"></div>
        <div class="frame-header-title"><span>刷脸登录</span></div>
    </div>
    <form action="#" id="form1" method="post" target="_parent">
        <div class="form-id-layer">
            <img src="../image/icon-user.png" alt="">
            <input type="text" id="certno" name="certno" placeholder="请输入身份证号/公积金账号" autocomplete="off" />
        </div>
        <!--
        <div class="form-id-layer">
            <img src="../image/icon-user.png" alt="">
            <input type="text" id="certname" name="certname" placeholder="姓名" />
        </div>
        -->
        <div class="form-2code">请输入身份证/公积金号后点击登录</div>
        <div class="form-button"><button type="button" id="bSubmit">登录</button></div>

        <div class="form-2code-img">
            <img id="face2code" src="" alt="">
            <div class="form-2code-tips">请使用<b>【支付宝】客户端</b>扫描二维码安全登录</div>
        </div>
        <!-- 登录时用 -->
        <input type="hidden" name="logintype" value="personface" />
        <input type="hidden" id="success"  name="success" />
        <input type="hidden" id="userName" value="" />
    </form>
</div>
<script src="<%= YdpxUtil.staticResource("jquery.js") %>"></script>
<script src="../script/loginCommon.js"></script>
<script type="text/javascript" src="<%= YdpxUtil.getContexPath() %>/common/script/face_recog.min.js"></script>
<!--[if lt IE 9]>
<script src="<%= YdpxUtil.staticResource("respond.js") %>"></script>
<![endif]-->
<script>
    //重设父页面的iframe元素高度，上线前注释掉
    /*
    window.onload = function(){
        var _iframe = window.parent;
        var _loginFrame =_iframe.document.getElementById('loginFrame');
        _loginFrame.style.height = '400px';
    };*/
    var tips = {
         init: '请使用<b>【支付宝】客户端</b>扫描二维码安全登录',
         timeout: '二维码超时失效，请点击 <a>刷新</a>',
         error: '认证失败，请点击 <a>刷新</a> 重新认证'
     }
    var checkCallBack = function(params, settings){
        if('Y' == params.resFlag && params.name){
            $('#userName').val(params.name);
            generateQrCode(params.name);
        } else {
            alert('对不起，您输入的身份证号码不存在有效的公积金账户.');
        }
    };

    var generateCallBack = function(params, settings){
        if(params.errCode){
            $('form>div').show();
            $('.form-2code-img').hide();
            console.log(params.errMsg);
            alert('认证二维码生成失败，请输入正确的身份证/公积金号重试！');
        } else if(params.biz_no) {
            $('.form-2code-tips').removeClass('refresh').html(tips.init);
            $('#face2code').show();
        } else {
            alert('系统繁忙，请刷新重试');
        }
    };

    var lxCallBack = function(params){
        console.log('I am lxCallBack, executed...');
        console.log(params);
        if('true' == params.passed){
            $("#success").val("success");
            login();
        } else {
            if(true === params.timeOutFlag){
                $('#face2code').hide();
                $('.form-2code-tips').addClass('refresh').html(tips.timeout);
            }
        }
    };

    function checkIdNo(){
        var certNo = $('#certno').val();
        if(!certNo){
            alert('请输入身份证/公积金号');
            return;
        }

        var opt = {
            idNo: certNo,//身份证，必传
            tranCode: 'TranFaceRecog01',//身份证校验交易码，必传
            callBack: checkCallBack,//校验结果回调方法，必传
            context: '<%=YdpxUtil.getContexPath()%>'//项目根路径，必传
        }
        faceRecog.checkIdNo(opt);

    }

    function generateQrCode(name){
        var certNo = $('#certno').val();
        var name = $('#userName').val();
        var geOpts = {
            context: '<%=YdpxUtil.getContexPath()%>',//必传,项目根路径，如果为空则传空字符串''
            certNo: certNo,//身份证号码,必传
            certName: name,//姓名,必传
            imgId: 'face2code',//二维码图片元素(img)的id，非必传,如果传了则自动设置二维码图片
            width: 300,//二维码图片的宽,默认300
            height: 300,//二维码图片的高,默认300
            callBack1: generateCallBack,//必传,接收生成二维码结果的回调函数
            cycleQuery: true,//是否启用轮询查询认证结果
            callBack2: lxCallBack,//获取认证结果后的回调函数
            alwaysExecute: false,//false:只在认证结果为成功或者轮询超时时才调用回调函数  true:总是调用回调函数，不论成功与否
            interval: 3000,//轮询查询认证结果的时间间隔，默认1000（单位：毫秒）
            timeOut: 120,//轮询超时时间，默认120（单位：秒）
            cbUrl: 'http://124.207.115.5:1234/ish/'//芝麻认证回调url，非必传，但必须在多级字典里配置好
        };

        if(geOpts.certNo && name){
            $('form>div').hide();
            $('.form-2code-img').show();
            faceRecog.generateQrCode(geOpts);
        } else {
           return;
        }
    }


$(function(){
	 //提交前校验
 	$('form').on('submit', function () {
 		// 身份证号校验
 		if ($('[name="certno"]', this).val() === '') {
 			alert("请输入身份证/公积金号"); 
 			return false;
 		}

       /*  if ($('[name="certname"]', this).val() === '') {
            alert("请输入姓名！");
            return false;
        } */
 		if($("#success").val() =="success"){
 			return true;
 		}else{
	 		 $('form>div').hide();
	         $('.form-2code-img').show();
	        checkIdNo();
	 		return false;
 		}
 	}); 
    //刷脸登录
	$('#bSubmit').click(function (){
        checkIdNo();
    });
    //点击刷新二维码
    $('.form-2code-img').on('click','a',function(){
        generateQrCode();
    });

});
function login(){
	// 登录基础参数
    var options={
       url:"<%= YdpxUtil.getContexPath() %>/login" , // 地址
       fail:function(code,reason){
             $('.form-2code-tips').addClass('refresh').html(reason);
       }, // 失败回调函数
    }
    loginCommon.login(options,$('#form1').serialize());
}
</script>
</body>
</html>