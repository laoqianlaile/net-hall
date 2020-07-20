<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="com.yd.ydpx.util.YdpxUtil" %>

<%
    final String LOGIN_TYPE_NAME = "单位CA登录";
    final String LOGIN_TYPE_CODE = "0";
%>
<%@ page import="com.yd.org.util.CommonErrorCode"%>
<%@include file="properties.jsp"%>
<%
    String strServerCert = sed.getServerCertificate();
    String strRandom = sed.genRandom(24);
    String strSignedData = sed.signData(strRandom.getBytes());
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>CA登录</title>
    <link rel="stylesheet" href="../css/frame.css">
    <script src="<%= YdpxUtil.staticResource("jquery.js") %>"></script>
    <styles>
        <style type="text/css">
            #UserList{
                width:224px;
                line-height:30px;
                height:30px
            }
            .zszj a{
                text-decoration: none;
                color:white;
            }
            .zszj a:hover{
                text-decoration: underline;
            }
        </style>
    </styles>

</head>
<body class="frame-possword">
<div class="frame-main">
    <div class="frame-header">
        <div class="frame-header-img"></div>
        <div class="frame-header-title"><span>证书登录</span></div>
    </div>
    <form action="#" id="LoginForm" name ="LoginForm" target="_parent">
        <div class="form-id-layer">
            <img src="../image/icon-user.png" alt="">
            <div><select id="UserList" name="UserList"></select></div>
        </div>
        <div class="form-pass-layer">
            <img src="../image/icon-lock.png" alt="">
            <input type="password" id="password" name="password" placeholder="请输入PIN码" />
        </div>
        <div>
            <span class="zszj" style="color:white;">第一次登录的单位请下载<a href="http://zfgjj.gxgg.gov.cn/material/%E7%BD%91%E5%8E%85%E6%8F%92%E4%BB%B6%E4%B8%8B%E8%BD%BD.rar">证书助手</a></span>
        </div>
        <div class="frame-Prompt"></div>
        <div class="form-button"><button type="button" id="buSubmit" >登录</button></div>
        <div class="form-href">
        </div>
        <!-- 登录时用 -->
        <input type="hidden" id="logintype" name="logintype" value="cert" />
        <input type="hidden" ID="UserSignedData" name="UserSignedData">
        <input type="hidden" ID="UserCert" name="UserCert">
        <input type="hidden" ID="ContainerName" name="ContainerName">
        <input type="hidden" ID="strRandom" name="strRandom">
    </form>
</div>
<script src="../script/loginCommon.js"></script>
<!--[if lt IE 9]>
<script src="<%= YdpxUtil.staticResource("respond.js") %>"></script>
<![endif]-->
<SCRIPT ID=clientEventHandlersJS LANGUAGE=javascript>
    var strServerSignedData = "<%=strSignedData%>";
    var strServerRan = "<%=strRandom%>";
    var strServerCert = "<%=strServerCert%>";
    $('form').on("keydown", function(event) {
        if (event.which == '13') {
            $('#buSubmit').click();
        }
    });
    $('#buSubmit').on("click", function() {
        //1.验证签名和证书
        $("#strRandom").val(strServerRan);
        //1.验证pin码,使用私钥对随机数签名
        var strCertID = $('#UserList').val();//获取选择的证书
        if (strCertID.value == '') {
            alert('请选择一张证书');
            return false;
        }
        var strPin = $("#password").val();//获取pin码
        Login("LoginForm", strCertID, strPin, "certLogin12.jsp");
    })

</SCRIPT>
<SCRIPT type="text/javascript" src="../js/XTXSAB.js" charset="UTF-8"></SCRIPT>
<script type="text/javascript">
    SetUserCertList("LoginForm.UserList", CERT_TYPE_HARD);
</script>

</body>
</html>