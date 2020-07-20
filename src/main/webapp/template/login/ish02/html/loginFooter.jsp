<%@ page language="java" contentType="text/html; charset=utf-8"
%><div class="bottom">
    <div class="tips">
        <ul id="bottomLink"></ul>
    </div>
    <div class="message" id="bottomMessage"></div>
</div>
<script src="<%= YdpxUtil.staticResource("jquery.js") %>"></script>
<script src="<%=_contexPath %>/template/login/<%=ReadProperty.getString("template") %>/script/loginFooter.js?v=1.0.0"></script>
<script>
    //登录页通用底部配置项
    loginFooter.init({
        'links' : [
            {'name':'用户手册','href':'#','img':'icon_login_yhsc.png'},
            {'name':'驱动手册','href':'#','img':'icon_login_qdsc.png'},
            {'name':'IE下载','href':'#','img':'icon_login_iexz.png'},
            {'name':'火狐下载','href':'#','img':'icon_login_hhxz.png'}
        ],
        'message' : [
            {'text':'公积金管理中心'},
            {'text':'版权所有'},
            {'text':'All Rights Reserved'},
            {'text':'客服电话:','tilt':'12329'},
            {'img':'img_login_copy.png','text':'技术支持：华信永道（北京）科技股份有限公司'},
            {'text':'支持电话：4008-12309-0'}
        ]
    });
</script>