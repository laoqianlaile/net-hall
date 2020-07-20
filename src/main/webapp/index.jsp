<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%--<%@ page import="com.ydyd.common.util.*"%>--%>
<%--<%@ page import = "org.owasp.encoder.Encode" %>--%>
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
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=GB18030" />
<meta   http-equiv="Expires"   CONTENT="0">
<meta   http-equiv="Cache-Control"   CONTENT="no-cache">
<meta   http-equiv="Pragma"   CONTENT="no-cache">
<title>单位证书登录</title>
<script type="text/javascript"	src="<%=_contexPath%>/scripts/jquery.min.js"></script>
<script type="text/javascript"	src="<%=_contexPath%>/scripts/wfl.base.js"></script>
<script type="text/javascript"	src="<%=_contexPath%>/scripts/wfl.custom.js"></script>
<script type="text/javascript" src="<%=_contexPath%>/scripts/wfl.jquery.ui.js"></script>
<script type="text/javascript" src="<%=_contexPath%>/scripts/ydl.base.js"></script>
<script type="text/javascript" src="<%=_contexPath%>/scripts/SecX_Common_CA.js" ></script>
<script type="text/javascript" src="<%=_contexPath%>/scripts/ydl.workflow.js"></script>
<!-- <object classid="clsid:3F367B74-92D9-4C5E-AB93-234F8A91D5E6" id="XTXAPP" style="HEIGHT: 1px; LEFT: 10px; TOP: 28px; WIDTH: 1px" width=1></object> -->

<script type="text/javascript"  event=OnLoad for=window>	
//检测浏览器版本
<%--var flg= "<%= Encode.forJavaScript(flg) %>";--%>
<%--alert(flg);--%>
<%--var strServerRan = '<%=randStr%>' ;--%>
// if(flg != 1) {
// 	if ($.browser.msie && parseInt($.browser.version) <= 4) location.href = 'download.jsp';
// }
// var caflag=0;
$(function(){
	
	
	$("#dateEle").text(wfl.date.format(new Date(), "yyyy年MM月dd日 星期W"));
	$("#switchable_focus").switchable({panels : $('#switchable_focus > div > p'), putTriggers : 'insertAfter', triggerWrapCls : 'tpl-focus-trigger' , effect : 'fade', vertical : true, interval: 5000, triggerType : 'click', autoplay : true, endToend : true});			
	//重要通知
	$("#topNews").switchable({triggers : false, panels : $('#topNews > ul > li'), steps : 1, visible : 1, effect : 'scrollup', endToend : true, autoplay : true, interval : 5000, duration : 300, endToend : true});
	//验证码切换
	$('img').click(function() {
		this.src ='ranCode.jsp?' + Math.random();
	});
	
	//登录方式页签切换
	$('.WTLoginTit a').click(function(){
 		
		$('.WTLoginTit a').removeClass('current');
		$(this).addClass('current');
		$('.WTLoginForm>div').hide();
		var tabid = $(this).prop('href').split('#')[1];	
	
		if (tabid=='tabs-2')
		{
			//caflag++;
			//initCA ();//加载证书
			//try{
				//alert("caflag ==== "+caflag);
				if(caflag==0)
				{
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
				
			//}
			//catch (e){
			//	alert("可能没有注册成功！"+e.message);
			//}
			
		 
			//initConfig(caflag);   
			
		}else{	
			//flag=-1;
			//$('body').remove('<object classid="clsid:3F367B74-92D9-4C5E-AB93-234F8A91D5E6" id="XTXAPP" style="HEIGHT: 1px; LEFT: 10px; TOP: 28px; WIDTH: 1px" width=1></object>')
			//initCA (false);//清除证书
		}
		
		$('#' + $(this).prop('href').split('#')[1]).show();
		return false;
	}).first().click();  
	//表单校验
 $('.WTLoginForm form').submit( 			
 	function () {
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
</head>
<body class="login">
	<!-- 头部开始 -->
	<div class="head">
		<div class="headContent">
			<%--<div class="fl">--%>
				<%--您好！欢迎来到${WTPageInfoMap["center_name"] }！<span id="dateEle"></span>--%>
			<%--</div>--%>
			<%--<div class="fr">--%>
				<%--<a href="javascript:void(0)"--%>
					<%--onclick="wfl.setHomepage('${WTPageInfoMap["WTAddress"]}')">设为首页</a>--%>
				<%--| <a href="javascript:void(0)"--%>
					<%--onclick="wfl.addFavorite('${WTPageInfoMap["WTAddress"]}','${WTPageInfoMap["center_name"] }')">加入收藏</a>--%>
			<%--</div>--%>
		</div>
	</div>
	<div class="topIndex"><div class="lo"></div><div class="logo"></div></div>
	<!-- 头部结束 -->
	<!-- 导航开始 -->

	<!-- 导航结束 -->

	<!-- 网厅登录开始 -->
	<div class="frameContentShow">
		<!-- 当前位置开始 -->
		<div class="framePosition">
			<%--<ul>--%>
				<%--<li class="tit">您的位置：首页 > 网上办事大厅登录</li>--%>
			<%--</ul>--%>
		</div>
		<!-- 当前位置结束 -->

		<div class="WTLogin">
			<div class="WTLoginTit" style="width: 600px">
				<%--<ul>--%>
					<%--<li style="cursor: pointer"><a href="#tabs-1">个人用户登录</a></li>--%>
					<%--<li style="cursor: pointer"><a href="#tabs-2">单位证书登录</a></li>--%>
					<%--<li style="cursor: pointer"><a href="#tabs-3">单位专办员登录</a></li> --%>
				<%--</ul>--%>
			</div>
			<div class="WTLoginForm">
				<div id="tabs-2">
					<form method="post" ID="LoginForm" name="tabs-2" action="cert.login"
						onsubmit="return login_submit()">
						<div class="WTLoginFormItem">
							<label>单位名称：</label>
							<div>
								<select id="CertList" name="CertList" style="width: 180px"
									onChange="selectCert();"></select>
								<input type="hidden" id ="unitcr"  name="unitcr"  maxlength="18" style="width: 150px" autocomplete="off" />
								
							</div>
						</div>
						<div class="WTLoginFormItem">
							<label>密&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;码：</label>
							<div>
								<input type="password" name="capwd"  id="capwd" maxlength=""
									style="width: 150px" autocomplete="off" />
							</div>
						</div>
						<div class="WTLoginFormItem">
							<label>&nbsp;</label>
							<div>
								<button type="submit">立即登录</button>
							</div>
						</div>
					  <input type="hidden" id="signed_data" name="signed_data" /> 
					  <input type="hidden" id ="UserSignedData"  name="UserSignedData"  />
						<input type="hidden" id="original_jsp" name="original_jsp" value="<%=original %>" />						
						<input id="UserList" name="UserList" type="hidden">			
					</form>
					<h1>注：如果插入UKEY后，单位名称没有显示时，请点击下面控件安装向导。</h1>
					<a href="help/help.html">控件安装向导</a>
				</div>
			</div>
		</div>
	</div>
	<div class="frameWrapAutiContent"></div>
	<!-- 网厅登录结束 -->

	<!-- 底部开始 -->
	<div class="footer">
		<ul>
			<li>Copyright @2020 贵港市住房公积金管理中心 版权所有</li>
			<li><span>${WTPageInfoMap["licence"] }</span><span>           技术支持：华信永道（北京）科技股份有限公司</span></li>
		</ul>
	</div>
	<!-- 底部结束 -->

	<!-- 页面浮动广告 -->

	<!-- 页面浮动广告 -->

</body>
</html>

