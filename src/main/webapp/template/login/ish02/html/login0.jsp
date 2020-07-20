<%@ page language="java" contentType="text/html; charset=utf-8" 
%><%@ page import="com.yd.svrplatform.util.ReadProperty"
%><%@ page import="com.yd.ydpx.util.YdpxUtil"
%><%@ page import="com.yd.org.util.CommonErrorCode"
%><%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
String _contexPath = request.getContextPath().equals("/") ? "" : request.getContextPath();

    ServletContext context = this.getServletConfig().getServletContext();
    YdpxUtil.setContexPath(context.getContextPath());
    YdpxUtil.setWebRoot(context.getRealPath("/"));
    System.out.println("ContexPath ============ " + YdpxUtil.getContexPath());
    //logger.info("YdpxInitServlet: ContexPath = " + YdpxUtil.getContexPath());
    //logger.debug("YdpxInitServlet: ContexPath = " + YdpxUtil.getContexPath());
    
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=edge" />
    <meta name="renderer" content="webkit" />
    <meta name="contexPath" content="<%= YdpxUtil.getContexPath() %>" />
    <meta name="cssTemplate" content="<%= YdpxUtil.getTemplate() %>" />
    <title>缴存单位</title>
    <link href="../css/login.css?v=1.0.0" rel="stylesheet" type="text/css" />
</head>


<body class="login">
    <!-- 头部 -->
    <%@ include file = "loginHeader.jsp" %>
    
    <!-- 中间部分 -->
    <div class="content-box" id="contentBox">
        <div class="content-login">
			<h>缴存单位</h>
			<ul class="tab-nav"></ul>
		</div>
    </div>
    
    <!-- 底部 -->    
    <%@ include file = "loginFooter.jsp" %>

</body>
<script src="<%= YdpxUtil.staticResource("jquery.js") %>"></script>
<script src="../script/loginCommon.js"></script>
<!--[if lt IE 9]>
<script src="<%= YdpxUtil.staticResource("respond.js") %>"></script>
<![endif]-->
<script>
var src = '<%= YdpxUtil.getContexPath() %>/ranCode.jsp';
//是否显示验证码标识，单位用户第一次登陆时默认不显示，密码输入错误后才显示
var checkYzmFlag = true;
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

    //登录tab页切换
    $('.tab-nav').on('click', 'a', function(){
        showTab($(this).attr('class'));
    });

    //设置默认显示的页签
    var typeDic = {
                    '0' : {
                        'id' : 'tabDwPassword',
                        'name' : '密码登录',
                        'action' : dwPassword
                    },
                    '1' : {
                        'id' : 'form8',
                        'name' : 'CA证书登录',  //启用CA证书登录
                        'action' : form8
                    },
                };
    
    //设置默认启用的登录方式
    var allTypeDefault = [0,1];
    
    //获取url参数
    function getQueryString(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return unescape(r[2]); return null;
    }
    var thisType = getQueryString('type') ? getQueryString('type') : false ;
    var allType = getQueryString('allType') ? getQueryString('allType').split(',') : false;

    //根据参数启用相应的登录方式，如果没有参数则根据默认配置项选择
    var allTypeData = '';
    if(allType) allTypeData = allType;
    else allTypeData = allTypeDefault;
    $.map(allTypeData, function(v){
        if(typeDic[v]) typeDic[v].action();
    });

    //设置默认显示页签
    if(thisType && typeDic[thisType]) {
        //如果有对应选择的方式，显示对应的页签
        showTab(typeDic[thisType].id);
    }else {
        //如果没有对应选择的方式则默认显示导航内第一个页签
        showTab($('.tab-nav li:eq(0) a').attr('class'));
    }

    //根据id显示页签方法
    function showTab(tabId) {
        $('.tab-nav li, .tab-page').removeClass('login-active');
        $('.'+tabId).closest('li').addClass('login-active');
        $('#'+tabId).addClass('login-active');
    }
    
    //单位帐号密码登录
    function dwPassword(){
        //添加结构
        var navHtml = '<li><a class="tabDwPassword" href="#">密码登录</a></li>';
        $('.tab-nav').append(navHtml);
        var pageHtml = '<form class="tab-page" id="tabDwPassword"  action="#" method="post" target="_parent">' +
                            '<div class="form-row">' +
                                '<img src="../image/icon_login_dwzh.png" />' +
                                '<input id="dlyhm" name="dlyhm" type="text" placeholder="请输入单位账号" autocomplete="off" title="请输入单位账号" />' +
                            '</div>' +
                            '<div class="form-row">' +
                                '<img src="../image/icon_login_cxmm.png" />' +
                                '<input id="dlmm" name="dlmm" type="password" placeholder="请输入密码" title="请输入密码" />' +
                            '</div>' +
                            '<div class="form-row" id="p_yzm">' +
                                '<img src="../image/icon_login_yzm.png" />' +
                                '<input id="yzm" name="yzm" type="text" placeholder="请输入验证码" autocomplete="off" title="请输入验证码" />' +
                            '</div>' +
                            '<div class="error"></div>' +
                            '<div class="form-row button-sub">' +
                                '<button type="button" id="buSubmit">立即登录</button>' +
                            '</div>' +
                            '<p>' +
                                '<a class="register hide">用户注册</a>' +
                                '<a class="forgetPassword hide">忘记密码?</a>' +
                            '</p>' +
                            //登录时用
                            '<input type="hidden" id="logintype" name="logintype" value="orgauth" />' +
                            '<input type="hidden" id="yzmkey" name="yzmkey" />' +
                            '<input type="hidden" id="checkYzm" name="checkYzm" />' +
                        '</form>';
        $('.content-login').append(pageHtml);

        //用帐号密码登录
        $('#p_yzm').hide();
        if(checkYzmFlag){
            $('#p_yzm').show();
        }
        console.log("加载验证码信息18");
        $('#p_yzm').append($('<img class="yzm-img" id="t_yzm" src="' + src + '" style="cursor:pointer;" />'));
        $('#t_yzm').click(function () {
            loginCommon.refresh(src);
        });
        loginCommon.refresh(src);
        // 登录基础参数
        var options={
            url:"<%= YdpxUtil.getContexPath() %>/login" , // 地址
            before:function(){
                if ($('#dlyhm').val() == '') {
                    $('.error').text("请输入用户名！"); 
                    return false;
                }
                if ($('#dlmm').val() == '') {
                    $('.error').text("请输入密码！"); 
                    return false;
                }
                if (checkYzmFlag) {
                    if ($('#yzm').val() == '') {
                        $('.error').text("请输入验证码！"); 
                        return false;
                    }
                    if ($('#yzmkey').val() == '') {
                        $('.error').text("验证码加载失败，请刷新！"); 
                        return false;
                    }
                    $('#checkYzm').val("1");
                }else{
                    $('#checkYzm').val("0");
                }
                return true;
            },// 登录之前校验函数
            fail:function(code,reason){
                $('.error').text(reason);
                //如果用户已被锁定不刷新验证码
                if(code!= "<%= CommonErrorCode.ERROR_YBSD %>"){
                    loginCommon.refresh(src);
                }
                checkYzmFlag = true;
                ifCheckYzm();
                loginCommon.refresh(src);
            }, // 失败回调函数
        }
        // 回车提交事件
        $('#tabDwPassword').on("keydown", function(event) {
            if (event.which == '13') {
                loginCommon.login(options,$('#tabDwPassword').serialize());
            }
        });
        $('#buSubmit').on("click", function() {
            loginCommon.login(options,$('#tabDwPassword').serialize());
        });
    }

    //CA证书登录
    function form8(){
        //添加结构
        var navHtml = '<li><a class="form8" href="#">CA证书登录</a></li>';
        $('.tab-nav').append(navHtml);
        var pageHtml = '<form class="tab-page" action="#" id="form8" name="form8" method="post" target="_parent">' +
                            '<div class="form-row text-line">' +
                                '<font color=red >请插入CA证书后稍事等候</font>' +
                            '</div>' +
                            '<div class="form-row">' +
                                '<img src="../image/icon_login_yzm.png" />' +
                                '<select  id="dlyhmCA" name="dlyhm"></select>' +
                            '</div>' +
                            '<div class="form-row">' +
                                '<img src="../image/icon_login_cxmm.png" />' +
                                '<input type="password" name="dlmm" id="dlmmCA" placeholder="请输入数字证书密码" value="" onkeypress="if(event.keyCode == 13) return false;" />' +
                            '</div>' +
                            '<div class="frame-Prompt"></div>' +
                            '<div class="form-row button-sub">' +
                                '<button type="button" id="buSubmitCA">登录</button>' +
                            '</div>' +
                            '<div class="form-row form-help"><a>如登录遇到问题请点击这里</a></div>' +
                            //登录时用
                            '<input type="hidden" id="UserSignedData" name="UserSignedData">' +
                            '<input type="hidden" id="UserCert" name="UserCert">' +
                            '<input type="hidden" id="ContainerName" name="ContainerName">' +
                            '<input type="hidden" id="strRandom" name="strRandom">' +
                            '<input type="hidden" id="logintypeCA" name="logintype" value="cert" />' +
                        '</form>';
        $('.content-login').append(pageHtml);

        // 登录基础参数
        var options={
            url:"<%= YdpxUtil.getContexPath() %>/login" , // 地址
            before:function(){
                if ($('#dlmmCA').val() == '') {
                    $('.frame-Prompt').text("请输入数字证书密码！"); 
                    return false;
                }
                return true;
            },// 登录之前校验函数
            fail:function(code,reason){
                $('.frame-Prompt').text(reason);
                //如果用户已被锁定不刷新验证码
                if(code!= "<%= CommonErrorCode.ERROR_YBSD %>"){
                    //loginCommon.refresh(src);
                }
            }, // 失败回调函数
        }
        // 回车提交事件
        $('#form8').on("keydown", function(event) {
            if (event.which == '13') { 
                var strCertID =  form8.dlyhm.value;
                var strPin = form8.dlmm.value;
                //这里删除了 ‘form8.strRandom.value = ""; 
                if(Login("form8", strCertID, strPin, "")){
                    loginCommon.login(options,$('#form8').serialize());
                }
            }
        });
        $('#buSubmitCA').on("click", function() {
            //loginCommon.login(options,$('#form8').serialize());
            var strCertID =  form8.dlyhm.value;
            var strPin = form8.dlmm.value;
          //这里删除了 ‘form8.strRandom.value = ""; 
            if(Login("form8", strCertID, strPin, "")){
                loginCommon.login(options,$('#form8').serialize());
            }
        });
        
        //查看帮助
        $('.form-help a').click(function(){
            parent.login.help();
        });
    }
})
</script>
</html>