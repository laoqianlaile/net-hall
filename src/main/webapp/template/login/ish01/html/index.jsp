<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ page import="java.util.*"%>
<%@ page import = "org.owasp.encoder.Encode" %>
<%@ page import="com.yd.ydpx.util.YdpxUtil" %>
<%@ page import="com.yd.org.util.CommonErrorCode" %>
<%@page import="java.util.*,java.io.FileInputStream"%>
<%@page import = "cn.org.bjca.client.security.SecurityEngineDeal"%>
<%
	String _contexPath = request.getContextPath().equals("/")	? "": request.getContextPath();
	String flg = request.getParameter("flg");

	//产生登录签名原文，出于安全考虑，签名原文必须在服务端产生，并且验证时也必须以服务端session中的签名原文为准，
	String randStr = String.valueOf(System.currentTimeMillis());
	session.setAttribute("ToSign", randStr);

	// 生成数字证书用认证原文
	String num = "1234567890abcdefghijklmnopqrstopqrstuvwxyz";
	int size = 6;
	char[] charArray = num.toCharArray();
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < size; i++) {
		sb.append(charArray[((int) (Math.random() * 10000) % charArray.length)]);
	}
	String original = sb.toString() == null ? null : sb.toString().trim();
	request.getSession().setAttribute("original_data", original);

	request.setCharacterEncoding("UTF-8");
	Properties properties = new Properties();
	properties.load(new FileInputStream(application.getRealPath("webappName.properties")));

	SecurityEngineDeal.setProfilePath("C:/BJCAROOT");
//	SecurityEngineDeal.setProfilePath("/app/workflow/BJCAROOT");

	SecurityEngineDeal sed = null;
	sed = SecurityEngineDeal.getInstance(properties.getProperty("webappName"));
//	sed = SecurityEngineDeal.getInstance("BJCADevice");
	String strServerCert = sed.getServerCertificate();
	String strRandom = sed.genRandom(24);
	String strSignedData = sed.signData(strRandom.getBytes());
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=utf-8" />
	<meta   http-equiv="Expires"   CONTENT="0">
	<meta   http-equiv="Cache-Control"   CONTENT="no-cache">
	<meta   http-equiv="Pragma"   CONTENT="no-cache">
	<title>单位CA登录</title>
	<%--<link rel="stylesheet" type="text/css"	href="../oldcss/ui.base.css">--%>
	<%--<link rel="stylesheet" type="text/css"	href="../oldcss/ui.web.page.css">--%>
	<%--<link rel="stylesheet" type="text/css"	href="../oldcss/ui.web.template.css">--%>
	<link rel="stylesheet" href="../css/frame.css">
	<script type="text/javascript"	src="../scripts/jquery.min.js"></script>
	<script type="text/javascript"	src="../scripts/wfl.base.js"></script>
	<script type="text/javascript"	src="../scripts/wfl.custom.js"></script>
	<script type="text/javascript" src="../scripts/wfl.jquery.ui.js"></script>
	<script type="text/javascript" src="../scripts/ydl.base.js"></script>
	<script type="text/javascript" src="../scripts/SecX_Common_CA.js" ></script>
	<script type="text/javascript" src="../scripts/ydl.workflow.js"></script>
    <script src="../script/loginCommon.js"></script>
    <script src="<%= YdpxUtil.getContexPath() %>/common/script/security.js"></script>
    <script src="<%= YdpxUtil.getContexPath() %>/common/script/aes.js"></script>
    <script src="<%= YdpxUtil.getContexPath() %>/common/script/my-aes-crypto.js"></script>
	<!-- <object classid="clsid:3F367B74-92D9-4C5E-AB93-234F8A91D5E6" id="XTXAPP" style="HEIGHT: 1px; LEFT: 10px; TOP: 28px; WIDTH: 1px" width=1></object> -->

	<script type="text/javascript"  event=OnLoad for=window>
		//检测浏览器版本
		var flg= "<%= Encode.forJavaScript(flg) %>";

		var strServerRan = '<%=randStr%>' ;
		if(flg != 1) {
			if ($.browser.msie && parseInt($.browser.version) <= 4) location.href = 'download.jsp';
		}
		var caflag = 0;
		$(function(){
			// $("#dateEle").text(wfl.date.format(new Date(), "yyyy年MM月dd日 星期W"));
			// $("#switchable_focus").switchable({panels : $('#switchable_focus > div > p'), putTriggers : 'insertAfter', triggerWrapCls : 'tpl-focus-trigger' , effect : 'fade', vertical : true, interval: 5000, triggerType : 'click', autoplay : true, endToend : true});
			//重要通知
			// $("#topNews").switchable({triggers : false, panels : $('#topNews > ul > li'), steps : 1, visible : 1, effect : 'scrollup', endToend : true, autoplay : true, interval : 5000, duration : 300, endToend : true});
			//验证码切换
			// $('img').click(function() {
			// 	this.src ='vericode.jsp?' + Math.random();
			// });

			//登录方式页签切换

            if(caflag == 0){
                $('body').append('<object classid="clsid:3F367B74-92D9-4C5E-AB93-234F8A91D5E6" id="XTXAPP" style="HEIGHT: 1px; LEFT: 10px; TOP: 28px; WIDTH: 1px" width=1></object>')

                if(document.XTXAPP.object == null) {
                    //alert("控件不存在，请下载且安装控件！")
                    location.href = 'download.jsp'
                }else{
                    //alert("成功安装了控件!");
                    GetList("LoginForm.CertList");
                }
                caflag=-1;
            }

			//表单校验
			$('.WTLoginForm form').submit(
					function () {
						// alert("84 ffsss");
						$('.pointout').remove();
						var doNotSubmit = true;
						$('input', this).each(function () {
							if ($(this).val() == '') {
								var errormassage = $(this).parent().prev().text().split('：')[0];
								if (errormassage!=''){
									$(this).parent().append('<span class="pointout" style="size:12px; color:red"> 请输入:' + errormassage + '</span>');
									doNotSubmit = false;
								}

							}
						});
						return doNotSubmit;
						return true;
					});
		});

	</script>
	<style>
		.frameContentShow select{
			width:234px;
			height:37px;
		}
		.frameContentShow .WTLogin .WTLoginForm #tabs-2 .WTLoginFormItem div{
			background-color:#b3c2d8;
			display:flex;
		}
		.frameContentShow #buSubmit{
			width:100px;
			border:1px solid #4ba8eb;
			background-color: #4ba8eb;
			height:35px;
			margin-left:76px;
			font-weight:bold;
			line-height:37px;
		}
		.frameContentShow #buSubmit:hover{
			opacity:0.7;
			cursor: pointer;
		}
		select{
			width:234px;
			height:37px;
			background-color:#b3c2d8;
			font-size:14px;
		}
		#capwd{
			width:230px;
			margin-top:3px;
			font-size:14px;
		}
	</style>
</head>
<body class="login">
<!-- 网厅登录开始 -->
<div class="frameContentShow">
	<div class="WTLogin">
		<div class="WTLoginForm">
			<div id="tabs-2">
				<form method="post" ID="LoginForm" name="tabs-2">
					<div class="WTLoginFormItem">
						<div>
							<img src="../image/icon-user.png" alt="">
							<select id="CertList" name="CertList" style="..."
									onChange="selectCert();"></select>
							<input type="hidden" id ="unitcr"  name="unitcr"  maxlength="18" style="width: 150px" autocomplete="off"/>

						</div>
					</div>
					<div class="WTLoginFormItem">
						<div>
							<img src="../image/icon-lock.png" alt="">
							<input type="password" name="capwd"  id="capwd"  maxlength="" placeholder="请输入PIN码"
								   style="width: 150px" autocomplete="off" />
						</div>
					</div>
					<div>
						<label>&nbsp;</label>
						<div>
							<button type="submit" id="buSubmit">登录</button>
						</div>
					</div>
					<input type="hidden" id="signed_data" name="signed_data" />
					<input type="hidden" id ="UserSignedData"  name="UserSignedData"/>
					<input type="hidden" id="original_jsp" name="original_jsp" value="<%=original %>" />
					<input id="UserList" name="UserList" type="hidden"/>
					<input id="UserCert" name="UserCert" type="hidden"/>
					<input id="logintype" type="hidden" name="logintype" value="orgcert"/>
					<input type="hidden" id="A01001" name="A01001" />
					<input type="hidden" id="gg" name="gg" />
				</form>
				<div class="frame-Prompt"></div>
				<p>注：如果插入UKEY后，单位名称没有显示时，请点击下面控件安装向导。</p>
				<a href="help/help.html">控件安装向导</a>
			</div>

		</div>
	</div>
</div>
<div class="frameWrapAutiContent"></div>
<!-- 网厅登录结束 -->
<script type="text/javascript">
	$(function(){
		var options={
			url:"<%= YdpxUtil.getContexPath() %>/login" , // 地址
			before:function(){
				if ($('#capwd').val() == '') {
					$('.frame-Prompt').text("请输入PIN码！");
					return false;
				}

				return true;
			},// 登录之前校验函数
			fail:function(code,reason) {
				$('.frame-Prompt').text(reason);
				// 登录失败取消disabled属性，登录中改成登录 start
				$('#buSubmit').removeAttr("disabled");
				$('#buSubmit').text("登录");
				// end
				$('#capwd').removeAttr("disabled");
			}
		};
		$('form').on("keydown", function(event) {
			if (event.which == '13') {
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
				$("#UserCert").val("<%=strServerCert%>");
				$("#UserSignedData").val("<%=strSignedData%>");
				$.ajax({
					type: 'post',
					url: "<%= YdpxUtil.getContexPath() %>/RSA/rsaKey",
					dataType: "json",
					cache:false,
					success: function (data) {
						var b01001 = new RSAUtils.getKeyPair(data.exponent, "", data.modulus);
						var a01001 = getRandomString(16);
						var a01002 = $('#CertList').val().trim();
						var a01003 = $('#capwd').val().trim();
						var a01004 = $("#original_jsp").val().trim();
						json.a01002=a01002;
						json.a01003=a01003;
						json.a01004 = a01004;
						var gg = JSON.stringify(json);
						var _gg = encrypt(gg,a01001);
						var reversedkey = a01001.split("").reverse().join("");
						var _a01001 = RSAUtils.encryptedString(b01001, reversedkey);
						$('#A01001').val(_a01001);
						$('#gg').val(_gg);
						loginCommon.login(options,$('#LoginForm').serialize());
					}
				});
			}
		});
		$('#buSubmit').on("click", function() {
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
			$("#UserCert").val("<%=strServerCert%>");
			$("#UserSignedData").val("<%=strSignedData%>");
			$.ajax({
				type: 'post',
				url: "<%= YdpxUtil.getContexPath() %>/RSA/rsaKey/",
				dataType: "json",
				cache:false,
				success: function (data) {
					console.log("返回的数据",data);
					var b01001 = new RSAUtils.getKeyPair(data.exponent, "", data.modulus);
					var a01001 = getRandomString(16);
					var a01002 = $('#CertList').val().trim();
					var a01003 = $('#capwd').val().trim();
					var a01004 = $("#original_jsp").val().trim();
					var json = {};
					json.a01002=a01002;
					json.a01003=a01003;
					json.a01004 = a01004;
					var gg = JSON.stringify(json);
					var _gg = encrypt(gg,a01001);
					var reversedkey = a01001.split("").reverse().join("");
					var _a01001 = RSAUtils.encryptedString(b01001, reversedkey);
					$('#A01001').val(_a01001);
					$('#gg').val(_gg);
					loginCommon.login(options,$("#LoginForm").serialize());
				}
			});
		});
	});
</script>
</body>
</html>

