<%@ page language="java" contentType="text/html; charset=utf-8"
%><%@ page import="com.yd.svrplatform.util.ReadProperty"
%><%@ page import="com.yd.ydpx.util.YdpxUtil"
%><%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
String _contexPath = request.getContextPath().equals("/") ? "" : request.getContextPath();
%><!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=edge" />
<meta name="renderer" content="webkit" />
<meta name="contexPath" content="<%= YdpxUtil.getContexPath() %>" />
<meta name="cssTemplate" content="<%= YdpxUtil.getTemplate() %>" />
<title>用户登录</title>
<link href="template/login/<%=ReadProperty.getString("template") %>/css/login.css?v=1.0.0" rel="stylesheet" type="text/css" />
<!--[if IE 8]>  
<link href="template/login/<%=ReadProperty.getString("template") %>/css/ielogin.css" rel="stylesheet" type="text/css" />
<![endif]-->
</head>
<body>
<body class="login">
<!-- 头部 -->
<div class="login-header">
    <div class="head-main">
        <div class="head-img">
            <img src="template/login/<%=ReadProperty.getString("template") %>/image/login-name-247x42.png" alt="">
        </div>
        <div class="head-ul">
            <ul></ul>
        </div>
    </div>
</div>
<!-- 背景层 -->    
<div class="login-body">
    <img class="login-bg" src=""/>
    <!-- 选择入口 -->
    <div class="login-main" >
        <div class="login-type" >
            <div class="login-type-img">
                <img src="<%=_contexPath %>/template/login/<%=ReadProperty.getString("template") %>/image/login-name2-395x85.png" alt="">
            </div>
            <div class="login-type-mold"></div>
        </div>
    </div>
    <div class="login-enter">
        <div class="login-type">
            <div class="login-enter-img">
                <img src="<%=_contexPath %>/template/login/<%=ReadProperty.getString("template") %>/image/banner.png" alt="">
            </div>
            <div class="login-iframe">
                <iframe src="<%=_contexPath %>/template/login/<%=ReadProperty.getString("template") %>/html/login0.jsp" allowtransparency="true" scrolling="no" frameborder="0"></iframe>
            </div>
            <div class="login-r-out"></div>
        </div>
    </div>
    <!-- 页脚 -->
    <div class="login-footer">
        <div class="login-footer-bg">
            <div class="login-footer-main">
                <div class="login-footer-main-1">
                    <div class="login-footer-link">
                        <dl>
                            <dt>
                                友情链接：
                            </dt>
                            <dd>

                            </dd>
                        </dl>
                    </div>
                    <div class="login-footer-content">
                        <div class="login-footer-content-info">
                            <div class="phone">住房公积金管理中心  版权所有  All Rights Reserved. &nbsp  客服电话：<img src="template/login/<%=ReadProperty.getString("template") %>/image/12329.png" alt=""></div>
                            <div class="tel">技术支持：华信永道（北京）科技股份有限公司 &nbsp 支持电话：<span>4008-12329-0</span></div>
                        </div>
                    </div>
                </div>
                <div class="login-footer-main-2">
                    <ul>

                    </ul>
                </div>
                <div class="login-footer-main-3">
                    <ul>

                    </ul>
                </div>
            </div>
        </div>    
    </div>
</div>
<script src="<%= YdpxUtil.staticResource("jquery.js") %>"></script>
<script src="<%= YdpxUtil.staticResource("bootstrap.js") %>"></script>
<script src="template/login/<%=ReadProperty.getString("template") %>/script/login.js?v=1.0.0"></script>
<!--[if lt IE 9]>
<script src="<%= YdpxUtil.staticResource("respond.js") %>"></script>
<![endif]-->
<script>
login.init({
    //头部的设置
    setHead:{
        img:'login-name-247x42.png',	//头部logo 图片名字
        /*headUrl:[      					//内部网站链接
            {url: '#', name: '加入我们'},//url,连接地址, name 对应的名字
            {url: '#', name: '联系我们'},
            {url: '#', name: '网站地图'}
        ]*/

    },
    setTypeName:{
        guanliyuan:[
            {name:'密码登录',url:'login4.jsp'},
            {name:'指纹登录',url:'#'},
            {name:'密码+指纹登录',url:'#'},
            {name:'CA证书登录',url:'#'},
            {name:'扫码登录',url:'#'},
            {name:'密码令牌登录',url:'#'},
			{name:'刷脸登录',url:'#'}
        ],
        kaifa:[
            {name:'密码登录',url:'login5.jsp'},
            {name:'指纹登录',url:'#'},
            {name:'密码+指纹登录',url:'#'},
            {name:'CA证书登录',url:'#'},
            {name:'扫码登录',url:'#'},
            {name:'密码令牌登录',url:'#'},
			{name:'刷脸登录',url:'#'}
        ]
    },
    //登录类型
    loginTypes:{
        //danwei:[0,1,2,3,4,5],
        guanliyuan:[0],
        kaifa: [0]
    },
    //内部网站链接
    links: {
        website: [     
            {url: '#', name: '综合计划管理系统'},//url,连接地址,name 对应的名字
            {url: '#', name: '业务稽核系统'},
            {url: '#', name: '决策支持系统'},
            {url: '#', name: '档案管理系统'}
        ],
        manual: '#',    //用户指南
        download: '#' //驱动下载
    },
    //背景图片
    bgBanner:'login-bg-2100x1024.jpg',//背景图片的名字
    //底部的设置
    foot:{
        leftImg:'icon-danwei.png',//最左侧的图片
        phone:'12329.png',//客服电话
        tel: '4008-12329-0' , //技术支持电话
        QRCode: [     
            {img: 'qrcode.png', name: '关注公众号'},//imgCode二维码 图片,name对应的名字
            {img: 'qrcode.png', name: 'App下载'}
        ]
    }
});
</script>
</body>
</html>
