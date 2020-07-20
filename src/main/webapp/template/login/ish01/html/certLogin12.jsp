﻿﻿<%@ page language="java" contentType="text/html; charset=utf-8"
%><%@ page import="com.yd.org.util.CommonErrorCode"
%>
<%@include file="properties.jsp"%>
<%
	final String LOGIN_TYPE_NAME = "密码登录（单位）";
	final String LOGIN_TYPE_CODE = "0";
%><%@ page import="com.yd.ydpx.util.YdpxUtil"
%>
<%
	response.setHeader("Pragma", "No-cache");
	response.setHeader("Cache-Control", "no-cache");
	response.setDateHeader("Expires", 0);

	String strRandom = (String) request.getParameter("strRandom");
	String strPath = request.getContextPath();


	//获得登陆用户cert
	String strClientCert = request.getParameter("UserCert");
	String strClientSignedData = request.getParameter("UserSignedData");
	String strCertID = request.getParameter("ContainerName");
	String strCertIssuer = sed.getCertInfo(strClientCert, 8);

	System.out.println("strClientCert:" + strClientCert);
	System.out.println("strClientSignedData:" + strClientSignedData);
	System.out.println("strCertID:" + strCertID);
	System.out.println("strCertIssuer:" + strCertIssuer);
%>


<%
	//验证客户端证书
	try {
		int retValue = sed.validateCert(strClientCert);
		retValue = 1; //此处屏蔽了验证客户端证书有效性 实际集成时要去掉
		if (retValue == 1) {
			session.setAttribute("CertID", strCertID);

			String strCertName = "";
			try {
				strCertName = sed.getCertInfo(strClientCert, 17);
			} catch (Exception e) {
				out.println("<p><h3>客户端证书验证失败(getCertInfo:17):" + e.getMessage() + "</h3><p>");
			}
			session.setAttribute("CertName", strCertName);

			String strCertEntityID = "";
			try {
//获得登陆用户唯一实体ID
				strCertEntityID = sed.getCertInfoByOid(strClientCert, "1.2.156.112562.2.1.1.1");
				System.out.println("strCertEntityID"+strCertEntityID);
			} catch (Exception e) {
				out.println("<p><h3>客户端证书验证失败(getCertInfoByOid):" + e.getMessage()
						+ "</h3><p>");
			}

			//out.println("<h3>欢迎您使用本系统!</h3>");
			//out.println("<h3>主题通用名：");
			//out.println(strCertName);
			//out.println("<br/>证书颁发者(颁发者通用名): ");
			//out.println(strCertIssuer);
			//out.println("<br/>证书唯一标识(备用主题通用名)：");
			//out.println(strCertEntityID);
			//out.println("<font color='red'>(实际集成时,会将唯一标识与数据库比对,判断是否为合法用户)</font>");
			//out.println("</h3><br/>");
		} else {
			//out.println("<h3>客户端证书验证失败()！</h3><br/>");
			//out.println("<h3><font color='red'>");

			if (retValue == -1) {
				out.println("登录证书的根不被信任");
			} else if (retValue == -2) {
				out.println("登录证书超过有效期");
			} else if (retValue == -3) {
				out.println("登录证书为作废证书");
			} else if (retValue == -4) {
				out.println("登录证书被临时冻结");
			}
			out.println("</font></h3>");
			return;
		}
	} catch (Exception ex) {
		//out.println("<p><h3>客户端证书验证失败(-):" + ex.getMessage() + "</h3><p>");
		return;
	}

//验证客户端签名
	//System.out.println("strClientCert=" + strClientCert);
	//System.out.println("strRandom=" + strRandom);
	//System.out.println("strClientSignedData=" + strClientSignedData);


	//验证签名
	byte[] signedByte = sed.base64Decode(strClientSignedData);
	try {
		if (sed.verifySignedData(strClientCert, strRandom.getBytes(), signedByte)) {
			out.println("<h3>验证客户端签名成功！</h3>");
		} else {
			out.println("<h3>验证客户端签名错误！</h3>");
		}
	} catch (Exception e) {
		out.println("<p><h3>验证客户端签名错误:" + e.getMessage() + "</h3><p>");
		return;
	}
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>Document</title>
	<link rel="stylesheet" href="../css/frame.css">
</head>
<body class="frame-possword">
<div class="frame-main" style="display: none">
	<div class="frame-header">
		<div class="frame-header-img"></div>
		<div class="frame-header-title"><span>密码登录</span></div>
	</div>
	<form action="#" id="form3" method="post" target="_parent" style="display:none">
		<div class="form-id-layer">
			<img src="../image/icon-user.png" alt="">
			<input type="text" id="dlyhm" name="dlyhm" value="dlyhm" placeholder="请输入用户名(单位账号)" autocomplete="off" />
		</div>
		<div class="form-pass-layer">
			<img src="../image/icon-lock.png" alt="">
			<input type="password" id="dlmm" name="dlmm" value="dlmm" placeholder="请输入密码" autocomplete="off"/>
		</div>
		<div class="form-verify" id="p_yzm">
			<input type="text" id="yzm" name="yzm" placeholder="请输入验证码" autocomplete="off" />
		</div>
		<div class="frame-Prompt"></div>
		<div class="form-button"><button type="button" id="buSubmit">登录</button></div>
		<!-- 登录时用 -->
		<input type="hidden" id="logintype" name="logintype" value="orgcert" />
		<input type="hidden" id="yzmkey" name="yzmkey" />
		<input type="hidden" id="checkYzm" name="checkYzm"/>
		<input type="hidden" id="A01001" name="A01001" />
		<input type="hidden" id="gg" name="gg">

	</form>
</div>

<SCRIPT type="text/javascript" src="../scripts/XTXSAB.js" charset="UTF-8"></SCRIPT>
<script src="<%= YdpxUtil.staticResource("jquery.js") %>"></script>
<script src="../script/loginCommon.js"></script>
<script src="<%= YdpxUtil.getContexPath() %>/common/script/security.js"></script>
<script src="<%= YdpxUtil.getContexPath() %>/common/script/aes.js"></script>
<script src="<%= YdpxUtil.getContexPath() %>/common/script/my-aes-crypto.js"></script>
<!--[if lt IE 9]>
<script src="<%= YdpxUtil.staticResource("respond.js") %>"></script>
<![endif]-->
<script>
	var strClientCert = "<%=strClientCert%>";

	var strCertEntityID = GetExtCertInfoByOID(strClientCert, "1.2.156.112562.2.1.1.23");
	var strOU = GetCertBasicinfo(strClientCert,15);
	//alert("登录用户名：" + strOU);
	$("#dlyhm").val(strOU);
    //$("#dlmm").val(dlmm);
    var src = '<%= YdpxUtil.getContexPath() %>/ranCode.jsp';
    //是否显示验证码标识，单位用户第一次登录时默认不显示，密码输入错误后才显示
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
    $(function() {
        $('#p_yzm').hide();
        if (checkYzmFlag) {
            $('#p_yzm').show();
        }
        $('#p_yzm').append($('<img id="t_yzm" src="' + src + '" style="cursor:pointer;" /><a href="javascript:void(0)" onclick="loginCommon.refresh(src)">换一张</a>'));
        $('#t_yzm').click(function () {
            loginCommon.refresh(src);
        });
        loginCommon.refresh(src);
        // 登录基础参数
        var options = {
            url: "<%= YdpxUtil.getContexPath() %>/login", // 地址
            before: function () {
                if ($('#dlyhm').val() == '') {
                    $('.frame-Prompt').text("请输入用户名！");
                    return false;
                }
                if ($('#dlmm').val() == '') {
                    $('.frame-Prompt').text("请输入密码！");
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
                } else {
                    $('#checkYzm').val("0");
                }
                return true;
            },// 登录之前校验函数
            fail: function (code, reason) {
                $('.frame-Prompt').text(reason);
                //如果用户已被锁定不刷新验证码
                if (code != "<%= CommonErrorCode.ERROR_YBSD %>") {
                    loginCommon.refresh(src);
                }
                $('#dlyhm').removeAttr("disabled");
                //$('#dlmm').removeAttr("disabled");
                $('#yzm').removeAttr("disabled");
                checkYzmFlag = true;
                ifCheckYzm();
                loginCommon.refresh(src);
            }, // 失败回调函数
        }


        // 请求前校验处理
        if (options.before && typeof options.before == "function") {
            var flag = options.before.call();
            if (!flag) {
                return false;
            }
        }
       $.ajax({
            type: 'post',
            url: "<%= YdpxUtil.getContexPath() %>/RSA/rsaKey/",
            dataType: "json",
            cache: false,
            success: function (data) {
                var b01001 = new RSAUtils.getKeyPair(data.exponent, "", data.modulus);
                var a01001 = getRandomString(16);
                var a01002 = $('#dlyhm').val().trim();
                var a01003 = $('#dlmm').val().trim();
                var a01004 = $('#yzm').val().trim();
                var a01005 = $('#yzmkey').val().trim();
                var json = {};
                json.a01002 = a01002;
                json.a01003 = a01003;
                json.a01004 = a01004;
                json.a01005 = a01005;
                var gg = JSON.stringify(json);
                var _gg = encrypt(gg, a01001);
                var reversedkey = a01001.split("").reverse().join("");
                var _a01001 = RSAUtils.encryptedString(b01001, reversedkey);
                $('#A01001').val(_a01001);
                $('#gg').val(_gg);
                $('#dlyhm').attr('disabled', 'disabled');
                $('#dlmm').attr('disabled', 'disabled');
                $('#yzm').attr('disabled', 'disabled');
                $('#yzmkey').attr('disabled', 'disabled');
                loginCommon.login(options, $('#form3').serialize());
            }
        });

    });

</script>

</body>
</html>