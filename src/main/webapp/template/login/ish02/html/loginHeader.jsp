<%@ page language="java" contentType="text/html; charset=utf-8"
%><div class="head" id="head">
    <img src="">
</div>
<script src="<%= YdpxUtil.staticResource("jquery.js") %>"></script>
<script src="<%=_contexPath %>/template/login/<%=ReadProperty.getString("template") %>/script/loginHeader.js?v=1.0.0"></script>
<script>
    //登录页通用顶部配置项
    loginHeader.init({
        'img':'img_login_logo.png',	//头部logo 图片名字
    });
</script>