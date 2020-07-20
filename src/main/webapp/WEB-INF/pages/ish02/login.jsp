<%@ page language="java" contentType="text/html; charset=utf-8"
%><%@ page import="com.yd.svrplatform.util.ReadProperty"
%><%@ page import="com.yd.ydpx.util.YdpxUtil"
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

%><!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=edge" />
<meta name="renderer" content="webkit" />
<meta name="contexPath" content="<%= YdpxUtil.getContexPath() %>" />
<meta name="cssTemplate" content="<%= YdpxUtil.getTemplate() %>" />
<title>用户登录</title>
<link href="<%=_contexPath %>/template/login/<%=ReadProperty.getString("template") %>/css/login.css?v=1.0.0" rel="stylesheet" type="text/css" />
<!--[if IE 8]>  
<link href="<%=_contexPath %>/template/login/<%=ReadProperty.getString("template") %>/css/ielogin.css" rel="stylesheet" type="text/css" />
<![endif]-->
</head>

<body class="login">
<!-- 头部 -->
<%@ include file = "../../../template/login/ish02/html/loginHeader.jsp" %>

<!-- 中间部分 -->
<div class="content-box" id="contentBox">
    <div class="content" id="content"></div>
</div>

<!-- 底部 -->    
<%@ include file = "../../../template/login/ish02/html/loginFooter.jsp" %>

<script src="<%= YdpxUtil.staticResource("jquery.js") %>"></script>
<script src="<%= YdpxUtil.staticResource("bootstrap.js") %>"></script>
<script src="<%=_contexPath %>/template/login/<%=ReadProperty.getString("template") %>/script/login.js?v=1.0.0"></script>

<!--[if lt IE 9]>
<script src="<%= YdpxUtil.staticResource("respond.js") %>"></script>
<![endif]-->
<script>
    login.init({

        /**
         * 可选择的登录种类：
         * jiaocundanwei-缴存单位；shebaosuo-社保所；fangyuandanwei-房源单位；
         */
        loginName : {
            'jiaocundanwei' : {
                'name' : '缴存单位',
                'type' : [
                    {'name' : '密码登录'},
                    {'name' : 'CA证书登录'}
                ],
                'url' : 'login0.jsp'
            },
            'shebaosuo' : {
                'name' : '社保所',
                'type' : [
                    {'name' : '密码登录'},
                    {'name' : 'CA证书登录'}
                ],
                'url' : 'login0.jsp'
            },
            'fangyuandanwei' : {
                'name' : '房源单位',
                'type' : [
                    {'name' : '密码登录'},
                    {'name' : 'CA证书登录'}
                ],
                'url' : 'login0.jsp'
            }
        },
        /*登录类型*/
        'loginTypes' : {
            'jiaocundanwei' : [0,1],
            'shebaosuo' : [0],
            'fangyuandanwei' : [1]
        }
        
    });
</script>
</body>
</html>
